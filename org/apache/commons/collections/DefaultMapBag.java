package org.apache.commons.collections;

import java.util.ConcurrentModificationException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections.set.UnmodifiableSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;

public abstract class DefaultMapBag implements Bag
{
    private Map _map;
    private int _total;
    private int _mods;
    
    public DefaultMapBag() {
        this._map = null;
        this._total = 0;
        this._mods = 0;
    }
    
    protected DefaultMapBag(final Map map) {
        this._map = null;
        this._total = 0;
        this._mods = 0;
        this.setMap(map);
    }
    
    public boolean add(final Object object) {
        return this.add(object, 1);
    }
    
    public boolean add(final Object object, final int nCopies) {
        ++this._mods;
        if (nCopies > 0) {
            final int count = nCopies + this.getCount(object);
            this._map.put(object, new Integer(count));
            this._total += nCopies;
            return count == nCopies;
        }
        return false;
    }
    
    public boolean addAll(final Collection coll) {
        boolean changed = false;
        final Iterator i = coll.iterator();
        while (i.hasNext()) {
            final boolean added = this.add(i.next());
            changed = (changed || added);
        }
        return changed;
    }
    
    public void clear() {
        ++this._mods;
        this._map.clear();
        this._total = 0;
    }
    
    public boolean contains(final Object object) {
        return this._map.containsKey(object);
    }
    
    public boolean containsAll(final Collection coll) {
        return this.containsAll(new HashBag(coll));
    }
    
    public boolean containsAll(final Bag other) {
        boolean result = true;
        final Iterator i = other.uniqueSet().iterator();
        while (i.hasNext()) {
            final Object current = i.next();
            final boolean contains = this.getCount(current) >= other.getCount(current);
            result = (result && contains);
        }
        return result;
    }
    
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Bag)) {
            return false;
        }
        final Bag other = (Bag)object;
        if (other.size() != this.size()) {
            return false;
        }
        final Iterator it = this._map.keySet().iterator();
        while (it.hasNext()) {
            final Object element = it.next();
            if (other.getCount(element) != this.getCount(element)) {
                return false;
            }
        }
        return true;
    }
    
    public int hashCode() {
        return this._map.hashCode();
    }
    
    public boolean isEmpty() {
        return this._map.isEmpty();
    }
    
    public Iterator iterator() {
        return new BagIterator(this, this.extractList().iterator());
    }
    
    public boolean remove(final Object object) {
        return this.remove(object, this.getCount(object));
    }
    
    public boolean remove(final Object object, final int nCopies) {
        ++this._mods;
        boolean result = false;
        final int count = this.getCount(object);
        if (nCopies <= 0) {
            result = false;
        }
        else if (count > nCopies) {
            this._map.put(object, new Integer(count - nCopies));
            result = true;
            this._total -= nCopies;
        }
        else {
            result = (this._map.remove(object) != null);
            this._total -= count;
        }
        return result;
    }
    
    public boolean removeAll(final Collection coll) {
        boolean result = false;
        if (coll != null) {
            final Iterator i = coll.iterator();
            while (i.hasNext()) {
                final boolean changed = this.remove(i.next(), 1);
                result = (result || changed);
            }
        }
        return result;
    }
    
    public boolean retainAll(final Collection coll) {
        return this.retainAll(new HashBag(coll));
    }
    
    public boolean retainAll(final Bag other) {
        boolean result = false;
        final Bag excess = new HashBag();
        final Iterator i = this.uniqueSet().iterator();
        while (i.hasNext()) {
            final Object current = i.next();
            final int myCount = this.getCount(current);
            final int otherCount = other.getCount(current);
            if (1 <= otherCount && otherCount <= myCount) {
                excess.add(current, myCount - otherCount);
            }
            else {
                excess.add(current, myCount);
            }
        }
        if (!excess.isEmpty()) {
            result = this.removeAll(excess);
        }
        return result;
    }
    
    public Object[] toArray() {
        return this.extractList().toArray();
    }
    
    public Object[] toArray(final Object[] array) {
        return this.extractList().toArray(array);
    }
    
    public int getCount(final Object object) {
        int result = 0;
        final Integer count = MapUtils.getInteger(this._map, object);
        if (count != null) {
            result = count;
        }
        return result;
    }
    
    public Set uniqueSet() {
        return UnmodifiableSet.decorate(this._map.keySet());
    }
    
    public int size() {
        return this._total;
    }
    
    protected int calcTotalSize() {
        return this._total = this.extractList().size();
    }
    
    protected void setMap(final Map map) {
        if (map == null || !map.isEmpty()) {
            throw new IllegalArgumentException("The map must be non-null and empty");
        }
        this._map = map;
    }
    
    protected Map getMap() {
        return this._map;
    }
    
    private List extractList() {
        final List result = new ArrayList();
        final Iterator i = this.uniqueSet().iterator();
        while (i.hasNext()) {
            final Object current = i.next();
            for (int index = this.getCount(current); index > 0; --index) {
                result.add(current);
            }
        }
        return result;
    }
    
    private int modCount() {
        return this._mods;
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append("[");
        final Iterator i = this.uniqueSet().iterator();
        while (i.hasNext()) {
            final Object current = i.next();
            final int count = this.getCount(current);
            buf.append(count);
            buf.append(":");
            buf.append(current);
            if (i.hasNext()) {
                buf.append(",");
            }
        }
        buf.append("]");
        return buf.toString();
    }
    
    static class BagIterator implements Iterator
    {
        private DefaultMapBag _parent;
        private Iterator _support;
        private Object _current;
        private int _mods;
        
        public BagIterator(final DefaultMapBag parent, final Iterator support) {
            this._parent = null;
            this._support = null;
            this._current = null;
            this._mods = 0;
            this._parent = parent;
            this._support = support;
            this._current = null;
            this._mods = parent.modCount();
        }
        
        public boolean hasNext() {
            return this._support.hasNext();
        }
        
        public Object next() {
            if (this._parent.modCount() != this._mods) {
                throw new ConcurrentModificationException();
            }
            return this._current = this._support.next();
        }
        
        public void remove() {
            if (this._parent.modCount() != this._mods) {
                throw new ConcurrentModificationException();
            }
            this._support.remove();
            this._parent.remove(this._current, 1);
            ++this._mods;
        }
    }
}
