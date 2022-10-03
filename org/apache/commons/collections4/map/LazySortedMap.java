package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.Map;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.Factory;
import java.util.SortedMap;

public class LazySortedMap<K, V> extends LazyMap<K, V> implements SortedMap<K, V>
{
    private static final long serialVersionUID = 2715322183617658933L;
    
    public static <K, V> LazySortedMap<K, V> lazySortedMap(final SortedMap<K, V> map, final Factory<? extends V> factory) {
        return new LazySortedMap<K, V>(map, factory);
    }
    
    public static <K, V> LazySortedMap<K, V> lazySortedMap(final SortedMap<K, V> map, final Transformer<? super K, ? extends V> factory) {
        return new LazySortedMap<K, V>(map, factory);
    }
    
    protected LazySortedMap(final SortedMap<K, V> map, final Factory<? extends V> factory) {
        super(map, factory);
    }
    
    protected LazySortedMap(final SortedMap<K, V> map, final Transformer<? super K, ? extends V> factory) {
        super(map, factory);
    }
    
    protected SortedMap<K, V> getSortedMap() {
        return (SortedMap)this.map;
    }
    
    @Override
    public K firstKey() {
        return this.getSortedMap().firstKey();
    }
    
    @Override
    public K lastKey() {
        return this.getSortedMap().lastKey();
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.getSortedMap().comparator();
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        final SortedMap<K, V> map = this.getSortedMap().subMap(fromKey, toKey);
        return new LazySortedMap((SortedMap<Object, Object>)map, (Transformer<? super Object, ?>)this.factory);
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        final SortedMap<K, V> map = this.getSortedMap().headMap(toKey);
        return new LazySortedMap((SortedMap<Object, Object>)map, (Transformer<? super Object, ?>)this.factory);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        final SortedMap<K, V> map = this.getSortedMap().tailMap(fromKey);
        return new LazySortedMap((SortedMap<Object, Object>)map, (Transformer<? super Object, ?>)this.factory);
    }
}
