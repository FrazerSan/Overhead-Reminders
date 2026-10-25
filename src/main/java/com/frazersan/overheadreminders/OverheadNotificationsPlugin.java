package com.frazersan.overheadreminders;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;


import java.util.regex.Pattern;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Overhead Reminders",
        description = "Stacked reminders for low prayer, expiring effects, combat boosts and pies",
        tags = {"overhead", "notification", "chat", "thrall", "overload", "boost", "raid", "slayer", "reminder", "remind"}
)
public class OverheadNotificationsPlugin extends Plugin
{
    private static final String MSG_OVERLOAD_WORN_OFF = "overload have worn off";
    private static final String MSG_SMELLING_SALT_WORN_OFF = "smelling salts has worn off";
    private static final String MSG_DIVINE_POTION_WORN_OFF = "divine potion have worn off";
    private static final String MSG_PRAYER_REGEN = "prayer regeneration effect has worn off";
    private static final String MSG_PRAYER_ENHANCE_WORN_OFF = "prayer enhance effect has worn off";
    private static final String MSG_THRALL_RETURNS = "thrall returns to the grave";
    private static final String MSG_MARK_OF_DARKNESS_FADED = "Mark of Darkness has faded away";
    private static final String MSG_WARD_OF_ARCEUUS_EXPIRED = "Ward of Arceuus has expired";
    private static final String MSG_SATURATED_HEART_WORN_OFF = "saturated heart have worn off";
    private static final String MSG_STAMINA_EXPIRED = "stamina enhancement has expired";
    private static final String MSG_VENOM_EXPIRED = "venom resistance has worn off";
    private static final String MSG_ANTIPOISON_EXPIRED = "poison resistance has worn off";
    private static final String MSG_ANTIFIRE_EXPIRED = "antifire potion has expired";
    private static final String MSG_SOULFLAMEHORN_1 = "encourages you with their Soulflame horn";
    private static final String MSG_SOULFLAMEHORN_2 = "You encourage nearby allies";
    private static final String MSG_SURGE_POTION_READY = "capable of drinking another dose of surge potion";
    public static final Pattern DEATH_CHARGE_ACTIVE =
            Pattern.compile("Upon the death of your next foe, some of your special attack energy will be restored\\.");

    public static final Pattern UPGRADED_DEATH_CHARGE_ACTIVE =
            Pattern.compile("Upon the death of your next two foes, some of your special attack energy will be restored\\.");

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ReminderOverlay overlay;

    @Inject
    private OverheadNotificationsConfig config;

    @Inject
    private ConfigManager configManager;

    @Inject
    private NotificationStore notifications;

    @Inject
    private BoostReminderController boostReminders;

    private static final int[] DIVINE_TIMERS = {
        VarbitID.DIVINEATTACK_POTION_TIME, VarbitID.DIVINESTRENGTH_POTION_TIME,
        VarbitID.DIVINEDEFENCE_POTION_TIME, VarbitID.DIVINECOMBAT_POTION_TIME,
        VarbitID.DIVINEMAGIC_POTION_TIME, VarbitID.DIVINEBATTLEMAGE_POTION_TIME,
        VarbitID.DIVINERANGE_POTION_TIME, VarbitID.DIVINEBASTION_POTION_TIME
    };
    private final int[] previousDivineTimers = new int[DIVINE_TIMERS.length];

    private final LowPrayerState lowPrayer = new LowPrayerState();
    private DeathChargeTimer timer = null;
    private List<TriggerRule> triggerRules;

    @Override
    protected void startUp()
    {
        log.info("Overhead Reminders started!");
        timer = new DeathChargeTimer();
        notifications.clear();
        java.util.Arrays.fill(previousDivineTimers, 0);
        boostReminders.reset();
        lowPrayer.reset();
        overlayManager.add(overlay);

        triggerRules = List.of(
                new TriggerRule("Overload", MSG_OVERLOAD_WORN_OFF, config::enableOverload, config::overloadMessage, config::overloadColor, 0),
                new TriggerRule("Smelling Salt", MSG_SMELLING_SALT_WORN_OFF, config::enableSmellingSalt, config::smellingSaltMessage, config::smellingSaltColor, 0),
                new TriggerRule("Divine Pot", MSG_DIVINE_POTION_WORN_OFF, config::enableDivinePot, config::divinePotMessage, config::divinePotColor, 0),
                new TriggerRule("Prayer Regen", MSG_PRAYER_REGEN, config::enablePrayerRegen, config::prayerRegenMessage, config::prayerRegenColor, 0),
                new TriggerRule("Prayer Enhance", MSG_PRAYER_ENHANCE_WORN_OFF, config::enablePrayerEnhance, config::prayerEnhanceMessage, config::prayerEnhanceColor, 0),
                new TriggerRule("Thrall", MSG_THRALL_RETURNS, config::enableThrall, config::thrallMessage, config::thrallColor, 0),
                new TriggerRule("Mark of Darkness", MSG_MARK_OF_DARKNESS_FADED, config::enableMarkOfDarkness, config::markOfDarknessMessage, config::markOfDarknessColor, 0),
                new TriggerRule("Ward of Arceuus", MSG_WARD_OF_ARCEUUS_EXPIRED, config::enableWardOfArceuus, config::WardOfArceuusMessage, config::WardOfArceuusColor, 0),
                new TriggerRule("Saturated Heart", MSG_SATURATED_HEART_WORN_OFF, config::enableSaturatedHeart, config::saturatedHeartMessage, config::saturatedHeartColor, 0),
                new TriggerRule("Stamina", MSG_STAMINA_EXPIRED, config::enableStamina, config::staminaMessage, config::staminaColor, 0),
                new TriggerRule("Venom", MSG_VENOM_EXPIRED, config::enableVenom, config::venomMessage, config::venomColor, 0),
                new TriggerRule("AntiFire", MSG_ANTIFIRE_EXPIRED, config::enableAntifire, config::antifireMessage, config::antifireColor, 0),
                new TriggerRule("AntiPoison", MSG_ANTIPOISON_EXPIRED, config::enableAntiPoison, config::antipoisonMessage, config::antipoisonColor, 0),
                new TriggerRule("Soulflamehorn", MSG_SOULFLAMEHORN_1, config::enableSoulflameHorn, config::soulflameHornMessage, config::soulflameHornColor, 0),
                new TriggerRule("Soulflamehorn", MSG_SOULFLAMEHORN_2, config::enableSoulflameHorn, config::soulflameHornMessage, config::soulflameHornColor, 0),
                new TriggerRule("Surge", MSG_SURGE_POTION_READY, config::enableSurgePotion, config::surgePotionMessage, config::surgePotionColor, 0)
        );
        ConfigDefaultsMigration.apply(configManager);
    }

