package sun.security.pkcs11;

import java.security.KeyFactory;
import java.math.BigInteger;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.util.DerValue;
import java.security.spec.ECPrivateKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.interfaces.ECPrivateKey;
import java.security.PrivateKey;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.interfaces.ECPublicKey;
import java.security.PublicKey;
import java.security.spec.ECPoint;
import java.security.spec.EllipticCurve;
import java.io.IOException;
import sun.security.util.ECUtil;
import java.security.spec.ECParameterSpec;
import java.security.Security;
import java.security.Provider;

final class P11ECKeyFactory extends P11KeyFactory
{
    private static Provider sunECprovider;
    
    private static Provider getSunECProvider() {
        if (P11ECKeyFactory.sunECprovider == null) {
            P11ECKeyFactory.sunECprovider = Security.getProvider("SunEC");
            if (P11ECKeyFactory.sunECprovider == null) {
                throw new RuntimeException("Cannot load SunEC provider");
            }
        }
        return P11ECKeyFactory.sunECprovider;
    }
    
    P11ECKeyFactory(final Token token, final String s) {
        super(token, s);
    }
    
    static ECParameterSpec getECParameterSpec(final String s) {
        return ECUtil.getECParameterSpec(getSunECProvider(), s);
    }
    
    static ECParameterSpec getECParameterSpec(final int n) {
        return ECUtil.getECParameterSpec(getSunECProvider(), n);
    }
    
    static ECParameterSpec getECParameterSpec(final ECParameterSpec ecParameterSpec) {
        return ECUtil.getECParameterSpec(getSunECProvider(), ecParameterSpec);
    }
    
    static ECParameterSpec decodeParameters(final byte[] array) throws IOException {
        return ECUtil.getECParameterSpec(getSunECProvider(), array);
    }
    
    static byte[] encodeParameters(final ECParameterSpec ecParameterSpec) {
        return ECUtil.encodeECParameterSpec(getSunECProvider(), ecParameterSpec);
    }
    
    static ECPoint decodePoint(final byte[] array, final EllipticCurve ellipticCurve) throws IOException {
        return ECUtil.decodePoint(array, ellipticCurve);
    }
    
