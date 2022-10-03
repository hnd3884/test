package org.apache.commons.collections4.bidimap;

import java.util.Map;
import org.apache.commons.collections4.BidiMap;
import java.util.SortedMap;
import java.util.Comparator;
import org.apache.commons.collections4.OrderedBidiMap;
import org.apache.commons.collections4.SortedBidiMap;

public abstract class AbstractSortedBidiMapDecorator<K, V> extends AbstractOrderedBidiMapDecorator<K, V> implements SortedBidiMap<K, V>
{
    public AbstractSortedBidiMapDecorator(final SortedBidiMap<K, V> map) {
        super(map);
    }
    
    @Override
    protected SortedBidiMap<K, V> decorated() {
        return (SortedBidiMap)super.decorated();
    }
    
    @Override
    public SortedBidiMap<V, K> inverseBidiMap() {
        return this.decorated().inverseBidiMap();
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.decorated().comparator();
    }
    
    @Override
    public Comparator<? super V> valueComparator() {
        return this.decorated().valueComparator();
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return this.decorated().subMap(fromKey, toKey);
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return this.decorated().headMap(toKey);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return this.decorated().tailMap(fromKey);
    }
}
