package org.apache.commons.collections4.multimap;

import java.util.Set;
import java.util.Collection;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import org.apache.commons.collections4.MultiValuedMap;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class HashSetValuedHashMap<K, V> extends AbstractSetValuedMap<K, V> implements Serializable
{
    private static final long serialVersionUID = 20151118L;
    private static final int DEFAULT_INITIAL_MAP_CAPACITY = 16;
    private static final int DEFAULT_INITIAL_SET_CAPACITY = 3;
    private final int initialSetCapacity;
    
    public HashSetValuedHashMap() {
        this(16, 3);
    }
    
    public HashSetValuedHashMap(final int initialSetCapacity) {
        this(16, initialSetCapacity);
    }
    
    public HashSetValuedHashMap(final int initialMapCapacity, final int initialSetCapacity) {
        super(new HashMap(initialMapCapacity));
        this.initialSetCapacity = initialSetCapacity;
    }
    
    public HashSetValuedHashMap(final MultiValuedMap<? extends K, ? extends V> map) {
        this(map.size(), 3);
        super.putAll(map);
    }
    
    public HashSetValuedHashMap(final Map<? extends K, ? extends V> map) {
        this(map.size(), 3);
        super.putAll(map);
    }
    
    @Override
    protected HashSet<V> createCollection() {
        return new HashSet<V>(this.initialSetCapacity);
    }
    
    private void writeObject(final ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        this.doWriteObject(oos);
    }
    
    private void readObject(final ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.setMap((Map<K, ? extends Collection<Object>>)new HashMap<K, Collection<Object>>());
        this.doReadObject(ois);
    }
}
