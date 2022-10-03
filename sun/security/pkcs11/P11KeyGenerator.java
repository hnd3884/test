package sun.security.pkcs11;

import sun.security.pkcs11.wrapper.CK_MECHANISM;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import javax.crypto.SecretKey;
import java.security.InvalidParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.SecureRandom;
import sun.security.pkcs11.wrapper.CK_MECHANISM_INFO;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.ProviderException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.KeyGeneratorSpi;

final class P11KeyGenerator extends KeyGeneratorSpi
{
    private final Token token;
    private final String algorithm;
    private long mechanism;
    private int keySize;
    private int significantKeySize;
    private long keyType;
    private boolean supportBothKeySizes;
    
    static int checkKeySize(final long n, final int n2, final Token token) throws InvalidAlgorithmParameterException, ProviderException {
        int n3 = 0;
        switch ((int)n) {
            case 288: {
                if (n2 != 64 && n2 != 56) {
                    throw new InvalidAlgorithmParameterException("DES key length must be 56 bits");
                }
                n3 = 56;
                break;
            }
            case 304:
            case 305: {
                if (n2 == 112 || n2 == 128) {
                    n3 = 112;
                    break;
                }
                if (n2 == 168 || n2 == 192) {
                    n3 = 168;
                    break;
                }
                throw new InvalidAlgorithmParameterException("DESede key length must be 112, or 168 bits");
            }
            default: {
                CK_MECHANISM_INFO mechanismInfo;
                try {
                    mechanismInfo = token.getMechanismInfo(n);
                }
                catch (final PKCS11Exception ex) {
                    throw new ProviderException("Cannot retrieve mechanism info", ex);
                }
                if (mechanismInfo == null) {
                    return n2;
                }
                int n4 = mechanismInfo.iMinKeySize;
                int n5 = mechanismInfo.iMaxKeySize;
                if (n != 272L || n4 < 8) {
                    n4 = Math.multiplyExact(n4, 8);
                    if (n5 != Integer.MAX_VALUE) {
                        n5 = Math.multiplyExact(n5, 8);
                    }
                }
                if (n4 < 40) {
                    n4 = 40;
                }
                if (n2 < n4 || n2 > n5) {
                    throw new InvalidAlgorithmParameterException("Key length must be between " + n4 + " and " + n5 + " bits");
                }
                if (n == 4224L && n2 != 128 && n2 != 192 && n2 != 256) {
                    throw new InvalidAlgorithmParameterException("AES key length must be " + n4 + ((n5 >= 192) ? ", 192" : "") + ((n5 >= 256) ? ", or 256" : "") + " bits");
                }
                n3 = n2;
                break;
            }
        }
        return n3;
    }
    
    P11KeyGenerator(final Token token, final String algorithm, final long mechanism) throws PKCS11Exception {
        this.token = token;
        this.algorithm = algorithm;
        this.mechanism = mechanism;
        if (this.mechanism == 305L) {
            this.supportBothKeySizes = (token.provider.config.isEnabled(304L) && token.getMechanismInfo(304L) != null);
        }
        this.setDefaultKeySize();
    }
    
    private void setDefaultKeySize() {
        switch ((int)this.mechanism) {
            case 288: {
                this.keySize = 64;
                this.keyType = 19L;
                break;
            }
            case 304: {
                this.keySize = 128;
                this.keyType = 20L;
                break;
            }
            case 305: {
                this.keySize = 192;
                this.keyType = 21L;
                break;
            }
            case 4224: {
                this.keySize = 128;
                this.keyType = 31L;
                break;
            }
            case 272: {
                this.keySize = 128;
                this.keyType = 18L;
                break;
            }
            case 4240: {
                this.keySize = 128;
                this.keyType = 32L;
                break;
            }
            default: {
                throw new ProviderException("Unknown mechanism " + this.mechanism);
            }
        }
        try {
            this.significantKeySize = checkKeySize(this.mechanism, this.keySize, this.token);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new ProviderException("Unsupported default key size", ex);
        }
    }
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        this.token.ensureValid();
        this.setDefaultKeySize();
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        throw new InvalidAlgorithmParameterException("AlgorithmParameterSpec not supported");
    }
    
    @Override
    protected void engineInit(final int keySize, final SecureRandom secureRandom) {
        this.token.ensureValid();
        int checkKeySize;
        try {
            checkKeySize = checkKeySize(this.mechanism, keySize, this.token);
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw (InvalidParameterException)new InvalidParameterException().initCause(ex);
        }
        if (this.mechanism == 304L || this.mechanism == 305L) {
            final long mechanism = (checkKeySize == 112) ? 304L : 305L;
            if (this.mechanism != mechanism) {
                if (!this.supportBothKeySizes) {
                    throw new InvalidParameterException("Only " + this.significantKeySize + "-bit DESede is supported");
                }
                this.mechanism = mechanism;
                this.keyType = ((this.mechanism == 304L) ? 20L : 21L);
            }
        }
        this.keySize = keySize;
        this.significantKeySize = checkKeySize;
    }
    
    @Override
    protected SecretKey engineGenerateKey() {
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            CK_ATTRIBUTE[] array = null;
            switch ((int)this.keyType) {
                case 19:
                case 20:
                case 21: {
                    array = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L) };
                    break;
                }
                default: {
                    array = new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 4L), new CK_ATTRIBUTE(353L, this.keySize >> 3) };
                    break;
                }
            }
            final CK_ATTRIBUTE[] attributes = this.token.getAttributes("generate", 4L, this.keyType, array);
            return P11Key.secretKey(objSession, this.token.p11.C_GenerateKey(objSession.id(), new CK_MECHANISM(this.mechanism), attributes), this.algorithm, this.significantKeySize, attributes);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("Could not generate key", ex);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
}
