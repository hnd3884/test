package org.apache.lucene.facet.taxonomy;

import java.util.Map;
import java.util.LinkedHashMap;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V>
{
    private int maxSize;
    
    public LRUHashMap(final int maxSize) {
        super(16, 0.75f, true);
        this.maxSize = maxSize;
    }
    
    public int getMaxSize() {
        return this.maxSize;
    }
    
    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return this.size() > this.maxSize;
    }
    
    @Override
    public LRUHashMap<K, V> clone() {
        return (LRUHashMap)super.clone();
    }
}
