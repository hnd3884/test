package org.bouncycastle.crypto.signers;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.util.BigIntegers;
import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Signer;

public class X931Signer implements Signer
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
    @Deprecated
    public static final int TRAILER_SHA224 = 14540;
    private Digest digest;
    private AsymmetricBlockCipher cipher;
    private RSAKeyParameters kParam;
    private int trailer;
    private int keyBits;
    private byte[] block;
    
    public X931Signer(final AsymmetricBlockCipher cipher, final Digest digest, final boolean b) {
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
    
    public X931Signer(final AsymmetricBlockCipher asymmetricBlockCipher, final Digest digest) {
        this(asymmetricBlockCipher, digest, false);
    }
    
    public void init(final boolean b, final CipherParameters cipherParameters) {
        this.kParam = (RSAKeyParameters)cipherParameters;
        this.cipher.init(b, this.kParam);
        this.keyBits = this.kParam.getModulus().bitLength();
        this.block = new byte[(this.keyBits + 7) / 8];
        this.reset();
    }
    
    private void clearBlock(final byte[] array) {
        for (int i = 0; i != array.length; ++i) {
            array[i] = 0;
        }
    }
    
    public void update(final byte b) {
        this.digest.update(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) {
        this.digest.update(array, n, n2);
    }
    
    public void reset() {
        this.digest.reset();
    }
    
    public byte[] generateSignature() throws CryptoException {
        this.createSignatureBlock(this.trailer);
        final BigInteger bigInteger = new BigInteger(1, this.cipher.processBlock(this.block, 0, this.block.length));
        this.clearBlock(this.block);
        return BigIntegers.asUnsignedByteArray((this.kParam.getModulus().bitLength() + 7) / 8, bigInteger.min(this.kParam.getModulus().subtract(bigInteger)));
    }
    
    private void createSignatureBlock(final int n) {
        final int digestSize = this.digest.getDigestSize();
        int n2;
        if (n == 188) {
            n2 = this.block.length - digestSize - 1;
            this.digest.doFinal(this.block, n2);
            this.block[this.block.length - 1] = -68;
        }
        else {
            n2 = this.block.length - digestSize - 2;
            this.digest.doFinal(this.block, n2);
            this.block[this.block.length - 2] = (byte)(n >>> 8);
            this.block[this.block.length - 1] = (byte)n;
        }
        this.block[0] = 107;
        for (int i = n2 - 2; i != 0; --i) {
            this.block[i] = -69;
        }
        this.block[n2 - 1] = -70;
    }
    
    public boolean verifySignature(final byte[] array) {
        try {
            this.block = this.cipher.processBlock(array, 0, array.length);
        }
        catch (final Exception ex) {
            return false;
        }
        final BigInteger bigInteger = new BigInteger(1, this.block);
        BigInteger bigInteger2;
        if ((bigInteger.intValue() & 0xF) == 0xC) {
            bigInteger2 = bigInteger;
        }
        else {
            final BigInteger subtract = this.kParam.getModulus().subtract(bigInteger);
            if ((subtract.intValue() & 0xF) != 0xC) {
                return false;
            }
            bigInteger2 = subtract;
        }
        this.createSignatureBlock(this.trailer);
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(this.block.length, bigInteger2);
        boolean b = Arrays.constantTimeAreEqual(this.block, unsignedByteArray);
        if (this.trailer == 15052 && !b) {
            this.block[this.block.length - 2] = 64;
            b = Arrays.constantTimeAreEqual(this.block, unsignedByteArray);
        }
        this.clearBlock(this.block);
        this.clearBlock(unsignedByteArray);
        return b;
    }
}
