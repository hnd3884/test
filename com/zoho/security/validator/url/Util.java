package com.zoho.security.validator.url;

import java.util.BitSet;

public class Util
{
    public static BitSet convertCharArrayToBitSet(final char[] reservedArray) {
        if (reservedArray != null && reservedArray.length > 0) {
            final BitSet resultBitSet = new BitSet(256);
            for (final char c : reservedArray) {
                resultBitSet.set(c);
            }
            return resultBitSet;
        }
        return null;
    }
}
