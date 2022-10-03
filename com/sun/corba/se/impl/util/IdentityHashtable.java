package com.sun.corba.se.impl.util;

import java.util.Enumeration;
import java.util.Dictionary;

public final class IdentityHashtable extends Dictionary
{
    private transient IdentityHashtableEntry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;
    
    public IdentityHashtable(final int n, final float loadFactor) {
        if (n <= 0 || loadFactor <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.loadFactor = loadFactor;
        this.table = new IdentityHashtableEntry[n];
        this.threshold = (int)(n * loadFactor);
    }
    
    public IdentityHashtable(final int n) {
        this(n, 0.75f);
    }
    
    public IdentityHashtable() {
        this(101, 0.75f);
    }
    
    @Override
    public int size() {
        return this.count;
    }
    
    @Override
    public boolean isEmpty() {
        return this.count == 0;
    }
    
    @Override
    public Enumeration keys() {
        return new IdentityHashtableEnumerator(this.table, true);
    }
    
    @Override
    public Enumeration elements() {
        return new IdentityHashtableEnumerator(this.table, false);
    }
    
    public boolean contains(final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        final IdentityHashtableEntry[] table = this.table;
        int length = table.length;
        while (length-- > 0) {
            for (IdentityHashtableEntry next = table[length]; next != null; next = next.next) {
                if (next.value == o) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean containsKey(final Object o) {
        final IdentityHashtableEntry[] table = this.table;
        final int identityHashCode = System.identityHashCode(o);
        for (IdentityHashtableEntry next = table[(identityHashCode & Integer.MAX_VALUE) % table.length]; next != null; next = next.next) {
            if (next.hash == identityHashCode && next.key == o) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object get(final Object o) {
        final IdentityHashtableEntry[] table = this.table;
        final int identityHashCode = System.identityHashCode(o);
        for (IdentityHashtableEntry next = table[(identityHashCode & Integer.MAX_VALUE) % table.length]; next != null; next = next.next) {
            if (next.hash == identityHashCode && next.key == o) {
                return next.value;
            }
        }
        return null;
    }
    
    protected void rehash() {
        final int length = this.table.length;
        final IdentityHashtableEntry[] table = this.table;
        final int n = length * 2 + 1;
        final IdentityHashtableEntry[] table2 = new IdentityHashtableEntry[n];
        this.threshold = (int)(n * this.loadFactor);
        this.table = table2;
        int n2 = length;
        while (n2-- > 0) {
            IdentityHashtableEntry identityHashtableEntry;
            int n3;
            for (IdentityHashtableEntry next = table[n2]; next != null; next = next.next, n3 = (identityHashtableEntry.hash & Integer.MAX_VALUE) % n, identityHashtableEntry.next = table2[n3], table2[n3] = identityHashtableEntry) {
                identityHashtableEntry = next;
            }
        }
    }
    
    @Override
    public Object put(final Object key, final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        final IdentityHashtableEntry[] table = this.table;
        final int identityHashCode = System.identityHashCode(key);
        final int n = (identityHashCode & Integer.MAX_VALUE) % table.length;
        for (IdentityHashtableEntry next = table[n]; next != null; next = next.next) {
            if (next.hash == identityHashCode && next.key == key) {
                final Object value = next.value;
                next.value = o;
                return value;
            }
        }
        if (this.count >= this.threshold) {
            this.rehash();
            return this.put(key, o);
        }
        final IdentityHashtableEntry identityHashtableEntry = new IdentityHashtableEntry();
        identityHashtableEntry.hash = identityHashCode;
        identityHashtableEntry.key = key;
        identityHashtableEntry.value = o;
        identityHashtableEntry.next = table[n];
        table[n] = identityHashtableEntry;
        ++this.count;
        return null;
    }
    
    @Override
    public Object remove(final Object o) {
        final IdentityHashtableEntry[] table = this.table;
        final int identityHashCode = System.identityHashCode(o);
        final int n = (identityHashCode & Integer.MAX_VALUE) % table.length;
        IdentityHashtableEntry next = table[n];
        IdentityHashtableEntry identityHashtableEntry = null;
        while (next != null) {
            if (next.hash == identityHashCode && next.key == o) {
                if (identityHashtableEntry != null) {
                    identityHashtableEntry.next = next.next;
                }
                else {
                    table[n] = next.next;
                }
                --this.count;
                return next.value;
            }
            identityHashtableEntry = next;
            next = next.next;
        }
        return null;
    }
    
    public void clear() {
        final IdentityHashtableEntry[] table = this.table;
        int length = table.length;
        while (--length >= 0) {
            table[length] = null;
        }
        this.count = 0;
    }
    
    @Override
    public String toString() {
        final int n = this.size() - 1;
        final StringBuffer sb = new StringBuffer();
        final Enumeration keys = this.keys();
        final Enumeration elements = this.elements();
        sb.append("{");
        for (int i = 0; i <= n; ++i) {
            sb.append(keys.nextElement().toString() + "=" + elements.nextElement().toString());
            if (i < n) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
