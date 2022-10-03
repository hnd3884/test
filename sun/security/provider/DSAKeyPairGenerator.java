package sun.security.provider;

import java.security.interfaces.DSAParams;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import sun.security.jca.JCAUtil;
import java.security.KeyPair;
import sun.security.util.SecurityProviderConstants;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.security.spec.DSAParameterSpec;
import java.security.KeyPairGenerator;

class DSAKeyPairGenerator extends KeyPairGenerator
{
    private int plen;
    private int qlen;
    boolean forceNewParameters;
    private DSAParameterSpec params;
    private SecureRandom random;
    
    DSAKeyPairGenerator(final int n) {
        super("DSA");
        this.initialize(n, null);
    }
    
    private static void checkStrength(final int n, final int n2) {
        if (n < 512 || n > 1024 || n % 64 != 0 || n2 != 160) {
            if (n == 2048) {
                if (n2 == 224) {
                    return;
                }
                if (n2 == 256) {
                    return;
                }
            }
            if (n != 3072 || n2 != 256) {
                throw new InvalidParameterException("Unsupported prime and subprime size combination: " + n + ", " + n2);
            }
        }
    }
    
    @Override
    public void initialize(final int n, final SecureRandom secureRandom) {
        this.init(n, secureRandom, false);
    }
    
    @Override
    public void initialize(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof DSAParameterSpec)) {
            throw new InvalidAlgorithmParameterException("Inappropriate parameter");
        }
        this.init((DSAParameterSpec)algorithmParameterSpec, secureRandom, false);
    }
    
    void init(final int plen, final SecureRandom random, final boolean forceNewParameters) {
        final int defDSASubprimeSize = SecurityProviderConstants.getDefDSASubprimeSize(plen);
        checkStrength(plen, defDSASubprimeSize);
        this.plen = plen;
        this.qlen = defDSASubprimeSize;
        this.params = null;
        this.random = random;
        this.forceNewParameters = forceNewParameters;
    }
    
    void init(final DSAParameterSpec params, final SecureRandom random, final boolean forceNewParameters) {
        final int bitLength = params.getP().bitLength();
        final int bitLength2 = params.getQ().bitLength();
        checkStrength(bitLength, bitLength2);
        this.plen = bitLength;
        this.qlen = bitLength2;
        this.params = params;
        this.random = random;
        this.forceNewParameters = forceNewParameters;
    }
    
    @Override
    public KeyPair generateKeyPair() {
        if (this.random == null) {
            this.random = JCAUtil.getSecureRandom();
        }
        DSAParameterSpec dsaParameterSpec;
        try {
            if (this.forceNewParameters) {
                dsaParameterSpec = ParameterCache.getNewDSAParameterSpec(this.plen, this.qlen, this.random);
            }
            else {
                if (this.params == null) {
                    this.params = ParameterCache.getDSAParameterSpec(this.plen, this.qlen, this.random);
                }
                dsaParameterSpec = this.params;
            }
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException(ex);
        }
        return this.generateKeyPair(dsaParameterSpec.getP(), dsaParameterSpec.getQ(), dsaParameterSpec.getG(), this.random);
    }
    
    private KeyPair generateKeyPair(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final SecureRandom secureRandom) {
        final BigInteger generateX = this.generateX(secureRandom, bigInteger2);
        final BigInteger generateY = this.generateY(generateX, bigInteger, bigInteger3);
        try {
            DSAPublicKey dsaPublicKey;
            if (DSAKeyFactory.SERIAL_INTEROP) {
                dsaPublicKey = new DSAPublicKey(generateY, bigInteger, bigInteger2, bigInteger3);
            }
            else {
                dsaPublicKey = new DSAPublicKeyImpl(generateY, bigInteger, bigInteger2, bigInteger3);
            }
            return new KeyPair(dsaPublicKey, new DSAPrivateKey(generateX, bigInteger, bigInteger2, bigInteger3));
        }
        catch (final InvalidKeyException ex) {
            throw new ProviderException(ex);
        }
    }
    
    private BigInteger generateX(final SecureRandom secureRandom, final BigInteger bigInteger) {
        final byte[] array = new byte[this.qlen];
        BigInteger mod;
        do {
            secureRandom.nextBytes(array);
            mod = new BigInteger(1, array).mod(bigInteger);
        } while (mod.signum() <= 0 || mod.compareTo(bigInteger) >= 0);
        return mod;
    }
    
    BigInteger generateY(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) {
        return bigInteger3.modPow(bigInteger, bigInteger2);
    }
    
    public static final class Current extends DSAKeyPairGenerator
    {
        public Current() {
            super(SecurityProviderConstants.DEF_DSA_KEY_SIZE);
        }
    }
    
    public static final class Legacy extends DSAKeyPairGenerator implements DSAKeyPairGenerator
    {
        public Legacy() {
            super(1024);
        }
        
        @Override
        public void initialize(final int n, final boolean b, final SecureRandom secureRandom) throws InvalidParameterException {
            if (b) {
                super.init(n, secureRandom, true);
            }
            else {
                final DSAParameterSpec cachedDSAParameterSpec = ParameterCache.getCachedDSAParameterSpec(n, SecurityProviderConstants.getDefDSASubprimeSize(n));
                if (cachedDSAParameterSpec == null) {
                    throw new InvalidParameterException("No precomputed parameters for requested modulus size available");
                }
                super.init(cachedDSAParameterSpec, secureRandom, false);
            }
        }
        
        @Override
        public void initialize(final DSAParams dsaParams, final SecureRandom secureRandom) throws InvalidParameterException {
            if (dsaParams == null) {
                throw new InvalidParameterException("Params must not be null");
            }
            super.init(new DSAParameterSpec(dsaParams.getP(), dsaParams.getQ(), dsaParams.getG()), secureRandom, false);
        }
    }
}
