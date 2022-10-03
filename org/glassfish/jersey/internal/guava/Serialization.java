package org.glassfish.jersey.internal.guava;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

final class Serialization
{
    private Serialization() {
    }
    
    static int readCount(final ObjectInputStream stream) throws IOException {
        return stream.readInt();
    }
    
    static <K, V> void writeMultimap(final Multimap<K, V> multimap, final ObjectOutputStream stream) throws IOException {
        stream.writeInt(multimap.asMap().size());
        for (final Map.Entry<K, Collection<V>> entry : multimap.asMap().entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeInt(entry.getValue().size());
            for (final V value : entry.getValue()) {
                stream.writeObject(value);
            }
        }
    }
    
    static <K, V> void populateMultimap(final Multimap<K, V> multimap, final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        final int distinctKeys = stream.readInt();
        populateMultimap(multimap, stream, distinctKeys);
    }
    
    static <K, V> void populateMultimap(final Multimap<K, V> multimap, final ObjectInputStream stream, final int distinctKeys) throws IOException, ClassNotFoundException {
        for (int i = 0; i < distinctKeys; ++i) {
            final K key = (K)stream.readObject();
            final Collection<V> values = multimap.get(key);
            for (int valueCount = stream.readInt(), j = 0; j < valueCount; ++j) {
                final V value = (V)stream.readObject();
                values.add(value);
            }
        }
    }
}
