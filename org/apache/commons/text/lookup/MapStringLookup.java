package org.apache.commons.text.lookup;

import java.util.Map;

final class MapStringLookup<V> implements StringLookup
{
    private final Map<String, V> map;
    
    static <T> MapStringLookup<T> on(final Map<String, T> map) {
        return new MapStringLookup<T>(map);
    }
    
    private MapStringLookup(final Map<String, V> map) {
        this.map = map;
    }
    
    Map<String, V> getMap() {
        return this.map;
    }
    
    @Override
    public String lookup(final String key) {
        if (this.map == null) {
            return null;
        }
        V obj;
        try {
            obj = this.map.get(key);
        }
        catch (final NullPointerException e) {
            return null;
        }
        return (obj != null) ? obj.toString() : null;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [map=" + this.map + "]";
    }
}
