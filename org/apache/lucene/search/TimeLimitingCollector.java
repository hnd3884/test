package org.apache.lucene.search;

import org.apache.lucene.util.ThreadInterruptedException;
import java.io.IOException;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.util.Counter;

public class TimeLimitingCollector implements Collector
{
    private long t0;
    private long timeout;
    private Collector collector;
    private final Counter clock;
    private final long ticksAllowed;
    private boolean greedy;
    private int docBase;
    
    public TimeLimitingCollector(final Collector collector, final Counter clock, final long ticksAllowed) {
        this.t0 = Long.MIN_VALUE;
        this.timeout = Long.MIN_VALUE;
        this.greedy = false;
        this.collector = collector;
        this.clock = clock;
        this.ticksAllowed = ticksAllowed;
    }
    
    public void setBaseline(final long clockTime) {
        this.t0 = clockTime;
        this.timeout = this.t0 + this.ticksAllowed;
    }
    
    public void setBaseline() {
        this.setBaseline(this.clock.get());
    }
    
    public boolean isGreedy() {
        return this.greedy;
    }
    
    public void setGreedy(final boolean greedy) {
        this.greedy = greedy;
    }
    
    @Override
    public LeafCollector getLeafCollector(final LeafReaderContext context) throws IOException {
        this.docBase = context.docBase;
        if (Long.MIN_VALUE == this.t0) {
            this.setBaseline();
        }
        final long time = this.clock.get();
        if (time - this.timeout > 0L) {
            throw new TimeExceededException(this.timeout - this.t0, time - this.t0, -1);
        }
        return new FilterLeafCollector(this.collector.getLeafCollector(context)) {
            @Override
            public void collect(final int doc) throws IOException {
                final long time = TimeLimitingCollector.this.clock.get();
                if (time - TimeLimitingCollector.this.timeout > 0L) {
                    if (TimeLimitingCollector.this.greedy) {
                        this.in.collect(doc);
                    }
                    throw new TimeExceededException(TimeLimitingCollector.this.timeout - TimeLimitingCollector.this.t0, time - TimeLimitingCollector.this.t0, TimeLimitingCollector.this.docBase + doc);
                }
                this.in.collect(doc);
            }
        };
    }
    
    @Override
    public boolean needsScores() {
        return this.collector.needsScores();
    }
    
    public void setCollector(final Collector collector) {
        this.collector = collector;
    }
    
    public static Counter getGlobalCounter() {
        return TimerThreadHolder.THREAD.counter;
    }
    
    public static TimerThread getGlobalTimerThread() {
        return TimerThreadHolder.THREAD;
    }
    
    public static class TimeExceededException extends RuntimeException
    {
        private long timeAllowed;
        private long timeElapsed;
        private int lastDocCollected;
        
        private TimeExceededException(final long timeAllowed, final long timeElapsed, final int lastDocCollected) {
            super("Elapsed time: " + timeElapsed + ".  Exceeded allowed search time: " + timeAllowed + " ms.");
            this.timeAllowed = timeAllowed;
            this.timeElapsed = timeElapsed;
            this.lastDocCollected = lastDocCollected;
        }
        
        public long getTimeAllowed() {
            return this.timeAllowed;
        }
        
        public long getTimeElapsed() {
            return this.timeElapsed;
        }
        
        public int getLastDocCollected() {
            return this.lastDocCollected;
        }
    }
    
    private static final class TimerThreadHolder
    {
        static final TimerThread THREAD;
        
        static {
            (THREAD = new TimerThread(Counter.newCounter(true))).start();
        }
    }
    
    public static final class TimerThread extends Thread
    {
        public static final String THREAD_NAME = "TimeLimitedCollector timer thread";
        public static final int DEFAULT_RESOLUTION = 20;
        private volatile long time;
        private volatile boolean stop;
        private volatile long resolution;
        final Counter counter;
        
        public TimerThread(final long resolution, final Counter counter) {
            super("TimeLimitedCollector timer thread");
            this.time = 0L;
            this.stop = false;
            this.resolution = resolution;
            this.counter = counter;
            this.setDaemon(true);
        }
        
        public TimerThread(final Counter counter) {
            this(20L, counter);
        }
        
        @Override
        public void run() {
            while (!this.stop) {
                this.counter.addAndGet(this.resolution);
                try {
                    Thread.sleep(this.resolution);
                    continue;
                }
                catch (final InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
                break;
            }
        }
        
        public long getMilliseconds() {
            return this.time;
        }
        
        public void stopTimer() {
            this.stop = true;
        }
        
        public long getResolution() {
            return this.resolution;
        }
        
        public void setResolution(final long resolution) {
            this.resolution = Math.max(resolution, 5L);
        }
    }
}
