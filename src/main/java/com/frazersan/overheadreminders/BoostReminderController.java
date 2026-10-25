/*
 * Copyright (c) 2022, Adam <Adam@sigterm.info>
 * Copyright (c) 2022, Ankou <https://github.com/AnkouOSRS>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.frazersan.overheadreminders;

import java.awt.Color;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.VarbitID;

/** Samples complete game ticks so inventory, XP and boost changes from one action agree. */
final class BoostReminderController
{
    private static final List<Skill> TRACKED = Arrays.asList(Skill.ATTACK, Skill.STRENGTH,
        Skill.DEFENCE, Skill.MAGIC, Skill.RANGED, Skill.SLAYER, Skill.AGILITY);

    private static final class Rule
    {
        final BoostReminderType type;
        final BooleanSupplier enabled;
        final IntSupplier threshold;
        final IntSupplier timeout;
        final Supplier<String> message;
        final Supplier<Color> color;
        boolean notified;
        boolean trackingPie;

        Rule(BoostReminderType type, BooleanSupplier enabled, IntSupplier threshold,
            IntSupplier timeout, Supplier<String> message, Supplier<Color> color)
        {
            this.type = type;
            this.enabled = enabled;
            this.threshold = threshold;
            this.timeout = timeout;
            this.message = message;
            this.color = color;
        }
    }

    private final Client client;
    private final OverheadNotificationsConfig config;
    private final NotificationStore notifications;
    private final List<Rule> rules;
    private final Map<Skill, Integer> experience = new EnumMap<>(Skill.class);
    private final Map<Skill, Integer> bonuses = new EnumMap<>(Skill.class);
    private boolean combatActivity;
    // A Divine expiry belongs to the Divine reminder until another boost is applied.
    private final EnumSet<Skill> divineEpisodes = EnumSet.noneOf(Skill.class);
    private final EnumSet<Skill> boostedAgain = EnumSet.noneOf(Skill.class);
    private EnumSet<Skill> previousSupplies = EnumSet.noneOf(Skill.class);

    @Inject
    BoostReminderController(Client client, OverheadNotificationsConfig config, NotificationStore notifications)
    {
        this.client = client;
        this.config = config;
        this.notifications = notifications;
        rules = Arrays.asList(
            new Rule(BoostReminderType.MELEE, config::meleeBoostEnabled, config::meleeBoostThreshold,
                config::meleeBoostTimeout, config::meleeBoostMessage, config::meleeBoostColor),
            new Rule(BoostReminderType.MAGIC, config::magicBoostEnabled, config::magicBoostThreshold,
                config::magicBoostTimeout, config::magicBoostMessage, config::magicBoostColor),
            new Rule(BoostReminderType.RANGED, config::rangedBoostEnabled, config::rangedBoostThreshold,
                config::rangedBoostTimeout, config::rangedBoostMessage, config::rangedBoostColor),
            new Rule(BoostReminderType.WILD_PIE, config::wildPieEnabled, config::wildPieThreshold,
                config::wildPieTimeout, config::wildPieMessage, config::wildPieColor),
            new Rule(BoostReminderType.SUMMER_PIE, config::summerPieEnabled, config::summerPieThreshold,
                config::summerPieTimeout, config::summerPieMessage, config::summerPieColor));
    }

    void onStatChanged(StatChanged event)
    {
        Skill skill = event.getSkill();
        if (!TRACKED.contains(skill) || client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }
        Integer previousXp = experience.put(skill, event.getXp());
        sampleBonus(skill, event.getLevel(), event.getBoostedLevel());
        if (previousXp != null && event.getXp() > previousXp && client.getLocalPlayer() != null
            && client.getLocalPlayer().getInteracting() != null)
        {
            if (isCombatSkill(skill))
            {
                combatActivity = true;
            }
        }
    }

    void onGameTick()
    {
        if (client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer() == null)
        {
            return;
        }
        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        if (inventory == null)
        {
            combatActivity = false;
            boostedAgain.clear();
            return;
        }
        for (Skill skill : TRACKED)
        {
            experience.putIfAbsent(skill, client.getSkillExperience(skill));
            sampleBonus(skill, client.getRealSkillLevel(skill), client.getBoostedSkillLevel(skill));
        }
        for (Skill skill : TRACKED)
        {
            if (!config.enableDivinePot())
            {
                divineEpisodes.remove(skill);
            }
            else if (divineBoostActive(skill))
            {
                divineEpisodes.add(skill);
            }
            else if (boostedAgain.contains(skill))
            {
                divineEpisodes.remove(skill);
            }
        }
        EnumSet<Skill> available = BoostItems.available(inventory.getItems(),
            client.getVarbitValue(VarbitID.IMBUED_HEART_TIMER) == 0);
        for (Rule rule : rules)
        {
            update(rule, available);
        }
        previousSupplies = available;
        combatActivity = false;
        boostedAgain.clear();
    }

