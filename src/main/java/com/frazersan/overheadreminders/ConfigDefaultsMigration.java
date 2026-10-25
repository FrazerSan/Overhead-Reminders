package com.frazersan.overheadreminders;

import java.util.LinkedHashMap;
import java.util.Map;
import net.runelite.client.config.ConfigManager;

/** Update stored old defaults once, preserving other values and later user edits. */
final class ConfigDefaultsMigration
{
    private static final String GROUP = "overheadreminder";
    private static final String REVISION = "tidyDefaultsRevision";

    static void apply(ConfigManager manager)
    {
        if ("1".equals(manager.getConfiguration(GROUP, REVISION)))
        {
            return;
        }
        Map<String, String[]> replacements = new LinkedHashMap<>();
        replacements.put("overheadDisplayTime", new String[] {"3", "15"});
        replacements.put("lowPrayerDuration", new String[] {"0", "15"});
        replacements.put("lowPrayerMessage", new String[] {"LOW PRAYER!", "Low Prayer"});
        replacements.put("meleeBoostMessage", new String[] {"MELEE BOOST LOW!", "Melee Boost"});
        replacements.put("magicBoostMessage", new String[] {"MAGIC BOOST LOW!", "Magic Boost"});
        replacements.put("rangedBoostMessage", new String[] {"RANGED BOOST LOW!", "Range Boost"});
        replacements.put("wildPieMessage", new String[] {"SLAYER BOOST LOW!", "Slayer Boost"});
        replacements.put("summerPieMessage", new String[] {"AGILITY BOOST LOW!", "Agility Boost"});
        for (String prefix : new String[] {"meleeBoost", "magicBoost", "rangedBoost", "wildPie", "summerPie"})
        {
            replacements.put(prefix + "Timeout", new String[] {"0", "15"});
        }
        for (Map.Entry<String, String[]> entry : replacements.entrySet())
        {
            String[] values = entry.getValue();
            if (values[0].equals(manager.getConfiguration(GROUP, entry.getKey())))
            {
                manager.setConfiguration(GROUP, entry.getKey(), values[1]);
            }
        }
        manager.setConfiguration(GROUP, REVISION, "1");
    }
}
