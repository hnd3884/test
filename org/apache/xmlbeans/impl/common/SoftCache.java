package org.apache.xmlbeans.impl.common;

import java.lang.ref.SoftReference;
import java.util.HashMap;

public class SoftCache
{
    private HashMap map;
    
    public SoftCache() {
        this.map = new HashMap();
    }
    
    public Object get(final Object key) {
        final SoftReference softRef = this.map.get(key);
        if (softRef == null) {
            return null;
        }
        return softRef.get();
    }
    
    public Object put(final Object key, final Object value) {
        final SoftReference softRef = this.map.put(key, new SoftReference(value));
        if (softRef == null) {
            return null;
        }
        final Object oldValue = softRef.get();
        softRef.clear();
        return oldValue;
    }
    
    public Object remove(final Object key) {
        final SoftReference softRef = this.map.remove(key);
        if (softRef == null) {
            return null;
        }
        final Object oldValue = softRef.get();
        softRef.clear();
        return oldValue;
    }
}
