package com.frazersan.overheadreminders;

import java.util.concurrent.atomic.AtomicLong;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.gameval.VarPlayerID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TimedReminderControllerTest
{
    private Client client;
    private OverheadNotificationsConfig config;
    private NotificationStore store;
    private TimedReminderController reminders;
    private AtomicLong clock;

    @Before
    public void setup()
    {
        client = mock(Client.class);
        config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        clock = new AtomicLong();
        store = new NotificationStore(clock::get);
        reminders = new TimedReminderController(client, config, store);
    }

    @Test
    public void divineWarnsOnceWithinWindowWithoutRepeatingAtExpiryAndRearmsAfterDose()
    {
        when(config.enableDivinePot()).thenReturn(true);
        when(config.divinePotEarlyWarning()).thenReturn(10);
        when(config.overheadDisplayTime()).thenReturn(2);
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(17);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(16);
        reminders.onGameTick();
        assertEquals("Divine Pot", store.snapshot().get(0).key);
        clock.set(2_000_000_000L);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(0);
        reminders.onExpiry(ReminderType.DIVINE);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(500);
        reminders.onGameTick();
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(16);
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
    }

    @Test
    public void defaultZeroMeansExpiryOnlyAndGlobalPermanentDurationWorks()
    {
        when(config.enableDivinePot()).thenReturn(true);
        when(config.overheadDisplayTime()).thenReturn(0);
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(1);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(0);
        reminders.onExpiry(ReminderType.DIVINE);
        reminders.onGameTick();
        clock.set(3_600_000_000_000L);
        assertEquals(1, store.snapshot().size());
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(500);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void coarseAntifireCountersInterpolateToTheChosenSecondAndClearOnRenewal()
    {
        when(config.enableAntifire()).thenReturn(true);
        when(config.antifireEarlyWarning()).thenReturn(5);
        when(client.getTickCount()).thenReturn(100);
        when(client.getVarbitValue(VarbitID.ANTIFIRE_POTION)).thenReturn(2);
        reminders.onGameTick();
        when(client.getTickCount()).thenReturn(130);
        when(client.getVarbitValue(VarbitID.ANTIFIRE_POTION)).thenReturn(1);
        reminders.onGameTick();
        when(client.getTickCount()).thenReturn(151);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty()); // 5.4 seconds remain.
        when(client.getTickCount()).thenReturn(152);
        reminders.onGameTick();
        assertEquals("AntiFire", store.snapshot().get(0).key); // 4.8 seconds remain.
        when(client.getVarbitValue(VarbitID.ANTIFIRE_POTION)).thenReturn(20);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void strongerAntifireCoveragePreventsPrematureWarning()
    {
        when(config.enableAntifire()).thenReturn(true);
        when(config.antifireEarlyWarning()).thenReturn(20);
        when(client.getVarbitValue(VarbitID.ANTIFIRE_POTION)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.SUPER_ANTIFIRE_POTION)).thenReturn(10);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.ANTIFIRE_POTION)).thenReturn(0);
        when(client.getVarbitValue(VarbitID.SUPER_ANTIFIRE_POTION)).thenReturn(1);
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
    }

    @Test
    public void timedPotionsUseTheirOwnUnitsAndIndependentWarnings()
    {
        when(config.enablePrayerRegen()).thenReturn(true);
        when(config.prayerRegenEarlyWarning()).thenReturn(10);
        when(config.enablePrayerEnhance()).thenReturn(true);
        when(config.prayerEnhanceEarlyWarning()).thenReturn(10);
        when(config.enableStamina()).thenReturn(true);
        when(config.staminaEarlyWarning()).thenReturn(10);
        when(config.enableOverload()).thenReturn(true);
        when(config.overloadEarlyWarning()).thenReturn(10);
        when(config.enableSmellingSalt()).thenReturn(true);
        when(config.smellingSaltEarlyWarning()).thenReturn(10);
        when(config.enableSurgePotion()).thenReturn(true);
        when(config.surgePotionEarlyWarning()).thenReturn(10);
        when(client.getVarbitValue(VarbitID.PRAYER_REGENERATION_POTION_TIMER)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.RAIDS_PRAYERENHANCE_TIMER)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.RAIDS_PRAYERENHANCE_RATE)).thenReturn(10);
        when(client.getVarbitValue(VarbitID.STAMINA_ACTIVE)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.STAMINA_DURATION)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.RAIDS_OVERLOAD_TIMER)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_STATS_TIMER)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.SURGE_POTION_TIMER)).thenReturn(1);
        reminders.onGameTick();
        assertEquals(4, store.snapshot().size()); // Overload and salts still have 15s.
        when(client.getTickCount()).thenReturn(10);
        reminders.onGameTick();
        assertEquals(6, store.snapshot().size());
    }

    @Test
    public void heartEarlyWarningUsesItsSharedRechargeClock()
    {
        when(config.enableSaturatedHeart()).thenReturn(true);
        when(config.saturatedHeartEarlyWarning()).thenReturn(10);
        when(client.getVarbitValue(VarbitID.SATURATED_HEART_TIME)).thenReturn(1);
        when(client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER)).thenReturn(2);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER)).thenReturn(1);
        reminders.onGameTick();
        assertEquals("Saturated Heart", store.snapshot().get(0).key);
    }

    @Test
    public void poisonAndVenomWindowsAreSeparateAndRespectImmunityBoundary()
    {
        when(config.enableAntiPoison()).thenReturn(true);
        when(config.antiPoisonEarlyWarning()).thenReturn(20);
        when(config.enableVenom()).thenReturn(true);
        when(config.venomEarlyWarning()).thenReturn(20);
        when(client.getVarpValue(VarPlayerID.POISON)).thenReturn(-39);
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
        assertEquals("Venom", store.snapshot().get(0).key);
        when(client.getVarpValue(VarPlayerID.POISON)).thenReturn(-1);
        reminders.onGameTick();
        assertEquals(2, store.snapshot().size());
    }

    @Test
    public void spellWarningsUseActualClientDurationsAndRecastingClearsOnlyTheirType()
    {
        when(config.enableThrall()).thenReturn(true);
        when(config.thrallEarlyWarning()).thenReturn(10);
        when(config.enableMarkOfDarkness()).thenReturn(true);
        when(config.markOfDarknessEarlyWarning()).thenReturn(10);
        when(config.enableWardOfArceuus()).thenReturn(true);
        when(config.wardOfArceuusEarlyWarning()).thenReturn(10);
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1000);
        when(client.getVarcIntValue(VarClientID.BUFF_RESURRECTION_ACTIVE_START)).thenReturn(925);
        when(client.getVarcIntValue(VarClientID.BUFF_RESURRECTION_ACTIVE_DURATION)).thenReturn(93);
        when(client.getVarcIntValue(VarClientID.BUFF_MARK_OF_DARKNESS_START)).thenReturn(715);
        when(client.getVarcIntValue(VarClientID.BUFF_MARK_OF_DARKNESS_DURATION)).thenReturn(300);
        when(client.getVarcIntValue(VarClientID.BUFF_WARD_OF_ARCEUUS_ACTIVE_START)).thenReturn(925);
        when(client.getRealSkillLevel(Skill.MAGIC)).thenReturn(90);
        reminders.onGameTick();
        assertEquals(3, store.snapshot().size());
        reminders.reapplied(ReminderType.THRALL);
        when(client.getVarcIntValue(VarClientID.BUFF_RESURRECTION_ACTIVE_START)).thenReturn(1000);
        reminders.onGameTick();
        assertEquals(2, store.snapshot().size());
        reminders.onExpiry(ReminderType.MARK);
        assertEquals(2, store.snapshot().size());
    }

    @Test
    public void disablingAndSessionResetClearStateAndRearm()
    {
        when(config.enableDivinePot()).thenReturn(true);
        when(config.divinePotEarlyWarning()).thenReturn(20);
        when(client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME)).thenReturn(10);
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
        when(config.enableDivinePot()).thenReturn(false);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(config.enableDivinePot()).thenReturn(true);
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
        store.clear();
        reminders.reset();
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
    }
    @Test
    public void liquidAdrenalineExpiresFromClientClockWithoutDependingOnChatAndRearms()
    {
        when(config.enableLiquidAdrenaline()).thenReturn(true);
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(1);
        when(client.getVarcIntValue(VarClientID.BUFF_TOA_MIDRAIDLOOT_ENERGY_END)).thenReturn(1250);
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1000);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1249);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(0);
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1250);
        reminders.onGameTick();
        assertEquals("Drink Liquid Adrenaline", store.snapshot().get(0).message);
        clock.set(15_000_000_000L);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(1);
        when(client.getVarcIntValue(VarClientID.BUFF_TOA_MIDRAIDLOOT_ENERGY_END)).thenReturn(1500);
        reminders.onGameTick();
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1500);
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
    }

    @Test
    public void adrenalineEarlyWarningIsNotRepeatedAndAnotherDoseClearsIt()
    {
        when(config.enableLiquidAdrenaline()).thenReturn(true);
        when(config.liquidAdrenalineEarlyWarning()).thenReturn(20);
        when(config.liquidAdrenalineUseGlobalTimer()).thenReturn(false);
        when(config.liquidAdrenalineDuration()).thenReturn(0);
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(1);
        when(client.getVarcIntValue(VarClientID.BUFF_TOA_MIDRAIDLOOT_ENERGY_END)).thenReturn(1250);
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1216);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1217);
        reminders.onGameTick();
        assertEquals("Liquid Adrenaline", store.snapshot().get(0).key);
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1250);
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(0);
        reminders.onGameTick();
        assertEquals(1, store.snapshot().size());
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(1);
        when(client.getVarcIntValue(VarClientID.BUFF_TOA_MIDRAIDLOOT_ENERGY_END)).thenReturn(1500);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void losingAdrenalineBeforeItsDeadlineDoesNotPretendItExpired()
    {
        when(config.enableLiquidAdrenaline()).thenReturn(true);
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(1);
        when(client.getVarcIntValue(VarClientID.BUFF_TOA_MIDRAIDLOOT_ENERGY_END)).thenReturn(1250);
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1000);
        reminders.onGameTick();
        when(client.getVarpValue(VarPlayerID.MAP_CLOCK)).thenReturn(1100);
        when(client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE)).thenReturn(0);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }
}
