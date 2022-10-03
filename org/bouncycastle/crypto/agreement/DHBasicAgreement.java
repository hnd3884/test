package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.BasicAgreement;

public class DHBasicAgreement implements BasicAgreement
{
    private static final BigInteger ONE;
    private DHPrivateKeyParameters key;
    private DHParameters dhParams;
    
    public void init(final CipherParameters cipherParameters) {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (cipherParameters instanceof ParametersWithRandom) {
            asymmetricKeyParameter = (AsymmetricKeyParameter)((ParametersWithRandom)cipherParameters).getParameters();
        }
        else {
            asymmetricKeyParameter = (AsymmetricKeyParameter)cipherParameters;
        }
        if (!(asymmetricKeyParameter instanceof DHPrivateKeyParameters)) {
            throw new IllegalArgumentException("DHEngine expects DHPrivateKeyParameters");
        }
        this.key = (DHPrivateKeyParameters)asymmetricKeyParameter;
        this.dhParams = this.key.getParameters();
    }
    
    public int getFieldSize() {
        return (this.key.getParameters().getP().bitLength() + 7) / 8;
    }
    
    public BigInteger calculateAgreement(final CipherParameters cipherParameters) {
        final DHPublicKeyParameters dhPublicKeyParameters = (DHPublicKeyParameters)cipherParameters;
        if (!dhPublicKeyParameters.getParameters().equals(this.dhParams)) {
            throw new IllegalArgumentException("Diffie-Hellman public key has wrong parameters.");
        }
        final BigInteger p = this.dhParams.getP();
        final BigInteger y = dhPublicKeyParameters.getY();
        if (y == null || y.compareTo(DHBasicAgreement.ONE) <= 0 || y.compareTo(p.subtract(DHBasicAgreement.ONE)) >= 0) {
            throw new IllegalArgumentException("Diffie-Hellman public key is weak");
        }
        final BigInteger modPow = y.modPow(this.key.getX(), p);
        if (modPow.equals(DHBasicAgreement.ONE)) {
            throw new IllegalStateException("Shared key can't be 1");
        }
        return modPow;
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
