package org.apache.commons.collections4.trie;

import java.io.Serializable;
import java.util.Comparator;

public abstract class KeyAnalyzer<K> implements Comparator<K>, Serializable
{
    private static final long serialVersionUID = -20497563720380683L;
    public static final int NULL_BIT_KEY = -1;
    public static final int EQUAL_BIT_KEY = -2;
    public static final int OUT_OF_BOUNDS_BIT_KEY = -3;
    
    static boolean isOutOfBoundsIndex(final int bitIndex) {
        return bitIndex == -3;
    }
    
    static boolean isEqualBitKey(final int bitIndex) {
        return bitIndex == -2;
    }
    
    static boolean isNullBitKey(final int bitIndex) {
        return bitIndex == -1;
    }
    
    static boolean isValidBitIndex(final int bitIndex) {
        return bitIndex >= 0;
    }
    
    public abstract int bitsPerElement();
    
    public abstract int lengthInBits(final K p0);
    
    public abstract boolean isBitSet(final K p0, final int p1, final int p2);
    
    public abstract int bitIndex(final K p0, final int p1, final int p2, final K p3, final int p4, final int p5);
    
    public abstract boolean isPrefix(final K p0, final int p1, final int p2, final K p3);
    
    @Override
    public int compare(final K o1, final K o2) {
        if (o1 == null) {
            return (o2 == null) ? 0 : -1;
        }
        if (o2 == null) {
            return 1;
        }
        return ((Comparable)o1).compareTo(o2);
    }
}
