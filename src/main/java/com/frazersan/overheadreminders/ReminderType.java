package com.frazersan.overheadreminders;

import java.awt.Color;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/** Common settings keep chat, timer and stat reminders consistent. */
enum ReminderType
{
    LOW_PRAYER("Low Prayer", OverheadNotificationsConfig::enableLowPrayer, OverheadNotificationsConfig::lowPrayerMessage,
        OverheadNotificationsConfig::lowPrayerColor, OverheadNotificationsConfig::lowPrayerUseGlobalTimer,
        OverheadNotificationsConfig::lowPrayerDuration, c -> 0),
    PRAYER_REGEN("Prayer Regen", OverheadNotificationsConfig::enablePrayerRegen, OverheadNotificationsConfig::prayerRegenMessage,
        OverheadNotificationsConfig::prayerRegenColor, OverheadNotificationsConfig::prayerRegenUseGlobalTimer,
        OverheadNotificationsConfig::prayerRegenDuration, OverheadNotificationsConfig::prayerRegenEarlyWarning),
    PRAYER_ENHANCE("Prayer Enhance", OverheadNotificationsConfig::enablePrayerEnhance, OverheadNotificationsConfig::prayerEnhanceMessage,
        OverheadNotificationsConfig::prayerEnhanceColor, OverheadNotificationsConfig::prayerEnhanceUseGlobalTimer,
        OverheadNotificationsConfig::prayerEnhanceDuration, OverheadNotificationsConfig::prayerEnhanceEarlyWarning),
    DIVINE("Divine Pot", OverheadNotificationsConfig::enableDivinePot, OverheadNotificationsConfig::divinePotMessage,
        OverheadNotificationsConfig::divinePotColor, OverheadNotificationsConfig::divinePotUseGlobalTimer,
        OverheadNotificationsConfig::divinePotDuration, OverheadNotificationsConfig::divinePotEarlyWarning),
    HEART("Saturated Heart", OverheadNotificationsConfig::enableSaturatedHeart, OverheadNotificationsConfig::saturatedHeartMessage,
        OverheadNotificationsConfig::saturatedHeartColor, OverheadNotificationsConfig::saturatedHeartUseGlobalTimer,
        OverheadNotificationsConfig::saturatedHeartDuration, OverheadNotificationsConfig::saturatedHeartEarlyWarning),
    MELEE("Melee Boost", OverheadNotificationsConfig::meleeBoostEnabled, OverheadNotificationsConfig::meleeBoostMessage,
        OverheadNotificationsConfig::meleeBoostColor, OverheadNotificationsConfig::meleeBoostUseGlobalTimer,
        OverheadNotificationsConfig::meleeBoostTimeout, c -> 0),
    RANGED("Ranged Boost", OverheadNotificationsConfig::rangedBoostEnabled, OverheadNotificationsConfig::rangedBoostMessage,
        OverheadNotificationsConfig::rangedBoostColor, OverheadNotificationsConfig::rangedBoostUseGlobalTimer,
        OverheadNotificationsConfig::rangedBoostTimeout, c -> 0),
    MAGIC_BOOST("Magic Boost", OverheadNotificationsConfig::magicBoostEnabled, OverheadNotificationsConfig::magicBoostMessage,
        OverheadNotificationsConfig::magicBoostColor, OverheadNotificationsConfig::magicBoostUseGlobalTimer,
        OverheadNotificationsConfig::magicBoostTimeout, c -> 0),
    HORN("Soulflamehorn", OverheadNotificationsConfig::enableSoulflameHorn, OverheadNotificationsConfig::soulflameHornMessage,
        OverheadNotificationsConfig::soulflameHornColor, OverheadNotificationsConfig::soulflameHornUseGlobalTimer,
        OverheadNotificationsConfig::soulflameHornDuration, c -> 0),
    WILD_PIE("Wild Pie", OverheadNotificationsConfig::wildPieEnabled, OverheadNotificationsConfig::wildPieMessage,
        OverheadNotificationsConfig::wildPieColor, OverheadNotificationsConfig::wildPieUseGlobalTimer,
        OverheadNotificationsConfig::wildPieTimeout, c -> 0),
    SUMMER_PIE("Summer Pie", OverheadNotificationsConfig::summerPieEnabled, OverheadNotificationsConfig::summerPieMessage,
        OverheadNotificationsConfig::summerPieColor, OverheadNotificationsConfig::summerPieUseGlobalTimer,
        OverheadNotificationsConfig::summerPieTimeout, c -> 0),
    STAMINA("Stamina", OverheadNotificationsConfig::enableStamina, OverheadNotificationsConfig::staminaMessage,
        OverheadNotificationsConfig::staminaColor, OverheadNotificationsConfig::staminaUseGlobalTimer,
        OverheadNotificationsConfig::staminaDuration, OverheadNotificationsConfig::staminaEarlyWarning),
    SURGE("Surge", OverheadNotificationsConfig::enableSurgePotion, OverheadNotificationsConfig::surgePotionMessage,
        OverheadNotificationsConfig::surgePotionColor, OverheadNotificationsConfig::surgePotionUseGlobalTimer,
        OverheadNotificationsConfig::surgePotionDuration, OverheadNotificationsConfig::surgePotionEarlyWarning),
    ANTIPOISON("AntiPoison", OverheadNotificationsConfig::enableAntiPoison, OverheadNotificationsConfig::antipoisonMessage,
        OverheadNotificationsConfig::antipoisonColor, OverheadNotificationsConfig::antiPoisonUseGlobalTimer,
        OverheadNotificationsConfig::antiPoisonDuration, OverheadNotificationsConfig::antiPoisonEarlyWarning),
    VENOM("Venom", OverheadNotificationsConfig::enableVenom, OverheadNotificationsConfig::venomMessage,
        OverheadNotificationsConfig::venomColor, OverheadNotificationsConfig::venomUseGlobalTimer,
        OverheadNotificationsConfig::venomDuration, OverheadNotificationsConfig::venomEarlyWarning),
    ANTIFIRE("AntiFire", OverheadNotificationsConfig::enableAntifire, OverheadNotificationsConfig::antifireMessage,
        OverheadNotificationsConfig::antifireColor, OverheadNotificationsConfig::antifireUseGlobalTimer,
        OverheadNotificationsConfig::antifireDuration, OverheadNotificationsConfig::antifireEarlyWarning),
    OVERLOAD("Overload", OverheadNotificationsConfig::enableOverload, OverheadNotificationsConfig::overloadMessage,
        OverheadNotificationsConfig::overloadColor, OverheadNotificationsConfig::overloadUseGlobalTimer,
        OverheadNotificationsConfig::overloadDuration, OverheadNotificationsConfig::overloadEarlyWarning),
    SALTS("Smelling Salt", OverheadNotificationsConfig::enableSmellingSalt, OverheadNotificationsConfig::smellingSaltMessage,
        OverheadNotificationsConfig::smellingSaltColor, OverheadNotificationsConfig::smellingSaltUseGlobalTimer,
        OverheadNotificationsConfig::smellingSaltDuration, OverheadNotificationsConfig::smellingSaltEarlyWarning),
    ADRENALINE("Liquid Adrenaline", OverheadNotificationsConfig::enableLiquidAdrenaline, OverheadNotificationsConfig::liquidAdrenalineMessage,
        OverheadNotificationsConfig::liquidAdrenalineColor, OverheadNotificationsConfig::liquidAdrenalineUseGlobalTimer,
        OverheadNotificationsConfig::liquidAdrenalineDuration, OverheadNotificationsConfig::liquidAdrenalineEarlyWarning),
    THRALL("Thrall", OverheadNotificationsConfig::enableThrall, OverheadNotificationsConfig::thrallMessage,
        OverheadNotificationsConfig::thrallColor, OverheadNotificationsConfig::thrallUseGlobalTimer,
        OverheadNotificationsConfig::thrallDuration, OverheadNotificationsConfig::thrallEarlyWarning),
    DEATH_CHARGE("Death Charge", OverheadNotificationsConfig::enableDeathCharge, OverheadNotificationsConfig::deathchargeMessage,
        OverheadNotificationsConfig::deathchargeColor, OverheadNotificationsConfig::deathChargeUseGlobalTimer,
        OverheadNotificationsConfig::deathChargeDuration, OverheadNotificationsConfig::deathChargeEarlyWarning),
    MARK("Mark of Darkness", OverheadNotificationsConfig::enableMarkOfDarkness, OverheadNotificationsConfig::markOfDarknessMessage,
        OverheadNotificationsConfig::markOfDarknessColor, OverheadNotificationsConfig::markOfDarknessUseGlobalTimer,
        OverheadNotificationsConfig::markOfDarknessDuration, OverheadNotificationsConfig::markOfDarknessEarlyWarning),
    WARD("Ward of Arceuus", OverheadNotificationsConfig::enableWardOfArceuus, OverheadNotificationsConfig::WardOfArceuusMessage,
        OverheadNotificationsConfig::WardOfArceuusColor, OverheadNotificationsConfig::wardOfArceuusUseGlobalTimer,
        OverheadNotificationsConfig::wardOfArceuusDuration, OverheadNotificationsConfig::wardOfArceuusEarlyWarning),
    GOADING("Goading", OverheadNotificationsConfig::enableGoading, OverheadNotificationsConfig::goadingMessage,
        OverheadNotificationsConfig::goadingColor, OverheadNotificationsConfig::goadingUseGlobalTimer,
        OverheadNotificationsConfig::goadingDuration, OverheadNotificationsConfig::goadingEarlyWarning),
    SPECIAL_ATTACK("Special Attack", OverheadNotificationsConfig::enableSpecialAttack, OverheadNotificationsConfig::specialAttackMessage,
        OverheadNotificationsConfig::specialAttackColor, OverheadNotificationsConfig::specialAttackUseGlobalTimer,
        OverheadNotificationsConfig::specialAttackDuration, c -> 0);

