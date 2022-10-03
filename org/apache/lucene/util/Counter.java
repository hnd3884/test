package org.apache.lucene.util;

import java.util.concurrent.atomic.AtomicLong;

public abstract class Counter
{
    public abstract long addAndGet(final long p0);
    
    public abstract long get();
    
    public static Counter newCounter() {
        return newCounter(false);
    }
    
    public static Counter newCounter(final boolean threadSafe) {
        return threadSafe ? new AtomicCounter() : new SerialCounter();
    }
    
    private static final class SerialCounter extends Counter
    {
        private long count;
        
        private SerialCounter() {
            this.count = 0L;
        }
        
        @Override
        public long addAndGet(final long delta) {
            return this.count += delta;
        }
        
        @Override
        public long get() {
            return this.count;
        }
    }
    
    private static final class AtomicCounter extends Counter
    {
        private final AtomicLong count;
        
        private AtomicCounter() {
            this.count = new AtomicLong();
        }
        
        @Override
        public long addAndGet(final long delta) {
            return this.count.addAndGet(delta);
        }
        
        @Override
        public long get() {
            return this.count.get();
        }
    }
}
