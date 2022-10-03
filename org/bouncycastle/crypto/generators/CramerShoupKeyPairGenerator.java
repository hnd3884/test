package org.bouncycastle.crypto.generators;

import org.bouncycastle.util.BigIntegers;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.CramerShoupPublicKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupPrivateKeyParameters;
import org.bouncycastle.crypto.params.CramerShoupParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.CramerShoupKeyGenerationParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;

public class CramerShoupKeyPairGenerator implements AsymmetricCipherKeyPairGenerator
{
    private static final BigInteger ONE;
    private CramerShoupKeyGenerationParameters param;
    
    public void init(final KeyGenerationParameters keyGenerationParameters) {
        this.param = (CramerShoupKeyGenerationParameters)keyGenerationParameters;
    }
    
    public AsymmetricCipherKeyPair generateKeyPair() {
        final CramerShoupParameters parameters = this.param.getParameters();
        final CramerShoupPrivateKeyParameters generatePrivateKey = this.generatePrivateKey(this.param.getRandom(), parameters);
        final CramerShoupPublicKeyParameters calculatePublicKey = this.calculatePublicKey(parameters, generatePrivateKey);
        generatePrivateKey.setPk(calculatePublicKey);
        return new AsymmetricCipherKeyPair(calculatePublicKey, generatePrivateKey);
    }
    
    private BigInteger generateRandomElement(final BigInteger bigInteger, final SecureRandom secureRandom) {
        return BigIntegers.createRandomInRange(CramerShoupKeyPairGenerator.ONE, bigInteger.subtract(CramerShoupKeyPairGenerator.ONE), secureRandom);
    }
    
    private CramerShoupPrivateKeyParameters generatePrivateKey(final SecureRandom secureRandom, final CramerShoupParameters cramerShoupParameters) {
        final BigInteger p2 = cramerShoupParameters.getP();
        return new CramerShoupPrivateKeyParameters(cramerShoupParameters, this.generateRandomElement(p2, secureRandom), this.generateRandomElement(p2, secureRandom), this.generateRandomElement(p2, secureRandom), this.generateRandomElement(p2, secureRandom), this.generateRandomElement(p2, secureRandom));
    }
    
    private CramerShoupPublicKeyParameters calculatePublicKey(final CramerShoupParameters cramerShoupParameters, final CramerShoupPrivateKeyParameters cramerShoupPrivateKeyParameters) {
        final BigInteger g1 = cramerShoupParameters.getG1();
        final BigInteger g2 = cramerShoupParameters.getG2();
        final BigInteger p2 = cramerShoupParameters.getP();
        return new CramerShoupPublicKeyParameters(cramerShoupParameters, g1.modPow(cramerShoupPrivateKeyParameters.getX1(), p2).multiply(g2.modPow(cramerShoupPrivateKeyParameters.getX2(), p2)), g1.modPow(cramerShoupPrivateKeyParameters.getY1(), p2).multiply(g2.modPow(cramerShoupPrivateKeyParameters.getY2(), p2)), g1.modPow(cramerShoupPrivateKeyParameters.getZ(), p2));
    }
    
    static {
        ONE = BigInteger.valueOf(1L);
    }
}
