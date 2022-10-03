package org.apache.commons.collections4.multimap;

import java.util.List;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.collections4.MultiValuedMap;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

public class ArrayListValuedHashMap<K, V> extends AbstractListValuedMap<K, V> implements Serializable
{
    private static final long serialVersionUID = 20151118L;
    private static final int DEFAULT_INITIAL_MAP_CAPACITY = 16;
    private static final int DEFAULT_INITIAL_LIST_CAPACITY = 3;
    private final int initialListCapacity;
    
    public ArrayListValuedHashMap() {
        this(16, 3);
    }
    
    public ArrayListValuedHashMap(final int initialListCapacity) {
        this(16, initialListCapacity);
    }
    
    public ArrayListValuedHashMap(final int initialMapCapacity, final int initialListCapacity) {
        super(new HashMap(initialMapCapacity));
        this.initialListCapacity = initialListCapacity;
    }
    
    public ArrayListValuedHashMap(final MultiValuedMap<? extends K, ? extends V> map) {
        this(map.size(), 3);
        super.putAll(map);
    }
    
    public ArrayListValuedHashMap(final Map<? extends K, ? extends V> map) {
        this(map.size(), 3);
        super.putAll(map);
    }
    
    @Override
    protected ArrayList<V> createCollection() {
        return new ArrayList<V>(this.initialListCapacity);
    }
    
    public void trimToSize() {
        for (final Collection<V> coll : this.getMap().values()) {
            final ArrayList<V> list = (ArrayList)coll;
            list.trimToSize();
        }
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
