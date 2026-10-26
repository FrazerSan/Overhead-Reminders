package com.frazersan.overheadreminders;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.MenuAction;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.events.OverlayMenuClicked;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.util.ImageUtil;
import javax.swing.SwingUtilities;
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

    @Inject
    private EventBus eventBus;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private ClientThread clientThread;
    private NavigationButton toolsButton;
    private volatile boolean running;

    @Inject
    private ReminderPreview preview;
    @Inject
    private InventorySupplyChecker supplies;

    private TimedReminderController timedReminders;
    private boolean deathChargeWarned;

    private final LowPrayerState lowPrayer = new LowPrayerState();
    private final SpecialAttackReminderController specialAttack = new SpecialAttackReminderController();
    private DeathChargeTimer timer = null;
    private List<TriggerRule> triggerRules;

    @Override
    protected void startUp()
    {
        log.info("Overhead Reminders started!");
        timer = new DeathChargeTimer();
        deathChargeWarned = false;
        timedReminders = new TimedReminderController(client, config, notifications);
        notifications.clear();
        preview.stop();
        supplies.reset();
        boostReminders.reset();
        lowPrayer.reset();
        specialAttack.reset(notifications);
        overlayManager.add(overlay);

        triggerRules = List.of(
                new TriggerRule(ReminderType.OVERLOAD, MSG_OVERLOAD_WORN_OFF),
                new TriggerRule(ReminderType.SALTS, MSG_SMELLING_SALT_WORN_OFF),
                new TriggerRule(ReminderType.DIVINE, MSG_DIVINE_POTION_WORN_OFF),
                new TriggerRule(ReminderType.PRAYER_REGEN, MSG_PRAYER_REGEN),
                new TriggerRule(ReminderType.PRAYER_ENHANCE, MSG_PRAYER_ENHANCE_WORN_OFF),
                new TriggerRule(ReminderType.THRALL, MSG_THRALL_RETURNS),
                new TriggerRule(ReminderType.MARK, MSG_MARK_OF_DARKNESS_FADED),
                new TriggerRule(ReminderType.WARD, MSG_WARD_OF_ARCEUUS_EXPIRED),
                new TriggerRule(ReminderType.HEART, MSG_SATURATED_HEART_WORN_OFF),
                new TriggerRule(ReminderType.STAMINA, MSG_STAMINA_EXPIRED),
                new TriggerRule(ReminderType.VENOM, MSG_VENOM_EXPIRED),
                new TriggerRule(ReminderType.ANTIFIRE, MSG_ANTIFIRE_EXPIRED),
                new TriggerRule(ReminderType.ANTIPOISON, MSG_ANTIPOISON_EXPIRED),
                new TriggerRule(ReminderType.HORN, MSG_SOULFLAMEHORN_1),
                new TriggerRule(ReminderType.HORN, MSG_SOULFLAMEHORN_2),
                new TriggerRule(ReminderType.SURGE, MSG_SURGE_POTION_READY)
        );
        ConfigDefaultsMigration.apply(configManager);
        running = true;
        updateToolsSidebar();
    }

    private void updateToolsSidebar()
    {
        SwingUtilities.invokeLater(() -> {
            if (!running || !config.showSidebarIcon())
            {
                if (toolsButton != null) clientToolbar.removeNavigation(toolsButton);
                toolsButton = null;
                return;
            }
            if (toolsButton != null) return;
            ReminderToolsPanel panel = new ReminderToolsPanel(new ReminderConfigActions(configManager, this::refreshSettings),
                action -> clientThread.invokeLater(action), () -> preview.start(config), preview::stop);
            toolsButton = NavigationButton.builder().tooltip("Overhead Reminders tools")
                .icon(ImageUtil.resizeImage(ImageUtil.loadImageResource(getClass(), "tools-icon.png"), 16, 16))
                .priority(6).panel(panel).build();
            clientToolbar.addNavigation(toolsButton);
        });
    }

    @Override
    protected void shutDown()
    {
        log.info("Overhead Reminders stopped!");
        running = false;
        SwingUtilities.invokeLater(() -> {
            if (toolsButton != null) clientToolbar.removeNavigation(toolsButton);
            toolsButton = null;
        });
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
            timedReminders.reapplied(ReminderType.THRALL);
            return;
        }
        if (plainMessage.equals("You have placed a Mark of Darkness upon yourself."))
        {
            timedReminders.reapplied(ReminderType.MARK);
            return;
        }
        if (plainMessage.equals("Your defence against Arceuus magic has been strengthened."))
        {
            timedReminders.reapplied(ReminderType.WARD);
            return;
        }

        if (plainMessage.equals("You drink some of the potion, reducing the energy cost of your special attacks."))
        {
            timedReminders.reapplied(ReminderType.ADRENALINE);
            return;
        }

        if (config.enableDeathCharge() && (plainMessage.matches(DEATH_CHARGE_ACTIVE.pattern()) || plainMessage.matches(UPGRADED_DEATH_CHARGE_ACTIVE.pattern())))
        {
            notifications.remove("Death Charge");
            timer.start();
            deathChargeWarned = false;
            return;
        }

        for (TriggerRule rule : triggerRules)
        {
            if (rule.type.enabled(config) && rule.matches(plainMessage))
            {
                timedReminders.onExpiry(rule.type);
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
        if (config.showOutOfSupplies()) supplies.update();
        timedReminders.onGameTick();
        boostReminders.onGameTick();
        specialAttack.update(client.getVarpValue(net.runelite.api.gameval.VarPlayerID.SA_ENERGY), config, notifications);
        if (timer.getState() != DeathChargeTimerState.IDLE)
        {
            if (config.enableDeathCharge() && !deathChargeWarned
                && timer.remainingMillis() <= ReminderType.DEATH_CHARGE.earlySeconds(config) * 1000L)
            {
                ReminderType.DEATH_CHARGE.show(config, notifications);
                deathChargeWarned = true;
            }
            if (timer.getState() == DeathChargeTimerState.EXPIRED) timer.stop();
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
                ReminderType.LOW_PRAYER.show(config, notifications);
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
        String key = event.getKey();
        if (key.equals("enableSpecialAttack") || key.startsWith("specialAttack"))
        {
            specialAttack.reset(notifications);
        }
        if ("showSidebarIcon".equals(key))
        {
            updateToolsSidebar();
            return;
        }
        if ("quickAction".equals(key))
        {
            runQuickAction(event.getNewValue());
            return;
        }
        preview.refresh(config);
        if (key.startsWith("meleeBoost") || key.startsWith("magicBoost") || key.startsWith("rangedBoost")
            || key.startsWith("wildPie") || key.startsWith("summerPie"))
        {
            boostReminders.reset();
        }
        if ("enableLowPrayer".equals(event.getKey()) || "prayerThreshold".equals(event.getKey()))
        {
            lowPrayer.reset();
            notifications.remove("Low Prayer");
        }
        for (TriggerRule rule : triggerRules)
        {
            if (!rule.type.enabled(config))
            {
                notifications.remove(rule.type.key);
            }
        }
        if (!config.enableLiquidAdrenaline()) notifications.remove("Liquid Adrenaline");
        if (!config.enableDeathCharge())
        {
            timer.stop();
            notifications.remove("Death Charge");
        }
    }

    private void runQuickAction(String value)
    {
        ReminderQuickAction action;
        try { action = ReminderQuickAction.valueOf(value == null ? "NONE" : value); }
        catch (IllegalArgumentException ex) { return; }
        if (action == ReminderQuickAction.NONE) return;
        configManager.setConfiguration("overheadreminder", "quickAction", ReminderQuickAction.NONE.name());
        clientThread.invokeLater(() -> {
            ReminderConfigActions actions = new ReminderConfigActions(configManager, this::refreshSettings);
            switch (action)
            {
                case SHOW_PREVIEW: preview.start(config); refreshSettings(); break;
                case STOP_PREVIEW: preview.stop(); refreshSettings(); break;
                case ENABLE_ALL: actions.enableAll(true); break;
                case DISABLE_ALL: actions.enableAll(false); break;
                case RESET_APPEARANCE: actions.resetAppearance(); break;
                case RESET_TIMERS: actions.resetTimers(); break;
                case RESET_MESSAGES: actions.resetMessages(); break;
                case USE_GLOBAL_TIMERS: actions.useGlobalForAll(true); break;
                case USE_INDIVIDUAL_TIMERS: actions.useGlobalForAll(false); break;
                default: break;
            }
        });
    }

    private void refreshSettings()
    {
        // RuneLite's settings form does not listen for arbitrary ConfigChanged
        // updates. Its public Configure action creates a fresh view of saved values.
        eventBus.post(new OverlayMenuClicked(new OverlayMenuEntry(
            MenuAction.RUNELITE_OVERLAY_CONFIG, "Configure", "Overhead Reminders"), overlay));
    }

    private void resetSession()
    {
        notifications.clear();
        preview.stop();
        supplies.reset();
        boostReminders.reset();
        timedReminders.reset();
        deathChargeWarned = false;
        lowPrayer.reset();
        specialAttack.reset(notifications);
        if (timer != null)
        {
            timer.stop();
        }
    }

    @Provides
    OverheadNotificationsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OverheadNotificationsConfig.class);
    }
}
