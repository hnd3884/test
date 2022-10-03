package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.math.ec.ECFieldElement;
import java.util.Random;
import org.bouncycastle.math.ec.ECConstants;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECPoint;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SM3Digest;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.Digest;

public class SM2Engine
{
    private final Digest digest;
    private boolean forEncryption;
    private ECKeyParameters ecKey;
    private ECDomainParameters ecParams;
    private int curveLength;
    private SecureRandom random;
    
    public SM2Engine() {
        this(new SM3Digest());
    }
    
    public SM2Engine(final Digest digest) {
        this.digest = digest;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) {
        this.forEncryption = forEncryption;
        if (forEncryption) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.ecKey = (ECKeyParameters)parametersWithRandom.getParameters();
            this.ecParams = this.ecKey.getParameters();
            if (((ECPublicKeyParameters)this.ecKey).getQ().multiply(this.ecParams.getH()).isInfinity()) {
                throw new IllegalArgumentException("invalid key: [h]Q at infinity");
            }
            this.random = parametersWithRandom.getRandom();
        }
        else {
            this.ecKey = (ECKeyParameters)cipherParameters;
            this.ecParams = this.ecKey.getParameters();
        }
        this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
    }
    
    public byte[] processBlock(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forEncryption) {
            return this.encrypt(array, n, n2);
        }
        return this.decrypt(array, n, n2);
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
    
    private byte[] encrypt(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, array2.length);
        final ECMultiplier basePointMultiplier = this.createBasePointMultiplier();
        byte[] encoded;
        ECPoint normalize;
        do {
            final BigInteger nextK = this.nextK();
            encoded = basePointMultiplier.multiply(this.ecParams.getG(), nextK).normalize().getEncoded(false);
            normalize = ((ECPublicKeyParameters)this.ecKey).getQ().multiply(nextK).normalize();
            this.kdf(this.digest, normalize, array2);
        } while (this.notEncrypted(array2, array, n));
        final byte[] array3 = new byte[this.digest.getDigestSize()];
        this.addFieldElement(this.digest, normalize.getAffineXCoord());
        this.digest.update(array, n, n2);
        this.addFieldElement(this.digest, normalize.getAffineYCoord());
        this.digest.doFinal(array3, 0);
        return Arrays.concatenate(encoded, array2, array3);
    }
    
    private byte[] decrypt(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        final byte[] array2 = new byte[this.curveLength * 2 + 1];
        System.arraycopy(array, n, array2, 0, array2.length);
        final ECPoint decodePoint = this.ecParams.getCurve().decodePoint(array2);
        if (decodePoint.multiply(this.ecParams.getH()).isInfinity()) {
            throw new InvalidCipherTextException("[h]C1 at infinity");
        }
        final ECPoint normalize = decodePoint.multiply(((ECPrivateKeyParameters)this.ecKey).getD()).normalize();
        final byte[] array3 = new byte[n2 - array2.length - this.digest.getDigestSize()];
        System.arraycopy(array, n + array2.length, array3, 0, array3.length);
        this.kdf(this.digest, normalize, array3);
        final byte[] array4 = new byte[this.digest.getDigestSize()];
        this.addFieldElement(this.digest, normalize.getAffineXCoord());
        this.digest.update(array3, 0, array3.length);
        this.addFieldElement(this.digest, normalize.getAffineYCoord());
        this.digest.doFinal(array4, 0);
        int n3 = 0;
        for (int i = 0; i != array4.length; ++i) {
            n3 |= (array4[i] ^ array[array2.length + array3.length + i]);
        }
        Arrays.fill(array2, (byte)0);
        Arrays.fill(array4, (byte)0);
        if (n3 != 0) {
            Arrays.fill(array3, (byte)0);
            throw new InvalidCipherTextException("invalid cipher text");
        }
        return array3;
    }
    
    private boolean notEncrypted(final byte[] array, final byte[] array2, final int n) {
        for (int i = 0; i != array.length; ++i) {
            if (array[i] != array2[n]) {
                return false;
            }
        }
        return true;
    }
    
    private void kdf(final Digest digest, final ECPoint ecPoint, final byte[] array) {
        final int digestSize = digest.getDigestSize();
        final byte[] array2 = new byte[Math.max(4, digestSize)];
        int i = 0;
        Memoable memoable = null;
        Memoable copy = null;
        if (digest instanceof Memoable) {
            this.addFieldElement(digest, ecPoint.getAffineXCoord());
            this.addFieldElement(digest, ecPoint.getAffineYCoord());
            memoable = (Memoable)digest;
            copy = memoable.copy();
        }
        int n = 0;
        while (i < array.length) {
            if (memoable != null) {
                memoable.reset(copy);
            }
            else {
                this.addFieldElement(digest, ecPoint.getAffineXCoord());
                this.addFieldElement(digest, ecPoint.getAffineYCoord());
            }
            Pack.intToBigEndian(++n, array2, 0);
            digest.update(array2, 0, 4);
            digest.doFinal(array2, 0);
            final int min = Math.min(digestSize, array.length - i);
            this.xor(array, array2, i, min);
            i += min;
        }
    }
    
    private void xor(final byte[] array, final byte[] array2, final int n, final int n2) {
        for (int i = 0; i != n2; ++i) {
            final int n3 = n + i;
            array[n3] ^= array2[i];
        }
    }
    
    private BigInteger nextK() {
        final int bitLength = this.ecParams.getN().bitLength();
        BigInteger bigInteger;
        do {
            bigInteger = new BigInteger(bitLength, this.random);
        } while (bigInteger.equals(ECConstants.ZERO) || bigInteger.compareTo(this.ecParams.getN()) >= 0);
        return bigInteger;
    }
    
    private void addFieldElement(final Digest digest, final ECFieldElement ecFieldElement) {
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(this.curveLength, ecFieldElement.toBigInteger());
        digest.update(unsignedByteArray, 0, unsignedByteArray.length);
    }
}
