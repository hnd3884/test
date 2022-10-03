package org.apache.commons.compress.harmony.unpack200;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.IdentityHashMap;

public class SegmentConstantPoolArrayCache
{
    protected IdentityHashMap knownArrays;
    protected List lastIndexes;
    protected String[] lastArray;
    protected String lastKey;
    
    public SegmentConstantPoolArrayCache() {
        this.knownArrays = new IdentityHashMap(1000);
    }
    
    public List indexesForArrayKey(final String[] array, final String key) {
        if (!this.arrayIsCached(array)) {
            this.cacheArray(array);
        }
        if (this.lastArray == array && this.lastKey == key) {
            return this.lastIndexes;
        }
        this.lastArray = array;
        this.lastKey = key;
        return this.lastIndexes = this.knownArrays.get(array).indexesForKey(key);
    }
    
    protected boolean arrayIsCached(final String[] array) {
        if (!this.knownArrays.containsKey(array)) {
            return false;
        }
        final CachedArray cachedArray = this.knownArrays.get(array);
        return cachedArray.lastKnownSize() == array.length;
    }
    
    protected void cacheArray(final String[] array) {
        if (this.arrayIsCached(array)) {
            throw new IllegalArgumentException("Trying to cache an array that already exists");
        }
        this.knownArrays.put(array, new CachedArray(array));
        this.lastArray = null;
    }
    
    protected class CachedArray
    {
        String[] primaryArray;
        int lastKnownSize;
        HashMap primaryTable;
        
        public CachedArray(final String[] array) {
            this.primaryArray = array;
            this.lastKnownSize = array.length;
            this.primaryTable = new HashMap(this.lastKnownSize);
            this.cacheIndexes();
        }
        
        public int lastKnownSize() {
            return this.lastKnownSize;
        }
        
        public List indexesForKey(final String key) {
            if (!this.primaryTable.containsKey(key)) {
                return Collections.EMPTY_LIST;
            }
            return this.primaryTable.get(key);
        }
        
        protected void cacheIndexes() {
            for (int index = 0; index < this.primaryArray.length; ++index) {
                final String key = this.primaryArray[index];
                if (!this.primaryTable.containsKey(key)) {
                    this.primaryTable.put(key, new ArrayList());
                }
                this.primaryTable.get(key).add(index);
            }
        }
    }
}
