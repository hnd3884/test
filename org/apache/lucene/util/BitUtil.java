package org.apache.lucene.util;

public final class BitUtil
{
    private static final byte[] BYTE_COUNTS;
    private static final int[] BIT_LISTS;
    private static final long[] MAGIC;
    private static final short[] SHIFT;
    
    private BitUtil() {
    }
    
    @Deprecated
    public static int bitCount(final byte b) {
        return BitUtil.BYTE_COUNTS[b & 0xFF];
    }
    
    @Deprecated
    public static int bitList(final byte b) {
        return BitUtil.BIT_LISTS[b & 0xFF];
    }
    
    public static long pop_array(final long[] arr, final int wordOffset, final int numWords) {
        long popCount = 0L;
        for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
            popCount += Long.bitCount(arr[i]);
        }
        return popCount;
    }
    
    public static long pop_intersect(final long[] arr1, final long[] arr2, final int wordOffset, final int numWords) {
        long popCount = 0L;
        for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
            popCount += Long.bitCount(arr1[i] & arr2[i]);
        }
        return popCount;
    }
    
    public static long pop_union(final long[] arr1, final long[] arr2, final int wordOffset, final int numWords) {
        long popCount = 0L;
        for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
            popCount += Long.bitCount(arr1[i] | arr2[i]);
        }
        return popCount;
    }
    
    public static long pop_andnot(final long[] arr1, final long[] arr2, final int wordOffset, final int numWords) {
        long popCount = 0L;
        for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
            popCount += Long.bitCount(arr1[i] & ~arr2[i]);
        }
        return popCount;
    }
    
    public static long pop_xor(final long[] arr1, final long[] arr2, final int wordOffset, final int numWords) {
        long popCount = 0L;
        for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
            popCount += Long.bitCount(arr1[i] ^ arr2[i]);
        }
        return popCount;
    }
    
    public static int nextHighestPowerOfTwo(int v) {
        v = (--v | v >> 1);
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        return ++v;
    }
    
    public static long nextHighestPowerOfTwo(long v) {
        --v;
        v |= v >> 1;
        v |= v >> 2;
        v |= v >> 4;
        v |= v >> 8;
        v |= v >> 16;
        v |= v >> 32;
        ++v;
        return v;
    }
    
    public static long interleave(long v1, long v2) {
        v1 = ((v1 | v1 << BitUtil.SHIFT[4]) & BitUtil.MAGIC[4]);
        v1 = ((v1 | v1 << BitUtil.SHIFT[3]) & BitUtil.MAGIC[3]);
        v1 = ((v1 | v1 << BitUtil.SHIFT[2]) & BitUtil.MAGIC[2]);
        v1 = ((v1 | v1 << BitUtil.SHIFT[1]) & BitUtil.MAGIC[1]);
        v1 = ((v1 | v1 << BitUtil.SHIFT[0]) & BitUtil.MAGIC[0]);
        v2 = ((v2 | v2 << BitUtil.SHIFT[4]) & BitUtil.MAGIC[4]);
        v2 = ((v2 | v2 << BitUtil.SHIFT[3]) & BitUtil.MAGIC[3]);
        v2 = ((v2 | v2 << BitUtil.SHIFT[2]) & BitUtil.MAGIC[2]);
        v2 = ((v2 | v2 << BitUtil.SHIFT[1]) & BitUtil.MAGIC[1]);
        v2 = ((v2 | v2 << BitUtil.SHIFT[0]) & BitUtil.MAGIC[0]);
        return v2 << 1 | v1;
    }
    
    public static long deinterleave(long b) {
        b &= BitUtil.MAGIC[0];
        b = ((b ^ b >>> BitUtil.SHIFT[0]) & BitUtil.MAGIC[1]);
        b = ((b ^ b >>> BitUtil.SHIFT[1]) & BitUtil.MAGIC[2]);
        b = ((b ^ b >>> BitUtil.SHIFT[2]) & BitUtil.MAGIC[3]);
        b = ((b ^ b >>> BitUtil.SHIFT[3]) & BitUtil.MAGIC[4]);
        b = ((b ^ b >>> BitUtil.SHIFT[4]) & BitUtil.MAGIC[5]);
        return b;
    }
    
    public static final long flipFlop(final long b) {
        return (b & BitUtil.MAGIC[6]) >>> 1 | (b & BitUtil.MAGIC[0]) << 1;
    }
    
    public static int zigZagEncode(final int i) {
        return i >> 31 ^ i << 1;
    }
    
    public static long zigZagEncode(final long l) {
        return l >> 63 ^ l << 1;
    }
    
    public static int zigZagDecode(final int i) {
        return i >>> 1 ^ -(i & 0x1);
    }
    
    public static long zigZagDecode(final long l) {
        return l >>> 1 ^ -(l & 0x1L);
    }
    
    static {
        BYTE_COUNTS = new byte[] { 0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7, 4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8 };
        BIT_LISTS = new int[] { 0, 1, 2, 33, 3, 49, 50, 801, 4, 65, 66, 1057, 67, 1073, 1074, 17185, 5, 81, 82, 1313, 83, 1329, 1330, 21281, 84, 1345, 1346, 21537, 1347, 21553, 21554, 344865, 6, 97, 98, 1569, 99, 1585, 1586, 25377, 100, 1601, 1602, 25633, 1603, 25649, 25650, 410401, 101, 1617, 1618, 25889, 1619, 25905, 25906, 414497, 1620, 25921, 25922, 414753, 25923, 414769, 414770, 6636321, 7, 113, 114, 1825, 115, 1841, 1842, 29473, 116, 1857, 1858, 29729, 1859, 29745, 29746, 475937, 117, 1873, 1874, 29985, 1875, 30001, 30002, 480033, 1876, 30017, 30018, 480289, 30019, 480305, 480306, 7684897, 118, 1889, 1890, 30241, 1891, 30257, 30258, 484129, 1892, 30273, 30274, 484385, 30275, 484401, 484402, 7750433, 1893, 30289, 30290, 484641, 30291, 484657, 484658, 7754529, 30292, 484673, 484674, 7754785, 484675, 7754801, 7754802, 124076833, 8, 129, 130, 2081, 131, 2097, 2098, 33569, 132, 2113, 2114, 33825, 2115, 33841, 33842, 541473, 133, 2129, 2130, 34081, 2131, 34097, 34098, 545569, 2132, 34113, 34114, 545825, 34115, 545841, 545842, 8733473, 134, 2145, 2146, 34337, 2147, 34353, 34354, 549665, 2148, 34369, 34370, 549921, 34371, 549937, 549938, 8799009, 2149, 34385, 34386, 550177, 34387, 550193, 550194, 8803105, 34388, 550209, 550210, 8803361, 550211, 8803377, 8803378, 140854049, 135, 2161, 2162, 34593, 2163, 34609, 34610, 553761, 2164, 34625, 34626, 554017, 34627, 554033, 554034, 8864545, 2165, 34641, 34642, 554273, 34643, 554289, 554290, 8868641, 34644, 554305, 554306, 8868897, 554307, 8868913, 8868914, 141902625, 2166, 34657, 34658, 554529, 34659, 554545, 554546, 8872737, 34660, 554561, 554562, 8872993, 554563, 8873009, 8873010, 141968161, 34661, 554577, 554578, 8873249, 554579, 8873265, 8873266, 141972257, 554580, 8873281, 8873282, 141972513, 8873283, 141972529, 141972530, -2023406815 };
        MAGIC = new long[] { 6148914691236517205L, 3689348814741910323L, 1085102592571150095L, 71777214294589695L, 281470681808895L, 4294967295L, -6148914691236517206L };
        SHIFT = new short[] { 1, 2, 4, 8, 16 };
    }
}
