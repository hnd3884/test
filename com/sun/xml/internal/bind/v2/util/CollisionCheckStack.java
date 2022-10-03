package com.sun.xml.internal.bind.v2.util;

import java.util.Arrays;
import java.util.AbstractList;

public final class CollisionCheckStack<E> extends AbstractList<E>
{
    private Object[] data;
    private int[] next;
    private int size;
    private boolean latestPushResult;
    private boolean useIdentity;
    private final int[] initialHash;
    
    public CollisionCheckStack() {
        this.size = 0;
        this.latestPushResult = false;
        this.useIdentity = true;
        this.initialHash = new int[17];
        this.data = new Object[16];
        this.next = new int[16];
    }
    
    public void setUseIdentity(final boolean useIdentity) {
        this.useIdentity = useIdentity;
    }
    
    public boolean getUseIdentity() {
        return this.useIdentity;
    }
    
    public boolean getLatestPushResult() {
        return this.latestPushResult;
    }
    
    public boolean push(final E o) {
        if (this.data.length == this.size) {
            this.expandCapacity();
        }
        this.data[this.size] = o;
        final int hash = this.hash(o);
        final boolean r = this.findDuplicate(o, hash);
        this.next[this.size] = this.initialHash[hash];
        this.initialHash[hash] = this.size + 1;
        ++this.size;
        return this.latestPushResult = r;
    }
    
    public void pushNocheck(final E o) {
        if (this.data.length == this.size) {
            this.expandCapacity();
        }
        this.data[this.size] = o;
        this.next[this.size] = -1;
        ++this.size;
    }
    
    public boolean findDuplicate(final E o) {
        final int hash = this.hash(o);
        return this.findDuplicate(o, hash);
    }
    
    @Override
    public E get(final int index) {
        return (E)this.data[index];
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    private int hash(final Object o) {
        return ((this.useIdentity ? System.identityHashCode(o) : o.hashCode()) & Integer.MAX_VALUE) % this.initialHash.length;
    }
    
    public E pop() {
        --this.size;
        final Object o = this.data[this.size];
        this.data[this.size] = null;
        final int n = this.next[this.size];
        if (n >= 0) {
            final int hash = this.hash(o);
            assert this.initialHash[hash] == this.size + 1;
            this.initialHash[hash] = n;
        }
        return (E)o;
    }
    
    public E peek() {
        return (E)this.data[this.size - 1];
    }
    
    private boolean findDuplicate(final E o, final int hash) {
        for (int p = this.initialHash[hash]; p != 0; p = this.next[p]) {
            --p;
            final Object existing = this.data[p];
            if (this.useIdentity) {
                if (existing == o) {
                    return true;
                }
            }
            else if (o.equals(existing)) {
                return true;
            }
        }
        return false;
    }
    
    private void expandCapacity() {
        final int oldSize = this.data.length;
        final int newSize = oldSize * 2;
        final Object[] d = new Object[newSize];
        final int[] n = new int[newSize];
        System.arraycopy(this.data, 0, d, 0, oldSize);
        System.arraycopy(this.next, 0, n, 0, oldSize);
        this.data = d;
        this.next = n;
    }
    
    public void reset() {
        if (this.size > 0) {
            this.size = 0;
            Arrays.fill(this.initialHash, 0);
        }
    }
    
    public String getCycleString() {
        final StringBuilder sb = new StringBuilder();
        int i = this.size() - 1;
        final E obj = this.get(i);
        sb.append(obj);
        Object x;
        do {
            sb.append(" -> ");
            x = this.get(--i);
            sb.append(x);
        } while (obj != x);
        return sb.toString();
    }
}
