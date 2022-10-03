package org.apache.commons.collections.map;

import org.apache.commons.collections.collection.UnmodifiableCollection;
import java.util.Collection;
import org.apache.commons.collections.set.UnmodifiableSet;
import java.util.Set;
import org.apache.commons.collections.iterators.UnmodifiableOrderedMapIterator;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.iterators.UnmodifiableMapIterator;
import org.apache.commons.collections.MapIterator;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections.OrderedMap;
import java.io.Serializable;
import org.apache.commons.collections.Unmodifiable;

public final class UnmodifiableOrderedMap extends AbstractOrderedMapDecorator implements Unmodifiable, Serializable
{
    private static final long serialVersionUID = 8136428161720526266L;
    
    public static OrderedMap decorate(final OrderedMap map) {
        if (map instanceof Unmodifiable) {
            return map;
        }
        return new UnmodifiableOrderedMap(map);
    }
    
    private UnmodifiableOrderedMap(final OrderedMap map) {
        super(map);
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    public MapIterator mapIterator() {
        final MapIterator it = this.getOrderedMap().mapIterator();
        return UnmodifiableMapIterator.decorate(it);
    }
    
    public OrderedMapIterator orderedMapIterator() {
        final OrderedMapIterator it = this.getOrderedMap().orderedMapIterator();
        return UnmodifiableOrderedMapIterator.decorate(it);
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
}
