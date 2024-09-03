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
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.util.Consumer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.spotbugs.common.util.IdeaUtilImpl;
import org.jetbrains.plugins.spotbugs.core.FindBugsProjects;
import org.jetbrains.plugins.spotbugs.core.FindBugsStarter;
import org.jetbrains.plugins.spotbugs.core.FindBugsState;

public final class AnalyzeSelectedFiles extends AbstractAnalyzeAction {

	@Override
	void updateImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@NotNull final ToolWindow toolWindow,
			@NotNull final FindBugsState state
	) {

    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      final VirtualFile[] selectedFiles = IdeaUtilImpl.getVirtualFiles(e.getDataContext());

      ApplicationManager.getApplication().invokeLater(() -> {
        boolean enable = false;
        if (state.isIdle()) {
          enable = selectedFiles != null &&
                   selectedFiles.length > 0 &&
                   !selectedFiles[0].isDirectory() &&
                   IdeaUtilImpl.isValidFileType(selectedFiles[0].getFileType());
        }

        e.getPresentation().setEnabled(enable);
        e.getPresentation().setVisible(true);
        setTextAndDescription(e, selectedFiles);
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

		final VirtualFile[] selectedFiles = IdeaUtilImpl.getVirtualFiles(e.getDataContext());
		final String taskTitle = getTaskTitle(e.getDataContext(), selectedFiles);

		new FindBugsStarter(project, taskTitle) {
			@Override
			protected void createCompileScope(@NotNull final CompilerManager compilerManager, @NotNull final Consumer<CompileScope> consumer) {
				consumer.consume(createFilesCompileScope(compilerManager, selectedFiles));
			}

			@Override
			protected boolean configure(@NotNull final ProgressIndicator indicator, @NotNull final FindBugsProjects projects, final boolean justCompiled) {
				return projects.addFiles(selectedFiles, !justCompiled, hasTests(selectedFiles));
			}
		}.start();
	}

	private void setTextAndDescription(@NotNull final AnActionEvent e, @Nullable final VirtualFile[] selectedFiles) {
		if (PlatformDataKeys.EDITOR.getData(e.getDataContext()) == null) {
			String text = "Analyze Selected File";
			String suffix = (selectedFiles == null || selectedFiles.length > 1) ? "s" : "";
			text += suffix;
			e.getPresentation().setText(text);
			e.getPresentation().setDescription(
					"Run SpotBugs analysis on the current selected file" + suffix + " in the project view."
			);
		} else {
			e.getPresentation().setText("Analyze Current File");
			e.getPresentation().setDescription("Run SpotBugs analysis on the current editor file.");
		}
	}

	private String getTaskTitle(@NotNull final DataContext dataContext, @Nullable final VirtualFile[] selectedFiles) {
		if (PlatformDataKeys.EDITOR.getData(dataContext) == null) {
			String suffix = (selectedFiles == null || selectedFiles.length > 1) ? "s" : "";
			return "Running SpotBugs analysis for selected file" + suffix + "...";
		}
		return "Running SpotBugs analysis for editor file...";
	}
}
