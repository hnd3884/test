package org.tukaani.xz.simple;

public final class IA64 implements SimpleFilter
{
    private static final int[] BRANCH_TABLE;
    private final boolean isEncoder;
    private int pos;
    
    public IA64(final boolean isEncoder, final int pos) {
        this.isEncoder = isEncoder;
        this.pos = pos;
    }
    
    @Override
    public int code(final byte[] array, final int n, final int n2) {
        int n3;
        int i;
        for (n3 = n + n2 - 16, i = n; i <= n3; i += 16) {
            final int n4 = IA64.BRANCH_TABLE[array[i] & 0x1F];
            for (int j = 0, n5 = 5; j < 3; ++j, n5 += 41) {
                if ((n4 >>> j & 0x1) != 0x0) {
                    final int n6 = n5 >>> 3;
                    final int n7 = n5 & 0x7;
                    long n8 = 0L;
                    for (int k = 0; k < 6; ++k) {
                        n8 |= ((long)array[i + n6 + k] & 0xFFL) << 8 * k;
                    }
                    final long n9 = n8 >>> n7;
                    if ((n9 >>> 37 & 0xFL) == 0x5L) {
                        if ((n9 >>> 9 & 0x7L) == 0x0L) {
                            final int n10 = ((int)(n9 >>> 13 & 0xFFFFFL) | ((int)(n9 >>> 36) & 0x1) << 20) << 4;
                            int n11;
                            if (this.isEncoder) {
                                n11 = n10 + (this.pos + i - n);
                            }
                            else {
                                n11 = n10 - (this.pos + i - n);
                            }
                            final int n12 = n11 >>> 4;
                            final long n13 = (n8 & (long)((1 << n7) - 1)) | ((n9 & 0xFFFFFFEE00001FFFL) | ((long)n12 & 0xFFFFFL) << 13 | ((long)n12 & 0x100000L) << 16) << n7;
                            for (int l = 0; l < 6; ++l) {
                                array[i + n6 + l] = (byte)(n13 >>> 8 * l);
                            }
                        }
                    }
                }
            }
        }
        final int n14 = i - n;
        this.pos += n14;
        return n14;
    }
    
    static {
        BRANCH_TABLE = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 4, 6, 6, 0, 0, 7, 7, 4, 4, 0, 0, 4, 4, 0, 0 };
    }
}
