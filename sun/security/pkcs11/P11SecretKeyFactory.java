package sun.security.pkcs11;

import java.util.HashMap;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.ProviderException;
import java.security.InvalidAlgorithmParameterException;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.security.Key;
import java.util.Locale;
import java.util.Map;
import javax.crypto.SecretKeyFactorySpi;

final class P11SecretKeyFactory extends SecretKeyFactorySpi
{
    private final Token token;
    private final String algorithm;
    private static final Map<String, Long> keyTypes;
    
    P11SecretKeyFactory(final Token token, final String algorithm) {
        this.token = token;
        this.algorithm = algorithm;
    }
    
    private static void addKeyType(final String s, final long n) {
        final Long value = n;
        P11SecretKeyFactory.keyTypes.put(s, value);
        P11SecretKeyFactory.keyTypes.put(s.toUpperCase(Locale.ENGLISH), value);
    }
    
    static long getKeyType(String upperCase) {
        Long n = P11SecretKeyFactory.keyTypes.get(upperCase);
        if (n == null) {
            upperCase = upperCase.toUpperCase(Locale.ENGLISH);
            n = P11SecretKeyFactory.keyTypes.get(upperCase);
            if (n == null) {
                if (upperCase.startsWith("HMAC")) {
                    return 2147483427L;
                }
                if (upperCase.startsWith("SSLMAC")) {
                    return 2147483428L;
                }
            }
        }
        return (n != null) ? n : -1L;
    }
    
    static P11Key convertKey(final Token token, final Key key, final String s) throws InvalidKeyException {
        return convertKey(token, key, s, null);
    }
    
    static P11Key convertKey(final Token token, final Key key, String algorithm, final CK_ATTRIBUTE[] array) throws InvalidKeyException {
        token.ensureValid();
        if (key == null) {
            throw new InvalidKeyException("Key must not be null");
        }
        if (!(key instanceof SecretKey)) {
            throw new InvalidKeyException("Key must be a SecretKey");
        }
        long n;
        if (algorithm == null) {
            algorithm = key.getAlgorithm();
            n = getKeyType(algorithm);
        }
        else {
            n = getKeyType(algorithm);
            if (n != getKeyType(key.getAlgorithm()) && n != 2147483427L) {
                if (n != 2147483428L) {
                    throw new InvalidKeyException("Key algorithm must be " + algorithm);
                }
            }
        }
        if (key instanceof P11Key) {
            P11Key p11Key = (P11Key)key;
            if (p11Key.token == token) {
                if (array != null) {
                    P11Key p11Key2 = null;
                    Session objSession = null;
                    final long keyID = p11Key.getKeyID();
                    try {
                        objSession = token.getObjSession();
                        p11Key2 = (P11Key)P11Key.secretKey(objSession, token.p11.C_CopyObject(objSession.id(), keyID, array), p11Key.algorithm, p11Key.keyLength, array);
                    }
                    catch (final PKCS11Exception ex) {
                        throw new InvalidKeyException("Cannot duplicate the PKCS11 key", ex);
                    }
                    finally {
                        p11Key.releaseKeyID();
                        token.releaseSession(objSession);
                    }
                    p11Key = p11Key2;
                }
                return p11Key;
            }
        }
        final P11Key value = token.secretCache.get(key);
        if (value != null) {
            return value;
        }
        if (!"RAW".equalsIgnoreCase(key.getFormat())) {
            throw new InvalidKeyException("Encoded format must be RAW");
        }
        final P11Key key2 = createKey(token, key.getEncoded(), algorithm, n, array);
        token.secretCache.put(key, key2);
        return key2;
    }
    
    static void fixDESParity(final byte[] array, int n) {
        for (int i = 0; i < 8; ++i) {
            final int n2 = array[n] & 0xFE;
            array[n++] = (byte)(n2 | ((Integer.bitCount(n2) & 0x1) ^ 0x1));
        }
    }
    
