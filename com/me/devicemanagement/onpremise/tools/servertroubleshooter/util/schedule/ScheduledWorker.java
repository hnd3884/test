package com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.schedule;

import java.util.concurrent.TimeUnit;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.Executors;
import com.me.devicemanagement.onpremise.tools.servertroubleshooter.util.STSToolUtil;
import java.util.logging.Level;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;

public class ScheduledWorker
{
    private static final Logger LOGGER;
    private static ScheduledWorker scheduledWorkerObj;
    private ScheduledExecutorService scheduledWorker;
    
    private ScheduledWorker() {
        this.scheduledWorker = null;
        this.initializeSchedulerWorkerPool();
    }
    
    public static ScheduledWorker getInstance() {
        if (ScheduledWorker.scheduledWorkerObj == null) {
            ScheduledWorker.scheduledWorkerObj = new ScheduledWorker();
        }
        return ScheduledWorker.scheduledWorkerObj;
    }
    
    private void initializeSchedulerWorkerPool() {
        try {
            ScheduledWorker.LOGGER.log(Level.FINE, "Getting scheduler worker pool size from conf file");
            final String schedulerWorkerpoolSizeAsString = STSToolUtil.getSTSConfFileProps().getProperty("sts.tool.scheduler.workerpool.size", new String("10"));
            final int schedulerWorkerpoolSize = Integer.parseInt(schedulerWorkerpoolSizeAsString.trim());
            ScheduledWorker.LOGGER.log(Level.FINE, "Scheduler Worker pool size from conf file : {0}", schedulerWorkerpoolSize);
            this.scheduledWorker = Executors.newScheduledThreadPool(schedulerWorkerpoolSize);
            ScheduledWorker.LOGGER.log(Level.INFO, "Initialized Scheduler Worker pool with size : {0}", schedulerWorkerpoolSize);
        }
        catch (final Exception ex) {
            ScheduledWorker.LOGGER.log(Level.WARNING, "Exception while initializing scheduler worker pool", ex);
        }
    }
    
    public void scheduleDaily(final ScheduledWorkerTask task, final Calendar time) {
        ScheduledWorker.LOGGER.log(Level.FINE, "Creating daily scheduler for " + task.getSchedulerName());
        final Date nextRuntime = this.getNextRuntime(time);
        final long initialDelay = nextRuntime.getTime() - new Date().getTime();
        this.scheduledWorker.scheduleAtFixedRate(task, initialDelay, TimeUnit.DAYS.toMillis(1L), TimeUnit.MILLISECONDS);
        ScheduledWorker.LOGGER.log(Level.INFO, "Started daily scheduler. Scheduler name : {0}, Next runtime : {1}", new Object[] { task.getSchedulerName(), nextRuntime });
    }
    
    public void scheduleOnce(final ScheduledWorkerTask task, final Calendar time) {
        ScheduledWorker.LOGGER.log(Level.FINE, "Creating one time scheduler for " + task.getSchedulerName());
        final Date nextRuntime = time.getTime();
        final Date currentTime = new Date();
        if (currentTime.compareTo(nextRuntime) >= 0) {
            ScheduledWorker.LOGGER.log(Level.INFO, "Specified time {0} is already expired. Ignoring it", nextRuntime);
        }
        else {
            final long initialDelay = nextRuntime.getTime() - new Date().getTime();
            this.scheduledWorker.schedule(task, initialDelay, TimeUnit.MILLISECONDS);
            ScheduledWorker.LOGGER.log(Level.INFO, "Started one time scheduler. Scheduler name : {0}, Next runtime : {1}", new Object[] { task.getSchedulerName(), nextRuntime });
        }
    }
    
    private Date getNextRuntime(final Calendar time) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(11, time.get(11));
        calendar.set(12, time.get(12));
        calendar.set(13, time.get(13));
        Date nextRuntime = calendar.getTime();
        final Date currentTime = new Date();
        if (currentTime.compareTo(nextRuntime) >= 0) {
            calendar.add(5, 1);
            nextRuntime = calendar.getTime();
        }
        return nextRuntime;
    }
    
    static {
        LOGGER = Logger.getLogger(ScheduledWorker.class.getName());
        ScheduledWorker.scheduledWorkerObj = null;
    }
}
