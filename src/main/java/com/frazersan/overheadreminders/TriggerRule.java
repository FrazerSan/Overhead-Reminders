package com.frazersan.overheadreminders;

/** A game message mapped to one reminder type. */
final class TriggerRule
{
    final ReminderType type;
    private final String keyword;

    TriggerRule(ReminderType type, String keyword)
    {
        this.type = type;
        this.keyword = keyword;
    }

    boolean matches(String message) { return message.contains(keyword); }
}
