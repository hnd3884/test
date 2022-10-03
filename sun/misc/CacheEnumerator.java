package sun.misc;

import java.util.NoSuchElementException;
import java.util.Enumeration;

class CacheEnumerator implements Enumeration
{
    boolean keys;
    int index;
    CacheEntry[] table;
    CacheEntry entry;
    
    CacheEnumerator(final CacheEntry[] table, final boolean keys) {
        this.table = table;
        this.keys = keys;
        this.index = table.length;
    }
    
    @Override
    public boolean hasMoreElements() {
        while (this.index >= 0) {
            while (this.entry != null) {
                if (this.entry.check() != null) {
                    return true;
                }
                this.entry = this.entry.next;
            }
            int index;
            do {
                index = this.index - 1;
                this.index = index;
            } while (index >= 0 && (this.entry = this.table[this.index]) == null);
        }
        return false;
    }
    
    @Override
    public Object nextElement() {
        while (this.index >= 0) {
            if (this.entry == null) {
                int index;
                do {
                    index = this.index - 1;
                    this.index = index;
                } while (index >= 0 && (this.entry = this.table[this.index]) == null);
            }
            if (this.entry != null) {
                final CacheEntry entry = this.entry;
                this.entry = entry.next;
                if (entry.check() != null) {
                    return this.keys ? entry.key : entry.check();
                }
                continue;
            }
        }
        throw new NoSuchElementException("CacheEnumerator");
    }
}
