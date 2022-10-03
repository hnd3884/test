package org.tukaani.xz.simple;

public final class PowerPC implements SimpleFilter
{
    private final boolean isEncoder;
    private int pos;
    
    public PowerPC(final boolean isEncoder, final int pos) {
        this.isEncoder = isEncoder;
        this.pos = pos;
    }
    
    @Override
    public int code(final byte[] array, final int n, final int n2) {
        int n3;
        int i;
        for (n3 = n + n2 - 4, i = n; i <= n3; i += 4) {
            if ((array[i] & 0xFC) == 0x48 && (array[i + 3] & 0x3) == 0x1) {
                final int n4 = (array[i] & 0x3) << 24 | (array[i + 1] & 0xFF) << 16 | (array[i + 2] & 0xFF) << 8 | (array[i + 3] & 0xFC);
                int n5;
                if (this.isEncoder) {
                    n5 = n4 + (this.pos + i - n);
                }
                else {
                    n5 = n4 - (this.pos + i - n);
                }
                array[i] = (byte)(0x48 | (n5 >>> 24 & 0x3));
                array[i + 1] = (byte)(n5 >>> 16);
                array[i + 2] = (byte)(n5 >>> 8);
                array[i + 3] = (byte)((array[i + 3] & 0x3) | n5);
            }
        }
        final int n6 = i - n;
        this.pos += n6;
        return n6;
    }
}
