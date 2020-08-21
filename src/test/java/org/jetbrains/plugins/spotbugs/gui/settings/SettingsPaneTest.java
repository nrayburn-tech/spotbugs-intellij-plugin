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

package org.jetbrains.plugins.spotbugs.gui.settings;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.testFramework.fixtures.JavaCodeInsightFixtureTestCase;
import org.jetbrains.plugins.spotbugs.core.AbstractSettings;
import org.jetbrains.plugins.spotbugs.core.ModuleSettings;
import org.jetbrains.plugins.spotbugs.core.WorkspaceSettings;

public class SettingsPaneTest extends JavaCodeInsightFixtureTestCase {
    public void testSettingsPane() throws ConfigurationException {
        Module module = getModule();
        SettingsPane pane = new ModuleSettingsPane(getProject(), module);
        AbstractSettings settings = ModuleSettings.getInstance(module);
        pane.reset(settings);
        pane.resetWorkspace(WorkspaceSettings.getInstance(getProject()));
        pane.requestFocusOnShareImportFile();
        pane.setFilter("blah-blah-blah");
        pane.apply(settings);
    }
    public void testModuleSettingsPane() {
        Module module = getModule();
        ModuleSettingsPane pane = new ModuleSettingsPane(getProject(), module);
        ModuleSettings settings = ModuleSettings.getInstance(module);
        pane.resetWorkspace(WorkspaceSettings.getInstance(getProject()));
        pane.applyModule(settings);
    }

    public void testProjectSettingsPaneConstructor() {
        new ProjectSettingsPane((getProject()));
    }
}
