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

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.util.Consumer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.spotbugs.collectors.RecurseFileCollector;
import org.jetbrains.plugins.spotbugs.common.util.IdeaUtilImpl;
import org.jetbrains.plugins.spotbugs.core.FindBugsProject;
import org.jetbrains.plugins.spotbugs.core.FindBugsProjects;
import org.jetbrains.plugins.spotbugs.core.FindBugsStarter;
import org.jetbrains.plugins.spotbugs.core.FindBugsState;

import java.io.File;

public final class AnalyzePackageFiles extends AbstractAnalyzeAction {

	@Override
	void updateImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@NotNull final ToolWindow toolWindow,
			@NotNull final FindBugsState state
	) {

    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      final boolean enable;
      if (state.isIdle()) {
        final VirtualFile directory = getDirectory(e, project);
        enable = directory != null && ModuleUtilCore.findModuleForFile(directory, project) != null;
      } else {
        enable = false;
      }

      ApplicationManager.getApplication().invokeLater(() -> {
        e.getPresentation().setEnabled(enable);
        e.getPresentation().setVisible(true);
      });
    });
	}

	@SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
	@Override
	void analyze(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@NotNull final ToolWindow toolWindow,
			@NotNull final FindBugsState state
	) {
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      final VirtualFile directory = getDirectory(e, project);
      final Module module = ModuleUtilCore.findModuleForFile(directory, project);
      if (module == null) {
        throw new IllegalStateException("No module found for " + directory);
      }
      final ProjectFileIndex fileIndex = ProjectRootManager.getInstance(project).getFileIndex();
      final String packageName = fileIndex.getPackageNameByDirectory(directory);
      final boolean isTest = fileIndex.isInTestSourceContent(directory);

      new FindBugsStarter(project, "Running SpotBugs analysis for package '" + packageName + "'...") {
        @Override
        protected void createCompileScope(@NotNull final CompilerManager compilerManager, @NotNull final Consumer<CompileScope> consumer) {
          consumer.consume(compilerManager.createProjectCompileScope(project));
        }

        @Override
        protected boolean configure(@NotNull final ProgressIndicator indicator, @NotNull final FindBugsProjects projects, final boolean justCompiled) {
          final CompilerModuleExtension extension = CompilerModuleExtension.getInstance(module);
          if (extension == null) {
            throw new IllegalStateException("No compiler extension for module " + module.getName());
          }
          final VirtualFile compilerOutputPath = isTest ? extension.getCompilerOutputPathForTests() : extension.getCompilerOutputPath();
          if (compilerOutputPath == null) {
            showWarning("Source is not compiled.");
            return false;
          }
          final File outputPath = new File(compilerOutputPath.getCanonicalPath(), packageName.replace(".", File.separator));
          if (!outputPath.exists()) {
            showWarning("Source is not compiled (" + outputPath + ").");
            return false;
          }
          indicator.setText("Collecting files for analysis...");
          final FindBugsProject findBugsProject = projects.get(module, isTest);
          final int[] count = new int[1];
          RecurseFileCollector.addFiles(project, indicator, findBugsProject, outputPath, count);
          return true;
        }
      }.start();
    });
	}

	@Nullable
	private static VirtualFile getDirectory(
			@NotNull final AnActionEvent e,
			@NotNull final Project project
	) {

		final VirtualFile[] selectedFiles = IdeaUtilImpl.getVirtualFiles(e.getDataContext());
		if (selectedFiles == null || selectedFiles.length != 1) {
			return null;
		}
		VirtualFile directory = selectedFiles[0];
		if (!directory.isDirectory()) {
			directory = directory.getParent();
			if (directory == null) {
				return null;
			}
			if (!directory.isDirectory()) {
				return null;
			}
		}
    final VirtualFile finalDirectory = directory;
		final PsiDirectory psiDirectory = ReadAction.compute(() -> PsiManager.getInstance(project).findDirectory(finalDirectory));
		if (psiDirectory == null) {
			return null;
		}
    final boolean isPackage = ReadAction.compute(() -> PsiDirectoryFactory.getInstance(project).isPackage(psiDirectory));
		if (!isPackage) {
			return null;
		}
		return directory;
	}
}
