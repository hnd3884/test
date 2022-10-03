package org.glassfish.jersey.internal.guava;

import java.util.Arrays;

final class Ints
{
    public static final int MAX_POWER_OF_TWO = 1073741824;
    private static final byte[] asciiDigits;
    
    private Ints() {
    }
    
    public static int saturatedCast(final long value) {
        if (value > 2147483647L) {
            return Integer.MAX_VALUE;
        }
        if (value < -2147483648L) {
            return Integer.MIN_VALUE;
        }
        return (int)value;
    }
    
    static {
        Arrays.fill(asciiDigits = new byte[128], (byte)(-1));
        for (int i = 0; i <= 9; ++i) {
            Ints.asciiDigits[48 + i] = (byte)i;
        }
        for (int i = 0; i <= 26; ++i) {
            Ints.asciiDigits[65 + i] = (byte)(10 + i);
            Ints.asciiDigits[97 + i] = (byte)(10 + i);
        }
    }
}
