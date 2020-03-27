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
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.spotbugs.core.AbstractSettings;
import org.jetbrains.plugins.spotbugs.core.FindBugsState;
import org.jetbrains.plugins.spotbugs.core.ProjectSettings;
import org.jetbrains.plugins.spotbugs.core.WorkspaceSettings;
import org.jetbrains.plugins.spotbugs.gui.toolwindow.view.ToolWindowPanel;

public final class ScrollToSource extends AbstractToggleAction {

	@Override
	boolean isSelectedImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@Nullable final Module module,
			@NotNull final ToolWindow toolWindow,
			@NotNull final ToolWindowPanel panel,
			@NotNull final FindBugsState state,
			@NotNull final ProjectSettings projectSettings,
			@NotNull final AbstractSettings settings
	) {

		final boolean isEnabled = panel.getBugTreePanel().isScrollToSource();
		final WorkspaceSettings workspaceSettings = WorkspaceSettings.getInstance(project);
		final boolean enabled = workspaceSettings.toolWindowScrollToSource;
		if (enabled != isEnabled) {
			panel.getBugTreePanel().setScrollToSource(enabled);
		}
		return enabled;
	}

	@Override
	void setSelectedImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@Nullable final Module module,
			@NotNull final ToolWindow toolWindow,
			@NotNull final ToolWindowPanel panel,
			@NotNull final FindBugsState state,
			@NotNull final ProjectSettings projectSettings,
			@NotNull final AbstractSettings settings,
			final boolean select
	) {

		final WorkspaceSettings workspaceSettings = WorkspaceSettings.getInstance(project);
		workspaceSettings.toolWindowScrollToSource = select;
		panel.getBugTreePanel().setScrollToSource(select);
	}
}
