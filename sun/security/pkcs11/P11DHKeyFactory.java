package sun.security.pkcs11;

import java.security.KeyFactory;
import sun.security.pkcs11.wrapper.CK_ATTRIBUTE;
import java.math.BigInteger;
import javax.crypto.spec.DHPrivateKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import javax.crypto.spec.DHPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.KeySpec;
import javax.crypto.interfaces.DHPrivateKey;
import java.security.PrivateKey;
import javax.crypto.spec.DHParameterSpec;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import javax.crypto.interfaces.DHPublicKey;
import java.security.PublicKey;

final class P11DHKeyFactory extends P11KeyFactory
{
    P11DHKeyFactory(final Token token, final String s) {
        super(token, s);
    }
    
    @Override
    PublicKey implTranslatePublicKey(PublicKey publicKey) throws InvalidKeyException {
        try {
            if (publicKey instanceof DHPublicKey) {
                final DHPublicKey dhPublicKey = (DHPublicKey)publicKey;
                final DHParameterSpec params = dhPublicKey.getParams();
                return this.generatePublic(dhPublicKey.getY(), params.getP(), params.getG());
            }
            if ("X.509".equals(publicKey.getFormat())) {
                try {
                    publicKey = (PublicKey)this.implGetSoftwareFactory().translateKey(publicKey);
                    return this.implTranslatePublicKey(publicKey);
                }
                catch (final GeneralSecurityException ex) {
                    throw new InvalidKeyException("Could not translate key", ex);
                }
            }
            throw new InvalidKeyException("PublicKey must be instance of DHPublicKey or have X.509 encoding");
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeyException("Could not create DH public key", ex2);
        }
    }
    
    @Override
    PrivateKey implTranslatePrivateKey(PrivateKey privateKey) throws InvalidKeyException {
        try {
            if (privateKey instanceof DHPrivateKey) {
                final DHPrivateKey dhPrivateKey = (DHPrivateKey)privateKey;
                final DHParameterSpec params = dhPrivateKey.getParams();
                return this.generatePrivate(dhPrivateKey.getX(), params.getP(), params.getG());
            }
            if ("PKCS#8".equals(privateKey.getFormat())) {
                try {
                    privateKey = (PrivateKey)this.implGetSoftwareFactory().translateKey(privateKey);
                    return this.implTranslatePrivateKey(privateKey);
                }
                catch (final GeneralSecurityException ex) {
                    throw new InvalidKeyException("Could not translate key", ex);
                }
            }
            throw new InvalidKeyException("PrivateKey must be instance of DHPrivateKey or have PKCS#8 encoding");
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeyException("Could not create DH private key", ex2);
        }
    }
    
    @Override
    protected PublicKey engineGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof X509EncodedKeySpec) {
            try {
                return this.implTranslatePublicKey(this.implGetSoftwareFactory().generatePublic(keySpec));
            }
            catch (final GeneralSecurityException ex) {
                throw new InvalidKeySpecException("Could not create DH public key", ex);
            }
        }
        if (!(keySpec instanceof DHPublicKeySpec)) {
            throw new InvalidKeySpecException("Only DHPublicKeySpec and X509EncodedKeySpec supported for DH public keys");
        }
        try {
            final DHPublicKeySpec dhPublicKeySpec = (DHPublicKeySpec)keySpec;
            return this.generatePublic(dhPublicKeySpec.getY(), dhPublicKeySpec.getP(), dhPublicKeySpec.getG());
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeySpecException("Could not create DH public key", ex2);
        }
    }
    
    @Override
    protected PrivateKey engineGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        this.token.ensureValid();
        if (keySpec instanceof PKCS8EncodedKeySpec) {
            try {
                return this.implTranslatePrivateKey(this.implGetSoftwareFactory().generatePrivate(keySpec));
            }
            catch (final GeneralSecurityException ex) {
                throw new InvalidKeySpecException("Could not create DH private key", ex);
            }
        }
        if (!(keySpec instanceof DHPrivateKeySpec)) {
            throw new InvalidKeySpecException("Only DHPrivateKeySpec and PKCS8EncodedKeySpec supported for DH private keys");
        }
        try {
            final DHPrivateKeySpec dhPrivateKeySpec = (DHPrivateKeySpec)keySpec;
            return this.generatePrivate(dhPrivateKeySpec.getX(), dhPrivateKeySpec.getP(), dhPrivateKeySpec.getG());
        }
        catch (final PKCS11Exception ex2) {
            throw new InvalidKeySpecException("Could not create DH private key", ex2);
        }
    }
    
    private PublicKey generatePublic(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) throws PKCS11Exception {
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 2L, 2L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 2L), new CK_ATTRIBUTE(256L, 2L), new CK_ATTRIBUTE(17L, bigInteger), new CK_ATTRIBUTE(304L, bigInteger2), new CK_ATTRIBUTE(306L, bigInteger3) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.publicKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "DH", bigInteger2.bitLength(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    private PrivateKey generatePrivate(final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3) throws PKCS11Exception {
        final CK_ATTRIBUTE[] attributes = this.token.getAttributes("import", 3L, 2L, new CK_ATTRIBUTE[] { new CK_ATTRIBUTE(0L, 3L), new CK_ATTRIBUTE(256L, 2L), new CK_ATTRIBUTE(17L, bigInteger), new CK_ATTRIBUTE(304L, bigInteger2), new CK_ATTRIBUTE(306L, bigInteger3) });
        Session objSession = null;
        try {
            objSession = this.token.getObjSession();
            return P11Key.privateKey(objSession, this.token.p11.C_CreateObject(objSession.id(), attributes), "DH", bigInteger2.bitLength(), attributes);
        }
        finally {
            this.token.releaseSession(objSession);
        }
    }
    
    @Override
     <T extends KeySpec> T implGetPublicKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (DHPublicKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(306L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
            }
            finally {
                p11Key.releaseKeyID();
            }
            return clazz.cast(new DHPublicKeySpec(array2[0].getBigInteger(), array2[1].getBigInteger(), array2[2].getBigInteger()));
        }
        throw new InvalidKeySpecException("Only DHPublicKeySpec and X509EncodedKeySpec supported for DH public keys");
    }
    
    @Override
     <T extends KeySpec> T implGetPrivateKeySpec(final P11Key p11Key, final Class<T> clazz, final Session[] array) throws PKCS11Exception, InvalidKeySpecException {
        if (DHPrivateKeySpec.class.isAssignableFrom(clazz)) {
            array[0] = this.token.getObjSession();
            final CK_ATTRIBUTE[] array2 = { new CK_ATTRIBUTE(17L), new CK_ATTRIBUTE(304L), new CK_ATTRIBUTE(306L) };
            final long keyID = p11Key.getKeyID();
            try {
                this.token.p11.C_GetAttributeValue(array[0].id(), keyID, array2);
            }
            finally {
                p11Key.releaseKeyID();
            }
            return clazz.cast(new DHPrivateKeySpec(array2[0].getBigInteger(), array2[1].getBigInteger(), array2[2].getBigInteger()));
        }
        throw new InvalidKeySpecException("Only DHPrivateKeySpec and PKCS8EncodedKeySpec supported for DH private keys");
    }
    
    @Override
    KeyFactory implGetSoftwareFactory() throws GeneralSecurityException {
        return KeyFactory.getInstance("DH", P11Util.getSunJceProvider());
    }
}
