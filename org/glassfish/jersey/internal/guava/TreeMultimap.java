package org.glassfish.jersey.internal.guava;

import java.util.Set;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.NavigableSet;
import java.util.NavigableMap;
import java.util.Collection;
import java.util.TreeSet;
import java.util.SortedSet;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Comparator;

public class TreeMultimap<K, V> extends AbstractSortedKeySortedSetMultimap<K, V>
{
    private static final long serialVersionUID = 0L;
    private transient Comparator<? super K> keyComparator;
    private transient Comparator<? super V> valueComparator;
    
    private TreeMultimap(final Comparator<? super K> keyComparator, final Comparator<? super V> valueComparator) {
        super(new TreeMap(keyComparator));
        this.keyComparator = keyComparator;
        this.valueComparator = valueComparator;
    }
    
    public static <K extends Comparable, V extends Comparable> TreeMultimap<K, V> create() {
        return new TreeMultimap<K, V>(Ordering.natural(), Ordering.natural());
    }
    
    @Override
    SortedSet<V> createCollection() {
        return new TreeSet<V>(this.valueComparator);
    }
    
    @Override
    Collection<V> createCollection(final K key) {
        if (key == null) {
            this.keyComparator().compare((Object)key, (Object)key);
        }
        return super.createCollection(key);
    }
    
    private Comparator<? super K> keyComparator() {
        return this.keyComparator;
    }
    
    @Override
    public Comparator<? super V> valueComparator() {
        return this.valueComparator;
    }
    
    @Override
    NavigableMap<K, Collection<V>> backingMap() {
        return (NavigableMap)super.backingMap();
    }
    
    @Override
    public NavigableSet<V> get(final K key) {
        return (NavigableSet)super.get(key);
    }
    
    @Override
    Collection<V> unmodifiableCollectionSubclass(final Collection<V> collection) {
        return (Collection<V>)Sets.unmodifiableNavigableSet((NavigableSet<Object>)(NavigableSet)collection);
    }
    
    @Override
    Collection<V> wrapCollection(final K key, final Collection<V> collection) {
        return (Collection<V>)new WrappedNavigableSet((K)key, (NavigableSet)collection, null);
    }
    
    @Override
    public NavigableSet<K> keySet() {
        return (NavigableSet)super.keySet();
    }
    
    @Override
    NavigableSet<K> createKeySet() {
        return (NavigableSet<K>)new NavigableKeySet(this.backingMap());
    }
    
    @Override
    public NavigableMap<K, Collection<V>> asMap() {
        return (NavigableMap)super.asMap();
    }
    
    @Override
    NavigableMap<K, Collection<V>> createAsMap() {
        return (NavigableMap<K, Collection<V>>)new NavigableAsMap(this.backingMap());
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeObject(this.keyComparator());
        stream.writeObject(this.valueComparator());
        Serialization.writeMultimap((Multimap<Object, Object>)this, stream);
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.keyComparator = Preconditions.checkNotNull(stream.readObject());
        this.valueComparator = Preconditions.checkNotNull(stream.readObject());
        this.setMap(new TreeMap<K, Collection<V>>(this.keyComparator));
        Serialization.populateMultimap((Multimap<Object, Object>)this, stream);
    }
}
