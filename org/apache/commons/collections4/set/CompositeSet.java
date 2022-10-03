package org.apache.commons.collections4.set;

import org.apache.commons.collections4.list.UnmodifiableList;
import java.util.HashSet;
import org.apache.commons.collections4.CollectionUtils;
import java.util.Collection;
import java.lang.reflect.Array;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.EmptyIterator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Set;

public class CompositeSet<E> implements Set<E>, Serializable
{
    private static final long serialVersionUID = 5185069727540378940L;
    private SetMutator<E> mutator;
    private final List<Set<E>> all;
    
    public CompositeSet() {
        this.all = new ArrayList<Set<E>>();
    }
    
    public CompositeSet(final Set<E> set) {
        this.all = new ArrayList<Set<E>>();
        this.addComposited(set);
    }
    
    public CompositeSet(final Set<E>... sets) {
        this.all = new ArrayList<Set<E>>();
        this.addComposited(sets);
    }
    
    @Override
    public int size() {
        int size = 0;
        for (final Set<E> item : this.all) {
            size += item.size();
        }
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        for (final Set<E> item : this.all) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean contains(final Object obj) {
        for (final Set<E> item : this.all) {
            if (item.contains(obj)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Iterator<E> iterator() {
        if (this.all.isEmpty()) {
            return EmptyIterator.emptyIterator();
        }
        final IteratorChain<E> chain = new IteratorChain<E>();
        for (final Set<E> item : this.all) {
            chain.addIterator((Iterator<? extends E>)item.iterator());
        }
        return chain;
    }
    
    @Override
    public Object[] toArray() {
        final Object[] result = new Object[this.size()];
        int i = 0;
        final Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            result[i] = it.next();
            ++i;
        }
        return result;
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        final int size = this.size();
        Object[] result = null;
        if (array.length >= size) {
            result = array;
        }
        else {
            result = (Object[])Array.newInstance(array.getClass().getComponentType(), size);
        }
        int offset = 0;
        for (final Collection<E> item : this.all) {
            for (final E e : item) {
                result[offset++] = e;
            }
        }
        if (result.length > size) {
            result[size] = null;
        }
        return (T[])result;
    }
    
    @Override
    public boolean add(final E obj) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("add() is not supported on CompositeSet without a SetMutator strategy");
        }
        return this.mutator.add(this, this.all, obj);
    }
    
    @Override
    public boolean remove(final Object obj) {
        for (final Set<E> set : this.getSets()) {
            if (set.contains(obj)) {
                return set.remove(obj);
            }
        }
        return false;
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        for (final Object item : coll) {
            if (!this.contains(item)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("addAll() is not supported on CompositeSet without a SetMutator strategy");
        }
        return this.mutator.addAll(this, this.all, coll);
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        if (coll.size() == 0) {
            return false;
        }
        boolean changed = false;
        for (final Collection<E> item : this.all) {
            changed |= item.removeAll(coll);
        }
        return changed;
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        boolean changed = false;
        for (final Collection<E> item : this.all) {
            changed |= item.retainAll(coll);
        }
        return changed;
    }
    
    @Override
    public void clear() {
        for (final Collection<E> coll : this.all) {
            coll.clear();
        }
    }
    
    public void setMutator(final SetMutator<E> mutator) {
        this.mutator = mutator;
    }
    
    public synchronized void addComposited(final Set<E> set) {
        for (final Set<E> existingSet : this.getSets()) {
            final Collection<E> intersects = CollectionUtils.intersection((Iterable<? extends E>)existingSet, (Iterable<? extends E>)set);
            if (intersects.size() > 0) {
                if (this.mutator == null) {
                    throw new UnsupportedOperationException("Collision adding composited set with no SetMutator set");
                }
                this.getMutator().resolveCollision(this, existingSet, set, intersects);
                if (CollectionUtils.intersection((Iterable<?>)existingSet, (Iterable<?>)set).size() > 0) {
                    throw new IllegalArgumentException("Attempt to add illegal entry unresolved by SetMutator.resolveCollision()");
                }
                continue;
            }
        }
        this.all.add(set);
    }
    
    public void addComposited(final Set<E> set1, final Set<E> set2) {
        this.addComposited(set1);
        this.addComposited(set2);
    }
    
    public void addComposited(final Set<E>... sets) {
        for (final Set<E> set : sets) {
            this.addComposited(set);
        }
    }
    
    public void removeComposited(final Set<E> set) {
        this.all.remove(set);
    }
    
    public Set<E> toSet() {
        return new HashSet<E>((Collection<? extends E>)this);
    }
    
    public List<Set<E>> getSets() {
        return UnmodifiableList.unmodifiableList((List<? extends Set<E>>)this.all);
    }
    
    protected SetMutator<E> getMutator() {
        return this.mutator;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Set) {
            final Set<?> set = (Set<?>)obj;
            return set.size() == this.size() && set.containsAll(this);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int code = 0;
        for (final E e : this) {
            code += ((e == null) ? 0 : e.hashCode());
        }
        return code;
    }
    
    public interface SetMutator<E> extends Serializable
    {
        boolean add(final CompositeSet<E> p0, final List<Set<E>> p1, final E p2);
        
        boolean addAll(final CompositeSet<E> p0, final List<Set<E>> p1, final Collection<? extends E> p2);
        
        void resolveCollision(final CompositeSet<E> p0, final Set<E> p1, final Set<E> p2, final Collection<E> p3);
    }
}
