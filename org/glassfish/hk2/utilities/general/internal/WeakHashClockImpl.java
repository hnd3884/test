package org.glassfish.hk2.utilities.general.internal;

import java.util.Map;
import java.util.Iterator;
import java.util.LinkedList;
import org.glassfish.hk2.utilities.cache.CacheKeyFilter;
import java.lang.ref.ReferenceQueue;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.glassfish.hk2.utilities.general.WeakHashClock;

public class WeakHashClockImpl<K, V> implements WeakHashClock<K, V>
{
    private final boolean isWeak;
    private final ConcurrentHashMap<K, DoubleNode<K, V>> byKeyNotWeak;
    private final WeakHashMap<K, DoubleNode<K, V>> byKey;
    private final ReferenceQueue<? super K> myQueue;
    private DoubleNode<K, V> head;
    private DoubleNode<K, V> tail;
    private DoubleNode<K, V> dot;
    
    public WeakHashClockImpl(final boolean isWeak) {
        this.myQueue = new ReferenceQueue<Object>();
        this.isWeak = isWeak;
        if (isWeak) {
            this.byKey = new WeakHashMap<K, DoubleNode<K, V>>();
            this.byKeyNotWeak = null;
        }
        else {
            this.byKeyNotWeak = new ConcurrentHashMap<K, DoubleNode<K, V>>();
            this.byKey = null;
        }
    }
    
    private DoubleNode<K, V> addBeforeDot(final K key, final V value) {
        final DoubleNode<K, V> toAdd = new DoubleNode<K, V>(key, value, this.myQueue);
        if (this.dot == null) {
            this.head = toAdd;
            this.tail = toAdd;
            return this.dot = toAdd;
        }
        if (this.dot.getPrevious() == null) {
            this.dot.setPrevious(toAdd);
            toAdd.setNext(this.dot);
            return this.head = toAdd;
        }
        toAdd.setNext(this.dot);
        toAdd.setPrevious(this.dot.getPrevious());
        this.dot.getPrevious().setNext(toAdd);
        this.dot.setPrevious(toAdd);
        return toAdd;
    }
    
    private void removeFromDLL(final DoubleNode<K, V> removeMe) {
        if (removeMe.getPrevious() != null) {
            removeMe.getPrevious().setNext(removeMe.getNext());
        }
        if (removeMe.getNext() != null) {
            removeMe.getNext().setPrevious(removeMe.getPrevious());
        }
        if (removeMe == this.head) {
            this.head = removeMe.getNext();
        }
        if (removeMe == this.tail) {
            this.tail = removeMe.getPrevious();
        }
        if (removeMe == this.dot) {
            this.dot = removeMe.getNext();
            if (this.dot == null) {
                this.dot = this.head;
            }
        }
        removeMe.setNext(null);
        removeMe.setPrevious(null);
    }
    
    @Override
    public void put(final K key, final V value) {
        if (key == null || value == null) {
            throw new IllegalArgumentException("key " + key + " or value " + value + " is null");
        }
        synchronized (this) {
            if (this.isWeak) {
                this.removeStale();
            }
            final DoubleNode<K, V> addMe = this.addBeforeDot(key, value);
            if (this.isWeak) {
                this.byKey.put(key, addMe);
            }
            else {
                this.byKeyNotWeak.put(key, addMe);
            }
        }
    }
    
    @Override
    public V get(final K key) {
        if (key == null) {
            return null;
        }
        DoubleNode<K, V> node;
        if (this.isWeak) {
            synchronized (this) {
                this.removeStale();
                node = this.byKey.get(key);
            }
        }
        else {
            node = this.byKeyNotWeak.get(key);
        }
        if (node == null) {
            return null;
        }
        return node.getValue();
    }
    
    @Override
    public V remove(final K key) {
        if (key == null) {
            return null;
        }
        synchronized (this) {
            DoubleNode<K, V> node;
            if (this.isWeak) {
                this.removeStale();
                node = this.byKey.remove(key);
            }
            else {
                node = this.byKeyNotWeak.remove(key);
            }
            if (node == null) {
                return null;
            }
            this.removeFromDLL(node);
            return node.getValue();
        }
    }
    
