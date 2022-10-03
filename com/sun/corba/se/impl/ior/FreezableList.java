package com.sun.corba.se.impl.ior;

import java.util.Iterator;
import com.sun.corba.se.spi.ior.MakeImmutable;
import java.util.List;
import java.util.AbstractList;

public class FreezableList extends AbstractList
{
    private List delegate;
    private boolean immutable;
    
    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof FreezableList)) {
            return false;
        }
        final FreezableList list = (FreezableList)o;
        return this.delegate.equals(list.delegate) && this.immutable == list.immutable;
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
    
    public FreezableList(final List delegate, final boolean immutable) {
        this.delegate = null;
        this.immutable = false;
        this.delegate = delegate;
        this.immutable = immutable;
    }
    
    public FreezableList(final List list) {
        this(list, false);
    }
    
    public void makeImmutable() {
        this.immutable = true;
    }
    
    public boolean isImmutable() {
        return this.immutable;
    }
    
    public void makeElementsImmutable() {
        for (final MakeImmutable next : this) {
            if (next instanceof MakeImmutable) {
                next.makeImmutable();
            }
        }
    }
    
    @Override
    public int size() {
        return this.delegate.size();
    }
    
    @Override
    public Object get(final int n) {
        return this.delegate.get(n);
    }
    
    @Override
    public Object set(final int n, final Object o) {
        if (this.immutable) {
            throw new UnsupportedOperationException();
        }
        return this.delegate.set(n, o);
    }
    
    @Override
    public void add(final int n, final Object o) {
        if (this.immutable) {
            throw new UnsupportedOperationException();
        }
        this.delegate.add(n, o);
    }
    
    @Override
    public Object remove(final int n) {
        if (this.immutable) {
            throw new UnsupportedOperationException();
        }
        return this.delegate.remove(n);
    }
    
    @Override
    public List subList(final int n, final int n2) {
        return new FreezableList(this.delegate.subList(n, n2), this.immutable);
    }
}
