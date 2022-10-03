package sun.misc;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.AbstractSet;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Set;
import java.lang.ref.ReferenceQueue;
import java.util.Map;
import java.util.AbstractMap;

public class SoftCache extends AbstractMap implements Map
{
    private Map hash;
    private ReferenceQueue queue;
    private Set entrySet;
    
    private void processQueue() {
        ValueCell valueCell;
        while ((valueCell = (ValueCell)this.queue.poll()) != null) {
            if (valueCell.isValid()) {
                this.hash.remove(valueCell.key);
            }
            else {
                ValueCell.dropped--;
            }
        }
    }
    
    public SoftCache(final int n, final float n2) {
        this.queue = new ReferenceQueue();
        this.entrySet = null;
        this.hash = new HashMap(n, n2);
    }
    
    public SoftCache(final int n) {
        this.queue = new ReferenceQueue();
        this.entrySet = null;
        this.hash = new HashMap(n);
    }
    
    public SoftCache() {
        this.queue = new ReferenceQueue();
        this.entrySet = null;
        this.hash = new HashMap();
    }
    
    @Override
    public int size() {
        return this.entrySet().size();
    }
    
    @Override
    public boolean isEmpty() {
        return this.entrySet().isEmpty();
    }
    
    @Override
    public boolean containsKey(final Object o) {
        return strip(this.hash.get(o), false) != null;
    }
    
    protected Object fill(final Object o) {
        return null;
    }
    
    @Override
    public Object get(final Object o) {
        this.processQueue();
        Object o2 = this.hash.get(o);
        if (o2 == null) {
            o2 = this.fill(o);
            if (o2 != null) {
                this.hash.put(o, create(o, o2, this.queue));
                return o2;
            }
        }
        return strip(o2, false);
    }
    
    @Override
    public Object put(final Object o, final Object o2) {
        this.processQueue();
        return strip(this.hash.put(o, create(o, o2, this.queue)), true);
    }
    
    @Override
    public Object remove(final Object o) {
        this.processQueue();
        return strip(this.hash.remove(o), true);
    }
    
    @Override
    public void clear() {
        this.processQueue();
        this.hash.clear();
    }
    
    private static boolean valEquals(final Object o, final Object o2) {
        return (o == null) ? (o2 == null) : o.equals(o2);
    }
    
    @Override
    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }
    
    private static class ValueCell extends SoftReference
    {
        private static Object INVALID_KEY;
        private static int dropped;
        private Object key;
        
        private ValueCell(final Object key, final Object o, final ReferenceQueue referenceQueue) {
            super(o, referenceQueue);
            this.key = key;
        }
        
        private static ValueCell create(final Object o, final Object o2, final ReferenceQueue referenceQueue) {
            if (o2 == null) {
                return null;
            }
            return new ValueCell(o, o2, referenceQueue);
        }
        
        private static Object strip(final Object o, final boolean b) {
            if (o == null) {
                return null;
            }
            final ValueCell valueCell = (ValueCell)o;
            final Object value = valueCell.get();
            if (b) {
                valueCell.drop();
            }
            return value;
        }
        
        private boolean isValid() {
            return this.key != ValueCell.INVALID_KEY;
        }
        
        private void drop() {
            super.clear();
            this.key = ValueCell.INVALID_KEY;
            ++ValueCell.dropped;
        }
        
        static {
            ValueCell.INVALID_KEY = new Object();
            ValueCell.dropped = 0;
        }
    }
    
    private class Entry implements Map.Entry
    {
        private Map.Entry ent;
        private Object value;
        
        Entry(final Map.Entry ent, final Object value) {
            this.ent = ent;
            this.value = value;
        }
        
        @Override
        public Object getKey() {
            return this.ent.getKey();
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public Object setValue(final Object o) {
            return this.ent.setValue(create(this.ent.getKey(), o, SoftCache.this.queue));
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)o;
            return valEquals(this.ent.getKey(), entry.getKey()) && valEquals(this.value, entry.getValue());
        }
        
        @Override
        public int hashCode() {
            final Object key;
            return (((key = this.getKey()) == null) ? 0 : key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
    }
    
    private class EntrySet extends AbstractSet
    {
        Set hashEntries;
        
        private EntrySet() {
            this.hashEntries = SoftCache.this.hash.entrySet();
        }
        
        @Override
        public Iterator iterator() {
            return new Iterator() {
                Iterator hashIterator = EntrySet.this.hashEntries.iterator();
                Entry next = null;
                
                @Override
                public boolean hasNext() {
                    while (this.hashIterator.hasNext()) {
                        final Map.Entry entry = this.hashIterator.next();
                        final ValueCell valueCell = (ValueCell)entry.getValue();
                        Object value = null;
                        if (valueCell != null && (value = valueCell.get()) == null) {
                            continue;
                        }
                        this.next = new Entry(entry, value);
                        return true;
                    }
                    return false;
                }
                
                @Override
                public Object next() {
                    if (this.next == null && !this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    final Entry next = this.next;
                    this.next = null;
                    return next;
                }
                
                @Override
                public void remove() {
                    this.hashIterator.remove();
                }
            };
        }
        
        @Override
        public boolean isEmpty() {
            return !this.iterator().hasNext();
        }
        
        @Override
        public int size() {
            int n = 0;
            final Iterator iterator = this.iterator();
            while (iterator.hasNext()) {
                ++n;
                iterator.next();
            }
            return n;
        }
        
        @Override
        public boolean remove(final Object o) {
            SoftCache.this.processQueue();
            return o instanceof Entry && this.hashEntries.remove(((Entry)o).ent);
        }
    }
}
