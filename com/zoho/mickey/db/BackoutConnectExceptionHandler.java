package com.zoho.mickey.db;

import java.time.temporal.Temporal;
import java.time.Duration;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;

public class BackoutConnectExceptionHandler extends DefaultConnectExceptionHandler
{
    protected LocalDateTime lastLogTime;
    protected int failCount;
    protected long maxWaitSeconds;
    protected long[] series;
    
    public BackoutConnectExceptionHandler() {
        this.maxWaitSeconds = TimeUnit.MINUTES.toSeconds(1L);
        this.series = this.generateSeries();
    }
    
    protected long[] generateSeries() {
        long start = 1L;
        long next = 2L;
        final long max = this.maxWaitSeconds;
        final double factor = this.factor();
        final List<Long> fibsList = new LinkedList<Long>();
        while (next * factor < max) {
            fibsList.add(Math.round(next * factor));
            final long newLast = start + next;
            start = next;
            next = newLast;
        }
        final int n = fibsList.size();
        final long[] fibs = new long[n];
        for (int i = 0; i < n; ++i) {
            fibs[i] = fibsList.get(i);
        }
        return fibs;
    }
    
    protected double factor() {
        return 1.0;
    }
    
    @Override
    protected synchronized void operationForNoManagedConnection() {
        final LocalDateTime currentTime = LocalDateTime.now();
        boolean needToDump;
        if (this.lastLogTime == null) {
            this.failCount = 0;
            needToDump = true;
        }
        else {
            final long remainSec = Duration.between(this.lastLogTime, currentTime).getSeconds();
            if (remainSec >= this.maxWaitSeconds) {
                this.failCount = 0;
                needToDump = true;
            }
            else if (this.failCount == this.series.length) {
                this.failCount = 0;
                needToDump = true;
            }
            else {
                final long upperBound = this.series[this.failCount - 1];
                needToDump = (remainSec >= upperBound);
            }
        }
        if (needToDump) {
            this.dumpInfo();
            this.lastLogTime = currentTime;
            ++this.failCount;
        }
        else {
            BackoutConnectExceptionHandler.LOGGER.info("Skipping information dump");
        }
    }
}
