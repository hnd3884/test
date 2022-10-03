package com.zoho.security.eventfw;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExecutionTimer
{
    private long startTime;
    private long stopTime;
    private long executionTime;
    public static final Logger LOGGER;
    
    public ExecutionTimer() {
        this.startTime = -1L;
        this.stopTime = -1L;
        this.executionTime = -1L;
    }
    
    public static ExecutionTimer startInstance() {
        final ExecutionTimer timer = new ExecutionTimer();
        timer.startTimer();
        return timer;
    }
    
    private void startTimer() {
        this.startTime = System.currentTimeMillis();
    }
    
    public void stop() {
        if (this.startTime != -1L) {
            this.stopTime = System.currentTimeMillis();
            this.executionTime = this.stopTime - this.startTime;
        }
        else {
            ExecutionTimer.LOGGER.log(Level.SEVERE, "Timer is not running. stop() called without starting the timer  ");
        }
    }
    
    public boolean isStopped() {
        return this.stopTime != -1L;
    }
    
    public long getExecutionTime() {
        if (this.startTime != -1L && !this.isStopped()) {
            this.stop();
        }
        return this.executionTime;
    }
    
    static {
        LOGGER = Logger.getLogger(ExecutionTimer.class.getName());
    }
}
