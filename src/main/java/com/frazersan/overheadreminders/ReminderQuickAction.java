package com.frazersan.overheadreminders;

public enum ReminderQuickAction
{
    NONE("Select an action"), SHOW_PREVIEW("Show six-message preview"), STOP_PREVIEW("Stop preview"),
    ENABLE_ALL("Enable all reminders"), DISABLE_ALL("Disable all reminders"), OPEN_TOOLS("Open tools sidebar");

    private final String label;
    ReminderQuickAction(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
