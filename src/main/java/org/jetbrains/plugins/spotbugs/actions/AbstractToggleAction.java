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

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.spotbugs.common.util.IdeaUtilImpl;
import org.jetbrains.plugins.spotbugs.core.AbstractSettings;
import org.jetbrains.plugins.spotbugs.core.FindBugsState;
import org.jetbrains.plugins.spotbugs.core.ModuleSettings;
import org.jetbrains.plugins.spotbugs.core.ProjectSettings;
import org.jetbrains.plugins.spotbugs.gui.toolwindow.view.ToolWindowPanel;

abstract class AbstractToggleAction extends ToggleAction {

  @Override
  public @NotNull ActionUpdateThread getActionUpdateThread() {
    return ActionUpdateThread.EDT;
  }

  @Override
	public final void update(@NotNull AnActionEvent e) {
		final Project project = IdeaUtilImpl.getProject(e.getDataContext());
		if (project == null || !project.isInitialized() || !project.isOpen()) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
		final ToolWindow toolWindow = ToolWindowPanel.getWindow(project);
		if (toolWindow == null || !toolWindow.isAvailable()) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
		final ToolWindowPanel panel = ToolWindowPanel.getInstance(toolWindow);
		if (panel == null) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      final Module module = IdeaUtilImpl.getModule(e.getDataContext(), project);
      final ProjectSettings projectSettings = ProjectSettings.getInstance(project);
      AbstractSettings settings = projectSettings;
      if (module != null) {
        final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
        if (moduleSettings.overrideProjectSettings) {
          settings = moduleSettings;
        }
      }

      final AbstractSettings finalSettings = settings;
      ApplicationManager.getApplication().invokeLater(() -> {
        final boolean select = isSelectedImpl(
          e,
          project,
          module,
          toolWindow,
          panel,
          FindBugsState.get(project),
          projectSettings,
          finalSettings
        );
        final Boolean selected = select ? Boolean.TRUE : Boolean.FALSE;
        e.getPresentation().putClientProperty(SELECTED_KEY, selected);
        e.getPresentation().setEnabledAndVisible(true);
      });
    });
	}

	@Override
	public final boolean isSelected(@NotNull AnActionEvent e) {
		final Project project = IdeaUtilImpl.getProject(e.getDataContext());
		if (project == null || !project.isInitialized() || !project.isOpen()) {
			e.getPresentation().setEnabledAndVisible(false);
			return false;
		}
		final ToolWindow toolWindow = ToolWindowPanel.getWindow(project);
		if (toolWindow == null || !toolWindow.isAvailable()) {
			e.getPresentation().setEnabledAndVisible(false);
			return false;
		}
		final ToolWindowPanel panel = ToolWindowPanel.getInstance(toolWindow);
		if (panel == null) {
			e.getPresentation().setEnabledAndVisible(false);
			return false;
		}
		final Module module = IdeaUtilImpl.getModule(e.getDataContext(), project);
		final ProjectSettings projectSettings = ProjectSettings.getInstance(project);
		AbstractSettings settings = projectSettings;
		if (module != null) {
			final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
			if (moduleSettings.overrideProjectSettings) {
				settings = moduleSettings;
			}
		}
		return isSelectedImpl(
				e,
				project,
				module,
				toolWindow,
				panel,
				FindBugsState.get(project),
				projectSettings,
				settings
		);
	}

	abstract boolean isSelectedImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@Nullable final Module module,
			@NotNull final ToolWindow toolWindow,
			@NotNull final ToolWindowPanel panel,
			@NotNull final FindBugsState state,
			@NotNull final ProjectSettings projectSettings,
			@NotNull final AbstractSettings settings
	);

	@Override
	public final void setSelected(@NotNull AnActionEvent e, boolean select) {
		final Project project = IdeaUtilImpl.getProject(e.getDataContext());
		if (project == null || !project.isInitialized() || !project.isOpen()) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
		final ToolWindow toolWindow = ToolWindowPanel.getWindow(project);
		if (toolWindow == null || !toolWindow.isAvailable()) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
		final ToolWindowPanel panel = ToolWindowPanel.getInstance(toolWindow);
		if (panel == null) {
			e.getPresentation().setEnabledAndVisible(false);
			return;
		}
		final Module module = IdeaUtilImpl.getModule(e.getDataContext(), project);
		final ProjectSettings projectSettings = ProjectSettings.getInstance(project);
		AbstractSettings settings = projectSettings;
		if (module != null) {
			final ModuleSettings moduleSettings = ModuleSettings.getInstance(module);
			if (moduleSettings.overrideProjectSettings) {
				settings = moduleSettings;
			}
		}
		setSelectedImpl(
				e,
				project,
				module,
				toolWindow,
				panel,
				FindBugsState.get(project),
				projectSettings,
				settings,
				select
		);
	}

	abstract void setSelectedImpl(
			@NotNull final AnActionEvent e,
			@NotNull final Project project,
			@Nullable final Module module,
			@NotNull final ToolWindow toolWindow,
			@NotNull final ToolWindowPanel panel,
			@NotNull final FindBugsState state,
			@NotNull final ProjectSettings projectSettings,
			@NotNull final AbstractSettings settings,
			boolean select
	);
}
