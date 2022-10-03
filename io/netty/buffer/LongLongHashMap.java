package io.netty.buffer;

final class LongLongHashMap
{
    private static final int MASK_TEMPLATE = -2;
    private int mask;
    private long[] array;
    private int maxProbe;
    private long zeroVal;
    private final long emptyVal;
    
    LongLongHashMap(final long emptyVal) {
        this.emptyVal = emptyVal;
        this.zeroVal = emptyVal;
        final int initialSize = 32;
        this.array = new long[initialSize];
        this.mask = initialSize - 1;
        this.computeMaskAndProbe();
    }
    
    public long put(final long key, final long value) {
        if (key == 0L) {
            final long prev = this.zeroVal;
            this.zeroVal = value;
            return prev;
        }
        int index = 0;
        int i = 0;
        long existing = 0L;
    Label_0062:
        while (true) {
            index = this.index(key);
            for (i = 0; i < this.maxProbe; ++i) {
                existing = this.array[index];
                if (existing == key || existing == 0L) {
                    break Label_0062;
                }
                index = (index + 2 & this.mask);
            }
            this.expand();
        }
        long prev2 = (existing == 0L) ? this.emptyVal : this.array[index + 1];
        this.array[index] = key;
        this.array[index + 1] = value;
        while (i < this.maxProbe) {
            index = (index + 2 & this.mask);
            if (this.array[index] == key) {
                this.array[index] = 0L;
                prev2 = this.array[index + 1];
                break;
            }
            ++i;
        }
        return prev2;
    }
    
    public void remove(final long key) {
        if (key == 0L) {
            this.zeroVal = this.emptyVal;
            return;
        }
        int index = this.index(key);
        for (int i = 0; i < this.maxProbe; ++i) {
            final long existing = this.array[index];
            if (existing == key) {
                this.array[index] = 0L;
                break;
            }
            index = (index + 2 & this.mask);
        }
    }
    
    public long get(final long key) {
        if (key == 0L) {
            return this.zeroVal;
        }
        int index = this.index(key);
        for (int i = 0; i < this.maxProbe; ++i) {
            final long existing = this.array[index];
            if (existing == key) {
                return this.array[index + 1];
            }
            index = (index + 2 & this.mask);
        }
        return this.emptyVal;
    }
    
    private int index(long key) {
        key ^= key >>> 33;
        key *= -49064778989728563L;
        key ^= key >>> 33;
        key *= -4265267296055464877L;
        key ^= key >>> 33;
        return (int)key & this.mask;
    }
    
    private void expand() {
        final long[] prev = this.array;
        this.array = new long[prev.length * 2];
        this.computeMaskAndProbe();
        for (int i = 0; i < prev.length; i += 2) {
            final long key = prev[i];
            if (key != 0L) {
                final long val = prev[i + 1];
                this.put(key, val);
            }
        }
    }
    
    private void computeMaskAndProbe() {
        final int length = this.array.length;
        this.mask = (length - 1 & 0xFFFFFFFE);
        this.maxProbe = (int)Math.log(length);
    }
}
