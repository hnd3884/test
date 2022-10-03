package com.me.mdm.server.ios.apns;

import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.List;
import java.util.logging.Logger;

public class LRUAPNSConnectionMap
{
    static final Logger LOGGER;
    static List<LRUAPNSConnectionMap> lruApnsConnectionMapList;
    private long hitCount;
    private long missCount;
    private long lastUpdatedTime;
    private LRUMap linkedLRUMap;
    private static Integer lruMapSize;
    
    public LRUAPNSConnectionMap() {
        this.lastUpdatedTime = System.currentTimeMillis();
        this.linkedLRUMap = new LRUMap(LRUAPNSConnectionMap.lruMapSize);
        LRUAPNSConnectionMap.lruApnsConnectionMapList.add(this);
    }
    
    private static void init() {
        try {
            final String size = MDMUtil.getInstance().getMDMApplicationProperties().getProperty("pushyclient.cacheMaxSize");
            LRUAPNSConnectionMap.lruMapSize = Integer.parseInt(size);
        }
        catch (final Exception e) {
            LRUAPNSConnectionMap.LOGGER.log(Level.SEVERE, "Exception occurred while initiating the connection map ", e);
        }
    }
    
    public APNsWakeUpProcessorWrapper addOrGet(final String key) {
        final APNsWakeUpProcessorWrapper value = (APNsWakeUpProcessorWrapper)this.linkedLRUMap.get(key);
        this.updateCount(value);
        if (value == null) {
            return this.add(key);
        }
        return value;
    }
    
    private void updateCount(final Object value) {
        if (value != null) {
            ++this.hitCount;
        }
        else {
            ++this.missCount;
        }
        if (this.lastUpdatedTime + 3600000L < System.currentTimeMillis()) {
            LRUAPNSConnectionMap.LOGGER.log(Level.INFO, "APNS Connection Cache Hit Count - {0} , Miss Count - {1}", new Object[] { this.hitCount, this.missCount });
            this.hitCount = 0L;
            this.missCount = 0L;
            this.lastUpdatedTime = System.currentTimeMillis();
        }
    }
    
    public APNsWakeUpProcessorWrapper add(final String key) {
        APNsWakeUpProcessorWrapper apNsWakeUpProcessorWrapper = null;
        if (this.linkedLRUMap.size() < LRUAPNSConnectionMap.lruMapSize) {
            this.linkedLRUMap.put(key, apNsWakeUpProcessorWrapper = APNsWakeUpProcessorWrapper.getInstance());
        }
        else {
            final Iterator<Map.Entry> itr = this.linkedLRUMap.entrySet().iterator();
            while (itr.hasNext()) {
                final Map.Entry eldest = itr.next();
                final APNsWakeUpProcessorWrapper apNsWakeUpProcessorWrapper2 = eldest.getValue();
                if (!apNsWakeUpProcessorWrapper2.queueInProgress()) {
                    itr.remove();
                    apNsWakeUpProcessorWrapper2.stopQueue();
                    this.linkedLRUMap.put(key, apNsWakeUpProcessorWrapper = APNsWakeUpProcessorWrapper.getInstance());
                    break;
                }
            }
        }
        if (apNsWakeUpProcessorWrapper == null) {
            LRUAPNSConnectionMap.LOGGER.info("Queue Map is Full.. So using Previous Flow For this Time..");
        }
        return apNsWakeUpProcessorWrapper;
    }
    
    public static boolean removeIfExists(final String key) {
        for (final LRUAPNSConnectionMap lruapnsConnectionMap : LRUAPNSConnectionMap.lruApnsConnectionMapList) {
            final APNsWakeUpProcessorWrapper apNsWakeUpProcessorWrapper = (APNsWakeUpProcessorWrapper)lruapnsConnectionMap.linkedLRUMap.get(key);
            if (apNsWakeUpProcessorWrapper != null) {
                lruapnsConnectionMap.linkedLRUMap.remove(key);
                apNsWakeUpProcessorWrapper.stopQueue();
                return true;
            }
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(LRUAPNSConnectionMap.class.getName());
        LRUAPNSConnectionMap.lruApnsConnectionMapList = new ArrayList<LRUAPNSConnectionMap>();
        LRUAPNSConnectionMap.lruMapSize = null;
        init();
    }
    
    class LRUMap<String, APNsWakeUpProcessorWrapper> extends LinkedHashMap
    {
        private int maxSize;
        
        public LRUMap(final int maxSize) {
            super(10, 0.75f, true);
            this.maxSize = maxSize;
        }
        
        @Override
        protected boolean removeEldestEntry(final Map.Entry eldest) {
            return this.size() > this.maxSize;
        }
    }
}
