package cryptix.jce.provider.md;

import java.security.MessageDigestSpi;

public class MD2 extends MessageDigestSpi implements Cloneable
{
    private static final int BLOCK_LENGTH = 16;
    private static final int[] S;
    private int[] checksum;
    private int count;
    private int[] buffer;
    private int[] X;
    
    public Object clone() {
        return new MD2(this);
    }
    
    public void engineReset() {
        this.count = 0;
        for (int i = 0; i < 16; ++i) {
            this.X[i] = 0;
            this.checksum[i] = 0;
        }
    }
    
    public void engineUpdate(final byte input) {
        this.buffer[this.count] = (input & 0xFF);
        if (this.count == 15) {
            this.transform(this.buffer, 0);
            this.count = 0;
        }
        else {
            ++this.count;
        }
    }
    
    public void engineUpdate(final byte[] input, final int offset, final int len) {
        if (offset < 0 || len < 0 || offset + (long)len > input.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        final int partLen = 16 - this.count;
        int i = 0;
        if (len >= partLen) {
            for (int j = 0; j < partLen; ++j) {
                this.buffer[this.count + j] = (input[offset + j] & 0xFF);
            }
            this.transform(this.buffer, 0);
            this.count = 0;
            for (i = partLen; i + 16 - 1 < len; i += 16) {
                this.transform(input, offset + i);
            }
        }
        if (i < len) {
            for (int j = 0; j < len - i; ++j) {
                this.buffer[this.count + j] = (input[offset + i + j] & 0xFF);
            }
            this.count += len - i;
        }
    }
    
    public byte[] engineDigest() {
        final int padLen = 16 - this.count;
        for (int i = this.count; i < 16; ++i) {
            this.buffer[i] = (byte)padLen;
        }
        this.transform(this.buffer, 0);
        this.transform(this.checksum, 0);
        final byte[] result = new byte[16];
        for (int j = 0; j < 16; ++j) {
            result[j] = (byte)this.X[j];
        }
        this.engineReset();
        return result;
    }
    
    private void transform(final int[] block, final int offset) {
        for (int i = 0; i < 16; ++i) {
            this.X[16 + i] = (block[offset + i] & 0xFF);
            this.X[32 + i] = (this.X[i] ^ this.X[16 + i]);
        }
        int t = 0;
        for (int j = 0; j < 18; ++j) {
            for (int k = 0; k < 48; ++k) {
                final int[] x = this.X;
                final int n = k;
                final int n2 = x[n] ^ MD2.S[t];
                x[n] = n2;
                t = n2;
            }
            t = (t + j & 0xFF);
        }
        t = this.checksum[15];
        for (int j = 0; j < 16; ++j) {
            final int[] checksum = this.checksum;
            final int n3 = j;
            final int n4 = checksum[n3] ^ MD2.S[(block[offset + j] & 0xFF) ^ t];
            checksum[n3] = n4;
            t = n4;
        }
    }
    
    private void transform(final byte[] block, final int offset) {
        for (int i = 0; i < 16; ++i) {
            this.X[16 + i] = (block[offset + i] & 0xFF);
            this.X[32 + i] = (this.X[i] ^ this.X[16 + i]);
        }
        int t = 0;
        for (int j = 0; j < 18; ++j) {
            for (int k = 0; k < 48; ++k) {
                final int[] x = this.X;
                final int n = k;
                final int n2 = x[n] ^ MD2.S[t];
                x[n] = n2;
                t = n2;
            }
            t = (t + j & 0xFF);
        }
        t = this.checksum[15];
        for (int j = 0; j < 16; ++j) {
            final int[] checksum = this.checksum;
            final int n3 = j;
            final int n4 = checksum[n3] ^ MD2.S[(block[offset + j] & 0xFF) ^ t];
            checksum[n3] = n4;
            t = n4;
        }
    }
    
    public MD2() {
        this.checksum = new int[16];
        this.buffer = new int[16];
        this.X = new int[48];
        this.engineReset();
    }
    
    private MD2(final MD2 md) {
        this();
        this.X = md.X.clone();
        this.checksum = md.checksum.clone();
        this.buffer = md.buffer.clone();
        this.count = md.count;
    }
    
    static {
        S = new int[] { 41, 46, 67, 201, 162, 216, 124, 1, 61, 54, 84, 161, 236, 240, 6, 19, 98, 167, 5, 243, 192, 199, 115, 140, 152, 147, 43, 217, 188, 76, 130, 202, 30, 155, 87, 60, 253, 212, 224, 22, 103, 66, 111, 24, 138, 23, 229, 18, 190, 78, 196, 214, 218, 158, 222, 73, 160, 251, 245, 142, 187, 47, 238, 122, 169, 104, 121, 145, 21, 178, 7, 63, 148, 194, 16, 137, 11, 34, 95, 33, 128, 127, 93, 154, 90, 144, 50, 39, 53, 62, 204, 231, 191, 247, 151, 3, 255, 25, 48, 179, 72, 165, 181, 209, 215, 94, 146, 42, 172, 86, 170, 198, 79, 184, 56, 210, 150, 164, 125, 182, 118, 252, 107, 226, 156, 116, 4, 241, 69, 157, 112, 89, 100, 113, 135, 32, 134, 91, 207, 101, 230, 45, 168, 2, 27, 96, 37, 173, 174, 176, 185, 246, 28, 70, 97, 105, 52, 64, 126, 15, 85, 71, 163, 35, 221, 81, 175, 58, 195, 92, 249, 206, 186, 197, 234, 38, 44, 83, 13, 110, 133, 40, 132, 9, 211, 223, 205, 244, 65, 129, 77, 82, 106, 220, 55, 200, 108, 193, 171, 250, 36, 225, 123, 8, 12, 189, 177, 74, 120, 136, 149, 139, 227, 99, 232, 109, 233, 203, 213, 254, 59, 0, 29, 57, 242, 239, 183, 14, 102, 88, 208, 228, 166, 119, 114, 248, 235, 117, 75, 10, 49, 68, 80, 180, 143, 237, 31, 26, 219, 153, 141, 51, 159, 17, 131, 20 };
    }
}
