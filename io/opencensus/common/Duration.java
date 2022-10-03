package io.opencensus.common;

import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Duration implements Comparable<Duration>
{
    public static Duration create(final long seconds, final int nanos) {
        if (seconds < -315576000000L) {
            throw new IllegalArgumentException("'seconds' is less than minimum (-315576000000): " + seconds);
        }
        if (seconds > 315576000000L) {
            throw new IllegalArgumentException("'seconds' is greater than maximum (315576000000): " + seconds);
        }
        if (nanos < -999999999) {
            throw new IllegalArgumentException("'nanos' is less than minimum (-999999999): " + nanos);
        }
        if (nanos > 999999999) {
            throw new IllegalArgumentException("'nanos' is greater than maximum (999999999): " + nanos);
        }
        if ((seconds < 0L && nanos > 0) || (seconds > 0L && nanos < 0)) {
            throw new IllegalArgumentException("'seconds' and 'nanos' have inconsistent sign: seconds=" + seconds + ", nanos=" + nanos);
        }
        return new AutoValue_Duration(seconds, nanos);
    }
    
    public static Duration fromMillis(final long millis) {
        final long seconds = millis / 1000L;
        final int nanos = (int)(millis % 1000L * 1000000L);
        return create(seconds, nanos);
    }
    
    public long toMillis() {
        return TimeUnit.SECONDS.toMillis(this.getSeconds()) + TimeUnit.NANOSECONDS.toMillis(this.getNanos());
    }
    
    public abstract long getSeconds();
    
    public abstract int getNanos();
    
    @Override
    public int compareTo(final Duration otherDuration) {
        final int cmp = TimeUtils.compareLongs(this.getSeconds(), otherDuration.getSeconds());
        if (cmp != 0) {
            return cmp;
        }
        return TimeUtils.compareLongs(this.getNanos(), otherDuration.getNanos());
    }
    
    Duration() {
    }
}
