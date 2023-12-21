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
package org.jetbrains.plugins.spotbugs.gui.preferences;

import com.intellij.openapi.components.*;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.spotbugs.common.FindBugsPluginConstants;
import org.jetbrains.plugins.spotbugs.core.ProjectSettings;
import org.jetbrains.plugins.spotbugs.core.WorkspaceSettings;
import org.jetbrains.plugins.spotbugs.preferences.PersistencePreferencesBean;

/**
 * Legacy settings are converted (by {@link LegacyProjectSettingsConverter}) to {@link ProjectSettings}.
 * The settings are removed when the .ipr or config xml is stored next time.
 */
@Service(Service.Level.PROJECT)
@State(
		name = FindBugsPluginConstants.PLUGIN_ID,
		storages = {
				@Storage(value = "$PROJECT_FILE$", deprecated = true),
				@Storage(value = "$PROJECT_CONFIG_DIR$/findbugs-idea.xml", deprecated = true)})
public final class LegacyProjectSettings implements PersistentStateComponent<PersistencePreferencesBean> {

	private static final Logger LOGGER = Logger.getInstance(LegacyProjectSettings.class);

	private PersistencePreferencesBean state;

	@Nullable
	@Override
	public PersistencePreferencesBean getState() {
		return state;
	}

	@Override
	public void loadState(final PersistencePreferencesBean state) {
		this.state = state;
	}

	public static LegacyProjectSettings getInstance(@NotNull final Project project) {
		return project.getService(LegacyProjectSettings.class);
	}

	void applyTo(@NotNull final ProjectSettings settings, @NotNull final WorkspaceSettings workspaceSettings) {
		if (state == null) {
			return;
		}
		LOGGER.info("Start convert legacy findbugs-idea project settings");
		LegacyAbstractSettingsConverter.applyTo(state, settings, workspaceSettings, WorkspaceSettings.PROJECT_IMPORT_FILE_PATH_KEY);
	}
}
