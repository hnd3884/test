package sun.security.pkcs11;

import java.security.KeyFactory;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import sun.security.rsa.RSAKeyFactory;
import java.math.BigInteger;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.KeySpec;
import sun.security.rsa.RSAPrivateCrtKeyImpl;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.PrivateKey;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.InvalidKeyException;
import sun.security.rsa.RSAPublicKeyImpl;
import java.security.interfaces.RSAPublicKey;
import java.security.PublicKey;

final class P11RSAKeyFactory extends P11KeyFactory
{
    P11RSAKeyFactory(final Token token, final String s) {
        super(token, s);
    }
    
    @Override
    PublicKey implTranslatePublicKey(final PublicKey publicKey) throws InvalidKeyException {
        try {
            if (publicKey instanceof RSAPublicKey) {
                final RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKey;
                return this.generatePublic(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
            }
            if ("X.509".equals(publicKey.getFormat())) {
                return this.implTranslatePublicKey(RSAPublicKeyImpl.newKey(publicKey.getEncoded()));
            }
            throw new InvalidKeyException("PublicKey must be instance of RSAPublicKey or have X.509 encoding");
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("Could not create RSA public key", ex);
        }
    }
    
    @Override
    PrivateKey implTranslatePrivateKey(final PrivateKey privateKey) throws InvalidKeyException {
        try {
            if (privateKey instanceof RSAPrivateCrtKey) {
                final RSAPrivateCrtKey rsaPrivateCrtKey = (RSAPrivateCrtKey)privateKey;
                return this.generatePrivate(rsaPrivateCrtKey.getModulus(), rsaPrivateCrtKey.getPublicExponent(), rsaPrivateCrtKey.getPrivateExponent(), rsaPrivateCrtKey.getPrimeP(), rsaPrivateCrtKey.getPrimeQ(), rsaPrivateCrtKey.getPrimeExponentP(), rsaPrivateCrtKey.getPrimeExponentQ(), rsaPrivateCrtKey.getCrtCoefficient());
            }
            if (privateKey instanceof RSAPrivateKey) {
                final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)privateKey;
                return this.generatePrivate(rsaPrivateKey.getModulus(), rsaPrivateKey.getPrivateExponent());
            }
            if ("PKCS#8".equals(privateKey.getFormat())) {
                return this.implTranslatePrivateKey(RSAPrivateCrtKeyImpl.newKey(privateKey.getEncoded()));
            }
            throw new InvalidKeyException("Private key must be instance of RSAPrivate(Crt)Key or have PKCS#8 encoding");
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("Could not create RSA private key", ex);
        }
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof X509EncodedKeySpec) {
            try {
                return this.implTranslatePublicKey(RSAPublicKeyImpl.newKey(((X509EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final InvalidKeyException ex) {
                throw new InvalidKeySpecException("Could not create RSA public key", ex);
            }
        }
        if (!(keySpec instanceof RSAPublicKeySpec)) {
            throw new InvalidKeySpecException("Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys");
        }
        try {
            final RSAPublicKeySpec rsaPublicKeySpec = (RSAPublicKeySpec)keySpec;
            return this.generatePublic(rsaPublicKeySpec.getModulus(), rsaPublicKeySpec.getPublicExponent());
        }
        catch (final PKCS11Exception | InvalidKeyException ex2) {
            throw new InvalidKeySpecException("Could not create RSA public key", (Throwable)ex2);
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                return this.implTranslatePrivateKey(RSAPrivateCrtKeyImpl.newKey(((PKCS8EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final GeneralSecurityException ex) {
                throw new InvalidKeySpecException("Could not create RSA private key", ex);
            }
        }
        try {
            if (keySpec instanceof RSAPrivateCrtKeySpec) {
                final RSAPrivateCrtKeySpec rsaPrivateCrtKeySpec = (RSAPrivateCrtKeySpec)keySpec;
                return this.generatePrivate(rsaPrivateCrtKeySpec.getModulus(), rsaPrivateCrtKeySpec.getPublicExponent(), rsaPrivateCrtKeySpec.getPrivateExponent(), rsaPrivateCrtKeySpec.getPrimeP(), rsaPrivateCrtKeySpec.getPrimeQ(), rsaPrivateCrtKeySpec.getPrimeExponentP(), rsaPrivateCrtKeySpec.getPrimeExponentQ(), rsaPrivateCrtKeySpec.getCrtCoefficient());
            }
            if (keySpec instanceof RSAPrivateKeySpec) {
                final RSAPrivateKeySpec rsaPrivateKeySpec = (RSAPrivateKeySpec)keySpec;
                return this.generatePrivate(rsaPrivateKeySpec.getModulus(), rsaPrivateKeySpec.getPrivateExponent());
            }
            throw new InvalidKeySpecException("Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys");
        }
        catch (final PKCS11Exception | InvalidKeyException ex2) {
            throw new InvalidKeySpecException("Could not create RSA private key", (Throwable)ex2);
        }
    }
    
    private PublicKey generatePublic(final BigInteger bigInteger, final BigInteger bigInteger2) throws PKCS11Exception, InvalidKeyException {
        RSAKeyFactory.checkKeyLengths(bigInteger.bitLength(), bigInteger2, -1, 65536);
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 2L, 0L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 2L), new CK_ATTRIBUTE(256L, 0L), new CK_ATTRIBUTE(288L, bigInteger), new CK_ATTRIBUTE(290L, bigInteger2) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.publicKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "RSA", bigInteger.bitLength(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    private PrivateKey generatePrivate(final BigInteger bigInteger, final BigInteger bigInteger2) throws PKCS11Exception, InvalidKeyException {
        RSAKeyFactory.checkKeyLengths(bigInteger.bitLength(), null, -1, 65536);
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 3L, 0L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 3L), new CK_ATTRIBUTE(256L, 0L), new CK_ATTRIBUTE(288L, bigInteger), new CK_ATTRIBUTE(291L, bigInteger2) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.privateKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "RSA", bigInteger.bitLength(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    private PrivateKey generatePrivate(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6, final BigInteger bigInteger7, final BigInteger bigInteger8) throws PKCS11Exception, InvalidKeyException {
        RSAKeyFactory.checkKeyLengths(bigInteger.bitLength(), bigInteger2, -1, 65536);
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 3L, 0L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 3L), new CK_ATTRIBUTE(256L, 0L), new CK_ATTRIBUTE(288L, bigInteger), new CK_ATTRIBUTE(290L, bigInteger2), new CK_ATTRIBUTE(291L, bigInteger3), new CK_ATTRIBUTE(292L, bigInteger4), new CK_ATTRIBUTE(293L, bigInteger5), new CK_ATTRIBUTE(294L, bigInteger6), new CK_ATTRIBUTE(295L, bigInteger7), new CK_ATTRIBUTE(296L, bigInteger8) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.privateKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "RSA", bigInteger.bitLength(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    @Override
     <T extends KeySpec> T implGetPublicKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (RSAPublicKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(288L), new CK_ATTRIBUTE(290L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
            }
            finally {
                p11Key.releaseKeyID();
            }
            return clazz.cast(new RSAPublicKeySpec(array2[0].getBigInteger(), array2[1].getBigInteger()));
        }
        throw new InvalidKeySpecException("Only RSAPublicKeySpec and X509EncodedKeySpec supported for RSA public keys");
    }
    
    @Override
     <T extends KeySpec> T implGetPrivateKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (RSAPrivateCrtKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(288L), new CK_ATTRIBUTE(290L), new CK_ATTRIBUTE(291L), new CK_ATTRIBUTE(292L), new CK_ATTRIBUTE(293L), new CK_ATTRIBUTE(294L), new CK_ATTRIBUTE(295L), new CK_ATTRIBUTE(296L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
            }
            finally {
                p11Key.releaseKeyID();
            }
            return clazz.cast(new RSAPrivateCrtKeySpec(array2[0].getBigInteger(), array2[1].getBigInteger(), array2[2].getBigInteger(), array2[3].getBigInteger(), array2[4].getBigInteger(), array2[5].getBigInteger(), array2[6].getBigInteger(), array2[7].getBigInteger()));
        }
        if (RSAPrivateKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array3 = { new CK_ATTRIBUTE(288L), new CK_ATTRIBUTE(291L) };
            final long keyID2 = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID2, array3);
            }
            finally {
                p11Key.releaseKeyID();
            }
            return clazz.cast(new RSAPrivateKeySpec(array3[0].getBigInteger(), array3[1].getBigInteger()));
        }
        throw new InvalidKeySpecException("Only RSAPrivate(Crt)KeySpec and PKCS8EncodedKeySpec supported for RSA private keys");
    }
    
    @Override
    KeyFactory implGetSoftwareFactory() throws GeneralSecurityException {
        return KeyFactory.getInstance("RSA", P11Util.getSunRsaSignProvider());
    }
}
