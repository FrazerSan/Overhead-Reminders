package com.frazersan.overheadreminders;

import net.runelite.client.config.*;
import java.awt.*;

@ConfigGroup("overheadreminder")
public interface OverheadNotificationsConfig extends Config
{
    String reminderDivider = "<html><hr width='190' size='1' color='#505050'></html>";

    @ConfigItem(keyName = "quickActionHeading", name = "Quick actions", description = "", position = -10)
    void quickActionHeading();

    @ConfigItem(keyName = "quickAction", name = "", description = "Preview messages, enable or disable all reminders, reset appearance/timers/messages, or switch all timer toggles. Runs once, then returns to Select an action", position = -9)
    default ReminderQuickAction quickAction() { return ReminderQuickAction.NONE; }

    @ConfigItem(keyName = "showSidebarIcon", name = "Show sidebar icon", description = "Show the optional tools sidebar. All tools are also available in Quick actions", position = -8)
    default boolean showSidebarIcon() { return false; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "overheadDisplayTime", name = "Overhead Display Time", description = "Time to display overhead text, in seconds (0-300). 0 stays until cleared", position = -7)
    default int overheadDisplayTime() { return 15; }

    @ConfigItem(keyName = "showCountdown", name = "Show remaining seconds", description = "Append remaining display time to each message", position = -6)
    default boolean showCountdown() { return false; }

    @Range(min = 0, max = 200)
    @ConfigItem(keyName = "verticalOffset", name = "Height above player", description = "Extra height above your player in scene units", position = -5)
    default int verticalOffset() { return 40; }

    @Range(min = -300, max = 300)
    @ConfigItem(keyName = "horizontalOffset", name = "Horizontal offset", description = "Move messages left or right in pixels; 0 centres them above your player", position = -4)
    default int horizontalOffset() { return 0; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "messageSpacing", name = "Message spacing", description = "Extra pixels between stacked messages; default 2", position = -3)
    default int messageSpacing() { return 2; }

    @Range(min = 0, max = 32)
    @ConfigItem(keyName = "textSize", name = "Text size", description = "Font size in pixels. 0 uses the original RuneScape bold font", position = -2)
    default int textSize() { return 0; }

    @ConfigItem(keyName = "showOutOfSupplies", name = "Show out-of-supplies", description = "Check inventory and show Out of followed by the item name when supplies are missing. Turn off to keep normal reminder text", position = -1)
    default boolean showOutOfSupplies() { return true; }

    @ConfigSection(name = "Prayer", description = "Prayer reminders", position = 0, closedByDefault = true)
    String prayerSection = "prayerSection";

    @ConfigItem(keyName = "enableLowPrayer", name = "Enable Low Prayer", description = "Alert once on entering low prayer, including login with low prayer", section = prayerSection, position = 0)
    default boolean enableLowPrayer() { return false; }

    @ConfigItem(keyName = "lowPrayerMessage", name = "Low Prayer: Message", description = "Low prayer text; {0} becomes Low Prayer", section = prayerSection, position = 1)
    default String lowPrayerMessage() { return "Low Prayer"; }

    @Range(min = 0, max = 99)
    @ConfigItem(keyName = "prayerThreshold", name = "Threshold: Low Prayer", description = "Warn when prayer is at or below this value", section = prayerSection, position = 2)
    default int prayerThreshold() { return 20; }

