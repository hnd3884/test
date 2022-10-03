package org.bouncycastle.crypto.agreement;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.DHKeyGenerationParameters;
import org.bouncycastle.crypto.generators.DHKeyPairGenerator;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import java.math.BigInteger;

public class DHAgreement
{
    private static final BigInteger ONE;
    private DHPrivateKeyParameters key;
    private DHParameters dhParams;
    private BigInteger privateValue;
    private SecureRandom random;
    
    public void init(final CipherParameters cipherParameters) {
        AsymmetricKeyParameter asymmetricKeyParameter;
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.random = parametersWithRandom.getRandom();
            asymmetricKeyParameter = (AsymmetricKeyParameter)parametersWithRandom.getParameters();
        }
        else {
            this.random = new SecureRandom();
            asymmetricKeyParameter = (AsymmetricKeyParameter)cipherParameters;
        }
        if (!(asymmetricKeyParameter instanceof DHPrivateKeyParameters)) {
            throw new IllegalArgumentException("DHEngine expects DHPrivateKeyParameters");
        }
        this.key = (DHPrivateKeyParameters)asymmetricKeyParameter;
        this.dhParams = this.key.getParameters();
    }
    
    public BigInteger calculateMessage() {
        final DHKeyPairGenerator dhKeyPairGenerator = new DHKeyPairGenerator();
        dhKeyPairGenerator.init(new DHKeyGenerationParameters(this.random, this.dhParams));
        final AsymmetricCipherKeyPair generateKeyPair = dhKeyPairGenerator.generateKeyPair();
        this.privateValue = ((DHPrivateKeyParameters)generateKeyPair.getPrivate()).getX();
        return ((DHPublicKeyParameters)generateKeyPair.getPublic()).getY();
    }
    
    public BigInteger calculateAgreement(final DHPublicKeyParameters dhPublicKeyParameters, final BigInteger bigInteger) {
        if (!dhPublicKeyParameters.getParameters().equals(this.dhParams)) {
            throw new IllegalArgumentException("Diffie-Hellman public key has wrong parameters.");
        }
        final BigInteger p2 = this.dhParams.getP();
        final BigInteger y = dhPublicKeyParameters.getY();
        if (y == null || y.compareTo(DHAgreement.ONE) <= 0 || y.compareTo(p2.subtract(DHAgreement.ONE)) >= 0) {
            throw new IllegalArgumentException("Diffie-Hellman public key is weak");
        }
        final BigInteger modPow = y.modPow(this.privateValue, p2);
        if (modPow.equals(DHAgreement.ONE)) {
            throw new IllegalStateException("Shared key can't be 1");
        }
        return bigInteger.modPow(this.key.getX(), p2).multiply(modPow).mod(p2);
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
