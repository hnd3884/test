package org.eclipse.jdt.internal.compiler.codegen;

public class LongCache
{
    public long[] keyTable;
    public int[] valueTable;
    int elementSize;
    int threshold;
    
    public LongCache() {
        this(13);
    }
    
    public LongCache(final int initialCapacity) {
        this.elementSize = 0;
        this.threshold = (int)(initialCapacity * 0.66);
        this.keyTable = new long[initialCapacity];
        this.valueTable = new int[initialCapacity];
    }
    
    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = 0L;
            this.valueTable[i] = 0;
        }
        this.elementSize = 0;
    }
    
    public boolean containsKey(final long key) {
        int index = this.hash(key);
        final int length = this.keyTable.length;
        while (this.keyTable[index] != 0L || (this.keyTable[index] == 0L && this.valueTable[index] != 0)) {
            if (this.keyTable[index] == key) {
                return true;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return false;
    }
    
    public int hash(final long key) {
        return ((int)key & Integer.MAX_VALUE) % this.keyTable.length;
    }
    
    public int put(final long key, final int value) {
        int index = this.hash(key);
        final int length = this.keyTable.length;
        while (this.keyTable[index] != 0L || (this.keyTable[index] == 0L && this.valueTable[index] != 0)) {
            if (this.keyTable[index] == key) {
                return this.valueTable[index] = value;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return value;
    }
    
    public int putIfAbsent(final long key, final int value) {
        int index = this.hash(key);
        final int length = this.keyTable.length;
        while (this.keyTable[index] != 0L || (this.keyTable[index] == 0L && this.valueTable[index] != 0)) {
            if (this.keyTable[index] == key) {
                return this.valueTable[index];
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
        return -value;
    }
    
    private void rehash() {
        final LongCache newHashtable = new LongCache(this.keyTable.length * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            final long key = this.keyTable[i];
            final int value = this.valueTable[i];
            if (key != 0L || (key == 0L && value != 0)) {
                newHashtable.put(key, value);
            }
        }
        this.keyTable = newHashtable.keyTable;
        this.valueTable = newHashtable.valueTable;
        this.threshold = newHashtable.threshold;
    }
    
    public int size() {
        return this.elementSize;
    }
    
    @Override
    public String toString() {
        final int max = this.size();
        final StringBuffer buf = new StringBuffer();
        buf.append("{");
        for (int i = 0; i < max; ++i) {
            if (this.keyTable[i] != 0L || (this.keyTable[i] == 0L && this.valueTable[i] != 0)) {
                buf.append(this.keyTable[i]).append("->").append(this.valueTable[i]);
            }
            if (i < max) {
                buf.append(", ");
            }
        }
        buf.append("}");
        return buf.toString();
    }
}
