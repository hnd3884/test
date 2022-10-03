package org.apache.commons.collections.map;

import java.util.Comparator;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.Factory;
import java.util.SortedMap;

public class LazySortedMap extends LazyMap implements SortedMap
{
    private static final long serialVersionUID = 2715322183617658933L;
    
    public static SortedMap decorate(final SortedMap map, final Factory factory) {
        return new LazySortedMap(map, factory);
    }
    
    public static SortedMap decorate(final SortedMap map, final Transformer factory) {
        return new LazySortedMap(map, factory);
    }
    
    protected LazySortedMap(final SortedMap map, final Factory factory) {
        super(map, factory);
    }
    
    protected LazySortedMap(final SortedMap map, final Transformer factory) {
        super(map, factory);
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
        return new LazySortedMap(map, this.factory);
    }
    
    public SortedMap headMap(final Object toKey) {
        final SortedMap map = this.getSortedMap().headMap(toKey);
        return new LazySortedMap(map, this.factory);
    }
    
    public SortedMap tailMap(final Object fromKey) {
        final SortedMap map = this.getSortedMap().tailMap(fromKey);
        return new LazySortedMap(map, this.factory);
    }
}
