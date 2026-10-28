package com.frazersan.overheadreminders;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.gameval.VarbitID;
import net.runelite.api.gameval.VarClientID;
import net.runelite.api.gameval.VarPlayerID;

/** One warning per effect cycle, shared by early warnings and chat expiry events. */
final class TimedReminderController
{
    private static final class Counter
    {
        final IntSupplier value;
        final IntSupplier unitTicks;
        int previous;
        int changedAt;

        Counter(IntSupplier value, IntSupplier unitTicks)
        {
            this.value = value;
            this.unitTicks = unitTicks;
        }
    }

    private static final class Effect
    {
        final ReminderType type;
        final List<Counter> counters = new ArrayList<>();
        boolean warned;
        boolean latestExpiry;

        Effect(ReminderType type) { this.type = type; }
    }

    private int adrenalineEnd;
    private final Client client;
    private final OverheadNotificationsConfig config;
    private final NotificationStore notifications;
    private final Map<ReminderType, Effect> effects = new EnumMap<>(ReminderType.class);

    TimedReminderController(Client client, OverheadNotificationsConfig config, NotificationStore notifications)
    {
        this.client = client;
        this.config = config;
        this.notifications = notifications;
        for (int id : new int[] {VarbitID.DIVINEATTACK_POTION_TIME, VarbitID.DIVINESTRENGTH_POTION_TIME,
            VarbitID.DIVINEDEFENCE_POTION_TIME, VarbitID.DIVINECOMBAT_POTION_TIME,
            VarbitID.DIVINEMAGIC_POTION_TIME, VarbitID.DIVINEBATTLEMAGE_POTION_TIME,
            VarbitID.DIVINERANGE_POTION_TIME, VarbitID.DIVINEBASTION_POTION_TIME})
        {
            varbit(ReminderType.DIVINE, id, 1);
        }
        // Saturated Heart's effect and recharge end together. The shared heart
        // recharge counter is in ten-tick units, unlike Divine's one-tick counters.
        add(ReminderType.HEART, () -> client.getVarbitValue(VarbitID.SATURATED_HEART_TIME) > 0
            ? Math.max(1, client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER)) : 0, () -> 10);
        add(ReminderType.STAMINA, () -> client.getVarbitValue(VarbitID.STAMINA_ACTIVE) > 0
            ? Math.max(1, client.getVarbitValue(VarbitID.STAMINA_DURATION)) : 0, () -> 10);
        varbit(ReminderType.PRAYER_REGEN, VarbitID.PRAYER_REGENERATION_POTION_TIMER, 12);
        add(ReminderType.PRAYER_ENHANCE, () -> client.getVarbitValue(VarbitID.RAIDS_PRAYERENHANCE_TIMER),
            () -> Math.max(1, client.getVarbitValue(VarbitID.RAIDS_PRAYERENHANCE_RATE)));
        varbit(ReminderType.OVERLOAD, VarbitID.NZONE_OVERLOAD_POTION_EFFECTS, 25);
        varbit(ReminderType.OVERLOAD, VarbitID.RAIDS_OVERLOAD_TIMER, 25);
        varbit(ReminderType.OVERLOAD, VarbitID.DEADMAN_OVERLOAD_POTION_EFFECTS, 25);
        varbit(ReminderType.SALTS, VarbitID.TOA_MIDRAIDLOOT_STATS_TIMER, 25);
        varbit(ReminderType.SURGE, VarbitID.SURGE_POTION_TIMER, 10);
        varbit(ReminderType.GOADING, VarbitID.GOADING_POTION_TIMER, 6);
        add(ReminderType.ADRENALINE, this::adrenalineTicks, () -> 1);
        varbit(ReminderType.ANTIFIRE, VarbitID.ANTIFIRE_POTION, 30);
        varbit(ReminderType.ANTIFIRE, VarbitID.SUPER_ANTIFIRE_POTION, 20);
        effects.get(ReminderType.ANTIFIRE).latestExpiry = true;
        add(ReminderType.ANTIPOISON, () -> Math.max(0, -client.getVarpValue(VarPlayerID.POISON)), () -> 30);
        add(ReminderType.VENOM, () -> Math.max(0, -client.getVarpValue(VarPlayerID.POISON) - 38), () -> 30);
        add(ReminderType.THRALL, () -> spellTicks(VarClientID.BUFF_RESURRECTION_ACTIVE_START,
            client.getVarcIntValue(VarClientID.BUFF_RESURRECTION_ACTIVE_DURATION) - 3), () -> 1);
        add(ReminderType.MARK, () -> spellTicks(VarClientID.BUFF_MARK_OF_DARKNESS_START,
            client.getVarcIntValue(VarClientID.BUFF_MARK_OF_DARKNESS_DURATION)), () -> 1);
        add(ReminderType.WARD, () -> spellTicks(VarClientID.BUFF_WARD_OF_ARCEUUS_ACTIVE_START,
            client.getRealSkillLevel(Skill.MAGIC)), () -> 1);
    }

    private int adrenalineTicks()
    {
        if (client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_ENERGY_ACTIVE) <= 0) return 0;
        adrenalineEnd = client.getVarcIntValue(VarClientID.BUFF_TOA_MIDRAIDLOOT_ENERGY_END);
        return Math.max(0, adrenalineEnd - client.getVarpValue(VarPlayerID.MAP_CLOCK));
    }

    private int spellTicks(int startVarc, int duration)
    {
        int start = client.getVarcIntValue(startVarc);
        int elapsed = client.getVarpValue(VarPlayerID.MAP_CLOCK) - start;
        return start > 0 && elapsed >= 0 ? Math.max(0, duration - elapsed) : 0;
    }

    private void varbit(ReminderType type, int id, int unit)
    {
        add(type, () -> client.getVarbitValue(id), () -> unit);
    }

    private void add(ReminderType type, IntSupplier value, IntSupplier unit)
    {
        effects.computeIfAbsent(type, Effect::new).counters.add(new Counter(value, unit));
    }

    void onGameTick()
    {
        int tick = client.getTickCount();
        for (Effect effect : effects.values())
        {
            int remaining = effect.latestExpiry ? 0 : Integer.MAX_VALUE;
            boolean active = false;
            boolean wasActive = false;
            boolean refreshed = false;
            boolean reachedEnd = false;
            for (Counter counter : effect.counters)
            {
                wasActive |= counter.previous > 0;
                int current = Math.max(0, counter.value.getAsInt());
                reachedEnd |= current == 0 && counter.previous > 0 && (counter.previous == 1
                    || tick - counter.changedAt >= counter.previous * counter.unitTicks.getAsInt());
                refreshed |= current > counter.previous;
                if (current != counter.previous) counter.changedAt = tick;
                if (current > 0)
                {
                    active = true;
                    // Follow observed counter boundaries between coarse server updates.
                    // Clamp above zero until the server confirms that the effect ended.
                    int ticks = Math.max(1, current * counter.unitTicks.getAsInt()
                        - Math.max(0, tick - counter.changedAt));
                    remaining = effect.latestExpiry ? Math.max(remaining, ticks) : Math.min(remaining, ticks);
                }
                counter.previous = current;
            }
            if (!effect.type.enabled(config))
            {
                effect.warned = false;
                notifications.remove(effect.type.key);
                continue;
            }
            if (refreshed)
            {
                effect.warned = false;
                notifications.remove(effect.type.key);
            }
            if (effect.type == ReminderType.ADRENALINE && wasActive && !active)
            {
                if (adrenalineEnd > 0 && client.getVarpValue(VarPlayerID.MAP_CLOCK) >= adrenalineEnd)
                {
                    if (!effect.warned)
                    {
                        effect.type.show(config, notifications);
                        effect.warned = true;
                    }
                }
                else
                {
                    // Leaving the raid or losing the effect early is not a natural expiry.
                    notifications.remove(effect.type.key);
                    effect.warned = false;
                }
            }
            if (effect.type == ReminderType.GOADING && wasActive && !active)
            {
                // A final decrement (or elapsed deadline after missed ticks) is natural expiry.
                // Removing the effect earlier must not produce a fresh reminder.
                if (reachedEnd)
                {
                    if (!effect.warned) effect.type.show(config, notifications);
                    effect.warned = true;
                }
                else
                {
                    notifications.remove(effect.type.key);
                    effect.warned = false;
                }
            }
            if (active && !effect.warned)
            {
                notifications.remove(effect.type.key);
                int early = effect.type.earlySeconds(config);
                if (early > 0 && remaining * 600L <= early * 1000L)
                {
                    effect.type.show(config, notifications);
                    effect.warned = true;
                }
            }
        }
    }

    /** Chat expiry must not add or refresh a warning already issued early. */
    void onExpiry(ReminderType type)
    {
        Effect effect = effects.get(type);
        if (effect == null)
        {
            type.show(config, notifications);
        }
        else if (!effect.warned)
        {
            type.show(config, notifications);
            effect.warned = true;
        }
    }

    void reapplied(ReminderType type)
    {
        notifications.remove(type.key);
        Effect effect = effects.get(type);
        if (effect != null) effect.warned = false;
    }

    void reset()
    {
        adrenalineEnd = 0;
        for (Effect effect : effects.values())
        {
            effect.warned = false;
            for (Counter counter : effect.counters)
            {
                counter.previous = 0;
                counter.changedAt = 0;
            }
        }
    }
}
