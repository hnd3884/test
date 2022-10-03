package org.apache.commons.collections4.list;

import org.apache.commons.collections4.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections4.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections4.ListUtils;
import java.util.ListIterator;
import java.util.Iterator;
import org.apache.commons.collections4.set.UnmodifiableSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUniqueList<E> extends AbstractSerializableListDecorator<E>
{
    private static final long serialVersionUID = 7196982186153478694L;
    private final Set<E> set;
    
    public static <E> SetUniqueList<E> setUniqueList(final List<E> list) {
        if (list == null) {
            throw new NullPointerException("List must not be null");
        }
        if (list.isEmpty()) {
            return new SetUniqueList<E>(list, new HashSet<E>());
        }
        final List<E> temp = new ArrayList<E>((Collection<? extends E>)list);
        list.clear();
        final SetUniqueList<E> sl = new SetUniqueList<E>(list, new HashSet<E>());
        sl.addAll((Collection<? extends E>)temp);
        return sl;
    }
    
    protected SetUniqueList(final List<E> list, final Set<E> set) {
        super(list);
        if (set == null) {
            throw new NullPointerException("Set must not be null");
        }
        this.set = set;
    }
    
    public Set<E> asSet() {
        return UnmodifiableSet.unmodifiableSet((Set<? extends E>)this.set);
    }
    
    @Override
    public boolean add(final E object) {
        final int sizeBefore = this.size();
        this.add(this.size(), object);
        return sizeBefore != this.size();
    }
    
    @Override
    public void add(final int index, final E object) {
        if (!this.set.contains(object)) {
            super.add(index, object);
            this.set.add(object);
        }
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        return this.addAll(this.size(), coll);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        final List<E> temp = new ArrayList<E>();
        for (final E e : coll) {
            if (this.set.add(e)) {
                temp.add(e);
            }
        }
        return super.addAll(index, (Collection<? extends E>)temp);
    }
    
    @Override
    public E set(final int index, final E object) {
        final int pos = this.indexOf(object);
        final E removed = super.set(index, object);
        if (pos != -1 && pos != index) {
            super.remove(pos);
        }
        this.set.remove(removed);
        this.set.add(object);
        return removed;
    }
    
    @Override
    public boolean remove(final Object object) {
        final boolean result = this.set.remove(object);
        if (result) {
            super.remove(object);
        }
        return result;
    }
    
    @Override
    public E remove(final int index) {
        final E result = super.remove(index);
        this.set.remove(result);
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
        final boolean result = this.set.retainAll(coll);
        if (!result) {
            return false;
        }
        if (this.set.size() == 0) {
            super.clear();
        }
        else {
            super.retainAll(this.set);
        }
        return result;
    }
    
    @Override
    public void clear() {
        super.clear();
        this.set.clear();
    }
    
    @Override
    public boolean contains(final Object object) {
        return this.set.contains(object);
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        return this.set.containsAll(coll);
    }
    
    @Override
    public Iterator<E> iterator() {
        return (Iterator<E>)new SetListIterator(super.iterator(), (Set<Object>)this.set);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return new SetListListIterator<E>(super.listIterator(), this.set);
    }
    
    @Override
    public ListIterator<E> listIterator(final int index) {
        return new SetListListIterator<E>(super.listIterator(index), this.set);
    }
    
    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        final List<E> superSubList = super.subList(fromIndex, toIndex);
        final Set<E> subSet = this.createSetBasedOnList(this.set, superSubList);
        return ListUtils.unmodifiableList((List<? extends E>)new SetUniqueList<E>((List<? extends E>)superSubList, (Set<? extends E>)subSet));
    }
    
    protected Set<E> createSetBasedOnList(final Set<E> set, final List<E> list) {
        Set<E> subSet;
        if (set.getClass().equals(HashSet.class)) {
            subSet = new HashSet<E>(list.size());
        }
        else {
            try {
                subSet = (Set)set.getClass().newInstance();
            }
            catch (final InstantiationException ie) {
                subSet = new HashSet<E>();
            }
            catch (final IllegalAccessException iae) {
                subSet = new HashSet<E>();
            }
        }
        subSet.addAll((Collection<? extends E>)list);
        return subSet;
    }
    
    static class SetListIterator<E> extends AbstractIteratorDecorator<E>
    {
        private final Set<E> set;
        private E last;
        
        protected SetListIterator(final Iterator<E> it, final Set<E> set) {
            super(it);
            this.last = null;
            this.set = set;
        }
        
        @Override
        public E next() {
            return this.last = super.next();
        }
        
        @Override
        public void remove() {
            super.remove();
            this.set.remove(this.last);
            this.last = null;
        }
    }
    
    static class SetListListIterator<E> extends AbstractListIteratorDecorator<E>
    {
        private final Set<E> set;
        private E last;
        
        protected SetListListIterator(final ListIterator<E> it, final Set<E> set) {
            super(it);
            this.last = null;
            this.set = set;
        }
        
        @Override
        public E next() {
            return this.last = super.next();
        }
        
        @Override
        public E previous() {
            return this.last = super.previous();
        }
        
        @Override
        public void remove() {
            super.remove();
            this.set.remove(this.last);
            this.last = null;
        }
        
        @Override
        public void add(final E object) {
            if (!this.set.contains(object)) {
                super.add(object);
                this.set.add(object);
            }
        }
        
        @Override
        public void set(final E object) {
            throw new UnsupportedOperationException("ListIterator does not support set");
        }
    }
}
