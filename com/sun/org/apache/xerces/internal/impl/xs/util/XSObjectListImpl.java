package com.sun.org.apache.xerces.internal.impl.xs.util;

import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.Iterator;
import com.sun.org.apache.xerces.internal.xs.XSObject;
import java.util.ListIterator;
import com.sun.org.apache.xerces.internal.xs.XSObjectList;
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
    
    public XSObjectListImpl(final XSObject[] array, final int length) {
        this.fArray = null;
        this.fLength = 0;
        this.fArray = array;
        this.fLength = length;
    }
    
    @Override
    public int getLength() {
        return this.fLength;
    }
    
    @Override
    public XSObject item(final int index) {
        if (index < 0 || index >= this.fLength) {
            return null;
        }
        return this.fArray[index];
    }
    
    public void clearXSObjectList() {
        for (int i = 0; i < this.fLength; ++i) {
            this.fArray[i] = null;
        }
        this.fArray = null;
        this.fLength = 0;
    }
    
    public void addXSObject(final XSObject object) {
        if (this.fLength == this.fArray.length) {
            final XSObject[] temp = new XSObject[this.fLength + 4];
            System.arraycopy(this.fArray, 0, temp, 0, this.fLength);
            this.fArray = temp;
        }
        this.fArray[this.fLength++] = object;
    }
    
    public void addXSObject(final int index, final XSObject object) {
        this.fArray[index] = object;
    }
    
    @Override
    public boolean contains(final Object value) {
        return (value == null) ? this.containsNull() : this.containsObject(value);
    }
    
    @Override
    public Object get(final int index) {
        if (index >= 0 && index < this.fLength) {
            return this.fArray[index];
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
    
    @Override
    public int size() {
        return this.getLength();
    }
    
    @Override
    public Iterator iterator() {
        return this.listIterator0(0);
    }
    
    @Override
    public ListIterator listIterator() {
        return this.listIterator0(0);
    }
    
    @Override
    public ListIterator listIterator(final int index) {
        if (index >= 0 && index < this.fLength) {
            return this.listIterator0(index);
        }
        throw new IndexOutOfBoundsException("Index: " + index);
    }
    
    private ListIterator listIterator0(final int index) {
        return (this.fLength == 0) ? XSObjectListImpl.EMPTY_ITERATOR : new XSObjectListIterator(index);
    }
    
    private boolean containsObject(final Object value) {
        for (int i = this.fLength - 1; i >= 0; --i) {
            if (value.equals(this.fArray[i])) {
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
    
    @Override
    public Object[] toArray() {
        final Object[] a = new Object[this.fLength];
        this.toArray0(a);
        return a;
    }
    
    @Override
    public Object[] toArray(Object[] a) {
        if (a.length < this.fLength) {
            final Class arrayClass = a.getClass();
            final Class componentType = arrayClass.getComponentType();
            a = (Object[])Array.newInstance(componentType, this.fLength);
        }
        this.toArray0(a);
        if (a.length > this.fLength) {
            a[this.fLength] = null;
        }
        return a;
    }
    
    private void toArray0(final Object[] a) {
        if (this.fLength > 0) {
            System.arraycopy(this.fArray, 0, a, 0, this.fLength);
        }
    }
    
    static {
        EMPTY_LIST = new XSObjectListImpl(new XSObject[0], 0);
        EMPTY_ITERATOR = new ListIterator() {
            @Override
            public boolean hasNext() {
                return false;
            }
            
            @Override
            public Object next() {
                throw new NoSuchElementException();
            }
            
            @Override
            public boolean hasPrevious() {
                return false;
            }
            
            @Override
            public Object previous() {
                throw new NoSuchElementException();
            }
            
            @Override
            public int nextIndex() {
                return 0;
            }
            
            @Override
            public int previousIndex() {
                return -1;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void set(final Object object) {
                throw new UnsupportedOperationException();
            }
            
            @Override
            public void add(final Object object) {
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
        
        @Override
        public boolean hasNext() {
            return this.index < XSObjectListImpl.this.fLength;
        }
        
        @Override
        public Object next() {
            if (this.index < XSObjectListImpl.this.fLength) {
                return XSObjectListImpl.this.fArray[this.index++];
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public boolean hasPrevious() {
            return this.index > 0;
        }
        
        @Override
        public Object previous() {
            if (this.index > 0) {
                final XSObject[] access$100 = XSObjectListImpl.this.fArray;
                final int index = this.index - 1;
                this.index = index;
                return access$100[index];
            }
            throw new NoSuchElementException();
        }
        
        @Override
        public int nextIndex() {
            return this.index;
        }
        
        @Override
        public int previousIndex() {
            return this.index - 1;
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
    }
}
