package org.eclipse.jdt.internal.compiler.codegen;

public class FloatCache
{
    private float[] keyTable;
    private int[] valueTable;
    private int elementSize;
    
    public FloatCache() {
        this(13);
    }
    
    public FloatCache(final int initialCapacity) {
        this.elementSize = 0;
        this.keyTable = new float[initialCapacity];
        this.valueTable = new int[initialCapacity];
    }
    
    public void clear() {
        int i = this.keyTable.length;
        while (--i >= 0) {
            this.keyTable[i] = 0.0f;
            this.valueTable[i] = 0;
        }
        this.elementSize = 0;
    }
    
    public boolean containsKey(final float key) {
        if (key == 0.0f) {
            for (int i = 0, max = this.elementSize; i < max; ++i) {
                if (this.keyTable[i] == 0.0f) {
                    final int value1 = Float.floatToIntBits(key);
                    final int value2 = Float.floatToIntBits(this.keyTable[i]);
                    if (value1 == Integer.MIN_VALUE && value2 == Integer.MIN_VALUE) {
                        return true;
                    }
                    if (value1 == 0 && value2 == 0) {
                        return true;
                    }
                }
            }
        }
        else {
            for (int i = 0, max = this.elementSize; i < max; ++i) {
                if (this.keyTable[i] == key) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public int put(final float key, final int value) {
        if (this.elementSize == this.keyTable.length) {
            System.arraycopy(this.keyTable, 0, this.keyTable = new float[this.elementSize * 2], 0, this.elementSize);
            System.arraycopy(this.valueTable, 0, this.valueTable = new int[this.elementSize * 2], 0, this.elementSize);
        }
        this.keyTable[this.elementSize] = key;
        this.valueTable[this.elementSize] = value;
        ++this.elementSize;
        return value;
    }
    
    public int putIfAbsent(final float key, final int value) {
        if (key == 0.0f) {
            for (int i = 0, max = this.elementSize; i < max; ++i) {
                if (this.keyTable[i] == 0.0f) {
                    final int value2 = Float.floatToIntBits(key);
                    final int value3 = Float.floatToIntBits(this.keyTable[i]);
                    if (value2 == Integer.MIN_VALUE && value3 == Integer.MIN_VALUE) {
                        return this.valueTable[i];
                    }
                    if (value2 == 0 && value3 == 0) {
                        return this.valueTable[i];
                    }
                }
            }
        }
        else {
            for (int i = 0, max = this.elementSize; i < max; ++i) {
                if (this.keyTable[i] == key) {
                    return this.valueTable[i];
                }
            }
        }
        if (this.elementSize == this.keyTable.length) {
            System.arraycopy(this.keyTable, 0, this.keyTable = new float[this.elementSize * 2], 0, this.elementSize);
            System.arraycopy(this.valueTable, 0, this.valueTable = new int[this.elementSize * 2], 0, this.elementSize);
        }
        this.keyTable[this.elementSize] = key;
        this.valueTable[this.elementSize] = value;
        ++this.elementSize;
        return -value;
    }
    
    @Override
    public String toString() {
        final int max = this.elementSize;
        final StringBuffer buf = new StringBuffer();
        buf.append("{");
        for (int i = 0; i < max; ++i) {
            if (this.keyTable[i] != 0.0f || (this.keyTable[i] == 0.0f && this.valueTable[i] != 0)) {
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
