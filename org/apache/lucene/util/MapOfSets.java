package org.apache.lucene.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public class MapOfSets<K, V>
{
    private final Map<K, Set<V>> theMap;
    
    public MapOfSets(final Map<K, Set<V>> m) {
        this.theMap = m;
    }
    
    public Map<K, Set<V>> getMap() {
        return this.theMap;
    }
    
    public int put(final K key, final V val) {
        Set<V> theSet;
        if (this.theMap.containsKey(key)) {
            theSet = this.theMap.get(key);
        }
        else {
            theSet = new HashSet<V>(23);
            this.theMap.put(key, theSet);
        }
        theSet.add(val);
        return theSet.size();
    }
    
    public int putAll(final K key, final Collection<? extends V> vals) {
        Set<V> theSet;
        if (this.theMap.containsKey(key)) {
            theSet = this.theMap.get(key);
        }
        else {
            theSet = new HashSet<V>(23);
            this.theMap.put(key, theSet);
        }
        theSet.addAll(vals);
        return theSet.size();
    }
}
