package sun.security.pkcs11;

import sun.security.pkcs11.wrapper.CK_MECHANISM;
import java.security.GeneralSecurityException;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.security.KeyPair;
import sun.security.provider.ParameterCache;
import java.security.InvalidKeyException;
import sun.security.rsa.RSAKeyFactory;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.DSAParameterSpec;
import javax.crypto.spec.DHParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidParameterException;
import sun.security.pkcs11.wrapper.CK_MECHANISM_INFO;
import sun.security.util.SecurityProviderConstants;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.KeyPairGeneratorSpi;

final class P11KeyPairGenerator extends KeyPairGeneratorSpi
{
    private final Token token;
    private final String algorithm;
    private final long mechanism;
    private int keySize;
    private AlgorithmParameterSpec params;
    private BigInteger rsaPublicExponent;
    private final int minKeySize;
    private final int maxKeySize;
    private SecureRandom random;
    
    P11KeyPairGenerator(final Token token, final String algorithm, final long mechanism) throws PKCS11Exception {
        this.rsaPublicExponent = RSAKeyGenParameterSpec.F4;
        int iMinKeySize = 0;
        int iMaxKeySize = Integer.MAX_VALUE;
        try {
            final CK_MECHANISM_INFO mechanismInfo = token.getMechanismInfo(mechanism);
            if (mechanismInfo != null) {
                iMinKeySize = mechanismInfo.iMinKeySize;
                iMaxKeySize = mechanismInfo.iMaxKeySize;
            }
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("Unexpected error while getting mechanism info", ex);
        }
        if (algorithm.equals("EC")) {
            this.keySize = SecurityProviderConstants.DEF_EC_KEY_SIZE;
            if (iMinKeySize < 112) {
                iMinKeySize = 112;
            }
            if (iMaxKeySize > 2048) {
                iMaxKeySize = 2048;
            }
        }
        else {
            if (algorithm.equals("DSA")) {
                this.keySize = SecurityProviderConstants.DEF_DSA_KEY_SIZE;
            }
            else if (algorithm.equals("RSA")) {
                this.keySize = SecurityProviderConstants.DEF_RSA_KEY_SIZE;
                if (iMaxKeySize > 65536) {
                    iMaxKeySize = 65536;
                }
            }
            else {
                this.keySize = SecurityProviderConstants.DEF_DH_KEY_SIZE;
            }
            if (iMinKeySize < 512) {
                iMinKeySize = 512;
            }
        }
        if (this.keySize < iMinKeySize) {
            this.keySize = iMinKeySize;
        }
        if (this.keySize > iMaxKeySize) {
            this.keySize = iMaxKeySize;
        }
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
        this.minKeySize = iMinKeySize;
        this.maxKeySize = iMaxKeySize;
        this.initialize(this.keySize, null);
    }
    
    @Override
    public void initialize(final int keySize, final SecureRandom random) {
        this.token.ensureValid();
        try {
            this.checkKeySize(keySize, null);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidParameterException(ex.getMessage());
        }
        this.params = null;
        if (this.algorithm.equals("EC")) {
            this.params = P11ECKeyFactory.getECParameterSpec(keySize);
            if (this.params == null) {
                throw new InvalidParameterException("No EC parameters available for key size " + keySize + " bits");
            }
        }
        this.keySize = keySize;
        this.random = random;
    }
    
