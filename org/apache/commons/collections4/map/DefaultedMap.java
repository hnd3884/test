package org.apache.commons.collections4.map;

import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import org.apache.commons.collections4.functors.FactoryTransformer;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.functors.ConstantTransformer;
import java.util.Map;
import org.apache.commons.collections4.Transformer;
import java.io.Serializable;

public class DefaultedMap<K, V> extends AbstractMapDecorator<K, V> implements Serializable
{
    private static final long serialVersionUID = 19698628745827L;
    private final Transformer<? super K, ? extends V> value;
    
    public static <K, V> DefaultedMap<K, V> defaultedMap(final Map<K, V> map, final V defaultValue) {
        return new DefaultedMap<K, V>(map, (Transformer<? super K, ? extends V>)ConstantTransformer.constantTransformer(defaultValue));
    }
    
    public static <K, V> DefaultedMap<K, V> defaultedMap(final Map<K, V> map, final Factory<? extends V> factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory must not be null");
        }
        return new DefaultedMap<K, V>(map, FactoryTransformer.factoryTransformer(factory));
    }
    
    public static <K, V> Map<K, V> defaultedMap(final Map<K, V> map, final Transformer<? super K, ? extends V> transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer must not be null");
        }
        return new DefaultedMap<K, V>(map, transformer);
    }
    
    public DefaultedMap(final V defaultValue) {
        this(ConstantTransformer.constantTransformer(defaultValue));
    }
    
    public DefaultedMap(final Transformer<? super K, ? extends V> defaultValueTransformer) {
        this((Map)new HashMap(), defaultValueTransformer);
    }
    
    protected DefaultedMap(final Map<K, V> map, final Transformer<? super K, ? extends V> defaultValueTransformer) {
        super(map);
        if (defaultValueTransformer == null) {
            throw new NullPointerException("Transformer must not be null.");
        }
        this.value = defaultValueTransformer;
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
            return (V)this.value.transform((Object)key);
        }
        return this.map.get(key);
    }
}
