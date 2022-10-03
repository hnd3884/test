package sun.security.pkcs11;

import java.security.ProviderException;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_TLS12_MASTER_KEY_DERIVE_PARAMS;
import sun.security.pkcs11.wrapper.Functions;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_SSL3_MASTER_KEY_DERIVE_PARAMS;
import sun.security.pkcs11.wrapper.CK_SSL3_RANDOM_DATA;
import sun.security.pkcs11.wrapper.CK_VERSION;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.internal.spec.TlsMasterSecretParameterSpec;
import javax.crypto.KeyGeneratorSpi;

public final class P11TlsMasterSecretGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec";
    private final Token token;
    private final String algorithm;
    private long mechanism;
    private TlsMasterSecretParameterSpec spec;
    private P11Key p11Key;
    int version;
    
    P11TlsMasterSecretGenerator(final Token token, final String algorithm, final long mechanism) throws PKCS11Exception {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
    }
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsMasterSecretParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
        }
        this.spec = (TlsMasterSecretParameterSpec)algorithmParameterSpec;
        final SecretKey premasterSecret = this.spec.getPremasterSecret();
        try {
            this.p11Key = P11SecretKeyFactory.convertKey(this.token, premasterSecret, null);
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
        throw new InvalidParameterException("TlsMasterSecretGenerator must be initialized using a TlsMasterSecretParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsMasterSecretGenerator must be initialized");
        }
        final boolean equals = this.p11Key.getAlgorithm().equals("TlsRsaPremasterSecret");
        if (this.version == 768) {
            this.mechanism = (equals ? 881L : 883L);
        }
        else if (this.version == 769 || this.version == 770) {
            this.mechanism = (equals ? 885L : 887L);
        }
        else if (this.version == 771) {
            this.mechanism = (equals ? 992L : 994L);
        }
        CK_VERSION ck_VERSION;
        if (equals) {
            ck_VERSION = new CK_VERSION(0, 0);
        }
        else {
            ck_VERSION = null;
        }
        final CK_SSL3_RANDOM_DATA ck_SSL3_RANDOM_DATA = new CK_SSL3_RANDOM_DATA(this.spec.getClientRandom(), this.spec.getServerRandom());
        CK_MECHANISM ck_MECHANISM = null;
        if (this.version < 771) {
            ck_MECHANISM = new CK_MECHANISM(this.mechanism, new CK_SSL3_MASTER_KEY_DERIVE_PARAMS(ck_SSL3_RANDOM_DATA, ck_VERSION));
        }
        else if (this.version == 771) {
            ck_MECHANISM = new CK_MECHANISM(this.mechanism, new CK_TLS12_MASTER_KEY_DERIVE_PARAMS(ck_SSL3_RANDOM_DATA, ck_VERSION, Functions.getHashMechId(this.spec.getPRFHashAlg())));
        }
        Session objSession = null;
        final long keyID = this.p11Key.getKeyID();
        try {
            objSession = this.token.getObjSession();
            final CK_ATTRIBUTE[] attributes = this.token.getAttributes("generate", 4L, 16L, new CK_ATTRIBUTE[0]);
            final long c_DeriveKey = this.token.p11.C_DeriveKey(objSession.id(), ck_MECHANISM, keyID, attributes);
            int major;
            int minor;
            if (ck_VERSION == null) {
                major = -1;
                minor = -1;
            }
            else {
                major = ck_VERSION.major;
                minor = ck_VERSION.minor;
            }
            return P11Key.masterSecretKey(objSession, c_DeriveKey, "TlsMasterSecret", 384, attributes, major, minor);
        }
        catch (final Exception ex) {
            throw new ProviderException("Could not generate key", ex);
        }
        finally {
            this.p11Key.releaseKeyID();
            this.token.releaseSession(objSession);
        }
    }
}
