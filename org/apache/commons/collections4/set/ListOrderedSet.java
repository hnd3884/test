package org.apache.commons.collections4.set;

import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import java.util.Iterator;
import java.util.ListIterator;
import org.apache.commons.collections4.OrderedIterator;
import org.apache.commons.collections4.list.UnmodifiableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.functors.UniquePredicate;
import java.util.Set;
import java.util.List;

public class ListOrderedSet<E> extends AbstractSerializableSetDecorator<E>
{
    private static final long serialVersionUID = -228664372470420141L;
    private final List<E> setOrder;
    
    public static <E> ListOrderedSet<E> listOrderedSet(final Set<E> set, final List<E> list) {
        if (set == null) {
            throw new NullPointerException("Set must not be null");
        }
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        if (set.size() > 0 || list.size() > 0) {
            throw new IllegalArgumentException("Set and List must be empty");
        }
        return new ListOrderedSet<E>(set, list);
    }
    
    public static <E> ListOrderedSet<E> listOrderedSet(final Set<E> set) {
        return new ListOrderedSet<E>(set);
    }
    
    public static <E> ListOrderedSet<E> listOrderedSet(final List<E> list) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        CollectionUtils.filter(list, UniquePredicate.uniquePredicate());
        final Set<E> set = new HashSet<E>((Collection<? extends E>)list);
        return new ListOrderedSet<E>(set, list);
    }
    
    public ListOrderedSet() {
        super(new HashSet());
        this.setOrder = new ArrayList<E>();
    }
    
    protected ListOrderedSet(final Set<E> set) {
        super(set);
        this.setOrder = new ArrayList<E>((Collection<? extends E>)set);
    }
    
    protected ListOrderedSet(final Set<E> set, final List<E> list) {
        super(set);
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        this.setOrder = list;
    }
    
    public List<E> asList() {
        return UnmodifiableList.unmodifiableList((List<? extends E>)this.setOrder);
    }
    
    @Override
    public void clear() {
        this.decorated().clear();
        this.setOrder.clear();
    }
    
    @Override
    public OrderedIterator<E> iterator() {
        return new OrderedSetIterator<E>((ListIterator)this.setOrder.listIterator(), (Collection)this.decorated());
    }
    
    @Override
    public boolean add(final E object) {
        if (this.decorated().add(object)) {
            this.setOrder.add(object);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        boolean result = false;
        for (final E e : coll) {
            result |= this.add(e);
        }
        return result;
    }
    
    @Override
    public boolean remove(final Object object) {
        final boolean result = this.decorated().remove(object);
        if (result) {
            this.setOrder.remove(object);
        }
        return result;
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        boolean result = false;
        for (final Object name : coll) {
            result |= this.remove(name);
        }
        return result;
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        final boolean result = this.decorated().retainAll(coll);
        if (!result) {
            return false;
        }
        if (this.decorated().size() == 0) {
            this.setOrder.clear();
        }
        else {
            final Iterator<E> it = this.setOrder.iterator();
            while (it.hasNext()) {
                if (!this.decorated().contains(it.next())) {
                    it.remove();
                }
            }
        }
        return result;
    }
    
    @Override
    public Object[] toArray() {
        return this.setOrder.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] a) {
        return this.setOrder.toArray(a);
    }
    
    public E get(final int index) {
        return this.setOrder.get(index);
    }
    
    public int indexOf(final Object object) {
        return this.setOrder.indexOf(object);
    }
    
    public void add(final int index, final E object) {
        if (!this.contains(object)) {
            this.decorated().add(object);
            this.setOrder.add(index, object);
        }
    }
    
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        boolean changed = false;
        final List<E> toAdd = new ArrayList<E>();
        for (final E e : coll) {
            if (this.contains(e)) {
                continue;
            }
            this.decorated().add(e);
            toAdd.add(e);
            changed = true;
        }
        if (changed) {
            this.setOrder.addAll(index, (Collection<? extends E>)toAdd);
        }
        return changed;
    }
    
    public E remove(final int index) {
        final E obj = this.setOrder.remove(index);
        this.remove(obj);
        return obj;
    }
    
    @Override
    public String toString() {
        return this.setOrder.toString();
    }
    
    static class OrderedSetIterator<E> extends AbstractIteratorDecorator<E> implements OrderedIterator<E>
    {
        private final Collection<E> set;
        private E last;
        
        private OrderedSetIterator(final ListIterator<E> iterator, final Collection<E> set) {
            super(iterator);
            this.set = set;
        }
        
        @Override
        public E next() {
            return this.last = (E)this.getIterator().next();
        }
        
        @Override
        public void remove() {
            this.set.remove(this.last);
            this.getIterator().remove();
            this.last = null;
        }
        
        @Override
        public boolean hasPrevious() {
            return ((ListIterator)this.getIterator()).hasPrevious();
        }
        
        @Override
        public E previous() {
            return this.last = ((ListIterator)this.getIterator()).previous();
        }
    }
}
