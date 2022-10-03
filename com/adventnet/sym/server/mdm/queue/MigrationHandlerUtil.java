package com.adventnet.sym.server.mdm.queue;

import com.me.devicemanagement.framework.server.queue.DCQueue;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.queue.DCQueueHandler;
import java.util.logging.Logger;

public class MigrationHandlerUtil
{
    public static MigrationHandlerUtil handler;
    Logger mdmLogger;
    
    public MigrationHandlerUtil() {
        this.mdmLogger = Logger.getLogger("MDMLogger");
    }
    
    public static MigrationHandlerUtil getInstance() {
        return MigrationHandlerUtil.handler;
    }
    
    public boolean isDataPresentInQueue(final String queueName, final int qState) {
        Boolean isDataPresent = false;
        try {
            final DCQueue mdmQueue = DCQueueHandler.getQueue(queueName);
            final int dataCount = mdmQueue.getQueueDataCount(qState);
            if (dataCount > 0) {
                isDataPresent = true;
            }
            else {
                isDataPresent = false;
            }
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.SEVERE, "Cannot fetch count of items in DB", exp);
        }
        return isDataPresent;
    }
    
    public void pauseNewQueues() {
        final QueueName[] array;
        final QueueName[] newQueues = array = QueueName.class.getEnumConstants();
        for (final QueueName singleQueue : array) {
            this.pauseQueue(singleQueue.getQueueName());
        }
    }
    
    public void pauseQueue(final String qName) {
        try {
            final DCQueue mdmQueue = DCQueueHandler.getQueue(qName);
            if (!mdmQueue.isQueueSuspended()) {
                mdmQueue.suspendQExecution();
            }
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.SEVERE, exp, () -> "Cannot fetch new queue for pausing " + s);
        }
    }
    
    public void resumeNewQueues() {
        final QueueName[] array;
        final QueueName[] newQueues = array = QueueName.class.getEnumConstants();
        for (final QueueName singleQueue : array) {
            this.resumeQueue(singleQueue.getQueueName());
        }
    }
    
    public void resumeQueue(final String qName) {
        try {
            final DCQueue mdmQueue = DCQueueHandler.getQueue(qName);
            if (mdmQueue.isQueueSuspended()) {
                mdmQueue.resumeQExecution();
            }
        }
        catch (final Exception exp) {
            this.mdmLogger.log(Level.SEVERE, exp, () -> "Cannot fetch new queue for pausing " + s);
        }
    }
    
    static {
        MigrationHandlerUtil.handler = new MigrationHandlerUtil();
    }
}
