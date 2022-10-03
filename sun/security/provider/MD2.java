package sun.security.provider;

import java.util.Arrays;

public final class MD2 extends DigestBase
{
    private int[] X;
    private int[] C;
    private byte[] cBytes;
    private static final int[] S;
    private static final byte[][] PADDING;
    
    public MD2() {
        super("MD2", 16, 16);
        this.X = new int[48];
        this.C = new int[16];
        this.cBytes = new byte[16];
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        final MD2 md2 = (MD2)super.clone();
        md2.X = md2.X.clone();
        md2.C = md2.C.clone();
        md2.cBytes = new byte[16];
        return md2;
    }
    
    @Override
    void implReset() {
        Arrays.fill(this.X, 0);
        Arrays.fill(this.C, 0);
    }
    
    @Override
    void implDigest(final byte[] array, final int n) {
        final int n2 = 16 - ((int)this.bytesProcessed & 0xF);
        this.engineUpdate(MD2.PADDING[n2], 0, n2);
        for (int i = 0; i < 16; ++i) {
            this.cBytes[i] = (byte)this.C[i];
        }
        this.implCompress(this.cBytes, 0);
        for (int j = 0; j < 16; ++j) {
            array[n + j] = (byte)this.X[j];
        }
    }
    
    @Override
    void implCompress(final byte[] array, final int n) {
        for (int i = 0; i < 16; ++i) {
            final int n2 = array[n + i] & 0xFF;
            this.X[16 + i] = n2;
            this.X[32 + i] = (n2 ^ this.X[i]);
        }
        int n3 = this.C[15];
        for (int j = 0; j < 16; ++j) {
            final int[] c = this.C;
            final int n4 = j;
            final int n5 = c[n4] ^ MD2.S[this.X[16 + j] ^ n3];
            c[n4] = n5;
            n3 = n5;
        }
        int n6 = 0;
        for (int k = 0; k < 18; ++k) {
            for (int l = 0; l < 48; ++l) {
                final int[] x = this.X;
                final int n7 = l;
                final int n8 = x[n7] ^ MD2.S[n6];
                x[n7] = n8;
                n6 = n8;
            }
            n6 = (n6 + k & 0xFF);
        }
    }
    
    static {
        S = new int[] { 41, 46, 67, 201, 162, 216, 124, 1, 61, 54, 84, 161, 236, 240, 6, 19, 98, 167, 5, 243, 192, 199, 115, 140, 152, 147, 43, 217, 188, 76, 130, 202, 30, 155, 87, 60, 253, 212, 224, 22, 103, 66, 111, 24, 138, 23, 229, 18, 190, 78, 196, 214, 218, 158, 222, 73, 160, 251, 245, 142, 187, 47, 238, 122, 169, 104, 121, 145, 21, 178, 7, 63, 148, 194, 16, 137, 11, 34, 95, 33, 128, 127, 93, 154, 90, 144, 50, 39, 53, 62, 204, 231, 191, 247, 151, 3, 255, 25, 48, 179, 72, 165, 181, 209, 215, 94, 146, 42, 172, 86, 170, 198, 79, 184, 56, 210, 150, 164, 125, 182, 118, 252, 107, 226, 156, 116, 4, 241, 69, 157, 112, 89, 100, 113, 135, 32, 134, 91, 207, 101, 230, 45, 168, 2, 27, 96, 37, 173, 174, 176, 185, 246, 28, 70, 97, 105, 52, 64, 126, 15, 85, 71, 163, 35, 221, 81, 175, 58, 195, 92, 249, 206, 186, 197, 234, 38, 44, 83, 13, 110, 133, 40, 132, 9, 211, 223, 205, 244, 65, 129, 77, 82, 106, 220, 55, 200, 108, 193, 171, 250, 36, 225, 123, 8, 12, 189, 177, 74, 120, 136, 149, 139, 227, 99, 232, 109, 233, 203, 213, 254, 59, 0, 29, 57, 242, 239, 183, 14, 102, 88, 208, 228, 166, 119, 114, 248, 235, 117, 75, 10, 49, 68, 80, 180, 143, 237, 31, 26, 219, 153, 141, 51, 159, 17, 131, 20 };
        PADDING = new byte[17][];
        for (int i = 1; i < 17; ++i) {
            final byte[] array = new byte[i];
            Arrays.fill(array, (byte)i);
            MD2.PADDING[i] = array;
        }
    }
}
