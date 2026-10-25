/*
 * Copyright (c) 2022, InfernoStats <http://github.com/InfernoStats>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.frazersan.overheadreminders;

import java.util.function.LongSupplier;

/** A monotonic deadline, polled on the client thread; no background timer threads. */
public class DeathChargeTimer
{
    private final LongSupplier clock;
    private boolean active;
    private long deadline;

    DeathChargeTimer()
    {
        this(System::nanoTime);
    }

    DeathChargeTimer(LongSupplier clock)
    {
        this.clock = clock;
    }

    public void start()
    {
        deadline = clock.getAsLong() + 60_000_000_000L;
        active = true;
    }

    public DeathChargeTimerState getState()
    {
        return !active ? DeathChargeTimerState.IDLE
            : clock.getAsLong() >= deadline ? DeathChargeTimerState.EXPIRED : DeathChargeTimerState.ACTIVE;
    }

    long remainingMillis()
    {
        return active ? Math.max(0, (deadline - clock.getAsLong()) / 1_000_000L) : 0;
    }

    public void stop()
    {
        active = false;
    }
}
