package com.frazersan.overheadreminders;

import java.util.List;
import javax.inject.Singleton;
import javax.inject.Inject;

/** Independent samples never alter real notifications, latches or expiry times. */
@Singleton
final class ReminderPreview
{
    private static final ReminderType[] TYPES = {ReminderType.LOW_PRAYER, ReminderType.DIVINE,
        ReminderType.HORN, ReminderType.THRALL, ReminderType.SURGE, ReminderType.OVERLOAD};
    private final NotificationStore samples = new NotificationStore();
    private volatile boolean active;

    @Inject
    ReminderPreview() {}

    void start(OverheadNotificationsConfig config)
    {
        active = true;
        refresh(config);
    }
    void stop() { active = false; samples.clear(); }
    boolean active() { return active; }
    void refresh(OverheadNotificationsConfig config)
    {
        if (!active) return;
        samples.clear();
        for (ReminderType type : TYPES) type.showPreview(config, samples);
    }
    List<NotificationStore.Entry> snapshot() { return samples.snapshot(); }
}
