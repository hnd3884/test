package org.apache.commons.collections4;

import org.apache.commons.collections4.multimap.TransformedMultiValuedMap;
import org.apache.commons.collections4.multimap.UnmodifiableMultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.collections4.bag.HashBag;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

public class MultiMapUtils
{
    public static final MultiValuedMap EMPTY_MULTI_VALUED_MAP;
    
    private MultiMapUtils() {
    }
    
    public static <K, V> MultiValuedMap<K, V> emptyMultiValuedMap() {
        return MultiMapUtils.EMPTY_MULTI_VALUED_MAP;
    }
    
    public static <K, V> MultiValuedMap<K, V> emptyIfNull(final MultiValuedMap<K, V> map) {
        return (map == null) ? MultiMapUtils.EMPTY_MULTI_VALUED_MAP : map;
    }
    
    public static boolean isEmpty(final MultiValuedMap<?, ?> map) {
        return map == null || map.isEmpty();
    }
    
    public static <K, V> Collection<V> getCollection(final MultiValuedMap<K, V> map, final K key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }
    
    public static <K, V> List<V> getValuesAsList(final MultiValuedMap<K, V> map, final K key) {
        if (map == null) {
            return null;
        }
        final Collection<V> col = map.get(key);
        if (col instanceof List) {
            return (List)col;
        }
        return new ArrayList<V>((Collection<? extends V>)col);
    }
    
    public static <K, V> Set<V> getValuesAsSet(final MultiValuedMap<K, V> map, final K key) {
        if (map == null) {
            return null;
        }
        final Collection<V> col = map.get(key);
        if (col instanceof Set) {
            return (Set)col;
        }
        return new HashSet<V>((Collection<? extends V>)col);
    }
    
    public static <K, V> Bag<V> getValuesAsBag(final MultiValuedMap<K, V> map, final K key) {
        if (map == null) {
            return null;
        }
        final Collection<V> col = map.get(key);
        if (col instanceof Bag) {
            return (Bag)col;
        }
        return new HashBag<V>((Collection<? extends V>)col);
    }
    
    public static <K, V> ListValuedMap<K, V> newListValuedHashMap() {
        return new ArrayListValuedHashMap<K, V>();
    }
    
    public static <K, V> SetValuedMap<K, V> newSetValuedHashMap() {
        return new HashSetValuedHashMap<K, V>();
    }
    
    public static <K, V> MultiValuedMap<K, V> unmodifiableMultiValuedMap(final MultiValuedMap<? extends K, ? extends V> map) {
        return (MultiValuedMap<K, V>)UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap((MultiValuedMap<?, ?>)map);
    }
    
    public static <K, V> MultiValuedMap<K, V> transformedMultiValuedMap(final MultiValuedMap<K, V> map, final Transformer<? super K, ? extends K> keyTransformer, final Transformer<? super V, ? extends V> valueTransformer) {
        return TransformedMultiValuedMap.transformingMap(map, keyTransformer, valueTransformer);
    }
    
    static {
        EMPTY_MULTI_VALUED_MAP = UnmodifiableMultiValuedMap.unmodifiableMultiValuedMap((MultiValuedMap<?, ?>)new ArrayListValuedHashMap<Object, Object>(0, 0));
    }
}
