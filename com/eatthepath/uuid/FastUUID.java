package com.eatthepath.uuid;

import java.util.Arrays;
import java.util.UUID;

public class FastUUID
{
    private static final boolean USE_JDK_UUID_TO_STRING;
    private static final int UUID_STRING_LENGTH = 36;
    private static final char[] HEX_DIGITS;
    private static final long[] HEX_VALUES;
    
    private FastUUID() {
    }
    
    public static UUID parseUUID(final CharSequence uuidSequence) {
        if (uuidSequence.length() != 36 || uuidSequence.charAt(8) != '-' || uuidSequence.charAt(13) != '-' || uuidSequence.charAt(18) != '-' || uuidSequence.charAt(23) != '-') {
            throw new IllegalArgumentException("Illegal UUID string: " + (Object)uuidSequence);
        }
        long mostSignificantBits = getHexValueForChar(uuidSequence.charAt(0)) << 60;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(1)) << 56;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(2)) << 52;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(3)) << 48;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(4)) << 44;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(5)) << 40;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(6)) << 36;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(7)) << 32;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(9)) << 28;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(10)) << 24;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(11)) << 20;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(12)) << 16;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(14)) << 12;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(15)) << 8;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(16)) << 4;
        mostSignificantBits |= getHexValueForChar(uuidSequence.charAt(17));
        long leastSignificantBits = getHexValueForChar(uuidSequence.charAt(19)) << 60;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(20)) << 56;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(21)) << 52;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(22)) << 48;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(24)) << 44;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(25)) << 40;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(26)) << 36;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(27)) << 32;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(28)) << 28;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(29)) << 24;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(30)) << 20;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(31)) << 16;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(32)) << 12;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(33)) << 8;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(34)) << 4;
        leastSignificantBits |= getHexValueForChar(uuidSequence.charAt(35));
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
    
    public static String toString(final UUID uuid) {
        if (FastUUID.USE_JDK_UUID_TO_STRING) {
            return uuid.toString();
        }
        final long mostSignificantBits = uuid.getMostSignificantBits();
        final long leastSignificantBits = uuid.getLeastSignificantBits();
        final char[] uuidChars = { FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF000000000000000L) >>> 60)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF00000000000000L) >>> 56)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF0000000000000L) >>> 52)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF000000000000L) >>> 48)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF00000000000L) >>> 44)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF0000000000L) >>> 40)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF000000000L) >>> 36)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF00000000L) >>> 32)], '-', FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF0000000L) >>> 28)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF000000L) >>> 24)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF00000L) >>> 20)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF0000L) >>> 16)], '-', FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF000L) >>> 12)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF00L) >>> 8)], FastUUID.HEX_DIGITS[(int)((mostSignificantBits & 0xF0L) >>> 4)], FastUUID.HEX_DIGITS[(int)(mostSignificantBits & 0xFL)], '-', FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF000000000000000L) >>> 60)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF00000000000000L) >>> 56)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF0000000000000L) >>> 52)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF000000000000L) >>> 48)], '-', FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF00000000000L) >>> 44)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF0000000000L) >>> 40)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF000000000L) >>> 36)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF00000000L) >>> 32)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF0000000L) >>> 28)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF000000L) >>> 24)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF00000L) >>> 20)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF0000L) >>> 16)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF000L) >>> 12)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF00L) >>> 8)], FastUUID.HEX_DIGITS[(int)((leastSignificantBits & 0xF0L) >>> 4)], FastUUID.HEX_DIGITS[(int)(leastSignificantBits & 0xFL)] };
        return new String(uuidChars);
    }
    
    static long getHexValueForChar(final char c) {
        try {
            if (FastUUID.HEX_VALUES[c] < 0L) {
                throw new IllegalArgumentException("Illegal hexadecimal digit: " + c);
            }
        }
        catch (final ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Illegal hexadecimal digit: " + c);
        }
        return FastUUID.HEX_VALUES[c];
    }
    
    static {
        int majorVersion = 0;
        try {
            majorVersion = Integer.parseInt(System.getProperty("java.specification.version"));
        }
        catch (final NumberFormatException ex) {}
        USE_JDK_UUID_TO_STRING = (majorVersion >= 9);
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        Arrays.fill(HEX_VALUES = new long[128], -1L);
        FastUUID.HEX_VALUES[48] = 0L;
        FastUUID.HEX_VALUES[49] = 1L;
        FastUUID.HEX_VALUES[50] = 2L;
        FastUUID.HEX_VALUES[51] = 3L;
        FastUUID.HEX_VALUES[52] = 4L;
        FastUUID.HEX_VALUES[53] = 5L;
        FastUUID.HEX_VALUES[54] = 6L;
        FastUUID.HEX_VALUES[55] = 7L;
        FastUUID.HEX_VALUES[56] = 8L;
        FastUUID.HEX_VALUES[57] = 9L;
        FastUUID.HEX_VALUES[97] = 10L;
        FastUUID.HEX_VALUES[98] = 11L;
        FastUUID.HEX_VALUES[99] = 12L;
        FastUUID.HEX_VALUES[100] = 13L;
        FastUUID.HEX_VALUES[101] = 14L;
        FastUUID.HEX_VALUES[102] = 15L;
        FastUUID.HEX_VALUES[65] = 10L;
        FastUUID.HEX_VALUES[66] = 11L;
        FastUUID.HEX_VALUES[67] = 12L;
        FastUUID.HEX_VALUES[68] = 13L;
        FastUUID.HEX_VALUES[69] = 14L;
        FastUUID.HEX_VALUES[70] = 15L;
    }
}
