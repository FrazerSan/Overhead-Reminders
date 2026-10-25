package com.frazersan.overheadreminders;

import java.awt.Color;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ReminderPreviewTest
{
    @Test
    public void previewShowsTopItemOfEachSectionEvenWhenDisabledAndDoesNotTouchRealAlerts()
    {
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        NotificationStore real = new NotificationStore(() -> 0L);
        real.show("Low Prayer", "Actual alert", Color.RED, 5);
        ReminderPreview preview = new ReminderPreview();
        preview.start(config);
        String[] keys = {"Low Prayer", "Divine Pot", "Soulflamehorn", "Thrall", "Surge", "Overload"};
        assertEquals(6, preview.snapshot().size());
        for (int i = 0; i < keys.length; i++)
        {
            assertEquals(keys[i], preview.snapshot().get(i).key);
            assertEquals(Long.MAX_VALUE, preview.snapshot().get(i).expiresAt);
        }
        preview.stop();
        assertFalse(preview.active());
        assertTrue(preview.snapshot().isEmpty());
        assertEquals("Actual alert", real.snapshot().get(0).message);
    }

    @Test
    public void previewRefreshUsesCurrentTextAndColourAndBlankMessagesKeepSixRows()
    {
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        ReminderPreview preview = new ReminderPreview();
        preview.start(config);
        when(config.divinePotMessage()).thenReturn("My divine {0}");
        when(config.divinePotColor()).thenReturn(Color.BLUE);
        when(config.lowPrayerMessage()).thenReturn("");
        preview.refresh(config);
        assertEquals(6, preview.snapshot().size());
        assertEquals("Low Prayer", preview.snapshot().get(0).message);
        assertEquals("My divine Divine Pot", preview.snapshot().get(1).message);
        assertEquals(Color.BLUE, preview.snapshot().get(1).color);
        preview.stop();
        preview.refresh(config);
        assertTrue(preview.snapshot().isEmpty());
    }
}