    private static P11Key createKey(final Token token, final byte[] array, final String s, long n, final CK_ATTRIBUTE[] array2) throws InvalidKeyException {
        int n3;
        final int n2 = n3 = array.length << 3;
        try {
            switch ((int)n) {
                case 19: {
                    n3 = P11KeyGenerator.checkKeySize(288L, n2, token);
                    fixDESParity(array, 0);
                    break;
                }
                case 21: {
                    n3 = P11KeyGenerator.checkKeySize(305L, n2, token);
                    fixDESParity(array, 0);
                    fixDESParity(array, 8);
                    if (n3 == 112) {
                        n = 20L;
                        break;
                    }
                    n = 21L;
                    fixDESParity(array, 16);
                    break;
                }
                case 31: {
                    n3 = P11KeyGenerator.checkKeySize(4224L, n2, token);
                    break;
                }
                case 18: {
                    n3 = P11KeyGenerator.checkKeySize(272L, n2, token);
                    break;
                }
                case 32: {
                    n3 = P11KeyGenerator.checkKeySize(4240L, n2, token);
                    break;
                }
                case 16:
                case 2147483429:
                case 2147483430:
                case 2147483431: {
                    n = 16L;
                    break;
                }
                case 2147483427:
                case 2147483428: {
                    if (n2 == 0) {
                        throw new InvalidKeyException("MAC keys must not be empty");
                    }
                    n = 16L;
                    break;
                }
                default: {
                    throw new InvalidKeyException("Unknown algorithm " + s);
                }
            }
        }
        catch (final InvalidAlgorithmParameterException ex) {
            throw new InvalidKeyException("Invalid key for " + s, ex);
        }
        catch (final ProviderException ex2) {
            throw new InvalidKeyException("Could not create key", ex2);
        }
        Session objSession = null;
        try {
            CK_ATTRIBUTE[] array3;
            if (array2 != null) {
                array3 = new CK_ATTRIBUTE[3 + array2.length];
                System.arraycopy(array2, 0, array3, 3, array2.length);
            }
            else {
                array3 = new CK_ATTRIBUTE[3];
            }
            array3[0] = new CK_ATTRIBUTE(0L, 4L);
            array3[1] = new CK_ATTRIBUTE(256L, n);
            array3[2] = new CK_ATTRIBUTE(17L, array);
            final CK_ATTRIBUTE[] attributes = token.getAttributes("import", 4L, n, array3);
            objSession = token.getObjSession();
            return (P11Key)P11Key.secretKey(objSession, token.p11.C_CreateObject(objSession.id(), attributes), s, n3, attributes);
        }
        catch (final PKCS11Exception ex3) {
            throw new InvalidKeyException("Could not create key", ex3);
        }
        finally {
            token.releaseSession(objSession);
        }
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec == null) {
            throw new InvalidKeySpecException("KeySpec must not be null");
        }
        if (keySpec instanceof SecretKeySpec) {
            try {
                return (SecretKey)convertKey(this.token, (Key)keySpec, this.algorithm);
            }
            catch (final InvalidKeyException ex) {
                throw new InvalidKeySpecException(ex);
            }
        }
        if (this.algorithm.equalsIgnoreCase("DES")) {
            if (keySpec instanceof DESKeySpec) {
                return this.engineGenerateSecret(new SecretKeySpec(((DESKeySpec)keySpec).getKey(), "DES"));
            }
        }
        else if (this.algorithm.equalsIgnoreCase("DESede") && keySpec instanceof DESedeKeySpec) {
            return this.engineGenerateSecret(new SecretKeySpec(((DESedeKeySpec)keySpec).getKey(), "DESede"));
        }
        throw new InvalidKeySpecException("Unsupported spec: " + keySpec.getClass().getName());
    }
    
    private byte[] getKeyBytes(SecretKey engineTranslateKey) throws InvalidKeySpecException {
        try {
            engineTranslateKey = this.engineTranslateKey(engineTranslateKey);
            if (!"RAW".equalsIgnoreCase(engineTranslateKey.getFormat())) {
                throw new InvalidKeySpecException("Could not obtain key bytes");
            }
            return engineTranslateKey.getEncoded();
        }
        catch (final InvalidKeyException ex) {
            throw new InvalidKeySpecException(ex);
        }
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final SecretKey secretKey, final Class<?> clazz) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (secretKey == null || clazz == null) {
            throw new InvalidKeySpecException("key and keySpec must not be null");
        }
        if (SecretKeySpec.class.isAssignableFrom(clazz)) {
            return new SecretKeySpec(this.getKeyBytes(secretKey), this.algorithm);
        }
        if (this.algorithm.equalsIgnoreCase("DES")) {
            try {
                if (DESKeySpec.class.isAssignableFrom(clazz)) {
                    return new DESKeySpec(this.getKeyBytes(secretKey));
                }
                throw new InvalidKeySpecException("Unsupported spec: " + clazz.getName());
            }
            catch (final InvalidKeyException ex) {
                throw new InvalidKeySpecException(ex);
            }
        }
        if (this.algorithm.equalsIgnoreCase("DESede")) {
            try {
                if (DESedeKeySpec.class.isAssignableFrom(clazz)) {
                    return new DESedeKeySpec(this.getKeyBytes(secretKey));
                }
            }
            catch (final InvalidKeyException ex2) {
                throw new InvalidKeySpecException(ex2);
            }
        }
        throw new InvalidKeySpecException("Unsupported spec: " + clazz.getName());
    }
    
    @Override
    protected SecretKey engineTranslateKey(final SecretKey secretKey) throws InvalidKeyException {
        return (SecretKey)convertKey(this.token, secretKey, this.algorithm);
    }
    
    static {
        keyTypes = new HashMap<String, Long>();
        addKeyType("RC4", 18L);
        addKeyType("ARCFOUR", 18L);
        addKeyType("DES", 19L);
        addKeyType("DESede", 21L);
        addKeyType("AES", 31L);
        addKeyType("Blowfish", 32L);
        addKeyType("RC2", 17L);
        addKeyType("IDEA", 26L);
        addKeyType("TlsPremasterSecret", 2147483429L);
        addKeyType("TlsRsaPremasterSecret", 2147483430L);
        addKeyType("TlsMasterSecret", 2147483431L);
        addKeyType("Generic", 16L);
    }
}
