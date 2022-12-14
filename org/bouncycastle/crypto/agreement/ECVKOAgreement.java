package org.bouncycastle.crypto.agreement;

import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithUKM;
import org.bouncycastle.crypto.CipherParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.Digest;

public class ECVKOAgreement
{
    private final Digest digest;
    private ECPrivateKeyParameters key;
    private BigInteger ukm;
    
    public ECVKOAgreement(final Digest digest) {
        this.digest = digest;
    }
    
    public void init(final CipherParameters cipherParameters) {
        final ParametersWithUKM parametersWithUKM = (ParametersWithUKM)cipherParameters;
        this.key = (ECPrivateKeyParameters)parametersWithUKM.getParameters();
        this.ukm = toInteger(parametersWithUKM.getUKM());
    }
    
    public int getFieldSize() {
        return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
    }
    
    public byte[] calculateAgreement(final CipherParameters cipherParameters) {
        final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)cipherParameters;
        final ECDomainParameters parameters = ecPublicKeyParameters.getParameters();
        if (!parameters.equals(this.key.getParameters())) {
            throw new IllegalStateException("ECVKO public key has wrong domain parameters");
        }
        final ECPoint normalize = ecPublicKeyParameters.getQ().multiply(parameters.getH().multiply(this.ukm).multiply(this.key.getD()).mod(parameters.getN())).normalize();
        if (normalize.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECVKO");
        }
        return this.fromPoint(normalize.normalize());
    }
    
    private static BigInteger toInteger(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i != array2.length; ++i) {
            array2[i] = array[array.length - i - 1];
        }
        return new BigInteger(1, array2);
    }
    
    private byte[] fromPoint(final ECPoint ecPoint) {
        final BigInteger bigInteger = ecPoint.getAffineXCoord().toBigInteger();
        final BigInteger bigInteger2 = ecPoint.getAffineYCoord().toBigInteger();
        int n;
        if (bigInteger.toByteArray().length > 33) {
            n = 64;
        }
        else {
            n = 32;
        }
        final byte[] array = new byte[2 * n];
        final byte[] unsignedByteArray = BigIntegers.asUnsignedByteArray(n, bigInteger);
        final byte[] unsignedByteArray2 = BigIntegers.asUnsignedByteArray(n, bigInteger2);
        for (int i = 0; i != n; ++i) {
            array[i] = unsignedByteArray[n - i - 1];
        }
        for (int j = 0; j != n; ++j) {
            array[n + j] = unsignedByteArray2[n - j - 1];
        }
        this.digest.update(array, 0, array.length);
        final byte[] array2 = new byte[this.digest.getDigestSize()];
        this.digest.doFinal(array2, 0);
        return array2;
    }
}
