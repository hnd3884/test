package org.tukaani.xz.simple;

public final class ARM implements SimpleFilter
{
    private final boolean isEncoder;
    private int pos;
    
    public ARM(final boolean isEncoder, final int n) {
        this.isEncoder = isEncoder;
        this.pos = n + 8;
    }
    
    @Override
    public int code(final byte[] array, final int n, final int n2) {
        int n3;
        int i;
        for (n3 = n + n2 - 4, i = n; i <= n3; i += 4) {
            if ((array[i + 3] & 0xFF) == 0xEB) {
                final int n4 = ((array[i + 2] & 0xFF) << 16 | (array[i + 1] & 0xFF) << 8 | (array[i] & 0xFF)) << 2;
                int n5;
                if (this.isEncoder) {
                    n5 = n4 + (this.pos + i - n);
                }
                else {
                    n5 = n4 - (this.pos + i - n);
                }
                final int n6 = n5 >>> 2;
                array[i + 2] = (byte)(n6 >>> 16);
                array[i + 1] = (byte)(n6 >>> 8);
                array[i] = (byte)n6;
            }
        }
        final int n7 = i - n;
        this.pos += n7;
        return n7;
    }
}
