package org.apache.commons.collections4.collection;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.collections4.map.MultiValueMap;
import java.util.HashMap;
import java.util.Collection;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.Transformer;

public class IndexedCollection<K, C> extends AbstractCollectionDecorator<C>
{
    private static final long serialVersionUID = -5512610452568370038L;
    private final Transformer<C, K> keyTransformer;
    private final MultiMap<K, C> index;
    private final boolean uniqueIndex;
    
    public static <K, C> IndexedCollection<K, C> uniqueIndexedCollection(final Collection<C> coll, final Transformer<C, K> keyTransformer) {
        return new IndexedCollection<K, C>(coll, keyTransformer, (MultiMap<K, C>)MultiValueMap.multiValueMap((Map<Object, ? super Collection<Object>>)new HashMap<Object, Object>()), true);
    }
    
    public static <K, C> IndexedCollection<K, C> nonUniqueIndexedCollection(final Collection<C> coll, final Transformer<C, K> keyTransformer) {
        return new IndexedCollection<K, C>(coll, keyTransformer, (MultiMap<K, C>)MultiValueMap.multiValueMap((Map<Object, ? super Collection<Object>>)new HashMap<Object, Object>()), false);
    }
    
    public IndexedCollection(final Collection<C> coll, final Transformer<C, K> keyTransformer, final MultiMap<K, C> map, final boolean uniqueIndex) {
        super(coll);
        this.keyTransformer = keyTransformer;
        this.index = map;
        this.uniqueIndex = uniqueIndex;
        this.reindex();
    }
    
    @Override
    public boolean add(final C object) {
        final boolean added = super.add(object);
        if (added) {
            this.addToIndex(object);
        }
        return added;
    }
    
    @Override
    public boolean addAll(final Collection<? extends C> coll) {
        boolean changed = false;
        for (final C c : coll) {
            changed |= this.add(c);
        }
        return changed;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.index.clear();
    }
    
    @Override
    public boolean contains(final Object object) {
        return this.index.containsKey(this.keyTransformer.transform((C)object));
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        for (final Object o : coll) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }
    
    public C get(final K key) {
        final Collection<C> coll = (Collection<C>)this.index.get(key);
        return (coll == null) ? null : coll.iterator().next();
    }
    
    public Collection<C> values(final K key) {
        return (Collection)this.index.get(key);
    }
    
    public void reindex() {
        this.index.clear();
        for (final C c : this.decorated()) {
            this.addToIndex(c);
        }
    }
    
    @Override
    public boolean remove(final Object object) {
        final boolean removed = super.remove(object);
        if (removed) {
            this.removeFromIndex(object);
        }
        return removed;
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        boolean changed = false;
        for (final Object o : coll) {
            changed |= this.remove(o);
        }
        return changed;
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        final boolean changed = super.retainAll(coll);
        if (changed) {
            this.reindex();
        }
        return changed;
    }
    
    private void addToIndex(final C object) {
        final K key = this.keyTransformer.transform(object);
        if (this.uniqueIndex && this.index.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate key in uniquely indexed collection.");
        }
        this.index.put(key, object);
    }
    
    private void removeFromIndex(final C object) {
        this.index.remove(this.keyTransformer.transform(object));
    }
}
