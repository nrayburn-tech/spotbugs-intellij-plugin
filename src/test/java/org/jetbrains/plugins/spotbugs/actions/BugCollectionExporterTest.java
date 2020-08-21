/*
 * Copyright 2020 SpotBugs plugin contributors
 *
 * This file is part of IntelliJ SpotBugs plugin.
 *
 * IntelliJ SpotBugs plugin is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * IntelliJ SpotBugs plugin is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with IntelliJ SpotBugs plugin.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package org.jetbrains.plugins.spotbugs.actions;

import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.ExpectedHighlightingData;
import com.intellij.testFramework.PsiTestUtil;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import com.intellij.util.Consumer;
import edu.umd.cs.findbugs.SortedBugCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.spotbugs.core.*;
import org.jetbrains.plugins.spotbugs.gui.toolwindow.view.ToolWindowPanel;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import javax.tools.*;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BugCollectionExporterTest extends JavaCodeInsightFixtureTestCase {

    @Rule
    private final TemporaryFolder folder = new TemporaryFolder();
    private File out;
    private ToolWindowPanel toolWindowPanel;

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/findBugsStarter";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        final WorkspaceSettings workspaceSettings = WorkspaceSettings.getInstance(getProject());
        workspaceSettings.compileBeforeAnalyze = false;

        folder.create();
        out = folder.newFolder("out");

        PsiTestUtil.setCompilerOutputPath(getModule(), VfsUtilCore.pathToUrl(out.getPath()), false);

        myFixture.configureByFile("NPETestData.java");

        compile();

        toolWindowPanel = new ToolWindowPanel(getProject());

        final VirtualFile[] selectedFiles = new VirtualFile[]{myFixture.getFile().getVirtualFile()};

        new FindBugsStarter(getProject(), getName()) {
            @Override
            protected void createCompileScope(@NotNull final CompilerManager compilerManager, @NotNull final Consumer<CompileScope> consumer) {
                // already compiled
            }

            @Override
            protected boolean configure(@NotNull final ProgressIndicator indicator, @NotNull final FindBugsProjects projects, final boolean justCompiled) {
                return projects.addFiles(selectedFiles, !justCompiled, hasTests(selectedFiles));
            }
        }.start();

        myFixture.checkHighlighting(true, true, true);
    }

    public void writeTestDataWithoutHighlights() throws IOException {
        final File file = folder.newFile(myFixture.getFile().getName());

        Document document = new DocumentImpl(myFixture.getDocument(myFixture.getFile()).getText());
        // Removes highlights (bounded with <marker>...</marker>) from test case file.
        new ExpectedHighlightingData(document, false, false, false).init();

        FileUtil.writeToFile(file, document.getText());
    }

    private void compile() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null);
        writeTestDataWithoutHighlights();
        List<File> files = Arrays.stream(folder.getRoot().listFiles()).filter(file -> !file.isDirectory()).collect(Collectors.toList());

        Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(files);

        manager.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singleton(out));
        JavaCompiler.CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sources);

        if (!task.call()) {
            fail(diagnostics.getDiagnostics().toString());
        }
    }

    public void testHtmlBugCollectionExporter() throws IOException, TransformerException {
        HtmlBugCollectionExporter htmlExporter = new HtmlBugCollectionExporter();
        final File html =  folder.newFile("report.html");

        final FindBugsResult result = toolWindowPanel.getResult();

        for (final Map.Entry<edu.umd.cs.findbugs.Project, SortedBugCollection> entry : result.getResults().entrySet()) {
            htmlExporter.export(entry.getValue(), html);
        }
    }

    public void testXmlBugCollectionExporter() throws IOException {
        XmlBugCollectionExporter xmlExporter = new XmlBugCollectionExporter();
        final File html = folder.newFile("report.xml");

        final FindBugsResult result = toolWindowPanel.getResult();

        for (final Map.Entry<edu.umd.cs.findbugs.Project, SortedBugCollection> entry : result.getResults().entrySet()) {
            xmlExporter.export(entry.getValue(), html);
        }
    }
}
