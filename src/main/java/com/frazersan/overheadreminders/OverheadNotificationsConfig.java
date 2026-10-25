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

    @ConfigSection(name = "Prayer", description = "Low prayer and prayer potion reminders", position = 0)
    String prayerSection = "prayerSection";

    @ConfigItem(keyName = "enableLowPrayer", name = "Enable Low Prayer", description = "Alert once on entering low prayer, including login with low prayer", section = prayerSection, position = 0)
    default boolean enableLowPrayer() { return true; }

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

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenablePrayerRegen", name = reminderDivider, description = "", section = prayerSection, position = 9)
    void dividerBeforeenablePrayerRegen();

    @ConfigItem(keyName = "enablePrayerRegen", name = "Enable Prayer Regen", description = "Enable overhead for Prayer Regeneration", section = prayerSection, position = 10)
    default boolean enablePrayerRegen() { return false; }

    @ConfigItem(keyName = "prayerRegenMessage", name = "Prayer Regen: Message", description = "Message for Prayer Regen", section = prayerSection, position = 12)
    default String prayerRegenMessage() { return "Prayer regeneration potion Expired!"; }

    @ConfigItem(keyName = "prayerRegenColor", name = "Prayer Regen: Colour", description = "Text color for Prayer Regen", section = prayerSection, position = 14)
    default Color prayerRegenColor() { return Color.PINK; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenablePrayerEnhance", name = reminderDivider, description = "", section = prayerSection, position = 15)
    void dividerBeforeenablePrayerEnhance();

    @ConfigItem(keyName = "enablePrayerEnhance", name = "Enable Prayer Enhance", description = "Enable overhead for Prayer Enhance", section = prayerSection, position = 16)
    default boolean enablePrayerEnhance() { return false; }

    @ConfigItem(keyName = "prayerEnhanceMessage", name = "Prayer Enhance: Message", description = "Message for Prayer Enhance", section = prayerSection, position = 18)
    default String prayerEnhanceMessage() { return "Prayer enhance potion Expired!"; }

    @ConfigItem(keyName = "prayerEnhanceColor", name = "Prayer Enhance: Colour", description = "Text color for Prayer Enhance", section = prayerSection, position = 20)
    default Color prayerEnhanceColor() { return Color.PINK; }

    @ConfigSection(name = "Boosts", description = "Potion, heart and combat boost reminders", position = 1)
    String boostsSection = "boostsSection";

    @ConfigItem(keyName = "enableDivinePot", name = "Enable Divine Pot", description = "Enable overhead for any divine potion", section = boostsSection, position = 0)
    default boolean enableDivinePot() { return false; }

    @ConfigItem(keyName = "divinePotMessage", name = "Divine: Message", description = "Message to display for any divine potion", section = boostsSection, position = 2)
    default String divinePotMessage() { return "Divine Expired!"; }

    @ConfigItem(keyName = "divinePotColor", name = "Divine: Colour", description = "Text color for divine potion overhead text", section = boostsSection, position = 4)
    default Color divinePotColor() { return Color.GREEN; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableSaturatedHeart", name = reminderDivider, description = "", section = boostsSection, position = 5)
    void dividerBeforeenableSaturatedHeart();

    @ConfigItem(keyName = "enableSaturatedHeart", name = "Enable Saturated Heart", description = "Enable overhead for Saturated Heart", section = boostsSection, position = 6)
    default boolean enableSaturatedHeart() { return false; }

    @ConfigItem(keyName = "saturatedHeartMessage", name = "Saturated Heart: Message", description = "Message for Saturated Heart", section = boostsSection, position = 8)
    default String saturatedHeartMessage() { return "Saturated Heart Expired!"; }

    @ConfigItem(keyName = "saturatedHeartColor", name = "Saturated Heart: Colour", description = "Text color for Saturated Heart", section = boostsSection, position = 10)
    default Color saturatedHeartColor() { return Color.MAGENTA; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforemeleeBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 11)
    void dividerBeforemeleeBoostEnabled();

    @ConfigItem(keyName = "meleeBoostEnabled", name = "Enable Melee Boost", description = "Enable the Melee Boost threshold reminder", section = boostsSection, position = 12)
    default boolean meleeBoostEnabled() { return false; }

    @ConfigItem(keyName = "meleeBoostMessage", name = "Melee Boost: Message", description = "Reminder text for Melee Boost", section = boostsSection, position = 14)
    default String meleeBoostMessage() { return "Melee Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "meleeBoostThreshold", name = "Threshold: Melee Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 16)
    default int meleeBoostThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "meleeBoostTimeout", name = "Display Time: Melee Boost", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = boostsSection, position = 18)
    default int meleeBoostTimeout() { return 15; }

    @ConfigItem(keyName = "meleeBoostColor", name = "Melee Boost: Colour", description = "Reminder colour for Melee Boost", section = boostsSection, position = 20)
    default Color meleeBoostColor() { return Color.ORANGE; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforemagicBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 21)
    void dividerBeforemagicBoostEnabled();

    @ConfigItem(keyName = "magicBoostEnabled", name = "Enable Magic Boost", description = "Enable the Magic Boost threshold reminder", section = boostsSection, position = 22)
    default boolean magicBoostEnabled() { return false; }

    @ConfigItem(keyName = "magicBoostMessage", name = "Magic Boost: Message", description = "Reminder text for Magic Boost", section = boostsSection, position = 24)
    default String magicBoostMessage() { return "Magic Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "magicBoostThreshold", name = "Threshold: Magic Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 26)
    default int magicBoostThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "magicBoostTimeout", name = "Display Time: Magic Boost", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = boostsSection, position = 28)
    default int magicBoostTimeout() { return 15; }

    @ConfigItem(keyName = "magicBoostColor", name = "Magic Boost: Colour", description = "Reminder colour for Magic Boost", section = boostsSection, position = 30)
    default Color magicBoostColor() { return Color.MAGENTA; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforerangedBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 31)
    void dividerBeforerangedBoostEnabled();

    @ConfigItem(keyName = "rangedBoostEnabled", name = "Enable Range Boost", description = "Enable the Ranged Boost threshold reminder", section = boostsSection, position = 32)
    default boolean rangedBoostEnabled() { return false; }

    @ConfigItem(keyName = "rangedBoostMessage", name = "Range Boost: Message", description = "Reminder text for Ranged Boost", section = boostsSection, position = 34)
    default String rangedBoostMessage() { return "Range Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "rangedBoostThreshold", name = "Threshold: Range Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 36)
    default int rangedBoostThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "rangedBoostTimeout", name = "Display Time: Range Boost", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = boostsSection, position = 38)
    default int rangedBoostTimeout() { return 15; }

    @ConfigItem(keyName = "rangedBoostColor", name = "Range Boost: Colour", description = "Reminder colour for Ranged Boost", section = boostsSection, position = 40)
    default Color rangedBoostColor() { return Color.GREEN; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableOverload", name = reminderDivider, description = "", section = boostsSection, position = 41)
    void dividerBeforeenableOverload();

    @ConfigItem(keyName = "enableOverload", name = "Enable Overload", description = "Enable overhead for Overload", section = boostsSection, position = 42)
    default boolean enableOverload() { return false; }

    @ConfigItem(keyName = "overloadMessage", name = "Overload: Message", description = "Message for Overload", section = boostsSection, position = 44)
    default String overloadMessage() { return "Overload Expired!"; }

    @ConfigItem(keyName = "overloadColor", name = "Overload: Colour", description = "Text color for Overload", section = boostsSection, position = 46)
    default Color overloadColor() { return Color.RED; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableSmellingSalt", name = reminderDivider, description = "", section = boostsSection, position = 47)
    void dividerBeforeenableSmellingSalt();

    @ConfigItem(keyName = "enableSmellingSalt", name = "Enable Smelling Salt", description = "Enable overhead for Smelling Salt", section = boostsSection, position = 48)
    default boolean enableSmellingSalt() { return false; }

    @ConfigItem(keyName = "smellingSaltMessage", name = "Smelling Salt: Message", description = "Message for Smelling Salt", section = boostsSection, position = 50)
    default String smellingSaltMessage() { return "Smelling Salt Expired!"; }

    @ConfigItem(keyName = "smellingSaltColor", name = "Smelling Salt: Colour", description = "Text color for Smelling Salt", section = boostsSection, position = 52)
    default Color smellingSaltColor() { return Color.ORANGE; }

    @ConfigSection(name = "Magic", description = "Spell reminders", position = 2)
    String magicSection = "magicSection";

    @ConfigItem(keyName = "enableThrall", name = "Enable Thrall", description = "Enable overhead for Thralls", section = magicSection, position = 0)
    default boolean enableThrall() { return false; }

    @ConfigItem(keyName = "thrallMessage", name = "Thrall: Message", description = "Message for Thralls", section = magicSection, position = 2)
    default String thrallMessage() { return "Thrall Expired!"; }

    @ConfigItem(keyName = "thrallColor", name = "Thrall: Colour", description = "Text color for Thralls", section = magicSection, position = 4)
    default Color thrallColor() { return Color.CYAN; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableDeathCharge", name = reminderDivider, description = "", section = magicSection, position = 5)
    void dividerBeforeenableDeathCharge();

    @ConfigItem(keyName = "enableDeathCharge", name = "Enable Death Charge", description = "Enable overhead for Death Charge", section = magicSection, position = 6)
    default boolean enableDeathCharge() { return false; }

    @ConfigItem(keyName = "deathChargeMessage", name = "Death Charge: Message", description = "Message for Death Charge", section = magicSection, position = 8)
    default String deathchargeMessage() { return "Death Charge Ready!"; }

    @ConfigItem(keyName = "deathChargeColor", name = "Death Charge: Colour", description = "Text color for Death Charge", section = magicSection, position = 10)
    default Color deathchargeColor() { return Color.RED; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableMarkOfDarkness", name = reminderDivider, description = "", section = magicSection, position = 11)
    void dividerBeforeenableMarkOfDarkness();

    @ConfigItem(keyName = "enableMarkOfDarkness", name = "Enable Mark of Darkness", description = "Enable overhead for Mark of Darkness", section = magicSection, position = 12)
    default boolean enableMarkOfDarkness() { return false; }

    @ConfigItem(keyName = "markOfDarknessMessage", name = "Mark of Darkness: Message", description = "Message for Mark of Darkness", section = magicSection, position = 14)
    default String markOfDarknessMessage() { return "Mark of Darkness expired!"; }

    @ConfigItem(keyName = "markOfDarknessColor", name = "Mark of Darkness: Colour", description = "Text color for Mark of Darkness", section = magicSection, position = 16)
    default Color markOfDarknessColor() { return Color.BLUE; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableWardOfArceuus", name = reminderDivider, description = "", section = magicSection, position = 17)
    void dividerBeforeenableWardOfArceuus();

    @ConfigItem(keyName = "enableWardOfArceuus", name = "Enable Ward of Arceuus", description = "Enable overhead for Ward of Arceuus", section = magicSection, position = 18)
    default boolean enableWardOfArceuus() { return false; }

    @ConfigItem(keyName = "WardOfArceuusMessage", name = "Ward of Arceuus: Message", description = "Message for Ward of Arceuus", section = magicSection, position = 20)
    default String WardOfArceuusMessage() { return "Ward of Arceuus expired!"; }

    @ConfigItem(keyName = "WardOfArceuusColor", name = "Ward of Arceuus: Colour", description = "Text color for Ward of Arceuus", section = magicSection, position = 22)
    default Color WardOfArceuusColor() { return Color.BLUE; }

    @ConfigSection(name = "Other", description = "Pies and other reminders", position = 3)
    String otherSection = "otherSection";

    @ConfigItem(keyName = "wildPieEnabled", name = "Enable Wild Pie", description = "Enable the Wild Pie threshold reminder", section = otherSection, position = 0)
    default boolean wildPieEnabled() { return false; }

    @ConfigItem(keyName = "wildPieMessage", name = "Wild Pie: Message", description = "Reminder text for Wild Pie", section = otherSection, position = 2)
    default String wildPieMessage() { return "Slayer Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "wildPieThreshold", name = "Threshold: Wild Pie", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = otherSection, position = 4)
    default int wildPieThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "wildPieTimeout", name = "Display Time: Wild Pie", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = otherSection, position = 6)
    default int wildPieTimeout() { return 15; }

    @ConfigItem(keyName = "wildPieColor", name = "Wild Pie: Colour", description = "Reminder colour for Wild Pie", section = otherSection, position = 8)
    default Color wildPieColor() { return Color.YELLOW; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforesummerPieEnabled", name = reminderDivider, description = "", section = otherSection, position = 9)
    void dividerBeforesummerPieEnabled();

    @ConfigItem(keyName = "summerPieEnabled", name = "Enable Summer Pie", description = "Enable the Summer Pie threshold reminder", section = otherSection, position = 10)
    default boolean summerPieEnabled() { return false; }

    @ConfigItem(keyName = "summerPieMessage", name = "Summer Pie: Message", description = "Reminder text for Summer Pie", section = otherSection, position = 12)
    default String summerPieMessage() { return "Agility Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "summerPieThreshold", name = "Threshold: Summer Pie", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = otherSection, position = 14)
    default int summerPieThreshold() { return 0; }

    @Range(min = 0, max = 120)
    @ConfigItem(keyName = "summerPieTimeout", name = "Display Time: Summer Pie", description = "Duration in seconds. 0 uses Overhead Display Time. Does not repeat until boosted again", section = otherSection, position = 16)
    default int summerPieTimeout() { return 15; }

    @ConfigItem(keyName = "summerPieColor", name = "Summer Pie: Colour", description = "Reminder colour for Summer Pie", section = otherSection, position = 18)
    default Color summerPieColor() { return Color.CYAN; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableSoulflameHorn", name = reminderDivider, description = "", section = otherSection, position = 19)
    void dividerBeforeenableSoulflameHorn();

    @ConfigItem(keyName = "enableSoulflameHorn", name = "Enable Soulflame Horn", description = "Enable overhead for Soulflame Horn", section = otherSection, position = 20)
    default boolean enableSoulflameHorn() { return false; }

    @ConfigItem(keyName = "soulflameHornMessage", name = "Soulflame Horn: Message", description = "Message for Soulflame Horn", section = otherSection, position = 22)
    default String soulflameHornMessage() { return "Soulflame Horn Active!"; }

    @ConfigItem(keyName = "soulflameHornColor", name = "Soulflame Horn: Colour", description = "Text color for Soulflame Horn", section = otherSection, position = 24)
    default Color soulflameHornColor() { return Color.RED; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableSurgePotion", name = reminderDivider, description = "", section = otherSection, position = 25)
    void dividerBeforeenableSurgePotion();

    @ConfigItem(keyName = "enableSurgePotion", name = "Enable Surge Potion", description = "Enable overhead for Surge Potion", section = otherSection, position = 26)
    default boolean enableSurgePotion() { return false; }

    @ConfigItem(keyName = "surgePotionMessage", name = "Surge: Message", description = "Message for Surge Potion", section = otherSection, position = 28)
    default String surgePotionMessage() { return "Surge Potion Ready!"; }

    @ConfigItem(keyName = "surgePotionColor", name = "Surge: Colour", description = "Text color for Surge Potion", section = otherSection, position = 30)
    default Color surgePotionColor() { return Color.BLUE; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableStamina", name = reminderDivider, description = "", section = otherSection, position = 31)
    void dividerBeforeenableStamina();

    @ConfigItem(keyName = "enableStamina", name = "Enable Stamina", description = "Enable overhead for Stamina", section = otherSection, position = 32)
    default boolean enableStamina() { return false; }

    @ConfigItem(keyName = "staminaMessage", name = "Stamina: Message", description = "Message for Stamina", section = otherSection, position = 34)
    default String staminaMessage() { return "Stamina potion Expired!"; }

    @ConfigItem(keyName = "staminaColor", name = "Stamina: Colour", description = "Text color for Stamina", section = otherSection, position = 36)
    default Color staminaColor() { return Color.YELLOW; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableAntiPoison", name = reminderDivider, description = "", section = otherSection, position = 37)
    void dividerBeforeenableAntiPoison();

    @ConfigItem(keyName = "enableAntiPoison", name = "Enable AntiPoison", description = "Enable overhead for AntiPoison", section = otherSection, position = 38)
    default boolean enableAntiPoison() { return false; }

    @ConfigItem(keyName = "antiPoisonMessage", name = "Antipoison: Message", description = "Message for AntiPoison", section = otherSection, position = 40)
    default String antipoisonMessage() { return "AntiPoison Expired!"; }

    @ConfigItem(keyName = "antiPoisonColor", name = "Antipoison: Colour", description = "Text color for AntiPoison", section = otherSection, position = 42)
    default Color antipoisonColor() { return Color.green; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableVenom", name = reminderDivider, description = "", section = otherSection, position = 43)
    void dividerBeforeenableVenom();

    @ConfigItem(keyName = "enableVenom", name = "Enable Venom", description = "Enable overhead for Venom", section = otherSection, position = 44)
    default boolean enableVenom() { return false; }

    @ConfigItem(keyName = "venomMessage", name = "Venom: Message", description = "Message for Venom", section = otherSection, position = 46)
    default String venomMessage() { return "Venom protection Expired!"; }

    @ConfigItem(keyName = "venomColor", name = "Venom: Colour", description = "Text color for Venom", section = otherSection, position = 48)
    default Color venomColor() { return Color.green; }

    // A void config item is a read-only label: no checkbox, value or stored default.
    @ConfigItem(keyName = "dividerBeforeenableAntifire", name = reminderDivider, description = "", section = otherSection, position = 49)
    void dividerBeforeenableAntifire();

    @ConfigItem(keyName = "enableAntifire", name = "Enable Antifire", description = "Enable overhead for Antifire", section = otherSection, position = 50)
    default boolean enableAntifire() { return false; }

    @ConfigItem(keyName = "antifireMessage", name = "Antifire: Message", description = "Message for Antifire", section = otherSection, position = 52)
    default String antifireMessage() { return "Antifire protection Expired!"; }

    @ConfigItem(keyName = "antifireColor", name = "Antifire: Colour", description = "Text color for Antifire", section = otherSection, position = 54)
    default Color antifireColor() { return Color.pink; }

}
