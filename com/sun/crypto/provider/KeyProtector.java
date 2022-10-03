package com.sun.crypto.provider;

import sun.security.util.SecurityProperties;
import java.security.spec.InvalidParameterSpecException;
import javax.crypto.Cipher;
import java.io.Serializable;
import javax.crypto.CipherSpi;
import javax.crypto.SealedObject;
import java.security.MessageDigest;
import javax.security.auth.Destroyable;
import javax.security.auth.DestroyFailedException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.KeyFactory;
import java.io.IOException;
import java.security.UnrecoverableKeyException;
import java.security.Key;
import sun.security.x509.AlgorithmId;
import sun.security.util.ObjectIdentifier;
import java.security.Provider;
import java.security.AlgorithmParameters;
import java.util.Arrays;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.PrivateKey;

final class KeyProtector
{
    private static final String PBE_WITH_MD5_AND_DES3_CBC_OID = "1.3.6.1.4.1.42.2.19.1";
    private static final String KEY_PROTECTOR_OID = "1.3.6.1.4.1.42.2.17.1.1";
    private static final int MAX_ITERATION_COUNT = 5000000;
    private static final int MIN_ITERATION_COUNT = 10000;
    private static final int DEFAULT_ITERATION_COUNT = 200000;
    private static final int SALT_LEN = 20;
    private static final int DIGEST_LEN = 20;
    private static final int ITERATION_COUNT;
    private char[] password;
    
    KeyProtector(final char[] password) {
        if (password == null) {
            throw new IllegalArgumentException("password can't be null");
        }
        this.password = password;
    }
    
    byte[] protect(final PrivateKey privateKey) throws Exception {
        final byte[] array = new byte[8];
        SunJCE.getRandom().nextBytes(array);
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(array, KeyProtector.ITERATION_COUNT);
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(this.password);
        Key key = null;
        PBEWithMD5AndTripleDESCipher pbeWithMD5AndTripleDESCipher;
        try {
            key = new PBEKey(pbeKeySpec, "PBEWithMD5AndTripleDES");
            pbeWithMD5AndTripleDESCipher = new PBEWithMD5AndTripleDESCipher();
            pbeWithMD5AndTripleDESCipher.engineInit(1, key, pbeParameterSpec, null);
        }
        finally {
            pbeKeySpec.clearPassword();
            if (key != null) {
                ((Destroyable)key).destroy();
            }
        }
        final byte[] encoded = privateKey.getEncoded();
        final byte[] engineDoFinal = pbeWithMD5AndTripleDESCipher.engineDoFinal(encoded, 0, encoded.length);
        Arrays.fill(encoded, (byte)0);
        final AlgorithmParameters instance = AlgorithmParameters.getInstance("PBE", SunJCE.getInstance());
        instance.init(pbeParameterSpec);
        return new EncryptedPrivateKeyInfo(new AlgorithmId(new ObjectIdentifier("1.3.6.1.4.1.42.2.19.1"), instance), engineDoFinal).getEncoded();
    }
    
    Key recover(final EncryptedPrivateKeyInfo encryptedPrivateKeyInfo) throws UnrecoverableKeyException, NoSuchAlgorithmException {
        byte[] array = null;
        Destroyable destroyable = null;
        try {
            final String string = encryptedPrivateKeyInfo.getAlgorithm().getOID().toString();
            if (!string.equals("1.3.6.1.4.1.42.2.19.1") && !string.equals("1.3.6.1.4.1.42.2.17.1.1")) {
                throw new UnrecoverableKeyException("Unsupported encryption algorithm");
            }
            if (string.equals("1.3.6.1.4.1.42.2.17.1.1")) {
                array = this.recover(encryptedPrivateKeyInfo.getEncryptedData());
            }
            else {
                final byte[] encodedParams = encryptedPrivateKeyInfo.getAlgorithm().getEncodedParams();
                final AlgorithmParameters instance = AlgorithmParameters.getInstance("PBE");
                instance.init(encodedParams);
                final PBEParameterSpec pbeParameterSpec = instance.getParameterSpec(PBEParameterSpec.class);
                if (pbeParameterSpec.getIterationCount() > 5000000) {
                    throw new IOException("PBE iteration count too large");
                }
                final PBEKeySpec pbeKeySpec = new PBEKeySpec(this.password);
                destroyable = new PBEKey(pbeKeySpec, "PBEWithMD5AndTripleDES");
                pbeKeySpec.clearPassword();
                final PBEWithMD5AndTripleDESCipher pbeWithMD5AndTripleDESCipher = new PBEWithMD5AndTripleDESCipher();
                pbeWithMD5AndTripleDESCipher.engineInit(2, (Key)destroyable, pbeParameterSpec, null);
                array = pbeWithMD5AndTripleDESCipher.engineDoFinal(encryptedPrivateKeyInfo.getEncryptedData(), 0, encryptedPrivateKeyInfo.getEncryptedData().length);
            }
            return KeyFactory.getInstance(new AlgorithmId(new PrivateKeyInfo(array).getAlgorithm().getOID()).getName()).generatePrivate(new PKCS8EncodedKeySpec(array));
        }
        catch (final NoSuchAlgorithmException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw new UnrecoverableKeyException(ex2.getMessage());
        }
        catch (final GeneralSecurityException ex3) {
            throw new UnrecoverableKeyException(ex3.getMessage());
        }
        finally {
            if (array != null) {
                Arrays.fill(array, (byte)0);
            }
            if (destroyable != null) {
                try {
                    destroyable.destroy();
                }
                catch (final DestroyFailedException ex4) {}
            }
        }
    }
    
