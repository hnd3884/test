package com.adventnet.ds.adapter;

import java.util.HashMap;

public class MDSContext
{
    private HashMap context;
    
    public MDSContext() {
        this.context = new HashMap();
    }
    
    public void add(final Object key, final Object value) {
        this.context.put(key, value);
    }
    
    public Object get(final Object key) {
        return this.context.get(key);
    }
    
    public Object remove(final Object key) {
        return this.context.remove(key);
    }
    
    @Override
    public String toString() {
        return super.toString() + this.context;
    }
}
