package io.grpc;

import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class Deadline implements Comparable<Deadline>
{
    private static final SystemTicker SYSTEM_TICKER;
    private static final long MAX_OFFSET;
    private static final long MIN_OFFSET;
    private static final long NANOS_PER_SECOND;
    private final Ticker ticker;
    private final long deadlineNanos;
    private volatile boolean expired;
    
    public static Ticker getSystemTicker() {
        return Deadline.SYSTEM_TICKER;
    }
    
    public static Deadline after(final long duration, final TimeUnit units) {
        return after(duration, units, Deadline.SYSTEM_TICKER);
    }
    
    public static Deadline after(final long duration, final TimeUnit units, final Ticker ticker) {
        checkNotNull(units, "units");
        return new Deadline(ticker, units.toNanos(duration), true);
    }
    
    private Deadline(final Ticker ticker, final long offset, final boolean baseInstantAlreadyExpired) {
        this(ticker, ticker.nanoTime(), offset, baseInstantAlreadyExpired);
    }
    
    private Deadline(final Ticker ticker, final long baseInstant, long offset, final boolean baseInstantAlreadyExpired) {
        this.ticker = ticker;
        offset = Math.min(Deadline.MAX_OFFSET, Math.max(Deadline.MIN_OFFSET, offset));
        this.deadlineNanos = baseInstant + offset;
        this.expired = (baseInstantAlreadyExpired && offset <= 0L);
    }
    
    public boolean isExpired() {
        if (!this.expired) {
            if (this.deadlineNanos - this.ticker.nanoTime() > 0L) {
                return false;
            }
            this.expired = true;
        }
        return true;
    }
    
    public boolean isBefore(final Deadline other) {
        this.checkTicker(other);
        return this.deadlineNanos - other.deadlineNanos < 0L;
    }
    
    public Deadline minimum(final Deadline other) {
        this.checkTicker(other);
        return this.isBefore(other) ? this : other;
    }
    
    public Deadline offset(final long offset, final TimeUnit units) {
        if (offset == 0L) {
            return this;
        }
        return new Deadline(this.ticker, this.deadlineNanos, units.toNanos(offset), this.isExpired());
    }
    
    public long timeRemaining(final TimeUnit unit) {
        final long nowNanos = this.ticker.nanoTime();
        if (!this.expired && this.deadlineNanos - nowNanos <= 0L) {
            this.expired = true;
        }
        return unit.convert(this.deadlineNanos - nowNanos, TimeUnit.NANOSECONDS);
    }
    
    public ScheduledFuture<?> runOnExpiration(final Runnable task, final ScheduledExecutorService scheduler) {
        checkNotNull(task, "task");
        checkNotNull(scheduler, "scheduler");
        return scheduler.schedule(task, this.deadlineNanos - this.ticker.nanoTime(), TimeUnit.NANOSECONDS);
    }
    
    @Override
    public String toString() {
        final long remainingNanos = this.timeRemaining(TimeUnit.NANOSECONDS);
        final long seconds = Math.abs(remainingNanos) / Deadline.NANOS_PER_SECOND;
        final long nanos = Math.abs(remainingNanos) % Deadline.NANOS_PER_SECOND;
        final StringBuilder buf = new StringBuilder();
        if (remainingNanos < 0L) {
            buf.append('-');
        }
        buf.append(seconds);
        if (nanos > 0L) {
            buf.append(String.format(Locale.US, ".%09d", nanos));
        }
        buf.append("s from now");
        if (this.ticker != Deadline.SYSTEM_TICKER) {
            buf.append(" (ticker=" + this.ticker + ")");
        }
        return buf.toString();
    }
    
    @Override
    public int compareTo(final Deadline that) {
        this.checkTicker(that);
        final long diff = this.deadlineNanos - that.deadlineNanos;
        if (diff < 0L) {
            return -1;
        }
        if (diff > 0L) {
            return 1;
        }
        return 0;
    }
    
    @Override
    public int hashCode() {
        return Arrays.asList(this.ticker, this.deadlineNanos).hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Deadline)) {
            return false;
        }
        final Deadline other = (Deadline)o;
        if (this.ticker == null) {
            if (other.ticker == null) {
                return this.deadlineNanos == other.deadlineNanos;
            }
        }
        else if (this.ticker == other.ticker) {
            return this.deadlineNanos == other.deadlineNanos;
        }
        return false;
    }
    
    private static <T> T checkNotNull(final T reference, final Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }
    
    private void checkTicker(final Deadline other) {
        if (this.ticker != other.ticker) {
            throw new AssertionError((Object)("Tickers (" + this.ticker + " and " + other.ticker + ") don't match. Custom Ticker should only be used in tests!"));
        }
    }
    
    static {
        SYSTEM_TICKER = new SystemTicker();
        MAX_OFFSET = TimeUnit.DAYS.toNanos(36500L);
        MIN_OFFSET = -Deadline.MAX_OFFSET;
        NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L);
    }
    
    public abstract static class Ticker
    {
        public abstract long nanoTime();
    }
    
    private static class SystemTicker extends Ticker
    {
        @Override
        public long nanoTime() {
            return System.nanoTime();
        }
    }
}
