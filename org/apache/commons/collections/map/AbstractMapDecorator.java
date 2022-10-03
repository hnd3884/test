package org.apache.commons.collections.map;

import java.util.Collection;
import java.util.Set;
import java.util.Map;

public abstract class AbstractMapDecorator implements Map
{
    protected transient Map map;
    
    protected AbstractMapDecorator() {
    }
    
    public AbstractMapDecorator(final Map map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null");
        }
        this.map = map;
    }
    
    protected Map getMap() {
        return this.map;
    }
    
    public void clear() {
        this.map.clear();
    }
    
    public boolean containsKey(final Object key) {
        return this.map.containsKey(key);
    }
    
    public boolean containsValue(final Object value) {
        return this.map.containsValue(value);
    }
    
    public Set entrySet() {
        return this.map.entrySet();
    }
    
    public Object get(final Object key) {
        return this.map.get(key);
    }
    
    public boolean isEmpty() {
        return this.map.isEmpty();
    }
    
    public Set keySet() {
        return this.map.keySet();
    }
    
    public Object put(final Object key, final Object value) {
        return this.map.put(key, value);
    }
    
    public void putAll(final Map mapToCopy) {
        this.map.putAll(mapToCopy);
    }
    
    public Object remove(final Object key) {
        return this.map.remove(key);
    }
    
    public int size() {
        return this.map.size();
    }
    
    public Collection values() {
        return this.map.values();
    }
    
    public boolean equals(final Object object) {
        return object == this || this.map.equals(object);
    }
    
    public int hashCode() {
        return this.map.hashCode();
    }
    
    public String toString() {
        return this.map.toString();
    }
}
