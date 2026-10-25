package com.frazersan.overheadreminders;

import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReminderTypeTest
{
    @Test
    public void everyReminderUsesGlobalTimerByDefaultIncludingZero()
    {
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        when(config.overheadDisplayTime()).thenReturn(7);
        for (ReminderType type : ReminderType.values()) assertEquals(type.key, 7, type.duration(config));
        when(config.overheadDisplayTime()).thenReturn(0);
        for (ReminderType type : ReminderType.values()) assertEquals(type.key, 0, type.duration(config));
    }

    @Test
    public void customTimerOverridesGlobalAndSwitchingBackLeavesItsValueIntact()
    {
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        when(config.overheadDisplayTime()).thenReturn(2);
        when(config.divinePotDuration()).thenReturn(0);
        assertEquals(2, ReminderType.DIVINE.duration(config));
        when(config.divinePotUseGlobalTimer()).thenReturn(false);
        assertEquals(0, ReminderType.DIVINE.duration(config));
        AtomicLong clock = new AtomicLong();
        NotificationStore store = new NotificationStore(clock::get);
        ReminderType.DIVINE.show(config, store);
        ReminderType.STAMINA.show(config, store);
        clock.set(300_000_000_000L);
        assertEquals(1, store.snapshot().size());
        assertEquals("Divine Pot", store.snapshot().get(0).key);
        when(config.divinePotUseGlobalTimer()).thenReturn(true);
        assertEquals(2, ReminderType.DIVINE.duration(config));
        assertEquals(0, config.divinePotDuration());
    }
}
