package org.tukaani.xz.check;

public class CRC64 extends Check
{
    private static final long[][] TABLE;
    private long crc;
    
    public CRC64() {
        this.crc = -1L;
        this.size = 8;
        this.name = "CRC64";
    }
    
    @Override
    public void update(final byte[] array, final int n, final int n2) {
        int n3;
        int i;
        for (n3 = n + n2, i = n; i < n3 - 3; i += 4) {
            final int n4 = (int)this.crc;
            this.crc = (CRC64.TABLE[3][(n4 & 0xFF) ^ (array[i] & 0xFF)] ^ CRC64.TABLE[2][(n4 >>> 8 & 0xFF) ^ (array[i + 1] & 0xFF)] ^ this.crc >>> 32 ^ CRC64.TABLE[1][(n4 >>> 16 & 0xFF) ^ (array[i + 2] & 0xFF)] ^ CRC64.TABLE[0][(n4 >>> 24 & 0xFF) ^ (array[i + 3] & 0xFF)]);
        }
        while (i < n3) {
            this.crc = (CRC64.TABLE[0][(array[i++] & 0xFF) ^ ((int)this.crc & 0xFF)] ^ this.crc >>> 8);
        }
    }
    
    @Override
    public byte[] finish() {
        final long n = ~this.crc;
        this.crc = -1L;
        final byte[] array = new byte[8];
        for (int i = 0; i < array.length; ++i) {
            array[i] = (byte)(n >> i * 8);
        }
        return array;
    }
    
    static {
        TABLE = new long[4][256];
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 256; ++j) {
                long n = (i == 0) ? j : CRC64.TABLE[i - 1][j];
                for (int k = 0; k < 8; ++k) {
                    if ((n & 0x1L) == 0x1L) {
                        n = (n >>> 1 ^ 0xC96C5795D7870F42L);
                    }
                    else {
                        n >>>= 1;
                    }
                }
                CRC64.TABLE[i][j] = n;
            }
        }
    }
}
