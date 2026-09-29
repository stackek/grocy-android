/*
 * This file is part of Grocy Android.
 *
 * Grocy Android is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Grocy Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Grocy Android. If not, see http://www.gnu.org/licenses/.
 *
 * Copyright (c) 2020-2024 by Patrick Zedler and Dominic Zedler
 * Copyright (c) 2024-2026 by Patrick Zedler
 */

package xyz.zedler.patrick.grocy.util;

import android.content.SharedPreferences;
import xyz.zedler.patrick.grocy.Constants;

/**
 * OfflineModeUtil handles offline mode configuration and checks.
 * The app always runs in offline mode (local-only).
 */
public class OfflineModeUtil {

  public static final String OFFLINE_MODE_ENABLED = "offline_mode_enabled";

  /**
   * Enable offline mode for the app.
   * In this version, offline mode is always enabled.
   */
  public static void enableOfflineMode(SharedPreferences prefs) {
    prefs.edit().putBoolean(OFFLINE_MODE_ENABLED, true).apply();
    prefs.edit().remove(Constants.PREF.SERVER_URL).apply();
    prefs.edit().remove(Constants.PREF.API_KEY).apply();
  }

  /**
   * Check if offline mode is enabled.
   * Returns true by default (always offline).
   */
  public static boolean isOfflineMode(SharedPreferences prefs) {
    return prefs.getBoolean(OFFLINE_MODE_ENABLED, true);
  }

  /**
   * Initialize offline mode on first launch.
   */
  public static void initializeOfflineModeIfNeeded(SharedPreferences prefs) {
    if (!prefs.contains(OFFLINE_MODE_ENABLED)) {
      enableOfflineMode(prefs);
    }
  }
}
