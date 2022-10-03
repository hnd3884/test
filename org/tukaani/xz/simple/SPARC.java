package org.tukaani.xz.simple;

public final class SPARC implements SimpleFilter
{
    private final boolean isEncoder;
    private int pos;
    
    public SPARC(final boolean isEncoder, final int pos) {
        this.isEncoder = isEncoder;
        this.pos = pos;
    }
    
    @Override
    public int code(final byte[] array, final int n, final int n2) {
        int n3;
        int i;
        for (n3 = n + n2 - 4, i = n; i <= n3; i += 4) {
            if ((array[i] == 64 && (array[i + 1] & 0xC0) == 0x0) || (array[i] == 127 && (array[i + 1] & 0xC0) == 0xC0)) {
                final int n4 = ((array[i] & 0xFF) << 24 | (array[i + 1] & 0xFF) << 16 | (array[i + 2] & 0xFF) << 8 | (array[i + 3] & 0xFF)) << 2;
                int n5;
                if (this.isEncoder) {
                    n5 = n4 + (this.pos + i - n);
                }
                else {
                    n5 = n4 - (this.pos + i - n);
                }
                final int n6 = n5 >>> 2;
                final int n7 = (0 - (n6 >>> 22 & 0x1) << 22 & 0x3FFFFFFF) | (n6 & 0x3FFFFF) | 0x40000000;
                array[i] = (byte)(n7 >>> 24);
                array[i + 1] = (byte)(n7 >>> 16);
                array[i + 2] = (byte)(n7 >>> 8);
                array[i + 3] = (byte)n7;
            }
        }
        final int n8 = i - n;
        this.pos += n8;
        return n8;
    }
}
