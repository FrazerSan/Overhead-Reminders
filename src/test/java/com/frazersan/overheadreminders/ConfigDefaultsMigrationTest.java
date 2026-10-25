package com.frazersan.overheadreminders;

import java.util.HashMap;
import java.util.Map;
import net.runelite.client.config.ConfigManager;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ConfigDefaultsMigrationTest
{
    private ConfigManager manager(Map<String, String> values)
    {
        ConfigManager manager = mock(ConfigManager.class);
        when(manager.getConfiguration(eq("overheadreminder"), anyString()))
            .thenAnswer(call -> values.get(call.getArgument(1)));
        doAnswer(call -> {
            values.put(call.getArgument(1), call.getArgument(2).toString());
            return null;
        }).when(manager).setConfiguration(eq("overheadreminder"), anyString(), any());
        return manager;
    }

    @Test
    public void storedOldDefaultsBecomeTitleCaseAndFifteenSeconds()
    {
        Map<String, String> values = new HashMap<>();
        values.put("overheadDisplayTime", "3");
        values.put("lowPrayerDuration", "0");
        values.put("lowPrayerMessage", "LOW PRAYER!");
        values.put("meleeBoostTimeout", "0");
        values.put("rangedBoostMessage", "RANGED BOOST LOW!");
        values.put("summerPieMessage", "AGILITY BOOST LOW!");
        ConfigDefaultsMigration.apply(manager(values));
        assertEquals("15", values.get("overheadDisplayTime"));
        assertEquals("15", values.get("lowPrayerDuration"));
        assertEquals("15", values.get("meleeBoostTimeout"));
        assertEquals("Low Prayer", values.get("lowPrayerMessage"));
        assertEquals("Range Boost", values.get("rangedBoostMessage"));
        assertEquals("Agility Boost", values.get("summerPieMessage"));
    }

    @Test
    public void customValuesAndSubsequentEditsArePreserved()
    {
        Map<String, String> values = new HashMap<>();
        values.put("overheadDisplayTime", "8");
        values.put("meleeBoostMessage", "Drink strength");
        ConfigManager manager = manager(values);
        ConfigDefaultsMigration.apply(manager);
        assertEquals("8", values.get("overheadDisplayTime"));
        assertEquals("Drink strength", values.get("meleeBoostMessage"));
        values.put("overheadDisplayTime", "3");
        values.put("meleeBoostMessage", "MELEE BOOST LOW!");
        ConfigDefaultsMigration.apply(manager);
        assertEquals("3", values.get("overheadDisplayTime"));
        assertEquals("MELEE BOOST LOW!", values.get("meleeBoostMessage"));
    }

    @Test
    public void missingValuesUseNewDefaultsWithoutCreatingOverrides()
    {
        Map<String, String> values = new HashMap<>();
        ConfigDefaultsMigration.apply(manager(values));
        assertFalse(values.containsKey("overheadDisplayTime"));
        assertFalse(values.containsKey("summerPieMessage"));
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        assertEquals(15, config.overheadDisplayTime());
        assertEquals(15, config.lowPrayerDuration());
        assertEquals(15, config.meleeBoostTimeout());
        assertEquals(15, config.magicBoostTimeout());
        assertEquals(15, config.rangedBoostTimeout());
        assertEquals(15, config.wildPieTimeout());
        assertEquals(15, config.summerPieTimeout());
        assertEquals("Agility Boost", config.summerPieMessage());
    }
}
