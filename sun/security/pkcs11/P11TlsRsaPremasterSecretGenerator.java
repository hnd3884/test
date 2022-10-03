package sun.security.pkcs11;

import java.security.ProviderException;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_VERSION;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import javax.crypto.KeyGeneratorSpi;

final class P11TlsRsaPremasterSecretGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec";
    private final Token token;
    private final String algorithm;
    private long mechanism;
    private int version;
    private TlsRsaPremasterSecretParameterSpec spec;
    
    P11TlsRsaPremasterSecretGenerator(final Token token, final String algorithm, final long mechanism) throws PKCS11Exception {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
    }
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsRsaPremasterSecretParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
        }
        this.spec = (TlsRsaPremasterSecretParameterSpec)algorithmParameterSpec;
        this.version = (this.spec.getMajorVersion() << 8 | this.spec.getMinorVersion());
        if (this.version < 768 && this.version > 771) {
            throw new InvalidAlgorithmParameterException("Only SSL 3.0, TLS 1.0, TLS 1.1, and TLS 1.2 are supported");
        }
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsRsaPremasterSecretGenerator must be initialized using a TlsRsaPremasterSecretParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsRsaPremasterSecretGenerator must be initialized");
        }
        final CK_VERSION ck_VERSION = new CK_VERSION(this.spec.getMajorVersion(), this.spec.getMinorVersion());
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            final CK_ATTRIBUTE[] attributes = this.token.getAttributes("generate", 4L, 16L, new CK_ATTRIBUTE[0]);
            return P11Key.secretKey(objSession, this.token.p11.C_GenerateKey(objSession.id(), new CK_MECHANISM(this.mechanism, ck_VERSION), attributes), "TlsRsaPremasterSecret", 384, attributes);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("Could not generate premaster secret", ex);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
}
