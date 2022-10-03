package org.apache.commons.collections.bidimap;

import org.apache.commons.collections.ResettableIterator;
import org.apache.commons.collections.keyvalue.AbstractMapEntryDecorator;
import org.apache.commons.collections.iterators.AbstractIteratorDecorator;
import org.apache.commons.collections.collection.AbstractCollectionDecorator;
import org.apache.commons.collections.MapIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import org.apache.commons.collections.BidiMap;

public abstract class AbstractDualBidiMap implements BidiMap
{
    protected final transient Map[] maps;
    protected transient BidiMap inverseBidiMap;
    protected transient Set keySet;
    protected transient Collection values;
    protected transient Set entrySet;
    
    protected AbstractDualBidiMap() {
        this.maps = new Map[2];
        this.inverseBidiMap = null;
        this.keySet = null;
        this.values = null;
        this.entrySet = null;
        this.maps[0] = this.createMap();
        this.maps[1] = this.createMap();
    }
    
    protected AbstractDualBidiMap(final Map normalMap, final Map reverseMap) {
        this.maps = new Map[2];
        this.inverseBidiMap = null;
        this.keySet = null;
        this.values = null;
        this.entrySet = null;
        this.maps[0] = normalMap;
        this.maps[1] = reverseMap;
    }
    
    protected AbstractDualBidiMap(final Map normalMap, final Map reverseMap, final BidiMap inverseBidiMap) {
        this.maps = new Map[2];
        this.inverseBidiMap = null;
        this.keySet = null;
        this.values = null;
        this.entrySet = null;
        this.maps[0] = normalMap;
        this.maps[1] = reverseMap;
        this.inverseBidiMap = inverseBidiMap;
    }
    
    protected Map createMap() {
        return null;
    }
    
    protected abstract BidiMap createBidiMap(final Map p0, final Map p1, final BidiMap p2);
    
    public Object get(final Object key) {
        return this.maps[0].get(key);
    }
    
    public int size() {
        return this.maps[0].size();
    }
    
    public boolean isEmpty() {
        return this.maps[0].isEmpty();
    }
    
    public boolean containsKey(final Object key) {
        return this.maps[0].containsKey(key);
    }
    
    public boolean equals(final Object obj) {
        return this.maps[0].equals(obj);
    }
    
    public int hashCode() {
        return this.maps[0].hashCode();
    }
    
    public String toString() {
        return this.maps[0].toString();
    }
    
    public Object put(final Object key, final Object value) {
        if (this.maps[0].containsKey(key)) {
            this.maps[1].remove(this.maps[0].get(key));
        }
        if (this.maps[1].containsKey(value)) {
            this.maps[0].remove(this.maps[1].get(value));
        }
        final Object obj = this.maps[0].put(key, value);
        this.maps[1].put(value, key);
        return obj;
    }
    
