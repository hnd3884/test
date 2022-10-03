package org.apache.taglibs.standard.lang.jstl;

import java.util.HashMap;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Set;
import java.util.Map;

public abstract class EnumeratedMap implements Map
{
    Map mMap;
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public boolean containsKey(final Object pKey) {
        return this.getValue(pKey) != null;
    }
    
    public boolean containsValue(final Object pValue) {
        return this.getAsMap().containsValue(pValue);
    }
    
    public Set entrySet() {
        return this.getAsMap().entrySet();
    }
    
    public Object get(final Object pKey) {
        return this.getValue(pKey);
    }
    
    public boolean isEmpty() {
        return !this.enumerateKeys().hasMoreElements();
    }
    
    public Set keySet() {
        return this.getAsMap().keySet();
    }
    
    public Object put(final Object pKey, final Object pValue) {
        throw new UnsupportedOperationException();
    }
    
    public void putAll(final Map pMap) {
        throw new UnsupportedOperationException();
    }
    
    public Object remove(final Object pKey) {
        throw new UnsupportedOperationException();
    }
    
    public int size() {
        return this.getAsMap().size();
    }
    
    public Collection values() {
        return this.getAsMap().values();
    }
    
    public abstract Enumeration enumerateKeys();
    
    public abstract boolean isMutable();
    
    public abstract Object getValue(final Object p0);
    
    public Map getAsMap() {
        if (this.mMap != null) {
            return this.mMap;
        }
        final Map m = this.convertToMap();
        if (!this.isMutable()) {
            this.mMap = m;
        }
        return m;
    }
    
    Map convertToMap() {
        final Map ret = new HashMap();
        final Enumeration e = this.enumerateKeys();
        while (e.hasMoreElements()) {
            final Object key = e.nextElement();
            final Object value = this.getValue(key);
            ret.put(key, value);
        }
        return ret;
    }
}
