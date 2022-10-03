package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import java.util.concurrent.TimeUnit;

public class FixedElapsedTime implements StoppingCondition
{
    private final long maxTimePeriod;
    private long endTime;
    
    public FixedElapsedTime(final long maxTime) throws NumberIsTooSmallException {
        this(maxTime, TimeUnit.SECONDS);
    }
    
    public FixedElapsedTime(final long maxTime, final TimeUnit unit) throws NumberIsTooSmallException {
        this.endTime = -1L;
        if (maxTime < 0L) {
            throw new NumberIsTooSmallException(maxTime, 0, true);
        }
        this.maxTimePeriod = unit.toNanos(maxTime);
    }
    
    public boolean isSatisfied(final Population population) {
        if (this.endTime < 0L) {
            this.endTime = System.nanoTime() + this.maxTimePeriod;
        }
        return System.nanoTime() >= this.endTime;
    }
}
