package org.apache.commons.collections4.trie;

import java.util.Collections;
import java.util.AbstractMap;
import java.util.ConcurrentModificationException;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import org.apache.commons.collections4.MapIterator;
import java.util.Iterator;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.SortedMap;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.NoSuchElementException;
import java.util.Comparator;
import java.util.Map;
import java.util.Collection;
import java.util.Set;

abstract class AbstractPatriciaTrie<K, V> extends AbstractBitwiseTrie<K, V>
{
    private static final long serialVersionUID = 5155253417231339498L;
    private transient TrieEntry<K, V> root;
    private transient volatile Set<K> keySet;
    private transient volatile Collection<V> values;
    private transient volatile Set<Map.Entry<K, V>> entrySet;
    private transient int size;
    protected transient int modCount;
    
    protected AbstractPatriciaTrie(final KeyAnalyzer<? super K> keyAnalyzer) {
        super(keyAnalyzer);
        this.root = new TrieEntry<K, V>(null, null, -1);
        this.size = 0;
        this.modCount = 0;
    }
    
    protected AbstractPatriciaTrie(final KeyAnalyzer<? super K> keyAnalyzer, final Map<? extends K, ? extends V> map) {
        super(keyAnalyzer);
        this.root = new TrieEntry<K, V>(null, null, -1);
        this.size = 0;
        this.modCount = 0;
        this.putAll(map);
    }
    
    @Override
    public void clear() {
        this.root.key = null;
        this.root.bitIndex = -1;
        this.root.value = null;
        this.root.parent = null;
        this.root.left = this.root;
        this.root.right = null;
        this.root.predecessor = this.root;
        this.size = 0;
        this.incrementModCount();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    void incrementSize() {
        ++this.size;
        this.incrementModCount();
    }
    
    void decrementSize() {
        --this.size;
        this.incrementModCount();
    }
    
    private void incrementModCount() {
        ++this.modCount;
    }
    
    @Override
    public V put(final K key, final V value) {
        if (key == null) {
            throw new NullPointerException("Key cannot be null");
        }
        final int lengthInBits = this.lengthInBits(key);
        if (lengthInBits == 0) {
            if (this.root.isEmpty()) {
                this.incrementSize();
            }
            else {
                this.incrementModCount();
            }
            return this.root.setKeyValue(key, value);
        }
        final TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
        if (this.compareKeys(key, found.key)) {
            if (found.isEmpty()) {
                this.incrementSize();
            }
            else {
                this.incrementModCount();
            }
            return found.setKeyValue(key, value);
        }
        final int bitIndex = this.bitIndex(key, found.key);
        if (!KeyAnalyzer.isOutOfBoundsIndex(bitIndex)) {
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
                final TrieEntry<K, V> t = new TrieEntry<K, V>(key, value, bitIndex);
                this.addEntry(t, lengthInBits);
                this.incrementSize();
                return null;
            }
            if (KeyAnalyzer.isNullBitKey(bitIndex)) {
                if (this.root.isEmpty()) {
                    this.incrementSize();
                }
                else {
                    this.incrementModCount();
                }
                return this.root.setKeyValue(key, value);
            }
            if (KeyAnalyzer.isEqualBitKey(bitIndex) && found != this.root) {
                this.incrementModCount();
                return found.setKeyValue(key, value);
            }
        }
        throw new IllegalArgumentException("Failed to put: " + key + " -> " + value + ", " + bitIndex);
    }
    
    TrieEntry<K, V> addEntry(final TrieEntry<K, V> entry, final int lengthInBits) {
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> path = this.root;
        while (current.bitIndex < entry.bitIndex && current.bitIndex > path.bitIndex) {
            path = current;
            if (!this.isBitSet(entry.key, current.bitIndex, lengthInBits)) {
                current = current.left;
            }
            else {
                current = current.right;
            }
        }
        entry.predecessor = entry;
        if (!this.isBitSet(entry.key, entry.bitIndex, lengthInBits)) {
            entry.left = entry;
            entry.right = current;
        }
        else {
            entry.left = current;
            entry.right = entry;
        }
        entry.parent = path;
        if (current.bitIndex >= entry.bitIndex) {
            current.parent = entry;
        }
        if (current.bitIndex <= path.bitIndex) {
            current.predecessor = entry;
        }
        if (path == this.root || !this.isBitSet(entry.key, path.bitIndex, lengthInBits)) {
            path.left = entry;
        }
        else {
            path.right = entry;
        }
        return entry;
    }
    
    @Override
    public V get(final Object k) {
        final TrieEntry<K, V> entry = this.getEntry(k);
        return (entry != null) ? entry.getValue() : null;
    }
    
    TrieEntry<K, V> getEntry(final Object k) {
        final K key = this.castKey(k);
        if (key == null) {
            return null;
        }
        final int lengthInBits = this.lengthInBits(key);
        final TrieEntry<K, V> entry = this.getNearestEntryForKey(key, lengthInBits);
        return (!entry.isEmpty() && this.compareKeys(key, entry.key)) ? entry : null;
    }
    
    public Map.Entry<K, V> select(final K key) {
        final int lengthInBits = this.lengthInBits(key);
        final Reference<Map.Entry<K, V>> reference = new Reference<Map.Entry<K, V>>();
        if (!this.selectR(this.root.left, -1, key, lengthInBits, reference)) {
            return reference.get();
        }
        return null;
    }
    
    public K selectKey(final K key) {
        final Map.Entry<K, V> entry = this.select(key);
        if (entry == null) {
            return null;
        }
        return entry.getKey();
    }
    
    public V selectValue(final K key) {
        final Map.Entry<K, V> entry = this.select(key);
        if (entry == null) {
            return null;
        }
        return entry.getValue();
    }
    
