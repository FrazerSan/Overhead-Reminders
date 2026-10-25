package com.frazersan.overheadreminders;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;

/** Buttons live in a normal plugin sidebar, without modifying RuneLite's settings UI. */
final class ReminderToolsPanel extends PluginPanel
{
    ReminderToolsPanel(ReminderConfigActions actions, Consumer<Runnable> dispatch, Runnable showPreview, Runnable hidePreview)
    {
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        JLabel title = new JLabel("Overhead Reminders");
        title.setForeground(Color.WHITE);
        add(title, BorderLayout.NORTH);
        JPanel controls = new JPanel(new GridLayout(0, 1, 0, 10));
        controls.setOpaque(false);
        controls.add(button("Show six-message preview", "Show the first reminder from each section, including disabled reminders",
            () -> dispatch.accept(showPreview)));
        controls.add(button("Stop preview", "Return to displaying real reminders",
            () -> dispatch.accept(hidePreview)));
        controls.add(button("Enable all reminders", "Enable every reminder without changing its text, thresholds or timers",
            () -> dispatch.accept(() -> actions.enableAll(true))));
        controls.add(button("Disable all reminders", "Disable every reminder without changing its settings",
            () -> dispatch.accept(() -> actions.enableAll(false))));
        controls.add(button("Reset appearance", "Restore the original position, text size and spacing",
            () -> dispatch.accept(actions::resetAppearance)));
        controls.add(button("Reset timers", "Restore 15 seconds, global timers on, early warnings off and countdown off",
            () -> dispatch.accept(actions::resetTimers)));
        controls.add(button("Reset messages", "Restore default message text. Colours and enabled reminders are kept",
            () -> dispatch.accept(actions::resetMessages)));
        controls.add(button("Use global timer for all", "Turn on every Use global timer toggle; custom durations are kept",
            () -> dispatch.accept(() -> actions.useGlobalForAll(true))));
        controls.add(button("Use individual timers for all", "Turn off every Use global timer toggle; use each saved custom duration",
            () -> dispatch.accept(() -> actions.useGlobalForAll(false))));
        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);
        body.add(controls, BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    private JButton button(String label, String tooltip, Runnable action)
    {
        JButton button = new JButton(label);
        button.setToolTipText(tooltip);
        button.addActionListener(event -> action.run());
        return button;
    }
}
