package com.google.api.client.testing.http;

import java.util.concurrent.atomic.AtomicLong;
import com.google.api.client.util.Beta;
import com.google.api.client.util.Clock;

@Beta
public class FixedClock implements Clock
{
    private AtomicLong currentTime;
    
    public FixedClock() {
        this(0L);
    }
    
    public FixedClock(final long startTime) {
        this.currentTime = new AtomicLong(startTime);
    }
    
    public FixedClock setTime(final long newTime) {
        this.currentTime.set(newTime);
        return this;
    }
    
    @Override
    public long currentTimeMillis() {
        return this.currentTime.get();
    }
}
