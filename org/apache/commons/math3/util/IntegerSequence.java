package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.Iterator;

public class IntegerSequence
{
    private IntegerSequence() {
    }
    
    public static Range range(final int start, final int end) {
        return range(start, end, 1);
    }
    
    public static Range range(final int start, final int max, final int step) {
        return new Range(start, max, step);
    }
    
    public static class Range implements Iterable<Integer>
    {
        private final int size;
        private final int start;
        private final int max;
        private final int step;
        
        public Range(final int start, final int max, final int step) {
            this.start = start;
            this.max = max;
            this.step = step;
            final int s = (max - start) / step + 1;
            this.size = ((s < 0) ? 0 : s);
        }
        
        public int size() {
            return this.size;
        }
        
        public Iterator<Integer> iterator() {
            return Incrementor.create().withStart(this.start).withMaximalCount(this.max + ((this.step > 0) ? 1 : -1)).withIncrement(this.step);
        }
    }
    
    public static class Incrementor implements Iterator<Integer>
    {
        private static final MaxCountExceededCallback CALLBACK;
        private final int init;
        private final int maximalCount;
        private final int increment;
        private final MaxCountExceededCallback maxCountCallback;
        private int count;
        
        private Incrementor(final int start, final int max, final int step, final MaxCountExceededCallback cb) throws NullArgumentException {
            this.count = 0;
            if (cb == null) {
                throw new NullArgumentException();
            }
            this.init = start;
            this.maximalCount = max;
            this.increment = step;
            this.maxCountCallback = cb;
            this.count = start;
        }
        
        public static Incrementor create() {
            return new Incrementor(0, 0, 1, Incrementor.CALLBACK);
        }
        
        public Incrementor withStart(final int start) {
            return new Incrementor(start, this.maximalCount, this.increment, this.maxCountCallback);
        }
        
        public Incrementor withMaximalCount(final int max) {
            return new Incrementor(this.init, max, this.increment, this.maxCountCallback);
        }
        
        public Incrementor withIncrement(final int step) {
            if (step == 0) {
                throw new ZeroException();
            }
            return new Incrementor(this.init, this.maximalCount, step, this.maxCountCallback);
        }
        
        public Incrementor withCallback(final MaxCountExceededCallback cb) {
            return new Incrementor(this.init, this.maximalCount, this.increment, cb);
        }
        
        public int getMaximalCount() {
            return this.maximalCount;
        }
        
        public int getCount() {
            return this.count;
        }
        
        public boolean canIncrement() {
            return this.canIncrement(1);
        }
        
        public boolean canIncrement(final int nTimes) {
            final int finalCount = this.count + nTimes * this.increment;
            return (this.increment < 0) ? (finalCount > this.maximalCount) : (finalCount < this.maximalCount);
        }
        
        public void increment(final int nTimes) throws MaxCountExceededException {
            if (nTimes <= 0) {
                throw new NotStrictlyPositiveException(nTimes);
            }
            if (!this.canIncrement(0)) {
                this.maxCountCallback.trigger(this.maximalCount);
            }
            this.count += nTimes * this.increment;
        }
        
        public void increment() throws MaxCountExceededException {
            this.increment(1);
        }
        
        public boolean hasNext() {
            return this.canIncrement(0);
        }
        
        public Integer next() {
            final int value = this.count;
            this.increment();
            return value;
        }
        
        public void remove() {
            throw new MathUnsupportedOperationException();
        }
        
        static {
            CALLBACK = new MaxCountExceededCallback() {
                public void trigger(final int max) throws MaxCountExceededException {
                    throw new MaxCountExceededException(max);
                }
            };
        }
        
        public interface MaxCountExceededCallback
        {
            void trigger(final int p0) throws MaxCountExceededException;
        }
    }
}
