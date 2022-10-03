package org.apache.commons.collections.map;

import java.util.ListIterator;
import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.keyvalue.AbstractMapEntry;
import java.util.AbstractSet;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import java.util.AbstractList;
import org.apache.commons.collections.list.UnmodifiableList;
import java.util.Set;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.MapIterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.Serializable;
import org.apache.commons.collections.OrderedMap;

public class ListOrderedMap extends AbstractMapDecorator implements OrderedMap, Serializable
{
    private static final long serialVersionUID = 2728177751851003750L;
    protected final List insertOrder;
    
    public static OrderedMap decorate(final Map map) {
        return new ListOrderedMap(map);
    }
    
    public ListOrderedMap() {
        this(new HashMap());
    }
    
    protected ListOrderedMap(final Map map) {
        super(map);
        (this.insertOrder = new ArrayList()).addAll(this.getMap().keySet());
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.map);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.map = (Map)in.readObject();
    }
    
    public MapIterator mapIterator() {
        return this.orderedMapIterator();
    }
    
    public OrderedMapIterator orderedMapIterator() {
        return new ListOrderedMapIterator(this);
    }
    
    public Object firstKey() {
        if (this.size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(0);
    }
    
    public Object lastKey() {
        if (this.size() == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.insertOrder.get(this.size() - 1);
    }
    
    public Object nextKey(final Object key) {
        final int index = this.insertOrder.indexOf(key);
        if (index >= 0 && index < this.size() - 1) {
            return this.insertOrder.get(index + 1);
        }
        return null;
    }
    
    public Object previousKey(final Object key) {
        final int index = this.insertOrder.indexOf(key);
        if (index > 0) {
            return this.insertOrder.get(index - 1);
        }
        return null;
    }
    
    public Object put(final Object key, final Object value) {
        if (this.getMap().containsKey(key)) {
            return this.getMap().put(key, value);
        }
        final Object result = this.getMap().put(key, value);
        this.insertOrder.add(key);
        return result;
    }
    
    public void putAll(final Map map) {
        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Object remove(final Object key) {
        final Object result = this.getMap().remove(key);
        this.insertOrder.remove(key);
        return result;
    }
    
    public void clear() {
        this.getMap().clear();
        this.insertOrder.clear();
    }
    
    public Set keySet() {
        return new KeySetView(this);
    }
    
    public List keyList() {
        return UnmodifiableList.decorate(this.insertOrder);
    }
    
    public Collection values() {
        return new ValuesView(this);
    }
    
    public List valueList() {
        return new ValuesView(this);
    }
    
    public Set entrySet() {
        return new EntrySetView(this, this.insertOrder);
    }
    
    public String toString() {
        if (this.isEmpty()) {
            return "{}";
        }
        final StringBuffer buf = new StringBuffer();
        buf.append('{');
        boolean first = true;
        final Iterator it = this.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            if (first) {
                first = false;
            }
            else {
                buf.append(", ");
            }
            buf.append((key == this) ? "(this Map)" : key);
            buf.append('=');
            buf.append((value == this) ? "(this Map)" : value);
        }
        buf.append('}');
        return buf.toString();
    }
    
    public Object get(final int index) {
        return this.insertOrder.get(index);
    }
    
    public Object getValue(final int index) {
        return this.get(this.insertOrder.get(index));
    }
    
    public int indexOf(final Object key) {
        return this.insertOrder.indexOf(key);
    }
    
    public Object setValue(final int index, final Object value) {
        final Object key = this.insertOrder.get(index);
        return this.put(key, value);
    }
    
    public Object put(int index, final Object key, final Object value) {
        final Map m = this.getMap();
        if (m.containsKey(key)) {
            final Object result = m.remove(key);
            final int pos = this.insertOrder.indexOf(key);
            this.insertOrder.remove(pos);
            if (pos < index) {
                --index;
            }
            this.insertOrder.add(index, key);
            m.put(key, value);
            return result;
        }
        this.insertOrder.add(index, key);
        m.put(key, value);
        return null;
    }
    
    public Object remove(final int index) {
        return this.remove(this.get(index));
    }
    
    public List asList() {
        return this.keyList();
    }
    
    static class ValuesView extends AbstractList
    {
        private final ListOrderedMap parent;
        
        ValuesView(final ListOrderedMap parent) {
            this.parent = parent;
        }
        
        public int size() {
            return this.parent.size();
        }
        
        public boolean contains(final Object value) {
            return this.parent.containsValue(value);
        }
        
        public void clear() {
            this.parent.clear();
        }
        
        public Iterator iterator() {
            return new AbstractIteratorDecorator((Iterator)this.parent.entrySet().iterator()) {
                public Object next() {
                    return this.iterator.next().getValue();
                }
            };
        }
        
        public Object get(final int index) {
            return this.parent.getValue(index);
        }
        
        public Object set(final int index, final Object value) {
            return this.parent.setValue(index, value);
        }
        
        public Object remove(final int index) {
            return this.parent.remove(index);
        }
    }
    
    static class KeySetView extends AbstractSet
    {
        private final ListOrderedMap parent;
        
        KeySetView(final ListOrderedMap parent) {
            this.parent = parent;
        }
        
        public int size() {
            return this.parent.size();
        }
        
        public boolean contains(final Object value) {
            return this.parent.containsKey(value);
        }
        
        public void clear() {
            this.parent.clear();
        }
        
        public Iterator iterator() {
            return new AbstractIteratorDecorator((Iterator)this.parent.entrySet().iterator()) {
                public Object next() {
                    return ((Map.Entry)super.next()).getKey();
                }
            };
        }
    }
    
    static class EntrySetView extends AbstractSet
    {
        private final ListOrderedMap parent;
        private final List insertOrder;
        private Set entrySet;
        
        public EntrySetView(final ListOrderedMap parent, final List insertOrder) {
            this.parent = parent;
            this.insertOrder = insertOrder;
        }
        
        private Set getEntrySet() {
            if (this.entrySet == null) {
                this.entrySet = this.parent.getMap().entrySet();
            }
            return this.entrySet;
        }
        
        public int size() {
            return this.parent.size();
        }
        
        public boolean isEmpty() {
            return this.parent.isEmpty();
        }
        
        public boolean contains(final Object obj) {
            return this.getEntrySet().contains(obj);
        }
        
        public boolean containsAll(final Collection coll) {
            return this.getEntrySet().containsAll(coll);
        }
        
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (this.getEntrySet().contains(obj)) {
                final Object key = ((Map.Entry)obj).getKey();
                this.parent.remove(key);
                return true;
            }
            return false;
        }
        
        public void clear() {
            this.parent.clear();
        }
        
        public boolean equals(final Object obj) {
            return obj == this || this.getEntrySet().equals(obj);
        }
        
        public int hashCode() {
            return this.getEntrySet().hashCode();
        }
        
        public String toString() {
            return this.getEntrySet().toString();
        }
        
        public Iterator iterator() {
            return new ListOrderedIterator(this.parent, this.insertOrder);
        }
    }
    
    static class ListOrderedIterator extends AbstractIteratorDecorator
    {
        private final ListOrderedMap parent;
        private Object last;
        
        ListOrderedIterator(final ListOrderedMap parent, final List insertOrder) {
            super(insertOrder.iterator());
            this.last = null;
            this.parent = parent;
        }
        
        public Object next() {
            this.last = super.next();
            return new ListOrderedMapEntry(this.parent, this.last);
        }
        
        public void remove() {
            super.remove();
            this.parent.getMap().remove(this.last);
        }
    }
    
    static class ListOrderedMapEntry extends AbstractMapEntry
    {
        private final ListOrderedMap parent;
        
        ListOrderedMapEntry(final ListOrderedMap parent, final Object key) {
            super(key, null);
            this.parent = parent;
        }
        
        public Object getValue() {
            return this.parent.get(this.key);
        }
        
        public Object setValue(final Object value) {
            return this.parent.getMap().put(this.key, value);
        }
    }
    
    static class ListOrderedMapIterator implements OrderedMapIterator, ResettableIterator
    {
        private final ListOrderedMap parent;
        private ListIterator iterator;
        private Object last;
        private boolean readable;
        
        ListOrderedMapIterator(final ListOrderedMap parent) {
            this.last = null;
            this.readable = false;
            this.parent = parent;
            this.iterator = parent.insertOrder.listIterator();
        }
        
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        public Object next() {
            this.last = this.iterator.next();
            this.readable = true;
            return this.last;
        }
        
        public boolean hasPrevious() {
            return this.iterator.hasPrevious();
        }
        
        public Object previous() {
            this.last = this.iterator.previous();
            this.readable = true;
            return this.last;
        }
        
        public void remove() {
            if (!this.readable) {
                throw new IllegalStateException("remove() can only be called once after next()");
            }
            this.iterator.remove();
            this.parent.map.remove(this.last);
            this.readable = false;
        }
        
        public Object getKey() {
            if (!this.readable) {
                throw new IllegalStateException("getKey() can only be called after next() and before remove()");
            }
            return this.last;
        }
        
        public Object getValue() {
            if (!this.readable) {
                throw new IllegalStateException("getValue() can only be called after next() and before remove()");
            }
            return this.parent.get(this.last);
        }
        
        public Object setValue(final Object value) {
            if (!this.readable) {
                throw new IllegalStateException("setValue() can only be called after next() and before remove()");
            }
            return this.parent.map.put(this.last, value);
        }
        
        public void reset() {
            this.iterator = this.parent.insertOrder.listIterator();
            this.last = null;
            this.readable = false;
        }
        
        public String toString() {
            if (this.readable) {
                return "Iterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "Iterator[]";
        }
    }
}
