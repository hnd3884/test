package org.antlr.v4.runtime.misc;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;

public class MultiMap<K, V> extends LinkedHashMap<K, List<V>>
{
    public void map(final K key, final V value) {
        List<V> elementsForKey = this.get(key);
        if (elementsForKey == null) {
            elementsForKey = new ArrayList<V>();
            super.put(key, elementsForKey);
        }
        elementsForKey.add(value);
    }
    
    public List<Pair<K, V>> getPairs() {
        final List<Pair<K, V>> pairs = new ArrayList<Pair<K, V>>();
        for (final K key : this.keySet()) {
            for (final V value : this.get(key)) {
                pairs.add(new Pair<K, V>(key, value));
            }
        }
        return pairs;
    }
}
