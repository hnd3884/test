package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import java.util.HashMap;
import java.util.Map;

class LargeContainer extends Container
{
    Map items;
    
    LargeContainer(final int size) {
        this.items = new HashMap(size * 2 + 1);
        this.values = new OneAttr[size];
    }
    
    @Override
    void put(final String key, final OneAttr value) {
        this.items.put(key, value);
        this.values[this.pos++] = value;
    }
    
    @Override
    OneAttr get(final String key) {
        final OneAttr ret = this.items.get(key);
        return ret;
    }
}