    @Override
    public synchronized void releaseMatching(final CacheKeyFilter<K> filter) {
        if (filter == null) {
            return;
        }
        if (this.isWeak) {
            this.removeStale();
        }
        final LinkedList<K> removeMe = new LinkedList<K>();
        for (DoubleNode<K, V> current = this.head; current != null; current = current.getNext()) {
            final K key = current.getWeakKey().get();
            if (key != null && filter.matches(key)) {
                removeMe.add(key);
            }
        }
        for (final K removeKey : removeMe) {
            this.remove(removeKey);
        }
    }
    
    @Override
    public int size() {
        if (this.isWeak) {
            synchronized (this) {
                this.removeStale();
                return this.byKey.size();
            }
        }
        return this.byKeyNotWeak.size();
    }
    
    private DoubleNode<K, V> moveDot() {
        if (this.dot == null) {
            return null;
        }
        final DoubleNode<K, V> returnSource = this.dot;
        this.dot = returnSource.getNext();
        if (this.dot == null) {
            this.dot = this.head;
        }
        return returnSource;
    }
    
    private DoubleNode<K, V> moveDotNoWeak() {
        DoubleNode<K, V> retVal;
        final DoubleNode<K, V> original = retVal = this.moveDot();
        if (retVal == null) {
            return null;
        }
        K key;
        while ((key = retVal.getWeakKey().get()) == null) {
            retVal = this.moveDot();
            if (retVal == null) {
                return null;
            }
            if (retVal == original) {
                return null;
            }
        }
        retVal.setHardenedKey(key);
        return retVal;
    }
    
    @Override
    public synchronized Map.Entry<K, V> next() {
        final DoubleNode<K, V> hardenedNode = this.moveDotNoWeak();
        if (hardenedNode == null) {
            return null;
        }
        try {
            final K key = hardenedNode.getHardenedKey();
            final V value = hardenedNode.getValue();
            return new Map.Entry<K, V>() {
                @Override
                public K getKey() {
                    return key;
                }
                
                @Override
                public V getValue() {
                    return value;
                }
                
                @Override
                public V setValue(final V value) {
                    throw new AssertionError((Object)"not implemented");
                }
            };
        }
        finally {
            hardenedNode.setHardenedKey(null);
            this.removeStale();
        }
    }
    
    @Override
    public synchronized void clear() {
        if (this.isWeak) {
            this.byKey.clear();
        }
        else {
            this.byKeyNotWeak.clear();
        }
        final DoubleNode<K, V> head = null;
        this.dot = head;
        this.tail = head;
        this.head = head;
    }
    
    @Override
    public synchronized void clearStaleReferences() {
        this.removeStale();
    }
    
    private void removeStale() {
        boolean goOn = false;
        while (this.myQueue.poll() != null) {
            goOn = true;
        }
        if (!goOn) {
            return;
        }
        DoubleNode<K, V> next;
        for (DoubleNode<K, V> current = this.head; current != null; current = next) {
            next = current.getNext();
            if (current.getWeakKey().get() == null) {
                this.removeFromDLL(current);
            }
        }
    }
    
    @Override
    public boolean hasWeakKeys() {
        return this.isWeak;
    }
    
    @Override
    public synchronized String toString() {
        final StringBuffer sb = new StringBuffer("WeakHashClockImpl({");
        boolean first = true;
        DoubleNode<K, V> current = this.dot;
        if (current != null) {
            do {
                final K key = current.getWeakKey().get();
                final String keyString = (key == null) ? "null" : key.toString();
                if (first) {
                    first = false;
                    sb.append(keyString);
                }
                else {
                    sb.append("," + keyString);
                }
                current = current.getNext();
                if (current == null) {
                    current = this.head;
                }
            } while (current != this.dot);
        }
        sb.append("}," + System.identityHashCode(this) + ")");
        return sb.toString();
    }
}
