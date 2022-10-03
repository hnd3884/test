package java.util.concurrent;

import java.util.Spliterator;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Collection;
import java.util.Comparator;
import sun.misc.Unsafe;
import java.io.Serializable;
import java.util.NavigableSet;
import java.util.AbstractSet;

public class ConcurrentSkipListSet<E> extends AbstractSet<E> implements NavigableSet<E>, Cloneable, Serializable
{
    private static final long serialVersionUID = -2479143111061671589L;
    private final ConcurrentNavigableMap<E, Object> m;
    private static final Unsafe UNSAFE;
    private static final long mapOffset;
    
    public ConcurrentSkipListSet() {
        this.m = new ConcurrentSkipListMap<E, Object>();
    }
    
    public ConcurrentSkipListSet(final Comparator<? super E> comparator) {
        this.m = new ConcurrentSkipListMap<E, Object>(comparator);
    }
    
    public ConcurrentSkipListSet(final Collection<? extends E> collection) {
        this.m = new ConcurrentSkipListMap<E, Object>();
        this.addAll(collection);
    }
    
    public ConcurrentSkipListSet(final SortedSet<E> set) {
        this.m = new ConcurrentSkipListMap<E, Object>(set.comparator());
        this.addAll((Collection<? extends E>)set);
    }
    
    ConcurrentSkipListSet(final ConcurrentNavigableMap<E, Object> m) {
        this.m = m;
    }
    
    public ConcurrentSkipListSet<E> clone() {
        try {
            final ConcurrentSkipListSet set = (ConcurrentSkipListSet)super.clone();
            set.setMap(new ConcurrentSkipListMap(this.m));
            return set;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    @Override
    public int size() {
        return this.m.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.m.isEmpty();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.m.containsKey(o);
    }
    
    @Override
    public boolean add(final E e) {
        return this.m.putIfAbsent(e, Boolean.TRUE) == null;
    }
    
    @Override
    public boolean remove(final Object o) {
        return this.m.remove(o, Boolean.TRUE);
    }
    
    @Override
    public void clear() {
        this.m.clear();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.m.navigableKeySet().iterator();
    }
    
    @Override
    public Iterator<E> descendingIterator() {
        return this.m.descendingKeySet().iterator();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        final Collection collection = (Collection)o;
        try {
            return this.containsAll(collection) && collection.containsAll(this);
        }
        catch (final ClassCastException ex) {
            return false;
        }
        catch (final NullPointerException ex2) {
            return false;
        }
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) {
        boolean b = false;
        final Iterator<?> iterator = collection.iterator();
        while (iterator.hasNext()) {
            if (this.remove(iterator.next())) {
                b = true;
            }
        }
        return b;
    }
    
    @Override
    public E lower(final E e) {
        return this.m.lowerKey(e);
    }
    
    @Override
    public E floor(final E e) {
        return this.m.floorKey(e);
    }
    
    @Override
    public E ceiling(final E e) {
        return this.m.ceilingKey(e);
    }
    
    @Override
    public E higher(final E e) {
        return this.m.higherKey(e);
    }
    
    @Override
    public E pollFirst() {
        final Map.Entry<Object, Object> pollFirstEntry = this.m.pollFirstEntry();
        return (pollFirstEntry == null) ? null : pollFirstEntry.getKey();
    }
    
    @Override
    public E pollLast() {
        final Map.Entry<Object, Object> pollLastEntry = this.m.pollLastEntry();
        return (pollLastEntry == null) ? null : pollLastEntry.getKey();
    }
    
    @Override
    public Comparator<? super E> comparator() {
        return this.m.comparator();
    }
    
    @Override
    public E first() {
        return this.m.firstKey();
    }
    
    @Override
    public E last() {
        return this.m.lastKey();
    }
    
    @Override
    public NavigableSet<E> subSet(final E e, final boolean b, final E e2, final boolean b2) {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap<Object, Object>)this.m.subMap(e, b, e2, b2));
    }
    
    @Override
    public NavigableSet<E> headSet(final E e, final boolean b) {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap<Object, Object>)this.m.headMap(e, b));
    }
    
    @Override
    public NavigableSet<E> tailSet(final E e, final boolean b) {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap<Object, Object>)this.m.tailMap(e, b));
    }
    
    @Override
    public NavigableSet<E> subSet(final E e, final E e2) {
        return this.subSet(e, true, e2, false);
    }
    
    @Override
    public NavigableSet<E> headSet(final E e) {
        return this.headSet(e, false);
    }
    
    @Override
    public NavigableSet<E> tailSet(final E e) {
        return this.tailSet(e, true);
    }
    
    @Override
    public NavigableSet<E> descendingSet() {
        return new ConcurrentSkipListSet((ConcurrentNavigableMap<Object, Object>)this.m.descendingMap());
    }
    
    @Override
    public Spliterator<E> spliterator() {
        if (this.m instanceof ConcurrentSkipListMap) {
            return ((ConcurrentSkipListMap)this.m).keySpliterator();
        }
        return (Spliterator)((ConcurrentSkipListMap.SubMap)this.m).keyIterator();
    }
    
    private void setMap(final ConcurrentNavigableMap<E, Object> concurrentNavigableMap) {
        ConcurrentSkipListSet.UNSAFE.putObjectVolatile(this, ConcurrentSkipListSet.mapOffset, concurrentNavigableMap);
    }
    
    static {
        try {
            UNSAFE = Unsafe.getUnsafe();
            mapOffset = ConcurrentSkipListSet.UNSAFE.objectFieldOffset(ConcurrentSkipListSet.class.getDeclaredField("m"));
        }
        catch (final Exception ex) {
            throw new Error(ex);
        }
    }
}
