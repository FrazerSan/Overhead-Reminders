package com.frazersan.overheadreminders;

import net.runelite.api.Skill;

/** Stable notification keys keep each boost reminder independent in the stack. */
enum BoostReminderType
{
    MELEE("Melee Boost", true, Skill.STRENGTH),
    MAGIC("Magic Boost", true, Skill.MAGIC),
    RANGED("Ranged Boost", true, Skill.RANGED),
    WILD_PIE("Wild Pie", false, Skill.SLAYER),
    SUMMER_PIE("Summer Pie", false, Skill.AGILITY);

    final String key;
    final boolean combat;
    final Skill[] skills;

    BoostReminderType(String key, boolean combat, Skill... skills)
    {
        this.key = key;
        this.combat = combat;
        this.skills = skills;
    }
}
