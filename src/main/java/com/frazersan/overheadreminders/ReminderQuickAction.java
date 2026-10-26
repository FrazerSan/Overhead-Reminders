package com.frazersan.overheadreminders;

public enum ReminderQuickAction
{
    NONE("Select an action"), SHOW_PREVIEW("Show six-message preview"), STOP_PREVIEW("Stop preview"),
    ENABLE_ALL("Enable all reminders"), DISABLE_ALL("Disable all reminders"),
    RESET_APPEARANCE("Reset appearance"), RESET_TIMERS("Reset timers"), RESET_MESSAGES("Reset messages"),
    USE_GLOBAL_TIMERS("Use global timer for all"), USE_INDIVIDUAL_TIMERS("Use individual timers for all");

    private final String label;
    ReminderQuickAction(String label) { this.label = label; }
    @Override public String toString() { return label; }
}