    @ConfigItem(keyName = "lowPrayerUseGlobalTimer", name = "Use global timer: Low Prayer", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = prayerSection, position = 3)
    default boolean lowPrayerUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "lowPrayerDuration", name = "Display Time: Low Prayer", description = "Duration in seconds (0-300). 0 stays until cleared", section = prayerSection, position = 4)
    default int lowPrayerDuration() { return 15; }

    @ConfigItem(keyName = "lowPrayerColor", name = "Low Prayer: Colour", description = "Low prayer text color", section = prayerSection, position = 5)
    default Color lowPrayerColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenablePrayerRegen", name = reminderDivider, description = "", section = prayerSection, position = 6)
    void dividerBeforeenablePrayerRegen();

    @ConfigItem(keyName = "enablePrayerRegen", name = "Enable Prayer Regen", description = "Enable overhead for Prayer Regeneration", section = prayerSection, position = 7)
    default boolean enablePrayerRegen() { return false; }

    @ConfigItem(keyName = "prayerRegenMessage", name = "Prayer Regen: Message", description = "Message for Prayer Regen", section = prayerSection, position = 8)
    default String prayerRegenMessage() { return "Prayer regeneration potion Expired!"; }

    @ConfigItem(keyName = "prayerRegenUseGlobalTimer", name = "Use global timer: Prayer Regen", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = prayerSection, position = 9)
    default boolean prayerRegenUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "prayerRegenDuration", name = "Display Time: Prayer Regen", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = prayerSection, position = 10)
    default int prayerRegenDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "prayerRegenEarlyWarning", name = "Warn before expiry: Prayer Regen", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = prayerSection, position = 11)
    default int prayerRegenEarlyWarning() { return 0; }

    @ConfigItem(keyName = "prayerRegenColor", name = "Prayer Regen: Colour", description = "Text color for Prayer Regen", section = prayerSection, position = 12)
    default Color prayerRegenColor() { return Color.PINK; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenablePrayerEnhance", name = reminderDivider, description = "", section = prayerSection, position = 13)
    void dividerBeforeenablePrayerEnhance();

    @ConfigItem(keyName = "enablePrayerEnhance", name = "Enable Prayer Enhance", description = "Enable overhead for Prayer Enhance", section = prayerSection, position = 14)
    default boolean enablePrayerEnhance() { return false; }

    @ConfigItem(keyName = "prayerEnhanceMessage", name = "Prayer Enhance: Message", description = "Message for Prayer Enhance", section = prayerSection, position = 15)
    default String prayerEnhanceMessage() { return "Prayer enhance potion Expired!"; }

    @ConfigItem(keyName = "prayerEnhanceUseGlobalTimer", name = "Use global timer: Prayer Enhance", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = prayerSection, position = 16)
    default boolean prayerEnhanceUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "prayerEnhanceDuration", name = "Display Time: Prayer Enhance", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = prayerSection, position = 17)
    default int prayerEnhanceDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "prayerEnhanceEarlyWarning", name = "Warn before expiry: Prayer Enhance", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = prayerSection, position = 18)
    default int prayerEnhanceEarlyWarning() { return 0; }

    @ConfigItem(keyName = "prayerEnhanceColor", name = "Prayer Enhance: Colour", description = "Text color for Prayer Enhance", section = prayerSection, position = 19)
    default Color prayerEnhanceColor() { return Color.PINK; }

    @ConfigSection(name = "Boosts", description = "Boosts reminders", position = 1, closedByDefault = true)
    String boostsSection = "boostsSection";

    @ConfigItem(keyName = "enableDivinePot", name = "Enable Divine Pot", description = "Enable overhead for any divine potion", section = boostsSection, position = 0)
    default boolean enableDivinePot() { return false; }

    @ConfigItem(keyName = "divinePotMessage", name = "Divine: Message", description = "Message to display for any divine potion", section = boostsSection, position = 1)
    default String divinePotMessage() { return "Drink Divine"; }

    @ConfigItem(keyName = "divinePotUseGlobalTimer", name = "Use global timer: Divine", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = boostsSection, position = 2)
    default boolean divinePotUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "divinePotDuration", name = "Display Time: Divine", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = boostsSection, position = 3)
    default int divinePotDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "divinePotEarlyWarning", name = "Warn before expiry: Divine", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = boostsSection, position = 4)
    default int divinePotEarlyWarning() { return 0; }

    @ConfigItem(keyName = "divinePotColor", name = "Divine: Colour", description = "Text color for divine potion overhead text", section = boostsSection, position = 5)
    default Color divinePotColor() { return Color.GREEN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableSaturatedHeart", name = reminderDivider, description = "", section = boostsSection, position = 6)
    void dividerBeforeenableSaturatedHeart();

    @ConfigItem(keyName = "enableSaturatedHeart", name = "Enable Saturated Heart", description = "Enable overhead for Saturated Heart", section = boostsSection, position = 7)
    default boolean enableSaturatedHeart() { return false; }

    @ConfigItem(keyName = "saturatedHeartMessage", name = "Saturated Heart: Message", description = "Message for Saturated Heart", section = boostsSection, position = 8)
    default String saturatedHeartMessage() { return "Invigorate Saturated Heart"; }

    @ConfigItem(keyName = "saturatedHeartUseGlobalTimer", name = "Use global timer: Saturated Heart", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = boostsSection, position = 9)
    default boolean saturatedHeartUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "saturatedHeartDuration", name = "Display Time: Saturated Heart", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = boostsSection, position = 10)
    default int saturatedHeartDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "saturatedHeartEarlyWarning", name = "Warn before expiry: Saturated Heart", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = boostsSection, position = 11)
    default int saturatedHeartEarlyWarning() { return 0; }

    @ConfigItem(keyName = "saturatedHeartColor", name = "Saturated Heart: Colour", description = "Text color for Saturated Heart", section = boostsSection, position = 12)
    default Color saturatedHeartColor() { return Color.MAGENTA; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforemeleeBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 13)
    void dividerBeforemeleeBoostEnabled();

    @ConfigItem(keyName = "meleeBoostEnabled", name = "Enable Melee Boost", description = "Enable the Melee Boost threshold reminder", section = boostsSection, position = 14)
    default boolean meleeBoostEnabled() { return false; }

    @ConfigItem(keyName = "meleeBoostMessage", name = "Melee Boost: Message", description = "Reminder text for Melee Boost", section = boostsSection, position = 15)
    default String meleeBoostMessage() { return "Drink Melee Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "meleeBoostThreshold", name = "Threshold: Melee Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 16)
    default int meleeBoostThreshold() { return 0; }

    @ConfigItem(keyName = "meleeBoostUseGlobalTimer", name = "Use global timer: Melee Boost", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = boostsSection, position = 17)
    default boolean meleeBoostUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "meleeBoostTimeout", name = "Display Time: Melee Boost", description = "Duration in seconds (0-300). 0 stays until cleared. Does not repeat until boosted again", section = boostsSection, position = 18)
    default int meleeBoostTimeout() { return 15; }

    @ConfigItem(keyName = "meleeBoostColor", name = "Melee Boost: Colour", description = "Reminder colour for Melee Boost", section = boostsSection, position = 19)
    default Color meleeBoostColor() { return Color.ORANGE; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforerangedBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 20)
    void dividerBeforerangedBoostEnabled();

    @ConfigItem(keyName = "rangedBoostEnabled", name = "Enable Range Boost", description = "Enable the Ranged Boost threshold reminder", section = boostsSection, position = 21)
    default boolean rangedBoostEnabled() { return false; }

    @ConfigItem(keyName = "rangedBoostMessage", name = "Range Boost: Message", description = "Reminder text for Ranged Boost", section = boostsSection, position = 22)
    default String rangedBoostMessage() { return "Drink Range Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "rangedBoostThreshold", name = "Threshold: Range Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 23)
    default int rangedBoostThreshold() { return 0; }

    @ConfigItem(keyName = "rangedBoostUseGlobalTimer", name = "Use global timer: Range Boost", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = boostsSection, position = 24)
    default boolean rangedBoostUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "rangedBoostTimeout", name = "Display Time: Range Boost", description = "Duration in seconds (0-300). 0 stays until cleared. Does not repeat until boosted again", section = boostsSection, position = 25)
    default int rangedBoostTimeout() { return 15; }

    @ConfigItem(keyName = "rangedBoostColor", name = "Range Boost: Colour", description = "Reminder colour for Ranged Boost", section = boostsSection, position = 26)
    default Color rangedBoostColor() { return Color.GREEN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforemagicBoostEnabled", name = reminderDivider, description = "", section = boostsSection, position = 27)
    void dividerBeforemagicBoostEnabled();

    @ConfigItem(keyName = "magicBoostEnabled", name = "Enable Magic Boost", description = "Enable the Magic Boost threshold reminder", section = boostsSection, position = 28)
    default boolean magicBoostEnabled() { return false; }

    @ConfigItem(keyName = "magicBoostMessage", name = "Magic Boost: Message", description = "Reminder text for Magic Boost", section = boostsSection, position = 29)
    default String magicBoostMessage() { return "Drink Magic Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "magicBoostThreshold", name = "Threshold: Magic Boost", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = boostsSection, position = 30)
    default int magicBoostThreshold() { return 0; }

    @ConfigItem(keyName = "magicBoostUseGlobalTimer", name = "Use global timer: Magic Boost", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = boostsSection, position = 31)
    default boolean magicBoostUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "magicBoostTimeout", name = "Display Time: Magic Boost", description = "Duration in seconds (0-300). 0 stays until cleared. Does not repeat until boosted again", section = boostsSection, position = 32)
    default int magicBoostTimeout() { return 15; }

    @ConfigItem(keyName = "magicBoostColor", name = "Magic Boost: Colour", description = "Reminder colour for Magic Boost", section = boostsSection, position = 33)
    default Color magicBoostColor() { return Color.MAGENTA; }

    @ConfigSection(name = "Other Boosts", description = "Other Boosts reminders", position = 2, closedByDefault = true)
    String otherBoostsSection = "otherBoostsSection";

    @ConfigItem(keyName = "enableSoulflameHorn", name = "Enable Soulflame Horn", description = "Enable overhead for Soulflame Horn", section = otherBoostsSection, position = 0)
    default boolean enableSoulflameHorn() { return false; }

    @ConfigItem(keyName = "soulflameHornMessage", name = "Soulflame Horn: Message", description = "Message for Soulflame Horn", section = otherBoostsSection, position = 1)
    default String soulflameHornMessage() { return "Soulflame Horn Active"; }

    @ConfigItem(keyName = "soulflameHornUseGlobalTimer", name = "Use global timer: Soulflame Horn", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = otherBoostsSection, position = 2)
    default boolean soulflameHornUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "soulflameHornDuration", name = "Display Time: Soulflame Horn", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = otherBoostsSection, position = 3)
    default int soulflameHornDuration() { return 15; }

    @ConfigItem(keyName = "soulflameHornColor", name = "Soulflame Horn: Colour", description = "Text color for Soulflame Horn", section = otherBoostsSection, position = 4)
    default Color soulflameHornColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforewildPieEnabled", name = reminderDivider, description = "", section = otherBoostsSection, position = 5)
    void dividerBeforewildPieEnabled();

    @ConfigItem(keyName = "wildPieEnabled", name = "Enable Wild Pie", description = "Enable the Wild Pie threshold reminder", section = otherBoostsSection, position = 6)
    default boolean wildPieEnabled() { return false; }

    @ConfigItem(keyName = "wildPieMessage", name = "Wild Pie: Message", description = "Reminder text for Wild Pie", section = otherBoostsSection, position = 7)
    default String wildPieMessage() { return "Slayer Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "wildPieThreshold", name = "Threshold: Wild Pie", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = otherBoostsSection, position = 8)
    default int wildPieThreshold() { return 0; }

    @ConfigItem(keyName = "wildPieUseGlobalTimer", name = "Use global timer: Wild Pie", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = otherBoostsSection, position = 9)
    default boolean wildPieUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "wildPieTimeout", name = "Display Time: Wild Pie", description = "Duration in seconds (0-300). 0 stays until cleared. Does not repeat until boosted again", section = otherBoostsSection, position = 10)
    default int wildPieTimeout() { return 15; }

    @ConfigItem(keyName = "wildPieColor", name = "Wild Pie: Colour", description = "Reminder colour for Wild Pie", section = otherBoostsSection, position = 11)
    default Color wildPieColor() { return Color.YELLOW; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforesummerPieEnabled", name = reminderDivider, description = "", section = otherBoostsSection, position = 12)
    void dividerBeforesummerPieEnabled();

    @ConfigItem(keyName = "summerPieEnabled", name = "Enable Summer Pie", description = "Enable the Summer Pie threshold reminder", section = otherBoostsSection, position = 13)
    default boolean summerPieEnabled() { return false; }

    @ConfigItem(keyName = "summerPieMessage", name = "Summer Pie: Message", description = "Reminder text for Summer Pie", section = otherBoostsSection, position = 14)
    default String summerPieMessage() { return "Agility Boost"; }

    @Range(min = 0, max = 30)
    @ConfigItem(keyName = "summerPieThreshold", name = "Threshold: Summer Pie", description = "Warn at this many levels above your normal level or lower; 0 means no boost remains", section = otherBoostsSection, position = 15)
    default int summerPieThreshold() { return 0; }

    @ConfigItem(keyName = "summerPieUseGlobalTimer", name = "Use global timer: Summer Pie", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = otherBoostsSection, position = 16)
    default boolean summerPieUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "summerPieTimeout", name = "Display Time: Summer Pie", description = "Duration in seconds (0-300). 0 stays until cleared. Does not repeat until boosted again", section = otherBoostsSection, position = 17)
    default int summerPieTimeout() { return 15; }

    @ConfigItem(keyName = "summerPieColor", name = "Summer Pie: Colour", description = "Reminder colour for Summer Pie", section = otherBoostsSection, position = 18)
    default Color summerPieColor() { return Color.CYAN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableStamina", name = reminderDivider, description = "", section = otherBoostsSection, position = 19)
    void dividerBeforeenableStamina();

    @ConfigItem(keyName = "enableStamina", name = "Enable Stamina", description = "Enable overhead for Stamina", section = otherBoostsSection, position = 20)
    default boolean enableStamina() { return false; }

    @ConfigItem(keyName = "staminaMessage", name = "Stamina: Message", description = "Message for Stamina", section = otherBoostsSection, position = 21)
    default String staminaMessage() { return "Drink Stamina"; }

    @ConfigItem(keyName = "staminaUseGlobalTimer", name = "Use global timer: Stamina", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = otherBoostsSection, position = 22)
    default boolean staminaUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "staminaDuration", name = "Display Time: Stamina", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = otherBoostsSection, position = 23)
    default int staminaDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "staminaEarlyWarning", name = "Warn before expiry: Stamina", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = otherBoostsSection, position = 24)
    default int staminaEarlyWarning() { return 0; }

    @ConfigItem(keyName = "staminaColor", name = "Stamina: Colour", description = "Text color for Stamina", section = otherBoostsSection, position = 25)
    default Color staminaColor() { return Color.YELLOW; }

    @ConfigSection(name = "Potions", description = "Potions reminders", position = 4, closedByDefault = true)
    String potionsSection = "potionsSection";

    @ConfigItem(keyName = "enableSurgePotion", name = "Enable Surge Potion", description = "Enable overhead for Surge Potion", section = potionsSection, position = 0)
    default boolean enableSurgePotion() { return false; }

    @ConfigItem(keyName = "surgePotionMessage", name = "Surge: Message", description = "Message for Surge Potion", section = potionsSection, position = 1)
    default String surgePotionMessage() { return "Drink Surge"; }

    @ConfigItem(keyName = "surgePotionUseGlobalTimer", name = "Use global timer: Surge", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = potionsSection, position = 2)
    default boolean surgePotionUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "surgePotionDuration", name = "Display Time: Surge", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = potionsSection, position = 3)
    default int surgePotionDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "surgePotionEarlyWarning", name = "Warn before expiry: Surge", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = potionsSection, position = 4)
    default int surgePotionEarlyWarning() { return 0; }

    @ConfigItem(keyName = "surgePotionColor", name = "Surge: Colour", description = "Text color for Surge Potion", section = potionsSection, position = 5)
    default Color surgePotionColor() { return Color.BLUE; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableAntiPoison", name = reminderDivider, description = "", section = potionsSection, position = 6)
    void dividerBeforeenableAntiPoison();

    @ConfigItem(keyName = "enableAntiPoison", name = "Enable AntiPoison", description = "Enable overhead for AntiPoison", section = potionsSection, position = 7)
    default boolean enableAntiPoison() { return false; }

    @ConfigItem(keyName = "antiPoisonMessage", name = "Antipoison: Message", description = "Message for AntiPoison", section = potionsSection, position = 8)
    default String antipoisonMessage() { return "Drink Antipoison"; }

    @ConfigItem(keyName = "antiPoisonUseGlobalTimer", name = "Use global timer: Antipoison", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = potionsSection, position = 9)
    default boolean antiPoisonUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "antiPoisonDuration", name = "Display Time: Antipoison", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = potionsSection, position = 10)
    default int antiPoisonDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "antiPoisonEarlyWarning", name = "Warn before expiry: Antipoison", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = potionsSection, position = 11)
    default int antiPoisonEarlyWarning() { return 0; }

    @ConfigItem(keyName = "antiPoisonColor", name = "Antipoison: Colour", description = "Text color for AntiPoison", section = potionsSection, position = 12)
    default Color antipoisonColor() { return Color.green; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableVenom", name = reminderDivider, description = "", section = potionsSection, position = 13)
    void dividerBeforeenableVenom();

    @ConfigItem(keyName = "enableVenom", name = "Enable Venom", description = "Enable overhead for Venom", section = potionsSection, position = 14)
    default boolean enableVenom() { return false; }

    @ConfigItem(keyName = "venomMessage", name = "Venom: Message", description = "Message for Venom", section = potionsSection, position = 15)
    default String venomMessage() { return "Drink Anti-venom"; }

    @ConfigItem(keyName = "venomUseGlobalTimer", name = "Use global timer: Venom", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = potionsSection, position = 16)
    default boolean venomUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "venomDuration", name = "Display Time: Venom", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = potionsSection, position = 17)
    default int venomDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "venomEarlyWarning", name = "Warn before expiry: Venom", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = potionsSection, position = 18)
    default int venomEarlyWarning() { return 0; }

    @ConfigItem(keyName = "venomColor", name = "Venom: Colour", description = "Text color for Venom", section = potionsSection, position = 19)
    default Color venomColor() { return Color.green; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableAntifire", name = reminderDivider, description = "", section = potionsSection, position = 20)
    void dividerBeforeenableAntifire();

    @ConfigItem(keyName = "enableAntifire", name = "Enable Antifire", description = "Expiry reminder for antifire potions and mixes, extended antifire, and super antifire variants", section = potionsSection, position = 21)
    default boolean enableAntifire() { return false; }

    @ConfigItem(keyName = "antifireMessage", name = "Antifire: Message", description = "Message for Antifire", section = potionsSection, position = 22)
    default String antifireMessage() { return "Drink Antifire"; }

    @ConfigItem(keyName = "antifireUseGlobalTimer", name = "Use global timer: Antifire", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = potionsSection, position = 23)
    default boolean antifireUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "antifireDuration", name = "Display Time: Antifire", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = potionsSection, position = 24)
    default int antifireDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "antifireEarlyWarning", name = "Warn before expiry: Antifire", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = potionsSection, position = 25)
    default int antifireEarlyWarning() { return 0; }

    @ConfigItem(keyName = "antifireColor", name = "Antifire: Colour", description = "Text color for Antifire", section = potionsSection, position = 26)
    default Color antifireColor() { return Color.pink; }

    @ConfigSection(name = "Raid Boosts", description = "Raid Boosts reminders", position = 5, closedByDefault = true)
    String raidBoostsSection = "raidBoostsSection";

    @ConfigItem(keyName = "enableOverload", name = "Enable Overload", description = "Enable overhead for Overload", section = raidBoostsSection, position = 0)
    default boolean enableOverload() { return false; }

    @ConfigItem(keyName = "overloadMessage", name = "Overload: Message", description = "Message for Overload", section = raidBoostsSection, position = 1)
    default String overloadMessage() { return "Drink Overload"; }

    @ConfigItem(keyName = "overloadUseGlobalTimer", name = "Use global timer: Overload", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = raidBoostsSection, position = 2)
    default boolean overloadUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "overloadDuration", name = "Display Time: Overload", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = raidBoostsSection, position = 3)
    default int overloadDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "overloadEarlyWarning", name = "Warn before expiry: Overload", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = raidBoostsSection, position = 4)
    default int overloadEarlyWarning() { return 0; }

    @ConfigItem(keyName = "overloadColor", name = "Overload: Colour", description = "Text color for Overload", section = raidBoostsSection, position = 5)
    default Color overloadColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableSmellingSalt", name = reminderDivider, description = "", section = raidBoostsSection, position = 6)
    void dividerBeforeenableSmellingSalt();

    @ConfigItem(keyName = "enableSmellingSalt", name = "Enable Smelling Salt", description = "Enable overhead for Smelling Salt", section = raidBoostsSection, position = 7)
    default boolean enableSmellingSalt() { return false; }

    @ConfigItem(keyName = "smellingSaltMessage", name = "Smelling Salt: Message", description = "Message for Smelling Salt", section = raidBoostsSection, position = 8)
    default String smellingSaltMessage() { return "Use Smelling Salt"; }

    @ConfigItem(keyName = "smellingSaltUseGlobalTimer", name = "Use global timer: Smelling Salt", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = raidBoostsSection, position = 9)
    default boolean smellingSaltUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "smellingSaltDuration", name = "Display Time: Smelling Salt", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = raidBoostsSection, position = 10)
    default int smellingSaltDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "smellingSaltEarlyWarning", name = "Warn before expiry: Smelling Salt", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = raidBoostsSection, position = 11)
    default int smellingSaltEarlyWarning() { return 0; }

    @ConfigItem(keyName = "smellingSaltColor", name = "Smelling Salt: Colour", description = "Text color for Smelling Salt", section = raidBoostsSection, position = 12)
    default Color smellingSaltColor() { return Color.ORANGE; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeLiquidAdrenaline", name = reminderDivider, description = "", section = raidBoostsSection, position = 13)
    void dividerBeforeLiquidAdrenaline();

    @ConfigItem(keyName = "enableLiquidAdrenaline", name = "Enable Liquid Adrenaline", description = "Remind when Liquid Adrenaline's special attack cost reduction ends", section = raidBoostsSection, position = 14)
    default boolean enableLiquidAdrenaline() { return false; }

    @ConfigItem(keyName = "liquidAdrenalineMessage", name = "Liquid Adrenaline: Message", description = "Message for Liquid Adrenaline", section = raidBoostsSection, position = 15)
    default String liquidAdrenalineMessage() { return "Drink Liquid Adrenaline"; }

    @ConfigItem(keyName = "liquidAdrenalineUseGlobalTimer", name = "Use global timer: Adrenaline", description = "Use Overhead Display Time. Turn off to use Liquid Adrenaline's own display time", section = raidBoostsSection, position = 16)
    default boolean liquidAdrenalineUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "liquidAdrenalineDuration", name = "Display Time: Adrenaline", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = raidBoostsSection, position = 17)
    default int liquidAdrenalineDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "liquidAdrenalineEarlyWarning", name = "Warn before expiry: Adrenaline", description = "Seconds before Liquid Adrenaline ends (0-20). 0 warns at expiry, without repeating an early warning", section = raidBoostsSection, position = 18)
    default int liquidAdrenalineEarlyWarning() { return 0; }

    @ConfigItem(keyName = "liquidAdrenalineColor", name = "Liquid Adrenaline: Colour", description = "Text colour for Liquid Adrenaline", section = raidBoostsSection, position = 19)
    default Color liquidAdrenalineColor() { return Color.YELLOW; }

    @ConfigSection(name = "Magic", description = "Magic reminders", position = 3, closedByDefault = true)
    String magicSection = "magicSection";

    @ConfigItem(keyName = "enableThrall", name = "Enable Thrall", description = "Enable overhead for Thralls", section = magicSection, position = 0)
    default boolean enableThrall() { return false; }

    @ConfigItem(keyName = "thrallMessage", name = "Thrall: Message", description = "Message for Thralls", section = magicSection, position = 1)
    default String thrallMessage() { return "Cast Thrall"; }

    @ConfigItem(keyName = "thrallUseGlobalTimer", name = "Use global timer: Thrall", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = magicSection, position = 2)
    default boolean thrallUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "thrallDuration", name = "Display Time: Thrall", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = magicSection, position = 3)
    default int thrallDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "thrallEarlyWarning", name = "Warn before expiry: Thrall", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = magicSection, position = 4)
    default int thrallEarlyWarning() { return 0; }

    @ConfigItem(keyName = "thrallColor", name = "Thrall: Colour", description = "Text color for Thralls", section = magicSection, position = 5)
    default Color thrallColor() { return Color.CYAN; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableDeathCharge", name = reminderDivider, description = "", section = magicSection, position = 6)
    void dividerBeforeenableDeathCharge();

    @ConfigItem(keyName = "enableDeathCharge", name = "Enable Death Charge", description = "Enable overhead for Death Charge", section = magicSection, position = 7)
    default boolean enableDeathCharge() { return false; }

    @ConfigItem(keyName = "deathChargeMessage", name = "Death Charge: Message", description = "Message for Death Charge", section = magicSection, position = 8)
    default String deathchargeMessage() { return "Cast Death Charge"; }

    @ConfigItem(keyName = "deathChargeUseGlobalTimer", name = "Use global timer: Death Charge", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = magicSection, position = 9)
    default boolean deathChargeUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "deathChargeDuration", name = "Display Time: Death Charge", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = magicSection, position = 10)
    default int deathChargeDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "deathChargeEarlyWarning", name = "Warn before expiry: Death Charge", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = magicSection, position = 11)
    default int deathChargeEarlyWarning() { return 0; }

    @ConfigItem(keyName = "deathChargeColor", name = "Death Charge: Colour", description = "Text color for Death Charge", section = magicSection, position = 12)
    default Color deathchargeColor() { return Color.RED; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableMarkOfDarkness", name = reminderDivider, description = "", section = magicSection, position = 13)
    void dividerBeforeenableMarkOfDarkness();

    @ConfigItem(keyName = "enableMarkOfDarkness", name = "Enable Mark of Darkness", description = "Enable overhead for Mark of Darkness", section = magicSection, position = 14)
    default boolean enableMarkOfDarkness() { return false; }

    @ConfigItem(keyName = "markOfDarknessMessage", name = "Mark of Darkness: Message", description = "Message for Mark of Darkness", section = magicSection, position = 15)
    default String markOfDarknessMessage() { return "Cast Mark of Darkness"; }

    @ConfigItem(keyName = "markOfDarknessUseGlobalTimer", name = "Use global timer: Mark of Darkness", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = magicSection, position = 16)
    default boolean markOfDarknessUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "markOfDarknessDuration", name = "Display Time: Mark of Darkness", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = magicSection, position = 17)
    default int markOfDarknessDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "markOfDarknessEarlyWarning", name = "Warn before expiry: Mark of Darkness", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = magicSection, position = 18)
    default int markOfDarknessEarlyWarning() { return 0; }

    @ConfigItem(keyName = "markOfDarknessColor", name = "Mark of Darkness: Colour", description = "Text color for Mark of Darkness", section = magicSection, position = 19)
    default Color markOfDarknessColor() { return Color.BLUE; }

    // Read-only separator between reminders.
    @ConfigItem(keyName = "dividerBeforeenableWardOfArceuus", name = reminderDivider, description = "", section = magicSection, position = 20)
    void dividerBeforeenableWardOfArceuus();

    @ConfigItem(keyName = "enableWardOfArceuus", name = "Enable Ward of Arceuus", description = "Enable overhead for Ward of Arceuus", section = magicSection, position = 21)
    default boolean enableWardOfArceuus() { return false; }

    @ConfigItem(keyName = "WardOfArceuusMessage", name = "Ward of Arceuus: Message", description = "Message for Ward of Arceuus", section = magicSection, position = 22)
    default String WardOfArceuusMessage() { return "Cast Ward of Arceuus"; }

    @ConfigItem(keyName = "wardOfArceuusUseGlobalTimer", name = "Use global timer: Ward of Arceuus", description = "Use Overhead Display Time. Turn off to use this reminder's own display time", section = magicSection, position = 23)
    default boolean wardOfArceuusUseGlobalTimer() { return true; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "wardOfArceuusDuration", name = "Display Time: Ward of Arceuus", description = "Custom duration when Use global timer is off. 0 stays until cleared; maximum 300 seconds", section = magicSection, position = 24)
    default int wardOfArceuusDuration() { return 15; }

    @Range(min = 0, max = 20)
    @ConfigItem(keyName = "wardOfArceuusEarlyWarning", name = "Warn before expiry: Ward of Arceuus", description = "Seconds before the effect expires or cooldown ends (0-20). 0 warns at expiry. Does not add a second expiry message", section = magicSection, position = 25)
    default int wardOfArceuusEarlyWarning() { return 0; }

    @ConfigItem(keyName = "WardOfArceuusColor", name = "Ward of Arceuus: Colour", description = "Text color for Ward of Arceuus", section = magicSection, position = 26)
    default Color WardOfArceuusColor() { return Color.BLUE; }

}
