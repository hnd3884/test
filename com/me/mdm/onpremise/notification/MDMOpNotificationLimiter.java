package com.me.mdm.onpremise.notification;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.mdm.server.notification.MDMNotificationLimiter;

public class MDMOpNotificationLimiter implements MDMNotificationLimiter
{
    private static Logger logger;
    private long lastClearedTime;
    private long notifiedCount;
    private int batchCount;
    private long perTimeCount;
    private int sleepCount;
    
    public MDMOpNotificationLimiter() {
        this.lastClearedTime = 0L;
        this.notifiedCount = 0L;
        this.batchCount = 0;
        this.perTimeCount = 0L;
        this.sleepCount = 0;
    }
    
    public void checkAndLimitNotification(final String key, final int count) {
        try {
            final long notificationLimitCount = this.getNotificationLimitCount(key);
            final long timingInprogress = System.currentTimeMillis() - this.lastClearedTime;
            final int timeInterval = this.getTimeIntervalForNotification(key);
            if (timingInprogress < timeInterval * 1000) {
                if (this.notifiedCount > notificationLimitCount) {
                    final long timeToSleep = timeInterval * 1000 - timingInprogress;
                    MDMOpNotificationLimiter.logger.log(Level.SEVERE, "Thread going to sleep for {0} - {1}", new Object[] { timeToSleep, key });
                    Thread.sleep(timeToSleep);
                }
                this.notifiedCount += count;
            }
            else {
                this.notifiedCount = 0L;
                this.lastClearedTime = System.currentTimeMillis();
                this.notifiedCount += count;
            }
        }
        catch (final InterruptedException e) {
            MDMOpNotificationLimiter.logger.log(Level.SEVERE, "Exception in notification limit", e);
        }
    }
    
    public int getNotificationBatchCount(final String key) {
        try {
            if (this.batchCount == 0) {
                final String batchKey = key + "_batch_count";
                final String batchKeyCount = MDMUtil.getInstance().getMDMApplicationProperties().getProperty(batchKey);
                this.batchCount = Integer.valueOf(batchKeyCount);
            }
        }
        catch (final Exception ex) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getNotificationBatchCount", ex);
            this.batchCount = 100;
        }
        return this.batchCount;
    }
    
    private long getNotificationLimitCount(final String key) {
        try {
            if (this.perTimeCount == 0L) {
                final String limitKey = key + "_limit_count";
                final String limitCount = MDMUtil.getInstance().getMDMApplicationProperties().getProperty(limitKey);
                this.perTimeCount = Long.valueOf(limitCount);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getNotification per minute", e);
            this.perTimeCount = 50L;
        }
        return this.perTimeCount;
    }
    
    private int getTimeIntervalForNotification(final String key) {
        try {
            if (this.sleepCount == 0) {
                final String intervalKey = key + "_limit_time";
                final String intervalSecond = MDMUtil.getInstance().getMDMApplicationProperties().getProperty(intervalKey);
                this.sleepCount = Integer.valueOf(intervalSecond);
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMLogger").log(Level.SEVERE, "Exception in getNotification per minute", e);
            this.sleepCount = 30;
        }
        return this.sleepCount;
    }
    
    static {
        MDMOpNotificationLimiter.logger = Logger.getLogger("MDMWakupReqLogger");
    }
}
