package org.apache.commons.collections.map;

import java.util.Comparator;
import java.util.Map;
import org.apache.commons.collections.Predicate;
import java.util.SortedMap;

public class PredicatedSortedMap extends PredicatedMap implements SortedMap
{
    private static final long serialVersionUID = 3359846175935304332L;
    
    public static SortedMap decorate(final SortedMap map, final Predicate keyPredicate, final Predicate valuePredicate) {
        return new PredicatedSortedMap(map, keyPredicate, valuePredicate);
    }
    
    protected PredicatedSortedMap(final SortedMap map, final Predicate keyPredicate, final Predicate valuePredicate) {
        super(map, keyPredicate, valuePredicate);
    }
    
    protected SortedMap getSortedMap() {
        return (SortedMap)this.map;
    }
    
    public Object firstKey() {
        return this.getSortedMap().firstKey();
    }
    
    public Object lastKey() {
        return this.getSortedMap().lastKey();
    }
    
    public Comparator comparator() {
        return this.getSortedMap().comparator();
    }
    
    public SortedMap subMap(final Object fromKey, final Object toKey) {
        final SortedMap map = this.getSortedMap().subMap(fromKey, toKey);
        return new PredicatedSortedMap(map, this.keyPredicate, this.valuePredicate);
    }
    
    public SortedMap headMap(final Object toKey) {
        final SortedMap map = this.getSortedMap().headMap(toKey);
        return new PredicatedSortedMap(map, this.keyPredicate, this.valuePredicate);
    }
    
    public SortedMap tailMap(final Object fromKey) {
        final SortedMap map = this.getSortedMap().tailMap(fromKey);
        return new PredicatedSortedMap(map, this.keyPredicate, this.valuePredicate);
    }
}