    private boolean selectR(final TrieEntry<K, V> h, final int bitIndex, final K key, final int lengthInBits, final Reference<Map.Entry<K, V>> reference) {
        if (h.bitIndex > bitIndex) {
            if (!this.isBitSet(key, h.bitIndex, lengthInBits)) {
                if (this.selectR(h.left, h.bitIndex, key, lengthInBits, reference)) {
                    return this.selectR(h.right, h.bitIndex, key, lengthInBits, reference);
                }
            }
            else if (this.selectR(h.right, h.bitIndex, key, lengthInBits, reference)) {
                return this.selectR(h.left, h.bitIndex, key, lengthInBits, reference);
            }
            return false;
        }
        if (!h.isEmpty()) {
            reference.set(h);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean containsKey(final Object k) {
        if (k == null) {
            return false;
        }
        final K key = this.castKey(k);
        final int lengthInBits = this.lengthInBits(key);
        final TrieEntry<K, V> entry = this.getNearestEntryForKey(key, lengthInBits);
        return !entry.isEmpty() && this.compareKeys(key, entry.key);
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntrySet();
        }
        return this.entrySet;
    }
    
    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new KeySet();
        }
        return this.keySet;
    }
    
    @Override
    public Collection<V> values() {
        if (this.values == null) {
            this.values = new Values();
        }
        return this.values;
    }
    
    @Override
    public V remove(final Object k) {
        if (k == null) {
            return null;
        }
        final K key = this.castKey(k);
        final int lengthInBits = this.lengthInBits(key);
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> path = this.root;
        while (current.bitIndex > path.bitIndex) {
            path = current;
            if (!this.isBitSet(key, current.bitIndex, lengthInBits)) {
                current = current.left;
            }
            else {
                current = current.right;
            }
        }
        if (!current.isEmpty() && this.compareKeys(key, current.key)) {
            return this.removeEntry(current);
        }
        return null;
    }
    
    TrieEntry<K, V> getNearestEntryForKey(final K key, final int lengthInBits) {
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> path = this.root;
        while (current.bitIndex > path.bitIndex) {
            path = current;
            if (!this.isBitSet(key, current.bitIndex, lengthInBits)) {
                current = current.left;
            }
            else {
                current = current.right;
            }
        }
        return current;
    }
    
    V removeEntry(final TrieEntry<K, V> h) {
        if (h != this.root) {
            if (h.isInternalNode()) {
                this.removeInternalEntry(h);
            }
            else {
                this.removeExternalEntry(h);
            }
        }
        this.decrementSize();
        return h.setKeyValue(null, null);
    }
    
    private void removeExternalEntry(final TrieEntry<K, V> h) {
        if (h == this.root) {
            throw new IllegalArgumentException("Cannot delete root Entry!");
        }
        if (!h.isExternalNode()) {
            throw new IllegalArgumentException(h + " is not an external Entry!");
        }
        final TrieEntry<K, V> parent = h.parent;
        final TrieEntry<K, V> child = (h.left == h) ? h.right : h.left;
        if (parent.left == h) {
            parent.left = child;
        }
        else {
            parent.right = child;
        }
        if (child.bitIndex > parent.bitIndex) {
            child.parent = parent;
        }
        else {
            child.predecessor = parent;
        }
    }
    
    private void removeInternalEntry(final TrieEntry<K, V> h) {
        if (h == this.root) {
            throw new IllegalArgumentException("Cannot delete root Entry!");
        }
        if (!h.isInternalNode()) {
            throw new IllegalArgumentException(h + " is not an internal Entry!");
        }
        final TrieEntry<K, V> p = h.predecessor;
        p.bitIndex = h.bitIndex;
        final TrieEntry<K, V> parent = p.parent;
        final TrieEntry<K, V> child = (p.left == h) ? p.right : p.left;
        if (p.predecessor == p && p.parent != h) {
            p.predecessor = p.parent;
        }
        if (parent.left == p) {
            parent.left = child;
        }
        else {
            parent.right = child;
        }
        if (child.bitIndex > parent.bitIndex) {
            child.parent = parent;
        }
        if (h.left.parent == h) {
            h.left.parent = p;
        }
        if (h.right.parent == h) {
            h.right.parent = p;
        }
        if (h.parent.left == h) {
            h.parent.left = p;
        }
        else {
            h.parent.right = p;
        }
        p.parent = h.parent;
        p.left = h.left;
        p.right = h.right;
        if (isValidUplink(p.left, p)) {
            p.left.predecessor = p;
        }
        if (isValidUplink(p.right, p)) {
            p.right.predecessor = p;
        }
    }
    
    TrieEntry<K, V> nextEntry(final TrieEntry<K, V> node) {
        if (node == null) {
            return this.firstEntry();
        }
        return this.nextEntryImpl(node.predecessor, node, null);
    }
    
    TrieEntry<K, V> nextEntryImpl(final TrieEntry<K, V> start, final TrieEntry<K, V> previous, final TrieEntry<K, V> tree) {
        TrieEntry<K, V> current = start;
        if (previous == null || start != previous.predecessor) {
            while (!current.left.isEmpty()) {
                if (previous == current.left) {
                    break;
                }
                if (isValidUplink(current.left, current)) {
                    return current.left;
                }
                current = current.left;
            }
        }
        if (current.isEmpty()) {
            return null;
        }
        if (current.right == null) {
            return null;
        }
        if (previous != current.right) {
            if (isValidUplink(current.right, current)) {
                return current.right;
            }
            return this.nextEntryImpl(current.right, previous, tree);
        }
        else {
            while (current == current.parent.right) {
                if (current == tree) {
                    return null;
                }
                current = current.parent;
            }
            if (current == tree) {
                return null;
            }
            if (current.parent.right == null) {
                return null;
            }
            if (previous != current.parent.right && isValidUplink(current.parent.right, current.parent)) {
                return current.parent.right;
            }
            if (current.parent.right == current.parent) {
                return null;
            }
            return this.nextEntryImpl(current.parent.right, previous, tree);
        }
    }
    
    TrieEntry<K, V> firstEntry() {
        if (this.isEmpty()) {
            return null;
        }
        return this.followLeft(this.root);
    }
    
    TrieEntry<K, V> followLeft(TrieEntry<K, V> node) {
        TrieEntry<K, V> child;
        while (true) {
            child = node.left;
            if (child.isEmpty()) {
                child = node.right;
            }
            if (child.bitIndex <= node.bitIndex) {
                break;
            }
            node = child;
        }
        return child;
    }
    
    @Override
    public Comparator<? super K> comparator() {
        return this.getKeyAnalyzer();
    }
    
    @Override
    public K firstKey() {
        if (this.size() == 0) {
            throw new NoSuchElementException();
        }
        return this.firstEntry().getKey();
    }
    
    @Override
    public K lastKey() {
        final TrieEntry<K, V> entry = this.lastEntry();
        if (entry != null) {
            return entry.getKey();
        }
        throw new NoSuchElementException();
    }
    
    @Override
    public K nextKey(final K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        final TrieEntry<K, V> entry = this.getEntry(key);
        if (entry != null) {
            final TrieEntry<K, V> nextEntry = this.nextEntry(entry);
            return (nextEntry != null) ? nextEntry.getKey() : null;
        }
        return null;
    }
    
    @Override
    public K previousKey(final K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        final TrieEntry<K, V> entry = this.getEntry(key);
        if (entry != null) {
            final TrieEntry<K, V> prevEntry = this.previousEntry(entry);
            return (prevEntry != null) ? prevEntry.getKey() : null;
        }
        return null;
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        return new TrieMapIterator();
    }
    
    @Override
    public SortedMap<K, V> prefixMap(final K key) {
        return this.getPrefixMapByBits(key, 0, this.lengthInBits(key));
    }
    
    private SortedMap<K, V> getPrefixMapByBits(final K key, final int offsetInBits, final int lengthInBits) {
        final int offsetLength = offsetInBits + lengthInBits;
        if (offsetLength > this.lengthInBits(key)) {
            throw new IllegalArgumentException(offsetInBits + " + " + lengthInBits + " > " + this.lengthInBits(key));
        }
        if (offsetLength == 0) {
            return this;
        }
        return new PrefixRangeMap((Object)key, offsetInBits, lengthInBits);
    }
    
    @Override
    public SortedMap<K, V> headMap(final K toKey) {
        return new RangeEntryMap(null, toKey);
    }
    
    @Override
    public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
        return new RangeEntryMap(fromKey, toKey);
    }
    
    @Override
    public SortedMap<K, V> tailMap(final K fromKey) {
        return new RangeEntryMap(fromKey, null);
    }
    
    TrieEntry<K, V> higherEntry(final K key) {
        final int lengthInBits = this.lengthInBits(key);
        if (lengthInBits == 0) {
            if (this.root.isEmpty()) {
                return this.firstEntry();
            }
            if (this.size() > 1) {
                return this.nextEntry(this.root);
            }
            return null;
        }
        else {
            final TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
            if (this.compareKeys(key, found.key)) {
                return this.nextEntry(found);
            }
            final int bitIndex = this.bitIndex(key, found.key);
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
                final TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
                this.addEntry(added, lengthInBits);
                this.incrementSize();
                final TrieEntry<K, V> ceil = this.nextEntry(added);
                this.removeEntry(added);
                this.modCount -= 2;
                return ceil;
            }
            if (KeyAnalyzer.isNullBitKey(bitIndex)) {
                if (!this.root.isEmpty()) {
                    return this.firstEntry();
                }
                if (this.size() > 1) {
                    return this.nextEntry(this.firstEntry());
                }
                return null;
            }
            else {
                if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
                    return this.nextEntry(found);
                }
                throw new IllegalStateException("invalid lookup: " + key);
            }
        }
    }
    
    TrieEntry<K, V> ceilingEntry(final K key) {
        final int lengthInBits = this.lengthInBits(key);
        if (lengthInBits == 0) {
            if (!this.root.isEmpty()) {
                return this.root;
            }
            return this.firstEntry();
        }
        else {
            final TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
            if (this.compareKeys(key, found.key)) {
                return found;
            }
            final int bitIndex = this.bitIndex(key, found.key);
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
                final TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
                this.addEntry(added, lengthInBits);
                this.incrementSize();
                final TrieEntry<K, V> ceil = this.nextEntry(added);
                this.removeEntry(added);
                this.modCount -= 2;
                return ceil;
            }
            if (KeyAnalyzer.isNullBitKey(bitIndex)) {
                if (!this.root.isEmpty()) {
                    return this.root;
                }
                return this.firstEntry();
            }
            else {
                if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
                    return found;
                }
                throw new IllegalStateException("invalid lookup: " + key);
            }
        }
    }
    
    TrieEntry<K, V> lowerEntry(final K key) {
        final int lengthInBits = this.lengthInBits(key);
        if (lengthInBits == 0) {
            return null;
        }
        final TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
        if (this.compareKeys(key, found.key)) {
            return this.previousEntry(found);
        }
        final int bitIndex = this.bitIndex(key, found.key);
        if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
            final TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
            this.addEntry(added, lengthInBits);
            this.incrementSize();
            final TrieEntry<K, V> prior = this.previousEntry(added);
            this.removeEntry(added);
            this.modCount -= 2;
            return prior;
        }
        if (KeyAnalyzer.isNullBitKey(bitIndex)) {
            return null;
        }
        if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
            return this.previousEntry(found);
        }
        throw new IllegalStateException("invalid lookup: " + key);
    }
    
    TrieEntry<K, V> floorEntry(final K key) {
        final int lengthInBits = this.lengthInBits(key);
        if (lengthInBits == 0) {
            if (!this.root.isEmpty()) {
                return this.root;
            }
            return null;
        }
        else {
            final TrieEntry<K, V> found = this.getNearestEntryForKey(key, lengthInBits);
            if (this.compareKeys(key, found.key)) {
                return found;
            }
            final int bitIndex = this.bitIndex(key, found.key);
            if (KeyAnalyzer.isValidBitIndex(bitIndex)) {
                final TrieEntry<K, V> added = new TrieEntry<K, V>(key, null, bitIndex);
                this.addEntry(added, lengthInBits);
                this.incrementSize();
                final TrieEntry<K, V> floor = this.previousEntry(added);
                this.removeEntry(added);
                this.modCount -= 2;
                return floor;
            }
            if (KeyAnalyzer.isNullBitKey(bitIndex)) {
                if (!this.root.isEmpty()) {
                    return this.root;
                }
                return null;
            }
            else {
                if (KeyAnalyzer.isEqualBitKey(bitIndex)) {
                    return found;
                }
                throw new IllegalStateException("invalid lookup: " + key);
            }
        }
    }
    
    TrieEntry<K, V> subtree(final K prefix, final int offsetInBits, final int lengthInBits) {
        TrieEntry<K, V> current = this.root.left;
        TrieEntry<K, V> path = this.root;
        while (current.bitIndex > path.bitIndex && lengthInBits > current.bitIndex) {
            path = current;
            if (!this.isBitSet(prefix, offsetInBits + current.bitIndex, offsetInBits + lengthInBits)) {
                current = current.left;
            }
            else {
                current = current.right;
            }
        }
        final TrieEntry<K, V> entry = current.isEmpty() ? path : current;
        if (entry.isEmpty()) {
            return null;
        }
        final int endIndexInBits = offsetInBits + lengthInBits;
        if (entry == this.root && this.lengthInBits(entry.getKey()) < endIndexInBits) {
            return null;
        }
        if (this.isBitSet(prefix, endIndexInBits - 1, endIndexInBits) != this.isBitSet(entry.key, lengthInBits - 1, this.lengthInBits(entry.key))) {
            return null;
        }
        final int bitIndex = this.getKeyAnalyzer().bitIndex((Object)prefix, offsetInBits, lengthInBits, (Object)entry.key, 0, this.lengthInBits(entry.getKey()));
        if (bitIndex >= 0 && bitIndex < lengthInBits) {
            return null;
        }
        return entry;
    }
    
    TrieEntry<K, V> lastEntry() {
        return this.followRight(this.root.left);
    }
    
    TrieEntry<K, V> followRight(TrieEntry<K, V> node) {
        if (node.right == null) {
            return null;
        }
        while (node.right.bitIndex > node.bitIndex) {
            node = node.right;
        }
        return node.right;
    }
    
    TrieEntry<K, V> previousEntry(final TrieEntry<K, V> start) {
        if (start.predecessor == null) {
            throw new IllegalArgumentException("must have come from somewhere!");
        }
        if (start.predecessor.right == start) {
            if (isValidUplink(start.predecessor.left, start.predecessor)) {
                return start.predecessor.left;
            }
            return this.followRight(start.predecessor.left);
        }
        else {
            TrieEntry<K, V> node;
            for (node = start.predecessor; node.parent != null && node == node.parent.left; node = node.parent) {}
            if (node.parent == null) {
                return null;
            }
            if (!isValidUplink(node.parent.left, node.parent)) {
                return this.followRight(node.parent.left);
            }
            if (node.parent.left != this.root) {
                return node.parent.left;
            }
            if (this.root.isEmpty()) {
                return null;
            }
            return this.root;
        }
    }
    
    TrieEntry<K, V> nextEntryInSubtree(final TrieEntry<K, V> node, final TrieEntry<K, V> parentOfSubtree) {
        if (node == null) {
            return this.firstEntry();
        }
        return this.nextEntryImpl(node.predecessor, node, parentOfSubtree);
    }
    
    static boolean isValidUplink(final TrieEntry<?, ?> next, final TrieEntry<?, ?> from) {
        return next != null && next.bitIndex <= from.bitIndex && !next.isEmpty();
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.root = new TrieEntry<K, V>(null, null, -1);
        for (int size = stream.readInt(), i = 0; i < size; ++i) {
            final K k = (K)stream.readObject();
            final V v = (V)stream.readObject();
            this.put(k, v);
        }
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.size());
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }
    
    private static class Reference<E>
    {
        private E item;
        
        public void set(final E item) {
            this.item = item;
        }
        
        public E get() {
            return this.item;
        }
    }
    
    protected static class TrieEntry<K, V> extends BasicEntry<K, V>
    {
        private static final long serialVersionUID = 4596023148184140013L;
        protected int bitIndex;
        protected TrieEntry<K, V> parent;
        protected TrieEntry<K, V> left;
        protected TrieEntry<K, V> right;
        protected TrieEntry<K, V> predecessor;
        
        public TrieEntry(final K key, final V value, final int bitIndex) {
            super(key, value);
            this.bitIndex = bitIndex;
            this.parent = null;
            this.left = this;
            this.right = null;
            this.predecessor = this;
        }
        
        public boolean isEmpty() {
            return this.key == null;
        }
        
        public boolean isInternalNode() {
            return this.left != this && this.right != this;
        }
        
        public boolean isExternalNode() {
            return !this.isInternalNode();
        }
        
        @Override
        public String toString() {
            final StringBuilder buffer = new StringBuilder();
            if (this.bitIndex == -1) {
                buffer.append("RootEntry(");
            }
            else {
                buffer.append("Entry(");
            }
            buffer.append("key=").append(this.getKey()).append(" [").append(this.bitIndex).append("], ");
            buffer.append("value=").append(this.getValue()).append(", ");
            if (this.parent != null) {
                if (this.parent.bitIndex == -1) {
                    buffer.append("parent=").append("ROOT");
                }
                else {
                    buffer.append("parent=").append(this.parent.getKey()).append(" [").append(this.parent.bitIndex).append("]");
                }
            }
            else {
                buffer.append("parent=").append("null");
            }
            buffer.append(", ");
            if (this.left != null) {
                if (this.left.bitIndex == -1) {
                    buffer.append("left=").append("ROOT");
                }
                else {
                    buffer.append("left=").append(this.left.getKey()).append(" [").append(this.left.bitIndex).append("]");
                }
            }
            else {
                buffer.append("left=").append("null");
            }
            buffer.append(", ");
            if (this.right != null) {
                if (this.right.bitIndex == -1) {
                    buffer.append("right=").append("ROOT");
                }
                else {
                    buffer.append("right=").append(this.right.getKey()).append(" [").append(this.right.bitIndex).append("]");
                }
            }
            else {
                buffer.append("right=").append("null");
            }
            buffer.append(", ");
            if (this.predecessor != null) {
                if (this.predecessor.bitIndex == -1) {
                    buffer.append("predecessor=").append("ROOT");
                }
                else {
                    buffer.append("predecessor=").append(this.predecessor.getKey()).append(" [").append(this.predecessor.bitIndex).append("]");
                }
            }
            buffer.append(")");
            return buffer.toString();
        }
    }
    
    private class EntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        final /* synthetic */ AbstractPatriciaTrie this$0;
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final TrieEntry<K, V> candidate = AbstractPatriciaTrie.this.getEntry(((Map.Entry)o).getKey());
            return candidate != null && candidate.equals(o);
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            if (!this.contains(obj)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            AbstractPatriciaTrie.this.remove(entry.getKey());
            return true;
        }
        
        @Override
        public int size() {
            return AbstractPatriciaTrie.this.size();
        }
        
        @Override
        public void clear() {
            AbstractPatriciaTrie.this.clear();
        }
        
        private class EntryIterator extends TrieIterator<Map.Entry<K, V>>
        {
            private EntryIterator() {
                EntrySet.this.this$0.super();
            }
            
            @Override
            public Map.Entry<K, V> next() {
                return this.nextEntry();
            }
        }
    }
    
    private class KeySet extends AbstractSet<K>
    {
        final /* synthetic */ AbstractPatriciaTrie this$0;
        
        @Override
        public Iterator<K> iterator() {
            return new KeyIterator();
        }
        
        @Override
        public int size() {
            return AbstractPatriciaTrie.this.size();
        }
        
        @Override
        public boolean contains(final Object o) {
            return AbstractPatriciaTrie.this.containsKey(o);
        }
        
        @Override
        public boolean remove(final Object o) {
            final int size = this.size();
            AbstractPatriciaTrie.this.remove(o);
            return size != this.size();
        }
        
        @Override
        public void clear() {
            AbstractPatriciaTrie.this.clear();
        }
        
        private class KeyIterator extends TrieIterator<K>
        {
            private KeyIterator() {
                KeySet.this.this$0.super();
            }
            
            @Override
            public K next() {
                return this.nextEntry().getKey();
            }
        }
    }
    
    private class Values extends AbstractCollection<V>
    {
        final /* synthetic */ AbstractPatriciaTrie this$0;
        
        @Override
        public Iterator<V> iterator() {
            return new ValueIterator();
        }
        
        @Override
        public int size() {
            return AbstractPatriciaTrie.this.size();
        }
        
        @Override
        public boolean contains(final Object o) {
            return AbstractPatriciaTrie.this.containsValue(o);
        }
        
        @Override
        public void clear() {
            AbstractPatriciaTrie.this.clear();
        }
        
        @Override
        public boolean remove(final Object o) {
            final Iterator<V> it = this.iterator();
            while (it.hasNext()) {
                final V value = it.next();
                if (AbstractBitwiseTrie.compare(value, o)) {
                    it.remove();
                    return true;
                }
            }
            return false;
        }
        
        private class ValueIterator extends TrieIterator<V>
        {
            private ValueIterator() {
                Values.this.this$0.super();
            }
            
            @Override
            public V next() {
                return this.nextEntry().getValue();
            }
        }
    }
    
    abstract class TrieIterator<E> implements Iterator<E>
    {
        protected int expectedModCount;
        protected TrieEntry<K, V> next;
        protected TrieEntry<K, V> current;
        
        protected TrieIterator() {
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            this.next = AbstractPatriciaTrie.this.nextEntry(null);
        }
        
        protected TrieIterator(final TrieEntry<K, V> firstEntry) {
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            this.next = firstEntry;
        }
        
        protected TrieEntry<K, V> nextEntry() {
            if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                throw new ConcurrentModificationException();
            }
            final TrieEntry<K, V> e = this.next;
            if (e == null) {
                throw new NoSuchElementException();
            }
            this.next = this.findNext(e);
            return this.current = e;
        }
        
        protected TrieEntry<K, V> findNext(final TrieEntry<K, V> prior) {
            return AbstractPatriciaTrie.this.nextEntry(prior);
        }
        
        @Override
        public boolean hasNext() {
            return this.next != null;
        }
        
        @Override
        public void remove() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                throw new ConcurrentModificationException();
            }
            final TrieEntry<K, V> node = this.current;
            this.current = null;
            AbstractPatriciaTrie.this.removeEntry(node);
            this.expectedModCount = AbstractPatriciaTrie.this.modCount;
        }
    }
    
    private class TrieMapIterator extends TrieIterator<K> implements OrderedMapIterator<K, V>
    {
        protected TrieEntry<K, V> previous;
        
        @Override
        public K next() {
            return this.nextEntry().getKey();
        }
        
        @Override
        public K getKey() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.getKey();
        }
        
        @Override
        public V getValue() {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.getValue();
        }
        
        @Override
        public V setValue(final V value) {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current.setValue(value);
        }
        
        @Override
        public boolean hasPrevious() {
            return this.previous != null;
        }
        
        @Override
        public K previous() {
            return this.previousEntry().getKey();
        }
        
        @Override
        protected TrieEntry<K, V> nextEntry() {
            final TrieEntry<K, V> nextEntry = super.nextEntry();
            return this.previous = nextEntry;
        }
        
        protected TrieEntry<K, V> previousEntry() {
            if (this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                throw new ConcurrentModificationException();
            }
            final TrieEntry<K, V> e = this.previous;
            if (e == null) {
                throw new NoSuchElementException();
            }
            this.previous = AbstractPatriciaTrie.this.previousEntry(e);
            this.next = this.current;
            return this.current = e;
        }
    }
    
    private abstract class RangeMap extends AbstractMap<K, V> implements SortedMap<K, V>
    {
        private transient volatile Set<Map.Entry<K, V>> entrySet;
        
        protected abstract Set<Map.Entry<K, V>> createEntrySet();
        
        protected abstract K getFromKey();
        
        protected abstract boolean isFromInclusive();
        
        protected abstract K getToKey();
        
        protected abstract boolean isToInclusive();
        
        @Override
        public Comparator<? super K> comparator() {
            return AbstractPatriciaTrie.this.comparator();
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return this.inRange(AbstractPatriciaTrie.this.castKey(key)) && AbstractPatriciaTrie.this.containsKey(key);
        }
        
        @Override
        public V remove(final Object key) {
            if (!this.inRange(AbstractPatriciaTrie.this.castKey(key))) {
                return null;
            }
            return AbstractPatriciaTrie.this.remove(key);
        }
        
        @Override
        public V get(final Object key) {
            if (!this.inRange(AbstractPatriciaTrie.this.castKey(key))) {
                return null;
            }
            return AbstractPatriciaTrie.this.get(key);
        }
        
        @Override
        public V put(final K key, final V value) {
            if (!this.inRange(key)) {
                throw new IllegalArgumentException("Key is out of range: " + key);
            }
            return AbstractPatriciaTrie.this.put(key, value);
        }
        
        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            if (this.entrySet == null) {
                this.entrySet = this.createEntrySet();
            }
            return this.entrySet;
        }
        
        @Override
        public SortedMap<K, V> subMap(final K fromKey, final K toKey) {
            if (!this.inRange2(fromKey)) {
                throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
            }
            if (!this.inRange2(toKey)) {
                throw new IllegalArgumentException("ToKey is out of range: " + toKey);
            }
            return this.createRangeMap(fromKey, this.isFromInclusive(), toKey, this.isToInclusive());
        }
        
        @Override
        public SortedMap<K, V> headMap(final K toKey) {
            if (!this.inRange2(toKey)) {
                throw new IllegalArgumentException("ToKey is out of range: " + toKey);
            }
            return this.createRangeMap(this.getFromKey(), this.isFromInclusive(), toKey, this.isToInclusive());
        }
        
        @Override
        public SortedMap<K, V> tailMap(final K fromKey) {
            if (!this.inRange2(fromKey)) {
                throw new IllegalArgumentException("FromKey is out of range: " + fromKey);
            }
            return this.createRangeMap(fromKey, this.isFromInclusive(), this.getToKey(), this.isToInclusive());
        }
        
        protected boolean inRange(final K key) {
            final K fromKey = this.getFromKey();
            final K toKey = this.getToKey();
            return (fromKey == null || this.inFromRange(key, false)) && (toKey == null || this.inToRange(key, false));
        }
        
        protected boolean inRange2(final K key) {
            final K fromKey = this.getFromKey();
            final K toKey = this.getToKey();
            return (fromKey == null || this.inFromRange(key, false)) && (toKey == null || this.inToRange(key, true));
        }
        
        protected boolean inFromRange(final K key, final boolean forceInclusive) {
            final K fromKey = this.getFromKey();
            final boolean fromInclusive = this.isFromInclusive();
            final int ret = AbstractPatriciaTrie.this.getKeyAnalyzer().compare(key, fromKey);
            if (fromInclusive || forceInclusive) {
                return ret >= 0;
            }
            return ret > 0;
        }
        
        protected boolean inToRange(final K key, final boolean forceInclusive) {
            final K toKey = this.getToKey();
            final boolean toInclusive = this.isToInclusive();
            final int ret = AbstractPatriciaTrie.this.getKeyAnalyzer().compare(key, toKey);
            if (toInclusive || forceInclusive) {
                return ret <= 0;
            }
            return ret < 0;
        }
        
        protected abstract SortedMap<K, V> createRangeMap(final K p0, final boolean p1, final K p2, final boolean p3);
    }
    
    private class RangeEntryMap extends RangeMap
    {
        private final K fromKey;
        private final K toKey;
        private final boolean fromInclusive;
        private final boolean toInclusive;
        
        protected RangeEntryMap(final AbstractPatriciaTrie abstractPatriciaTrie, final K fromKey, final K toKey) {
            this(fromKey, true, toKey, false);
        }
        
        protected RangeEntryMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
            if (fromKey == null && toKey == null) {
                throw new IllegalArgumentException("must have a from or to!");
            }
            if (fromKey != null && toKey != null && AbstractPatriciaTrie.this.getKeyAnalyzer().compare(fromKey, toKey) > 0) {
                throw new IllegalArgumentException("fromKey > toKey");
            }
            this.fromKey = fromKey;
            this.fromInclusive = fromInclusive;
            this.toKey = toKey;
            this.toInclusive = toInclusive;
        }
        
        @Override
        public K firstKey() {
            Map.Entry<K, V> e = null;
            if (this.fromKey == null) {
                e = AbstractPatriciaTrie.this.firstEntry();
            }
            else if (this.fromInclusive) {
                e = AbstractPatriciaTrie.this.ceilingEntry(this.fromKey);
            }
            else {
                e = AbstractPatriciaTrie.this.higherEntry(this.fromKey);
            }
            final K first = (e != null) ? e.getKey() : null;
            if (e == null || (this.toKey != null && !this.inToRange(first, false))) {
                throw new NoSuchElementException();
            }
            return first;
        }
        
        @Override
        public K lastKey() {
            Map.Entry<K, V> e;
            if (this.toKey == null) {
                e = AbstractPatriciaTrie.this.lastEntry();
            }
            else if (this.toInclusive) {
                e = AbstractPatriciaTrie.this.floorEntry(this.toKey);
            }
            else {
                e = AbstractPatriciaTrie.this.lowerEntry(this.toKey);
            }
            final K last = (e != null) ? e.getKey() : null;
            if (e == null || (this.fromKey != null && !this.inFromRange(last, false))) {
                throw new NoSuchElementException();
            }
            return last;
        }
        
        @Override
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return new RangeEntrySet(this);
        }
        
        public K getFromKey() {
            return this.fromKey;
        }
        
        public K getToKey() {
            return this.toKey;
        }
        
        public boolean isFromInclusive() {
            return this.fromInclusive;
        }
        
        public boolean isToInclusive() {
            return this.toInclusive;
        }
        
        @Override
        protected SortedMap<K, V> createRangeMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
            return new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
        }
    }
    
    private class RangeEntrySet extends AbstractSet<Map.Entry<K, V>>
    {
        private final RangeMap delegate;
        private transient int size;
        private transient int expectedModCount;
        final /* synthetic */ AbstractPatriciaTrie this$0;
        
        public RangeEntrySet(final RangeMap delegate) {
            this.size = -1;
            if (delegate == null) {
                throw new NullPointerException("delegate");
            }
            this.delegate = delegate;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            final K fromKey = this.delegate.getFromKey();
            final K toKey = this.delegate.getToKey();
            TrieEntry<K, V> first = null;
            if (fromKey == null) {
                first = AbstractPatriciaTrie.this.firstEntry();
            }
            else {
                first = AbstractPatriciaTrie.this.ceilingEntry(fromKey);
            }
            TrieEntry<K, V> last = null;
            if (toKey != null) {
                last = AbstractPatriciaTrie.this.ceilingEntry(toKey);
            }
            return new EntryIterator((TrieEntry)first, (TrieEntry)last);
        }
        
        @Override
        public int size() {
            if (this.size == -1 || this.expectedModCount != AbstractPatriciaTrie.this.modCount) {
                this.size = 0;
                final Iterator<?> it = this.iterator();
                while (it.hasNext()) {
                    ++this.size;
                    it.next();
                }
                this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            }
            return this.size;
        }
        
        @Override
        public boolean isEmpty() {
            return !this.iterator().hasNext();
        }
        
        @Override
        public boolean contains(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
            final K key = entry.getKey();
            if (!this.delegate.inRange(key)) {
                return false;
            }
            final TrieEntry<K, V> node = AbstractPatriciaTrie.this.getEntry(key);
            return node != null && AbstractBitwiseTrie.compare(node.getValue(), entry.getValue());
        }
        
        @Override
        public boolean remove(final Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<K, V> entry = (Map.Entry<K, V>)o;
            final K key = entry.getKey();
            if (!this.delegate.inRange(key)) {
                return false;
            }
            final TrieEntry<K, V> node = AbstractPatriciaTrie.this.getEntry(key);
            if (node != null && AbstractBitwiseTrie.compare(node.getValue(), entry.getValue())) {
                AbstractPatriciaTrie.this.removeEntry(node);
                return true;
            }
            return false;
        }
        
        private final class EntryIterator extends TrieIterator<Map.Entry<K, V>>
        {
            private final K excludedKey;
            
            private EntryIterator(final TrieEntry<K, V> first, final TrieEntry<K, V> last) {
                RangeEntrySet.this.this$0.super(first);
                this.excludedKey = ((last != null) ? last.getKey() : null);
            }
            
            @Override
            public boolean hasNext() {
                return this.next != null && !AbstractBitwiseTrie.compare(this.next.key, this.excludedKey);
            }
            
            @Override
            public Map.Entry<K, V> next() {
                if (this.next == null || AbstractBitwiseTrie.compare(this.next.key, this.excludedKey)) {
                    throw new NoSuchElementException();
                }
                return this.nextEntry();
            }
        }
    }
    
    private class PrefixRangeMap extends RangeMap
    {
        private final K prefix;
        private final int offsetInBits;
        private final int lengthInBits;
        private K fromKey;
        private K toKey;
        private transient int expectedModCount;
        private int size;
        
        private PrefixRangeMap(final K prefix, final int offsetInBits, final int lengthInBits) {
            this.fromKey = null;
            this.toKey = null;
            this.expectedModCount = 0;
            this.size = -1;
            this.prefix = prefix;
            this.offsetInBits = offsetInBits;
            this.lengthInBits = lengthInBits;
        }
        
        private int fixup() {
            if (this.size == -1 || AbstractPatriciaTrie.this.modCount != this.expectedModCount) {
                final Iterator<Map.Entry<K, V>> it = super.entrySet().iterator();
                this.size = 0;
                Map.Entry<K, V> entry = null;
                if (it.hasNext()) {
                    entry = it.next();
                    this.size = 1;
                }
                this.fromKey = ((entry == null) ? null : entry.getKey());
                if (this.fromKey != null) {
                    final TrieEntry<K, V> prior = AbstractPatriciaTrie.this.previousEntry((TrieEntry)entry);
                    this.fromKey = ((prior == null) ? null : prior.getKey());
                }
                this.toKey = this.fromKey;
                while (it.hasNext()) {
                    ++this.size;
                    entry = it.next();
                }
                this.toKey = ((entry == null) ? null : entry.getKey());
                if (this.toKey != null) {
                    entry = AbstractPatriciaTrie.this.nextEntry((TrieEntry)entry);
                    this.toKey = ((entry == null) ? null : entry.getKey());
                }
                this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            }
            return this.size;
        }
        
        @Override
        public K firstKey() {
            this.fixup();
            Map.Entry<K, V> e = null;
            if (this.fromKey == null) {
                e = AbstractPatriciaTrie.this.firstEntry();
            }
            else {
                e = AbstractPatriciaTrie.this.higherEntry(this.fromKey);
            }
            final K first = (e != null) ? e.getKey() : null;
            if (e == null || !AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, first)) {
                throw new NoSuchElementException();
            }
            return first;
        }
        
        @Override
        public K lastKey() {
            this.fixup();
            Map.Entry<K, V> e = null;
            if (this.toKey == null) {
                e = AbstractPatriciaTrie.this.lastEntry();
            }
            else {
                e = AbstractPatriciaTrie.this.lowerEntry(this.toKey);
            }
            final K last = (e != null) ? e.getKey() : null;
            if (e == null || !AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, last)) {
                throw new NoSuchElementException();
            }
            return last;
        }
        
        @Override
        protected boolean inRange(final K key) {
            return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key);
        }
        
        @Override
        protected boolean inRange2(final K key) {
            return this.inRange(key);
        }
        
        @Override
        protected boolean inFromRange(final K key, final boolean forceInclusive) {
            return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key);
        }
        
        @Override
        protected boolean inToRange(final K key, final boolean forceInclusive) {
            return AbstractPatriciaTrie.this.getKeyAnalyzer().isPrefix(this.prefix, this.offsetInBits, this.lengthInBits, key);
        }
        
        @Override
        protected Set<Map.Entry<K, V>> createEntrySet() {
            return new PrefixRangeEntrySet(this);
        }
        
        public K getFromKey() {
            return this.fromKey;
        }
        
        public K getToKey() {
            return this.toKey;
        }
        
        public boolean isFromInclusive() {
            return false;
        }
        
        public boolean isToInclusive() {
            return false;
        }
        
        @Override
        protected SortedMap<K, V> createRangeMap(final K fromKey, final boolean fromInclusive, final K toKey, final boolean toInclusive) {
            return new RangeEntryMap(fromKey, fromInclusive, toKey, toInclusive);
        }
    }
    
    private final class PrefixRangeEntrySet extends RangeEntrySet
    {
        private final PrefixRangeMap delegate;
        private TrieEntry<K, V> prefixStart;
        private int expectedModCount;
        final /* synthetic */ AbstractPatriciaTrie this$0;
        
        public PrefixRangeEntrySet(final PrefixRangeMap delegate) {
            super(delegate);
            this.expectedModCount = 0;
            this.delegate = delegate;
        }
        
        @Override
        public int size() {
            return this.delegate.fixup();
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            if (AbstractPatriciaTrie.this.modCount != this.expectedModCount) {
                this.prefixStart = (TrieEntry<K, V>)AbstractPatriciaTrie.this.subtree(this.delegate.prefix, this.delegate.offsetInBits, this.delegate.lengthInBits);
                this.expectedModCount = AbstractPatriciaTrie.this.modCount;
            }
            if (this.prefixStart == null) {
                final Set<Map.Entry<K, V>> empty = Collections.emptySet();
                return empty.iterator();
            }
            if (this.delegate.lengthInBits > this.prefixStart.bitIndex) {
                return new SingletonIterator(this.prefixStart);
            }
            return new EntryIterator(this.prefixStart, this.delegate.prefix, this.delegate.offsetInBits, this.delegate.lengthInBits);
        }
        
        private final class SingletonIterator implements Iterator<Map.Entry<K, V>>
        {
            private final TrieEntry<K, V> entry;
            private int hit;
            
            public SingletonIterator(final TrieEntry<K, V> entry) {
                this.hit = 0;
                this.entry = entry;
            }
            
            @Override
            public boolean hasNext() {
                return this.hit == 0;
            }
            
            @Override
            public Map.Entry<K, V> next() {
                if (this.hit != 0) {
                    throw new NoSuchElementException();
                }
                ++this.hit;
                return this.entry;
            }
            
            @Override
            public void remove() {
                if (this.hit != 1) {
                    throw new IllegalStateException();
                }
                ++this.hit;
                AbstractPatriciaTrie.this.removeEntry(this.entry);
            }
        }
        
        private final class EntryIterator extends TrieIterator<Map.Entry<K, V>>
        {
            private final K prefix;
            private final int offset;
            private final int lengthInBits;
            private boolean lastOne;
            private TrieEntry<K, V> subtree;
            
            EntryIterator(final TrieEntry<K, V> startScan, final K prefix, final int offset, final int lengthInBits) {
                PrefixRangeEntrySet.this.this$0.super();
                this.subtree = startScan;
                this.next = PrefixRangeEntrySet.this.this$0.followLeft(startScan);
                this.prefix = prefix;
                this.offset = offset;
                this.lengthInBits = lengthInBits;
            }
            
            @Override
            public Map.Entry<K, V> next() {
                final Map.Entry<K, V> entry = this.nextEntry();
                if (this.lastOne) {
                    this.next = null;
                }
                return entry;
            }
            
            @Override
            protected TrieEntry<K, V> findNext(final TrieEntry<K, V> prior) {
                return AbstractPatriciaTrie.this.nextEntryInSubtree(prior, this.subtree);
            }
            
            @Override
            public void remove() {
                boolean needsFixing = false;
                final int bitIdx = this.subtree.bitIndex;
                if (this.current == this.subtree) {
                    needsFixing = true;
                }
                super.remove();
                if (bitIdx != this.subtree.bitIndex || needsFixing) {
                    this.subtree = AbstractPatriciaTrie.this.subtree(this.prefix, this.offset, this.lengthInBits);
                }
                if (this.lengthInBits >= this.subtree.bitIndex) {
                    this.lastOne = true;
                }
            }
        }
    }
}
