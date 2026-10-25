package com.frazersan.overheadreminders;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/** Uses public Swing controls to target only this plugin's Quick actions row. */
class QuickActionScroll
{
    private final KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    private WeakReference<JComboBox<?>> openedActions = new WeakReference<>(null);
    private boolean listening;
    private final PropertyChangeListener focusChanged = event ->
        SwingUtilities.invokeLater(() -> SwingUtilities.invokeLater(() -> {
            if (listening) onOpened(focusManager.getActiveWindow());
        }));

    @Inject
    QuickActionScroll() {}

    void start()
    {
        if (listening) return;
        listening = true;
        focusManager.addPropertyChangeListener("focusOwner", focusChanged);
        SwingUtilities.invokeLater(() -> {
            if (listening) onOpened(focusManager.getActiveWindow());
        });
    }

    void stop()
    {
        listening = false;
        focusManager.removePropertyChangeListener("focusOwner", focusChanged);
        openedActions.clear();
    }

    void onOpened(Container root)
    {
        if (root == null) return;
        JComboBox<?> actions = findActions(root);
        JComboBox<?> previous = openedActions.get();
        if (actions == null)
        {
            // A popup or another window gaining focus must not re-arm the same
            // still-visible settings form and interrupt normal editing.
            if (previous != null && !previous.isShowing()) openedActions.clear();
            return;
        }
        if (actions == previous) return;
        openedActions = new WeakReference<>(actions);
        toTop(root);
    }

    static void toTop(Container root)
    {
        if (root == null) return;
        JComboBox<?> actions = findActions(root);
        if (actions == null) return;
        // Keeping focus on the first control also prevents focus traversal from
        // scrolling a lower checkbox back into view after the form is rebuilt.
        actions.requestFocusInWindow();
        JScrollPane scroll = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, actions);
        if (scroll != null)
        {
            scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMinimum());
            Point position = scroll.getViewport().getViewPosition();
            scroll.getViewport().setViewPosition(new Point(position.x, 0));
        }
    }

    private static JComboBox<?> findActions(Container root)
    {
        for (Component child : root.getComponents())
        {
            if (!child.isVisible()) continue;
            if (child instanceof JComboBox)
            {
                JComboBox<?> combo = (JComboBox<?>) child;
                if (combo.getItemCount() > 0 && combo.getItemAt(0) instanceof ReminderQuickAction) return combo;
            }
            if (child instanceof Container)
            {
                JComboBox<?> result = findActions((Container) child);
                if (result != null) return result;
            }
        }
        return null;
    }
}
