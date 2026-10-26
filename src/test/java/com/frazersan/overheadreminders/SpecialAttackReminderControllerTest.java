package com.frazersan.overheadreminders;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SpecialAttackReminderControllerTest
{
    private final AtomicLong clock = new AtomicLong();
    private final NotificationStore notifications = new NotificationStore(clock::get);
    private final SpecialAttackReminderController controller = new SpecialAttackReminderController();
    private final OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);

    @Before
    public void setup()
    {
        when(config.enableSpecialAttack()).thenReturn(true);
    }

    private void energy(int value) { controller.update(value, config, notifications); }

    @Test
    public void fullOnLoginIsSilentAndFullAfterUseAlertsOnlyOnce()
    {
        energy(1000);
        assertTrue(notifications.snapshot().isEmpty());
        energy(500);
        energy(750);
        energy(999);
        assertTrue(notifications.snapshot().isEmpty());
        energy(1000);
        assertEquals("Special Attack 100%", notifications.snapshot().get(0).message);
        assertEquals(Color.GREEN, notifications.snapshot().get(0).color);
        long expiry = notifications.snapshot().get(0).expiresAt;
        clock.set(5_000_000_000L);
        energy(1000);
        assertEquals(expiry, notifications.snapshot().get(0).expiresAt);
        clock.set(15_000_000_000L);
        assertTrue(notifications.snapshot().isEmpty());
        energy(1000);
        assertTrue(notifications.snapshot().isEmpty());
    }

    @Test
    public void selectedMilestonesCrossExactlyAndReplacePreviousRow()
    {
        when(config.specialAttack25()).thenReturn(true);
        when(config.specialAttack50()).thenReturn(true);
        when(config.specialAttack75()).thenReturn(true);
        energy(0);
        energy(249);
        assertTrue(notifications.snapshot().isEmpty());
        energy(250);
        assertEquals("Special Attack 25%", notifications.snapshot().get(0).message);
        energy(500);
        assertEquals(1, notifications.snapshot().size());
        assertEquals("Special Attack 50%", notifications.snapshot().get(0).message);
        energy(750);
        assertEquals("Special Attack 75%", notifications.snapshot().get(0).message);
        energy(1000);
        assertEquals("Special Attack 100%", notifications.snapshot().get(0).message);
    }

    @Test
    public void largeRestoreShowsHighestSelectedMilestoneAndFullCanBeDeselected()
    {
        when(config.specialAttack25()).thenReturn(true);
        when(config.specialAttack50()).thenReturn(true);
        when(config.specialAttack75()).thenReturn(true);
        when(config.specialAttack100()).thenReturn(false);
        energy(0);
        energy(1000);
        assertEquals(1, notifications.snapshot().size());
        assertEquals("Special Attack 75%", notifications.snapshot().get(0).message);
    }

    @Test
    public void energyUseClearsOnlySpecialAttackAndRearmsIt()
    {
        when(config.specialAttackUseGlobalTimer()).thenReturn(false);
        when(config.specialAttackDuration()).thenReturn(0);
        notifications.show("Thrall", "Cast Thrall", Color.CYAN, 0);
        energy(500);
        energy(1000);
        clock.set(1_000_000_000_000L);
        assertEquals(2, notifications.snapshot().size());
        energy(500);
        assertEquals(1, notifications.snapshot().size());
        assertEquals("Thrall", notifications.snapshot().get(0).key);
        energy(1000);
        assertEquals(2, notifications.snapshot().size());
    }

    @Test
    public void disabledAndSessionResetDoNotReplayRestoredEnergy()
    {
        energy(500);
        energy(1000);
        when(config.enableSpecialAttack()).thenReturn(false);
        energy(500);
        assertTrue(notifications.snapshot().isEmpty());
        when(config.enableSpecialAttack()).thenReturn(true);
        energy(1000);
        assertTrue(notifications.snapshot().isEmpty());
        energy(500);
        controller.reset(notifications);
        energy(1000);
        assertTrue(notifications.snapshot().isEmpty());
    }

    @Test
    public void customMessageColourAndTimerAreRespectedAndBlankSuppresses()
    {
        when(config.specialAttackMessage()).thenReturn("Ready: {percent}%");
        when(config.specialAttackColor()).thenReturn(Color.ORANGE);
        when(config.specialAttackUseGlobalTimer()).thenReturn(false);
        when(config.specialAttackDuration()).thenReturn(5);
        energy(750);
        energy(1000);
        assertEquals("Ready: 100%", notifications.snapshot().get(0).message);
        assertEquals(Color.ORANGE, notifications.snapshot().get(0).color);
        clock.set(5_000_000_000L);
        assertTrue(notifications.snapshot().isEmpty());
        when(config.specialAttackMessage()).thenReturn("");
        energy(750);
        energy(1000);
        assertTrue(notifications.snapshot().isEmpty());
    }
}
