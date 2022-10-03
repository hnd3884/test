package com.lowagie.text.pdf;

import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.Iterator;
import com.lowagie.text.error_messages.MessageLocalization;

public class IntHashtable implements Cloneable
{
    private transient Entry[] table;
    private transient int count;
    private int threshold;
    private float loadFactor;
    
    public IntHashtable() {
        this(150, 0.75f);
    }
    
    public IntHashtable(final int initialCapacity) {
        this(initialCapacity, 0.75f);
    }
    
    public IntHashtable(int initialCapacity, final float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.capacity.1", initialCapacity));
        }
        if (loadFactor <= 0.0f) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("illegal.load.1", String.valueOf(loadFactor)));
        }
        if (initialCapacity == 0) {
            initialCapacity = 1;
        }
        this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int)(initialCapacity * loadFactor);
    }
    
    public int size() {
        return this.count;
    }
    
    public boolean isEmpty() {
        return this.count == 0;
    }
    
    public boolean contains(final int value) {
        final Entry[] tab = this.table;
        int i = tab.length;
        while (i-- > 0) {
            for (Entry e = tab[i]; e != null; e = e.next) {
                if (e.value == value) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean containsValue(final int value) {
        return this.contains(value);
    }
    
    public boolean containsKey(final int key) {
        final Entry[] tab = this.table;
        final int hash = key;
        final int index = (hash & Integer.MAX_VALUE) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
                return true;
            }
        }
        return false;
    }
    
    public int get(final int key) {
        final Entry[] tab = this.table;
        final int hash = key;
        final int index = (hash & Integer.MAX_VALUE) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
                return e.value;
            }
        }
        return 0;
    }
    
    protected void rehash() {
        final int oldCapacity = this.table.length;
        final Entry[] oldMap = this.table;
        final int newCapacity = oldCapacity * 2 + 1;
        final Entry[] newMap = new Entry[newCapacity];
        this.threshold = (int)(newCapacity * this.loadFactor);
        this.table = newMap;
        int i = oldCapacity;
        while (i-- > 0) {
            Entry e;
            int index;
            for (Entry old = oldMap[i]; old != null; old = old.next, index = (e.hash & Integer.MAX_VALUE) % newCapacity, e.next = newMap[index], newMap[index] = e) {
                e = old;
            }
        }
    }
    
    public int put(final int key, final int value) {
        Entry[] tab = this.table;
        final int hash = key;
        int index = (hash & Integer.MAX_VALUE) % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (e.hash == hash && e.key == key) {
                final int old = e.value;
                e.value = value;
                return old;
            }
        }
        if (this.count >= this.threshold) {
            this.rehash();
            tab = this.table;
            index = (hash & Integer.MAX_VALUE) % tab.length;
        }
        Entry e = new Entry(hash, key, value, tab[index]);
        tab[index] = e;
        ++this.count;
        return 0;
    }
    
    public int remove(final int key) {
        final Entry[] tab = this.table;
        final int hash = key;
        final int index = (hash & Integer.MAX_VALUE) % tab.length;
        Entry e = tab[index];
        Entry prev = null;
        while (e != null) {
            if (e.hash == hash && e.key == key) {
                if (prev != null) {
                    prev.next = e.next;
                }
                else {
                    tab[index] = e.next;
                }
                --this.count;
                final int oldValue = e.value;
                e.value = 0;
                return oldValue;
            }
            prev = e;
            e = e.next;
        }
        return 0;
    }
    
    public void clear() {
        final Entry[] tab = this.table;
        int index = tab.length;
        while (--index >= 0) {
            tab[index] = null;
        }
        this.count = 0;
    }
    
    public Iterator getEntryIterator() {
        return new IntHashtableIterator(this.table);
    }
    
    public int[] toOrderedKeys() {
        final int[] res = this.getKeys();
        Arrays.sort(res);
        return res;
    }
    
    public int[] getKeys() {
        final int[] res = new int[this.count];
        int ptr = 0;
        int index = this.table.length;
        Entry entry = null;
        while (true) {
            if (entry == null) {
                while (index-- > 0 && (entry = this.table[index]) == null) {}
            }
            if (entry == null) {
                break;
            }
            final Entry e = entry;
            entry = e.next;
            res[ptr++] = e.key;
        }
        return res;
    }
    
    public int getOneKey() {
        if (this.count == 0) {
            return 0;
        }
        int index = this.table.length;
        Entry entry = null;
        while (index-- > 0 && (entry = this.table[index]) == null) {}
        if (entry == null) {
            return 0;
        }
        return entry.key;
    }
    
    public Object clone() {
        try {
            final IntHashtable t = (IntHashtable)super.clone();
            t.table = new Entry[this.table.length];
            int i = this.table.length;
            while (i-- > 0) {
                t.table[i] = ((this.table[i] != null) ? ((Entry)this.table[i].clone()) : null);
            }
            return t;
        }
        catch (final CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
    
    static class Entry
    {
        int hash;
        int key;
        int value;
        Entry next;
        
        protected Entry(final int hash, final int key, final int value, final Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
        
        public int getKey() {
            return this.key;
        }
        
        public int getValue() {
            return this.value;
        }
        
        @Override
        protected Object clone() {
            final Entry entry = new Entry(this.hash, this.key, this.value, (this.next != null) ? ((Entry)this.next.clone()) : null);
            return entry;
        }
    }
    
    static class IntHashtableIterator implements Iterator
    {
        int index;
        Entry[] table;
        Entry entry;
        
        IntHashtableIterator(final Entry[] table) {
            this.table = table;
            this.index = table.length;
        }
        
        @Override
        public boolean hasNext() {
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
        public Object next() {
            if (this.entry == null) {
                while (this.index-- > 0 && (this.entry = this.table[this.index]) == null) {}
            }
            if (this.entry != null) {
                final Entry e = this.entry;
                this.entry = e.next;
                return e;
            }
            throw new NoSuchElementException(MessageLocalization.getComposedMessage("inthashtableiterator"));
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("remove.not.supported"));
        }
    }
}
