package com.adventnet.taskengine.internal;

import java.util.SortedMap;
import java.util.Iterator;
import java.util.HashMap;
import java.util.TreeMap;

public class QuickSortedMap
{
    private TreeMap keyMap;
    private HashMap valueMap;
    
    public QuickSortedMap() {
        this.keyMap = new TreeMap();
        this.valueMap = new HashMap();
    }
    
    public boolean isEmpty() {
        return !this.keyMap.keySet().iterator().hasNext();
    }
    
    public boolean containsValue(final Object value) {
        return this.valueMap.containsKey(value);
    }
    
    public boolean containsKey(final Object key) {
        return this.keyMap.containsKey(key);
    }
    
    public void put(final Object key, final Object taskInputID) {
        this.keyMap.put(key, taskInputID);
        this.valueMap.put(taskInputID, key);
    }
    
    public Object firstKey() {
        return this.keyMap.firstKey();
    }
    
    public Iterator<Long> keySet() {
        return new TreeMap<Long, Object>(this.keyMap).keySet().iterator();
    }
    
    public Object getValue(final Object key) {
        return this.keyMap.get(key);
    }
    
    public Object remove(final Object key) {
        final Object value = this.keyMap.remove(key);
        this.valueMap.remove(value);
        return value;
    }
    
    public Object removeValue(final Object value) {
        final Object key = this.valueMap.remove(value);
        if (key != null) {
            this.keyMap.remove(key);
        }
        return key;
    }
    
    public int size() {
        return this.keyMap.size();
    }
    
    @Override
    public String toString() {
        return this.keyMap.toString();
    }
}