    static byte[] getEncodedPublicValue(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey = (ECPublicKey)publicKey;
            return ECUtil.encodePoint(ecPublicKey.getW(), ecPublicKey.getParams().getCurve());
        }
        throw new InvalidKeyException("Key class not yet supported: " + publicKey.getClass().getName());
    }
    
    @Override
    PublicKey implTranslatePublicKey(final PublicKey publicKey) throws InvalidKeyException {
        try {
            if (publicKey instanceof ECPublicKey) {
                final ECPublicKey ecPublicKey = (ECPublicKey)publicKey;
                return this.generatePublic(ecPublicKey.getW(), ecPublicKey.getParams());
            }
            if ("X.509".equals(publicKey.getFormat())) {
                final byte[] encoded = publicKey.getEncoded();
                ECPublicKey decodeX509ECPublicKey;
                try {
                    decodeX509ECPublicKey = P11ECUtil.decodeX509ECPublicKey(encoded);
                }
                catch (final InvalidKeySpecException ex) {
                    throw new InvalidKeyException(ex);
                }
                return this.implTranslatePublicKey(decodeX509ECPublicKey);
            }
            throw new InvalidKeyException("PublicKey must be instance of ECPublicKey or have X.509 encoding");
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeyException("Could not create EC public key", ex2);
        }
    }
    
    @Override
    PrivateKey implTranslatePrivateKey(final PrivateKey privateKey) throws InvalidKeyException {
        try {
            if (privateKey instanceof ECPrivateKey) {
                final ECPrivateKey ecPrivateKey = (ECPrivateKey)privateKey;
                return this.generatePrivate(ecPrivateKey.getS(), ecPrivateKey.getParams());
            }
            if ("PKCS#8".equals(privateKey.getFormat())) {
                final byte[] encoded = privateKey.getEncoded();
                ECPrivateKey decodePKCS8ECPrivateKey;
                try {
                    decodePKCS8ECPrivateKey = P11ECUtil.decodePKCS8ECPrivateKey(encoded);
                }
                catch (final InvalidKeySpecException ex) {
                    throw new InvalidKeyException(ex);
                }
                return this.implTranslatePrivateKey(decodePKCS8ECPrivateKey);
            }
            throw new InvalidKeyException("PrivateKey must be instance of ECPrivateKey or have PKCS#8 encoding");
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeyException("Could not create EC private key", ex2);
        }
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof X509EncodedKeySpec) {
            try {
                return this.implTranslatePublicKey(P11ECUtil.decodeX509ECPublicKey(((X509EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final InvalidKeyException ex) {
                throw new InvalidKeySpecException("Could not create EC public key", ex);
            }
        }
        if (!(keySpec instanceof ECPublicKeySpec)) {
            throw new InvalidKeySpecException("Only ECPublicKeySpec and X509EncodedKeySpec supported for EC public keys");
        }
        try {
            final ECPublicKeySpec ecPublicKeySpec = (ECPublicKeySpec)keySpec;
            return this.generatePublic(ecPublicKeySpec.getW(), ecPublicKeySpec.getParams());
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeySpecException("Could not create EC public key", ex2);
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                return this.implTranslatePrivateKey(P11ECUtil.decodePKCS8ECPrivateKey(((PKCS8EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final GeneralSecurityException ex) {
                throw new InvalidKeySpecException("Could not create EC private key", ex);
            }
        }
        if (!(keySpec instanceof ECPrivateKeySpec)) {
            throw new InvalidKeySpecException("Only ECPrivateKeySpec and PKCS8EncodedKeySpec supported for EC private keys");
        }
        try {
            final ECPrivateKeySpec ecPrivateKeySpec = (ECPrivateKeySpec)keySpec;
            return this.generatePrivate(ecPrivateKeySpec.getS(), ecPrivateKeySpec.getParams());
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeySpecException("Could not create EC private key", ex2);
        }
    }
    
    private PublicKey generatePublic(final ECPoint ecPoint, final ECParameterSpec ecParameterSpec) throws PKCS11Exception {
        final byte[] encodeECParameterSpec = ECUtil.encodeECParameterSpec(getSunECProvider(), ecParameterSpec);
        byte[] array = ECUtil.encodePoint(ecPoint, ecParameterSpec.getCurve());
        if (!this.token.config.getUseEcX963Encoding()) {
            try {
                array = new DerValue((byte)4, array).toByteArray();
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("Could not DER encode point", ex);
            }
        }
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 2L, 3L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 2L), new CK_ATTRIBUTE(256L, 3L), new CK_ATTRIBUTE(385L, array), new CK_ATTRIBUTE(384L, encodeECParameterSpec) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.publicKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "EC", ecParameterSpec.getCurve().getField().getFieldSize(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    private PrivateKey generatePrivate(final BigInteger bigInteger, final ECParameterSpec ecParameterSpec) throws PKCS11Exception {
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 3L, 3L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 3L), new CK_ATTRIBUTE(256L, 3L), new CK_ATTRIBUTE(17L, bigInteger), new CK_ATTRIBUTE(384L, ECUtil.encodeECParameterSpec(getSunECProvider(), ecParameterSpec)) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.privateKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "EC", ecParameterSpec.getCurve().getField().getFieldSize(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    @Override
     <T extends KeySpec> T implGetPublicKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (ECPublicKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(385L), new CK_ATTRIBUTE(384L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
                final ECParameterSpec decodeParameters = decodeParameters(array2[1].getByteArray());
                return clazz.cast(new ECPublicKeySpec(decodePoint(array2[0].getByteArray(), decodeParameters.getCurve()), decodeParameters));
            }
            catch (final IOException ex) {
                throw new InvalidKeySpecException("Could not parse key", ex);
            }
            finally {
                p11Key.releaseKeyID();
            }
        }
        throw new InvalidKeySpecException("Only ECPublicKeySpec and X509EncodedKeySpec supported for EC public keys");
    }
    
    @Override
     <T extends KeySpec> T implGetPrivateKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (ECPrivateKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(384L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
                return clazz.cast(new ECPrivateKeySpec(array2[0].getBigInteger(), decodeParameters(array2[1].getByteArray())));
            }
            catch (final IOException ex) {
                throw new InvalidKeySpecException("Could not parse key", ex);
            }
            finally {
                p11Key.releaseKeyID();
            }
        }
        throw new InvalidKeySpecException("Only ECPrivateKeySpec and PKCS8EncodedKeySpec supported for EC private keys");
    }
    
    @Override
    KeyFactory implGetSoftwareFactory() throws GeneralSecurityException {
        return KeyFactory.getInstance("EC", getSunECProvider());
    }
}
