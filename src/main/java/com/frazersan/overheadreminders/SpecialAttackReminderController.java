package com.frazersan.overheadreminders;

/** Observe energy in tenths of a percent. One row shows the highest newly crossed milestone. */
final class SpecialAttackReminderController
{
    private int previous = -1;

    void reset(NotificationStore notifications)
    {
        previous = -1;
        notifications.remove(ReminderType.SPECIAL_ATTACK.key);
    }

    void update(int energy, OverheadNotificationsConfig config, NotificationStore notifications)
    {
        if (!config.enableSpecialAttack())
        {
            reset(notifications);
            return;
        }
        if (energy < 0 || energy > 1000) return;
        if (previous < 0)
        {
            previous = energy;
            return;
        }
        if (energy < previous) notifications.remove(ReminderType.SPECIAL_ATTACK.key);
        int milestone = 0;
        if (config.specialAttack25() && previous < 250 && energy >= 250) milestone = 25;
        if (config.specialAttack50() && previous < 500 && energy >= 500) milestone = 50;
        if (config.specialAttack75() && previous < 750 && energy >= 750) milestone = 75;
        if (config.specialAttack100() && previous < 1000 && energy >= 1000) milestone = 100;
        previous = energy;
        if (milestone == 0) return;
        notifications.remove(ReminderType.SPECIAL_ATTACK.key);
        String message = config.specialAttackMessage();
        notifications.show(ReminderType.SPECIAL_ATTACK.key,
            message == null ? null : message.replace("{percent}", Integer.toString(milestone)),
            config.specialAttackColor(), ReminderType.SPECIAL_ATTACK.duration(config));
    }
}
