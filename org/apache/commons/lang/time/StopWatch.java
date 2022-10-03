package org.apache.commons.lang.time;

public class StopWatch
{
    private long startTime;
    private long stopTime;
    
    public StopWatch() {
        this.startTime = -1L;
        this.stopTime = -1L;
    }
    
    public void start() {
        this.stopTime = -1L;
        this.startTime = System.currentTimeMillis();
    }
    
    public void stop() {
        this.stopTime = System.currentTimeMillis();
    }
    
    public void reset() {
        this.startTime = -1L;
        this.stopTime = -1L;
    }
    
    public void split() {
        this.stopTime = System.currentTimeMillis();
    }
    
    public void unsplit() {
        this.stopTime = -1L;
    }
    
    public void suspend() {
        this.stopTime = System.currentTimeMillis();
    }
    
    public void resume() {
        this.startTime += System.currentTimeMillis() - this.stopTime;
        this.stopTime = -1L;
    }
    
    public long getTime() {
        if (this.stopTime != -1L) {
            return this.stopTime - this.startTime;
        }
        if (this.startTime == -1L) {
            return 0L;
        }
        return System.currentTimeMillis() - this.startTime;
    }
    
    public String toString() {
        return DurationFormatUtils.formatISO(this.getTime());
    }
}
