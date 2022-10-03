package org.apache.commons.collections;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import org.apache.commons.collections.list.UnmodifiableList;
import java.util.ArrayList;
import java.util.List;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.AbstractSet;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.io.Externalizable;
import java.util.Map;

public class SequencedHashMap implements Map, Cloneable, Externalizable
{
    private Entry sentinel;
    private HashMap entries;
    private transient long modCount;
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final int ENTRY = 2;
    private static final int REMOVED_MASK = Integer.MIN_VALUE;
    private static final long serialVersionUID = 3380552487888102930L;
    
    private static final Entry createSentinel() {
        final Entry s = new Entry(null, null);
        s.prev = s;
        return s.next = s;
    }
    
    public SequencedHashMap() {
        this.modCount = 0L;
        this.sentinel = createSentinel();
        this.entries = new HashMap();
    }
    
    public SequencedHashMap(final int initialSize) {
        this.modCount = 0L;
        this.sentinel = createSentinel();
        this.entries = new HashMap(initialSize);
    }
    
    public SequencedHashMap(final int initialSize, final float loadFactor) {
        this.modCount = 0L;
        this.sentinel = createSentinel();
        this.entries = new HashMap(initialSize, loadFactor);
    }
    
    public SequencedHashMap(final Map m) {
        this();
        this.putAll(m);
    }
    
    private void removeEntry(final Entry entry) {
        entry.next.prev = entry.prev;
        entry.prev.next = entry.next;
    }
    
    private void insertEntry(final Entry entry) {
        entry.next = this.sentinel;
        entry.prev = this.sentinel.prev;
        this.sentinel.prev.next = entry;
        this.sentinel.prev = entry;
    }
    
    public int size() {
        return this.entries.size();
    }
    
    public boolean isEmpty() {
        return this.sentinel.next == this.sentinel;
    }
    
    public boolean containsKey(final Object key) {
        return this.entries.containsKey(key);
    }
    
