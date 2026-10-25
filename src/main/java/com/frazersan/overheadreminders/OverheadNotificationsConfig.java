package com.frazersan.overheadreminders;

import net.runelite.client.config.*;
import java.awt.*;

@ConfigGroup("overheadreminder")
public interface OverheadNotificationsConfig extends Config
{
    String reminderDivider = "<html><hr width='190' size='1' color='#505050'></html>";

    @Range(min = 1, max = 120)
    @ConfigItem(keyName = "overheadDisplayTime", name = "Overhead Display Time", description = "Time to display overhead text (in seconds)", position = -3)
    default int overheadDisplayTime() { return 15; }

    @ConfigItem(keyName = "showCountdown", name = "Show remaining seconds", description = "Append remaining display time to each message", position = -2)
    default boolean showCountdown() { return false; }

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "verticalOffset", name = "Height above player", description = "Extra height above your player in scene units", position = -1)
    default int verticalOffset() { return 40; }

    @ConfigSection(name = "Prayer", description = "Prayer reminders", position = 0)
    String prayerSection = "prayerSection";

    @ConfigItem(keyName = "enableLowPrayer", name = "Enable Low Prayer", description = "Alert once on entering low prayer, including login with low prayer", section = prayerSection, position = 0)
    default boolean enableLowPrayer() { return false; }

    @ConfigItem(keyName = "lowPrayerMessage", name = "Low Prayer: Message", description = "Low prayer text; {0} becomes Low Prayer", section = prayerSection, position = 2)
    default String lowPrayerMessage() { return "Low Prayer"; }

    @Range(min = 0, max = 99)
    @ConfigItem(keyName = "prayerThreshold", name = "Threshold: Low Prayer", description = "Warn when prayer is at or below this value", section = prayerSection, position = 4)
    default int prayerThreshold() { return 20; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "lowPrayerDuration", name = "Display Time: Low Prayer", description = "Duration in seconds. 0 uses Overhead Display Time, like other reminders", section = prayerSection, position = 6)
    default int lowPrayerDuration() { return 15; }

    @ConfigItem(keyName = "lowPrayerColor", name = "Low Prayer: Colour", description = "Low prayer text color", section = prayerSection, position = 8)
    default Color lowPrayerColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenablePrayerRegen", name = reminderDivider, description = "", section = prayerSection, position = 10)
    void dividerBeforeenablePrayerRegen();

    @ConfigItem(keyName = "enablePrayerRegen", name = "Enable Prayer Regen", description = "Enable overhead for Prayer Regeneration", section = prayerSection, position = 11)
    default boolean enablePrayerRegen() { return false; }

    @ConfigItem(keyName = "prayerRegenMessage", name = "Prayer Regen: Message", description = "Message for Prayer Regen", section = prayerSection, position = 13)
    default String prayerRegenMessage() { return "Prayer regeneration potion Expired!"; }

    @ConfigItem(keyName = "prayerRegenColor", name = "Prayer Regen: Colour", description = "Text color for Prayer Regen", section = prayerSection, position = 15)
    default Color prayerRegenColor() { return Color.PINK; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenablePrayerEnhance", name = reminderDivider, description = "", section = prayerSection, position = 17)
    void dividerBeforeenablePrayerEnhance();

    @ConfigItem(keyName = "enablePrayerEnhance", name = "Enable Prayer Enhance", description = "Enable overhead for Prayer Enhance", section = prayerSection, position = 18)
    default boolean enablePrayerEnhance() { return false; }

    @ConfigItem(keyName = "prayerEnhanceMessage", name = "Prayer Enhance: Message", description = "Message for Prayer Enhance", section = prayerSection, position = 20)
    default String prayerEnhanceMessage() { return "Prayer enhance potion Expired!"; }

    @ConfigItem(keyName = "prayerEnhanceColor", name = "Prayer Enhance: Colour", description = "Text color for Prayer Enhance", section = prayerSection, position = 22)
    default Color prayerEnhanceColor() { return Color.PINK; }

    @ConfigSection(name = "Boosts", description = "Boosts reminders", position = 1)
    String boostsSection = "boostsSection";

    @ConfigItem(keyName = "enableDivinePot", name = "Enable Divine Pot", description = "Enable overhead for any divine potion", section = boostsSection, position = 0)
    default boolean enableDivinePot() { return false; }

    @ConfigItem(keyName = "divinePotMessage", name = "Divine: Message", description = "Message to display for any divine potion", section = boostsSection, position = 2)
    default String divinePotMessage() { return "Drink Divine"; }

    @ConfigItem(keyName = "divinePotColor", name = "Divine: Colour", description = "Text color for divine potion overhead text", section = boostsSection, position = 4)
    default Color divinePotColor() { return Color.GREEN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableSaturatedHeart", name = reminderDivider, description = "", section = boostsSection, position = 6)
    void dividerBeforeenableSaturatedHeart();

    @ConfigItem(keyName = "enableSaturatedHeart", name = "Enable Saturated Heart", description = "Enable overhead for Saturated Heart", section = boostsSection, position = 7)
    default boolean enableSaturatedHeart() { return false; }

    @ConfigItem(keyName = "saturatedHeartMessage", name = "Saturated Heart: Message", description = "Message for Saturated Heart", section = boostsSection, position = 9)
    default String saturatedHeartMessage() { return "Invigorate Saturated Heart"; }

    @ConfigItem(keyName = "saturatedHeartColor", name = "Saturated Heart: Colour", description = "Text color for Saturated Heart", section = boostsSection, position = 11)
    default Color saturatedHeartColor() { return Color.MAGENTA; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforemeleeBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 13)
    void dividerBeforemeleeBoostEnabled();

    @ConfigItem(keyName = "meleeBoostEnabled", name = "Enable Melee Boost", description = "Enable the Melee Boost threshold reminder", section = boostsSection, position = 14)
    default boolean meleeBoostEnabled() { return false; }

    @ConfigItem(keyName = "meleeBoostMessage", name = "Melee Boost: Message", description = "Reminder text for Melee Boost", section = boostsSection, position = 16)
    default String meleeBoostMessage() { return "Drink Melee Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "meleeBoostThreshold", name = "Threshold: Melee Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 18)
    default int meleeBoostThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "meleeBoostTimeout", name = "Display Time: Melee Boost", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = boostsSection, position = 20)
    default int meleeBoostTimeout() { return 15; }

    @ConfigItem(keyName = "meleeBoostColor", name = "Melee Boost: Colour", description = "Reminder colour for Melee Boost", section = boostsSection, position = 22)
    default Color meleeBoostColor() { return Color.ORANGE; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforerangedBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 24)
    void dividerBeforerangedBoostEnabled();

    @ConfigItem(keyName = "rangedBoostEnabled", name = "Enable Range Boost", description = "Enable the Ranged Boost threshold reminder", section = boostsSection, position = 25)
    default boolean rangedBoostEnabled() { return false; }

    @ConfigItem(keyName = "rangedBoostMessage", name = "Range Boost: Message", description = "Reminder text for Ranged Boost", section = boostsSection, position = 27)
    default String rangedBoostMessage() { return "Drink Range Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "rangedBoostThreshold", name = "Threshold: Range Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 29)
    default int rangedBoostThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "rangedBoostTimeout", name = "Display Time: Range Boost", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = boostsSection, position = 31)
    default int rangedBoostTimeout() { return 15; }

    @ConfigItem(keyName = "rangedBoostColor", name = "Range Boost: Colour", description = "Reminder colour for Ranged Boost", section = boostsSection, position = 33)
    default Color rangedBoostColor() { return Color.GREEN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforemagicBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 35)
    void dividerBeforemagicBoostEnabled();

    @ConfigItem(keyName = "magicBoostEnabled", name = "Enable Magic Boost", description = "Enable the Magic Boost threshold reminder", section = boostsSection, position = 36)
    default boolean magicBoostEnabled() { return false; }

    @ConfigItem(keyName = "magicBoostMessage", name = "Magic Boost: Message", description = "Reminder text for Magic Boost", section = boostsSection, position = 38)
    default String magicBoostMessage() { return "Drink Magic Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "magicBoostThreshold", name = "Threshold: Magic Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 40)
    default int magicBoostThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "magicBoostTimeout", name = "Display Time: Magic Boost", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = boostsSection, position = 42)
    default int magicBoostTimeout() { return 15; }

    @ConfigItem(keyName = "magicBoostColor", name = "Magic Boost: Colour", description = "Reminder colour for Magic Boost", section = boostsSection, position = 44)
    default Color magicBoostColor() { return Color.MAGENTA; }

    @ConfigSection(name = "Other Boosts", description = "Other Boosts reminders", position = 2)
    String otherBoostsSection = "otherBoostsSection";

    @ConfigItem(keyName = "enableSoulflameHorn", name = "Enable Soulflame Horn", description = "Enable overhead for Soulflame Horn", section = otherBoostsSection, position = 0)
    default boolean enableSoulflameHorn() { return false; }

    @ConfigItem(keyName = "soulflameHornMessage", name = "Soulflame Horn: Message", description = "Message for Soulflame Horn", section = otherBoostsSection, position = 2)
    default String soulflameHornMessage() { return "Blow Soulflame Horn"; }

    @ConfigItem(keyName = "soulflameHornColor", name = "Soulflame Horn: Colour", description = "Text color for Soulflame Horn", section = otherBoostsSection, position = 4)
    default Color soulflameHornColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforewildPieEnabled", name = reminderDivider, description = "", section = otherBoostsSection, position = 6)
    void dividerBeforewildPieEnabled();

    @ConfigItem(keyName = "wildPieEnabled", name = "Enable Wild Pie", description = "Enable the Wild Pie threshold reminder", section = otherBoostsSection, position = 7)
    default boolean wildPieEnabled() { return false; }

    @ConfigItem(keyName = "wildPieMessage", name = "Wild Pie: Message", description = "Reminder text for Wild Pie", section = otherBoostsSection, position = 9)
    default String wildPieMessage() { return "Slayer Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "wildPieThreshold", name = "Threshold: Wild Pie", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = otherBoostsSection, position = 11)
    default int wildPieThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "wildPieTimeout", name = "Display Time: Wild Pie", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = otherBoostsSection, position = 13)
    default int wildPieTimeout() { return 15; }

    @ConfigItem(keyName = "wildPieColor", name = "Wild Pie: Colour", description = "Reminder colour for Wild Pie", section = otherBoostsSection, position = 15)
    default Color wildPieColor() { return Color.YELLOW; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforesummerPieEnabled", name = reminderDivider, description = "", section = otherBoostsSection, position = 17)
    void dividerBeforesummerPieEnabled();

    @ConfigItem(keyName = "summerPieEnabled", name = "Enable Summer Pie", description = "Enable the Summer Pie threshold reminder", section = otherBoostsSection, position = 18)
    default boolean summerPieEnabled() { return false; }

    @ConfigItem(keyName = "summerPieMessage", name = "Summer Pie: Message", description = "Reminder text for Summer Pie", section = otherBoostsSection, position = 20)
    default String summerPieMessage() { return "Agility Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "summerPieThreshold", name = "Threshold: Summer Pie", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = otherBoostsSection, position = 22)
    default int summerPieThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "summerPieTimeout", name = "Display Time: Summer Pie", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = otherBoostsSection, position = 24)
    default int summerPieTimeout() { return 15; }

    @ConfigItem(keyName = "summerPieColor", name = "Summer Pie: Colour", description = "Reminder colour for Summer Pie", section = otherBoostsSection, position = 26)
    default Color summerPieColor() { return Color.CYAN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableStamina", name = reminderDivider, description = "", section = otherBoostsSection, position = 28)
    void dividerBeforeenableStamina();

    @ConfigItem(keyName = "enableStamina", name = "Enable Stamina", description = "Enable overhead for Stamina", section = otherBoostsSection, position = 29)
    default boolean enableStamina() { return false; }

    @ConfigItem(keyName = "staminaMessage", name = "Stamina: Message", description = "Message for Stamina", section = otherBoostsSection, position = 31)
    default String staminaMessage() { return "Drink Stamina"; }

    @ConfigItem(keyName = "staminaColor", name = "Stamina: Colour", description = "Text color for Stamina", section = otherBoostsSection, position = 33)
    default Color staminaColor() { return Color.YELLOW; }

    @ConfigSection(name = "Potions", description = "Potions reminders", position = 4)
    String potionsSection = "potionsSection";

    @ConfigItem(keyName = "enableSurgePotion", name = "Enable Surge Potion", description = "Enable overhead for Surge Potion", section = potionsSection, position = 0)
    default boolean enableSurgePotion() { return false; }

    @ConfigItem(keyName = "surgePotionMessage", name = "Surge: Message", description = "Message for Surge Potion", section = potionsSection, position = 2)
    default String surgePotionMessage() { return "Drink Surge"; }

    @ConfigItem(keyName = "surgePotionColor", name = "Surge: Colour", description = "Text color for Surge Potion", section = potionsSection, position = 4)
    default Color surgePotionColor() { return Color.BLUE; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableAntiPoison", name = reminderDivider, description = "", section = potionsSection, position = 6)
    void dividerBeforeenableAntiPoison();

    @ConfigItem(keyName = "enableAntiPoison", name = "Enable AntiPoison", description = "Enable overhead for AntiPoison", section = potionsSection, position = 7)
    default boolean enableAntiPoison() { return false; }

    @ConfigItem(keyName = "antiPoisonMessage", name = "Antipoison: Message", description = "Message for AntiPoison", section = potionsSection, position = 9)
    default String antipoisonMessage() { return "Drink Antipoison"; }

    @ConfigItem(keyName = "antiPoisonColor", name = "Antipoison: Colour", description = "Text color for AntiPoison", section = potionsSection, position = 11)
    default Color antipoisonColor() { return Color.green; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableVenom", name = reminderDivider, description = "", section = potionsSection, position = 13)
    void dividerBeforeenableVenom();

    @ConfigItem(keyName = "enableVenom", name = "Enable Venom", description = "Enable overhead for Venom", section = potionsSection, position = 14)
    default boolean enableVenom() { return false; }

    @ConfigItem(keyName = "venomMessage", name = "Venom: Message", description = "Message for Venom", section = potionsSection, position = 16)
    default String venomMessage() { return "Drink Anti-venom"; }

    @ConfigItem(keyName = "venomColor", name = "Venom: Colour", description = "Text color for Venom", section = potionsSection, position = 18)
    default Color venomColor() { return Color.green; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableAntifire", name = reminderDivider, description = "", section = potionsSection, position = 20)
    void dividerBeforeenableAntifire();

    @ConfigItem(keyName = "enableAntifire", name = "Enable Antifire", description = "Expiry reminder for antifire potions and mixes, extended antifire, and super antifire variants", section = potionsSection, position = 21)
    default boolean enableAntifire() { return false; }

    @ConfigItem(keyName = "antifireMessage", name = "Antifire: Message", description = "Message for Antifire", section = potionsSection, position = 23)
    default String antifireMessage() { return "Drink Antifire"; }

    @ConfigItem(keyName = "antifireColor", name = "Antifire: Colour", description = "Text color for Antifire", section = potionsSection, position = 25)
    default Color antifireColor() { return Color.pink; }

    @ConfigSection(name = "Raid Boosts", description = "Raid Boosts reminders", position = 5)
    String raidBoostsSection = "raidBoostsSection";

    @ConfigItem(keyName = "enableOverload", name = "Enable Overload", description = "Enable overhead for Overload", section = raidBoostsSection, position = 0)
    default boolean enableOverload() { return false; }

    @ConfigItem(keyName = "overloadMessage", name = "Overload: Message", description = "Message for Overload", section = raidBoostsSection, position = 2)
    default String overloadMessage() { return "Drink Overload"; }

    @ConfigItem(keyName = "overloadColor", name = "Overload: Colour", description = "Text color for Overload", section = raidBoostsSection, position = 4)
    default Color overloadColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableSmellingSalt", name = reminderDivider, description = "", section = raidBoostsSection, position = 6)
    void dividerBeforeenableSmellingSalt();

    @ConfigItem(keyName = "enableSmellingSalt", name = "Enable Smelling Salt", description = "Enable overhead for Smelling Salt", section = raidBoostsSection, position = 7)
    default boolean enableSmellingSalt() { return false; }

    @ConfigItem(keyName = "smellingSaltMessage", name = "Smelling Salt: Message", description = "Message for Smelling Salt", section = raidBoostsSection, position = 9)
    default String smellingSaltMessage() { return "Use Smelling Salt"; }

    @ConfigItem(keyName = "smellingSaltColor", name = "Smelling Salt: Colour", description = "Text color for Smelling Salt", section = raidBoostsSection, position = 11)
    default Color smellingSaltColor() { return Color.ORANGE; }

    @ConfigSection(name = "Magic", description = "Magic reminders", position = 3)
    String magicSection = "magicSection";

    @ConfigItem(keyName = "enableThrall", name = "Enable Thrall", description = "Enable overhead for Thralls", section = magicSection, position = 0)
    default boolean enableThrall() { return false; }

    @ConfigItem(keyName = "thrallMessage", name = "Thrall: Message", description = "Message for Thralls", section = magicSection, position = 2)
    default String thrallMessage() { return "Cast Thrall"; }

    @ConfigItem(keyName = "thrallColor", name = "Thrall: Colour", description = "Text color for Thralls", section = magicSection, position = 4)
    default Color thrallColor() { return Color.CYAN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableDeathCharge", name = reminderDivider, description = "", section = magicSection, position = 6)
    void dividerBeforeenableDeathCharge();

    @ConfigItem(keyName = "enableDeathCharge", name = "Enable Death Charge", description = "Enable overhead for Death Charge", section = magicSection, position = 7)
    default boolean enableDeathCharge() { return false; }

    @ConfigItem(keyName = "deathChargeMessage", name = "Death Charge: Message", description = "Message for Death Charge", section = magicSection, position = 9)
    default String deathchargeMessage() { return "Cast Death Charge"; }

    @ConfigItem(keyName = "deathChargeColor", name = "Death Charge: Colour", description = "Text color for Death Charge", section = magicSection, position = 11)
    default Color deathchargeColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableMarkOfDarkness", name = reminderDivider, description = "", section = magicSection, position = 13)
    void dividerBeforeenableMarkOfDarkness();

    @ConfigItem(keyName = "enableMarkOfDarkness", name = "Enable Mark of Darkness", description = "Enable overhead for Mark of Darkness", section = magicSection, position = 14)
    default boolean enableMarkOfDarkness() { return false; }

    @ConfigItem(keyName = "markOfDarknessMessage", name = "Mark of Darkness: Message", description = "Message for Mark of Darkness", section = magicSection, position = 16)
    default String markOfDarknessMessage() { return "Cast Mark of Darkness"; }

    @ConfigItem(keyName = "markOfDarknessColor", name = "Mark of Darkness: Colour", description = "Text color for Mark of Darkness", section = magicSection, position = 18)
    default Color markOfDarknessColor() { return Color.BLUE; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableWardOfArceuus", name = reminderDivider, description = "", section = magicSection, position = 20)
    void dividerBeforeenableWardOfArceuus();

    @ConfigItem(keyName = "enableWardOfArceuus", name = "Enable Ward of Arceuus", description = "Enable overhead for Ward of Arceuus", section = magicSection, position = 21)
    default boolean enableWardOfArceuus() { return false; }

    @ConfigItem(keyName = "WardOfArceuusMessage", name = "Ward of Arceuus: Message", description = "Message for Ward of Arceuus", section = magicSection, position = 23)
    default String WardOfArceuusMessage() { return "Cast Ward of Arceuus"; }

    @ConfigItem(keyName = "WardOfArceuusColor", name = "Ward of Arceuus: Colour", description = "Text color for Ward of Arceuus", section = magicSection, position = 25)
    default Color WardOfArceuusColor() { return Color.BLUE; }

}
