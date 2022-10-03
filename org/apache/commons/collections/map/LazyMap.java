package org.apache.commons.collections.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.Transformer;
import java.io.Serializable;
import java.util.Map;

public class LazyMap extends AbstractMapDecorator implements Map, Serializable
{
    private static final long serialVersionUID = 7990956402564206740L;
    protected final Transformer factory;
    
    public static Map decorate(final Map map, final Factory factory) {
        return new LazyMap(map, factory);
    }
    
    public static Map decorate(final Map map, final Transformer factory) {
        return new LazyMap(map, factory);
    }
    
    protected LazyMap(final Map map, final Factory factory) {
        super(map);
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        this.factory = FactoryTransformer.getInstance(factory);
    }
    
    protected LazyMap(final Map map, final Transformer factory) {
        super(map);
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
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
    
    public Object get(final Object key) {
        if (!this.map.containsKey(key)) {
            final Object value = this.factory.transform(key);
            this.map.put(key, value);
            return value;
        }
        return this.map.get(key);
    }
}
