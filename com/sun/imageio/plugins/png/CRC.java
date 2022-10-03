package com.sun.imageio.plugins.png;

class CRC
{
    private static int[] crcTable;
    private int crc;
    
    public CRC() {
        this.crc = -1;
    }
    
    public void reset() {
        this.crc = -1;
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.crc = (CRC.crcTable[(this.crc ^ array[n + i]) & 0xFF] ^ this.crc >>> 8);
        }
    }
    
    public void update(final int n) {
        this.crc = (CRC.crcTable[(this.crc ^ n) & 0xFF] ^ this.crc >>> 8);
    }
    
    public int getValue() {
        return ~this.crc;
    }
    
    static {
        CRC.crcTable = new int[256];
        for (int i = 0; i < 256; ++i) {
            int n = i;
            for (int j = 0; j < 8; ++j) {
                if ((n & 0x1) == 0x1) {
                    n = (0xEDB88320 ^ n >>> 1);
                }
                else {
                    n >>>= 1;
                }
                CRC.crcTable[i] = n;
            }
        }
    }
}
