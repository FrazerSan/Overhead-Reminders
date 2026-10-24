package com.shuddie.overhead;

/** Threshold latch: display expiry alone cannot rearm a low-prayer warning. */
final class LowPrayerState
{
    private boolean low;

    boolean update(int prayer, int threshold)
    {
        boolean next = prayer <= threshold;
        boolean alert = next && !low;
        low = next;
        return alert;
    }

    void reset()
    {
        low = false;
    }
}
