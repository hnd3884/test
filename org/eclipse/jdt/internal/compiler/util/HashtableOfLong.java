package org.eclipse.jdt.internal.compiler.util;

public final class HashtableOfLong
{
    public long[] keyTable;
    public Object[] valueTable;
    public int elementSize;
    int threshold;
    
    public HashtableOfLong() {
        this(13);
    }
    
    public HashtableOfLong(final int size) {
        this.elementSize = 0;
        this.threshold = size;
        int extraRoom = (int)(size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new long[extraRoom];
        this.valueTable = new Object[extraRoom];
    }
    
    public boolean containsKey(final long key) {
        final int length = this.keyTable.length;
        int index = (int)(key >>> 32) % length;
        long currentKey;
        while ((currentKey = this.keyTable[index]) != 0L) {
            if (currentKey == key) {
                return true;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return false;
    }
    
    public Object get(final long key) {
        final int length = this.keyTable.length;
        int index = (int)(key >>> 32) % length;
        long currentKey;
        while ((currentKey = this.keyTable[index]) != 0L) {
            if (currentKey == key) {
                return this.valueTable[index];
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return null;
    }
    
    public Object put(final long key, final Object value) {
        final int length = this.keyTable.length;
        int index = (int)(key >>> 32) % length;
        long currentKey;
        while ((currentKey = this.keyTable[index]) != 0L) {
            if (currentKey == key) {
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
    
    private void rehash() {
        final HashtableOfLong newHashtable = new HashtableOfLong(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            final long currentKey;
            if ((currentKey = this.keyTable[i]) != 0L) {
                newHashtable.put(currentKey, this.valueTable[i]);
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
        String s = "";
        for (int i = 0, length = this.valueTable.length; i < length; ++i) {
            final Object object;
            if ((object = this.valueTable[i]) != null) {
                s = String.valueOf(s) + this.keyTable[i] + " -> " + object.toString() + "\n";
            }
        }
        return s;
    }
}
