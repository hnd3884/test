package org.tukaani.xz.simple;

public final class ARMThumb implements SimpleFilter
{
    private final boolean isEncoder;
    private int pos;
    
    public ARMThumb(final boolean isEncoder, final int n) {
        this.isEncoder = isEncoder;
        this.pos = n + 4;
    }
    
    @Override
    public int code(final byte[] array, final int n, final int n2) {
        int n3;
        int i;
        for (n3 = n + n2 - 4, i = n; i <= n3; i += 2) {
            if ((array[i + 1] & 0xF8) == 0xF0 && (array[i + 3] & 0xF8) == 0xF8) {
                final int n4 = ((array[i + 1] & 0x7) << 19 | (array[i] & 0xFF) << 11 | (array[i + 3] & 0x7) << 8 | (array[i + 2] & 0xFF)) << 1;
                int n5;
                if (this.isEncoder) {
                    n5 = n4 + (this.pos + i - n);
                }
                else {
                    n5 = n4 - (this.pos + i - n);
                }
                final int n6 = n5 >>> 1;
                array[i + 1] = (byte)(0xF0 | (n6 >>> 19 & 0x7));
                array[i] = (byte)(n6 >>> 11);
                array[i + 3] = (byte)(0xF8 | (n6 >>> 8 & 0x7));
                array[i + 2] = (byte)n6;
                i += 2;
            }
        }
        final int n7 = i - n;
        this.pos += n7;
        return n7;
    }
}
