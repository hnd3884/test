package com.unboundid.util;

import java.util.List;
import java.util.logging.Level;
import java.util.Collections;
import java.util.ArrayList;
import java.io.Serializable;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class FixedRateBarrier implements Serializable
{
    private static final long minSleepMillis;
    private static final long serialVersionUID = -9048370191248737239L;
    private volatile boolean shutdownRequested;
    private long intervalDurationNanos;
    private double millisBetweenIterations;
    private int perInterval;
    private long countInThisInterval;
    private long intervalStartNanos;
    private long intervalEndNanos;
    
    public FixedRateBarrier(final long intervalDurationMs, final int perInterval) {
        this.shutdownRequested = false;
        this.setRate(intervalDurationMs, perInterval);
    }
    
    public synchronized void setRate(final long intervalDurationMs, final int perInterval) {
        Validator.ensureTrue(intervalDurationMs > 0L, "FixedRateBarrier.intervalDurationMs must be at least 1.");
        Validator.ensureTrue(perInterval > 0, "FixedRateBarrier.perInterval must be at least 1.");
        this.perInterval = perInterval;
        this.intervalDurationNanos = 1000000L * intervalDurationMs;
        this.millisBetweenIterations = intervalDurationMs / (double)perInterval;
        this.countInThisInterval = 0L;
        this.intervalStartNanos = 0L;
        this.intervalEndNanos = 0L;
    }
    
    public synchronized boolean await() {
        return this.await(1);
    }
    
    public synchronized boolean await(final int count) {
        if (count > this.perInterval) {
            Validator.ensureTrue(false, "FixedRateBarrier.await(int) count value " + count + " exceeds perInterval value " + this.perInterval + ".  The provided count value must be less than or equal to " + "the perInterval value.");
        }
        else if (count <= 0) {
            return this.shutdownRequested;
        }
        while (!this.shutdownRequested) {
            final long now = System.nanoTime();
            if (this.intervalStartNanos == 0L || now < this.intervalStartNanos) {
                this.intervalStartNanos = now;
                this.intervalEndNanos = this.intervalStartNanos + this.intervalDurationNanos;
            }
            else if (now >= this.intervalEndNanos) {
                this.countInThisInterval = 0L;
                if (now < this.intervalEndNanos + this.intervalDurationNanos) {
                    this.intervalStartNanos = now;
                }
                else {
                    this.intervalStartNanos = this.intervalEndNanos;
                }
                this.intervalEndNanos = this.intervalStartNanos + this.intervalDurationNanos;
            }
            final long intervalRemaining = this.intervalEndNanos - now;
            if (intervalRemaining <= 0L) {
                continue;
            }
            final double intervalFractionRemaining = intervalRemaining / (double)this.intervalDurationNanos;
            final double expectedRemaining = intervalFractionRemaining * this.perInterval;
            final long actualRemaining = this.perInterval - this.countInThisInterval;
            final long countBehind = (long)Math.ceil(actualRemaining - expectedRemaining);
            if (count <= countBehind) {
                this.countInThisInterval += count;
                break;
            }
            final long countNeeded = count - countBehind;
            final long remainingMillis = (long)Math.floor(this.millisBetweenIterations * countNeeded);
            if (remainingMillis >= FixedRateBarrier.minSleepMillis) {
                try {
                    final long waitTime = Math.min(remainingMillis, 10L);
                    this.wait(waitTime);
                    continue;
                }
                catch (final InterruptedException e) {
                    Debug.debugException(e);
                    Thread.currentThread().interrupt();
                    return this.shutdownRequested;
                }
            }
            Thread.yield();
        }
        return this.shutdownRequested;
    }
    
    public synchronized ObjectPair<Long, Integer> getTargetRate() {
        return new ObjectPair<Long, Integer>(this.intervalDurationNanos / 1000000L, this.perInterval);
    }
    
    public void shutdownRequested() {
        this.shutdownRequested = true;
    }
    
    public boolean isShutdownRequested() {
        return this.shutdownRequested;
    }
    
    static {
        final List<Long> minSleepMillisMeasurements = new ArrayList<Long>(11);
        for (int i = 0; i < 11; ++i) {
            final long timeBefore = System.currentTimeMillis();
            try {
                Thread.sleep(1L);
            }
            catch (final InterruptedException e) {
                Debug.debugException(e);
            }
            final long sleepMillis = System.currentTimeMillis() - timeBefore;
            minSleepMillisMeasurements.add(sleepMillis);
        }
        Collections.sort(minSleepMillisMeasurements);
        final long medianSleepMillis = minSleepMillisMeasurements.get(minSleepMillisMeasurements.size() / 2);
        minSleepMillis = Math.max(medianSleepMillis, 1L);
        final String message = "Calibrated FixedRateBarrier to use minSleepMillis=" + FixedRateBarrier.minSleepMillis + ".  " + "Minimum sleep measurements = " + minSleepMillisMeasurements;
        Debug.debug(Level.INFO, DebugType.OTHER, message);
    }
}
