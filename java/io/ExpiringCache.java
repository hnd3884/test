package java.io;

import java.util.Iterator;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Map;

class ExpiringCache
{
    private long millisUntilExpiration;
    private Map<String, Entry> map;
    private int queryCount;
    private int queryOverflow;
    private int MAX_ENTRIES;
    
    ExpiringCache() {
        this(30000L);
    }
    
    ExpiringCache(final long millisUntilExpiration) {
        this.queryOverflow = 300;
        this.MAX_ENTRIES = 200;
        this.millisUntilExpiration = millisUntilExpiration;
        this.map = new LinkedHashMap<String, Entry>() {
            @Override
            protected boolean removeEldestEntry(final Map.Entry<String, ExpiringCache.Entry> entry) {
                return this.size() > ExpiringCache.this.MAX_ENTRIES;
            }
        };
    }
    
    synchronized String get(final String s) {
        if (++this.queryCount >= this.queryOverflow) {
            this.cleanup();
        }
        final Entry entry = this.entryFor(s);
        if (entry != null) {
            return entry.val();
        }
        return null;
    }
    
    synchronized void put(final String s, final String val) {
        if (++this.queryCount >= this.queryOverflow) {
            this.cleanup();
        }
        final Entry entry = this.entryFor(s);
        if (entry != null) {
            entry.setTimestamp(System.currentTimeMillis());
            entry.setVal(val);
        }
        else {
            this.map.put(s, new Entry(System.currentTimeMillis(), val));
        }
    }
    
    synchronized void clear() {
        this.map.clear();
    }
    
    private Entry entryFor(final String s) {
        Entry entry = this.map.get(s);
        if (entry != null) {
            final long n = System.currentTimeMillis() - entry.timestamp();
            if (n < 0L || n >= this.millisUntilExpiration) {
                this.map.remove(s);
                entry = null;
            }
        }
        return entry;
    }
    
    private void cleanup() {
        final Set<String> keySet = this.map.keySet();
        final String[] array = new String[keySet.size()];
        int n = 0;
        final Iterator iterator = keySet.iterator();
        while (iterator.hasNext()) {
            array[n++] = (String)iterator.next();
        }
        for (int i = 0; i < array.length; ++i) {
            this.entryFor(array[i]);
        }
        this.queryCount = 0;
    }
    
    static class Entry
    {
        private long timestamp;
        private String val;
        
        Entry(final long timestamp, final String val) {
            this.timestamp = timestamp;
            this.val = val;
        }
        
        long timestamp() {
            return this.timestamp;
        }
        
        void setTimestamp(final long timestamp) {
            this.timestamp = timestamp;
        }
        
        String val() {
            return this.val;
        }
        
        void setVal(final String val) {
            this.val = val;
        }
    }
}