    private byte[] recover(final byte[] array) throws UnrecoverableKeyException, NoSuchAlgorithmException {
        final MessageDigest instance = MessageDigest.getInstance("SHA");
        final byte[] array2 = new byte[20];
        System.arraycopy(array, 0, array2, 0, 20);
        final int n = array.length - 20 - 20;
        int n2 = n / 20;
        if (n % 20 != 0) {
            ++n2;
        }
        final byte[] array3 = new byte[n];
        System.arraycopy(array, 20, array3, 0, n);
        final byte[] array4 = new byte[array3.length];
        final byte[] array5 = new byte[this.password.length * 2];
        int i = 0;
        int n3 = 0;
        while (i < this.password.length) {
            array5[n3++] = (byte)(this.password[i] >> 8);
            array5[n3++] = (byte)this.password[i];
            ++i;
        }
        int j = 0;
        int n4 = 0;
        byte[] digest = array2;
        while (j < n2) {
            instance.update(array5);
            instance.update(digest);
            digest = instance.digest();
            instance.reset();
            if (j < n2 - 1) {
                System.arraycopy(digest, 0, array4, n4, digest.length);
            }
            else {
                System.arraycopy(digest, 0, array4, n4, array4.length - n4);
            }
            ++j;
            n4 += 20;
        }
        final byte[] array6 = new byte[array3.length];
        for (int k = 0; k < array6.length; ++k) {
            array6[k] = (byte)(array3[k] ^ array4[k]);
        }
        instance.update(array5);
        Arrays.fill(array5, (byte)0);
        instance.update(array6);
        final byte[] digest2 = instance.digest();
        instance.reset();
        for (int l = 0; l < digest2.length; ++l) {
            if (digest2[l] != array[20 + n + l]) {
                throw new UnrecoverableKeyException("Cannot recover key");
            }
        }
        return array6;
    }
    
    SealedObject seal(final Key key) throws Exception {
        final byte[] array = new byte[8];
        SunJCE.getRandom().nextBytes(array);
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(array, KeyProtector.ITERATION_COUNT);
        final PBEKeySpec pbeKeySpec = new PBEKeySpec(this.password);
        Destroyable destroyable = null;
        CipherForKeyProtector cipherForKeyProtector;
        try {
            destroyable = new PBEKey(pbeKeySpec, "PBEWithMD5AndTripleDES");
            pbeKeySpec.clearPassword();
            cipherForKeyProtector = new CipherForKeyProtector(new PBEWithMD5AndTripleDESCipher(), SunJCE.getInstance(), "PBEWithMD5AndTripleDES");
            cipherForKeyProtector.init(1, (Key)destroyable, pbeParameterSpec);
        }
        finally {
            if (destroyable != null) {
                destroyable.destroy();
            }
        }
        return new SealedObjectForKeyProtector(key, cipherForKeyProtector);
    }
    
    Key unseal(final SealedObject sealedObject, final int n) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        Key key = null;
        try {
            final PBEKeySpec pbeKeySpec = new PBEKeySpec(this.password);
            key = new PBEKey(pbeKeySpec, "PBEWithMD5AndTripleDES");
            pbeKeySpec.clearPassword();
            SealedObjectForKeyProtector sealedObjectForKeyProtector;
            if (!(sealedObject instanceof SealedObjectForKeyProtector)) {
                sealedObjectForKeyProtector = new SealedObjectForKeyProtector(sealedObject);
            }
            else {
                sealedObjectForKeyProtector = (SealedObjectForKeyProtector)sealedObject;
            }
            final AlgorithmParameters parameters = sealedObjectForKeyProtector.getParameters();
            if (parameters == null) {
                throw new UnrecoverableKeyException("Cannot get algorithm parameters");
            }
            PBEParameterSpec pbeParameterSpec;
            try {
                pbeParameterSpec = parameters.getParameterSpec(PBEParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ex) {
                throw new IOException("Invalid PBE algorithm parameters");
            }
            if (pbeParameterSpec.getIterationCount() > 5000000) {
                throw new IOException("PBE iteration count too large");
            }
            final CipherForKeyProtector cipherForKeyProtector = new CipherForKeyProtector(new PBEWithMD5AndTripleDESCipher(), SunJCE.getInstance(), "PBEWithMD5AndTripleDES");
            cipherForKeyProtector.init(2, key, parameters);
            return sealedObjectForKeyProtector.getKey(cipherForKeyProtector, n);
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw ex2;
        }
        catch (final IOException ex3) {
            throw new UnrecoverableKeyException(ex3.getMessage());
        }
        catch (final ClassNotFoundException ex4) {
            throw new UnrecoverableKeyException(ex4.getMessage());
        }
        catch (final GeneralSecurityException ex5) {
            throw new UnrecoverableKeyException(ex5.getMessage());
        }
        finally {
            if (key != null) {
                try {
                    ((Destroyable)key).destroy();
                }
                catch (final DestroyFailedException ex6) {}
            }
        }
    }
    
    static {
        int int1 = 200000;
        final String privilegedGetOverridable = SecurityProperties.privilegedGetOverridable("jdk.jceks.iterationCount");
        if (privilegedGetOverridable != null && !privilegedGetOverridable.isEmpty()) {
            try {
                int1 = Integer.parseInt(privilegedGetOverridable);
                if (int1 < 10000 || int1 > 5000000) {
                    int1 = 200000;
                }
            }
            catch (final NumberFormatException ex) {}
        }
        ITERATION_COUNT = int1;
    }
}
