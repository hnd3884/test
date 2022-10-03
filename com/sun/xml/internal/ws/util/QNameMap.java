package com.sun.xml.internal.ws.util;

import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.HashSet;
import java.util.Collection;
import java.util.Map;
import com.sun.istack.internal.NotNull;
import javax.xml.namespace.QName;
import java.util.Iterator;
import java.util.Set;

public final class QNameMap<V>
{
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    transient Entry<V>[] table;
    transient int size;
    private int threshold;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Set<Entry<V>> entrySet;
    private transient Iterable<V> views;
    
    public QNameMap() {
        this.table = new Entry[16];
        this.entrySet = null;
        this.views = new Iterable<V>() {
            @Override
            public Iterator<V> iterator() {
                return new ValueIterator();
            }
        };
        this.threshold = 12;
        this.table = new Entry[16];
    }
    
    public void put(final String namespaceUri, final String localname, final V value) {
        assert localname != null;
        assert namespaceUri != null;
        final int hash = hash(localname);
        final int i = indexFor(hash, this.table.length);
        for (Entry<V> e = this.table[i]; e != null; e = e.next) {
            if (e.hash == hash && localname.equals(e.localName) && namespaceUri.equals(e.nsUri)) {
                e.value = value;
                return;
            }
        }
        this.addEntry(hash, namespaceUri, localname, value, i);
    }
    
    public void put(final QName name, final V value) {
        this.put(name.getNamespaceURI(), name.getLocalPart(), value);
    }
    
    public V get(@NotNull final String nsUri, final String localPart) {
        final Entry<V> e = this.getEntry(nsUri, localPart);
        if (e == null) {
            return null;
        }
        return e.value;
    }
    
    public V get(final QName name) {
        return this.get(name.getNamespaceURI(), name.getLocalPart());
    }
    
    public int size() {
        return this.size;
    }
    
    public QNameMap<V> putAll(final QNameMap<? extends V> map) {
        final int numKeysToBeAdded = map.size();
        if (numKeysToBeAdded == 0) {
            return this;
        }
        if (numKeysToBeAdded > this.threshold) {
            int targetCapacity = numKeysToBeAdded;
            if (targetCapacity > 1073741824) {
                targetCapacity = 1073741824;
            }
            int newCapacity;
            for (newCapacity = this.table.length; newCapacity < targetCapacity; newCapacity <<= 1) {}
            if (newCapacity > this.table.length) {
                this.resize(newCapacity);
            }
        }
        for (final Entry<? extends V> e : map.entrySet()) {
            this.put(e.nsUri, e.localName, e.getValue());
        }
        return this;
    }
    
    public QNameMap<V> putAll(final Map<QName, ? extends V> map) {
        for (final Map.Entry<QName, ? extends V> e : map.entrySet()) {
            final QName qn = e.getKey();
            this.put(qn.getNamespaceURI(), qn.getLocalPart(), e.getValue());
        }
        return this;
    }
    
    private static int hash(final String x) {
        int h = x.hashCode();
        h += ~(h << 9);
        h ^= h >>> 14;
        h += h << 4;
        h ^= h >>> 10;
        return h;
    }
    
    private static int indexFor(final int h, final int length) {
        return h & length - 1;
    }
    
    private void addEntry(final int hash, final String nsUri, final String localName, final V value, final int bucketIndex) {
        final Entry<V> e = this.table[bucketIndex];
        this.table[bucketIndex] = new Entry<V>(hash, nsUri, localName, value, e);
        if (this.size++ >= this.threshold) {
            this.resize(2 * this.table.length);
        }
    }
    
    private void resize(final int newCapacity) {
        final Entry[] oldTable = this.table;
        final int oldCapacity = oldTable.length;
        if (oldCapacity == 1073741824) {
            this.threshold = Integer.MAX_VALUE;
            return;
        }
        final Entry[] newTable = new Entry[newCapacity];
        this.transfer(newTable);
        this.table = newTable;
        this.threshold = newCapacity;
    }
    
    private void transfer(final Entry<V>[] newTable) {
        final Entry<V>[] src = this.table;
        final int newCapacity = newTable.length;
        for (int j = 0; j < src.length; ++j) {
            Entry<V> e = src[j];
            if (e != null) {
                src[j] = null;
                do {
                    final Entry<V> next = e.next;
                    final int i = indexFor(e.hash, newCapacity);
                    e.next = newTable[i];
                    newTable[i] = e;
                    e = next;
                } while (e != null);
            }
        }
    }
    
