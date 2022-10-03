package com.sun.java.util.jar.pack;

import java.util.ListIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

final class FixedList<E> implements List<E>
{
    private final ArrayList<E> flist;
    
    protected FixedList(final int n) {
        this.flist = new ArrayList<E>(n);
        for (int i = 0; i < n; ++i) {
            this.flist.add(null);
        }
    }
    
    @Override
    public int size() {
        return this.flist.size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.flist.isEmpty();
    }
    
    @Override
    public boolean contains(final Object o) {
        return this.flist.contains(o);
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.flist.iterator();
    }
    
    @Override
    public Object[] toArray() {
        return this.flist.toArray();
    }
    
    @Override
    public <T> T[] toArray(final T[] array) {
        return this.flist.toArray(array);
    }
    
    @Override
    public boolean add(final E e) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public boolean remove(final Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public boolean containsAll(final Collection<?> collection) {
        return this.flist.containsAll(collection);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public boolean addAll(final int n, final Collection<? extends E> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public boolean removeAll(final Collection<?> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public boolean retainAll(final Collection<?> collection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public void clear() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public E get(final int n) {
        return this.flist.get(n);
    }
    
    @Override
    public E set(final int n, final E e) {
        return this.flist.set(n, e);
    }
    
    @Override
    public void add(final int n, final E e) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public E remove(final int n) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("operation not permitted");
    }
    
    @Override
    public int indexOf(final Object o) {
        return this.flist.indexOf(o);
    }
    
    @Override
    public int lastIndexOf(final Object o) {
        return this.flist.lastIndexOf(o);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.flist.listIterator();
    }
    
    @Override
    public ListIterator<E> listIterator(final int n) {
        return this.flist.listIterator(n);
    }
    
    @Override
    public List<E> subList(final int n, final int n2) {
        return this.flist.subList(n, n2);
    }
    
    @Override
    public String toString() {
        return "FixedList{plist=" + this.flist + '}';
    }
}
