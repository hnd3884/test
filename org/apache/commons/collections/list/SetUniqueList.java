package org.apache.commons.collections.list;

import org.apache.commons.collections.iterators.AbstractListIteratorDecorator;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import java.util.ListIterator;
import java.util.Iterator;
import org.apache.commons.collections.set.UnmodifiableSet;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetUniqueList extends AbstractSerializableListDecorator
{
    private static final long serialVersionUID = 7196982186153478694L;
    protected final Set set;
    
    public static SetUniqueList decorate(final List list) {
        if (list == null) {
            throw new IllegalArgumentException("List must not be null");
        }
        if (list.isEmpty()) {
            return new SetUniqueList(list, new HashSet());
        }
        final List temp = new ArrayList(list);
        list.clear();
        final SetUniqueList sl = new SetUniqueList(list, new HashSet());
        sl.addAll(temp);
        return sl;
    }
    
    protected SetUniqueList(final List list, final Set set) {
        super(list);
        if (set == null) {
            throw new IllegalArgumentException("Set must not be null");
        }
        this.set = set;
    }
    
    public Set asSet() {
        return UnmodifiableSet.decorate(this.set);
    }
    
    public boolean add(final Object object) {
        final int sizeBefore = this.size();
        this.add(this.size(), object);
        return sizeBefore != this.size();
    }
    
    public void add(final int index, final Object object) {
        if (!this.set.contains(object)) {
            super.add(index, object);
            this.set.add(object);
        }
    }
    
    public boolean addAll(final Collection coll) {
        return this.addAll(this.size(), coll);
    }
    
    public boolean addAll(int index, final Collection coll) {
        final int sizeBefore = this.size();
        final Iterator it = coll.iterator();
        while (it.hasNext()) {
            final int sizeBeforeAddNext = this.size();
            this.add(index, it.next());
            if (sizeBeforeAddNext != this.size()) {
                ++index;
            }
        }
        return sizeBefore != this.size();
    }
    
    public Object set(final int index, final Object object) {
        final int pos = this.indexOf(object);
        final Object removed = super.set(index, object);
        if (pos != -1 && pos != index) {
            super.remove(pos);
        }
        this.set.remove(removed);
        this.set.add(object);
        return removed;
    }
    
    public boolean remove(final Object object) {
        final boolean result = super.remove(object);
        this.set.remove(object);
        return result;
    }
    
    public Object remove(final int index) {
        final Object result = super.remove(index);
        this.set.remove(result);
        return result;
    }
    
    public boolean removeAll(final Collection coll) {
        final boolean result = super.removeAll(coll);
        this.set.removeAll(coll);
        return result;
    }
    
    public boolean retainAll(final Collection coll) {
        final boolean result = super.retainAll(coll);
        this.set.retainAll(coll);
        return result;
    }
    
    public void clear() {
        super.clear();
        this.set.clear();
    }
    
    public boolean contains(final Object object) {
        return this.set.contains(object);
    }
    
    public boolean containsAll(final Collection coll) {
        return this.set.containsAll(coll);
    }
    
    public Iterator iterator() {
        return new SetListIterator(super.iterator(), this.set);
    }
    
    public ListIterator listIterator() {
        return new SetListListIterator(super.listIterator(), this.set);
    }
    
    public ListIterator listIterator(final int index) {
        return new SetListListIterator(super.listIterator(index), this.set);
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        final List superSubList = super.subList(fromIndex, toIndex);
        final Set subSet = this.createSetBasedOnList(this.set, superSubList);
        return new SetUniqueList(superSubList, subSet);
    }
    
    protected Set createSetBasedOnList(final Set set, final List list) {
        Set subSet = null;
        if (set.getClass().equals(HashSet.class)) {
            subSet = new HashSet();
        }
        else {
            try {
                subSet = (Set)set.getClass().newInstance();
            }
            catch (final InstantiationException ie) {
                subSet = new HashSet();
            }
            catch (final IllegalAccessException iae) {
                subSet = new HashSet();
            }
        }
        subSet.addAll(list);
        return subSet;
    }
    
    static class SetListIterator extends AbstractIteratorDecorator
    {
        protected final Set set;
        protected Object last;
        
        protected SetListIterator(final Iterator it, final Set set) {
            super(it);
            this.last = null;
            this.set = set;
        }
        
        public Object next() {
            return this.last = super.next();
        }
        
        public void remove() {
            super.remove();
            this.set.remove(this.last);
            this.last = null;
        }
    }
    
    static class SetListListIterator extends AbstractListIteratorDecorator
    {
        protected final Set set;
        protected Object last;
        
        protected SetListListIterator(final ListIterator it, final Set set) {
            super(it);
            this.last = null;
            this.set = set;
        }
        
        public Object next() {
            return this.last = super.next();
        }
        
        public Object previous() {
            return this.last = super.previous();
        }
        
        public void remove() {
            super.remove();
            this.set.remove(this.last);
            this.last = null;
        }
        
        public void add(final Object object) {
            if (!this.set.contains(object)) {
                super.add(object);
                this.set.add(object);
            }
        }
        
        public void set(final Object object) {
            throw new UnsupportedOperationException("ListIterator does not support set");
        }
    }
}
