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

package org.jetbrains.plugins.spotbugs.core;

import com.intellij.openapi.compiler.CompilationStatusListener;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerTopics;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.spotbugs.android.RFilerFilterSuggestion;
import org.jetbrains.plugins.spotbugs.gui.preferences.LegacyProjectSettingsConverter;

public class ProjectOpenCloseListener implements ProjectManagerListener {

    private final CompilationStatusListener compilationStatusListener;
    private MessageBusConnection connection;

    public ProjectOpenCloseListener() {
        compilationStatusListener = new CompilationStatusListener() {
            @Override
            public void compilationFinished(final boolean aborted, final int errors, final int warnings, final @NotNull CompileContext compileContext) {
                // note that this is not invoked when auto make trigger compilation
                if (!aborted && errors == 0) {
                    FindBugsCompileAfterHook.initWorker(compileContext);
                }
            }

            @Override
            public void fileGenerated(final @NotNull String s, final @NotNull String s1) {
            }

            @SuppressWarnings("UnusedDeclaration")
            public void fileGenerated(final String s) {
            }
        };
    }

    @Override
    public void projectOpened(@NotNull Project project) {
        connection = project.getMessageBus().connect();
        connection.subscribe(CompilerTopics.COMPILATION_STATUS, compilationStatusListener);
        if (FindBugsCompileAfterHook.isAfterAutoMakeEnabled(project)) {
            FindBugsCompileAfterHook.setAnalyzeAfterAutomake(project, true);
        }

        PluginSuggestion.suggestPlugins(project);
        new RFilerFilterSuggestion(project).suggest();

        LegacyProjectSettingsConverter.convertSettings(project);
    }

    @Override
    public void projectClosed(@NotNull Project project) {
        connection.disconnect();
        FindBugsCompileAfterHook.setAnalyzeAfterAutomake(project, false);
    }
}