    public Entry<V> getOne() {
        for (final Entry<V> e : this.table) {
            if (e != null) {
                return e;
            }
        }
        return null;
    }
    
    public Collection<QName> keySet() {
        final Set<QName> r = new HashSet<QName>();
        for (final Entry<V> e : this.entrySet()) {
            r.add(e.createQName());
        }
        return r;
    }
    
    public Iterable<V> values() {
        return this.views;
    }
    
    public boolean containsKey(@NotNull final String nsUri, final String localName) {
        return this.getEntry(nsUri, localName) != null;
    }
    
    public boolean isEmpty() {
        return this.size == 0;
    }
    
    public Set<Entry<V>> entrySet() {
        final Set<Entry<V>> es = this.entrySet;
        return (es != null) ? es : (this.entrySet = new EntrySet());
    }
    
    private Iterator<Entry<V>> newEntryIterator() {
        return new EntryIterator();
    }
    
    private Entry<V> getEntry(@NotNull final String nsUri, final String localName) {
        final int hash = hash(localName);
        final int i = indexFor(hash, this.table.length);
        Entry<V> e;
        for (e = this.table[i]; e != null && (!localName.equals(e.localName) || !nsUri.equals(e.nsUri)); e = e.next) {}
        return e;
    }
    
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append('{');
        for (final Entry<V> e : this.entrySet()) {
            if (buf.length() > 1) {
                buf.append(',');
            }
            buf.append('[');
            buf.append(e);
            buf.append(']');
        }
        buf.append('}');
        return buf.toString();
    }
    
    private abstract class HashIterator<E> implements Iterator<E>
    {
        Entry<V> next;
        int index;
        
        HashIterator() {
            final Entry<V>[] t = QNameMap.this.table;
            int i = t.length;
            Entry<V> n = null;
            if (QNameMap.this.size != 0) {
                while (i > 0 && (n = t[--i]) == null) {}
            }
            this.next = n;
            this.index = i;
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null;
        }
        
        Entry<V> nextEntry() {
            final Entry<V> e = this.next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            Entry<V> n;
            Entry<V>[] t;
            int i;
            for (n = e.next, t = QNameMap.this.table, i = this.index; n == null && i > 0; n = t[--i]) {}
            this.index = i;
            this.next = n;
            return e;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
    private class ValueIterator extends HashIterator<V>
    {
        @Override
        public V next() {
            return this.nextEntry().value;
        }
    }
    
    public static final class Entry<V>
    {
        public final String nsUri;
        public final String localName;
        V value;
        final int hash;
        Entry<V> next;
        
        Entry(final int h, final String nsUri, final String localName, final V v, final Entry<V> n) {
            this.value = v;
            this.next = n;
            this.nsUri = nsUri;
            this.localName = localName;
            this.hash = h;
        }
        
        public QName createQName() {
            return new QName(this.nsUri, this.localName);
        }
        
        public V getValue() {
            return this.value;
        }
        
        public V setValue(final V newValue) {
            final V oldValue = this.value;
            this.value = newValue;
            return oldValue;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry e = (Entry)o;
            final String k1 = this.nsUri;
            final String k2 = e.nsUri;
            final String k3 = this.localName;
            final String k4 = e.localName;
            if (k1.equals(k2) && k3.equals(k4)) {
                final Object v1 = this.getValue();
                final Object v2 = e.getValue();
                if (v1 == v2 || (v1 != null && v1.equals(v2))) {
                    return true;
                }
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            return this.localName.hashCode() ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public String toString() {
            return '\"' + this.nsUri + "\",\"" + this.localName + "\"=" + this.getValue();
        }
    }
    
    private class EntryIterator extends HashIterator<Entry<V>>
    {
        @Override
        public Entry<V> next() {
            return this.nextEntry();
        }
    }
    
    private class EntrySet extends AbstractSet<Entry<V>>
    {
        @Override
        public Iterator<Entry<V>> iterator() {
            return QNameMap.this.newEntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry<V> e = (Entry<V>)o;
            final Entry<V> candidate = QNameMap.this.getEntry(e.nsUri, e.localName);
            return candidate != null && candidate.equals(e);
        }
        
        @Override
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public int size() {
            return QNameMap.this.size;
        }
    }
}
