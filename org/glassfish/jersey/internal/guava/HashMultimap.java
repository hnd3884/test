package org.glassfish.jersey.internal.guava;

import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public final class HashMultimap<K, V> extends AbstractSetMultimap<K, V>
{
    private static final int DEFAULT_VALUES_PER_KEY = 2;
    private static final long serialVersionUID = 0L;
    private transient int expectedValuesPerKey;
    
    private HashMultimap() {
        super(new HashMap());
        this.expectedValuesPerKey = 2;
    }
    
    public static <K, V> HashMultimap<K, V> create() {
        return new HashMultimap<K, V>();
    }
    
    @Override
    Set<V> createCollection() {
        return (Set<V>)Sets.newHashSetWithExpectedSize(this.expectedValuesPerKey);
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.expectedValuesPerKey);
        Serialization.writeMultimap((Multimap<Object, Object>)this, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.expectedValuesPerKey = stream.readInt();
        final int distinctKeys = Serialization.readCount(stream);
        final Map<K, Collection<V>> map = (Map<K, Collection<V>>)Maps.newHashMapWithExpectedSize(distinctKeys);
        this.setMap(map);
        Serialization.populateMultimap((Multimap<Object, Object>)this, stream, distinctKeys);
    }
}
