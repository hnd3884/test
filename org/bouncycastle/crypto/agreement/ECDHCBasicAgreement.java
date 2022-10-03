package org.bouncycastle.crypto.agreement;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.BasicAgreement;

public class ECDHCBasicAgreement implements BasicAgreement
{
    ECPrivateKeyParameters key;
    
    public void init(final CipherParameters cipherParameters) {
        this.key = (ECPrivateKeyParameters)cipherParameters;
    }
    
    public int getFieldSize() {
        return (this.key.getParameters().getCurve().getFieldSize() + 7) / 8;
    }
    
    public BigInteger calculateAgreement(final CipherParameters cipherParameters) {
        final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)cipherParameters;
        final ECDomainParameters parameters = ecPublicKeyParameters.getParameters();
        if (!parameters.equals(this.key.getParameters())) {
            throw new IllegalStateException("ECDHC public key has wrong domain parameters");
        }
        final ECPoint normalize = ecPublicKeyParameters.getQ().multiply(parameters.getH().multiply(this.key.getD()).mod(parameters.getN())).normalize();
        if (normalize.isInfinity()) {
            throw new IllegalStateException("Infinity is not a valid agreement value for ECDHC");
        }
        return normalize.getAffineXCoord().toBigInteger();
    }
}
