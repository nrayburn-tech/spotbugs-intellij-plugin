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

import edu.umd.cs.findbugs.BugCollection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class XmlBugCollectionExporter implements BugCollectionExporter {

    public void export(@NotNull final BugCollection bugCollection, @NotNull final File file) throws IOException {
        // Issue 77: workaround internal FindBugs NPE
        // As of my point of view, the NPE is a FindBugs bug
        bugCollection.writeXML(file.getPath());
    }
}
