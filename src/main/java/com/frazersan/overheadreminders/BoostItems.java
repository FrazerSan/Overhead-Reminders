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

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;

/** Consumable, naturally decaying boosts only. Noted items and empty vials do not qualify.
 * Potion families and stat-change behavior were informed by AnkouOSRS/unpotted-reminder;
 * see LICENSE-unpotted-reminder for the retained upstream notices.
 */
final class BoostItems
{
    private static final Map<Skill, Set<Integer>> ITEMS = new EnumMap<>(Skill.class);

    static
    {
        ITEMS.put(Skill.ATTACK, ids(
            ItemID._3DOSE1ATTACK,
            ItemID._2DOSE1ATTACK,
            ItemID._1DOSE1ATTACK,
            ItemID._3DOSE2ATTACK,
            ItemID._2DOSE2ATTACK,
            ItemID._1DOSE2ATTACK,
            ItemID._3DOSEPOTIONOFZAMORAK,
            ItemID._2DOSEPOTIONOFZAMORAK,
            ItemID._1DOSEPOTIONOFZAMORAK,
            ItemID._4DOSE1ATTACK,
            ItemID._4DOSE2ATTACK,
            ItemID._4DOSEPOTIONOFZAMORAK,
            ItemID._4DOSECOMBAT,
            ItemID._3DOSECOMBAT,
            ItemID._2DOSECOMBAT,
            ItemID._1DOSECOMBAT,
            ItemID.BRUTAL_2DOSE1ATTACK,
            ItemID.BRUTAL_1DOSE1ATTACK,
            ItemID.BRUTAL_2DOSECOMBAT,
            ItemID.BRUTAL_1DOSECOMBAT,
            ItemID.BRUTAL_2DOSE2ATTACK,
            ItemID.BRUTAL_1DOSE2ATTACK,
            ItemID.BRUTAL_2DOSEPOTIONOFZAMORAK,
            ItemID.BRUTAL_1DOSEPOTIONOFZAMORAK,
            ItemID._4DOSE2COMBAT,
            ItemID._3DOSE2COMBAT,
            ItemID._2DOSE2COMBAT,
            ItemID._1DOSE2COMBAT,
            ItemID.RAIDS_VIAL_ELDER_WEAK_1,
            ItemID.RAIDS_VIAL_ELDER_WEAK_2,
            ItemID.RAIDS_VIAL_ELDER_WEAK_3,
            ItemID.RAIDS_VIAL_ELDER_WEAK_4,
            ItemID.RAIDS_VIAL_ELDER_1,
            ItemID.RAIDS_VIAL_ELDER_2,
            ItemID.RAIDS_VIAL_ELDER_3,
            ItemID.RAIDS_VIAL_ELDER_4,
            ItemID.RAIDS_VIAL_ELDER_STRONG_1,
            ItemID.RAIDS_VIAL_ELDER_STRONG_2,
            ItemID.RAIDS_VIAL_ELDER_STRONG_3,
            ItemID.RAIDS_VIAL_ELDER_STRONG_4,
            ItemID.BR_1DOSE2COMBAT,
            ItemID._4DOSEMOONLIGHTPOTION,
            ItemID._3DOSEMOONLIGHTPOTION,
            ItemID._2DOSEMOONLIGHTPOTION,
            ItemID._1DOSEMOONLIGHTPOTION));
        ITEMS.put(Skill.STRENGTH, ids(
            ItemID.STRENGTH4,
            ItemID._3DOSE1STRENGTH,
            ItemID._2DOSE1STRENGTH,
            ItemID._1DOSE1STRENGTH,
            ItemID._3DOSE2STRENGTH,
            ItemID._2DOSE2STRENGTH,
            ItemID._1DOSE2STRENGTH,
            ItemID._4DOSE2STRENGTH,
            ItemID._4DOSECOMBAT,
            ItemID._3DOSECOMBAT,
            ItemID._2DOSECOMBAT,
            ItemID._1DOSECOMBAT,
            ItemID.BRUTAL_1DOSE1STRENGTH,
            ItemID.BRUTAL_2DOSE1STRENGTH,
            ItemID.BRUTAL_2DOSECOMBAT,
            ItemID.BRUTAL_1DOSECOMBAT,
            ItemID.BRUTAL_2DOSE2STRENGTH,
            ItemID.BRUTAL_1DOSE2STRENGTH,
            ItemID._4DOSE2COMBAT,
            ItemID._3DOSE2COMBAT,
            ItemID._2DOSE2COMBAT,
            ItemID._1DOSE2COMBAT,
            ItemID.RAIDS_VIAL_ELDER_WEAK_1,
            ItemID.RAIDS_VIAL_ELDER_WEAK_2,
            ItemID.RAIDS_VIAL_ELDER_WEAK_3,
            ItemID.RAIDS_VIAL_ELDER_WEAK_4,
            ItemID.RAIDS_VIAL_ELDER_1,
            ItemID.RAIDS_VIAL_ELDER_2,
            ItemID.RAIDS_VIAL_ELDER_3,
            ItemID.RAIDS_VIAL_ELDER_4,
            ItemID.RAIDS_VIAL_ELDER_STRONG_1,
            ItemID.RAIDS_VIAL_ELDER_STRONG_2,
            ItemID.RAIDS_VIAL_ELDER_STRONG_3,
            ItemID.RAIDS_VIAL_ELDER_STRONG_4,
            ItemID.BR_1DOSE2COMBAT,
            ItemID._4DOSEMOONLIGHTPOTION,
            ItemID._3DOSEMOONLIGHTPOTION,
            ItemID._2DOSEMOONLIGHTPOTION,
            ItemID._1DOSEMOONLIGHTPOTION));
        ITEMS.put(Skill.MAGIC, ids(
            ItemID._4DOSE1MAGIC,
            ItemID._3DOSE1MAGIC,
            ItemID._2DOSE1MAGIC,
            ItemID._1DOSE1MAGIC,
            ItemID._4DOSEMAGICESS,
            ItemID._3DOSEMAGICESS,
            ItemID._2DOSEMAGICESS,
            ItemID._1DOSEMAGICESS,
            ItemID.BRUTAL_2DOSEMAGICESS,
            ItemID.BRUTAL_1DOSEMAGICESS,
            ItemID.BRUTAL_2DOSE1MAGIC,
            ItemID.BRUTAL_1DOSE1MAGIC,
            ItemID.NZONE4DOSE2MAGICPOTION,
            ItemID.NZONE3DOSE2MAGICPOTION,
            ItemID.NZONE2DOSE2MAGICPOTION,
            ItemID.NZONE1DOSE2MAGICPOTION,
            ItemID.RAIDS_VIAL_KODAI_WEAK_1,
            ItemID.RAIDS_VIAL_KODAI_WEAK_2,
            ItemID.RAIDS_VIAL_KODAI_WEAK_3,
            ItemID.RAIDS_VIAL_KODAI_WEAK_4,
            ItemID.RAIDS_VIAL_KODAI_1,
            ItemID.RAIDS_VIAL_KODAI_2,
            ItemID.RAIDS_VIAL_KODAI_3,
            ItemID.RAIDS_VIAL_KODAI_4,
            ItemID.RAIDS_VIAL_KODAI_STRONG_1,
            ItemID.RAIDS_VIAL_KODAI_STRONG_2,
            ItemID.RAIDS_VIAL_KODAI_STRONG_3,
            ItemID.RAIDS_VIAL_KODAI_STRONG_4,
            ItemID._4DOSEBATTLEMAGE,
            ItemID._3DOSEBATTLEMAGE,
            ItemID._2DOSEBATTLEMAGE,
            ItemID._1DOSEBATTLEMAGE,
            ItemID._4DOSEANCIENTBREW,
            ItemID._3DOSEANCIENTBREW,
            ItemID._2DOSEANCIENTBREW,
            ItemID._1DOSEANCIENTBREW,
            ItemID.BRUTAL_2DOSEANCIENTBREW,
            ItemID.BRUTAL_1DOSEANCIENTBREW,
            ItemID._4DOSEFORGOTTENBREW,
            ItemID._3DOSEFORGOTTENBREW,
            ItemID._2DOSEFORGOTTENBREW,
            ItemID._1DOSEFORGOTTENBREW));
        ITEMS.put(Skill.RANGED, ids(
            ItemID._3DOSERANGERSPOTION,
            ItemID._2DOSERANGERSPOTION,
            ItemID._1DOSERANGERSPOTION,
            ItemID._4DOSERANGERSPOTION,
            ItemID.BRUTAL_2DOSERANGERSPOTION,
            ItemID.BRUTAL_1DOSERANGERSPOTION,
            ItemID.NZONE4DOSE2RANGERSPOTION,
            ItemID.NZONE3DOSE2RANGERSPOTION,
            ItemID.NZONE2DOSE2RANGERSPOTION,
            ItemID.NZONE1DOSE2RANGERSPOTION,
            ItemID.RAIDS_VIAL_TWISTED_WEAK_1,
            ItemID.RAIDS_VIAL_TWISTED_WEAK_2,
            ItemID.RAIDS_VIAL_TWISTED_WEAK_3,
            ItemID.RAIDS_VIAL_TWISTED_WEAK_4,
            ItemID.RAIDS_VIAL_TWISTED_1,
            ItemID.RAIDS_VIAL_TWISTED_2,
            ItemID.RAIDS_VIAL_TWISTED_3,
            ItemID.RAIDS_VIAL_TWISTED_4,
            ItemID.RAIDS_VIAL_TWISTED_STRONG_1,
            ItemID.RAIDS_VIAL_TWISTED_STRONG_2,
            ItemID.RAIDS_VIAL_TWISTED_STRONG_3,
            ItemID.RAIDS_VIAL_TWISTED_STRONG_4,
            ItemID._4DOSEBASTION,
            ItemID._3DOSEBASTION,
            ItemID._2DOSEBASTION,
            ItemID._1DOSEBASTION,
            ItemID.BR_4DOSERANGERSPOTION,
            ItemID.BR_3DOSERANGERSPOTION,
            ItemID.BR_2DOSERANGERSPOTION,
            ItemID.BR_1DOSERANGERSPOTION,
            ItemID._4DOSEARMADYLBREW,
            ItemID._3DOSEARMADYLBREW,
            ItemID._2DOSEARMADYLBREW,
            ItemID._1DOSEARMADYLBREW));
        ITEMS.put(Skill.SLAYER, ids(ItemID.WILD_PIE, ItemID.HALF_WILD_PIE));
        ITEMS.put(Skill.AGILITY, ids(ItemID.SUMMER_PIE, ItemID.HALF_SUMMER_PIE));
    }

    private static Set<Integer> ids(Integer... values)
    {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(values)));
    }

    static EnumSet<Skill> available(Item[] inventory, boolean imbuedHeartReady)
    {
        EnumSet<Skill> result = EnumSet.noneOf(Skill.class);
        if (inventory == null)
        {
            return result;
        }
        for (Item item : inventory)
        {
            if (item == null || item.getQuantity() <= 0)
            {
                continue;
            }
            for (Map.Entry<Skill, Set<Integer>> entry : ITEMS.entrySet())
            {
                if (entry.getValue().contains(item.getId()))
                {
                    result.add(entry.getKey());
                }
            }
            if (imbuedHeartReady && item.getId() == ItemID.IMBUED_HEART)
            {
                result.add(Skill.MAGIC);
            }
        }
        return result;
    }
}
