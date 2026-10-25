package com.frazersan.overheadreminders;

import java.util.EnumSet;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.api.ItemComposition;
import net.runelite.api.ItemContainer;
import net.runelite.api.Skill;
import net.runelite.api.gameval.InventoryID;

/** Inventory-only visibility filtering; reminder episodes and timeouts keep running. */
@Singleton
final class InventorySupplyChecker
{
    private final Client client;
    private static final class Supply
    {
        final EnumSet<ReminderType> types;
        final String name;
        Supply(EnumSet<ReminderType> types, String name) { this.types = types; this.name = name; }
    }
    private final Map<Integer, Supply> itemTypes = new HashMap<>();
    private volatile Map<ReminderType, String> lastNames = new EnumMap<>(ReminderType.class);
    private volatile EnumSet<ReminderType> available = EnumSet.noneOf(ReminderType.class);

    @Inject
    InventorySupplyChecker(Client client) { this.client = client; }

    void update()
    {
        EnumSet<ReminderType> next = EnumSet.noneOf(ReminderType.class);
        Map<ReminderType, String> names = new EnumMap<>(lastNames);
        ItemContainer inventory = client.getItemContainer(InventoryID.INV);
        Item[] items = inventory == null ? null : inventory.getItems();
        if (items != null)
        {
            for (Item item : items)
            {
                if (item == null || item.getId() < 0 || item.getQuantity() <= 0) continue;
                Supply supply = itemTypes.computeIfAbsent(item.getId(), this::classify);
                next.addAll(supply.types);
                for (ReminderType type : supply.types) names.put(type, supply.name);
            }
            EnumSet<Skill> boosts = BoostItems.available(items, true);
            if (boosts.contains(Skill.STRENGTH)) next.add(ReminderType.MELEE);
            if (boosts.contains(Skill.RANGED)) next.add(ReminderType.RANGED);
            if (boosts.contains(Skill.MAGIC)) next.add(ReminderType.MAGIC_BOOST);
        }
        lastNames = names;
        available = next;
    }

    boolean visible(String key)
    {
        ReminderType type = ReminderType.forKey(key);
        switch (type)
        {
            case DIVINE: case HEART: case STAMINA: case SURGE: case ANTIPOISON: case VENOM:
            case ANTIFIRE: case OVERLOAD: case SALTS: case ADRENALINE: case PRAYER_REGEN: case PRAYER_ENHANCE:
            case MELEE: case RANGED: case MAGIC_BOOST:
                return available.contains(type);
            default:
                return true; // In particular, Low Prayer must not depend on carrying a potion.
        }
    }

    private Supply classify(int itemId)
    {
        EnumSet<ReminderType> types = EnumSet.noneOf(ReminderType.class);
        ItemComposition item = client.getItemDefinition(itemId);
        if (item == null || item.getNote() != -1 || item.getName() == null) return new Supply(types, "");
        String label = item.getName().replaceAll("<[^>]*>", "").replaceAll("\\(\\d+\\)", "").trim();
        String name = label.toLowerCase(Locale.ROOT);
        EnumSet<Skill> boost = BoostItems.available(new Item[] {new Item(itemId, 1)}, true);
        if (boost.contains(Skill.STRENGTH)) types.add(ReminderType.MELEE);
        if (boost.contains(Skill.RANGED)) types.add(ReminderType.RANGED);
        if (boost.contains(Skill.MAGIC)) types.add(ReminderType.MAGIC_BOOST);
        if (name.equals("saturated heart")) types.add(ReminderType.HEART);
        if (name.equals("smelling salts")) types.add(ReminderType.SALTS);
        boolean drinkable = false;
        String[] actions = item.getInventoryActions();
        if (actions != null)
        {
            for (String action : actions) drinkable |= "Drink".equalsIgnoreCase(action);
        }
        if (!drinkable) return new Supply(types, label);
        if (name.startsWith("divine ") && name.contains("potion")) types.add(ReminderType.DIVINE);
        if (name.contains("stamina") && (name.contains("potion") || name.contains("mix"))) types.add(ReminderType.STAMINA);
        if (name.startsWith("prayer regeneration")) types.add(ReminderType.PRAYER_REGEN);
        if (name.startsWith("prayer enhance")) types.add(ReminderType.PRAYER_ENHANCE);
        if (name.equals("liquid adrenaline")) types.add(ReminderType.ADRENALINE);
        if (name.equals("surge potion")) types.add(ReminderType.SURGE);
        if (name.contains("antifire")) types.add(ReminderType.ANTIFIRE);
        if (name.contains("overload")) types.add(ReminderType.OVERLOAD);
        if (name.contains("anti-venom"))
        {
            types.add(ReminderType.VENOM);
            types.add(ReminderType.ANTIPOISON);
        }
        if (name.contains("antipoison") || name.startsWith("antidote")) types.add(ReminderType.ANTIPOISON);
        return new Supply(types, label);
    }

    String outOfMessage(String key)
    {
        ReminderType type = ReminderType.forKey(key);
        String name = lastNames.get(type);
        if (name == null || name.isEmpty())
        {
            switch (type)
            {
                case DIVINE: name = "Divine potions"; break;
                case MELEE: name = "Melee boost potions"; break;
                case MAGIC_BOOST: name = "Magic boost supplies"; break;
                case RANGED: name = "Ranged boost potions"; break;
                case HEART: name = "Saturated Heart"; break;
                case SALTS: name = "Smelling Salts"; break;
                case ANTIPOISON: name = "Antipoison"; break;
                case ANTIFIRE: name = "Antifire"; break;
                case VENOM: name = "Anti-venom"; break;
                case PRAYER_REGEN: name = "Prayer regeneration potion"; break;
                case PRAYER_ENHANCE: name = "Prayer enhance"; break;
                default: name = type.key;
            }
        }
        return "Out of " + name;
    }

    void reset()
    {
        available = EnumSet.noneOf(ReminderType.class);
        lastNames = new EnumMap<>(ReminderType.class);
        itemTypes.clear();
    }
}
