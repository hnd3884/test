package org.apache.commons.collections.map;

import java.util.Comparator;
import java.util.Map;
import org.apache.commons.collections.Transformer;
import java.util.SortedMap;

public class TransformedSortedMap extends TransformedMap implements SortedMap
{
    private static final long serialVersionUID = -8751771676410385778L;
    
    public static SortedMap decorate(final SortedMap map, final Transformer keyTransformer, final Transformer valueTransformer) {
        return new TransformedSortedMap(map, keyTransformer, valueTransformer);
    }
    
    public static SortedMap decorateTransform(final SortedMap map, final Transformer keyTransformer, final Transformer valueTransformer) {
        final TransformedSortedMap decorated = new TransformedSortedMap(map, keyTransformer, valueTransformer);
        if (map.size() > 0) {
            final Map transformed = decorated.transformMap(map);
            decorated.clear();
            decorated.getMap().putAll(transformed);
        }
        return decorated;
    }
    
    protected TransformedSortedMap(final SortedMap map, final Transformer keyTransformer, final Transformer valueTransformer) {
        super(map, keyTransformer, valueTransformer);
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
        return new TransformedSortedMap(map, this.keyTransformer, this.valueTransformer);
    }
    
    public SortedMap headMap(final Object toKey) {
        final SortedMap map = this.getSortedMap().headMap(toKey);
        return new TransformedSortedMap(map, this.keyTransformer, this.valueTransformer);
    }
    
    public SortedMap tailMap(final Object fromKey) {
        final SortedMap map = this.getSortedMap().tailMap(fromKey);
        return new TransformedSortedMap(map, this.keyTransformer, this.valueTransformer);
    }
}
