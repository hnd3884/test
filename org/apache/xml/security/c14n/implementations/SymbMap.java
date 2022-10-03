package org.apache.xml.security.c14n.implementations;

import java.util.ArrayList;
import java.util.List;

class SymbMap implements Cloneable
{
    int free;
    NameSpaceSymbEntry[] entries;
    String[] keys;
    
    SymbMap() {
        this.free = 23;
        this.entries = new NameSpaceSymbEntry[this.free];
        this.keys = new String[this.free];
    }
    
    void put(final String s, final NameSpaceSymbEntry nameSpaceSymbEntry) {
        final int index = this.index(s);
        final String s2 = this.keys[index];
        this.keys[index] = s;
        this.entries[index] = nameSpaceSymbEntry;
        if ((s2 == null || !s2.equals(s)) && --this.free == 0) {
            this.free = this.entries.length;
            this.rehash(this.free << 2);
        }
    }
    
    List entrySet() {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < this.entries.length; ++i) {
            if (this.entries[i] != null && !"".equals(this.entries[i].uri)) {
                list.add(this.entries[i]);
            }
        }
        return list;
    }
    
    protected int index(final Object o) {
        final String[] keys = this.keys;
        final int length = keys.length;
        int n = (o.hashCode() & Integer.MAX_VALUE) % length;
        final String s = keys[n];
        if (s == null || s.equals(o)) {
            return n;
        }
        final int n2 = length - 1;
        String s2;
        do {
            n = ((n == n2) ? 0 : (++n));
            s2 = keys[n];
        } while (s2 != null && !s2.equals(o));
        return n;
    }
    
    protected void rehash(final int n) {
        final int length = this.keys.length;
        final String[] keys = this.keys;
        final NameSpaceSymbEntry[] entries = this.entries;
        this.keys = new String[n];
        this.entries = new NameSpaceSymbEntry[n];
        int n2 = length;
        while (n2-- > 0) {
            if (keys[n2] != null) {
                final String s = keys[n2];
                final int index = this.index(s);
                this.keys[index] = s;
                this.entries[index] = entries[n2];
            }
        }
    }
    
    NameSpaceSymbEntry get(final String s) {
        return this.entries[this.index(s)];
    }
    
    protected Object clone() {
        try {
            final SymbMap symbMap = (SymbMap)super.clone();
            symbMap.entries = new NameSpaceSymbEntry[this.entries.length];
            System.arraycopy(this.entries, 0, symbMap.entries, 0, this.entries.length);
            symbMap.keys = new String[this.keys.length];
            System.arraycopy(this.keys, 0, symbMap.keys, 0, this.keys.length);
            return symbMap;
        }
        catch (final CloneNotSupportedException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
