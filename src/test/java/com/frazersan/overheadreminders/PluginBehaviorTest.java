package com.frazersan.overheadreminders;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.StatChanged;
import net.runelite.client.ui.overlay.OverlayManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PluginBehaviorTest
{
    private OverheadNotificationsPlugin plugin;
    private Client client;
    private OverheadNotificationsConfig config;
    private NotificationStore store;
    private OverlayManager overlays;
    private ReminderOverlay overlay;
    private AtomicLong clock;

    @Before
    public void setup() throws Exception
    {
        plugin = new OverheadNotificationsPlugin();
        client = mock(Client.class);
        config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        clock = new AtomicLong();
        store = new NotificationStore(clock::get);
        overlays = mock(OverlayManager.class);
        overlay = new ReminderOverlay(client, store, config);
        inject("client", client);
        inject("config", config);
        inject("notifications", store);
        inject("overlayManager", overlays);
        inject("overlay", overlay);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getLocalPlayer()).thenReturn(mock(Player.class));
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(21);
        plugin.startUp();
    }

    private void inject(String name, Object value) throws Exception
    {
        Field field = OverheadNotificationsPlugin.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(plugin, value);
    }

    private void chat(String text)
    {
        ChatMessage event = new ChatMessage();
        event.setType(ChatMessageType.GAMEMESSAGE);
        event.setMessage(text);
        plugin.onChatMessage(event);
    }

    @Test
    public void prayerUsesGlobalDurationWithoutRefreshingOnSubsequentTicks()
    {
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(20);
        plugin.onGameTick(new GameTick());
        assertEquals("LOW PRAYER!", store.snapshot().get(0).message);
        clock.set(3_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertTrue(store.snapshot().isEmpty());
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(21);
        plugin.onGameTick(new GameTick());
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(20);
        plugin.onGameTick(new GameTick());
        assertEquals(1, store.snapshot().size());
    }

    @Test
    public void prayerOverrideAndChatRemindersExpireSeparately()
    {
        when(config.lowPrayerDuration()).thenReturn(8);
        when(config.enableThrall()).thenReturn(true);
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(10);
        plugin.onGameTick(new GameTick());
        chat("Your thrall returns to the grave.");
        chat("Your thrall returns to the grave.");
        assertEquals(2, store.snapshot().size());
        clock.set(3_000_000_000L);
        assertEquals(1, store.snapshot().size());
        assertEquals("Low Prayer", store.snapshot().get(0).key);
        clock.set(8_000_000_000L);
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void hornStatChangeOnlyClearsHorn()
    {
        when(config.enableSoulflameHorn()).thenReturn(true);
        when(config.enableThrall()).thenReturn(true);
        chat("You encourage nearby allies");
        chat("A friend encourages you with their Soulflame horn");
        chat("Your thrall returns to the grave.");
        assertEquals(2, store.snapshot().size());
        StatChanged stat = new StatChanged(Skill.ATTACK, 100, 99, 99);
        plugin.onStatChanged(stat);
        assertEquals(1, store.snapshot().size());
        assertEquals("Thrall", store.snapshot().get(0).key);
    }

    @Test
    public void disabledRemindersAndNonGameChatAreIgnored()
    {
        chat("Your thrall returns to the grave.");
        assertTrue(store.snapshot().isEmpty());
        when(config.enableThrall()).thenReturn(true);
        ChatMessage chat = new ChatMessage();
        chat.setType(ChatMessageType.PUBLICCHAT);
        chat.setMessage("Your thrall returns to the grave.");
        plugin.onChatMessage(chat);
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void logoutClearsAndRearmsButRegionLoadingPreservesAlerts()
    {
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(10);
        plugin.onGameTick(new GameTick());
        GameStateChanged state = new GameStateChanged();
        state.setGameState(GameState.LOADING);
        plugin.onGameStateChanged(state);
        assertEquals(1, store.snapshot().size());
        state.setGameState(GameState.LOGIN_SCREEN);
        plugin.onGameStateChanged(state);
        assertTrue(store.snapshot().isEmpty());
        plugin.onGameTick(new GameTick());
        assertEquals(1, store.snapshot().size());
    }

    @Test
    public void shutdownRemovesOverlayAndSupportsRestart() throws Exception
    {
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(10);
        plugin.onGameTick(new GameTick());
        plugin.shutDown();
        verify(overlays).remove(overlay);
        assertTrue(store.snapshot().isEmpty());
        plugin.startUp();
        plugin.onGameTick(new GameTick());
        assertEquals(1, store.snapshot().size());
        verify(overlays, times(2)).add(overlay);
    }

    @Test
    public void disablingLowPrayerClearsItsEntry()
    {
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(10);
        plugin.onGameTick(new GameTick());
        when(config.enableLowPrayer()).thenReturn(false);
        plugin.onGameTick(new GameTick());
        assertTrue(store.snapshot().isEmpty());
    }
    @Test
    public void deathChargeChatStartsDeadlineAndDisablingCancelsIt() throws Exception
    {
        inject("timer", new DeathChargeTimer(clock::get));
        when(config.enableDeathCharge()).thenReturn(true);
        chat("<col=ff0000>Upon the death of your next foe, some of your special attack energy will be restored.</col>");
        clock.set(60_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertEquals("Death Charge", store.snapshot().get(0).key);
        plugin.onGameTick(new GameTick());
        assertEquals(1, store.snapshot().size());
        clock.set(64_000_000_000L);
        assertTrue(store.snapshot().isEmpty());
        chat("<col=ff0000>Upon the death of your next two foes, some of your special attack energy will be restored.</col>");
        when(config.enableDeathCharge()).thenReturn(false);
        net.runelite.client.events.ConfigChanged changed = new net.runelite.client.events.ConfigChanged();
        changed.setGroup("overheadreminder");
        changed.setKey("enableDeathCharge");
        plugin.onConfigChanged(changed);
        when(config.enableDeathCharge()).thenReturn(true);
        clock.set(130_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void configDisableRemovesOnlyDisabledReminderAndThresholdChangeRearms()
    {
        when(config.enableThrall()).thenReturn(true);
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(10);
        plugin.onGameTick(new GameTick());
        chat("Your thrall returns to the grave.");
        when(config.enableThrall()).thenReturn(false);
        net.runelite.client.events.ConfigChanged changed = new net.runelite.client.events.ConfigChanged();
        changed.setGroup("overheadreminder");
        changed.setKey("enableThrall");
        plugin.onConfigChanged(changed);
        assertEquals(1, store.snapshot().size());
        assertEquals("Low Prayer", store.snapshot().get(0).key);
        clock.set(4_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertTrue(store.snapshot().isEmpty());
        changed.setKey("prayerThreshold");
        plugin.onConfigChanged(changed);
        plugin.onGameTick(new GameTick());
        assertEquals(1, store.snapshot().size());
    }
    @Test
    public void successfulThrallRecastClearsOnlyThrallAndAllowsNextExpiry()
    {
        when(config.enableThrall()).thenReturn(true);
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(10);
        plugin.onGameTick(new GameTick());
        for (String thrall : new String[] {"lesser ghostly", "superior skeletal", "greater zombified"})
        {
            chat("Your thrall returns to the grave.");
            assertEquals(2, store.snapshot().size());
            chat("<col=00ff00>You resurrect a " + thrall + " thrall.</col>");
            assertEquals(1, store.snapshot().size());
            assertEquals("Low Prayer", store.snapshot().get(0).key);
        }
    }

    @Test
    public void failedThrallCastAndPublicChatDoNotClearReminder()
    {
        when(config.enableThrall()).thenReturn(true);
        chat("Your thrall returns to the grave.");
        chat("You do not have enough runes to cast this spell.");
        chat("You need a Book of the Dead to resurrect a thrall.");
        assertEquals(1, store.snapshot().size());
        ChatMessage event = new ChatMessage();
        event.setType(ChatMessageType.PUBLICCHAT);
        event.setMessage("You resurrect a greater ghostly thrall.");
        plugin.onChatMessage(event);
        assertEquals(1, store.snapshot().size());
    }

    @Test
    public void restoredPrayerClearsOnlyAfterRisingAboveThresholdAndRearms()
    {
        when(config.enableThrall()).thenReturn(true);
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(5);
        plugin.onGameTick(new GameTick());
        chat("Your thrall returns to the grave.");
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(20);
        plugin.onGameTick(new GameTick());
        assertEquals(2, store.snapshot().size());
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(21);
        plugin.onGameTick(new GameTick());
        assertEquals(1, store.snapshot().size());
        assertEquals("Thrall", store.snapshot().get(0).key);
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(20);
        plugin.onGameTick(new GameTick());
        assertEquals(2, store.snapshot().size());
    }

    @Test
    public void successfulBuffCastsClearOnlyTheirOwnReminders()
    {
        when(config.enableMarkOfDarkness()).thenReturn(true);
        when(config.enableWardOfArceuus()).thenReturn(true);
        when(config.enableThrall()).thenReturn(true);
        chat("Your Mark of Darkness has faded away.");
        chat("Your Ward of Arceuus has expired.");
        chat("Your thrall returns to the grave.");
        chat("<col=00ff00>You have placed a Mark of Darkness upon yourself.</col>");
        assertEquals(2, store.snapshot().size());
        chat("<col=00ff00>Your defence against Arceuus magic has been strengthened.</col>");
        assertEquals(1, store.snapshot().size());
        assertEquals("Thrall", store.snapshot().get(0).key);
    }

    @Test
    public void recastingDeathChargeClearsReadyMessageAndStartsNewCooldown() throws Exception
    {
        inject("timer", new DeathChargeTimer(clock::get));
        when(config.enableDeathCharge()).thenReturn(true);
        String cast = "<col=ff0000>Upon the death of your next foe, some of your special attack energy will be restored.</col>";
        chat(cast);
        clock.set(60_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertEquals(1, store.snapshot().size());
        chat(cast);
        assertTrue(store.snapshot().isEmpty());
        clock.set(119_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertTrue(store.snapshot().isEmpty());
        clock.set(120_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertEquals("Death Charge", store.snapshot().get(0).key);
    }
    @Test
    public void liveThrallAndWardHighlightCodesStillClearOnlyRecastType()
    {
        when(config.enableThrall()).thenReturn(true);
        when(config.enableWardOfArceuus()).thenReturn(true);
        when(client.getBoostedSkillLevel(Skill.PRAYER)).thenReturn(10);
        plugin.onGameTick(new GameTick());
        chat("@mes_hl_mag@Your greater impish mage thrall returns to the grave.</col>");
        chat("@mes_hl_blu@Your Ward of Arceuus has expired.</col>");
        assertEquals(3, store.snapshot().size());
        chat("You can't resurrect a thrall here.");
        assertEquals(3, store.snapshot().size());
        chat("@mes_hl_mag@You resurrect a greater impish mage thrall.</col>");
        assertEquals(2, store.snapshot().size());
        assertEquals("Ward of Arceuus", store.snapshot().get(1).key);
        chat("@mes_hl_blu@Your defence against Arceuus magic has been strengthened.</col>");
        assertEquals(1, store.snapshot().size());
        assertEquals("Low Prayer", store.snapshot().get(0).key);
    }

    @Test
    public void highlightedMarkAndDeathChargeConfirmationsAlsoClearReminders() throws Exception
    {
        when(config.enableMarkOfDarkness()).thenReturn(true);
        when(config.enableDeathCharge()).thenReturn(true);
        inject("timer", new DeathChargeTimer(clock::get));
        chat("@mes_hl_blu@Your Mark of Darkness has faded away.</col>");
        assertEquals(1, store.snapshot().size());
        chat("@mes_hl_blu@You have placed a Mark of Darkness upon yourself.</col>");
        assertTrue(store.snapshot().isEmpty());
        String cast = "@mes_hl_blu@Upon the death of your next foe, some of your special attack energy will be restored.</col>";
        chat(cast);
        clock.set(60_000_000_000L);
        plugin.onGameTick(new GameTick());
        assertEquals("Death Charge", store.snapshot().get(0).key);
        chat(cast);
        assertTrue(store.snapshot().isEmpty());
    }
}



