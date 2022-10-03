package org.glassfish.jersey.server.internal.monitoring.core;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.TimeUnit;

public class UniformTimeReservoir implements TimeReservoir<Long>
{
    private final long startTime;
    private final TimeUnit startTimeUnit;
    private static final int DEFAULT_SIZE = 1024;
    private static final int BITS_PER_LONG = 63;
    private final AtomicLong count;
    private final AtomicLongArray values;
    
    public UniformTimeReservoir(final long startTime, final TimeUnit startTimeUnit) {
        this(1024, startTime, startTimeUnit);
    }
    
    public UniformTimeReservoir(final int size, final long startTime, final TimeUnit startTimeUnit) {
        this.count = new AtomicLong();
        this.startTime = startTime;
        this.startTimeUnit = startTimeUnit;
        this.values = new AtomicLongArray(size);
        for (int i = 0; i < this.values.length(); ++i) {
            this.values.set(i, 0L);
        }
        this.count.set(0L);
    }
    
    @Override
    public int size(final long time, final TimeUnit timeUnit) {
        final long c = this.count.get();
        if (c > this.values.length()) {
            return this.values.length();
        }
        return (int)c;
    }
    
    @Override
    public void update(final Long value, final long time, final TimeUnit timeUnit) {
        final long c = this.count.incrementAndGet();
        if (c <= this.values.length()) {
            this.values.set((int)c - 1, value);
        }
        else {
            final long r = nextLong(c);
            if (r < this.values.length()) {
                this.values.set((int)r, value);
            }
        }
    }
    
    private static long nextLong(final long n) {
        long bits;
        long val;
        do {
            bits = (ThreadLocalRandom.current().nextLong() & Long.MAX_VALUE);
            val = bits % n;
        } while (bits - val + (n - 1L) < 0L);
        return val;
    }
    
    @Override
    public UniformTimeSnapshot getSnapshot(final long time, final TimeUnit timeUnit) {
        final int s = this.size(time, timeUnit);
        final List<Long> copy = new ArrayList<Long>(s);
        for (int i = 0; i < s; ++i) {
            copy.add(this.values.get(i));
        }
        return new UniformTimeValuesSnapshot(copy, this.startTimeUnit.convert(time, timeUnit) - this.startTime, this.startTimeUnit) {
            @Override
            public long size() {
                return UniformTimeReservoir.this.count.get();
            }
        };
    }
    
    @Override
    public long interval(final TimeUnit timeUnit) {
        return 0L;
    }
}
