package org.apache.commons.collections.bidimap;

import org.apache.commons.collections.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections.set.UnmodifiableSet;
import org.apache.commons.collections.map.UnmodifiableEntrySet;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections.OrderedBidiMap;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableOrderedBidiMap extends AbstractOrderedBidiMapDecorator implements Unmodifiable
{
    private UnmodifiableOrderedBidiMap inverse;
    
    public static OrderedBidiMap decorate(final OrderedBidiMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableOrderedBidiMap(map);
    }
    
    private UnmodifiableOrderedBidiMap(final OrderedBidiMap map) {
        super(map);
    }
    
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    public Object put(final Object key, final Object value) {
        throw new UnsupportedOperationException();
    }
    
    public void putAll(final Map mapToCopy) {
        throw new UnsupportedOperationException();
    }
    
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    public Set entrySet() {
        final Set set = super.entrySet();
        return UnmodifiableEntrySet.decorate(set);
    }
    
    public Set keySet() {
        final Set set = super.keySet();
        return UnmodifiableSet.decorate(set);
    }
    
    public Collection values() {
        final Collection coll = super.values();
        return UnmodifiableCollection.decorate(coll);
    }
    
    public Object removeValue(final Object value) {
        throw new UnsupportedOperationException();
    }
    
    public MapIterator mapIterator() {
        return this.orderedMapIterator();
    }
    
    public BidiMap inverseBidiMap() {
        return this.inverseOrderedBidiMap();
    }
    
    public OrderedMapIterator orderedMapIterator() {
        final OrderedMapIterator it = this.getOrderedBidiMap().orderedMapIterator();
        return UnmodifiableOrderedMapIterator.decorate(it);
    }
    
    public OrderedBidiMap inverseOrderedBidiMap() {
        if (this.inverse == null) {
            this.inverse = new UnmodifiableOrderedBidiMap(this.getOrderedBidiMap().inverseOrderedBidiMap());
            this.inverse.inverse = this;
        }
        return this.inverse;
    }
}
