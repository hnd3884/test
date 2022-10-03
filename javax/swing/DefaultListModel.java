package javax.swing;

import java.util.Enumeration;
import java.util.Vector;

public class DefaultListModel<E> extends AbstractListModel<E>
{
    private Vector<E> delegate;
    
    public DefaultListModel() {
        this.delegate = new Vector<E>();
    }
    
    @Override
    public int getSize() {
        return this.delegate.size();
    }
    
    @Override
    public E getElementAt(final int n) {
        return this.delegate.elementAt(n);
    }
    
    public void copyInto(final Object[] array) {
        this.delegate.copyInto(array);
    }
    
    public void trimToSize() {
        this.delegate.trimToSize();
    }
    
    public void ensureCapacity(final int n) {
        this.delegate.ensureCapacity(n);
    }
    
    public void setSize(final int size) {
        final int size2 = this.delegate.size();
        this.delegate.setSize(size);
        if (size2 > size) {
            this.fireIntervalRemoved(this, size, size2 - 1);
        }
        else if (size2 < size) {
            this.fireIntervalAdded(this, size2, size - 1);
        }
    }
    
    public int capacity() {
        return this.delegate.capacity();
    }
    
    public int size() {
        return this.delegate.size();
    }
    
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }
    
    public Enumeration<E> elements() {
        return this.delegate.elements();
    }
    
    public boolean contains(final Object o) {
        return this.delegate.contains(o);
    }
    
    public int indexOf(final Object o) {
        return this.delegate.indexOf(o);
    }
    
    public int indexOf(final Object o, final int n) {
        return this.delegate.indexOf(o, n);
    }
    
    public int lastIndexOf(final Object o) {
        return this.delegate.lastIndexOf(o);
    }
    
    public int lastIndexOf(final Object o, final int n) {
        return this.delegate.lastIndexOf(o, n);
    }
    
    public E elementAt(final int n) {
        return this.delegate.elementAt(n);
    }
    
    public E firstElement() {
        return this.delegate.firstElement();
    }
    
    public E lastElement() {
        return this.delegate.lastElement();
    }
    
    public void setElementAt(final E e, final int n) {
        this.delegate.setElementAt(e, n);
        this.fireContentsChanged(this, n, n);
    }
    
    public void removeElementAt(final int n) {
        this.delegate.removeElementAt(n);
        this.fireIntervalRemoved(this, n, n);
    }
    
    public void insertElementAt(final E e, final int n) {
        this.delegate.insertElementAt(e, n);
        this.fireIntervalAdded(this, n, n);
    }
    
    public void addElement(final E e) {
        final int size = this.delegate.size();
        this.delegate.addElement(e);
        this.fireIntervalAdded(this, size, size);
    }
    
    public boolean removeElement(final Object o) {
        final int index = this.indexOf(o);
        final boolean removeElement = this.delegate.removeElement(o);
        if (index >= 0) {
            this.fireIntervalRemoved(this, index, index);
        }
        return removeElement;
    }
    
    public void removeAllElements() {
        final int n = this.delegate.size() - 1;
        this.delegate.removeAllElements();
        if (n >= 0) {
            this.fireIntervalRemoved(this, 0, n);
        }
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
    
    public Object[] toArray() {
        final Object[] array = new Object[this.delegate.size()];
        this.delegate.copyInto(array);
        return array;
    }
    
    public E get(final int n) {
        return this.delegate.elementAt(n);
    }
    
    public E set(final int n, final E e) {
        final E element = this.delegate.elementAt(n);
        this.delegate.setElementAt(e, n);
        this.fireContentsChanged(this, n, n);
        return element;
    }
    
    public void add(final int n, final E e) {
        this.delegate.insertElementAt(e, n);
        this.fireIntervalAdded(this, n, n);
    }
    
    public E remove(final int n) {
        final E element = this.delegate.elementAt(n);
        this.delegate.removeElementAt(n);
        this.fireIntervalRemoved(this, n, n);
        return element;
    }
    
    public void clear() {
        final int n = this.delegate.size() - 1;
        this.delegate.removeAllElements();
        if (n >= 0) {
            this.fireIntervalRemoved(this, 0, n);
        }
    }
    
    public void removeRange(final int n, final int n2) {
        if (n > n2) {
            throw new IllegalArgumentException("fromIndex must be <= toIndex");
        }
        for (int i = n2; i >= n; --i) {
            this.delegate.removeElementAt(i);
        }
        this.fireIntervalRemoved(this, n, n2);
    }
}
