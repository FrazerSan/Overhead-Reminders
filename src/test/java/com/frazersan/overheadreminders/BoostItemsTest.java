package com.frazersan.overheadreminders;

import java.util.EnumSet;
import net.runelite.api.Item;
import net.runelite.api.Skill;
import net.runelite.api.gameval.ItemID;
import org.junit.Test;
import static org.junit.Assert.*;

public class BoostItemsTest
{
    private static EnumSet<Skill> supplies(int id)
    {
        return BoostItems.available(new Item[] {new Item(id, 1)}, true);
    }

    @Test
    public void everyRegularSuperCombatDoseRestoresBothMeleeSkills()
    {
        for (int id : new int[] {ItemID._1DOSE2COMBAT, ItemID._2DOSE2COMBAT,
            ItemID._3DOSE2COMBAT, ItemID._4DOSE2COMBAT})
        {
            assertEquals(EnumSet.of(Skill.ATTACK, Skill.STRENGTH), supplies(id));
        }
        assertEquals(EnumSet.of(Skill.STRENGTH), supplies(ItemID.STRENGTH4));
        assertEquals(EnumSet.of(Skill.ATTACK), supplies(ItemID._1DOSE2ATTACK));
    }

    @Test
    public void magicAndRangedPotionFamiliesAndMixesAreRecognized()
    {
        for (int id : new int[] {ItemID._1DOSE1MAGIC, ItemID._2DOSE1MAGIC,
            ItemID._3DOSE1MAGIC, ItemID._4DOSE1MAGIC, ItemID._4DOSEBATTLEMAGE,
            ItemID._4DOSEANCIENTBREW, ItemID._1DOSEFORGOTTENBREW, ItemID._1DOSEMAGICESS,
            ItemID.BRUTAL_1DOSE1MAGIC, ItemID.RAIDS_VIAL_KODAI_STRONG_1, ItemID.NZONE1DOSE2MAGICPOTION})
        {
            assertTrue("Magic source " + id, supplies(id).contains(Skill.MAGIC));
        }
        for (int id : new int[] {ItemID._1DOSERANGERSPOTION, ItemID._2DOSERANGERSPOTION,
            ItemID._3DOSERANGERSPOTION, ItemID._4DOSERANGERSPOTION, ItemID._1DOSEBASTION,
            ItemID._1DOSEARMADYLBREW, ItemID.BRUTAL_1DOSERANGERSPOTION,
            ItemID.RAIDS_VIAL_TWISTED_STRONG_1, ItemID.NZONE1DOSE2RANGERSPOTION})
        {
            assertTrue("Ranged source " + id, supplies(id).contains(Skill.RANGED));
        }
    }

    @Test
    public void divineSaturatedAndOtherFixedBoostSuppliesDoNotQualify()
    {
        for (int id : new int[] {ItemID._4DOSEDIVINECOMBAT, ItemID._1DOSEDIVINEMAGIC,
            ItemID._1DOSEDIVINERANGE, ItemID._4DOSEDIVINEBATTLEMAGE,
            ItemID._1DOSEDIVINEBASTION, ItemID.SATURATED_HEART, ItemID.TOA_SUPPLY_STATS_1,
            ItemID.NZONE1DOSEOVERLOADPOTION, ItemID.VIAL_EMPTY})
        {
            assertTrue("Excluded source " + id, supplies(id).isEmpty());
        }
    }

    @Test
    public void onlyEdibleFullAndHalfPiesQualifyForTheirSkills()
    {
        assertEquals(EnumSet.of(Skill.SLAYER), supplies(ItemID.WILD_PIE));
        assertEquals(EnumSet.of(Skill.SLAYER), supplies(ItemID.HALF_WILD_PIE));
        assertEquals(EnumSet.of(Skill.AGILITY), supplies(ItemID.SUMMER_PIE));
        assertEquals(EnumSet.of(Skill.AGILITY), supplies(ItemID.HALF_SUMMER_PIE));
        assertTrue(supplies(ItemID.UNCOOKED_WILD_PIE).isEmpty());
    }

    @Test
    public void imbuedHeartRequiresReadinessAndInvalidItemsAreIgnored()
    {
        Item[] heart = {new Item(ItemID.IMBUED_HEART, 1)};
        assertTrue(BoostItems.available(heart, false).isEmpty());
        assertEquals(EnumSet.of(Skill.MAGIC), BoostItems.available(heart, true));
        assertTrue(BoostItems.available(null, true).isEmpty());
        assertTrue(BoostItems.available(new Item[] {null, new Item(-1, 0),
            new Item(ItemID._4DOSE2COMBAT, 0)}, true).isEmpty());
    }
}
