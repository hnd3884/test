package org.glassfish.jersey.internal.guava;

import java.util.Objects;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.function.Predicate;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class Maps
{
    private static final Joiner.MapJoiner STANDARD_JOINER;
    
    private Maps() {
    }
    
    private static <K> Function<Map.Entry<K, ?>, K> keyFunction() {
        return (Function<Map.Entry<K, ?>, K>)EntryFunction.KEY;
    }
    
    private static <V> Function<Map.Entry<?, V>, V> valueFunction() {
        return (Function<Map.Entry<?, V>, V>)EntryFunction.VALUE;
    }
    
    private static <K, V> Iterator<K> keyIterator(final Iterator<Map.Entry<K, V>> entryIterator) {
        return Iterators.transform(entryIterator, (Function<? super Map.Entry<K, V>, ? extends K>)keyFunction());
    }
    
    static <K, V> Iterator<V> valueIterator(final Iterator<Map.Entry<K, V>> entryIterator) {
        return Iterators.transform(entryIterator, (Function<? super Map.Entry<K, V>, ? extends V>)valueFunction());
    }
    
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(final int expectedSize) {
        return new HashMap<K, V>(capacity(expectedSize));
    }
    
    static int capacity(final int expectedSize) {
        if (expectedSize < 3) {
            CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
            return expectedSize + 1;
        }
        if (expectedSize < 1073741824) {
            return expectedSize + expectedSize / 3;
        }
        return Integer.MAX_VALUE;
    }
    
    static <K, V> Iterator<Map.Entry<K, V>> asMapEntryIterator(final Set<K> set, final Function<? super K, V> function) {
        return new TransformedIterator<K, Map.Entry<K, V>>(set.iterator()) {
            @Override
            Map.Entry<K, V> transform(final K key) {
                return Maps.immutableEntry(key, function.apply(key));
            }
        };
    }
    
    private static <E> Set<E> removeOnlySet(final Set<E> set) {
        return new ForwardingSet<E>() {
            @Override
            protected Set<E> delegate() {
                return set;
            }
            
            @Override
            public boolean add(final E element) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean addAll(final Collection<? extends E> es) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private static <E> SortedSet<E> removeOnlySortedSet(final SortedSet<E> set) {
        return new ForwardingSortedSet<E>() {
            @Override
            protected SortedSet<E> delegate() {
                return set;
            }
            
            @Override
            public boolean add(final E element) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public boolean addAll(final Collection<? extends E> es) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public SortedSet<E> headSet(final E toElement) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.headSet(toElement));
            }
            
            @Override
            public SortedSet<E> subSet(final E fromElement, final E toElement) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.subSet(fromElement, toElement));
            }
            
            @Override
            public SortedSet<E> tailSet(final E fromElement) {
                return (SortedSet<E>)removeOnlySortedSet((SortedSet<Object>)super.tailSet(fromElement));
            }
        };
    }
    
    public static <K, V> Map.Entry<K, V> immutableEntry(final K key, final V value) {
        return new ImmutableEntry<K, V>(key, value);
    }
    
    static <K> Predicate<Map.Entry<K, ?>> keyPredicateOnEntries(final Predicate<? super K> keyPredicate) {
        return Predicates.compose(keyPredicate, (Function<Map.Entry<K, ?>, ? extends K>)keyFunction());
    }
    
    static <V> Predicate<Map.Entry<?, V>> valuePredicateOnEntries(final Predicate<? super V> valuePredicate) {
        return Predicates.compose(valuePredicate, (Function<Map.Entry<?, V>, ? extends V>)valueFunction());
    }
    
    static <V> V safeGet(final Map<?, V> map, final Object key) {
        Preconditions.checkNotNull(map);
        try {
            return map.get(key);
        }
        catch (final ClassCastException e) {
            return null;
        }
        catch (final NullPointerException e2) {
            return null;
        }
    }
    
    static boolean safeContainsKey(final Map<?, ?> map, final Object key) {
        Preconditions.checkNotNull(map);
        try {
            return map.containsKey(key);
        }
        catch (final ClassCastException e) {
            return false;
        }
        catch (final NullPointerException e2) {
            return false;
        }
    }
    
    static <V> V safeRemove(final Map<?, V> map, final Object key) {
        Preconditions.checkNotNull(map);
        try {
            return map.remove(key);
        }
        catch (final ClassCastException e) {
            return null;
        }
        catch (final NullPointerException e2) {
            return null;
        }
    }
    
    static {
        STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator();
    }
    
    private enum EntryFunction implements Function<Map.Entry<?, ?>, Object>
    {
        KEY {
            @Override
            public Object apply(final Map.Entry<?, ?> entry) {
                return entry.getKey();
            }
        }, 
        VALUE {
            @Override
            public Object apply(final Map.Entry<?, ?> entry) {
                return entry.getValue();
            }
        };
    }
    
    private static class AsMapView<K, V> extends ImprovedAbstractMap<K, V>
    {
        final Function<? super K, V> function;
        private final Set<K> set;
        
        AsMapView(final Set<K> set, final Function<? super K, V> function) {
            this.set = Preconditions.checkNotNull(set);
            this.function = Preconditions.checkNotNull(function);
        }
        
        Set<K> backingSet() {
            return this.set;
        }
        
        public Set<K> createKeySet() {
            return (Set<K>)removeOnlySet((Set<Object>)this.backingSet());
        }
        
        @Override
        Collection<V> createValues() {
            return Collections2.transform(this.set, this.function);
        }
        
        @Override
        public int size() {
            return this.backingSet().size();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.backingSet().contains(key);
        }
        
        @Override
        public V get(final Object key) {
            if (Collections2.safeContains(this.backingSet(), key)) {
                final K k = (K)key;
                return this.function.apply(k);
            }
            return null;
        }
        
        @Override
        public V remove(final Object key) {
            if (this.backingSet().remove(key)) {
                final K k = (K)key;
                return this.function.apply(k);
            }
            return null;
        }
        
        @Override
        public void clear() {
            this.backingSet().clear();
        }
        
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return (Set<Map.Entry<K, V>>)new EntrySet<K, V>() {
                @Override
                Map<K, V> map() {
                    return AsMapView.this;
                }
                
                @Override
                public Iterator<Map.Entry<K, V>> iterator() {
                    return Maps.asMapEntryIterator(AsMapView.this.backingSet(), AsMapView.this.function);
                }
            };
        }
    }
    
    abstract static class ImprovedAbstractMap<K, V> extends AbstractMap<K, V>
    {
        private transient Set<Map.Entry<K, V>> entrySet;
        private transient Set<K> keySet;
        private transient Collection<V> values;
        
        abstract Set<Map.Entry<K, V>> createEntrySet();
        
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            final Set<Map.Entry<K, V>> result = this.entrySet;
            return (result == null) ? (this.entrySet = this.createEntrySet()) : result;
        }
        
        @Override
        public Set<K> keySet() {
            final Set<K> result = this.keySet;
            return (result == null) ? (this.keySet = this.createKeySet()) : result;
        }
        
        Set<K> createKeySet() {
            return (Set<K>)new KeySet((Map<Object, Object>)this);
        }
        
        @Override
        public Collection<V> values() {
            final Collection<V> result = this.values;
            return (result == null) ? (this.values = this.createValues()) : result;
        }
        
        Collection<V> createValues() {
            return (Collection<V>)new Values((Map<Object, Object>)this);
        }
    }
    
    static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K>
    {
        final Map<K, V> map;
        
        KeySet(final Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }
        
        Map<K, V> map() {
            return this.map;
        }
        
        @Override
        public Iterator<K> iterator() {
            return (Iterator<K>)keyIterator((Iterator<Map.Entry<Object, Object>>)this.map().entrySet().iterator());
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map().containsKey(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            if (this.contains(o)) {
                this.map().remove(o);
                return true;
            }
            return false;
        }
        
        @Override
        public void clear() {
            this.map().clear();
        }
    }
    
    static class Values<K, V> extends AbstractCollection<V>
    {
        final Map<K, V> map;
        
        Values(final Map<K, V> map) {
            this.map = Preconditions.checkNotNull(map);
        }
        
        final Map<K, V> map() {
            return this.map;
        }
        
        @Override
        public Iterator<V> iterator() {
            return Maps.valueIterator(this.map().entrySet().iterator());
        }
        
        @Override
        public boolean remove(final Object o) {
            try {
                return super.remove(o);
            }
            catch (final UnsupportedOperationException e) {
                for (final Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (Objects.equals(o, entry.getValue())) {
                        this.map().remove(entry.getKey());
                        return true;
                    }
                }
                return false;
            }
        }
        
        @Override
        public boolean removeAll(final Collection<?> c) {
            try {
                return super.removeAll(Preconditions.checkNotNull(c));
            }
            catch (final UnsupportedOperationException e) {
                final Set<K> toRemove = (Set<K>)Sets.newHashSet();
                for (final Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (c.contains(entry.getValue())) {
                        toRemove.add(entry.getKey());
                    }
                }
                return this.map().keySet().removeAll(toRemove);
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            try {
                return super.retainAll(Preconditions.checkNotNull(c));
            }
            catch (final UnsupportedOperationException e) {
                final Set<K> toRetain = (Set<K>)Sets.newHashSet();
                for (final Map.Entry<K, V> entry : this.map().entrySet()) {
                    if (c.contains(entry.getValue())) {
                        toRetain.add(entry.getKey());
                    }
                }
                return this.map().keySet().retainAll(toRetain);
            }
        }
        
        @Override
        public int size() {
            return this.map().size();
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        @Override
        public boolean contains(final Object o) {
            return this.map().containsValue(o);
        }
        
        @Override
        public void clear() {
            this.map().clear();
        }
    }
    
    abstract static class EntrySet<K, V> extends Sets.ImprovedAbstractSet<Map.Entry<K, V>>
    {
        abstract Map<K, V> map();
        
        @Override
        public int size() {
            return this.map().size();
        }
        
        @Override
        public void clear() {
            this.map().clear();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (o instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                final Object key = entry.getKey();
                final V value = Maps.safeGet(this.map(), key);
                return Objects.equals(value, entry.getValue()) && (value != null || this.map().containsKey(key));
            }
            return false;
        }
        
        @Override
        public boolean isEmpty() {
            return this.map().isEmpty();
        }
        
        @Override
        public boolean remove(final Object o) {
            if (this.contains(o)) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                return this.map().keySet().remove(entry.getKey());
            }
            return false;
        }
        
        @Override
        public boolean removeAll(final Collection<?> c) {
            try {
                return super.removeAll(Preconditions.checkNotNull(c));
            }
            catch (final UnsupportedOperationException e) {
                return Sets.removeAllImpl(this, c.iterator());
            }
        }
        
        @Override
        public boolean retainAll(final Collection<?> c) {
            try {
                return super.retainAll(Preconditions.checkNotNull(c));
            }
            catch (final UnsupportedOperationException e) {
                final Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());
                for (final Object o : c) {
                    if (this.contains(o)) {
                        final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)o;
                        keys.add(entry.getKey());
                    }
                }
                return this.map().keySet().retainAll(keys);
            }
        }
    }
}
