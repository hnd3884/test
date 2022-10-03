package org.apache.commons.collections4.collection;

import org.apache.commons.collections4.list.UnmodifiableList;
import java.util.Arrays;
import java.lang.reflect.Array;
import org.apache.commons.collections4.iterators.IteratorChain;
import org.apache.commons.collections4.iterators.EmptyIterator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Collection;

public class CompositeCollection<E> implements Collection<E>, Serializable
{
    private static final long serialVersionUID = 8417515734108306801L;
    private CollectionMutator<E> mutator;
    private final List<Collection<E>> all;
    
    public CompositeCollection() {
        this.all = new ArrayList<Collection<E>>();
    }
    
    public CompositeCollection(final Collection<E> compositeCollection) {
        this.all = new ArrayList<Collection<E>>();
        this.addComposited(compositeCollection);
    }
    
    public CompositeCollection(final Collection<E> compositeCollection1, final Collection<E> compositeCollection2) {
        this.all = new ArrayList<Collection<E>>();
        this.addComposited(compositeCollection1, compositeCollection2);
    }
    
    public CompositeCollection(final Collection<E>... compositeCollections) {
        this.all = new ArrayList<Collection<E>>();
        this.addComposited(compositeCollections);
    }
    
    @Override
    public int size() {
        int size = 0;
        for (final Collection<E> item : this.all) {
            size += item.size();
        }
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        for (final Collection<E> item : this.all) {
            if (!item.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean contains(final Object obj) {
        for (final Collection<E> item : this.all) {
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
        for (final Collection<E> item : this.all) {
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
            throw new UnsupportedOperationException("add() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.add(this, this.all, obj);
    }
    
    @Override
    public boolean remove(final Object obj) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("remove() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.remove(this, this.all, obj);
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
            throw new UnsupportedOperationException("addAll() is not supported on CompositeCollection without a CollectionMutator strategy");
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
    
    public void setMutator(final CollectionMutator<E> mutator) {
        this.mutator = mutator;
    }
    
    public void addComposited(final Collection<E> compositeCollection) {
        this.all.add(compositeCollection);
    }
    
    public void addComposited(final Collection<E> compositeCollection1, final Collection<E> compositeCollection2) {
        this.all.add(compositeCollection1);
        this.all.add(compositeCollection2);
    }
    
    public void addComposited(final Collection<E>... compositeCollections) {
        this.all.addAll(Arrays.asList(compositeCollections));
    }
    
    public void removeComposited(final Collection<E> coll) {
        this.all.remove(coll);
    }
    
    public Collection<E> toCollection() {
        return new ArrayList<E>((Collection<? extends E>)this);
    }
    
    public List<Collection<E>> getCollections() {
        return UnmodifiableList.unmodifiableList((List<? extends Collection<E>>)this.all);
    }
    
    protected CollectionMutator<E> getMutator() {
        return this.mutator;
    }
    
    public interface CollectionMutator<E> extends Serializable
    {
        boolean add(final CompositeCollection<E> p0, final List<Collection<E>> p1, final E p2);
        
        boolean addAll(final CompositeCollection<E> p0, final List<Collection<E>> p1, final Collection<? extends E> p2);
        
        boolean remove(final CompositeCollection<E> p0, final List<Collection<E>> p1, final Object p2);
    }
}
