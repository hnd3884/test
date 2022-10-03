package com.adventnet.iam.security;

import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.logging.Level;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.TimerTask;

public class AccessInfoExpiryHandler extends TimerTask
{
    static final Logger LOGGER;
    public static final long INVALID_TIME_INTERVAL = -1L;
    public static final long MINIMUM_SCHEDULE_TIME_INTERVAL_MILLIS = 3600000L;
    static AccessInfoExpiryHandler accessInfoExpiryHandlerTask;
    static Timer timer;
    static boolean scheduled;
    boolean inProgress;
    
    public AccessInfoExpiryHandler() {
        this.inProgress = false;
    }
    
    public static synchronized void schedule(long timeInterValInMillis) {
        if (!AccessInfoExpiryHandler.scheduled) {
            if (timeInterValInMillis < 3600000L) {
                AccessInfoExpiryHandler.LOGGER.log(Level.WARNING, "Configured Time Interval {0} is less than MINIMUM INTERVAL {1}", new Object[] { timeInterValInMillis, 3600000L });
                timeInterValInMillis = 3600000L;
            }
            AccessInfoExpiryHandler.timer = new Timer();
            AccessInfoExpiryHandler.accessInfoExpiryHandlerTask = new AccessInfoExpiryHandler();
            AccessInfoExpiryHandler.timer.schedule(AccessInfoExpiryHandler.accessInfoExpiryHandlerTask, timeInterValInMillis, timeInterValInMillis);
            AccessInfoExpiryHandler.scheduled = true;
            AccessInfoExpiryHandler.LOGGER.log(Level.WARNING, "Scheduling for Time Interval {0}", new Object[] { timeInterValInMillis });
        }
    }
    
    public static boolean isScheduled() {
        return AccessInfoExpiryHandler.scheduled;
    }
    
    @Override
    public void run() {
        this.removeExpiredAccessInfoEntries(InMemCacheAccessInfo.ACCESSINFO_CACHE);
    }
    
    public void removeExpiredAccessInfoEntries(final LRUCacheMap<String, InMemCacheAccessInfo> accessInfoMap) throws UnsupportedOperationException {
        if (this.inProgress) {
            AccessInfoExpiryHandler.LOGGER.log(Level.WARNING, "Cannot reschedule when expiry is in progress");
            return;
        }
        final Set<Map.Entry<String, LRUCacheMap.ValueWrap<String, InMemCacheAccessInfo>>> set = accessInfoMap.getAllEntriesSet();
        if (set.size() == 0) {
            return;
        }
        this.inProgress = true;
        int totalEntries = 0;
        int removedEntries = 0;
        long totalWeight = 0L;
        long removedWeight = 0L;
        final Iterator<Map.Entry<String, LRUCacheMap.ValueWrap<String, InMemCacheAccessInfo>>> it = set.iterator();
        final long shedulerRunTime = System.currentTimeMillis();
        try {
            while (it.hasNext()) {
                final Map.Entry<String, LRUCacheMap.ValueWrap<String, InMemCacheAccessInfo>> entry = it.next();
                final InMemCacheAccessInfo accessInfo = (InMemCacheAccessInfo)entry.getValue().value;
                final long tmpWeight = accessInfo.getWeight();
                if (accessInfo.isExpired(shedulerRunTime) && accessInfoMap.remove(entry.getKey()) != null) {
                    ++removedEntries;
                    removedWeight += tmpWeight;
                }
                ++totalEntries;
                totalWeight += tmpWeight;
            }
        }
        catch (final ClassCastException ce) {
            AccessInfoExpiryHandler.LOGGER.log(Level.SEVERE, "Only AccessInfo entries supported {0}", new Object[] { ce.getMessage() });
            return;
        }
        catch (final Exception e) {
            AccessInfoExpiryHandler.LOGGER.log(Level.WARNING, "Expired AccessInfo Cleanup Error {0}", new Object[] { e.getMessage() });
            return;
        }
        AccessInfoExpiryHandler.LOGGER.log(Level.INFO, "Total EntriesCount : {0} Weight : {1} , Removed EntriesCount : {2} , Weight : {3} ", new Object[] { totalEntries, totalWeight, removedEntries, removedWeight });
        this.inProgress = false;
    }
    
    public static void shutdown() {
        if (AccessInfoExpiryHandler.scheduled && AccessInfoExpiryHandler.accessInfoExpiryHandlerTask != null) {
            AccessInfoExpiryHandler.LOGGER.log(Level.WARNING, "Shutting down AccessInfoExpiryHandler Permanently");
            AccessInfoExpiryHandler.accessInfoExpiryHandlerTask.cancel();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AccessInfoExpiryHandler.class.getName());
        AccessInfoExpiryHandler.accessInfoExpiryHandlerTask = null;
        AccessInfoExpiryHandler.timer = null;
        AccessInfoExpiryHandler.scheduled = false;
    }
}
