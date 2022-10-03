package sun.security.pkcs11;

import sun.security.ec.ECPrivateKeyImpl;
import java.security.PrivateKey;
import java.security.GeneralSecurityException;
import sun.security.ec.ECPublicKeyImpl;
import java.security.PublicKey;
import java.security.spec.ECPrivateKeySpec;
import java.math.BigInteger;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.ECPublicKeySpec;
import sun.security.x509.X509Key;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.ECPublicKey;

final class P11ECUtil
{
    static ECPublicKey decodeX509ECPublicKey(final byte[] array) throws InvalidKeySpecException {
        return (ECPublicKey)ECGeneratePublic(new X509EncodedKeySpec(array));
    }
    
    static byte[] x509EncodeECPublicKey(final ECPoint ecPoint, final ECParameterSpec ecParameterSpec) throws InvalidKeySpecException {
        return ((X509Key)ECGeneratePublic(new ECPublicKeySpec(ecPoint, ecParameterSpec))).getEncoded();
    }
    
    static ECPrivateKey decodePKCS8ECPrivateKey(final byte[] array) throws InvalidKeySpecException {
        return (ECPrivateKey)ECGeneratePrivate(new PKCS8EncodedKeySpec(array));
    }
    
    static ECPrivateKey generateECPrivateKey(final BigInteger bigInteger, final ECParameterSpec ecParameterSpec) throws InvalidKeySpecException {
        return (ECPrivateKey)ECGeneratePrivate(new ECPrivateKeySpec(bigInteger, ecParameterSpec));
    }
    
    private static PublicKey ECGeneratePublic(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof X509EncodedKeySpec) {
                return new ECPublicKeyImpl(((X509EncodedKeySpec)keySpec).getEncoded());
            }
            if (keySpec instanceof ECPublicKeySpec) {
                final ECPublicKeySpec ecPublicKeySpec = (ECPublicKeySpec)keySpec;
                return new ECPublicKeyImpl(ecPublicKeySpec.getW(), ecPublicKeySpec.getParams());
            }
            throw new InvalidKeySpecException("Only ECPublicKeySpec and X509EncodedKeySpec supported for EC public keys");
        }
        catch (final InvalidKeySpecException ex) {
            throw ex;
        }
        catch (final GeneralSecurityException ex2) {
            throw new InvalidKeySpecException(ex2);
        }
    }
    
    private static PrivateKey ECGeneratePrivate(final KeySpec keySpec) throws InvalidKeySpecException {
        try {
            if (keySpec instanceof PKCS8EncodedKeySpec) {
                return new ECPrivateKeyImpl(((PKCS8EncodedKeySpec)keySpec).getEncoded());
            }
            if (keySpec instanceof ECPrivateKeySpec) {
                final ECPrivateKeySpec ecPrivateKeySpec = (ECPrivateKeySpec)keySpec;
                return new ECPrivateKeyImpl(ecPrivateKeySpec.getS(), ecPrivateKeySpec.getParams());
            }
            throw new InvalidKeySpecException("Only ECPrivateKeySpec and PKCS8EncodedKeySpec supported for EC private keys");
        }
        catch (final InvalidKeySpecException ex) {
            throw ex;
        }
        catch (final GeneralSecurityException ex2) {
            throw new InvalidKeySpecException(ex2);
        }
    }
    
    private P11ECUtil() {
    }
}
