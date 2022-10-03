package org.bouncycastle.crypto.agreement;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.BasicAgreement;

public class ECDHBasicAgreement implements BasicAgreement
{
    private ECPrivateKeyParameters key;
    
    public void init(final CipherParameters cipherParameters) {
        this.key = (ECPrivateKeyParameters)cipherParameters;
    }
    
    public int getFieldSize() {
        return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
    }
    
    public BigInteger calculateAgreement(final CipherParameters cipherParameters) {
        final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)cipherParameters;
        if (!ecPublicKeyParameters.getParameters().equals(this.key.getParameters())) {
            throw new IllegalStateException("ECDH public key has wrong domain parameters");
        }
        final ECPoint decodePoint = this.key.getParameters().getCurve().decodePoint(ecPublicKeyParameters.getQ().getEncoded(false));
        if (decodePoint.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid public key for ECDH");
        }
        final ECPoint normalize = decodePoint.multiply(this.key.getD()).normalize();
        if (normalize.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECDH");
        }
        return normalize.getAffineXCoord().toBigInteger();
    }
}
