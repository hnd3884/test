package com.sun.mail.util.logging;

import java.util.logging.LogRecord;
import java.util.logging.Filter;

public class DurationFilter implements Filter
{
    private final long records;
    private final long duration;
    private long count;
    private long peak;
    private long start;
    
    public DurationFilter() {
        this.records = checkRecords(this.initLong(".records"));
        this.duration = checkDuration(this.initLong(".duration"));
    }
    
    public DurationFilter(final long records, final long duration) {
        this.records = checkRecords(records);
        this.duration = checkDuration(duration);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final DurationFilter other = (DurationFilter)obj;
        if (this.records != other.records) {
            return false;
        }
        if (this.duration != other.duration) {
            return false;
        }
        final long c;
        final long p;
        final long s;
        synchronized (this) {
            c = this.count;
            p = this.peak;
            s = this.start;
        }
        synchronized (other) {
            if (c != other.count || p != other.peak || s != other.start) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isIdle() {
        return this.test(0L, System.currentTimeMillis());
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (int)(this.records ^ this.records >>> 32);
        hash = 89 * hash + (int)(this.duration ^ this.duration >>> 32);
        return hash;
    }
    
    @Override
    public boolean isLoggable(final LogRecord record) {
        return this.accept(record.getMillis());
    }
    
    public boolean isLoggable() {
        return this.test(this.records, System.currentTimeMillis());
    }
    
    @Override
    public String toString() {
        final boolean idle;
        final boolean loggable;
        synchronized (this) {
            final long millis = System.currentTimeMillis();
            idle = this.test(0L, millis);
            loggable = this.test(this.records, millis);
        }
        return this.getClass().getName() + "{records=" + this.records + ", duration=" + this.duration + ", idle=" + idle + ", loggable=" + loggable + '}';
    }
    
    @Override
    protected DurationFilter clone() throws CloneNotSupportedException {
        final DurationFilter clone = (DurationFilter)super.clone();
        clone.count = 0L;
        clone.peak = 0L;
        clone.start = 0L;
        return clone;
    }
    
    private boolean test(final long limit, final long millis) {
        assert limit >= 0L : limit;
        final long c;
        final long s;
        synchronized (this) {
            c = this.count;
            s = this.start;
        }
        if (c > 0L) {
            if (millis - s >= this.duration || c < limit) {
                return true;
            }
        }
        else if (millis - s >= 0L || c == 0L) {
            return true;
        }
        return false;
    }
    
    private synchronized boolean accept(final long millis) {
        boolean allow;
        if (this.count > 0L) {
            if (millis - this.peak > 0L) {
                this.peak = millis;
            }
            if (this.count != this.records) {
                ++this.count;
                allow = true;
            }
            else if (this.peak - this.start >= this.duration) {
                this.count = 1L;
                this.start = this.peak;
                allow = true;
            }
            else {
                this.count = -1L;
                this.start = this.peak + this.duration;
                allow = false;
            }
        }
        else if (millis - this.start >= 0L || this.count == 0L) {
            this.count = 1L;
            this.start = millis;
            this.peak = millis;
            allow = true;
        }
        else {
            allow = false;
        }
        return allow;
    }
    
    private long initLong(final String suffix) {
        long result = 0L;
        final String p = this.getClass().getName();
        String value = LogManagerProperties.fromLogManager(p.concat(suffix));
        if (value != null && value.length() != 0) {
            value = value.trim();
            if (this.isTimeEntry(suffix, value)) {
                try {
                    result = LogManagerProperties.parseDurationToMillis(value);
                }
                catch (final RuntimeException ex) {}
                catch (final Exception ex2) {}
                catch (final LinkageError linkageError) {}
            }
            if (result == 0L) {
                try {
                    result = 1L;
                    for (String s : tokenizeLongs(value)) {
                        if (s.endsWith("L") || s.endsWith("l")) {
                            s = s.substring(0, s.length() - 1);
                        }
                        result = multiplyExact(result, Long.parseLong(s));
                    }
                }
                catch (final RuntimeException ignore) {
                    result = Long.MIN_VALUE;
                }
            }
        }
        else {
            result = Long.MIN_VALUE;
        }
        return result;
    }
    
    private boolean isTimeEntry(final String suffix, final String value) {
        return (value.charAt(0) == 'P' || value.charAt(0) == 'p') && suffix.equals(".duration");
    }
    
    private static String[] tokenizeLongs(final String value) {
        final int i = value.indexOf(42);
        String[] e;
        if (i > -1 && (e = value.split("\\s*\\*\\s*")).length != 0) {
            if (i == 0 || value.charAt(value.length() - 1) == '*') {
                throw new NumberFormatException(value);
            }
            if (e.length == 1) {
                throw new NumberFormatException(e[0]);
            }
        }
        else {
            e = new String[] { value };
        }
        return e;
    }
    
    private static long multiplyExact(final long x, final long y) {
        final long r = x * y;
        if ((Math.abs(x) | Math.abs(y)) >>> 31 != 0L && ((y != 0L && r / y != x) || (x == Long.MIN_VALUE && y == -1L))) {
            throw new ArithmeticException();
        }
        return r;
    }
    
    private static long checkRecords(final long records) {
        return (records > 0L) ? records : 1000L;
    }
    
    private static long checkDuration(final long duration) {
        return (duration > 0L) ? duration : 900000L;
    }
}
