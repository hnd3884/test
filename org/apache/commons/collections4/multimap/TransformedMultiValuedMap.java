package org.apache.commons.collections4.multimap;

import java.util.Map;
import java.util.Iterator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.FluentIterable;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.Transformer;

public class TransformedMultiValuedMap<K, V> extends AbstractMultiValuedMapDecorator<K, V>
{
    private static final long serialVersionUID = 20150612L;
    private final Transformer<? super K, ? extends K> keyTransformer;
    private final Transformer<? super V, ? extends V> valueTransformer;
    
    public static <K, V> TransformedMultiValuedMap<K, V> transformingMap(final MultiValuedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        return new TransformedMultiValuedMap<K, V>(map, keyTransformer, valueTransformer);
    }
    
    public static <K, V> TransformedMultiValuedMap<K, V> transformedMap(final MultiValuedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        final TransformedMultiValuedMap<K, V> decorated = new TransformedMultiValuedMap<K, V>(map, keyTransformer, valueTransformer);
        if (!map.isEmpty()) {
            final MultiValuedMap<K, V> mapCopy = new ArrayListValuedHashMap<K, V>((MultiValuedMap<? extends K, ? extends V>)map);
            decorated.clear();
            decorated.putAll((MultiValuedMap<? extends K, ? extends V>)mapCopy);
        }
        return decorated;
    }
    
    protected TransformedMultiValuedMap(final MultiValuedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        super(map);
        this.keyTransformer = keyTransformer;
        this.valueTransformer = valueTransformer;
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
    
    @Override
    public boolean put(final K key, final V value) {
        return this.decorated().put(this.transformKey(key), this.transformValue(value));
    }
    
    @Override
    public boolean putAll(final K key, final Iterable<? extends V> values) {
        if (values == null) {
            throw new NullPointerException("Values must not be null.");
        }
        final Iterable<V> transformedValues = (Iterable<V>)FluentIterable.of(values).transform((Transformer<? super V, ?>)this.valueTransformer);
        final Iterator<? extends V> it = (Iterator<? extends V>)transformedValues.iterator();
        return it.hasNext() && CollectionUtils.addAll(this.decorated().get(this.transformKey(key)), it);
    }
    
    @Override
    public boolean putAll(final Map<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (final Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
            changed |= this.put(entry.getKey(), entry.getValue());
        }
        return changed;
    }
    
    @Override
    public boolean putAll(final MultiValuedMap<? extends K, ? extends V> map) {
        if (map == null) {
            throw new NullPointerException("Map must not be null.");
        }
        boolean changed = false;
        for (final Map.Entry<? extends K, ? extends V> entry : map.entries()) {
            changed |= this.put(entry.getKey(), entry.getValue());
        }
        return changed;
    }
}
