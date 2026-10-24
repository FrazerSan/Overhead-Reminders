package com.shuddie.overhead;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import static org.junit.Assert.*;

public class ReminderStateTest
{
    @Test
    public void prayerWarnsAtThresholdOnceUntilRestoredAboveIt()
    {
        LowPrayerState state = new LowPrayerState();
        assertFalse(state.update(21, 20));
        assertTrue(state.update(20, 20));
        assertFalse(state.update(19, 20));
        assertFalse(state.update(0, 20));
        assertFalse(state.update(20, 20));
        assertFalse(state.update(21, 20));
        assertTrue(state.update(20, 20));
    }

    @Test
    public void initiallyLowAndResetSessionsWarnAndZeroThresholdWorks()
    {
        LowPrayerState state = new LowPrayerState();
        assertTrue(state.update(0, 0));
        assertFalse(state.update(0, 0));
        state.reset();
        assertTrue(state.update(0, 0));
    }

    @Test
    public void deathChargeExpiresAtSixtySecondsAndRestartReplacesDeadline()
    {
        AtomicLong clock = new AtomicLong();
        DeathChargeTimer timer = new DeathChargeTimer(clock::get);
        assertEquals(DeathChargeTimerState.IDLE, timer.getState());
        timer.start();
        clock.set(59_999_999_999L);
        assertEquals(DeathChargeTimerState.ACTIVE, timer.getState());
        clock.set(60_000_000_000L);
        assertEquals(DeathChargeTimerState.EXPIRED, timer.getState());
        timer.start();
        clock.set(100_000_000_000L);
        assertEquals(DeathChargeTimerState.ACTIVE, timer.getState());
        timer.stop();
        clock.set(200_000_000_000L);
        assertEquals(DeathChargeTimerState.IDLE, timer.getState());
    }
}
