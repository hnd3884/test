package com.me.devicemanagement.framework.server.orglock;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.ArrayList;

public class FairLockThreadLocal
{
    private String threadName;
    private String taskName;
    private Long threadAddedToWaitQueue;
    private Long threadStartedWaitTime;
    private Integer reentranceCount;
    private String objectcode;
    private ArrayList<Long> lockObtainedTime;
    private ArrayList<Long> unLockedTime;
    private Long waitQueueThreadNotifiedTime;
    private static ThreadLocal<ArrayList<FairLockThreadLocal>> fairLockThreadLocalStack;
    private static final Logger LOGGER;
    
    public FairLockThreadLocal() {
        this.lockObtainedTime = new ArrayList<Long>();
        this.unLockedTime = new ArrayList<Long>();
    }
    
    public FairLockThreadLocal(final String key) {
        this.lockObtainedTime = new ArrayList<Long>();
        this.unLockedTime = new ArrayList<Long>();
        this.threadName = key;
    }
    
    public static FairLockThreadLocal setFairLockThreadLocal(final String key, final FairLock fairLock) {
        ArrayList<FairLockThreadLocal> availableLocks = FairLockThreadLocal.fairLockThreadLocalStack.get();
        if (availableLocks == null) {
            availableLocks = new ArrayList<FairLockThreadLocal>();
        }
        final ArrayList<FairLockThreadLocal> filteredLocks = availableLocks.stream().filter(fairLockIdx -> fairLockIdx.getThreadName().equalsIgnoreCase(s) && fairLockIdx.unLockedTime.size() != fairLockIdx.lockObtainedTime.size()).collect((Collector<? super Object, ?, ArrayList<FairLockThreadLocal>>)Collectors.toList());
        if (filteredLocks.size() > 0) {
            return filteredLocks.get(0);
        }
        final FairLockThreadLocal fairLockThreadLocal = new FairLockThreadLocal(key);
        fairLockThreadLocal.objectcode = Integer.toString(fairLock.hashCode());
        availableLocks.add(fairLockThreadLocal);
        FairLockThreadLocal.fairLockThreadLocalStack.set(availableLocks);
        return fairLockThreadLocal;
    }
    
    private static FairLockThreadLocal getFairLockThreadLocal(final String key) {
        final ArrayList<FairLockThreadLocal> availableLocks = FairLockThreadLocal.fairLockThreadLocalStack.get();
        if (availableLocks == null) {
            return null;
        }
        final ArrayList<FairLockThreadLocal> filteredLocks = availableLocks.stream().filter(fairLockIdx -> fairLockIdx.getThreadName().equalsIgnoreCase(s) && (fairLockIdx.unLockedTime.size() != fairLockIdx.lockObtainedTime.size() || fairLockIdx.getLockObtainedTime().size() == 0)).collect((Collector<? super Object, ?, ArrayList<FairLockThreadLocal>>)Collectors.toList());
        if (filteredLocks.size() == 0 || filteredLocks.size() > 1) {
            return null;
        }
        if (filteredLocks.size() == 1) {
            return filteredLocks.get(0);
        }
        return null;
    }
    
    public static ArrayList<FairLockThreadLocal> getAllFairLockThreadLocal() {
        final ArrayList<FairLockThreadLocal> allThreadLocals = FairLockThreadLocal.fairLockThreadLocalStack.get();
        FairLockThreadLocal.fairLockThreadLocalStack.remove();
        return allThreadLocals;
    }
    
    public static void setUnLockedTime(final String key, final Long unLockedTime, final boolean threadNotified) {
        final FairLockThreadLocal fairLockThreadLocal = getFairLockThreadLocal(key);
        if (fairLockThreadLocal != null) {
            if (threadNotified) {
                fairLockThreadLocal.waitQueueThreadNotifiedTime = unLockedTime;
            }
            fairLockThreadLocal.unLockedTime.add(unLockedTime);
        }
    }
    
    public static void setThreadAddedToWaitQueue(final String key, final Long threadAddedToWaitQueue) {
        final FairLockThreadLocal fairLockThreadLocal = getFairLockThreadLocal(key);
        if (fairLockThreadLocal != null) {
            fairLockThreadLocal.threadAddedToWaitQueue = threadAddedToWaitQueue;
        }
    }
    
    public static void setLockObtainedTime(final String key, final Long lockObtainedTime, final Integer reentrance) {
        final FairLockThreadLocal fairLockThreadLocal = getFairLockThreadLocal(key);
        if (fairLockThreadLocal != null) {
            fairLockThreadLocal.reentranceCount = reentrance;
            fairLockThreadLocal.lockObtainedTime.add(lockObtainedTime);
        }
    }
    
    public static void setThreadStartedWaitTime(final String key, final Long threadStartedWaitTime) {
        final FairLockThreadLocal fairLockThreadLocal = getFairLockThreadLocal(key);
        if (fairLockThreadLocal != null) {
            fairLockThreadLocal.threadStartedWaitTime = threadStartedWaitTime;
        }
    }
    
    private static void printThreadLocalDetails(final ArrayList<FairLockThreadLocal> fairLockThreadLocals) {
        for (final FairLockThreadLocal ftl : fairLockThreadLocals) {
            FairLockThreadLocal.LOGGER.log(Level.INFO, "Task Name : " + ftl.getTaskName() + " : " + "Thread Name : " + ftl.getThreadName() + " : " + "Thread Added to Wait Queue : " + ftl.getThreadAddedToWaitQueue() + " : " + "Thread Starting Waiting : " + ftl.getThreadStartedWaitTime() + " : " + "Lock Obtained : " + ftl.getLockObtainedTime() + " : " + "Reentrance Count : " + ftl.getReentranceCount() + " : " + "Thread UnLock Time : " + ftl.getUnLockedTime() + " : " + "Wait Thread Notified Time : " + ftl.getWaitQueueThreadNotifiedTime());
        }
    }
    
    public Long getThreadStartedWaitTime() {
        return this.threadStartedWaitTime;
    }
    
    public ArrayList<Long> getUnLockedTime() {
        return this.unLockedTime;
    }
    
    public Long getThreadAddedToWaitQueue() {
        return this.threadAddedToWaitQueue;
    }
    
    public Long getWaitQueueThreadNotifiedTime() {
        return this.waitQueueThreadNotifiedTime;
    }
    
    public ArrayList<Long> getLockObtainedTime() {
        return this.lockObtainedTime;
    }
    
    public String getThreadName() {
        return this.threadName;
    }
    
    public Integer getReentranceCount() {
        return this.reentranceCount;
    }
    
    public String gethashcode() {
        return this.objectcode;
    }
    
    public String getTaskName() {
        return this.taskName;
    }
    
    static {
        FairLockThreadLocal.fairLockThreadLocalStack = new ThreadLocal<ArrayList<FairLockThreadLocal>>();
        LOGGER = Logger.getLogger(FairLockThreadLocal.class.getName());
    }
}
