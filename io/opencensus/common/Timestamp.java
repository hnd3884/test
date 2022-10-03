package io.opencensus.common;

import java.math.RoundingMode;
import java.math.BigDecimal;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Timestamp implements Comparable<Timestamp>
{
    Timestamp() {
    }
    
    public static Timestamp create(final long seconds, final int nanos) {
        if (seconds < -315576000000L) {
            throw new IllegalArgumentException("'seconds' is less than minimum (-315576000000): " + seconds);
        }
        if (seconds > 315576000000L) {
            throw new IllegalArgumentException("'seconds' is greater than maximum (315576000000): " + seconds);
        }
        if (nanos < 0) {
            throw new IllegalArgumentException("'nanos' is less than zero: " + nanos);
        }
        if (nanos > 999999999) {
            throw new IllegalArgumentException("'nanos' is greater than maximum (999999999): " + nanos);
        }
        return new AutoValue_Timestamp(seconds, nanos);
    }
    
    public static Timestamp fromMillis(final long epochMilli) {
        final long secs = floorDiv(epochMilli, 1000L);
        final int mos = (int)floorMod(epochMilli, 1000L);
        return create(secs, (int)(mos * 1000000L));
    }
    
    public abstract long getSeconds();
    
    public abstract int getNanos();
    
    public Timestamp addNanos(final long nanosToAdd) {
        return this.plus(0L, nanosToAdd);
    }
    
    public Timestamp addDuration(final Duration duration) {
        return this.plus(duration.getSeconds(), duration.getNanos());
    }
    
    public Duration subtractTimestamp(final Timestamp timestamp) {
        long durationSeconds = this.getSeconds() - timestamp.getSeconds();
        int durationNanos = this.getNanos() - timestamp.getNanos();
        if (durationSeconds < 0L && durationNanos > 0) {
            ++durationSeconds;
            durationNanos -= (int)1000000000L;
        }
        else if (durationSeconds > 0L && durationNanos < 0) {
            --durationSeconds;
            durationNanos += (int)1000000000L;
        }
        return Duration.create(durationSeconds, durationNanos);
    }
    
    @Override
    public int compareTo(final Timestamp otherTimestamp) {
        final int cmp = TimeUtils.compareLongs(this.getSeconds(), otherTimestamp.getSeconds());
        if (cmp != 0) {
            return cmp;
        }
        return TimeUtils.compareLongs(this.getNanos(), otherTimestamp.getNanos());
    }
    
    private Timestamp plus(final long secondsToAdd, long nanosToAdd) {
        if ((secondsToAdd | nanosToAdd) == 0x0L) {
            return this;
        }
        long epochSec = TimeUtils.checkedAdd(this.getSeconds(), secondsToAdd);
        epochSec = TimeUtils.checkedAdd(epochSec, nanosToAdd / 1000000000L);
        nanosToAdd %= 1000000000L;
        final long nanoAdjustment = this.getNanos() + nanosToAdd;
        return ofEpochSecond(epochSec, nanoAdjustment);
    }
    
    private static Timestamp ofEpochSecond(final long epochSecond, final long nanoAdjustment) {
        final long secs = TimeUtils.checkedAdd(epochSecond, floorDiv(nanoAdjustment, 1000000000L));
        final int nos = (int)floorMod(nanoAdjustment, 1000000000L);
        return create(secs, nos);
    }
    
    private static long floorDiv(final long x, final long y) {
        return BigDecimal.valueOf(x).divide(BigDecimal.valueOf(y), 0, RoundingMode.FLOOR).longValue();
    }
    
    private static long floorMod(final long x, final long y) {
        return x - floorDiv(x, y) * y;
    }
}
