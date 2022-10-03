package sun.security.pkcs11;

import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_ECDH1_DERIVE_PARAMS;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Key;
import javax.crypto.KeyAgreementSpi;

final class P11ECDHKeyAgreement extends KeyAgreementSpi
{
    private final Token token;
    private final String algorithm;
    private final long mechanism;
    private P11Key privateKey;
    private byte[] publicValue;
    private int secretLen;
    
    P11ECDHKeyAgreement(final Token token, final String algorithm, final long mechanism) {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
    }
    
    @Override
    protected void engineInit(final Key key, final SecureRandom secureRandom) throws InvalidKeyException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException("Key must be instance of PrivateKey");
        }
        this.privateKey = P11KeyFactory.convertKey(this.token, key, "EC");
        this.publicValue = null;
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
        if (!b) {
            throw new IllegalStateException("Only two party agreement supported, lastPhase must be true");
        }
        if (!(key instanceof ECPublicKey)) {
            throw new InvalidKeyException("Key must be a PublicKey with algorithm EC");
        }
        final ECPublicKey ecPublicKey = (ECPublicKey)key;
        this.secretLen = ecPublicKey.getParams().getCurve().getField().getFieldSize() + 7 >> 3;
        this.publicValue = P11ECKeyFactory.getEncodedPublicValue(ecPublicKey);
        return null;
    }
    
    @Override
    protected byte[] engineGenerateSecret() throws IllegalStateException {
        if (this.privateKey == null || this.publicValue == null) {
            throw new IllegalStateException("Not initialized correctly");
        }
        Session opSession = null;
        final long keyID = this.privateKey.getKeyID();
        try {
            opSession = this.token.getOpSession();
            final long c_DeriveKey = this.token.p11.C_DeriveKey(opSession.id(), new CK_MECHANISM(this.mechanism, new CK_ECDH1_DERIVE_PARAMS(1L, null, this.publicValue)), keyID, this.token.getAttributes("generate", 4L, 16L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(256L, 16L) }));
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(17L) };
            this.token.p11.C_GetAttributeValue(opSession.id(), c_DeriveKey, array);
            final byte[] byteArray = array[0].getByteArray();
            this.token.p11.C_DestroyObject(opSession.id(), c_DeriveKey);
            return byteArray;
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
        if (n + this.secretLen > array.length) {
            throw new ShortBufferException("Need " + this.secretLen + " bytes, only " + (array.length - n) + " available");
        }
        final byte[] engineGenerateSecret = this.engineGenerateSecret();
        System.arraycopy(engineGenerateSecret, 0, array, n, engineGenerateSecret.length);
        return engineGenerateSecret.length;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final String s) throws IllegalStateException, NoSuchAlgorithmException, InvalidKeyException {
        if (s == null) {
            throw new NoSuchAlgorithmException("Algorithm must not be null");
        }
        if (!s.equals("TlsPremasterSecret")) {
            throw new NoSuchAlgorithmException("Only supported for algorithm TlsPremasterSecret");
        }
        return this.nativeGenerateSecret(s);
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
            final CK_ATTRIBUTE[] array = { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(256L, n) };
            final CK_ECDH1_DERIVE_PARAMS ck_ECDH1_DERIVE_PARAMS = new CK_ECDH1_DERIVE_PARAMS(1L, null, this.publicValue);
            final CK_ATTRIBUTE[] attributes = this.token.getAttributes("generate", 4L, n, array);
            final long c_DeriveKey = this.token.p11.C_DeriveKey(objSession.id(), new CK_MECHANISM(this.mechanism, ck_ECDH1_DERIVE_PARAMS), keyID, attributes);
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(353L) };
            this.token.p11.C_GetAttributeValue(objSession.id(), c_DeriveKey, array2);
            return P11Key.secretKey(objSession, c_DeriveKey, s, (int)array2[0].getLong() << 3, attributes);
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
}
