package sun.misc;

import java.util.Enumeration;
import java.util.Dictionary;

public class Cache extends Dictionary
{
    private CacheEntry[] table;
    private int count;
    private int threshold;
    private float loadFactor;
    
    private void init(final int n, final float loadFactor) {
        if (n <= 0 || loadFactor <= 0.0) {
            throw new IllegalArgumentException();
        }
        this.loadFactor = loadFactor;
        this.table = new CacheEntry[n];
        this.threshold = (int)(n * loadFactor);
    }
    
    public Cache(final int n, final float n2) {
        this.init(n, n2);
    }
    
    public Cache(final int n) {
        this.init(n, 0.75f);
    }
    
    public Cache() {
        try {
            this.init(101, 0.75f);
        }
        catch (final IllegalArgumentException ex) {
            throw new Error("panic");
        }
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
    public synchronized Enumeration keys() {
        return new CacheEnumerator(this.table, true);
    }
    
    @Override
    public synchronized Enumeration elements() {
        return new CacheEnumerator(this.table, false);
    }
    
    @Override
    public synchronized Object get(final Object o) {
        final CacheEntry[] table = this.table;
        final int hashCode = o.hashCode();
        for (CacheEntry next = table[(hashCode & Integer.MAX_VALUE) % table.length]; next != null; next = next.next) {
            if (next.hash == hashCode && next.key.equals(o)) {
                return next.check();
            }
        }
        return null;
    }
    
    protected void rehash() {
        final int length = this.table.length;
        final CacheEntry[] table = this.table;
        final int n = length * 2 + 1;
        final CacheEntry[] table2 = new CacheEntry[n];
        this.threshold = (int)(n * this.loadFactor);
        this.table = table2;
        int n2 = length;
        while (n2-- > 0) {
            CacheEntry next = table[n2];
            while (next != null) {
                final CacheEntry cacheEntry = next;
                next = next.next;
                if (cacheEntry.check() != null) {
                    final int n3 = (cacheEntry.hash & Integer.MAX_VALUE) % n;
                    cacheEntry.next = table2[n3];
                    table2[n3] = cacheEntry;
                }
                else {
                    --this.count;
                }
            }
        }
    }
    
    @Override
    public synchronized Object put(final Object key, final Object o) {
        if (o == null) {
            throw new NullPointerException();
        }
        final CacheEntry[] table = this.table;
        final int hashCode = key.hashCode();
        final int n = (hashCode & Integer.MAX_VALUE) % table.length;
        CacheEntry cacheEntry = null;
        for (CacheEntry next = table[n]; next != null; next = next.next) {
            if (next.hash == hashCode && next.key.equals(key)) {
                final Object check = next.check();
                next.setThing(o);
                return check;
            }
            if (next.check() == null) {
                cacheEntry = next;
            }
        }
        if (this.count >= this.threshold) {
            this.rehash();
            return this.put(key, o);
        }
        if (cacheEntry == null) {
            cacheEntry = new CacheEntry();
            cacheEntry.next = table[n];
            table[n] = cacheEntry;
            ++this.count;
        }
        cacheEntry.hash = hashCode;
        cacheEntry.key = key;
        cacheEntry.setThing(o);
        return null;
    }
    
    @Override
    public synchronized Object remove(final Object o) {
        final CacheEntry[] table = this.table;
        final int hashCode = o.hashCode();
        final int n = (hashCode & Integer.MAX_VALUE) % table.length;
        CacheEntry next = table[n];
        CacheEntry cacheEntry = null;
        while (next != null) {
            if (next.hash == hashCode && next.key.equals(o)) {
                if (cacheEntry != null) {
                    cacheEntry.next = next.next;
                }
                else {
                    table[n] = next.next;
                }
                --this.count;
                return next.check();
            }
            cacheEntry = next;
            next = next.next;
        }
        return null;
    }
}
