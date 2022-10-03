package org.apache.commons.collections4.map;

import java.util.Comparator;
import java.util.Map;
import org.apache.commons.collections4.Predicate;
import java.util.SortedMap;

public class PredicatedSortedMap<K, V> extends PredicatedMap<K, V> implements SortedMap<K, V>
{
    private static final long serialVersionUID = 3359846175935304332L;
    
    public static <K, V> PredicatedSortedMap<K, V> predicatedSortedMap(final SortedMap<K, V> map, final Predicate<? super K> keyPredicate, final Predicate<? super V> valuePredicate) {
        return new PredicatedSortedMap<K, V>(map, keyPredicate, valuePredicate);
    }
    
    protected PredicatedSortedMap(final SortedMap<K, V> map, final Predicate<? super K> keyPredicate, final Predicate<? super V> valuePredicate) {
        super(map, keyPredicate, valuePredicate);
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
        return new PredicatedSortedMap((SortedMap<Object, Object>)map, (Predicate<? super Object>)this.keyPredicate, (Predicate<? super Object>)this.valuePredicate);
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        final SortedMap<K, V> map = this.getSortedMap().headMap(toKey);
        return new PredicatedSortedMap((SortedMap<Object, Object>)map, (Predicate<? super Object>)this.keyPredicate, (Predicate<? super Object>)this.valuePredicate);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        final SortedMap<K, V> map = this.getSortedMap().tailMap(fromKey);
        return new PredicatedSortedMap((SortedMap<Object, Object>)map, (Predicate<? super Object>)this.keyPredicate, (Predicate<? super Object>)this.valuePredicate);
    }
}
