package cryptix.jce.provider.keyfactory;

import java.security.InvalidKeyException;
import cryptix.jce.provider.key.RawSecretKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.SecretKeyFactorySpi;

public final class DESKeyFactory extends SecretKeyFactorySpi
{
    private DESKeySpec desKeySpec;
    
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec == null || !(keySpec instanceof DESKeySpec)) {
            throw new InvalidKeySpecException("Cannot generate SecretKey using given KeySpec.");
        }
        this.desKeySpec = (DESKeySpec)keySpec;
        RawSecretKey key = null;
        key = new RawSecretKey("DES", this.desKeySpec.getKey());
        return key;
    }
    
    protected KeySpec engineGetKeySpec(final SecretKey key, final Class keySpec) throws InvalidKeySpecException {
        return null;
    }
    
    protected SecretKey engineTranslateKey(final SecretKey key) throws InvalidKeyException {
        return null;
    }
    
    public DESKeyFactory() {
        this.desKeySpec = null;
    }
}
