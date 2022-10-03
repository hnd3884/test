package org.eclipse.jdt.internal.compiler.util;

public final class SimpleLookupTable implements Cloneable
{
    public Object[] keyTable;
    public Object[] valueTable;
    public int elementSize;
    public int threshold;
    
    public SimpleLookupTable() {
        this(13);
    }
    
    public SimpleLookupTable(final int size) {
        this.elementSize = 0;
        this.threshold = size;
        int extraRoom = (int)(size * 1.5f);
        if (this.threshold == extraRoom) {
            ++extraRoom;
        }
        this.keyTable = new Object[extraRoom];
        this.valueTable = new Object[extraRoom];
    }
    
    public Object clone() throws CloneNotSupportedException {
        final SimpleLookupTable result = (SimpleLookupTable)super.clone();
        result.elementSize = this.elementSize;
        result.threshold = this.threshold;
        int length = this.keyTable.length;
        result.keyTable = new Object[length];
        System.arraycopy(this.keyTable, 0, result.keyTable, 0, length);
        length = this.valueTable.length;
        result.valueTable = new Object[length];
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
    
    public Object get(final Object key) {
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
    
    public Object getKey(final Object key) {
        final int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        Object currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                return currentKey;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return key;
    }
    
    public Object keyForValue(final Object valueToMatch) {
        if (valueToMatch != null) {
            for (int i = 0, l = this.keyTable.length; i < l; ++i) {
                if (this.keyTable[i] != null && valueToMatch.equals(this.valueTable[i])) {
                    return this.keyTable[i];
                }
            }
        }
        return null;
    }
    
    public Object put(final Object key, final Object value) {
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
    
    public Object removeKey(final Object key) {
        final int length = this.keyTable.length;
        int index = (key.hashCode() & Integer.MAX_VALUE) % length;
        Object currentKey;
        while ((currentKey = this.keyTable[index]) != null) {
            if (currentKey.equals(key)) {
                --this.elementSize;
                final Object oldValue = this.valueTable[index];
                this.keyTable[index] = null;
                this.valueTable[index] = null;
                if (this.keyTable[(index + 1 == length) ? 0 : (index + 1)] != null) {
                    this.rehash();
                }
                return oldValue;
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return null;
    }
    
    public void removeValue(final Object valueToRemove) {
        boolean rehash = false;
        for (int i = 0, l = this.valueTable.length; i < l; ++i) {
            final Object value = this.valueTable[i];
            if (value != null && value.equals(valueToRemove)) {
                --this.elementSize;
                this.keyTable[i] = null;
                this.valueTable[i] = null;
                if (!rehash && this.keyTable[(i + 1 == l) ? 0 : (i + 1)] != null) {
                    rehash = true;
                }
            }
        }
        if (rehash) {
            this.rehash();
        }
    }
    
    private void rehash() {
        final SimpleLookupTable newLookupTable = new SimpleLookupTable(this.elementSize * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            final Object currentKey;
            if ((currentKey = this.keyTable[i]) != null) {
                newLookupTable.put(currentKey, this.valueTable[i]);
            }
        }
        this.keyTable = newLookupTable.keyTable;
        this.valueTable = newLookupTable.valueTable;
        this.elementSize = newLookupTable.elementSize;
        this.threshold = newLookupTable.threshold;
    }
    
    @Override
    public String toString() {
        String s = "";
        for (int i = 0, l = this.valueTable.length; i < l; ++i) {
            final Object object;
            if ((object = this.valueTable[i]) != null) {
                s = String.valueOf(s) + this.keyTable[i].toString() + " -> " + object.toString() + "\n";
            }
        }
        return s;
    }
}
