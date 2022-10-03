package org.bouncycastle.crypto.agreement;

import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Memoable;
import java.math.BigInteger;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.bouncycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.Digest;

public class SM2KeyExchange
{
    private final Digest digest;
    private byte[] userID;
    private ECPrivateKeyParameters staticKey;
    private ECPoint staticPubPoint;
    private ECPoint ephemeralPubPoint;
    private ECDomainParameters ecParams;
    private int w;
    private ECPrivateKeyParameters ephemeralKey;
    private boolean initiator;
    
    public SM2KeyExchange() {
        this(new SM3Digest());
    }
    
    public SM2KeyExchange(final Digest digest) {
        this.digest = digest;
    }
    
    public void init(final CipherParameters cipherParameters) {
        SM2KeyExchangePrivateParameters sm2KeyExchangePrivateParameters;
        if (cipherParameters instanceof ParametersWithID) {
            sm2KeyExchangePrivateParameters = (SM2KeyExchangePrivateParameters)((ParametersWithID)cipherParameters).getParameters();
            this.userID = ((ParametersWithID)cipherParameters).getID();
        }
        else {
            sm2KeyExchangePrivateParameters = (SM2KeyExchangePrivateParameters)cipherParameters;
            this.userID = new byte[0];
        }
        this.initiator = sm2KeyExchangePrivateParameters.isInitiator();
        this.staticKey = sm2KeyExchangePrivateParameters.getStaticPrivateKey();
        this.ephemeralKey = sm2KeyExchangePrivateParameters.getEphemeralPrivateKey();
        this.ecParams = this.staticKey.getParameters();
        this.staticPubPoint = sm2KeyExchangePrivateParameters.getStaticPublicPoint();
        this.ephemeralPubPoint = sm2KeyExchangePrivateParameters.getEphemeralPublicPoint();
        this.w = this.ecParams.getCurve().getFieldSize() / 2 - 1;
    }
    
