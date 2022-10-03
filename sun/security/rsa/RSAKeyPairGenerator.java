package sun.security.rsa;

import sun.security.util.SecurityProviderConstants;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Random;
import sun.security.jca.JCAUtil;
import java.security.KeyPair;
import java.security.ProviderException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.SecureRandom;
import sun.security.x509.AlgorithmId;
import java.math.BigInteger;
import java.security.KeyPairGeneratorSpi;

public abstract class RSAKeyPairGenerator extends KeyPairGeneratorSpi
{
    private BigInteger publicExponent;
    private int keySize;
    private final RSAUtil.KeyType type;
    private AlgorithmId rsaId;
    private SecureRandom random;
    
    RSAKeyPairGenerator(final RSAUtil.KeyType type, final int n) {
        this.type = type;
        this.initialize(n, null);
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        try {
            this.initialize(new RSAKeyGenParameterSpec(n, RSAKeyGenParameterSpec.F4), secureRandom);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof RSAKeyGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Params must be instance of RSAKeyGenParameterSpec");
        }
        final RSAKeyGenParameterSpec rsaKeyGenParameterSpec = (RSAKeyGenParameterSpec)algorithmParameterSpec;
        final int keysize = rsaKeyGenParameterSpec.getKeysize();
        BigInteger publicExponent = rsaKeyGenParameterSpec.getPublicExponent();
        final AlgorithmParameterSpec keyParams = rsaKeyGenParameterSpec.getKeyParams();
        if (publicExponent == null) {
            publicExponent = RSAKeyGenParameterSpec.F4;
        }
        else {
            if (publicExponent.compareTo(RSAKeyGenParameterSpec.F0) < 0) {
                throw new InvalidAlgorithmParameterException("Public exponent must be 3 or larger");
            }
            if (publicExponent.bitLength() > keysize) {
                throw new InvalidAlgorithmParameterException("Public exponent must be smaller than key size");
            }
        }
        try {
            RSAKeyFactory.checkKeyLengths(keysize, publicExponent, 512, 65536);
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidAlgorithmParameterException("Invalid key sizes", ex);
        }
        try {
            this.rsaId = RSAUtil.createAlgorithmId(this.type, keyParams);
        }
        catch (final ProviderException ex2) {
            throw new InvalidAlgorithmParameterException("Invalid key parameters", ex2);
        }
        this.keySize = keysize;
        this.publicExponent = publicExponent;
        this.random = random;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        final int n = this.keySize + 1 >> 1;
        final int n2 = this.keySize - n;
        if (this.random == null) {
            this.random = JCAUtil.getSecureRandom();
        }
        final BigInteger publicExponent = this.publicExponent;
        BigInteger probablePrime;
        BigInteger multiply;
        BigInteger probablePrime2;
        BigInteger subtract;
        BigInteger subtract2;
        BigInteger multiply2;
        do {
            probablePrime = BigInteger.probablePrime(n, this.random);
            do {
                probablePrime2 = BigInteger.probablePrime(n2, this.random);
                if (probablePrime.compareTo(probablePrime2) < 0) {
                    final BigInteger bigInteger = probablePrime;
                    probablePrime = probablePrime2;
                    probablePrime2 = bigInteger;
                }
                multiply = probablePrime.multiply(probablePrime2);
            } while (multiply.bitLength() < this.keySize);
            subtract = probablePrime.subtract(BigInteger.ONE);
            subtract2 = probablePrime2.subtract(BigInteger.ONE);
            multiply2 = subtract.multiply(subtract2);
        } while (!publicExponent.gcd(multiply2).equals(BigInteger.ONE));
        final BigInteger modInverse = publicExponent.modInverse(multiply2);
        final BigInteger mod = modInverse.mod(subtract);
        final BigInteger mod2 = modInverse.mod(subtract2);
        final BigInteger modInverse2 = probablePrime2.modInverse(probablePrime);
        try {
            return new KeyPair(new RSAPublicKeyImpl(this.rsaId, multiply, publicExponent), new RSAPrivateCrtKeyImpl(this.rsaId, multiply, publicExponent, modInverse, probablePrime, probablePrime2, mod, mod2, modInverse2));
        }
        catch (final InvalidKeyException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static final class Legacy extends RSAKeyPairGenerator
    {
        public Legacy() {
            super(RSAUtil.KeyType.RSA, SecurityProviderConstants.DEF_RSA_KEY_SIZE);
        }
    }
    
    public static final class PSS extends RSAKeyPairGenerator
    {
        public PSS() {
            super(RSAUtil.KeyType.PSS, SecurityProviderConstants.DEF_RSASSA_PSS_KEY_SIZE);
        }
    }
}
