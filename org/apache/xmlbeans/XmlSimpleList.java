package org.apache.xmlbeans;

import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import java.util.List;

public class XmlSimpleList implements List, Serializable
{
    private static final long serialVersionUID = 1L;
    private List underlying;
    
    public XmlSimpleList(final List list) {
        this.underlying = list;
    }
    
    @Override
    public int size() {
        return this.underlying.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.underlying.isEmpty();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.underlying.contains(o);
    }
    
    @Override
    public boolean containsAll(final Collection coll) {
        return this.underlying.containsAll(coll);
    }
    
    @Override
    public Object[] toArray() {
        return this.underlying.toArray();
    }
    
    @Override
    public Object[] toArray(final Object[] a) {
        return this.underlying.toArray(a);
    }
    
    @Override
    public boolean add(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean addAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean removeAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean retainAll(final Collection coll) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object get(final int index) {
        return this.underlying.get(index);
    }
    
    @Override
    public Object set(final int index, final Object element) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void add(final int index, final Object element) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Object remove(final int index) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int indexOf(final Object o) {
        return this.underlying.indexOf(o);
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        return this.underlying.lastIndexOf(o);
    }
    
    @Override
    public boolean addAll(final int index, final Collection c) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List subList(final int from, final int to) {
        return new XmlSimpleList(this.underlying.subList(from, to));
    }
    
    @Override
    public Iterator iterator() {
        return new Iterator() {
            Iterator i = XmlSimpleList.this.underlying.iterator();
            
            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }
            
            @Override
            public Object next() {
                return this.i.next();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    @Override
    public ListIterator listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        return new ListIterator() {
            ListIterator i = XmlSimpleList.this.underlying.listIterator(index);
            
            @Override
            public boolean hasNext() {
                return this.i.hasNext();
            }
            
            @Override
            public Object next() {
                return this.i.next();
            }
            
            @Override
            public boolean hasPrevious() {
                return this.i.hasPrevious();
            }
            
            @Override
            public Object previous() {
                return this.i.previous();
            }
            
            @Override
            public int nextIndex() {
                return this.i.nextIndex();
            }
            
            @Override
            public int previousIndex() {
                return this.i.previousIndex();
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void set(final Object o) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final Object o) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private String stringValue(final Object o) {
        if (o instanceof SimpleValue) {
            return ((SimpleValue)o).stringValue();
        }
        return o.toString();
    }
    
    @Override
    public String toString() {
        final int size = this.underlying.size();
        if (size == 0) {
            return "";
        }
        final String first = this.stringValue(this.underlying.get(0));
        if (size == 1) {
            return first;
        }
        final StringBuffer result = new StringBuffer(first);
        for (int i = 1; i < size; ++i) {
            result.append(' ');
            result.append(this.stringValue(this.underlying.get(i)));
        }
        return result.toString();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof XmlSimpleList)) {
            return false;
        }
        final XmlSimpleList xmlSimpleList = (XmlSimpleList)o;
        final List underlying2 = xmlSimpleList.underlying;
        final int size = this.underlying.size();
        if (size != underlying2.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            final Object item = this.underlying.get(i);
            final Object item2 = underlying2.get(i);
            if (item == null) {
                if (item2 != null) {
                    return false;
                }
            }
            else if (!item.equals(item2)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int size = this.underlying.size();
        int hash = 0;
        for (int i = 0; i < size; ++i) {
            final Object item = this.underlying.get(i);
            hash *= 19;
            hash += item.hashCode();
        }
        return hash;
    }
}
