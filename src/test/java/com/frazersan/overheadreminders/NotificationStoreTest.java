package com.frazersan.overheadreminders;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import static org.junit.Assert.*;

public class NotificationStoreTest
{
    @Test
    public void simultaneousTypesRetainOrderColorAndIndependentExpiry()
    {
        AtomicLong clock = new AtomicLong();
        NotificationStore store = new NotificationStore(clock::get);
        store.show("Low Prayer", "{0}!", Color.RED, 2);
        store.show("Thrall", "Thrall expired", Color.CYAN, 5);
        assertEquals(2, store.snapshot().size());
        assertEquals("Low Prayer!", store.snapshot().get(0).message);
        assertEquals(Color.CYAN, store.snapshot().get(1).color);
        clock.set(2_000_000_000L);
        assertEquals(1, store.snapshot().size());
        assertEquals("Thrall", store.snapshot().get(0).key);
        clock.set(5_000_000_000L);
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void duplicateDoesNotAddLineOrRefreshExpiry()
    {
        AtomicLong clock = new AtomicLong();
        NotificationStore store = new NotificationStore(clock::get);
        store.show("Horn", "first", Color.RED, 3);
        clock.set(2_000_000_000L);
        store.show("Horn", "duplicate", Color.BLUE, 10);
        assertEquals(1, store.snapshot().size());
        assertEquals("first", store.snapshot().get(0).message);
        clock.set(3_000_000_000L);
        assertTrue(store.snapshot().isEmpty());
        store.show("Horn", "new event", Color.BLUE, 3);
        assertEquals("new event", store.snapshot().get(0).message);
    }

    @Test
    public void blankOrTargetedRemovalDoesNotClearOtherTypes()
    {
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("Prayer", "low", Color.RED, 3);
        store.show("Horn", "active", Color.BLUE, 3);
        store.show("Horn", " ", Color.BLUE, 3);
        assertEquals(1, store.snapshot().size());
        store.remove("missing");
        assertEquals("Prayer", store.snapshot().get(0).key);
        store.clear();
        assertTrue(store.snapshot().isEmpty());
    }

    @Test
    public void formatsCountdownAndSanitizesMultilineMarkup()
    {
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("Prayer", "<col=ff0000>Low\nprayer</col>", null, 3);
        NotificationStore.Entry entry = store.snapshot().get(0);
        assertEquals("Low prayer", entry.displayText(0L, false));
        assertEquals("Low prayer (3s)", entry.displayText(1L, true));
        assertEquals("Low prayer (1s)", entry.displayText(2_999_999_999L, true));
        assertEquals(Color.WHITE, entry.color);
    }
}
