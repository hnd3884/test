package org.eclipse.jdt.internal.compiler.codegen;

public class ObjectCache
{
    public Object[] keyTable;
    public int[] valueTable;
    int elementSize;
    int threshold;
    
    public ObjectCache() {
        this(13);
    }
    
    public ObjectCache(final int initialCapacity) {
        this.elementSize = 0;
        this.threshold = (int)(initialCapacity * 0.66f);
        this.keyTable = new Object[initialCapacity];
        this.valueTable = new int[initialCapacity];
    }
    
    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = null;
            this.valueTable[i] = 0;
        }
        this.elementSize = 0;
    }
    
    public boolean containsKey(final Object key) {
        int index = this.hashCode(key);
        final int length = this.keyTable.length;
        while (this.keyTable[index] != null) {
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
    
    public int get(final Object key) {
        int index = this.hashCode(key);
        final int length = this.keyTable.length;
        while (this.keyTable[index] != null) {
            if (this.keyTable[index] == key) {
                return this.valueTable[index];
            }
            if (++index != length) {
                continue;
            }
            index = 0;
        }
        return -1;
    }
    
    public int hashCode(final Object key) {
        return (key.hashCode() & Integer.MAX_VALUE) % this.keyTable.length;
    }
    
    public int put(final Object key, final int value) {
        int index = this.hashCode(key);
        final int length = this.keyTable.length;
        while (this.keyTable[index] != null) {
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
    
    private void rehash() {
        final ObjectCache newHashtable = new ObjectCache(this.keyTable.length * 2);
        int i = this.keyTable.length;
        while (--i >= 0) {
            if (this.keyTable[i] != null) {
                newHashtable.put(this.keyTable[i], this.valueTable[i]);
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
            if (this.keyTable[i] != null) {
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
