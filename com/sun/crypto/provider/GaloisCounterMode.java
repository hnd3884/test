package com.sun.crypto.provider;

import javax.crypto.AEADBadTagException;
import javax.crypto.ShortBufferException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.ProviderException;
import java.io.ByteArrayOutputStream;

final class GaloisCounterMode extends FeedbackCipher
{
    static int DEFAULT_TAG_LEN;
    static int DEFAULT_IV_LEN;
    private static final int MAX_BUF_SIZE = Integer.MAX_VALUE;
    private static final int TRIGGERLEN = 65536;
    private ByteArrayOutputStream aadBuffer;
    private int sizeOfAAD;
    private ByteArrayOutputStream ibuffer;
    private int tagLenBytes;
    private byte[] subkeyH;
    private byte[] preCounterBlock;
    private GCTR gctrPAndC;
    private GHASH ghashAllToS;
    private int processed;
    private byte[] aadBufferSave;
    private int sizeOfAADSave;
    private byte[] ibufferSave;
    private int processedSave;
    
    static void increment32(final byte[] array) {
        if (array.length != 16) {
            throw new ProviderException("Illegal counter block length");
        }
        for (int i = array.length - 1; i >= array.length - 4; --i) {
            final int n = i;
            if (++array[n] != 0) {
                break;
            }
        }
    }
    
