package org.bouncycastle.crypto.kems;

import org.bouncycastle.crypto.DerivationParameters;
import org.bouncycastle.crypto.params.KDFParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.ECMultiplier;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECKeyParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.DerivationFunction;
import java.math.BigInteger;
import org.bouncycastle.crypto.KeyEncapsulation;

public class ECIESKeyEncapsulation implements KeyEncapsulation
{
    private static final BigInteger ONE;
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private ECKeyParameters key;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;
    
    public ECIESKeyEncapsulation(final DerivationFunction kdf, final SecureRandom rnd) {
        this.kdf = kdf;
        this.rnd = rnd;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }
    
    public ECIESKeyEncapsulation(final DerivationFunction kdf, final SecureRandom rnd, final boolean cofactorMode, final boolean oldCofactorMode, final boolean singleHashMode) {
        this.kdf = kdf;
        this.rnd = rnd;
        this.CofactorMode = cofactorMode;
        this.OldCofactorMode = oldCofactorMode;
        this.SingleHashMode = singleHashMode;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ECKeyParameters)) {
            throw new IllegalArgumentException("EC key required");
        }
        this.key = (ECKeyParameters)cipherParameters;
    }
    
    public CipherParameters encrypt(final byte[] array, final int n, final int n2) throws IllegalArgumentException {
        if (!(this.key instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("Public key required for encryption");
        }
        final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)this.key;
        final ECDomainParameters parameters = ecPublicKeyParameters.getParameters();
        final ECCurve curve = parameters.getCurve();
        final BigInteger n3 = parameters.getN();
        final BigInteger h = parameters.getH();
        final BigInteger randomInRange = BigIntegers.createRandomInRange(ECIESKeyEncapsulation.ONE, n3, this.rnd);
        final ECPoint[] array2 = { this.createBasePointMultiplier().multiply(parameters.getG(), randomInRange), ecPublicKeyParameters.getQ().multiply(this.CofactorMode ? randomInRange.multiply(h).mod(n3) : randomInRange) };
        curve.normalizeAll(array2);
        final ECPoint ecPoint = array2[0];
        final ECPoint ecPoint2 = array2[1];
        final byte[] encoded = ecPoint.getEncoded(false);
        System.arraycopy(encoded, 0, array, n, encoded.length);
        return this.deriveKey(n2, encoded, ecPoint2.getAffineXCoord().getEncoded());
    }
    
    public CipherParameters encrypt(final byte[] array, final int n) {
        return this.encrypt(array, 0, n);
    }
    
    public CipherParameters decrypt(final byte[] array, final int n, final int n2, final int n3) throws IllegalArgumentException {
        if (!(this.key instanceof ECPrivateKeyParameters)) {
            throw new IllegalArgumentException("Private key required for encryption");
        }
        final ECPrivateKeyParameters ecPrivateKeyParameters = (ECPrivateKeyParameters)this.key;
        final ECDomainParameters parameters = ecPrivateKeyParameters.getParameters();
        final ECCurve curve = parameters.getCurve();
        final BigInteger n4 = parameters.getN();
        final BigInteger h = parameters.getH();
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        ECPoint ecPoint = curve.decodePoint(array2);
        if (this.CofactorMode || this.OldCofactorMode) {
            ecPoint = ecPoint.multiply(h);
        }
        BigInteger bigInteger = ecPrivateKeyParameters.getD();
        if (this.CofactorMode) {
            bigInteger = bigInteger.multiply(h.modInverse(n4)).mod(n4);
        }
        return this.deriveKey(n3, array2, ecPoint.multiply(bigInteger).normalize().getAffineXCoord().getEncoded());
    }
    
    public CipherParameters decrypt(final byte[] array, final int n) {
        return this.decrypt(array, 0, array.length, n);
    }
    
    protected ECMultiplier createBasePointMultiplier() {
        return new FixedPointCombMultiplier();
    }
    
    protected KeyParameter deriveKey(final int n, final byte[] array, final byte[] array2) {
        byte[] concatenate = array2;
        if (!this.SingleHashMode) {
            concatenate = Arrays.concatenate(array, array2);
            Arrays.fill(array2, (byte)0);
        }
        try {
            this.kdf.init(new KDFParameters(concatenate, null));
            final byte[] array3 = new byte[n];
            this.kdf.generateBytes(array3, 0, array3.length);
            return new KeyParameter(array3);
        }
        finally {
            Arrays.fill(concatenate, (byte)0);
        }
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
