package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.MaxCountExceededException;

@Deprecated
public class Incrementor
{
    private int maximalCount;
    private int count;
    private final MaxCountExceededCallback maxCountCallback;
    
    public Incrementor() {
        this(0);
    }
    
    public Incrementor(final int max) {
        this(max, new MaxCountExceededCallback() {
            public void trigger(final int max) throws MaxCountExceededException {
                throw new MaxCountExceededException(max);
            }
        });
    }
    
    public Incrementor(final int max, final MaxCountExceededCallback cb) throws NullArgumentException {
        this.count = 0;
        if (cb == null) {
            throw new NullArgumentException();
        }
        this.maximalCount = max;
        this.maxCountCallback = cb;
    }
    
    public void setMaximalCount(final int max) {
        this.maximalCount = max;
    }
    
    public int getMaximalCount() {
        return this.maximalCount;
    }
    
    public int getCount() {
        return this.count;
    }
    
    public boolean canIncrement() {
        return this.count < this.maximalCount;
    }
    
    public void incrementCount(final int value) throws MaxCountExceededException {
        for (int i = 0; i < value; ++i) {
            this.incrementCount();
        }
    }
    
    public void incrementCount() throws MaxCountExceededException {
        if (++this.count > this.maximalCount) {
            this.maxCountCallback.trigger(this.maximalCount);
        }
    }
    
    public void resetCount() {
        this.count = 0;
    }
    
    public static Incrementor wrap(final IntegerSequence.Incrementor incrementor) {
        return new Incrementor() {
            private IntegerSequence.Incrementor delegate = incrementor;
            
            {
                super.setMaximalCount(this.delegate.getMaximalCount());
                super.incrementCount(this.delegate.getCount());
            }
            
            @Override
            public void setMaximalCount(final int max) {
                super.setMaximalCount(max);
                this.delegate = this.delegate.withMaximalCount(max);
            }
            
            @Override
            public void resetCount() {
                super.resetCount();
                this.delegate = this.delegate.withStart(0);
            }
            
            @Override
            public void incrementCount() {
                super.incrementCount();
                this.delegate.increment();
            }
        };
    }
    
    public interface MaxCountExceededCallback
    {
        void trigger(final int p0) throws MaxCountExceededException;
    }
}
