package org.apache.commons.collections.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.apache.commons.collections.functors.FactoryTransformer;
import org.apache.commons.collections.Factory;
import org.apache.commons.collections.functors.ConstantTransformer;
import org.apache.commons.collections.Transformer;
import java.io.Serializable;
import java.util.Map;

public class DefaultedMap extends AbstractMapDecorator implements Map, Serializable
{
    private static final long serialVersionUID = 19698628745827L;
    protected final Object value;
    
    public static Map decorate(final Map map, Object defaultValue) {
        if (defaultValue instanceof Transformer) {
            defaultValue = ConstantTransformer.getInstance(defaultValue);
        }
        return new DefaultedMap(map, defaultValue);
    }
    
    public static Map decorate(final Map map, final Factory factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        return new DefaultedMap(map, FactoryTransformer.getInstance(factory));
    }
    
    public static Map decorate(final Map map, final Transformer factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        return new DefaultedMap(map, factory);
    }
    
    public DefaultedMap(Object defaultValue) {
        super(new HashMap());
        if (defaultValue instanceof Transformer) {
            defaultValue = ConstantTransformer.getInstance(defaultValue);
        }
        this.value = defaultValue;
    }
    
    protected DefaultedMap(final Map map, final Object value) {
        super(map);
        this.value = value;
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
        if (this.map.containsKey(key)) {
            return this.map.get(key);
        }
        if (this.value instanceof Transformer) {
            return ((Transformer)this.value).transform(key);
        }
        return this.value;
    }
}
