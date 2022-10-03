package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.Map;
import org.apache.commons.collections4.Transformer;
import java.util.SortedMap;

public class TransformedSortedMap<K, V> extends TransformedMap<K, V> implements SortedMap<K, V>
{
    private static final long serialVersionUID = -8751771676410385778L;
    
    public static <K, V> TransformedSortedMap<K, V> transformingSortedMap(final SortedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        return new TransformedSortedMap<K, V>(map, keyTransformer, valueTransformer);
    }
    
    public static <K, V> TransformedSortedMap<K, V> transformedSortedMap(final SortedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        final TransformedSortedMap<K, V> decorated = new TransformedSortedMap<K, V>(map, keyTransformer, valueTransformer);
        if (map.size() > 0) {
            final Map<K, V> transformed = decorated.transformMap((Map<? extends K, ? extends V>)map);
            decorated.clear();
            decorated.decorated().putAll(transformed);
        }
        return decorated;
    }
    
    protected TransformedSortedMap(final SortedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        super(map, keyTransformer, valueTransformer);
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
        return new TransformedSortedMap((SortedMap<Object, Object>)map, (Transformer<? super Object, ?>)this.keyTransformer, (Transformer<? super Object, ?>)this.valueTransformer);
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        final SortedMap<K, V> map = this.getSortedMap().headMap(toKey);
        return new TransformedSortedMap((SortedMap<Object, Object>)map, (Transformer<? super Object, ?>)this.keyTransformer, (Transformer<? super Object, ?>)this.valueTransformer);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        final SortedMap<K, V> map = this.getSortedMap().tailMap(fromKey);
        return new TransformedSortedMap((SortedMap<Object, Object>)map, (Transformer<? super Object, ?>)this.keyTransformer, (Transformer<? super Object, ?>)this.valueTransformer);
    }
}
