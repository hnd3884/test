package org.apache.lucene.index;

import java.util.concurrent.TimeUnit;

public class QueryTimeoutImpl implements QueryTimeout
{
    private Long timeoutAt;
    
    public QueryTimeoutImpl(long timeAllowed) {
        if (timeAllowed < 0L) {
            timeAllowed = Long.MAX_VALUE;
        }
        this.timeoutAt = System.nanoTime() + TimeUnit.NANOSECONDS.convert(timeAllowed, TimeUnit.MILLISECONDS);
    }
    
    public Long getTimeoutAt() {
        return this.timeoutAt;
    }
    
    @Override
    public boolean shouldExit() {
        return this.timeoutAt != null && System.nanoTime() - this.timeoutAt > 0L;
    }
    
    public void reset() {
        this.timeoutAt = null;
    }
    
    @Override
    public String toString() {
        return "timeoutAt: " + this.timeoutAt + " (System.nanoTime(): " + System.nanoTime() + ")";
    }
}
