package org.apache.commons.collections4.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections4.functors.FactoryTransformer;
import org.apache.commons.collections4.Factory;
import java.util.Map;
import org.apache.commons.collections4.Transformer;
import java.io.Serializable;

public class LazyMap<K, V> extends AbstractMapDecorator<K, V> implements Serializable
{
    private static final long serialVersionUID = 7990956402564206740L;
    protected final Transformer<? super K, ? extends V> factory;
    
    public static <K, V> LazyMap<K, V> lazyMap(final Map<K, V> map, final Factory<? extends V> factory) {
        return new LazyMap<K, V>(map, factory);
    }
    
    public static <V, K> LazyMap<K, V> lazyMap(final Map<K, V> map, final Transformer<? super K, ? extends V> factory) {
        return new LazyMap<K, V>(map, factory);
    }
    
    protected LazyMap(final Map<K, V> map, final Factory<? extends V> factory) {
        super(map);
        if (factory == null) {
            throw new NullPointerException("Factory must not be null");
        }
        this.factory = FactoryTransformer.factoryTransformer(factory);
    }
    
    protected LazyMap(final Map<K, V> map, final Transformer<? super K, ? extends V> factory) {
        super(map);
        if (factory == null) {
            throw new NullPointerException("Factory must not be null");
        }
        this.factory = factory;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    @Override
    public V get(final Object key) {
        if (!this.map.containsKey(key)) {
            final K castKey = (K)key;
            final V value = (V)this.factory.transform(castKey);
            this.map.put(castKey, value);
            return value;
        }
        return this.map.get(key);
    }
}