    private void sampleBonus(Skill skill, int realLevel, int boostedLevel)
    {
        if (realLevel <= 0)
        {
            return;
        }
        int bonus = boostedLevel - realLevel;
        Integer previous = bonuses.put(skill, bonus);
        if (previous != null && bonus < previous && isCombatSkill(skill)
            && client.getLocalPlayer() != null && client.getLocalPlayer().getInteracting() != null)
        {
            combatActivity = true;
        }
        if (previous != null && bonus > 0 && bonus > previous)
        {
            boostedAgain.add(skill);
        }
    }

    private void update(Rule rule, EnumSet<Skill> available)
    {
        if (!rule.enabled.getAsBoolean())
        {
            clear(rule);
            return;
        }
        boolean low = false;
        boolean eligible = false;
        boolean active = !rule.type.combat || combatActivity;
        boolean reapplied = false;
        for (Skill skill : rule.type.skills)
        {
            if (!bonuses.containsKey(skill))
            {
                continue;
            }
            int bonus = bonuses.get(skill);
            boolean supply = available.contains(skill);
            if (!rule.type.combat && bonus > 0 && (supply || previousSupplies.contains(skill)))
            {
                // Continue tracking after the final half-pie is eaten.
                rule.trackingPie = true;
            }
            boolean canRemind = rule.type.combat ? supply && !fixedBoostActive(skill) && !divineEpisodes.contains(skill) : rule.trackingPie;
            eligible |= canRemind;
            low |= canRemind && bonus <= rule.threshold.getAsInt();
            reapplied |= boostedAgain.contains(skill);
        }
        if (!eligible)
        {
            notifications.remove(rule.type.key);
            rule.notified = false;
            return;
        }
        if (reapplied || !low)
        {
            notifications.remove(rule.type.key);
            rule.notified = false;
            return;
        }
        if (active && !rule.notified)
        {
            int timeout = rule.timeout.getAsInt();
            notifications.show(rule.type.key, rule.message.get(), rule.color.get(),
                timeout > 0 ? timeout : config.overheadDisplayTime());
            rule.notified = true;
        }
    }

    private boolean fixedBoostActive(Skill skill)
    {
        if (client.getVarbitValue(VarbitID.NZONE_OVERLOAD_POTION_EFFECTS) > 0
            || client.getVarbitValue(VarbitID.RAIDS_OVERLOAD_TIMER) > 0
            || client.getVarbitValue(VarbitID.TOA_MIDRAIDLOOT_STATS_TIMER) > 0)
        {
            return true;
        }
        return divineBoostActive(skill)
            || (skill == Skill.MAGIC && client.getVarbitValue(VarbitID.SATURATED_HEART_TIME) > 0);
    }

    private boolean divineBoostActive(Skill skill)
    {
        switch (skill)
        {
            case ATTACK:
                return client.getVarbitValue(VarbitID.DIVINEATTACK_POTION_TIME) > 0
                    || client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME) > 0;
            case STRENGTH:
                return client.getVarbitValue(VarbitID.DIVINESTRENGTH_POTION_TIME) > 0
                    || client.getVarbitValue(VarbitID.DIVINECOMBAT_POTION_TIME) > 0;
            case MAGIC:
                return client.getVarbitValue(VarbitID.DIVINEMAGIC_POTION_TIME) > 0
                    || client.getVarbitValue(VarbitID.DIVINEBATTLEMAGE_POTION_TIME) > 0;
            case RANGED:
                return client.getVarbitValue(VarbitID.DIVINERANGE_POTION_TIME) > 0
                    || client.getVarbitValue(VarbitID.DIVINEBASTION_POTION_TIME) > 0;
            default:
                return false;
        }
    }

    private static boolean isCombatSkill(Skill skill)
    {
        return skill == Skill.ATTACK || skill == Skill.STRENGTH || skill == Skill.DEFENCE
            || skill == Skill.MAGIC || skill == Skill.RANGED;
    }
    private void clear(Rule rule)
    {
        notifications.remove(rule.type.key);
        rule.notified = false;
        rule.trackingPie = false;
    }

    void reset()
    {
        for (Rule rule : rules)
        {
            clear(rule);
        }
        experience.clear();
        bonuses.clear();
        combatActivity = false;
        boostedAgain.clear();
        previousSupplies.clear();
        divineEpisodes.clear();
    }
}
