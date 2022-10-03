package org.apache.lucene.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Collection;
import java.util.Arrays;

public final class FrequencyTrackingRingBuffer implements Accountable
{
    private static final long BASE_RAM_BYTES_USED;
    private final int maxSize;
    private final int[] buffer;
    private int position;
    private final IntBag frequencies;
    
    public FrequencyTrackingRingBuffer(final int maxSize, final int sentinel) {
        if (maxSize < 2) {
            throw new IllegalArgumentException("maxSize must be at least 2");
        }
        this.maxSize = maxSize;
        this.buffer = new int[maxSize];
        this.position = 0;
        this.frequencies = new IntBag(maxSize);
        Arrays.fill(this.buffer, sentinel);
        for (int i = 0; i < maxSize; ++i) {
            this.frequencies.add(sentinel);
        }
        assert this.frequencies.frequency(sentinel) == maxSize;
    }
    
    @Override
    public long ramBytesUsed() {
        return FrequencyTrackingRingBuffer.BASE_RAM_BYTES_USED + this.frequencies.ramBytesUsed() + RamUsageEstimator.sizeOf(this.buffer);
    }
    
    @Override
    public Collection<Accountable> getChildResources() {
        return (Collection<Accountable>)Collections.emptyList();
    }
    
    public void add(final int i) {
        final int removed = this.buffer[this.position];
        final boolean removedFromBag = this.frequencies.remove(removed);
        assert removedFromBag;
        this.buffer[this.position] = i;
        this.frequencies.add(i);
        ++this.position;
        if (this.position == this.maxSize) {
            this.position = 0;
        }
    }
    
    public int frequency(final int key) {
        return this.frequencies.frequency(key);
    }
    
    Map<Integer, Integer> asFrequencyMap() {
        return this.frequencies.asMap();
    }
    
    static {
        BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(FrequencyTrackingRingBuffer.class);
    }
    
    private static class IntBag implements Accountable
    {
        private static final long BASE_RAM_BYTES_USED;
        private final int[] keys;
        private final int[] freqs;
        private final int mask;
        
        IntBag(final int maxSize) {
            int capacity = Math.max(2, maxSize * 3 / 2);
            capacity = Integer.highestOneBit(capacity - 1) << 1;
            assert capacity > maxSize;
            this.keys = new int[capacity];
            this.freqs = new int[capacity];
            this.mask = capacity - 1;
        }
        
        @Override
        public long ramBytesUsed() {
            return IntBag.BASE_RAM_BYTES_USED + RamUsageEstimator.sizeOf(this.keys) + RamUsageEstimator.sizeOf(this.freqs);
        }
        
        @Override
        public Collection<Accountable> getChildResources() {
            return (Collection<Accountable>)Collections.emptyList();
        }
        
        int frequency(final int key) {
            int slot;
            for (slot = (key & this.mask); this.keys[slot] != key; slot = (slot + 1 & this.mask)) {
                if (this.freqs[slot] == 0) {
                    return 0;
                }
            }
            return this.freqs[slot];
        }
        
        int add(final int key) {
            int slot;
            for (slot = (key & this.mask); this.freqs[slot] != 0; slot = (slot + 1 & this.mask)) {
                if (this.keys[slot] == key) {
                    return ++this.freqs[slot];
                }
            }
            this.keys[slot] = key;
            return this.freqs[slot] = 1;
        }
        
        boolean remove(final int key) {
            for (int slot = key & this.mask; this.freqs[slot] != 0; slot = (slot + 1 & this.mask)) {
                if (this.keys[slot] == key) {
                    final int[] freqs = this.freqs;
                    final int n = slot;
                    final int n2 = freqs[n] - 1;
                    freqs[n] = n2;
                    final int newFreq = n2;
                    if (newFreq == 0) {
                        this.relocateAdjacentKeys(slot);
                    }
                    return true;
                }
            }
            return false;
        }
        
        private void relocateAdjacentKeys(int freeSlot) {
            int slot = freeSlot + 1 & this.mask;
            while (true) {
                final int freq = this.freqs[slot];
                if (freq == 0) {
                    break;
                }
                final int key = this.keys[slot];
                final int expectedSlot = key & this.mask;
                if (between(expectedSlot, slot, freeSlot)) {
                    this.keys[freeSlot] = key;
                    this.freqs[freeSlot] = freq;
                    this.freqs[slot] = 0;
                    freeSlot = slot;
                }
                slot = (slot + 1 & this.mask);
            }
        }
        
        private static boolean between(final int chainStart, final int chainEnd, final int slot) {
            if (chainStart <= chainEnd) {
                return chainStart <= slot && slot <= chainEnd;
            }
            return slot >= chainStart || slot <= chainEnd;
        }
        
        Map<Integer, Integer> asMap() {
            final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (int i = 0; i < this.keys.length; ++i) {
                if (this.freqs[i] > 0) {
                    map.put(this.keys[i], this.freqs[i]);
                }
            }
            return map;
        }
        
        static {
            BASE_RAM_BYTES_USED = RamUsageEstimator.shallowSizeOfInstance(IntBag.class);
        }
    }
}
