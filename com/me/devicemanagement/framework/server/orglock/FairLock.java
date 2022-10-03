package com.me.devicemanagement.framework.server.orglock;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;

public class FairLock
{
    private int lockedCount;
    private Thread lockingThread;
    private List<QueueObject> waitingThreads;
    public static boolean enableLog;
    private String key;
    protected static final Logger LOGGER;
    
    public FairLock() {
        this.lockedCount = 0;
        this.lockingThread = null;
        this.waitingThreads = new ArrayList<QueueObject>();
        this.key = null;
    }
    
    public FairLock(final String key) {
        this.lockedCount = 0;
        this.lockingThread = null;
        this.waitingThreads = new ArrayList<QueueObject>();
        this.key = null;
        this.key = key;
    }
    
    public void lock() throws InterruptedException {
        if (this.lockingThread != null && this.lockingThread.equals(Thread.currentThread())) {
            ++this.lockedCount;
            FairLockThreadLocal.setLockObtainedTime(new String(this.key + "_TL"), System.currentTimeMillis(), this.lockedCount - 1);
            return;
        }
        final QueueObject queueObject = new QueueObject(this.key);
        boolean isLockedForThisThread = true;
        synchronized (this) {
            this.waitingThreads.add(queueObject);
            FairLockThreadLocal.setThreadAddedToWaitQueue(new String(this.key + "_TL"), System.currentTimeMillis());
        }
        while (isLockedForThisThread) {
            synchronized (this) {
                isLockedForThisThread = (this.lockedCount > 0 || this.waitingThreads.get(0) != queueObject);
                if (!isLockedForThisThread) {
                    this.lockedCount = 1;
                    this.waitingThreads.remove(queueObject);
                    this.lockingThread = Thread.currentThread();
                    FairLockThreadLocal.setLockObtainedTime(new String(this.key + "_TL"), System.currentTimeMillis(), this.lockedCount - 1);
                    if (FairLock.enableLog) {
                        FairLock.LOGGER.log(Level.INFO, "FairLock: Lock obtain::" + this.toString());
                    }
                    return;
                }
            }
            try {
                FairLockThreadLocal.setThreadStartedWaitTime(new String(this.key + "_TL"), System.currentTimeMillis());
                if (FairLock.enableLog) {
                    FairLock.LOGGER.log(Level.INFO, "FairLock: Lock wait::" + this.toString());
                }
                queueObject.doWait();
                continue;
            }
            catch (final InterruptedException e) {
                synchronized (this) {
                    this.waitingThreads.remove(queueObject);
                }
                if (FairLock.enableLog) {
                    FairLock.LOGGER.log(Level.SEVERE, "FairLock_alert: con't move to wait state::" + this.toString());
                }
                throw e;
            }
            break;
        }
    }
    
    public synchronized void unlock() {
        if (this.lockingThread != Thread.currentThread()) {
            throw new IllegalMonitorStateException("Calling thread has not locked this lock");
        }
        if (this.lockedCount > 1) {
            --this.lockedCount;
            FairLockThreadLocal.setUnLockedTime(new String(this.key + "_TL"), System.currentTimeMillis(), false);
            if (FairLock.enableLog) {
                FairLock.LOGGER.log(Level.INFO, "FairLock: unlock obtain" + this.toString());
            }
            return;
        }
        if (this.lockedCount == 1) {
            this.lockedCount = 0;
        }
        this.lockingThread = null;
        FairLockThreadLocal.setUnLockedTime(new String(this.key + "_TL"), System.currentTimeMillis(), this.waitingThreads.size() > 0);
        if (FairLock.enableLog) {
            FairLock.LOGGER.log(Level.INFO, "FairLock: unlock obtain" + this.toString());
        }
        if (this.waitingThreads.size() > 0) {
            this.waitingThreads.get(0).doNotify();
            if (FairLock.enableLog) {
                FairLock.LOGGER.log(Level.INFO, "FairLock: notify" + this.waitingThreads.get(0).toString());
            }
        }
    }
    
    static {
        FairLock.enableLog = false;
        LOGGER = Logger.getLogger(FairLock.class.getName());
    }
}
