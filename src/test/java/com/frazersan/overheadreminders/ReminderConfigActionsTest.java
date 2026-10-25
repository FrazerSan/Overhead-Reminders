package com.frazersan.overheadreminders;

import java.awt.Component;
import java.awt.Container;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigManager;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ReminderConfigActionsTest
{
    private Map<String, String> values;
    private ReminderConfigActions actions;

    @Before
    public void setup()
    {
        values = new HashMap<>();
        ConfigManager manager = mock(ConfigManager.class);
        doAnswer(call -> {
            values.put(call.getArgument(1), call.getArgument(2).toString());
            return null;
        }).when(manager).setConfiguration(eq("overheadreminder"), anyString(), any());
        actions = new ReminderConfigActions(manager);
    }

    @Test
    public void appearanceResetRestoresOriginalDefaultsAndKeepsOtherSettings()
    {
        values.put("enableLowPrayer", "true");
        values.put("overheadDisplayTime", "60");
        values.put("lowPrayerMessage", "My custom message");
        values.put("textSize", "24");
        actions.resetAppearance();
        assertEquals("40", values.get("verticalOffset"));
        assertEquals("0", values.get("horizontalOffset"));
        assertEquals("2", values.get("messageSpacing"));
        assertEquals("0", values.get("textSize"));
        assertEquals("true", values.get("enableLowPrayer"));
        assertEquals("60", values.get("overheadDisplayTime"));
        assertEquals("My custom message", values.get("lowPrayerMessage"));
    }

    @Test
    public void timerResetMatchesAllTimerDefaultsWithoutChangingThresholdsOrEnabledReminders() throws Exception
    {
        values.put("enableLowPrayer", "true");
        values.put("prayerThreshold", "25");
        values.put("horizontalOffset", "15");
        actions.resetTimers();
        OverheadNotificationsConfig defaults = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        for (java.lang.reflect.Method method : OverheadNotificationsConfig.class.getDeclaredMethods())
        {
            String name = method.getName();
            if (name.endsWith("Duration") || name.endsWith("Timeout") || name.endsWith("UseGlobalTimer")
                || name.endsWith("EarlyWarning") || name.equals("overheadDisplayTime") || name.equals("showCountdown"))
            {
                assertEquals(name, method.invoke(defaults).toString(), values.get(method.getAnnotation(ConfigItem.class).keyName()));
            }
        }
        assertEquals("true", values.get("enableLowPrayer"));
        assertEquals("25", values.get("prayerThreshold"));
        assertEquals("15", values.get("horizontalOffset"));
    }

    @Test
    public void messageResetMatchesDefaultTextAndKeepsColoursAndTimers() throws Exception
    {
        values.put("divinePotColor", "1234");
        values.put("divinePotDuration", "60");
        actions.resetMessages();
        OverheadNotificationsConfig defaults = mock(OverheadNotificationsConfig.class, CALLS_REAL_METHODS);
        int messages = 0;
        for (java.lang.reflect.Method method : OverheadNotificationsConfig.class.getDeclaredMethods())
        {
            if (method.isDefault() && method.getReturnType() == String.class)
            {
                messages++;
                assertEquals(method.getName(), method.invoke(defaults), values.get(method.getAnnotation(ConfigItem.class).keyName()));
            }
        }
        assertEquals(ReminderType.values().length, messages);
        assertEquals("1234", values.get("divinePotColor"));
        assertEquals("60", values.get("divinePotDuration"));
    }

    @Test
    public void allGlobalAndAllIndividualChangeOnlyThePerReminderToggles()
    {
        values.put("divinePotDuration", "0");
        values.put("meleeBoostTimeout", "45");
        values.put("overheadDisplayTime", "30");
        actions.useGlobalForAll(false);
        assertEquals(23, values.entrySet().stream().filter(e -> e.getKey().endsWith("UseGlobalTimer") && e.getValue().equals("false")).count());
        actions.useGlobalForAll(true);
        assertEquals(23, values.entrySet().stream().filter(e -> e.getKey().endsWith("UseGlobalTimer") && e.getValue().equals("true")).count());
        assertEquals("0", values.get("divinePotDuration"));
        assertEquals("45", values.get("meleeBoostTimeout"));
        assertEquals("30", values.get("overheadDisplayTime"));
    }

    @Test
    public void toolsPanelProvidesWorkingScopedButtons() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            ReminderToolsPanel panel = new ReminderToolsPanel(actions, Runnable::run, () -> values.put("preview", "on"), () -> values.put("preview", "off"));
            List<JButton> buttons = new ArrayList<>();
            collect(panel, buttons);
            assertEquals(9, buttons.size());
            buttons.get(0).doClick();
            assertEquals("on", values.get("preview"));
            buttons.get(1).doClick();
            assertEquals("off", values.get("preview"));
            buttons.get(2).doClick();
            assertEquals("true", values.get("enableLowPrayer"));
            buttons.get(3).doClick();
            assertEquals("false", values.get("enableLowPrayer"));
            buttons.get(4).doClick();
            assertEquals("40", values.get("verticalOffset"));
            buttons.get(5).doClick();
            assertEquals("15", values.get("overheadDisplayTime"));
            buttons.get(6).doClick();
            assertEquals("Drink Divine", values.get("divinePotMessage"));
            buttons.get(7).doClick();
            assertEquals("true", values.get("divinePotUseGlobalTimer"));
            buttons.get(8).doClick();
            assertEquals("false", values.get("divinePotUseGlobalTimer"));
        });
    }

    @Test
    public void enableAllAndDisableAllKeepThresholdsTimersAndMessages() throws Exception
    {
        values.put("prayerThreshold", "10");
        values.put("lowPrayerMessage", "Custom prayer");
        values.put("divinePotDuration", "25");
        for (boolean enabled : new boolean[] {true, false})
        {
            actions.enableAll(enabled);
            int count = 0;
            for (java.lang.reflect.Method method : OverheadNotificationsConfig.class.getDeclaredMethods())
            {
                if (method.getReturnType() == boolean.class && (method.getName().startsWith("enable") || method.getName().endsWith("Enabled")))
                {
                    count++;
                    assertEquals(Boolean.toString(enabled), values.get(method.getAnnotation(ConfigItem.class).keyName()));
                }
            }
            assertEquals(23, count);
        }
        assertEquals("10", values.get("prayerThreshold"));
        assertEquals("Custom prayer", values.get("lowPrayerMessage"));
        assertEquals("25", values.get("divinePotDuration"));
    }

    @Test
    public void bulkChangesRefreshOnlyAfterAllCheckboxValuesHaveBeenWritten()
    {
        java.util.concurrent.atomic.AtomicInteger refreshes = new java.util.concurrent.atomic.AtomicInteger();
        ConfigManager manager = mock(ConfigManager.class);
        doAnswer(call -> { values.put(call.getArgument(1), call.getArgument(2).toString()); return null; })
            .when(manager).setConfiguration(eq("overheadreminder"), anyString(), any());
        ReminderConfigActions bulk = new ReminderConfigActions(manager, () -> refreshes.incrementAndGet());
        bulk.enableAll(false);
        assertEquals(23, values.entrySet().stream().filter(e -> (e.getKey().startsWith("enable") || e.getKey().endsWith("Enabled")) && e.getValue().equals("false")).count());
        assertEquals(1, refreshes.get());
        bulk.enableAll(true);
        assertEquals(23, values.entrySet().stream().filter(e -> (e.getKey().startsWith("enable") || e.getKey().endsWith("Enabled")) && e.getValue().equals("true")).count());
        assertEquals(2, refreshes.get());
        bulk.useGlobalForAll(false);
        assertEquals(23, values.entrySet().stream().filter(e -> e.getKey().endsWith("UseGlobalTimer") && e.getValue().equals("false")).count());
        assertEquals(3, refreshes.get());
        bulk.useGlobalForAll(true);
        assertEquals(23, values.entrySet().stream().filter(e -> e.getKey().endsWith("UseGlobalTimer") && e.getValue().equals("true")).count());
        assertEquals(4, refreshes.get());
    }

    private void collect(Container container, List<JButton> buttons)
    {
        for (Component component : container.getComponents())
        {
            if (component instanceof JButton) buttons.add((JButton) component);
            else if (component instanceof Container) collect((Container) component, buttons);
        }
    }
}
