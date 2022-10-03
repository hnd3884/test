package org.apache.commons.collections4.splitmap;

import java.util.Iterator;
import org.apache.commons.collections4.map.LinkedMap;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import org.apache.commons.collections4.Transformer;
import java.io.Serializable;
import org.apache.commons.collections4.Put;

public class TransformedSplitMap<J, K, U, V> extends AbstractIterableGetMapDecorator<K, V> implements Put<J, U>, Serializable
{
    private static final long serialVersionUID = 5966875321133456994L;
    private final Transformer<? super J, ? extends K> keyTransformer;
    private final Transformer<? super U, ? extends V> valueTransformer;
    
    public static <J, K, U, V> TransformedSplitMap<J, K, U, V> transformingMap(final Map<K, V> map, final Transformer<? super J, ? extends K> keyTransformer, final Transformer<? super U, ? extends V> valueTransformer) {
        return new TransformedSplitMap<J, K, U, V>(map, keyTransformer, valueTransformer);
    }
    
    protected TransformedSplitMap(final Map<K, V> map, final Transformer<? super J, ? extends K> keyTransformer, final Transformer<? super U, ? extends V> valueTransformer) {
        super(map);
        if (keyTransformer == null) {
            throw new NullPointerException("KeyTransformer must not be null.");
        }
        this.keyTransformer = keyTransformer;
        if (valueTransformer == null) {
            throw new NullPointerException("ValueTransformer must not be null.");
        }
        this.valueTransformer = valueTransformer;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.decorated());
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    protected K transformKey(final J object) {
        return (K)this.keyTransformer.transform(object);
    }
    
    protected V transformValue(final U object) {
        return (V)this.valueTransformer.transform(object);
    }
    
    protected Map<K, V> transformMap(final Map<? extends J, ? extends U> map) {
        if (map.isEmpty()) {
            return (Map<K, V>)map;
        }
        final Map<K, V> result = new LinkedMap<K, V>(map.size());
        for (final Map.Entry<? extends J, ? extends U> entry : map.entrySet()) {
            result.put(this.transformKey(entry.getKey()), this.transformValue(entry.getValue()));
        }
        return result;
    }
    
    protected V checkSetValue(final U value) {
        return (V)this.valueTransformer.transform(value);
    }
    
    @Override
    public V put(final J key, final U value) {
        return this.decorated().put(this.transformKey(key), this.transformValue(value));
    }
    
    @Override
    public void putAll(final Map<? extends J, ? extends U> mapToCopy) {
        this.decorated().putAll(this.transformMap(mapToCopy));
    }
    
    @Override
    public void clear() {
        this.decorated().clear();
    }
}