    final String key;
    private final Predicate<OverheadNotificationsConfig> enabled;
    private final Function<OverheadNotificationsConfig, String> message;
    private final Function<OverheadNotificationsConfig, Color> color;
    private final Predicate<OverheadNotificationsConfig> globalTimer;
    private final ToIntFunction<OverheadNotificationsConfig> duration;
    private final ToIntFunction<OverheadNotificationsConfig> earlyWarning;

    ReminderType(String key, Predicate<OverheadNotificationsConfig> enabled,
        Function<OverheadNotificationsConfig, String> message,
        Function<OverheadNotificationsConfig, Color> color,
        Predicate<OverheadNotificationsConfig> globalTimer,
        ToIntFunction<OverheadNotificationsConfig> duration,
        ToIntFunction<OverheadNotificationsConfig> earlyWarning)
    {
        this.key = key;
        this.enabled = enabled;
        this.message = message;
        this.color = color;
        this.globalTimer = globalTimer;
        this.duration = duration;
        this.earlyWarning = earlyWarning;
    }

    boolean enabled(OverheadNotificationsConfig config) { return enabled.test(config); }
    int duration(OverheadNotificationsConfig config)
    {
        return globalTimer.test(config) ? config.overheadDisplayTime() : duration.applyAsInt(config);
    }
    int earlySeconds(OverheadNotificationsConfig config)
    {
        return Math.max(0, Math.min(20, earlyWarning.applyAsInt(config)));
    }
    void show(OverheadNotificationsConfig config, NotificationStore notifications)
    {
        notifications.show(key, message.apply(config), color.apply(config), duration(config));
    }
    void showPreview(OverheadNotificationsConfig config, NotificationStore samples)
    {
        String text = message.apply(config);
        samples.show(key, text == null || text.trim().isEmpty() ? key : text, color.apply(config), 0);
    }
    static ReminderType forKey(String key)
    {
        for (ReminderType type : values())
        {
            if (type.key.equals(key)) return type;
        }
        throw new IllegalArgumentException("Unknown reminder: " + key);
    }
}
