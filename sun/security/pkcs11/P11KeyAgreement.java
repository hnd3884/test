package sun.security.pkcs11;

import java.security.AccessController;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import sun.security.util.KeyUtil;
import javax.crypto.interfaces.DHPublicKey;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Key;
import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import javax.crypto.KeyAgreementSpi;

final class P11KeyAgreement extends KeyAgreementSpi
{
    private final Token token;
    private final String algorithm;
    private final long mechanism;
    private P11Key privateKey;
    private BigInteger publicValue;
    private int secretLen;
    private KeyAgreement multiPartyAgreement;
    
    P11KeyAgreement(final Token token, final String algorithm, final long mechanism) {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
    }
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("Key must be instance of PrivateKey");
        }
        this.privateKey = P11KeyFactory.convertKey(this.token, key, this.algorithm);
        this.publicValue = null;
        this.multiPartyAgreement = null;
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (algorithmParameterSpec != null) {
            throw new InvalidAlgorithmParameterException("Parameters not supported");
        }
        this.engineInit(key, secureRandom);
    }
    
    @Override
    protected Key engineDoPhase(final Key key, final boolean b) throws InvalidKeyException, IllegalStateException {
        if (this.privateKey == null) {
            throw new IllegalStateException("Not initialized");
        }
        if (this.publicValue != null) {
            throw new IllegalStateException("Phase already executed");
        }
        if (this.multiPartyAgreement != null || !b) {
            if (this.multiPartyAgreement == null) {
                try {
                    (this.multiPartyAgreement = KeyAgreement.getInstance("DH", P11Util.getSunJceProvider())).init(this.privateKey);
                }
                catch (final NoSuchAlgorithmException ex) {
                    throw new InvalidKeyException("Could not initialize multi party agreement", ex);
                }
            }
            return this.multiPartyAgreement.doPhase(key, b);
        }
        if (!(key instanceof PublicKey) || !key.getAlgorithm().equals(this.algorithm)) {
            throw new InvalidKeyException("Key must be a PublicKey with algorithm DH");
        }
        BigInteger publicValue;
        BigInteger bigInteger;
        BigInteger bigInteger2;
        if (key instanceof DHPublicKey) {
            final DHPublicKey dhPublicKey = (DHPublicKey)key;
            KeyUtil.validate(dhPublicKey);
            publicValue = dhPublicKey.getY();
            final DHParameterSpec params = dhPublicKey.getParams();
            bigInteger = params.getP();
            bigInteger2 = params.getG();
        }
        else {
            final P11DHKeyFactory p11DHKeyFactory = new P11DHKeyFactory(this.token, "DH");
            try {
                final DHPublicKeySpec dhPublicKeySpec = p11DHKeyFactory.engineGetKeySpec(key, DHPublicKeySpec.class);
                KeyUtil.validate(dhPublicKeySpec);
                publicValue = dhPublicKeySpec.getY();
                bigInteger = dhPublicKeySpec.getP();
                bigInteger2 = dhPublicKeySpec.getG();
            }
            catch (final InvalidKeySpecException ex2) {
                throw new InvalidKeyException("Could not obtain key values", ex2);
            }
        }
        if (this.privateKey instanceof DHPrivateKey) {
            final DHParameterSpec params2 = ((DHPrivateKey)this.privateKey).getParams();
            if (!bigInteger.equals(params2.getP()) || !bigInteger2.equals(params2.getG())) {
                throw new InvalidKeyException("PublicKey DH parameters must match PrivateKey DH parameters");
            }
        }
        this.publicValue = publicValue;
        this.secretLen = bigInteger.bitLength() + 7 >> 3;
        return null;
    }
    
    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.multiPartyAgreement != null) {
            final byte[] generateSecret = this.multiPartyAgreement.generateSecret();
            this.multiPartyAgreement = null;
            return generateSecret;
        }
        if (this.privateKey == null || this.publicValue == null) {
            throw new IllegalStateException("Not initialized correctly");
        }
        Session opSession = null;
        final long keyID = this.privateKey.getKeyID();
        try {
            opSession = this.token.getOpSession();
            final long c_DeriveKey = this.token.p11.C_DeriveKey(opSession.id(), new CK_MECHANISM(this.mechanism, this.publicValue), keyID, this.token.getAttributes("generate", 4L, 16L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(256L, 16L) }));
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L) };
            this.token.p11.C_GetAttributeValue(opSession.id(), c_DeriveKey, array);
            final byte[] byteArray = array[0].getByteArray();
            this.token.p11.C_DestroyObject(opSession.id(), c_DeriveKey);
            if (byteArray.length == this.secretLen) {
                return byteArray;
            }
            if (byteArray.length > this.secretLen) {
                throw new ProviderException("generated secret is out-of-range");
            }
            final byte[] array2 = new byte[this.secretLen];
            System.arraycopy(byteArray, 0, array2, this.secretLen - byteArray.length, byteArray.length);
            return array2;
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("Could not derive key", ex);
        }
        finally {
            this.privateKey.releaseKeyID();
            this.publicValue = null;
            this.token.releaseSession(opSession);
        }
    }
    
    @Override
    protected int engineGenerateSecret(final byte[] array, final int n) throws IllegalStateException, ShortBufferException {
        if (this.multiPartyAgreement != null) {
            final int generateSecret = this.multiPartyAgreement.generateSecret(array, n);
            this.multiPartyAgreement = null;
            return generateSecret;
        }
        if (n + this.secretLen > array.length) {
            throw new ShortBufferException("Need " + this.secretLen + " bytes, only " + (array.length - n) + " available");
        }
        final byte[] engineGenerateSecret = this.engineGenerateSecret();
        System.arraycopy(engineGenerateSecret, 0, array, n, engineGenerateSecret.length);
        return engineGenerateSecret.length;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final String s) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        if (this.multiPartyAgreement != null) {
            final SecretKey generateSecret = this.multiPartyAgreement.generateSecret(s);
            this.multiPartyAgreement = null;
            return generateSecret;
        }
        if (s == null) {
            throw new NoSuchAlgorithmException("Algorithm must not be null");
        }
        if (s.equals("TlsPremasterSecret")) {
            return this.nativeGenerateSecret(s);
        }
        if (!s.equalsIgnoreCase("TlsPremasterSecret") && !AllowKDF.VALUE) {
            throw new NoSuchAlgorithmException("Unsupported secret key algorithm: " + s);
        }
        final byte[] engineGenerateSecret = this.engineGenerateSecret();
        int n;
        if (s.equalsIgnoreCase("DES")) {
            n = 8;
        }
        else if (s.equalsIgnoreCase("DESede")) {
            n = 24;
        }
        else if (s.equalsIgnoreCase("Blowfish")) {
            n = Math.min(56, engineGenerateSecret.length);
        }
        else {
            if (!s.equalsIgnoreCase("TlsPremasterSecret")) {
                throw new NoSuchAlgorithmException("Unknown algorithm " + s);
            }
            n = engineGenerateSecret.length;
        }
        if (engineGenerateSecret.length < n) {
            throw new InvalidKeyException("Secret too short");
        }
        if (s.equalsIgnoreCase("DES") || s.equalsIgnoreCase("DESede")) {
            for (int i = 0; i < n; i += 8) {
                P11SecretKeyFactory.fixDESParity(engineGenerateSecret, i);
            }
        }
        return new SecretKeySpec(engineGenerateSecret, 0, n, s);
    }
    
    private SecretKey nativeGenerateSecret(final String s) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        if (this.privateKey == null || this.publicValue == null) {
            throw new IllegalStateException("Not initialized correctly");
        }
        final long n = 16L;
        Session objSession = null;
        final long keyID = this.privateKey.getKeyID();
        try {
            objSession = this.token.getObjSession();
            final CK_ATTRIBUTE[] attributes = this.token.getAttributes("generate", 4L, n, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(256L, n) });
            final long c_DeriveKey = this.token.p11.C_DeriveKey(objSession.id(), new CK_MECHANISM(this.mechanism, this.publicValue), keyID, attributes);
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(353L) };
            this.token.p11.C_GetAttributeValue(objSession.id(), c_DeriveKey, array);
            SecretKey secretKey = P11Key.secretKey(objSession, c_DeriveKey, s, (int)array[0].getLong() << 3, attributes);
            if ("RAW".equals(secretKey.getFormat())) {
                final byte[] encoded = secretKey.getEncoded();
                final byte[] trimZeroes = KeyUtil.trimZeroes(encoded);
                if (encoded != trimZeroes) {
                    secretKey = new SecretKeySpec(trimZeroes, s);
                }
            }
            return secretKey;
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("Could not derive key", ex);
        }
        finally {
            this.privateKey.releaseKeyID();
            this.publicValue = null;
            this.token.releaseSession(objSession);
        }
    }
    
    private static class AllowKDF
    {
        private static final boolean VALUE;
        
        private static boolean getValue() {
            return AccessController.doPrivileged(() -> Boolean.getBoolean("jdk.crypto.KeyAgreement.legacyKDF"));
        }
        
        static {
            VALUE = getValue();
        }
    }
}
