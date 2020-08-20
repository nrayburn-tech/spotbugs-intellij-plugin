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

import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class VersionManagerTest {
  @Test
  public void testVersion() {
    Properties properties = new Properties();
    properties.setProperty("version", "1.0.2");
    VersionManager.Version ver = VersionManager.Version.load(properties);
    assertEquals("1.0.2", ver.toString());
  }
  
  @Test(expected = RuntimeException.class)
  public void testVersionAbsent() {
    VersionManager.Version.load(new Properties());
  }
  
  @Test(expected = RuntimeException.class)
  public void testVersionCorrupted() {
    Properties properties = new Properties();
    properties.setProperty("version", "1.0.1.2");
    VersionManager.Version.load(properties);
  }
  
  @Test(expected = RuntimeException.class)
  public void testVersionCorrupted2() {
    Properties properties = new Properties();
    properties.setProperty("version", "1.0.x");
    VersionManager.Version.load(properties);
  }
}