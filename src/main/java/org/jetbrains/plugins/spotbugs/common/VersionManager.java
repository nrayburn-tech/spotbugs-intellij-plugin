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

import org.jetbrains.plugins.spotbugs.common.util.IoUtil;

import java.io.*;
import java.nio.charset.*;
import java.util.Properties;


/**
 * @author Andre Pfeiler<andrep@twodividedbyzero.org>
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr", "StringConcatenation", "CallToPrintStackTrace", "CallToPrintStackTrace"})
public class VersionManager {

	private static final String PROPERTIES_FILE = "version.properties";
	private static final Version VERSION = Version.load(PROPERTIES_FILE);

	static class Version {
		private final long major;
		private final long minor;
		private final long build;

		private Version(long major, long minor, long build) {
			this.major = major;
			this.minor = minor;
			this.build = build;
		}

		@Override
		public String toString() {
			return major + "." + minor + "." + build;
		}

		@SuppressWarnings("SameParameterValue")
		static Version load(String resource) {
			Properties properties = new Properties();
			try (InputStream stream = VersionManager.class.getResourceAsStream(resource)) {
				properties.load(stream);
			} catch (IOException e) {
				throw new RuntimeException("Unable to read '"+resource+"': build corrupted", e);
			}
			return load(properties);
		}

		static Version load(Properties properties) {
			String version = properties.getProperty("pluginVersion");
			if (version == null) {
				throw new RuntimeException("Unable to read version from '"+PROPERTIES_FILE+"': build corrupted");
			}
			String[] components = version.split("\\.");
			if (components.length != 3) {
				throw new RuntimeException("Invalid version: "+version);
			}
			try {
				return new Version(Long.parseLong(components[0]), Long.parseLong(components[1]), Long.parseLong(components[2]));
			} catch (NumberFormatException e) {
				throw new RuntimeException("Invalid version: "+version);
			}
		}
	}

	private static final String NAME = FindBugsPluginConstants.PLUGIN_NAME;

	private static final String WEBSITE = "https://github.com/JetBrains/spotbugs-intellij-plugin/";

	private static final String DOWNLOAD_WEBSITE = "https://plugins.jetbrains.com/plugin/14014-spotbugs";

	private static final String ISSUE_TRACKER = WEBSITE + "issues";

	private static final long REVISION;

	private static final String FULL_VERSION;

	private static final String MAJOR_MINOR_BUILD = VERSION.toString();

	static {
		final String revisionString = VersionManager.class.getPackage().getImplementationVersion();
		long parsedRevision = -1;
		if (revisionString != null) {
			try {
				parsedRevision = Long.parseLong(revisionString);
				System.out.println("Revision: " + revisionString);
				System.out.println("parsedRevision: " + parsedRevision);
			} catch (final RuntimeException ignore) {
			}
		}
		REVISION = parsedRevision;
		FULL_VERSION = NAME + ' ' + MAJOR_MINOR_BUILD;
	}

	/**
	 * @return version number, e.g. "1.0.0"
	 */
	public static String getVersion() {
		return MAJOR_MINOR_BUILD;
	}


	public static String getFullVersion() {
		return FULL_VERSION;
	}


	public static long getRevision() {
		return REVISION;
	}


	public static String getName() {
		return NAME;
	}


	public static String getWebsite() {
		return WEBSITE;
	}


	public static String getDownloadWebsite() {
		return DOWNLOAD_WEBSITE;
	}


	public static String getIssueTracker() {
		return ISSUE_TRACKER;
	}


	@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
	public static void main(final String[] args) {
		if (args.length == 1) {
			final File file = new File(args[0]);
			System.out.println("version string file: " + args[0]);
			OutputStreamWriter writer = null;
			try {
				//noinspection IOResourceOpenedButNotSafelyClosed
				writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
				writer.write(getVersion());
				writer.flush();
			} catch (final IOException e) {
				e.printStackTrace();
			} finally {
				IoUtil.safeClose(writer);
			}

		}
		System.out.println(getVersion());
	}
}
