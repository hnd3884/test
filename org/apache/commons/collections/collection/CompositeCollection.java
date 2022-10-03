package org.apache.commons.collections.collection;

import java.util.List;
import org.apache.commons.collections.list.UnmodifiableList;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.reflect.Array;
import org.apache.commons.collections.iterators.IteratorChain;
import org.apache.commons.collections.iterators.EmptyIterator;
import java.util.Iterator;
import java.util.Collection;

public class CompositeCollection implements Collection
{
    protected CollectionMutator mutator;
    protected Collection[] all;
    
    public CompositeCollection() {
        this.all = new Collection[0];
    }
    
    public CompositeCollection(final Collection coll) {
        this();
        this.addComposited(coll);
    }
    
    public CompositeCollection(final Collection[] colls) {
        this();
        this.addComposited(colls);
    }
    
    public int size() {
        int size = 0;
        for (int i = this.all.length - 1; i >= 0; --i) {
            size += this.all[i].size();
        }
        return size;
    }
    
    public boolean isEmpty() {
        for (int i = this.all.length - 1; i >= 0; --i) {
            if (!this.all[i].isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean contains(final Object obj) {
        for (int i = this.all.length - 1; i >= 0; --i) {
            if (this.all[i].contains(obj)) {
                return true;
            }
        }
        return false;
    }
    
    public Iterator iterator() {
        if (this.all.length == 0) {
            return EmptyIterator.INSTANCE;
        }
        final IteratorChain chain = new IteratorChain();
        for (int i = 0; i < this.all.length; ++i) {
            chain.addIterator(this.all[i].iterator());
        }
        return chain;
    }
    
    public Object[] toArray() {
        final Object[] result = new Object[this.size()];
        int i = 0;
        final Iterator it = this.iterator();
        while (it.hasNext()) {
            result[i] = it.next();
            ++i;
        }
        return result;
    }
    
    public Object[] toArray(final Object[] array) {
        final int size = this.size();
        Object[] result = null;
        if (array.length >= size) {
            result = array;
        }
        else {
            result = (Object[])Array.newInstance(array.getClass().getComponentType(), size);
        }
        int offset = 0;
        for (int i = 0; i < this.all.length; ++i) {
            final Iterator it = this.all[i].iterator();
            while (it.hasNext()) {
                result[offset++] = it.next();
            }
        }
        if (result.length > size) {
            result[size] = null;
        }
        return result;
    }
    
    public boolean add(final Object obj) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("add() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.add(this, this.all, obj);
    }
    
    public boolean remove(final Object obj) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("remove() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.remove(this, this.all, obj);
    }
    
    public boolean containsAll(final Collection coll) {
        final Iterator it = coll.iterator();
        while (it.hasNext()) {
            if (!this.contains(it.next())) {
                return false;
            }
        }
        return true;
    }
    
    public boolean addAll(final Collection coll) {
        if (this.mutator == null) {
            throw new UnsupportedOperationException("addAll() is not supported on CompositeCollection without a CollectionMutator strategy");
        }
        return this.mutator.addAll(this, this.all, coll);
    }
    
    public boolean removeAll(final Collection coll) {
        if (coll.size() == 0) {
            return false;
        }
        boolean changed = false;
        for (int i = this.all.length - 1; i >= 0; --i) {
            changed = (this.all[i].removeAll(coll) || changed);
        }
        return changed;
    }
    
    public boolean retainAll(final Collection coll) {
        boolean changed = false;
        for (int i = this.all.length - 1; i >= 0; --i) {
            changed = (this.all[i].retainAll(coll) || changed);
        }
        return changed;
    }
    
    public void clear() {
        for (int i = 0; i < this.all.length; ++i) {
            this.all[i].clear();
        }
    }
    
    public void setMutator(final CollectionMutator mutator) {
        this.mutator = mutator;
    }
    
    public void addComposited(final Collection[] comps) {
        final ArrayList list = new ArrayList((Collection<? extends E>)Arrays.asList((Collection[])this.all));
        list.addAll(Arrays.asList((Collection[])comps));
        this.all = list.toArray(new Collection[list.size()]);
    }
    
    public void addComposited(final Collection c) {
        this.addComposited(new Collection[] { c });
    }
    
    public void addComposited(final Collection c, final Collection d) {
        this.addComposited(new Collection[] { c, d });
    }
    
    public void removeComposited(final Collection coll) {
        final ArrayList list = new ArrayList(this.all.length);
        list.addAll(Arrays.asList((Collection[])this.all));
        list.remove(coll);
        this.all = list.toArray(new Collection[list.size()]);
    }
    
    public Collection toCollection() {
        return new ArrayList(this);
    }
    
    public Collection getCollections() {
        return UnmodifiableList.decorate(Arrays.asList((Collection[])this.all));
    }
    
    public interface CollectionMutator
    {
        boolean add(final CompositeCollection p0, final Collection[] p1, final Object p2);
        
        boolean addAll(final CompositeCollection p0, final Collection[] p1, final Collection p2);
        
        boolean remove(final CompositeCollection p0, final Collection[] p1, final Object p2);
    }
}
