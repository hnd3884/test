package org.eclipse.jdt.internal.compiler.util;

import org.eclipse.jdt.core.compiler.CharOperation;

public final class HashtableOfObject implements Cloneable
{
    public char[][] keyTable;
    public Object[] valueTable;
    public int elementSize;
    int threshold;
    
    public HashtableOfObject() {
        this(13);
    }
    
    public HashtableOfObject(final int size) {
        this.elementSize = 0;
        this.threshold = size;
        int extraRoom = (int)(size * 1.75f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new char[extraRoom][];
        this.valueTable = new Object[extraRoom];
    }
    
    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = null;
            this.valueTable[i] = null;
        }
        this.elementSize = 0;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final HashtableOfObject result = (HashtableOfObject)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new char[length][];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new Object[length];
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
    
    public Object get(final char[] key) {
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
        return null;
    }
    
    public Object put(final char[] key, final Object value) {
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
    
    public void putUnsafely(final char[] key, final Object value) {
        final int length = this.keyTable.length;
        int index;
        for (index = CharOperation.hashCode(key) % length; this.keyTable[index] != null; index = 0) {
            if (++index == length) {}
        }
        this.keyTable[index] = key;
        this.valueTable[index] = value;
        if (++this.elementSize > this.threshold) {
            this.rehash();
        }
    }
    
    public Object removeKey(final char[] key) {
        final int length = this.keyTable.length;
        int index = CharOperation.hashCode(key) % length;
        final int keyLength = key.length;
        char[] currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.length == keyLength && CharOperation.equals(currentKey, key)) {
                final Object value = this.valueTable[index];
                --this.elementSize;
                this.keyTable[index] = null;
                this.valueTable[index] = null;
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
        final HashtableOfObject newHashtable = new HashtableOfObject(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            final char[] currentKey;
            if ((currentKey = this.keyTable[i]) != null) {
                newHashtable.putUnsafely(currentKey, this.valueTable[i]);
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
                s = String.valueOf(s) + new String(this.keyTable[i]) + " -> " + object.toString() + "\n";
            }
        }
        return s;
    }
}