    private static byte[] getLengthBlock(final int n) {
        final long n2 = (long)n << 3;
        return new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, (byte)(n2 >>> 56), (byte)(n2 >>> 48), (byte)(n2 >>> 40), (byte)(n2 >>> 32), (byte)(n2 >>> 24), (byte)(n2 >>> 16), (byte)(n2 >>> 8), (byte)n2 };
    }
    
    private static byte[] getLengthBlock(final int n, final int n2) {
        final long n3 = (long)n << 3;
        final long n4 = (long)n2 << 3;
        return new byte[] { (byte)(n3 >>> 56), (byte)(n3 >>> 48), (byte)(n3 >>> 40), (byte)(n3 >>> 32), (byte)(n3 >>> 24), (byte)(n3 >>> 16), (byte)(n3 >>> 8), (byte)n3, (byte)(n4 >>> 56), (byte)(n4 >>> 48), (byte)(n4 >>> 40), (byte)(n4 >>> 32), (byte)(n4 >>> 24), (byte)(n4 >>> 16), (byte)(n4 >>> 8), (byte)n4 };
    }
    
    private static byte[] expandToOneBlock(final byte[] array, final int n, final int n2) {
        if (n2 > 16) {
            throw new ProviderException("input " + n2 + " too long");
        }
        if (n2 == 16 && n == 0) {
            return array;
        }
        final byte[] array2 = new byte[16];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    private static byte[] getJ0(final byte[] array, final byte[] array2) {
        byte[] array3;
        if (array.length == 12) {
            array3 = expandToOneBlock(array, 0, array.length);
            array3[15] = 1;
        }
        else {
            final GHASH ghash = new GHASH(array2);
            final int n = array.length % 16;
            if (n != 0) {
                ghash.update(array, 0, array.length - n);
                ghash.update(expandToOneBlock(array, array.length - n, n));
            }
            else {
                ghash.update(array);
            }
            ghash.update(getLengthBlock(array.length));
            array3 = ghash.digest();
        }
        return array3;
    }
    
    private static void checkDataLength(final int n, final int n2) {
        if (n > Integer.MAX_VALUE - n2) {
            throw new ProviderException("SunJCE provider only supports input size up to 2147483647 bytes");
        }
    }
    
    GaloisCounterMode(final SymmetricCipher symmetricCipher) {
        super(symmetricCipher);
        this.aadBuffer = new ByteArrayOutputStream();
        this.sizeOfAAD = 0;
        this.ibuffer = null;
        this.tagLenBytes = GaloisCounterMode.DEFAULT_TAG_LEN;
        this.subkeyH = null;
        this.preCounterBlock = null;
        this.gctrPAndC = null;
        this.ghashAllToS = null;
        this.processed = 0;
        this.aadBufferSave = null;
        this.sizeOfAADSave = 0;
        this.ibufferSave = null;
        this.processedSave = 0;
        this.aadBuffer = new ByteArrayOutputStream();
    }
    
    @Override
    String getFeedback() {
        return "GCM";
    }
    
    @Override
    void reset() {
        if (this.aadBuffer == null) {
            this.aadBuffer = new ByteArrayOutputStream();
        }
        else {
            this.aadBuffer.reset();
        }
        if (this.gctrPAndC != null) {
            this.gctrPAndC.reset();
        }
        if (this.ghashAllToS != null) {
            this.ghashAllToS.reset();
        }
        this.processed = 0;
        this.sizeOfAAD = 0;
        if (this.ibuffer != null) {
            this.ibuffer.reset();
        }
    }
    
    @Override
    void save() {
        this.processedSave = this.processed;
        this.sizeOfAADSave = this.sizeOfAAD;
        this.aadBufferSave = (byte[])((this.aadBuffer == null || this.aadBuffer.size() == 0) ? null : this.aadBuffer.toByteArray());
        if (this.gctrPAndC != null) {
            this.gctrPAndC.save();
        }
        if (this.ghashAllToS != null) {
            this.ghashAllToS.save();
        }
        if (this.ibuffer != null) {
            this.ibufferSave = this.ibuffer.toByteArray();
        }
    }
    
    @Override
    void restore() {
        this.processed = this.processedSave;
        this.sizeOfAAD = this.sizeOfAADSave;
        if (this.aadBuffer != null) {
            this.aadBuffer.reset();
            if (this.aadBufferSave != null) {
                this.aadBuffer.write(this.aadBufferSave, 0, this.aadBufferSave.length);
            }
        }
        if (this.gctrPAndC != null) {
            this.gctrPAndC.restore();
        }
        if (this.ghashAllToS != null) {
            this.ghashAllToS.restore();
        }
        if (this.ibuffer != null) {
            this.ibuffer.reset();
            this.ibuffer.write(this.ibufferSave, 0, this.ibufferSave.length);
        }
    }
    
    @Override
    void init(final boolean b, final String s, final byte[] array, final byte[] array2) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.init(b, s, array, array2, GaloisCounterMode.DEFAULT_TAG_LEN);
    }
    
    void init(final boolean b, final String s, final byte[] array, final byte[] array2, final int tagLenBytes) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (array == null) {
            throw new InvalidKeyException("Internal error");
        }
        if (array2 == null) {
            throw new InvalidAlgorithmParameterException("Internal error");
        }
        if (array2.length == 0) {
            throw new InvalidAlgorithmParameterException("IV is empty");
        }
        this.embeddedCipher.init(false, s, array);
        this.subkeyH = new byte[16];
        this.embeddedCipher.encryptBlock(new byte[16], 0, this.subkeyH, 0);
        this.iv = array2.clone();
        this.preCounterBlock = getJ0(this.iv, this.subkeyH);
        final byte[] array3 = this.preCounterBlock.clone();
        increment32(array3);
        this.gctrPAndC = new GCTR(this.embeddedCipher, array3);
        this.ghashAllToS = new GHASH(this.subkeyH);
        this.tagLenBytes = tagLenBytes;
        if (this.aadBuffer == null) {
            this.aadBuffer = new ByteArrayOutputStream();
        }
        else {
            this.aadBuffer.reset();
        }
        this.processed = 0;
        this.sizeOfAAD = 0;
        if (b) {
            this.ibuffer = new ByteArrayOutputStream();
        }
    }
    
    @Override
    void updateAAD(final byte[] array, final int n, final int n2) {
        if (this.aadBuffer != null) {
            this.aadBuffer.write(array, n, n2);
            return;
        }
        throw new IllegalStateException("Update has been called; no more AAD data");
    }
    
    void processAAD() {
        if (this.aadBuffer != null) {
            if (this.aadBuffer.size() > 0) {
                final byte[] byteArray = this.aadBuffer.toByteArray();
                this.sizeOfAAD = byteArray.length;
                final int n = byteArray.length % 16;
                if (n != 0) {
                    this.ghashAllToS.update(byteArray, 0, byteArray.length - n);
                    this.ghashAllToS.update(expandToOneBlock(byteArray, byteArray.length - n, n));
                }
                else {
                    this.ghashAllToS.update(byteArray);
                }
            }
            this.aadBuffer = null;
        }
    }
    
    void doLastBlock(final byte[] array, int n, final int n2, final byte[] array2, int n3, final boolean b) throws IllegalBlockSizeException {
        int n4 = n2;
        byte[] array3;
        int n5;
        if (b) {
            array3 = array2;
            n5 = n3;
        }
        else {
            array3 = array;
            n5 = n;
        }
        if (n2 > 65536) {
            int n6;
            int i;
            for (n6 = 0, i = n2 / 1024; i > n6; ++n6) {
                final int update = this.gctrPAndC.update(array, n, 96, array2, n3);
                this.ghashAllToS.update(array3, n5, update);
                n += update;
                n3 += update;
                n5 += update;
            }
            n4 -= i * 96;
            this.processed += i * 96;
        }
        this.gctrPAndC.doFinal(array, n, n4, array2, n3);
        this.processed += n4;
        final int n7 = n4 % 16;
        if (n7 != 0) {
            this.ghashAllToS.update(array3, n5, n4 - n7);
            this.ghashAllToS.update(expandToOneBlock(array3, n5 + n4 - n7, n7));
        }
        else {
            this.ghashAllToS.update(array3, n5, n4);
        }
    }
    
    @Override
    int encrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        checkDataLength(this.processed, n2);
        RangeUtil.blockSizeCheck(n2, this.blockSize);
        this.processAAD();
        if (n2 > 0) {
            RangeUtil.nullAndBoundsCheck(array, n, n2);
            RangeUtil.nullAndBoundsCheck(array2, n3, n2);
            this.gctrPAndC.update(array, n, n2, array2, n3);
            this.processed += n2;
            this.ghashAllToS.update(array2, n3, n2);
        }
        return n2;
    }
    
    @Override
    int encryptFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalBlockSizeException, ShortBufferException {
        if (n2 > Integer.MAX_VALUE - this.tagLenBytes) {
            throw new ShortBufferException("Can't fit both data and tag into one buffer");
        }
        try {
            RangeUtil.nullAndBoundsCheck(array2, n3, n2 + this.tagLenBytes);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new ShortBufferException("Output buffer too small");
        }
        checkDataLength(this.processed, n2);
        this.processAAD();
        if (n2 > 0) {
            RangeUtil.nullAndBoundsCheck(array, n, n2);
            this.doLastBlock(array, n, n2, array2, n3, true);
        }
        this.ghashAllToS.update(getLengthBlock(this.sizeOfAAD, this.processed));
        final byte[] digest = this.ghashAllToS.digest();
        final byte[] array3 = new byte[digest.length];
        new GCTR(this.embeddedCipher, this.preCounterBlock).doFinal(digest, 0, digest.length, array3, 0);
        System.arraycopy(array3, 0, array2, n3 + n2, this.tagLenBytes);
        return n2 + this.tagLenBytes;
    }
    
    @Override
    int decrypt(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        checkDataLength(this.ibuffer.size(), n2);
        RangeUtil.blockSizeCheck(n2, this.blockSize);
        this.processAAD();
        if (n2 > 0) {
            RangeUtil.nullAndBoundsCheck(array, n, n2);
            this.ibuffer.write(array, n, n2);
        }
        return 0;
    }
    
    @Override
    int decryptFinal(byte[] byteArray, int n, int length, final byte[] array, final int n2) throws IllegalBlockSizeException, AEADBadTagException, ShortBufferException {
        if (length < this.tagLenBytes) {
            throw new AEADBadTagException("Input too short - need tag");
        }
        checkDataLength(this.ibuffer.size(), length - this.tagLenBytes);
        try {
            RangeUtil.nullAndBoundsCheck(array, n2, this.ibuffer.size() + length - this.tagLenBytes);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new ShortBufferException("Output buffer too small");
        }
        this.processAAD();
        RangeUtil.nullAndBoundsCheck(byteArray, n, length);
        final byte[] array2 = new byte[this.tagLenBytes];
        System.arraycopy(byteArray, n + length - this.tagLenBytes, array2, 0, this.tagLenBytes);
        length -= this.tagLenBytes;
        if (byteArray == array || this.ibuffer.size() > 0) {
            if (length > 0) {
                this.ibuffer.write(byteArray, n, length);
            }
            byteArray = this.ibuffer.toByteArray();
            n = 0;
            length = byteArray.length;
            this.ibuffer.reset();
        }
        if (length > 0) {
            this.doLastBlock(byteArray, n, length, array, n2, false);
        }
        this.ghashAllToS.update(getLengthBlock(this.sizeOfAAD, this.processed));
        final byte[] digest = this.ghashAllToS.digest();
        final byte[] array3 = new byte[digest.length];
        new GCTR(this.embeddedCipher, this.preCounterBlock).doFinal(digest, 0, digest.length, array3, 0);
        int n3 = 0;
        for (int i = 0; i < this.tagLenBytes; ++i) {
            n3 |= (array2[i] ^ array3[i]);
        }
        if (n3 != 0) {
            throw new AEADBadTagException("Tag mismatch!");
        }
        return length;
    }
    
    int getTagLen() {
        return this.tagLenBytes;
    }
    
    @Override
    int getBufferedLength() {
        if (this.ibuffer == null) {
            return 0;
        }
        return this.ibuffer.size();
    }
    
    static {
        GaloisCounterMode.DEFAULT_TAG_LEN = 16;
        GaloisCounterMode.DEFAULT_IV_LEN = 12;
    }
}
