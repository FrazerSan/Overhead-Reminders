package com.shuddie.overhead;

import net.runelite.client.config.*;
import java.awt.*;

@ConfigGroup("overheadreminder")
public interface OverheadNotificationsConfig extends Config
{
	@Range(min = 1, max = 120)
	@ConfigItem(keyName = "overheadDisplayTime", name = "Overhead Display Time", description = "Time to display overhead text (in seconds)")
	default int overheadDisplayTime() { return 3; }
	// ====== SECTION: Boosts ======
	@ConfigSection(name = "Boosts", description = "Settings for boosts-related potions", position = 0)
	String boostsSection = "boostsSection";

	@ConfigItem(keyName = "enableOverload", name = "Enable Overload", description = "Enable overhead for Overload", section = boostsSection, position = 0)
	default boolean enableOverload() { return false; }
	@ConfigItem(keyName = "overloadColor", name = "Color", description = "Text color for Overload", section = boostsSection, position = 1)
	default Color overloadColor() { return Color.RED; }
	@ConfigItem(keyName = "overloadMessage", name = "Message", description = "Message for Overload", section = boostsSection, position = 2)
	default String overloadMessage() { return "Overload Expired!"; }

	@ConfigItem(keyName = "enableSmellingSalt", name = "Enable Smelling Salt", description = "Enable overhead for Smelling Salt", section = boostsSection, position = 3)
	default boolean enableSmellingSalt() { return false; }
	@ConfigItem(keyName = "smellingSaltColor", name = "Color", description = "Text color for Smelling Salt", section = boostsSection, position = 4)
	default Color smellingSaltColor() { return Color.ORANGE; }
	@ConfigItem(keyName = "smellingSaltMessage", name = "Message", description = "Message for Smelling Salt", section = boostsSection, position = 5)
	default String smellingSaltMessage() { return "Smelling Salt Expired!"; }

	@ConfigItem(keyName = "enableDivinePot", name = "Enable Divine Pot", description = "Enable overhead for any divine potion", section = boostsSection, position = 6)
	default boolean enableDivinePot() { return false; }
	@ConfigItem(keyName = "divinePotColor", name = "Color", description = "Text color for divine potion overhead text", section = boostsSection, position = 7)
	default Color divinePotColor() { return Color.GREEN; }
	@ConfigItem(keyName = "divinePotMessage", name = "Message", description = "Message to display for any divine potion", section = boostsSection, position = 8)
	default String divinePotMessage() { return "Divine Expired!"; }

    @ConfigItem(keyName = "enableSaturatedHeart", name = "Enable Saturated Heart", description = "Enable overhead for Saturated Heart", section = boostsSection, position = 9)
    default boolean enableSaturatedHeart() { return false; }
    @ConfigItem(keyName = "saturatedHeartColor", name = "Color", description = "Text color for Saturated Heart", section = boostsSection, position = 10)
    default Color saturatedHeartColor() { return Color.MAGENTA; }
    @ConfigItem(keyName = "saturatedHeartMessage", name = "Message", description = "Message for Saturated Heart", section = boostsSection, position = 11)
    default String saturatedHeartMessage() { return "Saturated Heart Expired!"; }

	// ====== SECTION: Prayer ======
	@ConfigSection(name = "Prayer", description = "Settings for prayer-related potions", position = 2)
	String prayerSection = "prayerSection";

	@ConfigItem(keyName = "enablePrayerRegen", name = "Enable Prayer Regen", description = "Enable overhead for Prayer Regeneration", section = prayerSection, position = 0)
	default boolean enablePrayerRegen() { return false; }
	@ConfigItem(keyName = "prayerRegenColor", name = "Color", description = "Text color for Prayer Regen", section = prayerSection, position = 1)
	default Color prayerRegenColor() { return Color.PINK; }
	@ConfigItem(keyName = "prayerRegenMessage", name = "Message", description = "Message for Prayer Regen", section = prayerSection, position = 2)
	default String prayerRegenMessage() { return "Prayer regeneration potion Expired!"; }

