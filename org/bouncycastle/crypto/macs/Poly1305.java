package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;

public class Poly1305 implements Mac
{
    private static final int BLOCK_SIZE = 16;
    private final BlockCipher cipher;
    private final byte[] singleByte;
    private int r0;
    private int r1;
    private int r2;
    private int r3;
    private int r4;
    private int s1;
    private int s2;
    private int s3;
    private int s4;
    private int k0;
    private int k1;
    private int k2;
    private int k3;
    private final byte[] currentBlock;
    private int currentBlockOffset;
    private int h0;
    private int h1;
    private int h2;
    private int h3;
    private int h4;
    
    public Poly1305() {
        this.singleByte = new byte[1];
        this.currentBlock = new byte[16];
        this.currentBlockOffset = 0;
        this.cipher = null;
    }
    
    public Poly1305(final BlockCipher cipher) {
        this.singleByte = new byte[1];
        this.currentBlock = new byte[16];
        this.currentBlockOffset = 0;
        if (cipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("Poly1305 requires a 128 bit block cipher.");
        }
        this.cipher = cipher;
    }
    
    public void init(CipherParameters parameters) throws IllegalArgumentException {
        byte[] iv = null;
        if (this.cipher != null) {
            if (!(parameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("Poly1305 requires an IV when used with a block cipher.");
            }
            final ParametersWithIV parametersWithIV = (ParametersWithIV)parameters;
            iv = parametersWithIV.getIV();
            parameters = parametersWithIV.getParameters();
        }
        if (!(parameters instanceof KeyParameter)) {
            throw new IllegalArgumentException("Poly1305 requires a key.");
        }
        this.setKey(((KeyParameter)parameters).getKey(), iv);
        this.reset();
    }
    
    private void setKey(final byte[] array, final byte[] array2) {
        if (array.length != 32) {
            throw new IllegalArgumentException("Poly1305 key must be 256 bits.");
        }
        if (this.cipher != null && (array2 == null || array2.length != 16)) {
            throw new IllegalArgumentException("Poly1305 requires a 128 bit IV.");
        }
        final int littleEndianToInt = Pack.littleEndianToInt(array, 0);
        final int littleEndianToInt2 = Pack.littleEndianToInt(array, 4);
        final int littleEndianToInt3 = Pack.littleEndianToInt(array, 8);
        final int littleEndianToInt4 = Pack.littleEndianToInt(array, 12);
        this.r0 = (littleEndianToInt & 0x3FFFFFF);
        this.r1 = ((littleEndianToInt >>> 26 | littleEndianToInt2 << 6) & 0x3FFFF03);
        this.r2 = ((littleEndianToInt2 >>> 20 | littleEndianToInt3 << 12) & 0x3FFC0FF);
        this.r3 = ((littleEndianToInt3 >>> 14 | littleEndianToInt4 << 18) & 0x3F03FFF);
        this.r4 = (littleEndianToInt4 >>> 8 & 0xFFFFF);
        this.s1 = this.r1 * 5;
        this.s2 = this.r2 * 5;
        this.s3 = this.r3 * 5;
        this.s4 = this.r4 * 5;
        byte[] array3;
        int n;
        if (this.cipher == null) {
            array3 = array;
            n = 16;
        }
        else {
            array3 = new byte[16];
            n = 0;
            this.cipher.init(true, new KeyParameter(array, 16, 16));
            this.cipher.processBlock(array2, 0, array3, 0);
        }
        this.k0 = Pack.littleEndianToInt(array3, n + 0);
        this.k1 = Pack.littleEndianToInt(array3, n + 4);
        this.k2 = Pack.littleEndianToInt(array3, n + 8);
        this.k3 = Pack.littleEndianToInt(array3, n + 12);
    }
    
    public String getAlgorithmName() {
        return (this.cipher == null) ? "Poly1305" : ("Poly1305-" + this.cipher.getAlgorithmName());
    }
    
    public int getMacSize() {
        return 16;
    }
    
    public void update(final byte b) throws IllegalStateException {
        this.singleByte[0] = b;
        this.update(this.singleByte, 0, 1);
    }
    
    public void update(final byte[] array, final int n, final int i) throws DataLengthException, IllegalStateException {
        int min;
        for (int n2 = 0; i > n2; n2 += min, this.currentBlockOffset += min) {
            if (this.currentBlockOffset == 16) {
                this.processBlock();
                this.currentBlockOffset = 0;
            }
            min = Math.min(i - n2, 16 - this.currentBlockOffset);
            System.arraycopy(array, n2 + n, this.currentBlock, this.currentBlockOffset, min);
        }
    }
    
    private void processBlock() {
        if (this.currentBlockOffset < 16) {
            this.currentBlock[this.currentBlockOffset] = 1;
            for (int i = this.currentBlockOffset + 1; i < 16; ++i) {
                this.currentBlock[i] = 0;
            }
        }
        final long n = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 0);
        final long n2 = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 4);
        final long n3 = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 8);
        final long n4 = 0xFFFFFFFFL & (long)Pack.littleEndianToInt(this.currentBlock, 12);
        this.h0 += (int)(n & 0x3FFFFFFL);
        this.h1 += (int)((n2 << 32 | n) >>> 26 & 0x3FFFFFFL);
        this.h2 += (int)((n3 << 32 | n2) >>> 20 & 0x3FFFFFFL);
        this.h3 += (int)((n4 << 32 | n3) >>> 14 & 0x3FFFFFFL);
        this.h4 += (int)(n4 >>> 8);
        if (this.currentBlockOffset == 16) {
            this.h4 += 16777216;
        }
        final long n5 = mul32x32_64(this.h0, this.r0) + mul32x32_64(this.h1, this.s4) + mul32x32_64(this.h2, this.s3) + mul32x32_64(this.h3, this.s2) + mul32x32_64(this.h4, this.s1);
        final long n6 = mul32x32_64(this.h0, this.r1) + mul32x32_64(this.h1, this.r0) + mul32x32_64(this.h2, this.s4) + mul32x32_64(this.h3, this.s3) + mul32x32_64(this.h4, this.s2);
        final long n7 = mul32x32_64(this.h0, this.r2) + mul32x32_64(this.h1, this.r1) + mul32x32_64(this.h2, this.r0) + mul32x32_64(this.h3, this.s4) + mul32x32_64(this.h4, this.s3);
        final long n8 = mul32x32_64(this.h0, this.r3) + mul32x32_64(this.h1, this.r2) + mul32x32_64(this.h2, this.r1) + mul32x32_64(this.h3, this.r0) + mul32x32_64(this.h4, this.s4);
        final long n9 = mul32x32_64(this.h0, this.r4) + mul32x32_64(this.h1, this.r3) + mul32x32_64(this.h2, this.r2) + mul32x32_64(this.h3, this.r1) + mul32x32_64(this.h4, this.r0);
        this.h0 = ((int)n5 & 0x3FFFFFF);
        final long n10 = n6 + (n5 >>> 26);
        this.h1 = ((int)n10 & 0x3FFFFFF);
        final long n11 = n7 + (n10 >>> 26);
        this.h2 = ((int)n11 & 0x3FFFFFF);
        final long n12 = n8 + (n11 >>> 26);
        this.h3 = ((int)n12 & 0x3FFFFFF);
        final long n13 = n9 + (n12 >>> 26);
        this.h4 = ((int)n13 & 0x3FFFFFF);
        this.h0 += (int)(n13 >>> 26) * 5;
        this.h1 += this.h0 >>> 26;
        this.h0 &= 0x3FFFFFF;
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        if (n + 16 > array.length) {
            throw new OutputLengthException("Output buffer is too short.");
        }
        if (this.currentBlockOffset > 0) {
            this.processBlock();
        }
        this.h1 += this.h0 >>> 26;
        this.h0 &= 0x3FFFFFF;
        this.h2 += this.h1 >>> 26;
        this.h1 &= 0x3FFFFFF;
        this.h3 += this.h2 >>> 26;
        this.h2 &= 0x3FFFFFF;
        this.h4 += this.h3 >>> 26;
        this.h3 &= 0x3FFFFFF;
        this.h0 += (this.h4 >>> 26) * 5;
        this.h4 &= 0x3FFFFFF;
        this.h1 += this.h0 >>> 26;
        this.h0 &= 0x3FFFFFF;
        final int n2 = this.h0 + 5;
        final int n3 = n2 >>> 26;
        final int n4 = n2 & 0x3FFFFFF;
        final int n5 = this.h1 + n3;
        final int n6 = n5 >>> 26;
        final int n7 = n5 & 0x3FFFFFF;
        final int n8 = this.h2 + n6;
        final int n9 = n8 >>> 26;
        final int n10 = n8 & 0x3FFFFFF;
        final int n11 = this.h3 + n9;
        final int n12 = n11 >>> 26;
        final int n13 = n11 & 0x3FFFFFF;
        final int n14 = this.h4 + n12 - 67108864;
        final int n15 = (n14 >>> 31) - 1;
        final int n16 = ~n15;
        this.h0 = ((this.h0 & n16) | (n4 & n15));
        this.h1 = ((this.h1 & n16) | (n7 & n15));
        this.h2 = ((this.h2 & n16) | (n10 & n15));
        this.h3 = ((this.h3 & n16) | (n13 & n15));
        this.h4 = ((this.h4 & n16) | (n14 & n15));
        final long n17 = ((long)(this.h0 | this.h1 << 26) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k0);
        final long n18 = ((long)(this.h1 >>> 6 | this.h2 << 20) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k1);
        final long n19 = ((long)(this.h2 >>> 12 | this.h3 << 14) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k2);
        final long n20 = ((long)(this.h3 >>> 18 | this.h4 << 8) & 0xFFFFFFFFL) + (0xFFFFFFFFL & (long)this.k3);
        Pack.intToLittleEndian((int)n17, array, n);
        final long n21 = n18 + (n17 >>> 32);
        Pack.intToLittleEndian((int)n21, array, n + 4);
        final long n22 = n19 + (n21 >>> 32);
        Pack.intToLittleEndian((int)n22, array, n + 8);
        Pack.intToLittleEndian((int)(n20 + (n22 >>> 32)), array, n + 12);
        this.reset();
        return 16;
    }
    
    public void reset() {
        this.currentBlockOffset = 0;
        final int h0 = 0;
        this.h4 = h0;
        this.h3 = h0;
        this.h2 = h0;
        this.h1 = h0;
        this.h0 = h0;
    }
    
    private static final long mul32x32_64(final int n, final int n2) {
        return ((long)n & 0xFFFFFFFFL) * n2;
    }
}
