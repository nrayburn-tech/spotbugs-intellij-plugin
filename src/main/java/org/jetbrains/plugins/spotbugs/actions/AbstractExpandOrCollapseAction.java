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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.spotbugs.core.FindBugsState;
import org.jetbrains.plugins.spotbugs.gui.toolwindow.view.BugTreePanel;
import org.jetbrains.plugins.spotbugs.gui.toolwindow.view.ToolWindowPanel;

import javax.swing.JTree;

abstract class AbstractExpandOrCollapseAction extends AbstractAction {

	@Override
	final void updateImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@NotNull final ToolWindow toolWindow,
			@NotNull final FindBugsState state
	) {

		final ToolWindowPanel panel = ToolWindowPanel.getInstance(toolWindow);
		if (panel == null) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
		final JTree tree = panel.getBugTreePanel().getBugTree();
		final boolean enabled = isExpandedOrCollapsed(tree) && tree.getRowCount() > 1;
		e.getPresentation().setEnabled(enabled);
		e.getPresentation().setVisible(true);
	}

	abstract boolean isExpandedOrCollapsed(@NotNull final JTree bugTree);

	@Override
	final void actionPerformedImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@NotNull final ToolWindow toolWindow,
			@NotNull final FindBugsState state
	) {

		final ToolWindowPanel panel = ToolWindowPanel.getInstance(toolWindow);
		if (panel != null) {
			expandOrCollapse(panel.getBugTreePanel());
		}
	}

	abstract void expandOrCollapse(@NotNull final BugTreePanel bugTreePanel);
}
