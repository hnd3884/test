package java.beans;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;

abstract class WeakIdentityMap<T>
{
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final Object NULL;
    private final ReferenceQueue<Object> queue;
    private volatile Entry<T>[] table;
    private int threshold;
    private int size;
    
    WeakIdentityMap() {
        this.queue = new ReferenceQueue<Object>();
        this.table = this.newTable(8);
        this.threshold = 6;
        this.size = 0;
    }
    
    public T get(Object null) {
        this.removeStaleEntries();
        if (null == null) {
            null = WeakIdentityMap.NULL;
        }
        final int hashCode = null.hashCode();
        final Entry<T>[] table = this.table;
        for (WeakReference<Object> access$000 = table[getIndex(table, hashCode)]; access$000 != null; access$000 = ((Entry<Object>)access$000).next) {
            if (((Entry)access$000).isMatched(null, hashCode)) {
                return (T)((Entry<Object>)access$000).value;
            }
        }
        synchronized (WeakIdentityMap.NULL) {
            final int index = getIndex(this.table, hashCode);
            for (WeakReference<Object> access$2 = this.table[index]; access$2 != null; access$2 = ((Entry<Object>)access$2).next) {
                if (((Entry)access$2).isMatched(null, hashCode)) {
                    return (T)((Entry<Object>)access$2).value;
                }
            }
            final T create = this.create(null);
            this.table[index] = new Entry<T>(null, hashCode, create, this.queue, (Entry<Object>)this.table[index]);
            if (++this.size >= this.threshold) {
                if (this.table.length == 1073741824) {
                    this.threshold = Integer.MAX_VALUE;
                }
                else {
                    this.removeStaleEntries();
                    final Entry<T>[] table2 = this.newTable(this.table.length * 2);
                    this.transfer(this.table, table2);
                    if (this.size >= this.threshold / 2) {
                        this.table = table2;
                        this.threshold *= 2;
                    }
                    else {
                        this.transfer(table2, this.table);
                    }
                }
            }
            return create;
        }
    }
    
    protected abstract T create(final Object p0);
    
    private void removeStaleEntries() {
        Reference<?> reference = this.queue.poll();
        if (reference != null) {
            synchronized (WeakIdentityMap.NULL) {
                do {
                    final Entry entry = (Entry)reference;
                    final int index = getIndex(this.table, entry.hash);
                    Entry<T> entry3;
                    Entry<Object> access$000;
                    for (Entry<T> entry2 = entry3 = this.table[index]; entry3 != null; entry3 = (Entry<T>)access$000) {
                        access$000 = ((Entry<Object>)entry3).next;
                        if (entry3 == entry) {
                            if (entry2 == entry) {
                                this.table[index] = (Entry<T>)access$000;
                            }
                            else {
                                ((Entry<Object>)entry2).next = access$000;
                            }
                            entry.value = null;
                            entry.next = null;
                            --this.size;
                            break;
                        }
                        entry2 = entry3;
                    }
                    reference = this.queue.poll();
                } while (reference != null);
            }
        }
    }
    
    private void transfer(final Entry<T>[] array, final Entry<T>[] array2) {
        for (int i = 0; i < array.length; ++i) {
            Entry<T> entry = array[i];
            array[i] = null;
            while (entry != null) {
                final Entry<Object> access$000 = ((Entry<Object>)entry).next;
                if (entry.get() == null) {
                    ((Entry<Object>)entry).value = null;
                    ((Entry<Object>)entry).next = null;
                    --this.size;
                }
                else {
                    final int index = getIndex(array2, ((Entry<Object>)entry).hash);
                    ((Entry<Object>)entry).next = (Entry<Object>)array2[index];
                    array2[index] = entry;
                }
                entry = (Entry<T>)access$000;
            }
        }
    }
    
    private Entry<T>[] newTable(final int n) {
        return new Entry[n];
    }
    
    private static int getIndex(final Entry<?>[] array, final int n) {
        return n & array.length - 1;
    }
    
    static {
        NULL = new Object();
    }
    
    private static class Entry<T> extends WeakReference<Object>
    {
        private final int hash;
        private volatile T value;
        private volatile Entry<T> next;
        
        Entry(final Object o, final int hash, final T value, final ReferenceQueue<Object> referenceQueue, final Entry<T> next) {
            super(o, referenceQueue);
            this.hash = hash;
            this.value = value;
            this.next = next;
        }
        
        boolean isMatched(final Object o, final int n) {
            return this.hash == n && o == this.get();
        }
    }
}
