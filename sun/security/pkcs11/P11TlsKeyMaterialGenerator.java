package sun.security.pkcs11;

import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_OUT;
import sun.security.internal.spec.TlsKeyMaterialSpec;
import javax.crypto.spec.IvParameterSpec;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.security.ProviderException;
import sun.security.pkcs11.wrapper.CK_TLS12_KEY_MAT_PARAMS;
import sun.security.pkcs11.wrapper.Functions;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_SSL3_KEY_MAT_PARAMS;
import sun.security.pkcs11.wrapper.CK_SSL3_RANDOM_DATA;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.internal.spec.TlsKeyMaterialParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class P11TlsKeyMaterialGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec";
    private final Token token;
    private final String algorithm;
    private long mechanism;
    private TlsKeyMaterialParameterSpec spec;
    private P11Key p11Key;
    private int version;
    
    P11TlsKeyMaterialGenerator(final Token token, final String algorithm, final long mechanism) throws PKCS11Exception {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
    }
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsKeyMaterialParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
        }
        this.spec = (TlsKeyMaterialParameterSpec)algorithmParameterSpec;
        try {
            this.p11Key = P11SecretKeyFactory.convertKey(this.token, this.spec.getMasterSecret(), "TlsMasterSecret");
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidAlgorithmParameterException("init() failed", ex);
        }
        this.version = (this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion());
        if (this.version < 768 && this.version > 771) {
            throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0, TLS 1.1, and TLS 1.2 are supported");
        }
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsKeyMaterialGenerator must be initialized using a TlsKeyMaterialParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsKeyMaterialGenerator must be initialized");
        }
        if (this.version == 768) {
            this.mechanism = 882L;
        }
        else if (this.version == 769 || this.version == 770) {
            this.mechanism = 886L;
        }
        final int n = this.spec.getMacKeyLength() << 3;
        final int n2 = this.spec.getIvLength() << 3;
        int n3 = this.spec.getExpandedCipherKeyLength() << 3;
        final int n4 = this.spec.getCipherKeyLength() << 3;
        boolean b;
        if (n3 != 0) {
            b = true;
        }
        else {
            b = false;
            n3 = n4;
        }
        final CK_SSL3_RANDOM_DATA ck_SSL3_RANDOM_DATA = new CK_SSL3_RANDOM_DATA(this.spec.getClientRandom(), this.spec.getServerRandom());
        Object o = null;
        CK_MECHANISM ck_MECHANISM = null;
        if (this.version < 771) {
            o = new CK_SSL3_KEY_MAT_PARAMS(n, n4, n2, b, ck_SSL3_RANDOM_DATA);
            ck_MECHANISM = new CK_MECHANISM(this.mechanism, (CK_SSL3_KEY_MAT_PARAMS)o);
        }
        else if (this.version == 771) {
            o = new CK_TLS12_KEY_MAT_PARAMS(n, n4, n2, b, ck_SSL3_RANDOM_DATA, Functions.getHashMechId(this.spec.getPRFHashAlg()));
            ck_MECHANISM = new CK_MECHANISM(this.mechanism, (CK_TLS12_KEY_MAT_PARAMS)o);
        }
        final String cipherAlgorithm = this.spec.getCipherAlgorithm();
        long keyType = P11SecretKeyFactory.getKeyType(cipherAlgorithm);
        if (keyType < 0L) {
            if (n4 != 0) {
                throw new ProviderException("Unknown algorithm: " + this.spec.getCipherAlgorithm());
            }
            keyType = 16L;
        }
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            CK_ATTRIBUTE[] array;
            if (n4 != 0) {
                array = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(256L, keyType), new CK_ATTRIBUTE(353L, n3 >> 3) };
            }
            else {
                array = new CK_ATTRIBUTE[0];
            }
            final CK_ATTRIBUTE[] attributes = this.token.getAttributes("generate", 4L, keyType, array);
            final long keyID = this.p11Key.getKeyID();
            try {
                this.token.p11.C_DeriveKey(objSession.id(), ck_MECHANISM, keyID, attributes);
            }
            finally {
                this.p11Key.releaseKeyID();
            }
            CK_SSL3_KEY_MAT_OUT ck_SSL3_KEY_MAT_OUT = null;
            if (o instanceof CK_SSL3_KEY_MAT_PARAMS) {
                ck_SSL3_KEY_MAT_OUT = ((CK_SSL3_KEY_MAT_PARAMS)o).pReturnedKeyMaterial;
            }
            else if (o instanceof CK_TLS12_KEY_MAT_PARAMS) {
                ck_SSL3_KEY_MAT_OUT = ((CK_TLS12_KEY_MAT_PARAMS)o).pReturnedKeyMaterial;
            }
            SecretKey secretKey;
            SecretKey secretKey2;
            if (n != 0) {
                secretKey = P11Key.secretKey(objSession, ck_SSL3_KEY_MAT_OUT.hClientMacSecret, "MAC", n, attributes);
                secretKey2 = P11Key.secretKey(objSession, ck_SSL3_KEY_MAT_OUT.hServerMacSecret, "MAC", n, attributes);
            }
            else {
                secretKey = null;
                secretKey2 = null;
            }
            SecretKey secretKey3;
            SecretKey secretKey4;
            if (n4 != 0) {
                secretKey3 = P11Key.secretKey(objSession, ck_SSL3_KEY_MAT_OUT.hClientKey, cipherAlgorithm, n3, attributes);
                secretKey4 = P11Key.secretKey(objSession, ck_SSL3_KEY_MAT_OUT.hServerKey, cipherAlgorithm, n3, attributes);
            }
            else {
                secretKey3 = null;
                secretKey4 = null;
            }
            return new TlsKeyMaterialSpec(secretKey, secretKey2, secretKey3, (ck_SSL3_KEY_MAT_OUT.pIVClient == null) ? null : new IvParameterSpec(ck_SSL3_KEY_MAT_OUT.pIVClient), secretKey4, (ck_SSL3_KEY_MAT_OUT.pIVServer == null) ? null : new IvParameterSpec(ck_SSL3_KEY_MAT_OUT.pIVServer));
        }
        catch (final Exception ex) {
            throw new ProviderException("Could not generate key", ex);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
}
