package org.openjsse.com.sun.crypto.provider;

import sun.security.util.math.intpoly.IntegerPolynomial1305;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.security.InvalidKeyException;
import java.util.Objects;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import sun.security.util.math.MutableIntegerModuloP;
import sun.security.util.math.IntegerModuloP;
import sun.security.util.math.IntegerFieldModuloP;

final class Poly1305
{
    private static final int KEY_LENGTH = 32;
    private static final int RS_LENGTH = 16;
    private static final int BLOCK_LENGTH = 16;
    private static final int TAG_LENGTH = 16;
    private static final IntegerFieldModuloP ipl1305;
    private byte[] keyBytes;
    private final byte[] block;
    private int blockOffset;
    private IntegerModuloP r;
    private IntegerModuloP s;
    private MutableIntegerModuloP a;
    private final MutableIntegerModuloP n;
    
    Poly1305() {
        this.block = new byte[16];
        this.n = Poly1305.ipl1305.get1().mutable();
    }
    
    void engineInit(final Key newKey, final AlgorithmParameterSpec params) throws InvalidKeyException {
        Objects.requireNonNull(newKey, "Null key provided during init");
        this.keyBytes = newKey.getEncoded();
        if (this.keyBytes == null) {
            throw new InvalidKeyException("Key does not support encoding");
        }
        if (this.keyBytes.length != 32) {
            throw new InvalidKeyException("Incorrect length for key: " + this.keyBytes.length);
        }
        this.engineReset();
        this.setRSVals();
    }
    
    int engineGetMacLength() {
        return 16;
    }
    
    void engineReset() {
        Arrays.fill(this.block, (byte)0);
        this.blockOffset = 0;
        this.a = Poly1305.ipl1305.get0().mutable();
    }
    
    void engineUpdate(final ByteBuffer buf) {
        int bytesToWrite;
        for (int remaining = buf.remaining(); remaining > 0; remaining -= bytesToWrite) {
            bytesToWrite = Integer.min(remaining, 16 - this.blockOffset);
            if (bytesToWrite >= 16) {
                this.processBlock(buf, bytesToWrite);
            }
            else {
                buf.get(this.block, this.blockOffset, bytesToWrite);
                this.blockOffset += bytesToWrite;
                if (this.blockOffset >= 16) {
                    this.processBlock(this.block, 0, 16);
                    this.blockOffset = 0;
                }
            }
        }
    }
    
    void engineUpdate(final byte[] input, int offset, int len) {
        this.checkFromIndexSize(offset, len, input.length);
        if (this.blockOffset > 0) {
            final int blockSpaceLeft = 16 - this.blockOffset;
            if (len < blockSpaceLeft) {
                System.arraycopy(input, offset, this.block, this.blockOffset, len);
                this.blockOffset += len;
                return;
            }
            System.arraycopy(input, offset, this.block, this.blockOffset, blockSpaceLeft);
            offset += blockSpaceLeft;
            len -= blockSpaceLeft;
            this.processBlock(this.block, 0, 16);
            this.blockOffset = 0;
        }
        while (len >= 16) {
            this.processBlock(input, offset, 16);
            offset += 16;
            len -= 16;
        }
        if (len > 0) {
            System.arraycopy(input, offset, this.block, 0, len);
            this.blockOffset = len;
        }
    }
    
    void engineUpdate(final byte input) {
        assert this.blockOffset < 16;
        this.block[this.blockOffset++] = input;
        if (this.blockOffset == 16) {
            this.processBlock(this.block, 0, 16);
            this.blockOffset = 0;
        }
    }
    
    byte[] engineDoFinal() {
        final byte[] tag = new byte[16];
        if (this.blockOffset > 0) {
            this.processBlock(this.block, 0, this.blockOffset);
            this.blockOffset = 0;
        }
        this.a.addModPowerTwo(this.s, tag);
        this.engineReset();
        return tag;
    }
    
    private void processBlock(final ByteBuffer buf, final int len) {
        this.n.setValue(buf, len, (byte)1);
        this.a.setSum((IntegerModuloP)this.n);
        this.a.setProduct(this.r);
    }
    
    private void processBlock(final byte[] block, final int offset, final int length) {
        this.checkFromIndexSize(offset, length, block.length);
        this.n.setValue(block, offset, length, (byte)1);
        this.a.setSum((IntegerModuloP)this.n);
        this.a.setProduct(this.r);
    }
    
    private void setRSVals() {
        final byte[] keyBytes = this.keyBytes;
        final int n = 3;
        keyBytes[n] &= 0xF;
        final byte[] keyBytes2 = this.keyBytes;
        final int n2 = 7;
        keyBytes2[n2] &= 0xF;
        final byte[] keyBytes3 = this.keyBytes;
        final int n3 = 11;
        keyBytes3[n3] &= 0xF;
        final byte[] keyBytes4 = this.keyBytes;
        final int n4 = 15;
        keyBytes4[n4] &= 0xF;
        final byte[] keyBytes5 = this.keyBytes;
        final int n5 = 4;
        keyBytes5[n5] &= (byte)252;
        final byte[] keyBytes6 = this.keyBytes;
        final int n6 = 8;
        keyBytes6[n6] &= (byte)252;
        final byte[] keyBytes7 = this.keyBytes;
        final int n7 = 12;
        keyBytes7[n7] &= (byte)252;
        this.r = (IntegerModuloP)Poly1305.ipl1305.getElement(this.keyBytes, 0, 16, (byte)0);
        this.s = (IntegerModuloP)Poly1305.ipl1305.getElement(this.keyBytes, 16, 16, (byte)0);
    }
    
    private int checkFromIndexSize(final int fromIndex, final int size, final int length) throws IndexOutOfBoundsException {
        if ((length | fromIndex | size) < 0 || size > length - fromIndex) {
            throw new IndexOutOfBoundsException();
        }
        return fromIndex;
    }
    
    static {
        ipl1305 = (IntegerFieldModuloP)new IntegerPolynomial1305();
    }
}
