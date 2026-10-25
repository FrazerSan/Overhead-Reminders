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
        applyOldDefaults(manager);
        if (!"1".equals(manager.getConfiguration(GROUP, "hornActiveDefaultsRevision")))
        {
            String horn = manager.getConfiguration(GROUP, "soulflameHornMessage");
            if ("Blow Soulflame Horn".equals(horn) || "Soulflame Horn Active!".equals(horn))
            {
                manager.setConfiguration(GROUP, "soulflameHornMessage", "Soulflame Horn Active");
            }
            manager.setConfiguration(GROUP, "hornActiveDefaultsRevision", "1");
        }
        if ("1".equals(manager.getConfiguration(GROUP, "actionDefaultsRevision")))
        {
            return;
        }
        // Only replace exact former defaults. Never disable a saved enabled choice.
        String[][] messages = {
            {"divinePotMessage", "Divine Expired!", "Drink Divine"},
            {"saturatedHeartMessage", "Saturated Heart Expired!", "Invigorate Saturated Heart"},
            {"meleeBoostMessage", "Melee Boost", "Drink Melee Boost"},
            {"magicBoostMessage", "Magic Boost", "Drink Magic Boost"},
            {"rangedBoostMessage", "Range Boost", "Drink Range Boost"},
            {"overloadMessage", "Overload Expired!", "Drink Overload"},
            {"smellingSaltMessage", "Smelling Salt Expired!", "Use Smelling Salt"},
            {"thrallMessage", "Thrall Expired!", "Cast Thrall"},
            {"deathChargeMessage", "Death Charge Ready!", "Cast Death Charge"},
            {"markOfDarknessMessage", "Mark of Darkness expired!", "Cast Mark of Darkness"},
            {"WardOfArceuusMessage", "Ward of Arceuus expired!", "Cast Ward of Arceuus"},
            {"surgePotionMessage", "Surge Potion Ready!", "Drink Surge"},
            {"staminaMessage", "Stamina potion Expired!", "Drink Stamina"},
            {"antiPoisonMessage", "AntiPoison Expired!", "Drink Antipoison"},
            {"venomMessage", "Venom protection Expired!", "Drink Anti-venom"},
            {"antifireMessage", "Antifire protection Expired!", "Drink Antifire"}
        };
        for (String[] message : messages)
        {
            if (message[1].equals(manager.getConfiguration(GROUP, message[0])))
            {
                manager.setConfiguration(GROUP, message[0], message[2]);
            }
        }
        manager.setConfiguration(GROUP, "actionDefaultsRevision", "1");
    }

    private static void applyOldDefaults(ConfigManager manager)
    {
        if ("1".equals(manager.getConfiguration(GROUP, REVISION)))
        {
            return;
        }
        Map<String, String[]> replacements = new LinkedHashMap<>();
        replacements.put("overheadDisplayTime", new String[] {"3", "15"});
        replacements.put("lowPrayerMessage", new String[] {"LOW PRAYER!", "Low Prayer"});
        replacements.put("meleeBoostMessage", new String[] {"MELEE BOOST LOW!", "Melee Boost"});
        replacements.put("magicBoostMessage", new String[] {"MAGIC BOOST LOW!", "Magic Boost"});
        replacements.put("rangedBoostMessage", new String[] {"RANGED BOOST LOW!", "Range Boost"});
        replacements.put("wildPieMessage", new String[] {"SLAYER BOOST LOW!", "Slayer Boost"});
        replacements.put("summerPieMessage", new String[] {"AGILITY BOOST LOW!", "Agility Boost"});
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
