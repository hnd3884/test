package cryptix.jce.provider.keyfactory;

import java.security.InvalidKeyException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import cryptix.jce.provider.key.RawSecretKey;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactorySpi;

public final class PBEKeyFactory extends SecretKeyFactorySpi
{
    private PBEKeySpec pbeKeySpec;
    private static /* synthetic */ Class array$C;
    
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        if (keySpec == null || !(keySpec instanceof PBEKeySpec)) {
            throw new InvalidKeySpecException("Cannot generate SecretKey using given KeySpec.");
        }
        this.pbeKeySpec = (PBEKeySpec)keySpec;
        final RawSecretKey key = new RawSecretKey("PBE", new String(this.pbeKeySpec.getPassword()).getBytes());
        return key;
    }
    
    protected KeySpec engineGetKeySpec(final SecretKey key, final Class keySpec) throws InvalidKeySpecException {
        if (key == null || keySpec == null) {
            throw new InvalidKeySpecException("Null parameter provided.");
        }
        Class specClass = null;
        try {
            specClass = Class.forName("javax.crypto.spec.PBEKeySpec");
        }
        catch (final ClassNotFoundException cnfe) {
            throw new InvalidKeySpecException("Cannot create KeySpec class not found!");
        }
        if (keySpec.isAssignableFrom(specClass)) {
            final byte[] keyData = key.getEncoded();
            final char[] rawKeyData = new char[keyData.length];
            for (int i = 0; i < keyData.length; ++i) {
                rawKeyData[i] = (char)keyData[i];
            }
            final Object[] initArgs = { rawKeyData };
            final Class[] constructorArgs = { (PBEKeyFactory.array$C != null) ? PBEKeyFactory.array$C : (PBEKeyFactory.array$C = class$("[C")) };
            KeySpec pks = null;
            try {
                final Constructor specConstructor = keySpec.getConstructor((Class[])constructorArgs);
                pks = specConstructor.newInstance(initArgs);
            }
            catch (final InstantiationException ex) {
                throw new InvalidKeySpecException("InvalidKeySpec.");
            }
            catch (final IllegalAccessException ex2) {
                throw new InvalidKeySpecException("IllegalAccess.");
            }
            catch (final IllegalArgumentException ex3) {
                throw new InvalidKeySpecException("Illegal constr. argument.");
            }
            catch (final InvocationTargetException ex4) {
                throw new InvalidKeySpecException("InvocationTargetException.");
            }
            catch (final NoSuchMethodException e) {
                throw new InvalidKeySpecException("Method not found.");
            }
            return pks;
        }
        throw new InvalidKeySpecException("Cannot assign to KeySpec.");
    }
    
    protected SecretKey engineTranslateKey(final SecretKey key) throws InvalidKeyException {
        if (key == null) {
            throw new InvalidKeyException();
        }
        if (key instanceof RawSecretKey && key.getAlgorithm() == "PBE") {
            return key;
        }
        try {
            return this.engineGenerateSecret(this.engineGetKeySpec(key, null));
        }
        catch (final InvalidKeySpecException ikse) {
            throw new InvalidKeyException("Translation not possible.");
        }
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public PBEKeyFactory() {
        this.pbeKeySpec = null;
    }
}
