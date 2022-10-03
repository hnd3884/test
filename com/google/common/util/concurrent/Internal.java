package com.google.common.util.concurrent;

import java.time.Duration;
import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
final class Internal
{
    static long toNanosSaturated(final Duration duration) {
        try {
            return duration.toNanos();
        }
        catch (final ArithmeticException tooBig) {
            return duration.isNegative() ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
    }
    
    private Internal() {
    }
}
