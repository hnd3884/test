package io.opencensus.internal;

import io.opencensus.common.Timestamp;
import javax.annotation.concurrent.Immutable;
import io.opencensus.common.Clock;

@Immutable
public final class ZeroTimeClock extends Clock
{
    private static final ZeroTimeClock INSTANCE;
    private static final Timestamp ZERO_TIMESTAMP;
    
    private ZeroTimeClock() {
    }
    
    public static ZeroTimeClock getInstance() {
        return ZeroTimeClock.INSTANCE;
    }
    
    @Override
    public Timestamp now() {
        return ZeroTimeClock.ZERO_TIMESTAMP;
    }
    
    @Override
    public long nowNanos() {
        return 0L;
    }
    
    static {
        INSTANCE = new ZeroTimeClock();
        ZERO_TIMESTAMP = Timestamp.create(0L, 0);
    }
}
