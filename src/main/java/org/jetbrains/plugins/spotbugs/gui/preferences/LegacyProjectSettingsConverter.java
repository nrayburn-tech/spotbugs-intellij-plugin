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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.spotbugs.core.ModuleSettings;
import org.jetbrains.plugins.spotbugs.core.ProjectSettings;
import org.jetbrains.plugins.spotbugs.core.WorkspaceSettings;
import org.jetbrains.plugins.spotbugs.preferences.PersistencePreferencesBean;

import java.util.List;

public final class LegacyProjectSettingsConverter {

	/**
	 * We can not persist changes immediately in some cases (see issue #121).
	 * So it is necessary to read both (project- and all module-settings) all the time
	 * because it could be possible that IDEA only persist the converted project settings
	 * but not the module settings (f. e. user open only project settings. But, in general,
	 * it is up to IDEA when the settings are stored).
	 */
	public static void convertSettings(@NotNull final Project project) {

		final LegacyProjectSettings legacy = LegacyProjectSettings.getInstance(project);
		PersistencePreferencesBean legacyBean = null;
		List<String> enabledModuleConfigs = null;

		if (legacy != null) {
			legacyBean = legacy.getState();
			if (legacyBean != null) {
				enabledModuleConfigs = legacyBean.getEnabledModuleConfigs();
			}
		}

		final WorkspaceSettings currentWorkspace = WorkspaceSettings.getInstance(project);

		// first convert module settings if necessary
		for (final Module module : ModuleManager.getInstance(project).getModules()) {
			final LegacyModuleSettings legacyModuleSettings = LegacyModuleSettings.getInstance(module);
			if (legacyModuleSettings != null && legacyModuleSettings.getState() != null) {
				final ModuleSettings currentModule = ModuleSettings.getInstance(module);
				legacyModuleSettings.applyTo(currentModule, null);
				currentModule.overrideProjectSettings = enabledModuleConfigs != null && enabledModuleConfigs.contains(module.getName());
			}
		}

		// convert project- after module-settings if necessary
		if (legacyBean != null) {
			final ProjectSettings current = ProjectSettings.getInstance(project);
			legacy.applyTo(current, currentWorkspace);
		}

		//ApplicationManager.getApplication().saveAll(); can not persist changes immediately - see javadoc above
	}
}