    @Override
    protected void shutDown()
    {
        log.info("Overhead Reminders stopped!");
        overlayManager.remove(overlay);
        resetSession();
        timer = null;
        triggerRules = java.util.Collections.emptyList();
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() != ChatMessageType.GAMEMESSAGE || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        final String msg = event.getMessage();
        // The live client also uses @mes_hl_*@ highlight tokens outside HTML tags.
        final String plainMessage = Text.removeTags(msg).replaceAll("@mes_[A-Za-z0-9_]+@", "").trim();

        // Successful cast confirmations, rather than clicks or cast animations:
        // failed casts must leave the reminder visible.
        if (plainMessage.startsWith("You resurrect a ") && plainMessage.endsWith(" thrall."))
        {
            notifications.remove("Thrall");
            return;
        }
        if (plainMessage.equals("You have placed a Mark of Darkness upon yourself."))
        {
            notifications.remove("Mark of Darkness");
            return;
        }
        if (plainMessage.equals("Your defence against Arceuus magic has been strengthened."))
        {
            notifications.remove("Ward of Arceuus");
            return;
        }

        if (config.enableDeathCharge() && (plainMessage.matches(DEATH_CHARGE_ACTIVE.pattern()) || plainMessage.matches(UPGRADED_DEATH_CHARGE_ACTIVE.pattern())))
        {
            notifications.remove("Death Charge");
            timer.start();
            return;
        }

        for (TriggerRule rule : triggerRules)
        {
            if (rule.isEnabled.getAsBoolean() && rule.matches(plainMessage))
            {
                triggerAlert(
                        rule.triggerKey,
                        rule.messageSupplier.get(),
                        rule.colorSupplier.get(),
                        rule.durationOverride
                );
                return;
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick e)
    {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
        {
            return;
        }
        for (int i = 0; i < DIVINE_TIMERS.length; i++)
        {
            int remaining = client.getVarbitValue(DIVINE_TIMERS[i]);
            if (remaining > previousDivineTimers[i])
            {
                // A fresh dose also counts when the stat itself does not rise.
                notifications.remove("Divine Pot");
            }
            previousDivineTimers[i] = remaining;
        }
        boostReminders.onGameTick();
        if (timer.getState() == DeathChargeTimerState.EXPIRED)
        {
            if (config.enableDeathCharge())
            {
                triggerAlert("Death Charge", config.deathchargeMessage(), config.deathchargeColor(), 0);
            }
            timer.stop();
        }
        if (config.enableLowPrayer())
        {
            int prayer = client.getBoostedSkillLevel(Skill.PRAYER);
            if (prayer > config.prayerThreshold())
            {
                notifications.remove("Low Prayer");
            }
            if (lowPrayer.update(prayer, config.prayerThreshold()))
            {
                triggerAlert("Low Prayer", config.lowPrayerMessage(), config.lowPrayerColor(), config.lowPrayerDuration());
            }
        }
        else
        {
            lowPrayer.reset();
            notifications.remove("Low Prayer");
        }
    }

    @Subscribe
    public void onStatChanged(StatChanged event)
    {
        boostReminders.onStatChanged(event);
        Skill skill = event.getSkill();
        if (skill == Skill.ATTACK || skill == Skill.STRENGTH || skill == Skill.DEFENCE)
        {
            // Clear only the horn reminder; other simultaneous messages stay visible.
            notifications.remove("Soulflamehorn");
        }
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        GameState state = event.getGameState();
        // LOADING also happens on region changes, so preserve alerts during those.
        if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING || state == GameState.CONNECTION_LOST)
        {
            resetSession();
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!"overheadreminder".equals(event.getGroup()))
        {
            return;
        }
        boostReminders.reset();
        if ("enableLowPrayer".equals(event.getKey()) || "prayerThreshold".equals(event.getKey()))
        {
            lowPrayer.reset();
            notifications.remove("Low Prayer");
        }
        for (TriggerRule rule : triggerRules)
        {
            if (!rule.isEnabled.getAsBoolean())
            {
                notifications.remove(rule.triggerKey);
            }
        }
        if (!config.enableDeathCharge())
        {
            timer.stop();
            notifications.remove("Death Charge");
        }
    }

    private void resetSession()
    {
        notifications.clear();
        java.util.Arrays.fill(previousDivineTimers, 0);
        boostReminders.reset();
        lowPrayer.reset();
        if (timer != null)
        {
            timer.stop();
        }
    }

    private void triggerAlert(String triggerKey, String message, java.awt.Color color, int duration)
    {
        notifications.show(triggerKey, message, color, duration > 0 ? duration : config.overheadDisplayTime());
    }
    @Provides
    OverheadNotificationsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OverheadNotificationsConfig.class);
    }
}
