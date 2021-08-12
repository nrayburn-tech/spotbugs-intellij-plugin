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

package org.jetbrains.plugins.spotbugs.common;

import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;
import org.jetbrains.plugins.spotbugs.resources.ResourcesLoader;

import java.io.File;

public enum ExportErrorType {
    EMPTY_PATH("export.error.emptyPath"),
    NOT_DIRECTORY("error.directory.type"),
    NOT_WRITABLE("error.directory.writable");

    @NotNull
    private final @PropertyKey(resourceBundle = ResourcesLoader.BUNDLE) String propertyKey;

    ExportErrorType(@PropertyKey(resourceBundle = ResourcesLoader.BUNDLE) @NotNull String propertyKey) {
        this.propertyKey = propertyKey;
    }

    @Nullable
    public static ExportErrorType from(@NotNull File exportDirPath) {
        if (StringUtil.isEmptyOrSpaces(exportDirPath.getPath())) {
            return ExportErrorType.EMPTY_PATH;
        }
        if (exportDirPath.exists()) {
            if (!exportDirPath.isDirectory()) {
                return ExportErrorType.NOT_DIRECTORY;
            } else if (!exportDirPath.canWrite()) {
                return ExportErrorType.NOT_WRITABLE;
            }
        }
        return null;
    }

    public String getText(File exportDirPath) {
        return ResourcesLoader.getString(propertyKey, exportDirPath);
    }
}
