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

package icons;

import javax.swing.*;
import java.util.*;

import static icons.PluginIcons.*;

class InitIcons {
  static Map<String, Icon> initGroupByRankIconsMap() {
      Map<String, Icon> iconMap = new HashMap<>();
      iconMap.put("SCARIEST", GROUP_BY_RANK_SCARIEST_ICON);
      iconMap.put("SCARY", GROUP_BY_RANK_SCARY_ICON);
      iconMap.put("TROUBLING", GROUP_BY_RANK_TROUBLING_ICON);
      iconMap.put("OF_CONCERN", GROUP_BY_RANK_OF_CONCERN_ICON);
      iconMap.put("OF CONCERN", GROUP_BY_RANK_OF_CONCERN_ICON);
      return Collections.unmodifiableMap(iconMap);
  }

  static Map<String, Icon> initGroupByPriorityIconsMap() {
      Map<String, Icon> iconMap = new HashMap<>();
      iconMap.put("Low", GROUP_BY_PRIORITY_LOW_ICON);
      iconMap.put("Medium", GROUP_BY_PRIORITY_MEDIUM_ICON);
      iconMap.put("High", GROUP_BY_PRIORITY_HIGH_ICON);
      iconMap.put("Exp", GROUP_BY_PRIORITY_EXP_ICON);
      iconMap.put("Ignore", GROUP_BY_PRIORITY_IGNORE_ICON);
      return Collections.unmodifiableMap(iconMap);
  }
}