    public byte[] calculateKey(final int n, final CipherParameters cipherParameters) {
        SM2KeyExchangePublicParameters sm2KeyExchangePublicParameters;
        byte[] id;
        if (cipherParameters instanceof ParametersWithID) {
            sm2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)((ParametersWithID)cipherParameters).getParameters();
            id = ((ParametersWithID)cipherParameters).getID();
        }
        else {
            sm2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)cipherParameters;
            id = new byte[0];
        }
        final byte[] z = this.getZ(this.digest, this.userID, this.staticPubPoint);
        final byte[] z2 = this.getZ(this.digest, id, sm2KeyExchangePublicParameters.getStaticPublicKey().getQ());
        final ECPoint calculateU = this.calculateU(sm2KeyExchangePublicParameters);
        byte[] array;
        if (this.initiator) {
            array = this.kdf(calculateU, z, z2, n);
        }
        else {
            array = this.kdf(calculateU, z2, z, n);
        }
        return array;
    }
    
    public byte[][] calculateKeyWithConfirmation(final int n, final byte[] array, final CipherParameters cipherParameters) {
        SM2KeyExchangePublicParameters sm2KeyExchangePublicParameters;
        byte[] id;
        if (cipherParameters instanceof ParametersWithID) {
            sm2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)((ParametersWithID)cipherParameters).getParameters();
            id = ((ParametersWithID)cipherParameters).getID();
        }
        else {
            sm2KeyExchangePublicParameters = (SM2KeyExchangePublicParameters)cipherParameters;
            id = new byte[0];
        }
        if (this.initiator && array == null) {
            throw new IllegalArgumentException("if initiating, confirmationTag must be set");
        }
        final byte[] z = this.getZ(this.digest, this.userID, this.staticPubPoint);
        final byte[] z2 = this.getZ(this.digest, id, sm2KeyExchangePublicParameters.getStaticPublicKey().getQ());
        final ECPoint calculateU = this.calculateU(sm2KeyExchangePublicParameters);
        if (!this.initiator) {
            final byte[] kdf = this.kdf(calculateU, z2, z, n);
            final byte[] calculateInnerHash = this.calculateInnerHash(this.digest, calculateU, z2, z, sm2KeyExchangePublicParameters.getEphemeralPublicKey().getQ(), this.ephemeralPubPoint);
            return new byte[][] { kdf, this.S1(this.digest, calculateU, calculateInnerHash), this.S2(this.digest, calculateU, calculateInnerHash) };
        }
        final byte[] kdf2 = this.kdf(calculateU, z, z2, n);
        final byte[] calculateInnerHash2 = this.calculateInnerHash(this.digest, calculateU, z, z2, this.ephemeralPubPoint, sm2KeyExchangePublicParameters.getEphemeralPublicKey().getQ());
        if (!Arrays.constantTimeAreEqual(this.S1(this.digest, calculateU, calculateInnerHash2), array)) {
            throw new IllegalStateException("confirmation tag mismatch");
        }
        return new byte[][] { kdf2, this.S2(this.digest, calculateU, calculateInnerHash2) };
    }
    
    private ECPoint calculateU(final SM2KeyExchangePublicParameters sm2KeyExchangePublicParameters) {
        final ECPoint q = sm2KeyExchangePublicParameters.getStaticPublicKey().getQ();
        final ECPoint q2 = sm2KeyExchangePublicParameters.getEphemeralPublicKey().getQ();
        final BigInteger reduce = this.reduce(this.ephemeralPubPoint.getAffineXCoord().toBigInteger());
        final BigInteger reduce2 = this.reduce(q2.getAffineXCoord().toBigInteger());
        final BigInteger mod = this.ecParams.getH().multiply(this.staticKey.getD().add(reduce.multiply(this.ephemeralKey.getD()))).mod(this.ecParams.getN());
        return ECAlgorithms.sumOfTwoMultiplies(q, mod, q2, mod.multiply(reduce2).mod(this.ecParams.getN())).normalize();
    }
    
    private byte[] kdf(final ECPoint ecPoint, final byte[] array, final byte[] array2, final int n) {
        final int digestSize = this.digest.getDigestSize();
        final byte[] array3 = new byte[Math.max(4, digestSize)];
        final byte[] array4 = new byte[(n + 7) / 8];
        int i = 0;
        Memoable memoable = null;
        Memoable copy = null;
        if (this.digest instanceof Memoable) {
            this.addFieldElement(this.digest, ecPoint.getAffineXCoord());
            this.addFieldElement(this.digest, ecPoint.getAffineYCoord());
            this.digest.update(array, 0, array.length);
            this.digest.update(array2, 0, array2.length);
            memoable = (Memoable)this.digest;
            copy = memoable.copy();
        }
        int n2 = 0;
        while (i < array4.length) {
            if (memoable != null) {
                memoable.reset(copy);
            }
            else {
                this.addFieldElement(this.digest, ecPoint.getAffineXCoord());
                this.addFieldElement(this.digest, ecPoint.getAffineYCoord());
                this.digest.update(array, 0, array.length);
                this.digest.update(array2, 0, array2.length);
            }
            Pack.intToBigEndian(++n2, array3, 0);
            this.digest.update(array3, 0, 4);
            this.digest.doFinal(array3, 0);
            final int min = Math.min(digestSize, array4.length - i);
            System.arraycopy(array3, 0, array4, i, min);
            i += min;
        }
        return array4;
    }
    
    private BigInteger reduce(final BigInteger bigInteger) {
        return bigInteger.and(BigInteger.valueOf(1L).shiftLeft(this.w).subtract(BigInteger.valueOf(1L))).setBit(this.w);
    }
    
    private byte[] S1(final Digest digest, final ECPoint ecPoint, final byte[] array) {
        digest.update((byte)2);
        this.addFieldElement(digest, ecPoint.getAffineYCoord());
        digest.update(array, 0, array.length);
        return this.digestDoFinal();
    }
    
    private byte[] calculateInnerHash(final Digest digest, final ECPoint ecPoint, final byte[] array, final byte[] array2, final ECPoint ecPoint2, final ECPoint ecPoint3) {
        this.addFieldElement(digest, ecPoint.getAffineXCoord());
        digest.update(array, 0, array.length);
        digest.update(array2, 0, array2.length);
        this.addFieldElement(digest, ecPoint2.getAffineXCoord());
        this.addFieldElement(digest, ecPoint2.getAffineYCoord());
        this.addFieldElement(digest, ecPoint3.getAffineXCoord());
        this.addFieldElement(digest, ecPoint3.getAffineYCoord());
        return this.digestDoFinal();
    }
    
    private byte[] S2(final Digest digest, final ECPoint ecPoint, final byte[] array) {
        digest.update((byte)3);
        this.addFieldElement(digest, ecPoint.getAffineYCoord());
        digest.update(array, 0, array.length);
        return this.digestDoFinal();
    }
    
    private byte[] getZ(final Digest digest, final byte[] array, final ECPoint ecPoint) {
        this.addUserID(digest, array);
        this.addFieldElement(digest, this.ecParams.getCurve().getA());
        this.addFieldElement(digest, this.ecParams.getCurve().getB());
        this.addFieldElement(digest, this.ecParams.getG().getAffineXCoord());
        this.addFieldElement(digest, this.ecParams.getG().getAffineYCoord());
        this.addFieldElement(digest, ecPoint.getAffineXCoord());
        this.addFieldElement(digest, ecPoint.getAffineYCoord());
        return this.digestDoFinal();
    }
    
    private void addUserID(final Digest digest, final byte[] array) {
        final int n = array.length * 8;
        digest.update((byte)(n >>> 8));
        digest.update((byte)n);
        digest.update(array, 0, array.length);
    }
    
    private void addFieldElement(final Digest digest, final ECFieldElement ecFieldElement) {
        final byte[] encoded = ecFieldElement.getEncoded();
        digest.update(encoded, 0, encoded.length);
    }
    
    private byte[] digestDoFinal() {
        final byte[] array = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array, 0);
        return array;
    }
}
