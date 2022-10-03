package org.apache.commons.collections4.map;

import java.util.Set;
import java.util.Iterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import org.apache.commons.collections4.Transformer;
import java.io.Serializable;

public class TransformedMap<K, V> extends AbstractInputCheckedMapDecorator<K, V> implements Serializable
{
    private static final long serialVersionUID = 7023152376788900464L;
    protected final Transformer<? super K, ? extends K> keyTransformer;
    protected final Transformer<? super V, ? extends V> valueTransformer;
    
    public static <K, V> TransformedMap<K, V> transformingMap(final Map<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        return new TransformedMap<K, V>(map, keyTransformer, valueTransformer);
    }
    
    public static <K, V> TransformedMap<K, V> transformedMap(final Map<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        final TransformedMap<K, V> decorated = new TransformedMap<K, V>(map, keyTransformer, valueTransformer);
        if (map.size() > 0) {
            final Map<K, V> transformed = decorated.transformMap((Map<? extends K, ? extends V>)map);
            decorated.clear();
            decorated.decorated().putAll(transformed);
        }
        return decorated;
    }
    
    protected TransformedMap(final Map<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        super(map);
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    protected K transformKey(final K object) {
        if (this.keyTransformer == null) {
            return object;
        }
        return (K)this.keyTransformer.transform(object);
    }
    
    protected V transformValue(final V object) {
        if (this.valueTransformer == null) {
            return object;
        }
        return (V)this.valueTransformer.transform(object);
    }
    
    protected Map<K, V> transformMap(final Map<? extends K, ? extends V> map) {
        if (map.isEmpty()) {
            return (Map<K, V>)map;
        }
        final Map<K, V> result = new LinkedMap<K, V>(map.size());
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            result.put((K)this.transformKey(entry.getKey()), (V)this.transformValue(entry.getValue()));
        }
        return result;
    }
    
    @Override
    protected V checkSetValue(final V value) {
        return (V)this.valueTransformer.transform(value);
    }
    
    @Override
    protected boolean isSetValueChecking() {
        return this.valueTransformer != null;
    }
    
    @Override
    public V put(K key, V value) {
        key = this.transformKey(key);
        value = this.transformValue(value);
        return this.decorated().put(key, value);
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends V> mapToCopy) {
        mapToCopy = this.transformMap(mapToCopy);
        this.decorated().putAll(mapToCopy);
    }
}
