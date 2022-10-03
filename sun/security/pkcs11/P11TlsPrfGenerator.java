package sun.security.pkcs11;

import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.pkcs11.wrapper.CK_TLS_PRF_PARAMS;
import java.security.ProviderException;
import javax.crypto.spec.SecretKeySpec;
import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_TLS_MAC_PARAMS;
import sun.security.pkcs11.wrapper.Functions;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import javax.crypto.SecretKey;
import sun.security.internal.spec.TlsPrfParameterSpec;
import javax.crypto.KeyGeneratorSpi;

final class P11TlsPrfGenerator extends KeyGeneratorSpi
{
    private static final String MSG = "TlsPrfGenerator must be initialized using a TlsPrfParameterSpec";
    private final Token token;
    private final String algorithm;
    private final long mechanism;
    private TlsPrfParameterSpec spec;
    private P11Key p11Key;
    private static final SecretKey NULL_KEY;
    
    P11TlsPrfGenerator(final Token token, final String algorithm, final long mechanism) throws PKCS11Exception {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
    }
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsPrfParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
        }
        this.spec = (TlsPrfParameterSpec)algorithmParameterSpec;
        SecretKey secretKey = this.spec.getSecret();
        if (secretKey == null) {
            secretKey = P11TlsPrfGenerator.NULL_KEY;
        }
        try {
            this.p11Key = P11SecretKeyFactory.convertKey(this.token, secretKey, null);
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidAlgorithmParameterException("init() failed", ex);
        }
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        if (this.spec == null) {
            throw new IllegalStateException("TlsPrfGenerator must be initialized");
        }
        final byte[] seed = this.spec.getSeed();
        if (this.mechanism == 996L) {
            int n = 0;
            if (this.spec.getLabel().equals("server finished")) {
                n = 1;
            }
            if (this.spec.getLabel().equals("client finished")) {
                n = 2;
            }
            if (n != 0) {
                final CK_TLS_MAC_PARAMS ck_TLS_MAC_PARAMS = new CK_TLS_MAC_PARAMS(Functions.getHashMechId(this.spec.getPRFHashAlg()), this.spec.getOutputLength(), n);
                Session opSession = null;
                final long keyID = this.p11Key.getKeyID();
                try {
                    opSession = this.token.getOpSession();
                    this.token.p11.C_SignInit(opSession.id(), new CK_MECHANISM(this.mechanism, ck_TLS_MAC_PARAMS), keyID);
                    this.token.p11.C_SignUpdate(opSession.id(), 0L, seed, 0, seed.length);
                    return new SecretKeySpec(this.token.p11.C_SignFinal(opSession.id(), this.spec.getOutputLength()), "TlsPrf");
                }
                catch (final PKCS11Exception ex) {
                    throw new ProviderException("Could not calculate PRF", ex);
                }
                finally {
                    this.p11Key.releaseKeyID();
                    this.token.releaseSession(opSession);
                }
            }
            throw new ProviderException("Only Finished message authentication code generation supported for TLS 1.2.");
        }
        final byte[] bytesUTF8 = P11Util.getBytesUTF8(this.spec.getLabel());
        if (this.mechanism == 2147484531L) {
            Session opSession2 = null;
            final long keyID2 = this.p11Key.getKeyID();
            try {
                opSession2 = this.token.getOpSession();
                this.token.p11.C_SignInit(opSession2.id(), new CK_MECHANISM(this.mechanism), keyID2);
                this.token.p11.C_SignUpdate(opSession2.id(), 0L, bytesUTF8, 0, bytesUTF8.length);
                this.token.p11.C_SignUpdate(opSession2.id(), 0L, seed, 0, seed.length);
                return new SecretKeySpec(this.token.p11.C_SignFinal(opSession2.id(), this.spec.getOutputLength()), "TlsPrf");
            }
            catch (final PKCS11Exception ex2) {
                throw new ProviderException("Could not calculate PRF", ex2);
            }
            finally {
                this.p11Key.releaseKeyID();
                this.token.releaseSession(opSession2);
            }
        }
        final byte[] array = new byte[this.spec.getOutputLength()];
        final CK_TLS_PRF_PARAMS ck_TLS_PRF_PARAMS = new CK_TLS_PRF_PARAMS(seed, bytesUTF8, array);
        Session opSession3 = null;
        final long keyID3 = this.p11Key.getKeyID();
        try {
            opSession3 = this.token.getOpSession();
            this.token.p11.C_DeriveKey(opSession3.id(), new CK_MECHANISM(this.mechanism, ck_TLS_PRF_PARAMS), keyID3, null);
            return new SecretKeySpec(array, "TlsPrf");
        }
        catch (final PKCS11Exception ex3) {
            throw new ProviderException("Could not calculate PRF", ex3);
        }
        finally {
            this.p11Key.releaseKeyID();
            this.token.releaseSession(opSession3);
        }
    }
    
    static {
        NULL_KEY = new SecretKey() {
            @Override
            public byte[] getEncoded() {
                return new byte[0];
            }
            
            @Override
            public String getFormat() {
                return "RAW";
            }
            
            @Override
            public String getAlgorithm() {
                return "Generic";
            }
        };
    }
}
