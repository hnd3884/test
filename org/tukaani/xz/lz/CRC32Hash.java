package org.tukaani.xz.lz;

class CRC32Hash
{
    private static final int CRC32_POLY = -306674912;
    static final int[] crcTable;
    
    static {
        crcTable = new int[256];
        for (int i = 0; i < 256; ++i) {
            int n = i;
            for (int j = 0; j < 8; ++j) {
                if ((n & 0x1) != 0x0) {
                    n = (n >>> 1 ^ 0xEDB88320);
                }
                else {
                    n >>>= 1;
                }
            }
            CRC32Hash.crcTable[i] = n;
        }
    }
}
