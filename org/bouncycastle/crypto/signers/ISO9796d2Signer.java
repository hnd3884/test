package org.bouncycastle.crypto.signers;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.SignerWithRecovery;

public class ISO9796d2Signer implements SignerWithRecovery
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
    private int trailer;
    private int keyBits;
    private byte[] block;
    private byte[] mBuf;
    private int messageLength;
    private boolean fullMessage;
    private byte[] recoveredMessage;
    private byte[] preSig;
    private byte[] preBlock;
    
    public ISO9796d2Signer(final AsymmetricBlockCipher cipher, final Digest digest, final boolean b) {
        this.cipher = cipher;
        this.digest = digest;
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
    
    public ISO9796d2Signer(final AsymmetricBlockCipher asymmetricBlockCipher, final Digest digest) {
        this(asymmetricBlockCipher, digest, false);
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        final RSAKeyParameters rsaKeyParameters = (RSAKeyParameters)cipherParameters;
        this.cipher.init(b, rsaKeyParameters);
        this.keyBits = rsaKeyParameters.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        if (this.trailer == 188) {
            this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - 2];
        }
        else {
            this.mBuf = new byte[this.block.length - this.digest.getDigestSize() - 3];
        }
        this.reset();
    }
    
    private boolean isSameAs(final byte[] array, final byte[] array2) {
        boolean b = true;
        if (this.messageLength > this.mBuf.length) {
            if (this.mBuf.length > array2.length) {
                b = false;
            }
            for (int i = 0; i != this.mBuf.length; ++i) {
                if (array[i] != array2[i]) {
                    b = false;
                }
            }
        }
        else {
            if (this.messageLength != array2.length) {
                b = false;
            }
            for (int j = 0; j != array2.length; ++j) {
                if (array[j] != array2[j]) {
                    b = false;
                }
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
        final byte[] processBlock = this.cipher.processBlock(preSig, 0, preSig.length);
        if (((processBlock[0] & 0xC0) ^ 0x40) != 0x0) {
            throw new InvalidCipherTextException("malformed signature");
        }
        if (((processBlock[processBlock.length - 1] & 0xF) ^ 0xC) != 0x0) {
            throw new InvalidCipherTextException("malformed signature");
        }
        int n;
        if (((processBlock[processBlock.length - 1] & 0xFF) ^ 0xBC) == 0x0) {
            n = 1;
        }
        else {
            final int n2 = (processBlock[processBlock.length - 2] & 0xFF) << 8 | (processBlock[processBlock.length - 1] & 0xFF);
            final Integer trailer = ISOTrailers.getTrailer(this.digest);
            if (trailer == null) {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
            final int intValue = trailer;
            if (n2 != intValue && (intValue != 15052 || n2 != 16588)) {
                throw new IllegalStateException("signer initialised with wrong digest for trailer " + n2);
            }
            n = 2;
        }
        int n3;
        for (n3 = 0; n3 != processBlock.length && ((processBlock[n3] & 0xF) ^ 0xA) != 0x0; ++n3) {}
        ++n3;
        final int n4 = processBlock.length - n - this.digest.getDigestSize();
        if (n4 - n3 <= 0) {
            throw new InvalidCipherTextException("malformed block");
        }
        if ((processBlock[0] & 0x20) == 0x0) {
            this.fullMessage = true;
            System.arraycopy(processBlock, n3, this.recoveredMessage = new byte[n4 - n3], 0, this.recoveredMessage.length);
        }
        else {
            this.fullMessage = false;
            System.arraycopy(processBlock, n3, this.recoveredMessage = new byte[n4 - n3], 0, this.recoveredMessage.length);
        }
        this.preSig = preSig;
        this.preBlock = processBlock;
        this.digest.update(this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = this.recoveredMessage.length;
        System.arraycopy(this.recoveredMessage, 0, this.mBuf, 0, this.recoveredMessage.length);
    }
    
    public void update(final byte b) {
        this.digest.update(b);
        if (this.messageLength < this.mBuf.length) {
            this.mBuf[this.messageLength] = b;
        }
        ++this.messageLength;
    }
    
    public void update(final byte[] array, int n, int n2) {
        while (n2 > 0 && this.messageLength < this.mBuf.length) {
            this.update(array[n]);
            ++n;
            --n2;
        }
        this.digest.update(array, n, n2);
        this.messageLength += n2;
    }
    
    public void reset() {
        this.digest.reset();
        this.messageLength = 0;
        this.clearBlock(this.mBuf);
        if (this.recoveredMessage != null) {
            this.clearBlock(this.recoveredMessage);
        }
        this.recoveredMessage = null;
        this.fullMessage = false;
        if (this.preSig != null) {
            this.preSig = null;
            this.clearBlock(this.preBlock);
            this.preBlock = null;
        }
    }
    
    public byte[] generateSignature() throws CryptoException {
        final int digestSize = this.digest.getDigestSize();
        int n;
        int n2;
        if (this.trailer == 188) {
            n = 8;
            n2 = this.block.length - digestSize - 1;
            this.digest.doFinal(this.block, n2);
            this.block[this.block.length - 1] = -68;
        }
        else {
            n = 16;
            n2 = this.block.length - digestSize - 2;
            this.digest.doFinal(this.block, n2);
            this.block[this.block.length - 2] = (byte)(this.trailer >>> 8);
            this.block[this.block.length - 1] = (byte)this.trailer;
        }
        final int n3 = (digestSize + this.messageLength) * 8 + n + 4 - this.keyBits;
        byte b;
        int n5;
        if (n3 > 0) {
            final int n4 = this.messageLength - (n3 + 7) / 8;
            b = 96;
            n5 = n2 - n4;
            System.arraycopy(this.mBuf, 0, this.block, n5, n4);
            this.recoveredMessage = new byte[n4];
        }
        else {
            b = 64;
            n5 = n2 - this.messageLength;
            System.arraycopy(this.mBuf, 0, this.block, n5, this.messageLength);
            this.recoveredMessage = new byte[this.messageLength];
        }
        if (n5 - 1 > 0) {
            for (int i = n5 - 1; i != 0; --i) {
                this.block[i] = -69;
            }
            final byte[] block = this.block;
            final int n6 = n5 - 1;
            block[n6] ^= 0x1;
            this.block[0] = 11;
            final byte[] block2 = this.block;
            final int n7 = 0;
            block2[n7] |= b;
        }
        else {
            this.block[0] = 10;
            final byte[] block3 = this.block;
            final int n8 = 0;
            block3[n8] |= b;
        }
        final byte[] processBlock = this.cipher.processBlock(this.block, 0, this.block.length);
        this.fullMessage = ((b & 0x20) == 0x0);
        System.arraycopy(this.mBuf, 0, this.recoveredMessage, 0, this.recoveredMessage.length);
        this.messageLength = 0;
        this.clearBlock(this.mBuf);
        this.clearBlock(this.block);
        return processBlock;
    }
    
    public boolean verifySignature(final byte[] array) {
        byte[] array2 = null;
        Label_0065: {
            if (this.preSig == null) {
                try {
                    array2 = this.cipher.processBlock(array, 0, array.length);
                    break Label_0065;
                }
                catch (final Exception ex) {
                    return false;
                }
            }
            if (!Arrays.areEqual(this.preSig, array)) {
                throw new IllegalStateException("updateWithRecoveredMessage called on different signature");
            }
            array2 = this.preBlock;
            this.preSig = null;
            this.preBlock = null;
        }
        if (((array2[0] & 0xC0) ^ 0x40) != 0x0) {
            return this.returnFalse(array2);
        }
        if (((array2[array2.length - 1] & 0xF) ^ 0xC) != 0x0) {
            return this.returnFalse(array2);
        }
        int n;
        if (((array2[array2.length - 1] & 0xFF) ^ 0xBC) == 0x0) {
            n = 1;
        }
        else {
            final int n2 = (array2[array2.length - 2] & 0xFF) << 8 | (array2[array2.length - 1] & 0xFF);
            final Integer trailer = ISOTrailers.getTrailer(this.digest);
            if (trailer == null) {
                throw new IllegalArgumentException("unrecognised hash in signature");
            }
            final int intValue = trailer;
            if (n2 != intValue && (intValue != 15052 || n2 != 16588)) {
                throw new IllegalStateException("signer initialised with wrong digest for trailer " + n2);
            }
            n = 2;
        }
        int n3;
        for (n3 = 0; n3 != array2.length && ((array2[n3] & 0xF) ^ 0xA) != 0x0; ++n3) {}
        ++n3;
        final byte[] array3 = new byte[this.digest.getDigestSize()];
        final int n4 = array2.length - n - array3.length;
        if (n4 - n3 <= 0) {
            return this.returnFalse(array2);
        }
        if ((array2[0] & 0x20) == 0x0) {
            this.fullMessage = true;
            if (this.messageLength > n4 - n3) {
                return this.returnFalse(array2);
            }
            this.digest.reset();
            this.digest.update(array2, n3, n4 - n3);
            this.digest.doFinal(array3, 0);
            boolean b = true;
            for (int i = 0; i != array3.length; ++i) {
                final byte[] array4 = array2;
                final int n5 = n4 + i;
                array4[n5] ^= array3[i];
                if (array2[n4 + i] != 0) {
                    b = false;
                }
            }
            if (!b) {
                return this.returnFalse(array2);
            }
            System.arraycopy(array2, n3, this.recoveredMessage = new byte[n4 - n3], 0, this.recoveredMessage.length);
        }
        else {
            this.fullMessage = false;
            this.digest.doFinal(array3, 0);
            boolean b2 = true;
            for (int j = 0; j != array3.length; ++j) {
                final byte[] array5 = array2;
                final int n6 = n4 + j;
                array5[n6] ^= array3[j];
                if (array2[n4 + j] != 0) {
                    b2 = false;
                }
            }
            if (!b2) {
                return this.returnFalse(array2);
            }
            System.arraycopy(array2, n3, this.recoveredMessage = new byte[n4 - n3], 0, this.recoveredMessage.length);
        }
        if (this.messageLength != 0 && !this.isSameAs(this.mBuf, this.recoveredMessage)) {
            return this.returnFalse(array2);
        }
        this.clearBlock(this.mBuf);
        this.clearBlock(array2);
        this.messageLength = 0;
        return true;
    }
    
    private boolean returnFalse(final byte[] array) {
        this.messageLength = 0;
        this.clearBlock(this.mBuf);
        this.clearBlock(array);
        return false;
    }
    
    public boolean hasFullMessage() {
        return this.fullMessage;
    }
    
    public byte[] getRecoveredMessage() {
        return this.recoveredMessage;
    }
}
