package com.unboundid.util;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import java.util.Set;

@Mutable
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class WeakHashSet<T> implements Set<T>
{
    private final WeakHashMap<T, WeakReference<T>> m;
    
    public WeakHashSet() {
        this.m = new WeakHashMap<T, WeakReference<T>>(16);
    }
    
    public WeakHashSet(final int initialCapacity) {
        this.m = new WeakHashMap<T, WeakReference<T>>(initialCapacity);
    }
    
    @Override
    public void clear() {
        this.m.clear();
    }
    
    @Override
    public boolean isEmpty() {
        return this.m.isEmpty();
    }
    
    @Override
    public int size() {
        return this.m.size();
    }
    
    @Override
    public boolean contains(final Object e) {
        return this.m.containsKey(e);
    }
    
    @Override
    public boolean containsAll(final Collection<?> c) {
        return this.m.keySet().containsAll(c);
    }
    
    public T get(final T e) {
        final WeakReference<T> r = this.m.get(e);
        if (r == null) {
            return null;
        }
        return r.get();
    }
    
    @Override
    public boolean add(final T e) {
        if (this.m.containsKey(e)) {
            return false;
        }
        this.m.put(e, new WeakReference<T>(e));
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends T> c) {
        boolean changed = false;
        for (final T e : c) {
            if (!this.m.containsKey(e)) {
                this.m.put(e, new WeakReference<T>(e));
                changed = true;
            }
        }
        return changed;
    }
    
    public T addAndGet(final T e) {
        final WeakReference<T> r = this.m.get(e);
        if (r != null) {
            final T existingElement = r.get();
            if (existingElement != null) {
                return existingElement;
            }
        }
        this.m.put(e, new WeakReference<T>(e));
        return e;
    }
    
    @Override
    public boolean remove(final Object e) {
        return this.m.remove(e) != null;
    }
    
    @Override
    public boolean removeAll(final Collection<?> c) {
        boolean changed = false;
        for (final Object o : c) {
            final Object e = this.m.remove(o);
            if (e != null) {
                changed = true;
            }
        }
        return changed;
    }
    
    @Override
    public boolean retainAll(final Collection<?> c) {
        boolean changed = false;
        final Iterator<Map.Entry<T, WeakReference<T>>> iterator = this.m.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<T, WeakReference<T>> e = iterator.next();
            if (!c.contains(e.getKey())) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.m.keySet().iterator();
    }
    
    @Override
    public Object[] toArray() {
        return this.m.keySet().toArray();
    }
    
    @Override
    public <E> E[] toArray(final E[] a) {
        return this.m.keySet().toArray(a);
    }
    
    @Override
    public int hashCode() {
        return this.m.keySet().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof Set && this.m.keySet().equals(o);
    }
    
    @Override
    public String toString() {
        return this.m.keySet().toString();
    }
}
