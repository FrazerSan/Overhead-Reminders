package com.frazersan.overheadreminders;

import net.runelite.client.config.ConfigManager;

/** Preserve saved settings. Only fill missing compatibility choices for older timers. */
final class ConfigDefaultsMigration
{
    private static final String GROUP = "overheadreminder";

    static void apply(ConfigManager manager)
    {
        if ("1".equals(manager.getConfiguration(GROUP, "sharedTimerDefaultsRevision"))) return;
        String[][] legacyTimers = {
            {"lowPrayerDuration", "lowPrayerUseGlobalTimer"},
            {"meleeBoostTimeout", "meleeBoostUseGlobalTimer"},
            {"magicBoostTimeout", "magicBoostUseGlobalTimer"},
            {"rangedBoostTimeout", "rangedBoostUseGlobalTimer"},
            {"wildPieTimeout", "wildPieUseGlobalTimer"},
            {"summerPieTimeout", "summerPieUseGlobalTimer"}
        };
        for (String[] timer : legacyTimers)
        {
            // Before shared timers, these saved durations applied directly. Keep
            // that behaviour, including an explicitly saved 15-second duration.
            if (manager.getConfiguration(GROUP, timer[0]) != null
                && manager.getConfiguration(GROUP, timer[1]) == null)
            {
                manager.setConfiguration(GROUP, timer[1], "false");
            }
        }
        manager.setConfiguration(GROUP, "sharedTimerDefaultsRevision", "1");
    }
}