    @Override
    public void initialize(AlgorithmParameterSpec params, final SecureRandom random) throws InvalidAlgorithmParameterException {
        this.token.ensureValid();
        int keySize;
        if (this.algorithm.equals("DH")) {
            if (!(params instanceof DHParameterSpec)) {
                throw new InvalidAlgorithmParameterException("DHParameterSpec required for Diffie-Hellman");
            }
            final DHParameterSpec dhParameterSpec = (DHParameterSpec)params;
            keySize = dhParameterSpec.getP().bitLength();
            this.checkKeySize(keySize, dhParameterSpec);
        }
        else if (this.algorithm.equals("RSA")) {
            if (!(params instanceof RSAKeyGenParameterSpec)) {
                throw new InvalidAlgorithmParameterException("RSAKeyGenParameterSpec required for RSA");
            }
            final RSAKeyGenParameterSpec rsaKeyGenParameterSpec = (RSAKeyGenParameterSpec)params;
            keySize = rsaKeyGenParameterSpec.getKeysize();
            this.checkKeySize(keySize, rsaKeyGenParameterSpec);
            params = null;
            this.rsaPublicExponent = rsaKeyGenParameterSpec.getPublicExponent();
        }
        else if (this.algorithm.equals("DSA")) {
            if (!(params instanceof DSAParameterSpec)) {
                throw new InvalidAlgorithmParameterException("DSAParameterSpec required for DSA");
            }
            final DSAParameterSpec dsaParameterSpec = (DSAParameterSpec)params;
            keySize = dsaParameterSpec.getP().bitLength();
            this.checkKeySize(keySize, dsaParameterSpec);
        }
        else {
            if (!this.algorithm.equals("EC")) {
                throw new ProviderException("Unknown algorithm: " + this.algorithm);
            }
            ECParameterSpec ecParameterSpec;
            if (params instanceof ECParameterSpec) {
                ecParameterSpec = P11ECKeyFactory.getECParameterSpec((ECParameterSpec)params);
                if (ecParameterSpec == null) {
                    throw new InvalidAlgorithmParameterException("Unsupported curve: " + params);
                }
            }
            else {
                if (!(params instanceof ECGenParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("ECParameterSpec or ECGenParameterSpec required for EC");
                }
                final String name = ((ECGenParameterSpec)params).getName();
                ecParameterSpec = P11ECKeyFactory.getECParameterSpec(name);
                if (ecParameterSpec == null) {
                    throw new InvalidAlgorithmParameterException("Unknown curve name: " + name);
                }
                params = ecParameterSpec;
            }
            keySize = ecParameterSpec.getCurve().getField().getFieldSize();
            this.checkKeySize(keySize, ecParameterSpec);
        }
        this.keySize = keySize;
        this.params = params;
        this.random = random;
    }
    
    private void checkKeySize(final int n, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidAlgorithmParameterException {
        if (n <= 0) {
            throw new InvalidAlgorithmParameterException("key size must be positive, got " + n);
        }
        if (n < this.minKeySize) {
            throw new InvalidAlgorithmParameterException(this.algorithm + " key must be at least " + this.minKeySize + " bits. The specific key size " + n + " is not supported");
        }
        if (n > this.maxKeySize) {
            throw new InvalidAlgorithmParameterException(this.algorithm + " key must be at most " + this.maxKeySize + " bits. The specific key size " + n + " is not supported");
        }
        if (this.algorithm.equals("EC")) {
            if (n < 112) {
                throw new InvalidAlgorithmParameterException("EC key size must be at least 112 bit. The specific key size " + n + " is not supported");
            }
            if (n > 2048) {
                throw new InvalidAlgorithmParameterException("EC key size must be at most 2048 bit. The specific key size " + n + " is not supported");
            }
        }
        else {
            if (n < 512) {
                throw new InvalidAlgorithmParameterException(this.algorithm + " key size must be at least 512 bit. The specific key size " + n + " is not supported");
            }
            if (this.algorithm.equals("RSA")) {
                BigInteger bigInteger = this.rsaPublicExponent;
                if (algorithmParameterSpec != null) {
                    bigInteger = ((RSAKeyGenParameterSpec)algorithmParameterSpec).getPublicExponent();
                }
                try {
                    RSAKeyFactory.checkKeyLengths(n, bigInteger, this.minKeySize, this.maxKeySize);
                }
                catch (final InvalidKeyException ex) {
                    throw new InvalidAlgorithmParameterException(ex);
                }
            }
            else if (this.algorithm.equals("DH")) {
                if (algorithmParameterSpec != null) {
                    if (n > 65536) {
                        throw new InvalidAlgorithmParameterException("DH key size must be at most 65536 bit. The specific key size " + n + " is not supported");
                    }
                }
                else {
                    if (n > 8192 || n < 512 || (n & 0x3F) != 0x0) {
                        throw new InvalidAlgorithmParameterException("DH key size must be multiple of 64, and can only range from 512 to 8192 (inclusive). The specific key size " + n + " is not supported");
                    }
                    if (ParameterCache.getCachedDHParameterSpec(n) == null && n > 1024) {
                        throw new InvalidAlgorithmParameterException("Unsupported " + n + "-bit DH parameter generation");
                    }
                }
            }
            else if (n != 3072 && n != 2048 && (n > 1024 || (n & 0x3F) != 0x0)) {
                throw new InvalidAlgorithmParameterException("DSA key must be multiples of 64 if less than 1024 bits, or 2048, 3072 bits. The specific key size " + n + " is not supported");
            }
        }
    }
    
    @Override
    public KeyPair generateKeyPair() {
        this.token.ensureValid();
        long n;
        CK_ATTRIBUTE[] array;
        CK_ATTRIBUTE[] array2;
        if (this.algorithm.equals("RSA")) {
            n = 0L;
            array = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(289L, this.keySize), new CK_ATTRIBUTE(290L, this.rsaPublicExponent) };
            array2 = new CK_ATTRIBUTE[0];
        }
        else if (this.algorithm.equals("DSA")) {
            n = 1L;
            DSAParameterSpec dsaParameterSpec = null;
            Label_0129: {
                if (this.params == null) {
                    try {
                        dsaParameterSpec = ParameterCache.getDSAParameterSpec(this.keySize, this.random);
                        break Label_0129;
                    }
                    catch (final GeneralSecurityException ex) {
                        throw new ProviderException("Could not generate DSA parameters", ex);
                    }
                }
                dsaParameterSpec = (DSAParameterSpec)this.params;
            }
            array = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(304L, dsaParameterSpec.getP()), new CK_ATTRIBUTE(305L, dsaParameterSpec.getQ()), new CK_ATTRIBUTE(306L, dsaParameterSpec.getG()) };
            array2 = new CK_ATTRIBUTE[0];
        }
        else if (this.algorithm.equals("DH")) {
            n = 2L;
            DHParameterSpec dhParameterSpec;
            int l;
            if (this.params == null) {
                try {
                    dhParameterSpec = ParameterCache.getDHParameterSpec(this.keySize, this.random);
                }
                catch (final GeneralSecurityException ex2) {
                    throw new ProviderException("Could not generate DH parameters", ex2);
                }
                l = 0;
            }
            else {
                dhParameterSpec = (DHParameterSpec)this.params;
                l = dhParameterSpec.getL();
            }
            if (l <= 0) {
                l = ((this.keySize >= 1024) ? 768 : 512);
            }
            array = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(304L, dhParameterSpec.getP()), new CK_ATTRIBUTE(306L, dhParameterSpec.getG()) };
            array2 = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(352L, l) };
        }
        else {
            if (!this.algorithm.equals("EC")) {
                throw new ProviderException("Unknown algorithm: " + this.algorithm);
            }
            n = 3L;
            array = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(384L, P11ECKeyFactory.encodeParameters((ECParameterSpec)this.params)) };
            array2 = new CK_ATTRIBUTE[0];
        }
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            final CK_ATTRIBUTE[] attributes = this.token.getAttributes("generate", 2L, n, array);
            final CK_ATTRIBUTE[] attributes2 = this.token.getAttributes("generate", 3L, n, array2);
            final long[] c_GenerateKeyPair = this.token.p11.C_GenerateKeyPair(objSession.id(), new CK_MECHANISM(this.mechanism), attributes, attributes2);
            return new KeyPair(P11Key.publicKey(objSession, c_GenerateKeyPair[0], this.algorithm, this.keySize, attributes), P11Key.privateKey(objSession, c_GenerateKeyPair[1], this.algorithm, this.keySize, attributes2));
        }
        catch (final PKCS11Exception ex3) {
            throw new ProviderException(ex3);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
}
