package org.apache.lucene.store;

import org.apache.lucene.util.ThreadInterruptedException;
import java.io.IOException;

public abstract class RateLimiter
{
    public abstract void setMBPerSec(final double p0);
    
    public abstract double getMBPerSec();
    
    public abstract long pause(final long p0) throws IOException;
    
    public abstract long getMinPauseCheckBytes();
    
    public static class SimpleRateLimiter extends RateLimiter
    {
        private static final int MIN_PAUSE_CHECK_MSEC = 5;
        private volatile double mbPerSec;
        private volatile long minPauseCheckBytes;
        private long lastNS;
        
        public SimpleRateLimiter(final double mbPerSec) {
            this.setMBPerSec(mbPerSec);
            this.lastNS = System.nanoTime();
        }
        
        @Override
        public void setMBPerSec(final double mbPerSec) {
            this.mbPerSec = mbPerSec;
            this.minPauseCheckBytes = (long)(0.005 * mbPerSec * 1024.0 * 1024.0);
        }
        
        @Override
        public long getMinPauseCheckBytes() {
            return this.minPauseCheckBytes;
        }
        
        @Override
        public double getMBPerSec() {
            return this.mbPerSec;
        }
        
        @Override
        public long pause(final long bytes) {
            final long startNS = System.nanoTime();
            final double secondsToPause = bytes / 1024.0 / 1024.0 / this.mbPerSec;
            final long targetNS;
            synchronized (this) {
                targetNS = this.lastNS + (long)(1.0E9 * secondsToPause);
                if (startNS >= targetNS) {
                    this.lastNS = startNS;
                    return 0L;
                }
                this.lastNS = targetNS;
            }
            long curNS = startNS;
            while (true) {
                final long pauseNS = targetNS - curNS;
                if (pauseNS <= 0L) {
                    break;
                }
                try {
                    int sleepMS;
                    int sleepNS;
                    if (pauseNS > 214748364700000L) {
                        sleepMS = Integer.MAX_VALUE;
                        sleepNS = 0;
                    }
                    else {
                        sleepMS = (int)(pauseNS / 1000000L);
                        sleepNS = (int)(pauseNS % 1000000L);
                    }
                    Thread.sleep(sleepMS, sleepNS);
                }
                catch (final InterruptedException ie) {
                    throw new ThreadInterruptedException(ie);
                }
                curNS = System.nanoTime();
            }
            return curNS - startNS;
        }
    }
}
