package com.shuddie.overhead;

import java.awt.*;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class TriggerRule {
    final String triggerKey;
    final String keyword;
    final BooleanSupplier isEnabled;
    final Supplier<String> messageSupplier;
    final Supplier<Color> colorSupplier;
    final int durationOverride;

    TriggerRule(String triggerKey, String keyword,
                BooleanSupplier isEnabled,
                Supplier<String> messageSupplier,
                Supplier<Color> colorSupplier,
                int durationOverride) {
        this.triggerKey = triggerKey;
        this.keyword = keyword;
        this.isEnabled = isEnabled;
        this.messageSupplier = messageSupplier;
        this.colorSupplier = colorSupplier;
        this.durationOverride = durationOverride;
    }

    boolean matches(String msg) {
        return msg.contains(keyword);
    }
}
