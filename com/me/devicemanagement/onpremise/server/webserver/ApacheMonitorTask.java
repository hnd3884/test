package com.me.devicemanagement.onpremise.server.webserver;

import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.util.logging.Level;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.TimerTask;

public class ApacheMonitorTask extends TimerTask
{
    private Logger logger;
    private static boolean alreadyScheduled;
    private Timer scheduler;
    private long lastLogTime;
    private int logInterval;
    
    public ApacheMonitorTask() {
        this.logger = Logger.getLogger("WebServerControllerLogger");
        this.scheduler = null;
        this.lastLogTime = -1L;
        this.logInterval = 3;
        this.scheduler = new Timer();
        this.logger.log(Level.INFO, "Constructor of ApacheMonitorTask has been invoked.");
    }
    
    @Override
    public void run() {
        this.logger.log(Level.FINEST, "ApacheMonitorTask.run() has been invoked.");
        try {
            final long currTime = System.currentTimeMillis();
            if (this.lastLogTime != -1L) {
                if ((currTime - this.lastLogTime) / 3600000L > this.logInterval) {
                    this.logger.log(Level.INFO, "Apache Service Status: ");
                    this.lastLogTime = currTime;
                }
            }
            else {
                this.logger.log(Level.INFO, "Apache Service Status: ");
                this.lastLogTime = currTime;
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception while invoking Apache Monitor Task.", ex);
        }
    }
    
    private void schedule(int startDelay, int interval) {
        if (ApacheMonitorTask.alreadyScheduled) {
            this.logger.log(Level.WARNING, "Task is already scheduled. This will terminate the old schedule and initiate a new one");
            this.scheduler.cancel();
        }
        startDelay *= 1000;
        interval *= 1000;
        this.logger.log(Level.INFO, "Scheduling the task with the start delay: " + startDelay + " and interval: " + interval);
        this.scheduler.scheduleAtFixedRate(this, startDelay, interval);
        ApacheMonitorTask.alreadyScheduled = true;
    }
    
    public void schedule() {
        final int startDelay = 120;
        int interval = 120;
        interval = WebServerUtil.apachePingInterval;
        this.schedule(startDelay, interval);
    }
    
    public void suspend() {
        this.logger.log(Level.INFO, "Terminating the Apache Monitor Task...");
        if (this.scheduler != null) {
            this.scheduler.cancel();
        }
    }
    
    static {
        ApacheMonitorTask.alreadyScheduled = false;
    }
}
