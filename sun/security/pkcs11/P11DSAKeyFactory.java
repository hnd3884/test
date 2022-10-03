package sun.security.pkcs11;

import java.security.KeyFactory;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.math.BigInteger;
import java.security.spec.DSAPrivateKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.KeySpec;
import java.security.interfaces.DSAPrivateKey;
import java.security.PrivateKey;
import java.security.interfaces.DSAParams;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.InvalidKeyException;
import java.security.interfaces.DSAPublicKey;
import java.security.PublicKey;

final class P11DSAKeyFactory extends P11KeyFactory
{
    P11DSAKeyFactory(final Token token, final String s) {
        super(token, s);
    }
    
    @Override
    PublicKey implTranslatePublicKey(final PublicKey publicKey) throws InvalidKeyException {
        try {
            if (publicKey instanceof DSAPublicKey) {
                final DSAPublicKey dsaPublicKey = (DSAPublicKey)publicKey;
                final DSAParams params = dsaPublicKey.getParams();
                return this.generatePublic(dsaPublicKey.getY(), params.getP(), params.getQ(), params.getG());
            }
            if ("X.509".equals(publicKey.getFormat())) {
                return this.implTranslatePublicKey(new sun.security.provider.DSAPublicKey(publicKey.getEncoded()));
            }
            throw new InvalidKeyException("PublicKey must be instance of DSAPublicKey or have X.509 encoding");
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("Could not create DSA public key", ex);
        }
    }
    
    @Override
    PrivateKey implTranslatePrivateKey(final PrivateKey privateKey) throws InvalidKeyException {
        try {
            if (privateKey instanceof DSAPrivateKey) {
                final DSAPrivateKey dsaPrivateKey = (DSAPrivateKey)privateKey;
                final DSAParams params = dsaPrivateKey.getParams();
                return this.generatePrivate(dsaPrivateKey.getX(), params.getP(), params.getQ(), params.getG());
            }
            if ("PKCS#8".equals(privateKey.getFormat())) {
                return this.implTranslatePrivateKey(new sun.security.provider.DSAPrivateKey(privateKey.getEncoded()));
            }
            throw new InvalidKeyException("PrivateKey must be instance of DSAPrivateKey or have PKCS#8 encoding");
        }
        catch (final PKCS11Exception ex) {
            throw new InvalidKeyException("Could not create DSA private key", ex);
        }
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof X509EncodedKeySpec) {
            try {
                return this.implTranslatePublicKey(new sun.security.provider.DSAPublicKey(((X509EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final InvalidKeyException ex) {
                throw new InvalidKeySpecException("Could not create DSA public key", ex);
            }
        }
        if (!(keySpec instanceof DSAPublicKeySpec)) {
            throw new InvalidKeySpecException("Only DSAPublicKeySpec and X509EncodedKeySpec supported for DSA public keys");
        }
        try {
            final DSAPublicKeySpec dsaPublicKeySpec = (DSAPublicKeySpec)keySpec;
            return this.generatePublic(dsaPublicKeySpec.getY(), dsaPublicKeySpec.getP(), dsaPublicKeySpec.getQ(), dsaPublicKeySpec.getG());
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeySpecException("Could not create DSA public key", ex2);
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                return this.implTranslatePrivateKey(new sun.security.provider.DSAPrivateKey(((PKCS8EncodedKeySpec)keySpec).getEncoded()));
            }
            catch (final GeneralSecurityException ex) {
                throw new InvalidKeySpecException("Could not create DSA private key", ex);
            }
        }
        if (!(keySpec instanceof DSAPrivateKeySpec)) {
            throw new InvalidKeySpecException("Only DSAPrivateKeySpec and PKCS8EncodedKeySpec supported for DSA private keys");
        }
        try {
            final DSAPrivateKeySpec dsaPrivateKeySpec = (DSAPrivateKeySpec)keySpec;
            return this.generatePrivate(dsaPrivateKeySpec.getX(), dsaPrivateKeySpec.getP(), dsaPrivateKeySpec.getQ(), dsaPrivateKeySpec.getG());
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeySpecException("Could not create DSA private key", ex2);
        }
    }
    
    private PublicKey generatePublic(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) throws PKCS11Exception {
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 2L, 1L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 2L), new CK_ATTRIBUTE(256L, 1L), new CK_ATTRIBUTE(17L, bigInteger), new CK_ATTRIBUTE(304L, bigInteger2), new CK_ATTRIBUTE(305L, bigInteger3), new CK_ATTRIBUTE(306L, bigInteger4) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.publicKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "DSA", bigInteger2.bitLength(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    private PrivateKey generatePrivate(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4) throws PKCS11Exception {
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 3L, 1L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 3L), new CK_ATTRIBUTE(256L, 1L), new CK_ATTRIBUTE(17L, bigInteger), new CK_ATTRIBUTE(304L, bigInteger2), new CK_ATTRIBUTE(305L, bigInteger3), new CK_ATTRIBUTE(306L, bigInteger4) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.privateKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "DSA", bigInteger2.bitLength(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    @Override
     <T extends KeySpec> T implGetPublicKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (DSAPublicKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(305L), new CK_ATTRIBUTE(306L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
            }
            finally {
                p11Key.releaseKeyID();
            }
            return clazz.cast(new DSAPublicKeySpec(array2[0].getBigInteger(), array2[1].getBigInteger(), array2[2].getBigInteger(), array2[3].getBigInteger()));
        }
        throw new InvalidKeySpecException("Only DSAPublicKeySpec and X509EncodedKeySpec supported for DSA public keys");
    }
    
    @Override
     <T extends KeySpec> T implGetPrivateKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (DSAPrivateKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(305L), new CK_ATTRIBUTE(306L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
            }
            finally {
                p11Key.releaseKeyID();
            }
            return clazz.cast(new DSAPrivateKeySpec(array2[0].getBigInteger(), array2[1].getBigInteger(), array2[2].getBigInteger(), array2[3].getBigInteger()));
        }
        throw new InvalidKeySpecException("Only DSAPrivateKeySpec and PKCS8EncodedKeySpec supported for DSA private keys");
    }
    
    @Override
    KeyFactory implGetSoftwareFactory() throws GeneralSecurityException {
        return KeyFactory.getInstance("DSA", P11Util.getSunProvider());
    }
}
