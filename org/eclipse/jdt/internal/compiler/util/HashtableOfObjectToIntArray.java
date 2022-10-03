package org.eclipse.jdt.internal.compiler.util;

public final class HashtableOfObjectToIntArray implements Cloneable
{
    public Object[] keyTable;
    public int[][] valueTable;
    public int elementSize;
    int threshold;
    
    public HashtableOfObjectToIntArray() {
        this(13);
    }
    
    public HashtableOfObjectToIntArray(final int size) {
        this.elementSize = 0;
        this.threshold = size;
        int extraRoom = (int)(size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new Object[extraRoom];
        this.valueTable = new int[extraRoom][];
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HashtableOfObjectToIntArray result = (HashtableOfObjectToIntArray)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new Object[length];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new int[length][];
        System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
        return result;
    }
    
    public boolean containsKey(final Object key) {
        final int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        Object currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                return true;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return false;
    }
    
    public int[] get(final Object key) {
        final int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        Object currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                return this.valueTable[index];
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return null;
    }
    
    public void keysToArray(final Object[] array) {
        int index = 0;
        for (int i = 0, length = this.keyTable.length; i < length; ++i) {
            if (this.keyTable[i] != null) {
                array[index++] = this.keyTable[i];
            }
        }
    }
    
    public int[] put(final Object key, final int[] value) {
        final int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        Object currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
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
    
    public int[] removeKey(final Object key) {
        final int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        Object currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                final int[] value = this.valueTable[index];
                --this.elementSize;
                this.keyTable[index] = null;
                this.rehash();
                return value;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return null;
    }
    
    private void rehash() {
        final HashtableOfObjectToIntArray newHashtable = new HashtableOfObjectToIntArray(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            final Object currentKey;
            if ((currentKey = this.keyTable[i]) != null) {
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
        final StringBuffer buffer = new StringBuffer();
        for (int i = 0, length = this.keyTable.length; i < length; ++i) {
            final Object key;
            if ((key = this.keyTable[i]) != null) {
                buffer.append(key).append(" -> ");
                final int[] ints = this.valueTable[i];
                buffer.append('[');
                if (ints != null) {
                    for (int j = 0, max = ints.length; j < max; ++j) {
                        if (j > 0) {
                            buffer.append(',');
                        }
                        buffer.append(ints[j]);
                    }
                }
                buffer.append("]\n");
            }
        }
        return String.valueOf(buffer);
    }
}