	@ConfigItem(keyName = "enablePrayerEnhance", name = "Enable Prayer Enhance", description = "Enable overhead for Prayer Enhance", section = prayerSection, position = 3)
	default boolean enablePrayerEnhance() { return false; }
	@ConfigItem(keyName = "prayerEnhanceColor", name = "Color", description = "Text color for Prayer Enhance", section = prayerSection, position = 4)
	default Color prayerEnhanceColor() { return Color.PINK; }
	@ConfigItem(keyName = "prayerEnhanceMessage", name = "Message", description = "Message for Prayer Enhance", section = prayerSection, position = 5)
	default String prayerEnhanceMessage() { return "Prayer enhance potion Expired!"; }
    // ====== SECTION: Magic ======
    @ConfigSection(name = "Magic", description = "Settings for magic buffs", position = 3)
    String magicSection = "magicSection";
    @ConfigItem(keyName = "enableThrall", name = "Enable Thrall", description = "Enable overhead for Thralls", section = magicSection, position = 0)
    default boolean enableThrall() { return false; }
    @ConfigItem(keyName = "thrallColor", name = "Color", description = "Text color for Thralls", section = magicSection, position = 1)
    default Color thrallColor() { return Color.CYAN; }
    @ConfigItem(keyName = "thrallMessage", name = "Message", description = "Message for Thralls", section = magicSection, position = 2)
    default String thrallMessage() { return "Thrall Expired!"; }

    @ConfigItem(keyName = "enableDeathCharge", name = "Enable Death Charge", description = "Enable overhead for Death Charge", section = magicSection, position = 3)
    default boolean enableDeathCharge() { return false; }
    @ConfigItem(keyName = "deathChargeColor", name = "Color", description = "Text color for Death Charge", section = magicSection, position = 4)
    default Color deathchargeColor() { return Color.RED; }
    @ConfigItem(keyName = "deathChargeMessage", name = "Message", description = "Message for Death Charge", section = magicSection, position = 5)
    default String deathchargeMessage() { return "Death Charge Ready!"; }


    @ConfigItem(keyName = "enableMarkOfDarkness", name = "Enable Mark of Darkness", description = "Enable overhead for Mark of Darkness", section = magicSection, position = 6)
    default boolean enableMarkOfDarkness() { return false; }

    @ConfigItem(keyName = "markOfDarknessColor", name = "Color", description = "Text color for Mark of Darkness", section = magicSection, position = 7)
    default Color markOfDarknessColor() { return Color.BLUE; }

    @ConfigItem(keyName = "markOfDarknessMessage", name = "Message", description = "Message for Mark of Darkness", section = magicSection, position = 8)
    default String markOfDarknessMessage() { return "Mark of Darkness expired!"; }

    @ConfigItem(keyName = "enableWardOfArceuus", name = "Enable Ward of Arceuus", description = "Enable overhead for Ward of Arceuus", section = magicSection, position = 9)
    default boolean enableWardOfArceuus() { return false; }

    @ConfigItem(keyName = "WardOfArceuusColor", name = "Color", description = "Text color for Ward of Arceuus", section = magicSection, position = 10)
    default Color WardOfArceuusColor() { return Color.BLUE; }

    @ConfigItem(keyName = "WardOfArceuusMessage", name = "Message", description = "Message for Ward of Arceuus", section = magicSection, position = 11)
    default String WardOfArceuusMessage() { return "Ward of Arceuus expired!"; }
	// ====== SECTION: Other ======
	@ConfigSection(name = "Other", description = "Other special cases", position = 4)
	String otherSection = "otherSection";

	@ConfigItem(keyName = "enableSoulflameHorn", name = "Enable Soulflame Horn", description = "Enable overhead for Soulflame Horn", section = otherSection, position = 0)
	default boolean enableSoulflameHorn() { return false; }
	@ConfigItem(keyName = "soulflameHornColor", name = "Color", description = "Text color for Soulflame Horn", section = otherSection, position = 1)
	default Color soulflameHornColor() { return Color.RED; }
	@ConfigItem(keyName = "soulflameHornMessage", name = "Message", description = "Message for Soulflame Horn", section = otherSection, position = 2)
	default String soulflameHornMessage() { return "Soulflame Horn Active!"; }

