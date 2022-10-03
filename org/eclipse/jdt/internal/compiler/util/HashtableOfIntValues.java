package org.eclipse.jdt.internal.compiler.util;

import org.eclipse.jdt.core.compiler.CharOperation;

public final class HashtableOfIntValues implements Cloneable
{
    public static final int NO_VALUE = Integer.MIN_VALUE;
    public char[][] keyTable;
    public int[] valueTable;
    public int elementSize;
    int threshold;
    
    public HashtableOfIntValues() {
        this(13);
    }
    
    public HashtableOfIntValues(final int size) {
        this.elementSize = 0;
        this.threshold = size;
        int extraRoom = (int)(size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new char[extraRoom][];
        this.valueTable = new int[extraRoom];
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HashtableOfIntValues result = (HashtableOfIntValues)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new char[length][];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new int[length];
        System.arraycopy(this.valueTable, 0, result.valueTable, 0, length);
        return result;
    }
    
    public boolean containsKey(final char[] key) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return true;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return false;
    }
    
    public int get(final char[] key) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                return this.valueTable[index];
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return Integer.MIN_VALUE;
    }
    
    public int put(final char[] key, final int value) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
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
    
    public int removeKey(final char[] key) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                final int value = this.valueTable[index];
                --this.elementSize;
                this.keyTable[index] = null;
                this.valueTable[index] = Integer.MIN_VALUE;
                this.rehash();
                return value;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return Integer.MIN_VALUE;
    }
    
    private void rehash() {
        final HashtableOfIntValues newHashtable = new HashtableOfIntValues(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            final char[] currentKey;
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
        String s = "";
        for (int i = 0, length = this.valueTable.length; i < length; ++i) {
            final char[] key;
            if ((key = this.keyTable[i]) != null) {
                s = String.valueOf(s) + new String(key) + " -> " + this.valueTable[i] + "\n";
            }
        }
        return s;
    }
}
