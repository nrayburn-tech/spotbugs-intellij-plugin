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
package org.jetbrains.plugins.spotbugs.resources;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.IconLoader;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.PropertyKey;

import javax.swing.Icon;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * $Date$
 *
 * @author Andre Pfeiler<andrep@twodividedbyzero.org>
 * @version $Revision$
 * @since 0.0.1
 */
@SuppressWarnings("HardcodedFileSeparator")
public final class ResourcesLoader {

	private static final Logger LOGGER = Logger.getInstance(ResourcesLoader.class.getName());

	private static volatile ResourceBundle _bundle;
	public static final String BUNDLE = "org.jetbrains.plugins.spotbugs.resources.i18n.Messages";
	private static final String ICON_RESOURCES_PKG = "/org/jetbrains/plugins/spotbugs/resources/icons";


	private ResourcesLoader() {
	}


	@NotNull
	public static ResourceBundle getResourceBundle() {
		LOGGER.info("Loading locale properties for '" + Locale.getDefault() + ')');

		//noinspection StaticVariableUsedBeforeInitialization
		if (_bundle != null) {
			return _bundle;
		}

		//noinspection UnusedCatchParameter
		try {
			_bundle = ResourceBundle.getBundle(BUNDLE, Locale.getDefault());
		} catch (final MissingResourceException e) {
			throw new MissingResourceException("Missing Resource bundle: " + Locale.getDefault() + ' ', BUNDLE, "");
		}

		return _bundle;
	}


	@Nls
	@SuppressWarnings({"UnusedCatchParameter"})
	public static String getString(@NotNull @PropertyKey(resourceBundle = BUNDLE) final String key, @Nullable Object... params) {
		try {
			//noinspection StaticVariableUsedBeforeInitialization
			if (_bundle == null) {
				getResourceBundle();
			}
			String ret = _bundle.getString(key);
			if (params != null && params.length > 0 && ret.indexOf('{') >= 0) {
				return MessageFormat.format(ret, params);
			}
			return ret;
		} catch (final MissingResourceException e) {
			throw new MissingResourceException("Missing Resource: " + Locale.getDefault() + " - key: " + key + "  - resources: " + BUNDLE, BUNDLE, key);
		}
	}

	@NotNull
	public static Icon loadIcon(final String filename) {
		return IconLoader.getIcon(ICON_RESOURCES_PKG + '/' + filename);
	}

}