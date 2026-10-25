package com.frazersan.overheadreminders;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BoostReminderControllerTest
{
    private Client client;
    private Player player;
    private ItemContainer inventory;
    private OverheadNotificationsConfig config;
    private NotificationStore store;
    private BoostReminderController reminders;
    private AtomicLong clock;
    private final Map<Skill, Integer> xp = new EnumMap<>(Skill.class);

    @Before
    public void setup()
    {
        client = mock(Client.class);
        player = mock(Player.class);
        inventory = mock(ItemContainer.class);
        config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        clock = new AtomicLong();
        store = new NotificationStore(clock::get);
        reminders = new BoostReminderController(client, config, store);
        when(client.getGameState()).thenReturn(GameState.LOGGED_IN);
        when(client.getLocalPlayer()).thenReturn(player);
        when(player.getInteracting()).thenReturn(mock(NPC.class));
        when(client.getItemContainer(InventoryID.INV)).thenReturn(inventory);
        items();
        for (Skill skill : Skill.values())
        {
            when(client.getRealSkillLevel(skill)).thenReturn(90);
            when(client.getBoostedSkillLevel(skill)).thenReturn(90);
            when(client.getSkillExperience(skill)).thenReturn(1000);
            xp.put(skill, 1000);
        }
        reminders.onGameTick();
    }

    private void items(int... ids)
    {
        Item[] items = new Item[ids.length];
        for (int i = 0; i < ids.length; i++)
        {
            items[i] = new Item(ids[i], 1);
        }
        when(inventory.getItems()).thenReturn(items);
    }

    private void bonus(Skill skill, int bonus)
    {
        when(client.getBoostedSkillLevel(skill)).thenReturn(90 + bonus);
    }

    private void activity(Skill skill)
    {
        int nextXp = xp.get(skill) + 10;
        xp.put(skill, nextXp);
        when(client.getSkillExperience(skill)).thenReturn(nextXp);
        reminders.onStatChanged(new StatChanged(skill, nextXp, client.getRealSkillLevel(skill),
            client.getBoostedSkillLevel(skill)));
    }

    private void assertOnly(String key)
    {
        assertEquals(1, store.snapshot().size());
        assertEquals(key, store.snapshot().get(0).key);
    }

    @Test
    public void meleeNeedsCombatActivityAndMatchingInventorySupply()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE2COMBAT);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertOnly("Melee Boost");
    }

    @Test
    public void idleNpcInteractionAndNonCombatXpDoNotTrigger()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE2COMBAT);
        reminders.onGameTick();
        activity(Skill.AGILITY);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(player.getInteracting()).thenReturn(null);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void noInventoryPotionAndWrongPotionDoNotTrigger()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        items(ItemID._4DOSERANGERSPOTION);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void strengthOnlyPotionDoesNotDemandAnAttackBoost()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        when(config.meleeBoostThreshold()).thenReturn(3);
        items(ItemID._4DOSE2STRENGTH);
        bonus(Skill.STRENGTH, 4);
        reminders.onGameTick();
        activity(Skill.ATTACK);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        bonus(Skill.STRENGTH, 3);
        activity(Skill.ATTACK);
        reminders.onGameTick();
        assertOnly("Melee Boost");
    }

    @Test
    public void thresholdIsBonusAboveBaseAndTimeoutDoesNotRepeatUntilReboosted()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        when(config.meleeBoostThreshold()).thenReturn(3);
        when(config.meleeBoostTimeout()).thenReturn(8);
        items(ItemID._1DOSE2STRENGTH);
        bonus(Skill.STRENGTH, 4);
        reminders.onGameTick();
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        bonus(Skill.STRENGTH, 3);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertOnly("Melee Boost");
        clock.set(7_999_999_999L);
        assertOnly("Melee Boost");
        clock.set(8_000_000_000L);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        bonus(Skill.STRENGTH, 15);
        reminders.onGameTick();
        bonus(Skill.STRENGTH, 3);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertOnly("Melee Boost");
    }

    @Test
    public void reboostClearsItsOwnRowWithoutClearingOtherReminders()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        when(config.magicBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE2COMBAT, ItemID._4DOSE1MAGIC);
        activity(Skill.STRENGTH);
        activity(Skill.MAGIC);
        reminders.onGameTick();
        assertEquals(2, store.snapshot().size());
        bonus(Skill.STRENGTH, 15);
        bonus(Skill.ATTACK, 15);
        reminders.onGameTick();
        assertOnly("Magic Boost");
    }

    @Test
    public void restoringDrainedStatToBaseDoesNotPretendItWasReboosted()
    {
        when(config.magicBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE1MAGIC);
        bonus(Skill.MAGIC, -2);
        activity(Skill.MAGIC);
        reminders.onGameTick();
        assertOnly("Magic Boost");
        bonus(Skill.MAGIC, 0);
        reminders.onGameTick();
        assertOnly("Magic Boost");
        bonus(Skill.MAGIC, 4);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void finalPotionDoseAndInventoryRemovalClearCombatWarning()
    {
        when(config.rangedBoostEnabled()).thenReturn(true);
        items(ItemID._1DOSERANGERSPOTION);
        activity(Skill.RANGED);
        reminders.onGameTick();
        assertOnly("Ranged Boost");
        items(ItemID.VIAL_EMPTY);
        bonus(Skill.RANGED, 13);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void oneAttackChecksAllEnabledInventorySupportedCombatBoosts()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        when(config.magicBoostEnabled()).thenReturn(true);
        when(config.rangedBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE2STRENGTH, ItemID._4DOSERANGERSPOTION, ItemID._4DOSE1MAGIC);
        activity(Skill.DEFENCE);
        reminders.onGameTick();
        assertEquals(3, store.snapshot().size());
    }
    @Test
    public void activeDivineAndSaturatedEffectsSuppressDecayingBoostReminder()
    {
        when(config.magicBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE1MAGIC);
        when(client.getVarbitValue(VarbitID.DIVINEMAGIC_POTION_TIME)).thenReturn(100);
        activity(Skill.MAGIC);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.DIVINEMAGIC_POTION_TIME)).thenReturn(0);
        when(client.getVarbitValue(VarbitID.SATURATED_HEART_TIME)).thenReturn(100);
        activity(Skill.MAGIC);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void saltsAndOverloadsSuppressCombatBoostWarnings()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE2COMBAT);
        for (int varbit : new int[] {VarbitID.TOA_MIDRAIDLOOT_STATS_TIMER,
            VarbitID.RAIDS_OVERLOAD_TIMER, VarbitID.NZONE_OVERLOAD_POTION_EFFECTS})
        {
            when(client.getVarbitValue(varbit)).thenReturn(100);
            activity(Skill.STRENGTH);
            reminders.onGameTick();
            assertTrue(store.snapshot().isEmpty());
            when(client.getVarbitValue(varbit)).thenReturn(0);
        }
    }

    @Test
    public void imbuedHeartMustBeReadyAndReboostClearsItsAlert()
    {
        when(config.magicBoostEnabled()).thenReturn(true);
        items(ItemID.IMBUED_HEART);
        when(client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER)).thenReturn(10);
        activity(Skill.MAGIC);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER)).thenReturn(0);
        activity(Skill.MAGIC);
        reminders.onGameTick();
        assertOnly("Magic Boost");
        bonus(Skill.MAGIC, 10);
        when(client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER)).thenReturn(100);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void pieNeedsObservedBoostThenWarnsWithoutCombatAndThroughFinalHalf()
    {
        when(config.wildPieEnabled()).thenReturn(true);
        when(config.wildPieThreshold()).thenReturn(1);
        items(ItemID.HALF_WILD_PIE);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        // Eating the final half updates inventory before the next game tick.
        items(ItemID.VIAL_EMPTY);
        bonus(Skill.SLAYER, 5);
        reminders.onGameTick();
        bonus(Skill.SLAYER, 1);
        reminders.onGameTick();
        assertOnly("Wild Pie");
        clock.set(15_000_000_000L);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void pieReboostClearsAndRearmsAndBothPiesStack()
    {
        when(config.wildPieEnabled()).thenReturn(true);
        when(config.summerPieEnabled()).thenReturn(true);
        items(ItemID.WILD_PIE, ItemID.SUMMER_PIE);
        bonus(Skill.SLAYER, 5);
        bonus(Skill.AGILITY, 5);
        reminders.onGameTick();
        bonus(Skill.SLAYER, 0);
        bonus(Skill.AGILITY, 0);
        reminders.onGameTick();
        assertEquals(2, store.snapshot().size());
        bonus(Skill.SLAYER, 5);
        reminders.onGameTick();
        assertOnly("Summer Pie");
        bonus(Skill.SLAYER, 0);
        reminders.onGameTick();
        assertEquals(2, store.snapshot().size());
    }

    @Test
    public void unrelatedBoostWithoutPieDoesNotStartPieReminder()
    {
        when(config.wildPieEnabled()).thenReturn(true);
        bonus(Skill.SLAYER, 5);
        reminders.onGameTick();
        bonus(Skill.SLAYER, 0);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void resetAndDisablingClearTrackedWarningsAndAllowFreshSession()
    {
        when(config.rangedBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSERANGERSPOTION);
        activity(Skill.RANGED);
        reminders.onGameTick();
        assertOnly("Ranged Boost");
        when(config.rangedBoostEnabled()).thenReturn(false);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(config.rangedBoostEnabled()).thenReturn(true);
        activity(Skill.RANGED);
        reminders.onGameTick();
        assertOnly("Ranged Boost");
        reminders.reset();
        assertTrue(store.snapshot().isEmpty());
        reminders.onGameTick();
        activity(Skill.RANGED);
        reminders.onGameTick();
        assertOnly("Ranged Boost");
    }

    @Test
    public void missingInventoryOrLoggedOutClientDoesNotTrigger()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        items(ItemID._4DOSE2COMBAT);
        when(client.getItemContainer(InventoryID.INV)).thenReturn(null);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        when(client.getGameState()).thenReturn(GameState.LOGIN_SCREEN);
        when(client.getItemContainer(InventoryID.INV)).thenReturn(inventory);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }
    @Test
    public void strengthPotionClearsMeleeEvenWithCombatPotionCarriedAndAttackUnboosted()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        when(config.meleeBoostThreshold()).thenReturn(3);
        items(ItemID._4DOSE2COMBAT, ItemID.STRENGTH4);
        activity(Skill.ATTACK);
        reminders.onGameTick();
        assertOnly("Melee Boost");
        bonus(Skill.STRENGTH, 12);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
        activity(Skill.ATTACK);
        reminders.onGameTick();
        assertTrue("Unboosted Attack must not bring the melee alert back", store.snapshot().isEmpty());
    }

    @Test
    public void boostDrainWhileTargetingChecksEveryAvailableCategoryWithoutXpGain()
    {
        when(config.magicBoostEnabled()).thenReturn(true);
        when(config.rangedBoostEnabled()).thenReturn(true);
        when(config.magicBoostThreshold()).thenReturn(3);
        items(ItemID._4DOSE1MAGIC, ItemID._4DOSERANGERSPOTION);
        bonus(Skill.MAGIC, 4);
        reminders.onGameTick();
        bonus(Skill.MAGIC, 3);
        reminders.onStatChanged(new StatChanged(Skill.MAGIC, 1000, 90, 93));
        reminders.onGameTick();
        assertEquals(2, store.snapshot().size());
    }

    @Test
    public void statDrainWithNoTargetDoesNotTriggerCombatAlerts()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        when(config.meleeBoostThreshold()).thenReturn(3);
        items(ItemID._4DOSE2STRENGTH);
        bonus(Skill.STRENGTH, 4);
        reminders.onGameTick();
        when(player.getInteracting()).thenReturn(null);
        bonus(Skill.STRENGTH, 3);
        reminders.onStatChanged(new StatChanged(Skill.STRENGTH, 1000, 90, 93));
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void defaultCombatDisplayLastsFifteenSecondsWithoutRefreshing()
    {
        when(config.meleeBoostEnabled()).thenReturn(true);
        items(ItemID.STRENGTH4);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        clock.set(3_000_000_000L);
        assertOnly("Melee Boost");
        clock.set(15_000_000_000L);
        activity(Skill.STRENGTH);
        reminders.onGameTick();
        assertTrue(store.snapshot().isEmpty());
    }
}
