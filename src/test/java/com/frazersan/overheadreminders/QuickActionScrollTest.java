package com.frazersan.overheadreminders;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import org.junit.Test;
import static org.junit.Assert.*;

public class QuickActionScrollTest
{
    @Test
    public void reopenedSettingsReturnToTopOfTheirOwnScrollPane() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            JPanel content = new JPanel(new BorderLayout());
            content.add(new JComboBox<>(ReminderQuickAction.values()), BorderLayout.NORTH);
            content.setPreferredSize(new Dimension(225, 1600));
            JScrollPane scroll = new JScrollPane(content);
            scroll.setSize(242, 350);
            scroll.doLayout();
            scroll.getViewport().setViewSize(content.getPreferredSize());
            scroll.getViewport().setViewPosition(new Point(0, 650));
            assertTrue(scroll.getViewport().getViewPosition().y > 0);
            JPanel root = new JPanel();
            root.add(scroll);
            QuickActionScroll.toTop(root);
            assertEquals(0, scroll.getViewport().getViewPosition().y);
            assertEquals(0, scroll.getVerticalScrollBar().getValue());
        });
    }

    @Test
    public void unrelatedOrHiddenDropdownDoesNotMoveAnotherSettingsPanel() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            JPanel content = new JPanel(new BorderLayout());
            content.add(new JComboBox<>(new String[] {"Unrelated settings"}), BorderLayout.NORTH);
            JPanel hidden = new JPanel();
            hidden.add(new JComboBox<>(ReminderQuickAction.values()));
            hidden.setVisible(false);
            content.add(hidden, BorderLayout.SOUTH);
            content.setPreferredSize(new Dimension(225, 1600));
            JScrollPane scroll = new JScrollPane(content);
            scroll.setSize(242, 350);
            scroll.doLayout();
            scroll.getViewport().setViewSize(content.getPreferredSize());
            scroll.getViewport().setViewPosition(new Point(0, 650));
            JPanel root = new JPanel();
            root.add(scroll);
            QuickActionScroll.toTop(root);
            assertEquals(650, scroll.getViewport().getViewPosition().y);
        });
    }
    @Test
    public void firstOpeningStartsAtTopButFurtherEditingKeepsManualScroll() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            JPanel content = new JPanel(new BorderLayout());
            content.add(new JComboBox<>(ReminderQuickAction.values()), BorderLayout.NORTH);
            content.setPreferredSize(new Dimension(225, 1600));
            JScrollPane scroll = new JScrollPane(content);
            scroll.setSize(242, 350);
            scroll.doLayout();
            scroll.getViewport().setViewSize(content.getPreferredSize());
            scroll.getViewport().setViewPosition(new Point(0, 650));
            JPanel root = new JPanel();
            root.add(scroll);
            QuickActionScroll watcher = new QuickActionScroll();
            watcher.onOpened(root);
            assertEquals(0, scroll.getViewport().getViewPosition().y);
            scroll.getViewport().setViewPosition(new Point(0, 700));
            watcher.onOpened(root);
            assertEquals(700, scroll.getViewport().getViewPosition().y);
            // Leave this settings form, then reopen it: opening should reset again.
            content.setVisible(false);
            watcher.onOpened(root);
            content.setVisible(true);
            watcher.onOpened(root);
            assertEquals(0, scroll.getViewport().getViewPosition().y);
            watcher.stop();
        });
    }

    @Test
    public void dropdownPopupDoesNotMakeTheSameSettingsFormLookNew() throws Exception
    {
        SwingUtilities.invokeAndWait(() -> {
            JPanel content = new JPanel(new BorderLayout());
            JComboBox<ReminderQuickAction> actions = new JComboBox<ReminderQuickAction>(ReminderQuickAction.values())
            {
                @Override public boolean isShowing() { return true; }
            };
            content.add(actions, BorderLayout.NORTH);
            content.setPreferredSize(new Dimension(225, 1600));
            JScrollPane scroll = new JScrollPane(content);
            scroll.setSize(242, 350);
            scroll.doLayout();
            scroll.getViewport().setViewSize(content.getPreferredSize());
            JPanel root = new JPanel();
            root.add(scroll);
            QuickActionScroll watcher = new QuickActionScroll();
            watcher.onOpened(root);
            scroll.getViewport().setViewPosition(new Point(0, 700));
            watcher.onOpened(new JPanel()); // A temporary popup has no Quick actions row.
            watcher.onOpened(root);
            assertEquals(700, scroll.getViewport().getViewPosition().y);
            watcher.stop();
        });
    }
}
