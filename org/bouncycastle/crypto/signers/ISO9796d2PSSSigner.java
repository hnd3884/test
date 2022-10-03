package org.bouncycastle.crypto.signers;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithSalt;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.SignerWithRecovery;

public class ISO9796d2PSSSigner implements SignerWithRecovery
{
    @Deprecated
    public static final int TRAILER_IMPLICIT = 188;
    @Deprecated
    public static final int TRAILER_RIPEMD160 = 12748;
    @Deprecated
    public static final int TRAILER_RIPEMD128 = 13004;
    @Deprecated
    public static final int TRAILER_SHA1 = 13260;
    @Deprecated
    public static final int TRAILER_SHA256 = 13516;
    @Deprecated
    public static final int TRAILER_SHA512 = 13772;
    @Deprecated
    public static final int TRAILER_SHA384 = 14028;
    @Deprecated
    public static final int TRAILER_WHIRLPOOL = 14284;
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private SecureRandom random;
    private byte[] standardSalt;
    private int hLen;
    private int trailer;
    private int keyBits;
    private byte[] block;
    private byte[] mBuf;
    private int messageLength;
    private int saltLength;
    private boolean fullMessage;
    private byte[] recoveredMessage;
    private byte[] preSig;
    private byte[] preBlock;
    private int preMStart;
    private int preTLength;
    
    public ISO9796d2PSSSigner(final AsymmetricBlockCipher cipher, final Digest digest, final int saltLength, final boolean b) {
        this.cipher = cipher;
        this.digest = digest;
        this.hLen = digest.getDigestSize();
        this.saltLength = saltLength;
        if (b) {
            this.trailer = 188;
        }
        else {
            final Integer trailer = ISOTrailers.getTrailer(digest);
            if (trailer == null) {
                throw new IllegalArgumentException("no valid trailer for digest: " + digest.getAlgorithmName());
            }
            this.trailer = trailer;
        }
    }
    
