package org.apache.commons.math3.util;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class OpenIntToDoubleHashMap implements Serializable
{
    protected static final byte FREE = 0;
    protected static final byte FULL = 1;
    protected static final byte REMOVED = 2;
    private static final long serialVersionUID = -3646337053166149105L;
    private static final float LOAD_FACTOR = 0.5f;
    private static final int DEFAULT_EXPECTED_SIZE = 16;
    private static final int RESIZE_MULTIPLIER = 2;
    private static final int PERTURB_SHIFT = 5;
    private int[] keys;
    private double[] values;
    private byte[] states;
    private final double missingEntries;
    private int size;
    private int mask;
    private transient int count;
    
    public OpenIntToDoubleHashMap() {
        this(16, Double.NaN);
    }
    
    public OpenIntToDoubleHashMap(final double missingEntries) {
        this(16, missingEntries);
    }
    
    public OpenIntToDoubleHashMap(final int expectedSize) {
        this(expectedSize, Double.NaN);
    }
    
    public OpenIntToDoubleHashMap(final int expectedSize, final double missingEntries) {
        final int capacity = computeCapacity(expectedSize);
        this.keys = new int[capacity];
        this.values = new double[capacity];
        this.states = new byte[capacity];
        this.missingEntries = missingEntries;
        this.mask = capacity - 1;
    }
    
    public OpenIntToDoubleHashMap(final OpenIntToDoubleHashMap source) {
        final int length = source.keys.length;
        this.keys = new int[length];
        System.arraycopy(source.keys, 0, this.keys, 0, length);
        this.values = new double[length];
        System.arraycopy(source.values, 0, this.values, 0, length);
        this.states = new byte[length];
        System.arraycopy(source.states, 0, this.states, 0, length);
        this.missingEntries = source.missingEntries;
        this.size = source.size;
        this.mask = source.mask;
        this.count = source.count;
    }
    
    private static int computeCapacity(final int expectedSize) {
        if (expectedSize == 0) {
            return 1;
        }
        final int capacity = (int)FastMath.ceil(expectedSize / 0.5f);
        final int powerOfTwo = Integer.highestOneBit(capacity);
        if (powerOfTwo == capacity) {
            return capacity;
        }
        return nextPowerOfTwo(capacity);
    }
    
    private static int nextPowerOfTwo(final int i) {
        return Integer.highestOneBit(i) << 1;
    }
    
    public double get(final int key) {
        final int hash = hashOf(key);
        int index = hash & this.mask;
        if (this.containsKey(key, index)) {
            return this.values[index];
        }
        if (this.states[index] == 0) {
            return this.missingEntries;
        }
        int j = index;
        int perturb = perturb(hash);
        while (this.states[index] != 0) {
            j = probe(perturb, j);
            index = (j & this.mask);
            if (this.containsKey(key, index)) {
                return this.values[index];
            }
            perturb >>= 5;
        }
        return this.missingEntries;
    }
    
    public boolean containsKey(final int key) {
        final int hash = hashOf(key);
        int index = hash & this.mask;
        if (this.containsKey(key, index)) {
            return true;
        }
        if (this.states[index] == 0) {
            return false;
        }
        int j = index;
        int perturb = perturb(hash);
        while (this.states[index] != 0) {
            j = probe(perturb, j);
            index = (j & this.mask);
            if (this.containsKey(key, index)) {
                return true;
            }
            perturb >>= 5;
        }
        return false;
    }
    
    public Iterator iterator() {
        return new Iterator();
    }
    
    private static int perturb(final int hash) {
        return hash & Integer.MAX_VALUE;
    }
    
    private int findInsertionIndex(final int key) {
        return findInsertionIndex(this.keys, this.states, key, this.mask);
    }
    
    private static int findInsertionIndex(final int[] keys, final byte[] states, final int key, final int mask) {
        final int hash = hashOf(key);
        int index = hash & mask;
        if (states[index] == 0) {
            return index;
        }
        if (states[index] == 1 && keys[index] == key) {
            return changeIndexSign(index);
        }
        int perturb = perturb(hash);
        int j = index;
        if (states[index] == 1) {
            do {
                j = probe(perturb, j);
                index = (j & mask);
                perturb >>= 5;
            } while (states[index] == 1 && keys[index] != key);
        }
        if (states[index] == 0) {
            return index;
        }
        if (states[index] == 1) {
            return changeIndexSign(index);
        }
        final int firstRemoved = index;
        while (true) {
            j = probe(perturb, j);
            index = (j & mask);
            if (states[index] == 0) {
                return firstRemoved;
            }
            if (states[index] == 1 && keys[index] == key) {
                return changeIndexSign(index);
            }
            perturb >>= 5;
        }
    }
    
    private static int probe(final int perturb, final int j) {
        return (j << 2) + j + perturb + 1;
    }
    
    private static int changeIndexSign(final int index) {
        return -index - 1;
    }
    
    public int size() {
        return this.size;
    }
    
    public double remove(final int key) {
        final int hash = hashOf(key);
        int index = hash & this.mask;
        if (this.containsKey(key, index)) {
            return this.doRemove(index);
        }
        if (this.states[index] == 0) {
            return this.missingEntries;
        }
        int j = index;
        int perturb = perturb(hash);
        while (this.states[index] != 0) {
            j = probe(perturb, j);
            index = (j & this.mask);
            if (this.containsKey(key, index)) {
                return this.doRemove(index);
            }
            perturb >>= 5;
        }
        return this.missingEntries;
    }
    
    private boolean containsKey(final int key, final int index) {
        return (key != 0 || this.states[index] == 1) && this.keys[index] == key;
    }
    
    private double doRemove(final int index) {
        this.keys[index] = 0;
        this.states[index] = 2;
        final double previous = this.values[index];
        this.values[index] = this.missingEntries;
        --this.size;
        ++this.count;
        return previous;
    }
    
    public double put(final int key, final double value) {
        int index = this.findInsertionIndex(key);
        double previous = this.missingEntries;
        boolean newMapping = true;
        if (index < 0) {
            index = changeIndexSign(index);
            previous = this.values[index];
            newMapping = false;
        }
        this.keys[index] = key;
        this.states[index] = 1;
        this.values[index] = value;
        if (newMapping) {
            ++this.size;
            if (this.shouldGrowTable()) {
                this.growTable();
            }
            ++this.count;
        }
        return previous;
    }
    
    private void growTable() {
        final int oldLength = this.states.length;
        final int[] oldKeys = this.keys;
        final double[] oldValues = this.values;
        final byte[] oldStates = this.states;
        final int newLength = 2 * oldLength;
        final int[] newKeys = new int[newLength];
        final double[] newValues = new double[newLength];
        final byte[] newStates = new byte[newLength];
        final int newMask = newLength - 1;
        for (int i = 0; i < oldLength; ++i) {
            if (oldStates[i] == 1) {
                final int key = oldKeys[i];
                final int index = findInsertionIndex(newKeys, newStates, key, newMask);
                newKeys[index] = key;
                newValues[index] = oldValues[i];
                newStates[index] = 1;
            }
        }
        this.mask = newMask;
        this.keys = newKeys;
        this.values = newValues;
        this.states = newStates;
    }
    
    private boolean shouldGrowTable() {
        return this.size > (this.mask + 1) * 0.5f;
    }
    
    private static int hashOf(final int key) {
        final int h = key ^ (key >>> 20 ^ key >>> 12);
        return h ^ h >>> 7 ^ h >>> 4;
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.count = 0;
    }
    
    public class Iterator
    {
        private final int referenceCount;
        private int current;
        private int next;
        
        private Iterator() {
            this.referenceCount = OpenIntToDoubleHashMap.this.count;
            this.next = -1;
            try {
                this.advance();
            }
            catch (final NoSuchElementException ex) {}
        }
        
        public boolean hasNext() {
            return this.next >= 0;
        }
        
        public int key() throws ConcurrentModificationException, NoSuchElementException {
            if (this.referenceCount != OpenIntToDoubleHashMap.this.count) {
                throw new ConcurrentModificationException();
            }
            if (this.current < 0) {
                throw new NoSuchElementException();
            }
            return OpenIntToDoubleHashMap.this.keys[this.current];
        }
        
        public double value() throws ConcurrentModificationException, NoSuchElementException {
            if (this.referenceCount != OpenIntToDoubleHashMap.this.count) {
                throw new ConcurrentModificationException();
            }
            if (this.current < 0) {
                throw new NoSuchElementException();
            }
            return OpenIntToDoubleHashMap.this.values[this.current];
        }
        
        public void advance() throws ConcurrentModificationException, NoSuchElementException {
            if (this.referenceCount != OpenIntToDoubleHashMap.this.count) {
                throw new ConcurrentModificationException();
            }
            this.current = this.next;
            try {
                while (OpenIntToDoubleHashMap.this.states[++this.next] != 1) {}
            }
            catch (final ArrayIndexOutOfBoundsException e) {
                this.next = -2;
                if (this.current < 0) {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}
