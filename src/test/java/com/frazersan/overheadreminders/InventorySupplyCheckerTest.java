package com.frazersan.overheadreminders;

import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class InventorySupplyCheckerTest
{
    private Client client;
    private ItemContainer inventory;
    private InventorySupplyChecker supplies;
    private int nextId;

    @Before
    public void setup()
    {
        client = mock(Client.class);
        inventory = mock(ItemContainer.class);
        when(client.getItemContainer(InventoryID.INV)).thenReturn(inventory);
        supplies = new InventorySupplyChecker(client);
        nextId = 500000;
    }

    private int item(String name, boolean drinkable)
    {
        int id = nextId++;
        ItemComposition definition = mock(ItemComposition.class);
        when(definition.getName()).thenReturn(name);
        when(definition.getNote()).thenReturn(-1);
        when(definition.getInventoryActions()).thenReturn(drinkable ? new String[] {"Drink", null, "Drop"} : new String[] {"Use", "Drop"});
        when(client.getItemDefinition(id)).thenReturn(definition);
        when(inventory.getItems()).thenReturn(new Item[] {new Item(id, 1)});
        return id;
    }

    @Test
    public void potionsAndMixesOfAllDosesAndRaidTiersQualifyForMatchingReminder()
    {
        String[][] cases = {
            {"Divine super combat potion(1)", "Divine Pot"},
            {"Divine bastion potion(4)", "Divine Pot"},
            {"Stamina potion(1)", "Stamina"}, {"Stamina mix(2)", "Stamina"},
            {"Extended stamina potion(4)", "Stamina"},
            {"Prayer regeneration potion(3)", "Prayer Regen"},
            {"Prayer enhance (+)(4)", "Prayer Enhance"},
            {"Prayer enhance (-)(1)", "Prayer Enhance"},
            {"Surge potion(1)", "Surge"}, {"Antipoison(4)", "AntiPoison"},
            {"Superantipoison(1)", "AntiPoison"}, {"Antidote++(3)", "AntiPoison"},
            {"Antipoison mix(2)", "AntiPoison"}, {"Antipoison (+)(4)", "AntiPoison"},
            {"Anti-venom+(1)", "Venom"}, {"Extended anti-venom+(4)", "Venom"},
            {"Antifire potion(1)", "AntiFire"}, {"Antifire mix(2)", "AntiFire"},
            {"Extended antifire(4)", "AntiFire"}, {"Super antifire potion(4)", "AntiFire"},
            {"Extended super antifire mix(1)", "AntiFire"},
            {"Overload (+)(4)", "Overload"}, {"Overload (-)(1)", "Overload"},
            {"Overload(1)", "Overload"}
        };
        for (String[] entry : cases)
        {
            item(entry[0], true);
            supplies.update();
            assertTrue(entry[0], supplies.visible(entry[1]));
        }
    }

    @Test
    public void heartAndSaltsDoNotRequireADrinkActionAndCannotStandInForEachOther()
    {
        item("Saturated heart", false);
        supplies.update();
        assertTrue(supplies.visible("Saturated Heart"));
        assertFalse(supplies.visible("Smelling Salt"));
        item("Smelling salts(1)", false);
        supplies.update();
        assertTrue(supplies.visible("Smelling Salt"));
        assertFalse(supplies.visible("Saturated Heart"));
    }

    @Test
    public void notedEmptyMissingAndNonDrinkableItemsDoNotQualify()
    {
        int noted = item("Antifire potion(4)", true);
        when(client.getItemDefinition(noted).getNote()).thenReturn(799);
        supplies.update();
        assertFalse(supplies.visible("AntiFire"));
        int empty = item("Antifire potion(4)", true);
        when(inventory.getItems()).thenReturn(new Item[] {new Item(empty, 0), null});
        supplies.update();
        assertFalse(supplies.visible("AntiFire"));
        item("Antifire potion (unf)", false);
        supplies.update();
        assertFalse(supplies.visible("AntiFire"));
        when(client.getItemContainer(InventoryID.INV)).thenReturn(null);
        supplies.update();
        assertFalse(supplies.visible("AntiFire"));
    }

    @Test
    public void suppliesDisappearAndReturnWithoutChangingRealNotificationOrItsTimeout()
    {
        NotificationStore store = new NotificationStore(() -> 0L);
        store.show("AntiFire", "Drink Antifire", java.awt.Color.RED, 15);
        int id = item("Antifire potion(1)", true);
        supplies.update();
        assertTrue(supplies.visible("AntiFire"));
        when(inventory.getItems()).thenReturn(new Item[0]);
        supplies.update();
        assertFalse(supplies.visible("AntiFire"));
        assertEquals(1, store.snapshot().size());
        when(inventory.getItems()).thenReturn(new Item[] {new Item(id, 1)});
        supplies.update();
        assertTrue(supplies.visible("AntiFire"));
        assertEquals(15_000_000_000L, store.snapshot().get(0).expiresAt);
        verify(client, times(1)).getItemDefinition(id);
    }

    @Test
    public void prayerSpellsPiesAndActiveHornDoNotDependOnHavingAnItem()
    {
        supplies.update();
        for (String key : new String[] {"Low Prayer", "Thrall", "Death Charge", "Mark of Darkness", "Ward of Arceuus", "Wild Pie", "Summer Pie", "Soulflamehorn"})
            assertTrue(key, supplies.visible(key));
    }

    @Test
    public void matchingNormalBoostSuppliesUseExistingItemFamilies()
    {
        when(inventory.getItems()).thenReturn(new Item[] {new Item(ItemID.STRENGTH4, 1)});
        supplies.update();
        assertTrue(supplies.visible("Melee Boost"));
        assertFalse(supplies.visible("Magic Boost"));
        assertFalse(supplies.visible("Ranged Boost"));
        when(inventory.getItems()).thenReturn(new Item[] {new Item(ItemID._4DOSE1MAGIC, 1), new Item(ItemID._4DOSERANGERSPOTION, 1)});
        supplies.update();
        assertFalse(supplies.visible("Melee Boost"));
        assertTrue(supplies.visible("Magic Boost"));
        assertTrue(supplies.visible("Ranged Boost"));
    }

    @Test
    public void antivenomAlsoCountsAsAPoisonRemedyAndResetDropsCachedInventory()
    {
        item("Anti-venom+(1)", true);
        supplies.update();
        assertTrue(supplies.visible("Venom"));
        assertTrue(supplies.visible("AntiPoison"));
        supplies.reset();
        assertFalse(supplies.visible("Venom"));
    }
    @Test
    public void outOfSupplyNamesRememberRealDoseFreeNameAndStockReturningRestoresPrompt()
    {
        int id = item("Extended super antifire(1)", true);
        supplies.update();
        assertTrue(supplies.visible("AntiFire"));
        when(inventory.getItems()).thenReturn(new Item[0]);
        supplies.update();
        assertFalse(supplies.visible("AntiFire"));
        assertEquals("Out of Extended super antifire", supplies.outOfMessage("AntiFire"));
        when(inventory.getItems()).thenReturn(new Item[] {new Item(id, 1)});
        supplies.update();
        assertTrue(supplies.visible("AntiFire"));
    }

    @Test
    public void adrenalineBothDosesAreRecognisedAndMissingHistoryUsesSensibleName()
    {
        assertEquals("Out of Liquid Adrenaline", supplies.outOfMessage("Liquid Adrenaline"));
        for (String name : new String[] {"Liquid adrenaline(1)", "Liquid adrenaline(2)"})
        {
            item(name, true);
            supplies.update();
            assertTrue(supplies.visible("Liquid Adrenaline"));
            when(inventory.getItems()).thenReturn(new Item[0]);
            supplies.update();
            assertEquals("Out of Liquid adrenaline", supplies.outOfMessage("Liquid Adrenaline"));
        }
    }
}
