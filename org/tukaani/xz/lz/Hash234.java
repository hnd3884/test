package org.tukaani.xz.lz;

import org.tukaani.xz.ArrayCache;

final class Hash234 extends CRC32Hash
{
    private static final int HASH_2_SIZE = 1024;
    private static final int HASH_2_MASK = 1023;
    private static final int HASH_3_SIZE = 65536;
    private static final int HASH_3_MASK = 65535;
    private final int hash4Mask;
    private final int[] hash2Table;
    private final int[] hash3Table;
    private final int[] hash4Table;
    private final int hash4Size;
    private int hash2Value;
    private int hash3Value;
    private int hash4Value;
    
    static int getHash4Size(final int n) {
        final int n2 = n - 1;
        final int n3 = n2 | n2 >>> 1;
        final int n4 = n3 | n3 >>> 2;
        final int n5 = n4 | n4 >>> 4;
        int n6 = (n5 | n5 >>> 8) >>> 1 | 0xFFFF;
        if (n6 > 16777216) {
            n6 >>>= 1;
        }
        return n6 + 1;
    }
    
    static int getMemoryUsage(final int n) {
        return (66560 + getHash4Size(n)) / 256 + 4;
    }
    
    Hash234(final int n, final ArrayCache arrayCache) {
        this.hash2Value = 0;
        this.hash3Value = 0;
        this.hash4Value = 0;
        this.hash2Table = arrayCache.getIntArray(1024, true);
        this.hash3Table = arrayCache.getIntArray(65536, true);
        this.hash4Size = getHash4Size(n);
        this.hash4Table = arrayCache.getIntArray(this.hash4Size, true);
        this.hash4Mask = this.hash4Size - 1;
    }
    
    void putArraysToCache(final ArrayCache arrayCache) {
        arrayCache.putArray(this.hash4Table);
        arrayCache.putArray(this.hash3Table);
        arrayCache.putArray(this.hash2Table);
    }
    
    void calcHashes(final byte[] array, final int n) {
        final int n2 = Hash234.crcTable[array[n] & 0xFF] ^ (array[n + 1] & 0xFF);
        this.hash2Value = (n2 & 0x3FF);
        final int n3 = n2 ^ (array[n + 2] & 0xFF) << 8;
        this.hash3Value = (n3 & 0xFFFF);
        this.hash4Value = ((n3 ^ Hash234.crcTable[array[n + 3] & 0xFF] << 5) & this.hash4Mask);
    }
    
    int getHash2Pos() {
        return this.hash2Table[this.hash2Value];
    }
    
    int getHash3Pos() {
        return this.hash3Table[this.hash3Value];
    }
    
    int getHash4Pos() {
        return this.hash4Table[this.hash4Value];
    }
    
    void updateTables(final int n) {
        this.hash2Table[this.hash2Value] = n;
        this.hash3Table[this.hash3Value] = n;
        this.hash4Table[this.hash4Value] = n;
    }
    
    void normalize(final int n) {
        LZEncoder.normalize(this.hash2Table, 1024, n);
        LZEncoder.normalize(this.hash3Table, 65536, n);
        LZEncoder.normalize(this.hash4Table, this.hash4Size, n);
    }
}
