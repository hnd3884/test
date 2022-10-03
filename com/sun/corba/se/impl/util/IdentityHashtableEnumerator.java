package com.sun.corba.se.impl.util;

import java.util.NoSuchElementException;
import java.util.Enumeration;

class IdentityHashtableEnumerator implements Enumeration
{
    boolean keys;
    int index;
    IdentityHashtableEntry[] table;
    IdentityHashtableEntry entry;
    
    IdentityHashtableEnumerator(final IdentityHashtableEntry[] table, final boolean keys) {
        this.table = table;
        this.keys = keys;
        this.index = table.length;
    }
    
    @Override
    public boolean hasMoreElements() {
        if (this.entry != null) {
            return true;
        }
        while (this.index-- > 0) {
            if ((this.entry = this.table[this.index]) != null) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Object nextElement() {
        if (this.entry == null) {
            while (this.index-- > 0 && (this.entry = this.table[this.index]) == null) {}
        }
        if (this.entry != null) {
            final IdentityHashtableEntry entry = this.entry;
            this.entry = entry.next;
            return this.keys ? entry.key : entry.value;
        }
        throw new NoSuchElementException("IdentityHashtableEnumerator");
    }
}