    public boolean containsValue(final Object value) {
        if (value == null) {
            for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
                if (pos.getValue() == null) {
                    return true;
                }
            }
        }
        else {
            for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
                if (value.equals(pos.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Object get(final Object o) {
        final Entry entry = this.entries.get(o);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }
    
    public Map.Entry getFirst() {
        return this.isEmpty() ? null : this.sentinel.next;
    }
    
    public Object getFirstKey() {
        return this.sentinel.next.getKey();
    }
    
    public Object getFirstValue() {
        return this.sentinel.next.getValue();
    }
    
    public Map.Entry getLast() {
        return this.isEmpty() ? null : this.sentinel.prev;
    }
    
    public Object getLastKey() {
        return this.sentinel.prev.getKey();
    }
    
    public Object getLastValue() {
        return this.sentinel.prev.getValue();
    }
    
    public Object put(final Object key, final Object value) {
        ++this.modCount;
        Object oldValue = null;
        Entry e = this.entries.get(key);
        if (e != null) {
            this.removeEntry(e);
            oldValue = e.setValue(value);
        }
        else {
            e = new Entry(key, value);
            this.entries.put(key, e);
        }
        this.insertEntry(e);
        return oldValue;
    }
    
    public Object remove(final Object key) {
        final Entry e = this.removeImpl(key);
        return (e == null) ? null : e.getValue();
    }
    
    private Entry removeImpl(final Object key) {
        final Entry e = this.entries.remove(key);
        if (e == null) {
            return null;
        }
        ++this.modCount;
        this.removeEntry(e);
        return e;
    }
    
    public void putAll(final Map t) {
        final Iterator iter = t.entrySet().iterator();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    public void clear() {
        ++this.modCount;
        this.entries.clear();
        this.sentinel.next = this.sentinel;
        this.sentinel.prev = this.sentinel;
    }
    
    public boolean equals(final Object obj) {
        return obj != null && (obj == this || (obj instanceof Map && this.entrySet().equals(((Map)obj).entrySet())));
    }
    
    public int hashCode() {
        return this.entrySet().hashCode();
    }
    
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
            buf.append(pos.getKey());
            buf.append('=');
            buf.append(pos.getValue());
            if (pos.next != this.sentinel) {
                buf.append(',');
            }
        }
        buf.append(']');
        return buf.toString();
    }
    
    public Set keySet() {
        return new AbstractSet() {
            public Iterator iterator() {
                return new OrderedIterator(0);
            }
            
            public boolean remove(final Object o) {
                final Entry e = SequencedHashMap.this.removeImpl(o);
                return e != null;
            }
            
            public void clear() {
                SequencedHashMap.this.clear();
            }
            
            public int size() {
                return SequencedHashMap.this.size();
            }
            
            public boolean isEmpty() {
                return SequencedHashMap.this.isEmpty();
            }
            
            public boolean contains(final Object o) {
                return SequencedHashMap.this.containsKey(o);
            }
        };
    }
    
    public Collection values() {
        return new AbstractCollection() {
            public Iterator iterator() {
                return new OrderedIterator(1);
            }
            
            public boolean remove(final Object value) {
                if (value == null) {
                    for (Entry pos = SequencedHashMap.this.sentinel.next; pos != SequencedHashMap.this.sentinel; pos = pos.next) {
                        if (pos.getValue() == null) {
                            SequencedHashMap.this.removeImpl(pos.getKey());
                            return true;
                        }
                    }
                }
                else {
                    for (Entry pos = SequencedHashMap.this.sentinel.next; pos != SequencedHashMap.this.sentinel; pos = pos.next) {
                        if (value.equals(pos.getValue())) {
                            SequencedHashMap.this.removeImpl(pos.getKey());
                            return true;
                        }
                    }
                }
                return false;
            }
            
            public void clear() {
                SequencedHashMap.this.clear();
            }
            
            public int size() {
                return SequencedHashMap.this.size();
            }
            
            public boolean isEmpty() {
                return SequencedHashMap.this.isEmpty();
            }
            
            public boolean contains(final Object o) {
                return SequencedHashMap.this.containsValue(o);
            }
        };
    }
    
    public Set entrySet() {
        return new AbstractSet() {
            private Entry findEntry(final Object o) {
                if (o == null) {
                    return null;
                }
                if (!(o instanceof Map.Entry)) {
                    return null;
                }
                final Map.Entry e = (Map.Entry)o;
                final Entry entry = SequencedHashMap.this.entries.get(e.getKey());
                if (entry != null && entry.equals(e)) {
                    return entry;
                }
                return null;
            }
            
            public Iterator iterator() {
                return new OrderedIterator(2);
            }
            
            public boolean remove(final Object o) {
                final Entry e = this.findEntry(o);
                return e != null && SequencedHashMap.this.removeImpl(e.getKey()) != null;
            }
            
            public void clear() {
                SequencedHashMap.this.clear();
            }
            
            public int size() {
                return SequencedHashMap.this.size();
            }
            
            public boolean isEmpty() {
                return SequencedHashMap.this.isEmpty();
            }
            
            public boolean contains(final Object o) {
                return this.findEntry(o) != null;
            }
        };
    }
    
    public Object clone() throws CloneNotSupportedException {
        final SequencedHashMap map = (SequencedHashMap)super.clone();
        map.sentinel = createSentinel();
        map.entries = new HashMap();
        map.putAll(this);
        return map;
    }
    
    private Map.Entry getEntry(final int index) {
        Entry pos = this.sentinel;
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index + " < 0");
        }
        int i;
        for (i = -1; i < index - 1 && pos.next != this.sentinel; ++i, pos = pos.next) {}
        if (pos.next == this.sentinel) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + (i + 1));
        }
        return pos.next;
    }
    
    public Object get(final int index) {
        return this.getEntry(index).getKey();
    }
    
    public Object getValue(final int index) {
        return this.getEntry(index).getValue();
    }
    
    public int indexOf(final Object key) {
        Entry e = this.entries.get(key);
        if (e == null) {
            return -1;
        }
        int pos = 0;
        while (e.prev != this.sentinel) {
            ++pos;
            e = e.prev;
        }
        return pos;
    }
    
    public Iterator iterator() {
        return this.keySet().iterator();
    }
    
    public int lastIndexOf(final Object key) {
        return this.indexOf(key);
    }
    
    public List sequence() {
        final List l = new ArrayList(this.size());
        final Iterator iter = this.keySet().iterator();
        while (iter.hasNext()) {
            l.add(iter.next());
        }
        return UnmodifiableList.decorate(l);
    }
    
    public Object remove(final int index) {
        return this.remove(this.get(index));
    }
    
    public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
        for (int size = in.readInt(), i = 0; i < size; ++i) {
            final Object key = in.readObject();
            final Object value = in.readObject();
            this.put(key, value);
        }
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        out.writeInt(this.size());
        for (Entry pos = this.sentinel.next; pos != this.sentinel; pos = pos.next) {
            out.writeObject(pos.getKey());
            out.writeObject(pos.getValue());
        }
    }
    
    private static class Entry implements Map.Entry, KeyValue
    {
        private final Object key;
        private Object value;
        Entry next;
        Entry prev;
        
        public Entry(final Object key, final Object value) {
            this.next = null;
            this.prev = null;
            this.key = key;
            this.value = value;
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public Object setValue(final Object value) {
            final Object oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        public int hashCode() {
            return ((this.getKey() == null) ? 0 : this.getKey().hashCode()) ^ ((this.getValue() == null) ? 0 : this.getValue().hashCode());
        }
        
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry other = (Map.Entry)obj;
            if (this.getKey() == null) {
                if (other.getKey() != null) {
                    return false;
                }
            }
            else if (!this.getKey().equals(other.getKey())) {
                return false;
            }
            if ((this.getValue() != null) ? this.getValue().equals(other.getValue()) : (other.getValue() == null)) {
                return true;
            }
            return false;
        }
        
        public String toString() {
            return "[" + this.getKey() + "=" + this.getValue() + "]";
        }
    }
    
    private class OrderedIterator implements Iterator
    {
        private int returnType;
        private Entry pos;
        private transient long expectedModCount;
        
        public OrderedIterator(final int returnType) {
            this.pos = SequencedHashMap.this.sentinel;
            this.expectedModCount = SequencedHashMap.this.modCount;
            this.returnType = (returnType | Integer.MIN_VALUE);
        }
        
        public boolean hasNext() {
            return this.pos.next != SequencedHashMap.this.sentinel;
        }
        
        public Object next() {
            if (SequencedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (this.pos.next == SequencedHashMap.this.sentinel) {
                throw new NoSuchElementException();
            }
            this.returnType &= Integer.MAX_VALUE;
            this.pos = this.pos.next;
            switch (this.returnType) {
                case 0: {
                    return this.pos.getKey();
                }
                case 1: {
                    return this.pos.getValue();
                }
                case 2: {
                    return this.pos;
                }
                default: {
                    throw new Error("bad iterator type: " + this.returnType);
                }
            }
        }
        
        public void remove() {
            if ((this.returnType & Integer.MIN_VALUE) != 0x0) {
                throw new IllegalStateException("remove() must follow next()");
            }
            if (SequencedHashMap.this.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
            SequencedHashMap.this.removeImpl(this.pos.getKey());
            ++this.expectedModCount;
            this.returnType |= Integer.MIN_VALUE;
        }
    }
}
