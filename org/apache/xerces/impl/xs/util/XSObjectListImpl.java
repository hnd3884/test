package org.apache.xerces.impl.xs.util;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Iterator;
import org.apache.xerces.xs.XSObject;
import java.util.ListIterator;
import org.apache.xerces.xs.XSObjectList;
import java.util.AbstractList;

public class XSObjectListImpl extends AbstractList implements XSObjectList
{
    public static final XSObjectListImpl EMPTY_LIST;
    private static final ListIterator EMPTY_ITERATOR;
    private static final int DEFAULT_SIZE = 4;
    private XSObject[] fArray;
    private int fLength;
    
    public XSObjectListImpl() {
        this.fArray = null;
        this.fLength = 0;
        this.fArray = new XSObject[4];
        this.fLength = 0;
    }
    
    public XSObjectListImpl(final XSObject[] fArray, final int fLength) {
        this.fArray = null;
        this.fLength = 0;
        this.fArray = fArray;
        this.fLength = fLength;
    }
    
    public int getLength() {
        return this.fLength;
    }
    
    public XSObject item(final int n) {
        if (n < 0 || n >= this.fLength) {
            return null;
        }
        return this.fArray[n];
    }
    
    public void clearXSObjectList() {
        for (int i = 0; i < this.fLength; ++i) {
            this.fArray[i] = null;
        }
        this.fArray = null;
        this.fLength = 0;
    }
    
    public void addXSObject(final XSObject xsObject) {
        if (this.fLength == this.fArray.length) {
            final XSObject[] fArray = new XSObject[this.fLength + 4];
            System.arraycopy(this.fArray, 0, fArray, 0, this.fLength);
            this.fArray = fArray;
        }
        this.fArray[this.fLength++] = xsObject;
    }
    
    public void addXSObject(final int n, final XSObject xsObject) {
        this.fArray[n] = xsObject;
    }
    
    public boolean contains(final Object o) {
        return (o == null) ? this.containsNull() : this.containsObject(o);
    }
    
    public Object get(final int n) {
        if (n >= 0 && n < this.fLength) {
            return this.fArray[n];
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    public int size() {
        return this.getLength();
    }
    
    public Iterator iterator() {
        return this.listIterator0(0);
    }
    
    public ListIterator listIterator() {
        return this.listIterator0(0);
    }
    
    public ListIterator listIterator(final int n) {
        if (n >= 0 && n < this.fLength) {
            return this.listIterator0(n);
        }
        throw new IndexOutOfBoundsException("Index: " + n);
    }
    
    private ListIterator listIterator0(final int n) {
        return (this.fLength == 0) ? XSObjectListImpl.EMPTY_ITERATOR : new XSObjectListIterator(n);
    }
    
    private boolean containsObject(final Object o) {
        for (int i = this.fLength - 1; i >= 0; --i) {
            if (o.equals(this.fArray[i])) {
                return true;
            }
        }
        return false;
    }
    
    private boolean containsNull() {
        for (int i = this.fLength - 1; i >= 0; --i) {
            if (this.fArray[i] == null) {
                return true;
            }
        }
        return false;
    }
    
    public Object[] toArray() {
        final Object[] array = new Object[this.fLength];
        this.toArray0(array);
        return array;
    }
    
    public Object[] toArray(Object[] array) {
        if (array.length < this.fLength) {
            array = (Object[])Array.newInstance(array.getClass().getComponentType(), this.fLength);
        }
        this.toArray0(array);
        if (array.length > this.fLength) {
            array[this.fLength] = null;
        }
        return array;
    }
    
    private void toArray0(final Object[] array) {
        if (this.fLength > 0) {
            System.arraycopy(this.fArray, 0, array, 0, this.fLength);
        }
    }
    
    static {
        EMPTY_LIST = new XSObjectListImpl(new XSObject[0], 0);
        EMPTY_ITERATOR = new ListIterator() {
            public boolean hasNext() {
                return false;
            }
            
            public Object next() {
                throw new NoSuchElementException();
            }
            
            public boolean hasPrevious() {
                return false;
            }
            
            public Object previous() {
                throw new NoSuchElementException();
            }
            
            public int nextIndex() {
                return 0;
            }
            
            public int previousIndex() {
                return -1;
            }
            
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            public void set(final Object o) {
                throw new UnsupportedOperationException();
            }
            
            public void add(final Object o) {
                throw new UnsupportedOperationException();
            }
        };
    }
    
    private final class XSObjectListIterator implements ListIterator
    {
        private int index;
        
        public XSObjectListIterator(final int index) {
            this.index = index;
        }
        
        public boolean hasNext() {
            return this.index < XSObjectListImpl.this.fLength;
        }
        
        public Object next() {
            if (this.index < XSObjectListImpl.this.fLength) {
                return XSObjectListImpl.this.fArray[this.index++];
            }
            throw new NoSuchElementException();
        }
        
        public boolean hasPrevious() {
            return this.index > 0;
        }
        
        public Object previous() {
            if (this.index > 0) {
                final XSObject[] access$100 = XSObjectListImpl.this.fArray;
                final int index = this.index - 1;
                this.index = index;
                return access$100[index];
            }
            throw new NoSuchElementException();
        }
        
        public int nextIndex() {
            return this.index;
        }
        
        public int previousIndex() {
            return this.index - 1;
        }
        
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        public void set(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        public void add(final Object o) {
            throw new UnsupportedOperationException();
        }
    }
}
