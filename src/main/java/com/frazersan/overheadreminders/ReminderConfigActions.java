package com.frazersan.overheadreminders;

import net.runelite.client.config.ConfigManager;

/** Explicit, scoped bulk actions. Enabled reminders and thresholds are preserved. */
final class ReminderConfigActions
{
    private static final String GROUP = "overheadreminder";
    private static final String[][] TIMERS = {
        {"lowPrayerDuration", "lowPrayerUseGlobalTimer", ""},
        {"prayerRegenDuration", "prayerRegenUseGlobalTimer", "prayerRegenEarlyWarning"},
        {"prayerEnhanceDuration", "prayerEnhanceUseGlobalTimer", "prayerEnhanceEarlyWarning"},
        {"divinePotDuration", "divinePotUseGlobalTimer", "divinePotEarlyWarning"},
        {"saturatedHeartDuration", "saturatedHeartUseGlobalTimer", "saturatedHeartEarlyWarning"},
        {"meleeBoostTimeout", "meleeBoostUseGlobalTimer", ""},
        {"rangedBoostTimeout", "rangedBoostUseGlobalTimer", ""},
        {"magicBoostTimeout", "magicBoostUseGlobalTimer", ""},
        {"soulflameHornDuration", "soulflameHornUseGlobalTimer", ""},
        {"wildPieTimeout", "wildPieUseGlobalTimer", ""},
        {"summerPieTimeout", "summerPieUseGlobalTimer", ""},
        {"staminaDuration", "staminaUseGlobalTimer", "staminaEarlyWarning"},
        {"surgePotionDuration", "surgePotionUseGlobalTimer", "surgePotionEarlyWarning"},
        {"antiPoisonDuration", "antiPoisonUseGlobalTimer", "antiPoisonEarlyWarning"},
        {"venomDuration", "venomUseGlobalTimer", "venomEarlyWarning"},
        {"antifireDuration", "antifireUseGlobalTimer", "antifireEarlyWarning"},
        {"overloadDuration", "overloadUseGlobalTimer", "overloadEarlyWarning"},
        {"smellingSaltDuration", "smellingSaltUseGlobalTimer", "smellingSaltEarlyWarning"},
        {"liquidAdrenalineDuration", "liquidAdrenalineUseGlobalTimer", "liquidAdrenalineEarlyWarning"},
        {"thrallDuration", "thrallUseGlobalTimer", "thrallEarlyWarning"},
        {"deathChargeDuration", "deathChargeUseGlobalTimer", "deathChargeEarlyWarning"},
        {"markOfDarknessDuration", "markOfDarknessUseGlobalTimer", "markOfDarknessEarlyWarning"},
        {"wardOfArceuusDuration", "wardOfArceuusUseGlobalTimer", "wardOfArceuusEarlyWarning"}
    };
    private static final String[][] MESSAGES = {
        {"lowPrayerMessage", "Low Prayer"},
        {"prayerRegenMessage", "Prayer regeneration potion Expired!"},
        {"prayerEnhanceMessage", "Prayer enhance potion Expired!"},
        {"divinePotMessage", "Drink Divine"},
        {"saturatedHeartMessage", "Invigorate Saturated Heart"},
        {"meleeBoostMessage", "Drink Melee Boost"},
        {"rangedBoostMessage", "Drink Range Boost"},
        {"magicBoostMessage", "Drink Magic Boost"},
        {"soulflameHornMessage", "Soulflame Horn Active"},
        {"wildPieMessage", "Slayer Boost"},
        {"summerPieMessage", "Agility Boost"},
        {"staminaMessage", "Drink Stamina"},
        {"surgePotionMessage", "Drink Surge"},
        {"antiPoisonMessage", "Drink Antipoison"},
        {"venomMessage", "Drink Anti-venom"},
        {"antifireMessage", "Drink Antifire"},
        {"overloadMessage", "Drink Overload"},
        {"smellingSaltMessage", "Use Smelling Salt"},
        {"liquidAdrenalineMessage", "Drink Liquid Adrenaline"},
        {"thrallMessage", "Cast Thrall"},
        {"deathChargeMessage", "Cast Death Charge"},
        {"markOfDarknessMessage", "Cast Mark of Darkness"},
        {"WardOfArceuusMessage", "Cast Ward of Arceuus"}
    };
    private static final String[] ENABLED = {
        "enableLowPrayer",
        "enablePrayerRegen",
        "enablePrayerEnhance",
        "enableDivinePot",
        "enableSaturatedHeart",
        "meleeBoostEnabled",
        "rangedBoostEnabled",
        "magicBoostEnabled",
        "enableSoulflameHorn",
        "wildPieEnabled",
        "summerPieEnabled",
        "enableStamina",
        "enableSurgePotion",
        "enableAntiPoison",
        "enableVenom",
        "enableAntifire",
        "enableOverload",
        "enableSmellingSalt",
        "enableLiquidAdrenaline",
        "enableThrall",
        "enableDeathCharge",
        "enableMarkOfDarkness",
        "enableWardOfArceuus"
    };
    private final ConfigManager manager;
    private final Runnable settingsChanged;
    ReminderConfigActions(ConfigManager manager) { this(manager, () -> {}); }
    ReminderConfigActions(ConfigManager manager, Runnable settingsChanged)
    {
        this.manager = manager;
        this.settingsChanged = settingsChanged;
    }

    void resetAppearance()
    {
        set("verticalOffset", "40");
        set("horizontalOffset", "0");
        set("messageSpacing", "2");
        set("textSize", "0");
        settingsChanged.run();
    }

    void resetTimers()
    {
        set("overheadDisplayTime", "15");
        set("showCountdown", "false");
        for (String[] timer : TIMERS)
        {
            set(timer[0], "15");
            set(timer[1], "true");
            if (!timer[2].isEmpty()) set(timer[2], "0");
        }
        settingsChanged.run();
    }

    void resetMessages()
    {
        for (String[] message : MESSAGES) set(message[0], message[1]);
        settingsChanged.run();
    }

    void useGlobalForAll(boolean global)
    {
        for (String[] timer : TIMERS) set(timer[1], Boolean.toString(global));
        settingsChanged.run();
    }

    void enableAll(boolean enabled)
    {
        for (String key : ENABLED) set(key, Boolean.toString(enabled));
        settingsChanged.run();
    }

    private void set(String key, String value) { manager.setConfiguration(GROUP, key, value); }
}