	@ConfigItem(keyName = "enableSurgePotion", name = "Enable Surge Potion", description = "Enable overhead for Surge Potion", section = otherSection, position = 3)
	default boolean enableSurgePotion() { return false; }
	@ConfigItem(keyName = "surgePotionColor", name = "Color", description = "Text color for Surge Potion", section = otherSection, position = 4)
	default Color surgePotionColor() { return Color.BLUE; }
	@ConfigItem(keyName = "surgePotionMessage", name = "Message", description = "Message for Surge Potion", section = otherSection, position = 5)
	default String surgePotionMessage() { return "Surge Potion Ready!"; }

	@ConfigItem(keyName = "enableStamina", name = "Enable Stamina", description = "Enable overhead for Stamina", section = otherSection, position = 6)
	default boolean enableStamina() { return false; }
	@ConfigItem(keyName = "staminaColor", name = "Color", description = "Text color for Stamina", section = otherSection, position = 7)
	default Color staminaColor() { return Color.YELLOW; }
	@ConfigItem(keyName = "staminaMessage", name = "Message", description = "Message for Stamina", section = otherSection, position = 8)
	default String staminaMessage() { return "Stamina potion Expired!"; }

    @ConfigItem(keyName = "enableAntiPoison", name = "Enable AntiPoison", description = "Enable overhead for AntiPoison", section = otherSection, position = 9)
    default boolean enableAntiPoison() { return false; }
    @ConfigItem(keyName = "antiPoisonColor", name = "Color", description = "Text color for AntiPoison", section = otherSection, position = 10)
    default Color antipoisonColor() { return Color.green; }
    @ConfigItem(keyName = "antiPoisonMessage", name = "Message", description = "Message for AntiPoison", section = otherSection, position = 11)
    default String antipoisonMessage() { return "AntiPoison Expired!"; }

    @ConfigItem(keyName = "enableVenom", name = "Enable Venom", description = "Enable overhead for Venom", section = otherSection, position = 12)
    default boolean enableVenom() { return false; }
    @ConfigItem(keyName = "venomColor", name = "Color", description = "Text color for Venom", section = otherSection, position = 13)
    default Color venomColor() { return Color.green; }
    @ConfigItem(keyName = "venomMessage", name = "Message", description = "Message for Venom", section = otherSection, position = 14)
    default String venomMessage() { return "Venom protection Expired!"; }

    @ConfigItem(keyName = "enableAntifire", name = "Enable Antifire", description = "Enable overhead for Antifire", section = otherSection, position = 15)
    default boolean enableAntifire() { return false; }
    @ConfigItem(keyName = "antifireColor", name = "Color", description = "Text color for Antifire", section = otherSection, position = 16)
    default Color antifireColor() { return Color.pink; }
    @ConfigItem(keyName = "antifireMessage", name = "Message", description = "Message for Antifire", section = otherSection, position = 17)
    default String antifireMessage() { return "Antifire protection Expired!"; }
    @ConfigItem(keyName = "showCountdown", name = "Show remaining seconds", description = "Append remaining display time to each message", position = -2)
    default boolean showCountdown() { return false; }

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "verticalOffset", name = "Height above player", description = "Extra height above your player in scene units", position = -1)
    default int verticalOffset() { return 40; }

    @ConfigSection(name = "Low Prayer", description = "Warn once at or below the threshold; restore above it to rearm", position = 1)
    String lowPrayerSection = "lowPrayerSection";

    @ConfigItem(keyName = "enableLowPrayer", name = "Enable Low Prayer", description = "Alert once on entering low prayer, including login with low prayer", section = lowPrayerSection, position = 0)
    default boolean enableLowPrayer() { return true; }

    @Range(min = 0, max = 99)
    @ConfigItem(keyName = "prayerThreshold", name = "Prayer threshold", description = "Warn when prayer is at or below this value", section = lowPrayerSection, position = 1)
    default int prayerThreshold() { return 20; }

    @ConfigItem(keyName = "lowPrayerMessage", name = "Message", description = "Low prayer text; {0} becomes Low Prayer", section = lowPrayerSection, position = 2)
    default String lowPrayerMessage() { return "LOW PRAYER!"; }

    @ConfigItem(keyName = "lowPrayerColor", name = "Color", description = "Low prayer text color", section = lowPrayerSection, position = 3)
    default Color lowPrayerColor() { return Color.RED; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "lowPrayerDuration", name = "Display duration (seconds)", description = "0 uses Overhead Display Time, like other reminders", section = lowPrayerSection, position = 4)
    default int lowPrayerDuration() { return 0; }
}
