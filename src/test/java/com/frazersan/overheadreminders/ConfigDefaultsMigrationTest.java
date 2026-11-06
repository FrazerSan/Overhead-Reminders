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
    public void savedValuesMatchingOldDefaultsArePreserved()
    {
        Map<String, String> values = new HashMap<>();
        values.put("overheadDisplayTime", "3");
        values.put("lowPrayerDuration", "0");
        values.put("lowPrayerMessage", "LOW PRAYER!");
        values.put("meleeBoostTimeout", "0");
        values.put("rangedBoostMessage", "RANGED BOOST LOW!");
        values.put("summerPieMessage", "AGILITY BOOST LOW!");
        ConfigDefaultsMigration.apply(manager(values));
        assertEquals("3", values.get("overheadDisplayTime"));
        assertEquals("0", values.get("lowPrayerDuration"));
        assertEquals("0", values.get("meleeBoostTimeout"));
        assertEquals("LOW PRAYER!", values.get("lowPrayerMessage"));
        assertEquals("RANGED BOOST LOW!", values.get("rangedBoostMessage"));
        assertEquals("AGILITY BOOST LOW!", values.get("summerPieMessage"));
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
    @Test
    public void oldAndCustomMessagesAndEnabledChoicesAreAllPreserved()
    {
        Map<String, String> values = new HashMap<>();
        values.put("tidyDefaultsRevision", "1");
        values.put("enableLowPrayer", "true");
        values.put("divinePotMessage", "Divine Expired!");
        values.put("thrallMessage", "My thrall");
        values.put("prayerRegenMessage", "Prayer regeneration potion Expired!");
        ConfigManager manager = manager(values);
        ConfigDefaultsMigration.apply(manager);
        assertEquals("Divine Expired!", values.get("divinePotMessage"));
        assertEquals("My thrall", values.get("thrallMessage"));
        assertEquals("true", values.get("enableLowPrayer"));
        assertEquals("Prayer regeneration potion Expired!", values.get("prayerRegenMessage"));
        values.put("divinePotMessage", "Divine Expired!");
        ConfigDefaultsMigration.apply(manager);
        assertEquals("Divine Expired!", values.get("divinePotMessage"));
    }

    @Test
    public void freshReminderDefaultsAreAllDisabled() throws Exception
    {
        OverheadNotificationsConfig config = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        for (java.lang.reflect.Method method : OverheadNotificationsConfig.class.getDeclaredMethods())
        {
            if (method.isDefault() && method.getReturnType() == boolean.class)
            {
                assertEquals(method.getName(), (method.getName().endsWith("UseGlobalTimer")
                    || method.getName().equals("showOutOfSupplies") || method.getName().equals("specialAttack100")), method.invoke(config));
            }
        }
        assertEquals("Drink Divine", config.divinePotMessage());
        assertEquals("Cast Thrall", config.thrallMessage());
        assertEquals("Invigorate Saturated Heart", config.saturatedHeartMessage());
    }
    @Test
    public void savedHornWordingAndZeroDurationsArePreserved()
    {
        Map<String, String> values = new HashMap<>();
        values.put("soulflameHornMessage", "Blow Soulflame Horn");
        values.put("overheadDisplayTime", "0");
        values.put("lowPrayerDuration", "0");
        values.put("meleeBoostTimeout", "0");
        ConfigManager manager = manager(values);
        ConfigDefaultsMigration.apply(manager);
        assertEquals("Blow Soulflame Horn", values.get("soulflameHornMessage"));
        assertEquals("0", values.get("overheadDisplayTime"));
        assertEquals("0", values.get("lowPrayerDuration"));
        assertEquals("0", values.get("meleeBoostTimeout"));
        values.put("soulflameHornMessage", "Blow Soulflame Horn");
        ConfigDefaultsMigration.apply(manager);
        assertEquals("Blow Soulflame Horn", values.get("soulflameHornMessage"));
    }
    @Test
    public void upgradingPreservesCustomTimerSelectionsAndExplicitGlobalChoices()
    {
        Map<String, String> values = new HashMap<>();
        values.put("lowPrayerDuration", "8");
        values.put("meleeBoostTimeout", "0");
        values.put("magicBoostTimeout", "15");
        values.put("rangedBoostTimeout", "25");
        values.put("rangedBoostUseGlobalTimer", "true");
        ConfigManager manager = manager(values);
        ConfigDefaultsMigration.apply(manager);
        assertEquals("false", values.get("lowPrayerUseGlobalTimer"));
        assertEquals("false", values.get("meleeBoostUseGlobalTimer"));
        assertEquals("false", values.get("magicBoostUseGlobalTimer"));
        assertEquals("true", values.get("rangedBoostUseGlobalTimer"));
        values.put("lowPrayerUseGlobalTimer", "true");
        ConfigDefaultsMigration.apply(manager);
        assertEquals("true", values.get("lowPrayerUseGlobalTimer"));
    }

    @Test
    public void repeatedUpdatesLeaveExistingProfileSettingsExactlyAsSaved()
    {
        Map<String, String> values = new HashMap<>();
        values.put("sharedTimerDefaultsRevision", "1");
        values.put("overheadDisplayTime", "3");
        values.put("lowPrayerMessage", "LOW PRAYER!");
        values.put("divinePotMessage", "Divine Expired!");
        values.put("soulflameHornMessage", "Blow Soulflame Horn");
        values.put("enableLowPrayer", "true");
        values.put("enableThrall", "false");
        values.put("prayerThreshold", "25");
        values.put("lowPrayerColor", "123456");
        values.put("lowPrayerDuration", "0");
        values.put("lowPrayerUseGlobalTimer", "true");
        values.put("thrallEarlyWarning", "10");
        values.put("verticalOffset", "80");
        values.put("horizontalOffset", "-20");
        values.put("textSize", "24");
        values.put("messageSpacing", "10");
        values.put("showOutOfSupplies", "false");
        values.put("showSidebarIcon", "true");
        Map<String, String> before = new HashMap<>(values);
        ConfigManager manager = manager(values);
        ConfigDefaultsMigration.apply(manager);
        ConfigDefaultsMigration.apply(manager);
        assertEquals(before, values);
        verify(manager, never()).setConfiguration(anyString(), anyString(), any());
        assertFalse(values.containsKey("enableLowHitpoints"));
    }
}
