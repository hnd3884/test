package sun.security.pkcs11;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;

final class ConstructKeys
{
    private static final PublicKey constructPublicKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            return KeyFactory.getInstance(s).generatePublic(new X509EncodedKeySpec(array));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException("No installed providers can create keys for the " + s + "algorithm", ex);
        }
        catch (final InvalidKeySpecException ex2) {
            throw new InvalidKeyException("Cannot construct public key", ex2);
        }
    }
    
    private static final PrivateKey constructPrivateKey(final byte[] array, final String s) throws InvalidKeyException, NoSuchAlgorithmException {
        try {
            return KeyFactory.getInstance(s).generatePrivate(new PKCS8EncodedKeySpec(array));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new NoSuchAlgorithmException("No installed providers can create keys for the " + s + "algorithm", ex);
        }
        catch (final InvalidKeySpecException ex2) {
            throw new InvalidKeyException("Cannot construct private key", ex2);
        }
    }
    
    private static final SecretKey constructSecretKey(final byte[] array, final String s) {
        return new SecretKeySpec(array, s);
    }
    
    static final Key constructKey(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        switch (n) {
            case 3: {
                return constructSecretKey(array, s);
            }
            case 2: {
                return constructPrivateKey(array, s);
            }
            case 1: {
                return constructPublicKey(array, s);
            }
            default: {
                throw new InvalidKeyException("Unknown keytype " + n);
            }
        }
    }
}
