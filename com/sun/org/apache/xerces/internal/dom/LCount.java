package com.sun.org.apache.xerces.internal.dom;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

class LCount
{
    static final Map<String, LCount> lCounts;
    public int captures;
    public int bubbles;
    public int defaults;
    public int total;
    
    LCount() {
        this.captures = 0;
        this.bubbles = 0;
        this.total = 0;
    }
    
    static LCount lookup(final String evtName) {
        LCount lc = LCount.lCounts.get(evtName);
        if (lc == null) {
            LCount.lCounts.put(evtName, lc = new LCount());
        }
        return lc;
    }
    
    static {
        lCounts = new ConcurrentHashMap<String, LCount>();
    }
}
