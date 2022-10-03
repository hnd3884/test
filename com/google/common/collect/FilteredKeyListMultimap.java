package com.google.common.collect;

import java.util.Collection;
import javax.annotation.CheckForNull;
import java.util.List;
import com.google.common.base.Predicate;
import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class FilteredKeyListMultimap<K, V> extends FilteredKeyMultimap<K, V> implements ListMultimap<K, V>
{
    FilteredKeyListMultimap(final ListMultimap<K, V> unfiltered, final Predicate<? super K> keyPredicate) {
        super(unfiltered, keyPredicate);
    }
    
    @Override
    public ListMultimap<K, V> unfiltered() {
        return (ListMultimap)super.unfiltered();
    }
    
    @Override
    public List<V> get(@ParametricNullness final K key) {
        return (List)super.get(key);
    }
    
    @Override
    public List<V> removeAll(@CheckForNull final Object key) {
        return (List)super.removeAll(key);
    }
    
    @Override
    public List<V> replaceValues(@ParametricNullness final K key, final Iterable<? extends V> values) {
        return (List)super.replaceValues(key, values);
    }
}