    public ISO9796d2PSSSigner(final AsymmetricBlockCipher asymmetricBlockCipher, final Digest digest, final int n) {
        this(asymmetricBlockCipher, digest, n, false);
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        int n = this.saltLength;
        RSAKeyParameters rsaKeyParameters;
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            rsaKeyParameters = (RSAKeyParameters)parametersWithRandom.getParameters();
            if (b) {
                this.random = parametersWithRandom.getRandom();
            }
        }
        else if (cipherParameters instanceof ParametersWithSalt) {
            final ParametersWithSalt parametersWithSalt = (ParametersWithSalt)cipherParameters;
            rsaKeyParameters = (RSAKeyParameters)parametersWithSalt.getParameters();
            this.standardSalt = parametersWithSalt.getSalt();
            n = this.standardSalt.length;
            if (this.standardSalt.length != this.saltLength) {
                throw new IllegalArgumentException("Fixed salt is of wrong length");
            }
        }
        else {
            rsaKeyParameters = (RSAKeyParameters)cipherParameters;
            if (b) {
                this.random = new SecureRandom();
            }
        }
        this.cipher.init(b, rsaKeyParameters);
        this.keyBits = rsaKeyParameters.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        if (this.trailer == 188) {
            this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - n - 1 - 1];
        }
        else {
            this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - n - 1 - 2];
        }
        this.reset();
    }
    
    private boolean isSameAs(final byte[] array, final byte[] array2) {
        boolean b = true;
        if (this.messageLength != array2.length) {
            b = false;
        }
        for (int i = 0; i != array2.length; ++i) {
            if (array[i] != array2[i]) {
                b = false;
            }
        }
        return b;
    }
    
    private void clearBlock(final byte[] array) {
        for (int i = 0; i != array.length; ++i) {
            array[i] = 0;
        }
    }
    
    public void updateWithRecoveredMessage(final byte[] preSig) throws InvalidCipherTextException {
        byte[] processBlock = this.cipher.processBlock(preSig, 0, preSig.length);
        if (processBlock.length < (this.keyBits + 7) / 8) {
            final byte[] array = new byte[(this.keyBits + 7) / 8];
            System.arraycopy(processBlock, 0, array, array.length - processBlock.length, processBlock.length);
            this.clearBlock(processBlock);
            processBlock = array;
        }
        int preTLength;
        if (((processBlock[processBlock.length - 1] & 0xFF) ^ 0xBC) == 0x0) {
            preTLength = 1;
        }
        else {
            final int n = (processBlock[processBlock.length - 2] & 0xFF) << 8 | (processBlock[processBlock.length - 1] & 0xFF);
            final Integer trailer = ISOTrailers.getTrailer(this.digest);
            if (trailer == null) {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
            final int intValue = trailer;
            if (n != intValue && (intValue != 15052 || n != 16588)) {
                throw new IllegalStateException("signer initialised with wrong digest for trailer " + n);
            }
            preTLength = 2;
        }
        this.digest.doFinal(new byte[this.hLen], 0);
        final byte[] maskGeneratorFunction1 = this.maskGeneratorFunction1(processBlock, processBlock.length - this.hLen - preTLength, this.hLen, processBlock.length - this.hLen - preTLength);
        for (int i = 0; i != maskGeneratorFunction1.length; ++i) {
            final byte[] array2 = processBlock;
            final int n2 = i;
            array2[n2] ^= maskGeneratorFunction1[i];
        }
        final byte[] array3 = processBlock;
        final int n3 = 0;
        array3[n3] &= 0x7F;
        int preMStart;
        for (preMStart = 0; preMStart != processBlock.length && processBlock[preMStart] != 1; ++preMStart) {}
        if (++preMStart >= processBlock.length) {
            this.clearBlock(processBlock);
        }
        this.fullMessage = (preMStart > 1);
        System.arraycopy(processBlock, preMStart, this.recoveredMessage = new byte[maskGeneratorFunction1.length - preMStart - this.saltLength], 0, this.recoveredMessage.length);
        System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
        this.preSig = preSig;
        this.preBlock = processBlock;
        this.preMStart = preMStart;
        this.preTLength = preTLength;
    }
    
    public void update(final byte b) {
        if (this.preSig == null && this.messageLength < this.mBuf.length) {
            this.mBuf[this.messageLength++] = b;
        }
        else {
            this.digest.update(b);
        }
    }
    
    public void update(final byte[] array, int n, int n2) {
        if (this.preSig == null) {
            while (n2 > 0 && this.messageLength < this.mBuf.length) {
                this.update(array[n]);
                ++n;
                --n2;
            }
        }
        if (n2 > 0) {
            this.digest.update(array, n, n2);
        }
    }
    
    public void reset() {
        this.digest.reset();
        this.messageLength = 0;
        if (this.mBuf != null) {
            this.clearBlock(this.mBuf);
        }
        if (this.recoveredMessage != null) {
            this.clearBlock(this.recoveredMessage);
            this.recoveredMessage = null;
        }
        this.fullMessage = false;
        if (this.preSig != null) {
            this.preSig = null;
            this.clearBlock(this.preBlock);
            this.preBlock = null;
        }
    }
    
    public byte[] generateSignature() throws CryptoException {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        final byte[] array2 = new byte[8];
        this.LtoOSP(this.messageLength * 8, array2);
        this.digest.update(array2, 0, array2.length);
        this.digest.update(this.mBuf, 0, this.messageLength);
        this.digest.update(array, 0, array.length);
        byte[] standardSalt;
        if (this.standardSalt != null) {
            standardSalt = this.standardSalt;
        }
        else {
            standardSalt = new byte[this.saltLength];
            this.random.nextBytes(standardSalt);
        }
        this.digest.update(standardSalt, 0, standardSalt.length);
        final byte[] array3 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array3, 0);
        int n = 2;
        if (this.trailer == 188) {
            n = 1;
        }
        final int n2 = this.block.length - this.messageLength - standardSalt.length - this.hLen - n - 1;
        this.block[n2] = 1;
        System.arraycopy(this.mBuf, 0, this.block, n2 + 1, this.messageLength);
        System.arraycopy(standardSalt, 0, this.block, n2 + 1 + this.messageLength, standardSalt.length);
        final byte[] maskGeneratorFunction1 = this.maskGeneratorFunction1(array3, 0, array3.length, this.block.length - this.hLen - n);
        for (int i = 0; i != maskGeneratorFunction1.length; ++i) {
            final byte[] block = this.block;
            final int n3 = i;
            block[n3] ^= maskGeneratorFunction1[i];
        }
        System.arraycopy(array3, 0, this.block, this.block.length - this.hLen - n, this.hLen);
        if (this.trailer == 188) {
            this.block[this.block.length - 1] = -68;
        }
        else {
            this.block[this.block.length - 2] = (byte)(this.trailer >>> 8);
            this.block[this.block.length - 1] = (byte)this.trailer;
        }
        final byte[] block2 = this.block;
        final int n4 = 0;
        block2[n4] &= 0x7F;
        final byte[] processBlock = this.cipher.processBlock(this.block, 0, this.block.length);
        this.recoveredMessage = new byte[this.messageLength];
        this.fullMessage = (this.messageLength <= this.mBuf.length);
        System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
        this.clearBlock(this.mBuf);
        this.clearBlock(this.block);
        this.messageLength = 0;
        return processBlock;
    }
    
    public boolean verifySignature(final byte[] array) {
        final byte[] array2 = new byte[this.hLen];
        this.digest.doFinal(array2, 0);
        Label_0062: {
            if (this.preSig == null) {
                try {
                    this.updateWithRecoveredMessage(array);
                    break Label_0062;
                }
                catch (final Exception ex) {
                    return false;
                }
            }
            if (!Arrays.areEqual(this.preSig, array)) {
                throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
            }
        }
        final byte[] preBlock = this.preBlock;
        final int preMStart = this.preMStart;
        final int preTLength = this.preTLength;
        this.preSig = null;
        this.preBlock = null;
        final byte[] array3 = new byte[8];
        this.LtoOSP(this.recoveredMessage.length * 8, array3);
        this.digest.update(array3, 0, array3.length);
        if (this.recoveredMessage.length != 0) {
            this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
        }
        this.digest.update(array2, 0, array2.length);
        if (this.standardSalt != null) {
            this.digest.update(this.standardSalt, 0, this.standardSalt.length);
        }
        else {
            this.digest.update(preBlock, preMStart + this.recoveredMessage.length, this.saltLength);
        }
        final byte[] array4 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array4, 0);
        final int n = preBlock.length - preTLength - array4.length;
        boolean b = true;
        for (int i = 0; i != array4.length; ++i) {
            if (array4[i] != preBlock[n + i]) {
                b = false;
            }
        }
        this.clearBlock(preBlock);
        this.clearBlock(array4);
        if (!b) {
            this.fullMessage = false;
            this.messageLength = 0;
            this.clearBlock(this.recoveredMessage);
            return false;
        }
        if (this.messageLength != 0 && !this.isSameAs(this.mBuf, this.recoveredMessage)) {
            this.messageLength = 0;
            this.clearBlock(this.mBuf);
            return false;
        }
        this.messageLength = 0;
        this.clearBlock(this.mBuf);
        return true;
    }
    
    public boolean hasFullMessage() {
        return this.fullMessage;
    }
    
    public byte[] getRecoveredMessage() {
        return this.recoveredMessage;
    }
    
    private void ItoOSP(final int n, final byte[] array) {
        array[0] = (byte)(n >>> 24);
        array[1] = (byte)(n >>> 16);
        array[2] = (byte)(n >>> 8);
        array[3] = (byte)(n >>> 0);
    }
    
    private void LtoOSP(final long n, final byte[] array) {
        array[0] = (byte)(n >>> 56);
        array[1] = (byte)(n >>> 48);
        array[2] = (byte)(n >>> 40);
        array[3] = (byte)(n >>> 32);
        array[4] = (byte)(n >>> 24);
        array[5] = (byte)(n >>> 16);
        array[6] = (byte)(n >>> 8);
        array[7] = (byte)(n >>> 0);
    }
    
    private byte[] maskGeneratorFunction1(final byte[] array, final int n, final int n2, final int n3) {
        final byte[] array2 = new byte[n3];
        final byte[] array3 = new byte[this.hLen];
        final byte[] array4 = new byte[4];
        int i = 0;
        this.digest.reset();
        while (i < n3 / this.hLen) {
            this.ItoOSP(i, array4);
            this.digest.update(array, n, n2);
            this.digest.update(array4, 0, array4.length);
            this.digest.doFinal(array3, 0);
            System.arraycopy(array3, 0, array2, i * this.hLen, this.hLen);
            ++i;
        }
        if (i * this.hLen < n3) {
            this.ItoOSP(i, array4);
            this.digest.update(array, n, n2);
            this.digest.update(array4, 0, array4.length);
            this.digest.doFinal(array3, 0);
            System.arraycopy(array3, 0, array2, i * this.hLen, array2.length - i * this.hLen);
        }
        return array2;
    }
}