    public void putAll(final Map map) {
        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Object remove(final Object key) {
        Object value = null;
        if (this.maps[0].containsKey(key)) {
            value = this.maps[0].remove(key);
            this.maps[1].remove(value);
        }
        return value;
    }
    
    public void clear() {
        this.maps[0].clear();
        this.maps[1].clear();
    }
    
    public boolean containsValue(final Object value) {
        return this.maps[1].containsKey(value);
    }
    
    public MapIterator mapIterator() {
        return new BidiMapIterator(this);
    }
    
    public Object getKey(final Object value) {
        return this.maps[1].get(value);
    }
    
    public Object removeValue(final Object value) {
        Object key = null;
        if (this.maps[1].containsKey(value)) {
            key = this.maps[1].remove(value);
            this.maps[0].remove(key);
        }
        return key;
    }
    
    public BidiMap inverseBidiMap() {
        if (this.inverseBidiMap == null) {
            this.inverseBidiMap = this.createBidiMap(this.maps[1], this.maps[0], this);
        }
        return this.inverseBidiMap;
    }
    
    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet(this);
        }
        return this.keySet;
    }
    
    protected Iterator createKeySetIterator(final Iterator iterator) {
        return new KeySetIterator(iterator, this);
    }
    
    public Collection values() {
        if (this.values == null) {
            this.values = new Values(this);
        }
        return this.values;
    }
    
    protected Iterator createValuesIterator(final Iterator iterator) {
        return new ValuesIterator(iterator, this);
    }
    
    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet(this);
        }
        return this.entrySet;
    }
    
    protected Iterator createEntrySetIterator(final Iterator iterator) {
        return new EntrySetIterator(iterator, this);
    }
    
    protected abstract static class View extends AbstractCollectionDecorator
    {
        protected final AbstractDualBidiMap parent;
        
        protected View(final Collection coll, final AbstractDualBidiMap parent) {
            super(coll);
            this.parent = parent;
        }
        
        public boolean removeAll(final Collection coll) {
            if (this.parent.isEmpty() || coll.isEmpty()) {
                return false;
            }
            boolean modified = false;
            final Iterator it = this.iterator();
            while (it.hasNext()) {
                if (coll.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        public boolean retainAll(final Collection coll) {
            if (this.parent.isEmpty()) {
                return false;
            }
            if (coll.isEmpty()) {
                this.parent.clear();
                return true;
            }
            boolean modified = false;
            final Iterator it = this.iterator();
            while (it.hasNext()) {
                if (!coll.contains(it.next())) {
                    it.remove();
                    modified = true;
                }
            }
            return modified;
        }
        
        public void clear() {
            this.parent.clear();
        }
    }
    
    protected static class KeySet extends View implements Set
    {
        protected KeySet(final AbstractDualBidiMap parent) {
            super(parent.maps[0].keySet(), parent);
        }
        
        public Iterator iterator() {
            return this.parent.createKeySetIterator(super.iterator());
        }
        
        public boolean contains(final Object key) {
            return this.parent.maps[0].containsKey(key);
        }
        
        public boolean remove(final Object key) {
            if (this.parent.maps[0].containsKey(key)) {
                final Object value = this.parent.maps[0].remove(key);
                this.parent.maps[1].remove(value);
                return true;
            }
            return false;
        }
    }
    
    protected static class KeySetIterator extends AbstractIteratorDecorator
    {
        protected final AbstractDualBidiMap parent;
        protected Object lastKey;
        protected boolean canRemove;
        
        protected KeySetIterator(final Iterator iterator, final AbstractDualBidiMap parent) {
            super(iterator);
            this.lastKey = null;
            this.canRemove = false;
            this.parent = parent;
        }
        
        public Object next() {
            this.lastKey = super.next();
            this.canRemove = true;
            return this.lastKey;
        }
        
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            final Object value = this.parent.maps[0].get(this.lastKey);
            super.remove();
            this.parent.maps[1].remove(value);
            this.lastKey = null;
            this.canRemove = false;
        }
    }
    
    protected static class Values extends View implements Set
    {
        protected Values(final AbstractDualBidiMap parent) {
            super(parent.maps[0].values(), parent);
        }
        
        public Iterator iterator() {
            return this.parent.createValuesIterator(super.iterator());
        }
        
        public boolean contains(final Object value) {
            return this.parent.maps[1].containsKey(value);
        }
        
        public boolean remove(final Object value) {
            if (this.parent.maps[1].containsKey(value)) {
                final Object key = this.parent.maps[1].remove(value);
                this.parent.maps[0].remove(key);
                return true;
            }
            return false;
        }
    }
    
    protected static class ValuesIterator extends AbstractIteratorDecorator
    {
        protected final AbstractDualBidiMap parent;
        protected Object lastValue;
        protected boolean canRemove;
        
        protected ValuesIterator(final Iterator iterator, final AbstractDualBidiMap parent) {
            super(iterator);
            this.lastValue = null;
            this.canRemove = false;
            this.parent = parent;
        }
        
        public Object next() {
            this.lastValue = super.next();
            this.canRemove = true;
            return this.lastValue;
        }
        
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            super.remove();
            this.parent.maps[1].remove(this.lastValue);
            this.lastValue = null;
            this.canRemove = false;
        }
    }
    
    protected static class EntrySet extends View implements Set
    {
        protected EntrySet(final AbstractDualBidiMap parent) {
            super(parent.maps[0].entrySet(), parent);
        }
        
        public Iterator iterator() {
            return this.parent.createEntrySetIterator(super.iterator());
        }
        
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)obj;
            final Object key = entry.getKey();
            if (this.parent.containsKey(key)) {
                final Object value = this.parent.maps[0].get(key);
                if (value == null) {
                    if (entry.getValue() != null) {
                        return false;
                    }
                }
                else if (!value.equals(entry.getValue())) {
                    return false;
                }
                this.parent.maps[0].remove(key);
                this.parent.maps[1].remove(value);
                return true;
            }
            return false;
        }
    }
    
    protected static class EntrySetIterator extends AbstractIteratorDecorator
    {
        protected final AbstractDualBidiMap parent;
        protected Map.Entry last;
        protected boolean canRemove;
        
        protected EntrySetIterator(final Iterator iterator, final AbstractDualBidiMap parent) {
            super(iterator);
            this.last = null;
            this.canRemove = false;
            this.parent = parent;
        }
        
        public Object next() {
            this.last = new MapEntry((Map.Entry)super.next(), this.parent);
            this.canRemove = true;
            return this.last;
        }
        
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            final Object value = this.last.getValue();
            super.remove();
            this.parent.maps[1].remove(value);
            this.last = null;
            this.canRemove = false;
        }
    }
    
    protected static class MapEntry extends AbstractMapEntryDecorator
    {
        protected final AbstractDualBidiMap parent;
        
        protected MapEntry(final Map.Entry entry, final AbstractDualBidiMap parent) {
            super(entry);
            this.parent = parent;
        }
        
        public Object setValue(final Object value) {
            final Object key = this.getKey();
            if (this.parent.maps[1].containsKey(value) && this.parent.maps[1].get(value) != key) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            this.parent.put(key, value);
            final Object oldValue = super.setValue(value);
            return oldValue;
        }
    }
    
    protected static class BidiMapIterator implements MapIterator, ResettableIterator
    {
        protected final AbstractDualBidiMap parent;
        protected Iterator iterator;
        protected Map.Entry last;
        protected boolean canRemove;
        
        protected BidiMapIterator(final AbstractDualBidiMap parent) {
            this.last = null;
            this.canRemove = false;
            this.parent = parent;
            this.iterator = parent.maps[0].entrySet().iterator();
        }
        
        public boolean hasNext() {
            return this.iterator.hasNext();
        }
        
        public Object next() {
            this.last = this.iterator.next();
            this.canRemove = true;
            return this.last.getKey();
        }
        
        public void remove() {
            if (!this.canRemove) {
                throw new IllegalStateException("Iterator remove() can only be called once after next()");
            }
            final Object value = this.last.getValue();
            this.iterator.remove();
            this.parent.maps[1].remove(value);
            this.last = null;
            this.canRemove = false;
        }
        
        public Object getKey() {
            if (this.last == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return this.last.getKey();
        }
        
        public Object getValue() {
            if (this.last == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return this.last.getValue();
        }
        
        public Object setValue(final Object value) {
            if (this.last == null) {
                throw new IllegalStateException("Iterator setValue() can only be called after next() and before remove()");
            }
            if (this.parent.maps[1].containsKey(value) && this.parent.maps[1].get(value) != this.last.getKey()) {
                throw new IllegalArgumentException("Cannot use setValue() when the object being set is already in the map");
            }
            return this.parent.put(this.last.getKey(), value);
        }
        
        public void reset() {
            this.iterator = this.parent.maps[0].entrySet().iterator();
            this.last = null;
            this.canRemove = false;
        }
        
        public String toString() {
            if (this.last != null) {
                return "MapIterator[" + this.getKey() + "=" + this.getValue() + "]";
            }
            return "MapIterator[]";
        }
    }
}
