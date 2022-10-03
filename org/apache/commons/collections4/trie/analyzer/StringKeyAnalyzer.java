package org.apache.commons.collections4.trie.analyzer;

import org.apache.commons.collections4.trie.KeyAnalyzer;

public class StringKeyAnalyzer extends KeyAnalyzer<String>
{
    private static final long serialVersionUID = -7032449491269434877L;
    public static final StringKeyAnalyzer INSTANCE;
    public static final int LENGTH = 16;
    private static final int MSB = 32768;
    
    private static int mask(final int bit) {
        return 32768 >>> bit;
    }
    
    @Override
    public int bitsPerElement() {
        return 16;
    }
    
    @Override
    public int lengthInBits(final String key) {
        return (key != null) ? (key.length() * 16) : 0;
    }
    
    @Override
    public int bitIndex(final String key, final int offsetInBits, final int lengthInBits, final String other, final int otherOffsetInBits, final int otherLengthInBits) {
        boolean allNull = true;
        if (offsetInBits % 16 != 0 || otherOffsetInBits % 16 != 0 || lengthInBits % 16 != 0 || otherLengthInBits % 16 != 0) {
            throw new IllegalArgumentException("The offsets and lengths must be at Character boundaries");
        }
        final int beginIndex1 = offsetInBits / 16;
        final int beginIndex2 = otherOffsetInBits / 16;
        final int endIndex1 = beginIndex1 + lengthInBits / 16;
        final int endIndex2 = beginIndex2 + otherLengthInBits / 16;
        final int length = Math.max(endIndex1, endIndex2);
        char k = '\0';
        char f = '\0';
        for (int i = 0; i < length; ++i) {
            final int index1 = beginIndex1 + i;
            final int index2 = beginIndex2 + i;
            if (index1 >= endIndex1) {
                k = '\0';
            }
            else {
                k = key.charAt(index1);
            }
            if (other == null || index2 >= endIndex2) {
                f = '\0';
            }
            else {
                f = other.charAt(index2);
            }
            if (k != f) {
                final int x = k ^ f;
                return i * 16 + Integer.numberOfLeadingZeros(x) - 16;
            }
            if (k != '\0') {
                allNull = false;
            }
        }
        if (allNull) {
            return -1;
        }
        return -2;
    }
    
    @Override
    public boolean isBitSet(final String key, final int bitIndex, final int lengthInBits) {
        if (key == null || bitIndex >= lengthInBits) {
            return false;
        }
        final int index = bitIndex / 16;
        final int bit = bitIndex % 16;
        return (key.charAt(index) & mask(bit)) != 0x0;
    }
    
    @Override
    public boolean isPrefix(final String prefix, final int offsetInBits, final int lengthInBits, final String key) {
        if (offsetInBits % 16 != 0 || lengthInBits % 16 != 0) {
            throw new IllegalArgumentException("Cannot determine prefix outside of Character boundaries");
        }
        final String s1 = prefix.substring(offsetInBits / 16, lengthInBits / 16);
        return key.startsWith(s1);
    }
    
    static {
        INSTANCE = new StringKeyAnalyzer();
    }
}
