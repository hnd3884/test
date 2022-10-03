package org.tukaani.xz.simple;

public final class X86 implements SimpleFilter
{
    private static final boolean[] MASK_TO_ALLOWED_STATUS;
    private static final int[] MASK_TO_BIT_NUMBER;
    private final boolean isEncoder;
    private int pos;
    private int prevMask;
    
    private static boolean test86MSByte(final byte b) {
        final int n = b & 0xFF;
        return n == 0 || n == 255;
    }
    
    public X86(final boolean isEncoder, final int n) {
        this.prevMask = 0;
        this.isEncoder = isEncoder;
        this.pos = n + 5;
    }
    
    @Override
    public int code(final byte[] array, final int n, final int n2) {
        int n3 = n - 1;
        int n4;
        int i;
        for (n4 = n + n2 - 5, i = n; i <= n4; ++i) {
            if ((array[i] & 0xFE) == 0xE8) {
                final int n5 = i - n3;
                if ((n5 & 0xFFFFFFFC) != 0x0) {
                    this.prevMask = 0;
                }
                else {
                    this.prevMask = (this.prevMask << n5 - 1 & 0x7);
                    if (this.prevMask != 0 && (!X86.MASK_TO_ALLOWED_STATUS[this.prevMask] || test86MSByte(array[i + 4 - X86.MASK_TO_BIT_NUMBER[this.prevMask]]))) {
                        n3 = i;
                        this.prevMask = (this.prevMask << 1 | 0x1);
                        continue;
                    }
                }
                n3 = i;
                if (test86MSByte(array[i + 4])) {
                    int n6 = (array[i + 1] & 0xFF) | (array[i + 2] & 0xFF) << 8 | (array[i + 3] & 0xFF) << 16 | (array[i + 4] & 0xFF) << 24;
                    int n7;
                    while (true) {
                        if (this.isEncoder) {
                            n7 = n6 + (this.pos + i - n);
                        }
                        else {
                            n7 = n6 - (this.pos + i - n);
                        }
                        if (this.prevMask == 0) {
                            break;
                        }
                        final int n8 = X86.MASK_TO_BIT_NUMBER[this.prevMask] * 8;
                        if (!test86MSByte((byte)(n7 >>> 24 - n8))) {
                            break;
                        }
                        n6 = (n7 ^ (1 << 32 - n8) - 1);
                    }
                    array[i + 1] = (byte)n7;
                    array[i + 2] = (byte)(n7 >>> 8);
                    array[i + 3] = (byte)(n7 >>> 16);
                    array[i + 4] = (byte)~((n7 >>> 24 & 0x1) - 1);
                    i += 4;
                }
                else {
                    this.prevMask = (this.prevMask << 1 | 0x1);
                }
            }
        }
        final int n9 = i - n3;
        this.prevMask = (((n9 & 0xFFFFFFFC) != 0x0) ? 0 : (this.prevMask << n9 - 1));
        final int n10 = i - n;
        this.pos += n10;
        return n10;
    }
    
    static {
        MASK_TO_ALLOWED_STATUS = new boolean[] { true, true, true, false, true, false, false, false };
        MASK_TO_BIT_NUMBER = new int[] { 0, 1, 2, 2, 3, 3, 3, 3 };
    }
}
