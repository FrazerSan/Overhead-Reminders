package com.frazersan.overheadreminders;

import java.awt.Color;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.LongSupplier;
import javax.inject.Inject;
import javax.inject.Singleton;

/** One independently expiring entry per type. Duplicate events never extend its life. */
@Singleton
public class NotificationStore
{
    private static final long NO_EXPIRY = Long.MAX_VALUE;

    static final class Entry
    {
        final String key;
        final String message;
        final Color color;
        final long expiresAt;

        Entry(String key, String message, Color color, long expiresAt)
        {
            this.key = key;
            this.message = message;
            this.color = color;
            this.expiresAt = expiresAt;
        }

        String displayText(long now, boolean countdown)
        {
            return displayText(now, countdown, message);
        }

        String displayText(long now, boolean countdown, String text)
        {
            return countdown && expiresAt != NO_EXPIRY ? text + " (" + Math.max(1, (expiresAt - now + 999_999_999L) / 1_000_000_000L) + "s)" : text;
        }
    }

    private final Map<String, Entry> entries = new LinkedHashMap<>();
    private final LongSupplier clock;

    @Inject
    public NotificationStore()
    {
        this(System::nanoTime);
    }

    NotificationStore(LongSupplier clock)
    {
        this.clock = clock;
    }

    synchronized void show(String key, String template, Color color, int seconds)
    {
        prune();
        if (template == null || template.trim().isEmpty())
        {
            entries.remove(key);
            return;
        }
        if (!entries.containsKey(key))
        {
            String message = template.replace("{0}", key).replaceAll("<[^>]*>", "")
                .replaceAll("[\r\n\t]+", " ").trim();
            if (!message.isEmpty())
            {
                entries.put(key, new Entry(key, message, color == null ? Color.WHITE : color,
                    seconds == 0 ? NO_EXPIRY : clock.getAsLong() + Math.max(1, Math.min(300, seconds)) * 1_000_000_000L));
            }
        }
    }

    synchronized List<Entry> snapshot()
    {
        prune();
        return new ArrayList<>(entries.values());
    }

    long now()
    {
        return clock.getAsLong();
    }

    synchronized void remove(String key)
    {
        entries.remove(key);
    }

    synchronized void clear()
    {
        entries.clear();
    }

    private void prune()
    {
        long now = clock.getAsLong();
        entries.values().removeIf(entry -> entry.expiresAt != NO_EXPIRY && now >= entry.expiresAt);
    }
}
