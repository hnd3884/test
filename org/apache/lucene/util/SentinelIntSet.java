package org.apache.lucene.util;

import java.util.Arrays;

public class SentinelIntSet
{
    public int[] keys;
    public int count;
    public final int emptyVal;
    public int rehashCount;
    
    public SentinelIntSet(final int size, final int emptyVal) {
        this.emptyVal = emptyVal;
        int tsize = Math.max(BitUtil.nextHighestPowerOfTwo(size), 1);
        this.rehashCount = tsize - (tsize >> 2);
        if (size >= this.rehashCount) {
            tsize <<= 1;
            this.rehashCount = tsize - (tsize >> 2);
        }
        this.keys = new int[tsize];
        if (emptyVal != 0) {
            this.clear();
        }
    }
    
    public void clear() {
        Arrays.fill(this.keys, this.emptyVal);
        this.count = 0;
    }
    
    public int hash(final int key) {
        return key;
    }
    
    public int size() {
        return this.count;
    }
    
    public int getSlot(final int key) {
        assert key != this.emptyVal;
        final int h = this.hash(key);
        int s = h & this.keys.length - 1;
        if (this.keys[s] == key || this.keys[s] == this.emptyVal) {
            return s;
        }
        final int increment = h >> 7 | 0x1;
        do {
            s = (s + increment & this.keys.length - 1);
        } while (this.keys[s] != key && this.keys[s] != this.emptyVal);
        return s;
    }
    
    public int find(final int key) {
        assert key != this.emptyVal;
        final int h = this.hash(key);
        int s = h & this.keys.length - 1;
        if (this.keys[s] == key) {
            return s;
        }
        if (this.keys[s] == this.emptyVal) {
            return -s - 1;
        }
        final int increment = h >> 7 | 0x1;
        do {
            s = (s + increment & this.keys.length - 1);
            if (this.keys[s] == key) {
                return s;
            }
        } while (this.keys[s] != this.emptyVal);
        return -s - 1;
    }
    
    public boolean exists(final int key) {
        return this.find(key) >= 0;
    }
    
    public int put(final int key) {
        int s = this.find(key);
        if (s < 0) {
            ++this.count;
            if (this.count >= this.rehashCount) {
                this.rehash();
                s = this.getSlot(key);
            }
            else {
                s = -s - 1;
            }
            this.keys[s] = key;
        }
        return s;
    }
    
    public void rehash() {
        final int newSize = this.keys.length << 1;
        final int[] oldKeys = this.keys;
        this.keys = new int[newSize];
        if (this.emptyVal != 0) {
            Arrays.fill(this.keys, this.emptyVal);
        }
        for (final int key : oldKeys) {
            if (key != this.emptyVal) {
                final int newSlot = this.getSlot(key);
                this.keys[newSlot] = key;
            }
        }
        this.rehashCount = newSize - (newSize >> 2);
    }
    
    public long ramBytesUsed() {
        return RamUsageEstimator.alignObjectSize(12 + RamUsageEstimator.NUM_BYTES_OBJECT_REF) + RamUsageEstimator.sizeOf(this.keys);
    }
}
