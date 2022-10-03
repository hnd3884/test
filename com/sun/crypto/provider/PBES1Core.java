package com.sun.crypto.provider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.DigestException;
import java.security.ProviderException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Provider;
import javax.crypto.spec.PBEParameterSpec;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;

final class PBES1Core
{
    private CipherCore cipher;
    private MessageDigest md;
    private int blkSize;
    private String algo;
    private byte[] salt;
    private int iCount;
    
    PBES1Core(final String algo) throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.algo = null;
        this.salt = null;
        this.iCount = 10;
        this.algo = algo;
        if (this.algo.equals("DES")) {
            this.cipher = new CipherCore(new DESCrypt(), 8);
        }
        else {
            if (!this.algo.equals("DESede")) {
                throw new NoSuchAlgorithmException("No Cipher implementation for PBEWithMD5And" + this.algo);
            }
            this.cipher = new CipherCore(new DESedeCrypt(), 8);
        }
        this.cipher.setMode("CBC");
        this.cipher.setPadding("PKCS5Padding");
        this.md = MessageDigest.getInstance("MD5");
    }
    
    void setMode(final String mode) throws NoSuchAlgorithmException {
        this.cipher.setMode(mode);
    }
    
    void setPadding(final String padding) throws NoSuchPaddingException {
        this.cipher.setPadding(padding);
    }
    
    int getBlockSize() {
        return 8;
    }
    
    int getOutputSize(final int n) {
        return this.cipher.getOutputSize(n);
    }
    
    byte[] getIV() {
        return this.cipher.getIV();
    }
    
    AlgorithmParameters getParameters() {
        if (this.salt == null) {
            this.salt = new byte[8];
            SunJCE.getRandom().nextBytes(this.salt);
        }
        final PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(this.salt, this.iCount);
        AlgorithmParameters instance;
        try {
            instance = AlgorithmParameters.getInstance("PBEWithMD5And" + (this.algo.equalsIgnoreCase("DES") ? "DES" : "TripleDES"), SunJCE.getInstance());
            instance.init(pbeParameterSpec);
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new RuntimeException("SunJCE called, but not configured");
        }
        catch (final InvalidParameterSpecException ex2) {
            throw new RuntimeException("PBEParameterSpec not supported");
        }
        return instance;
    }
    
    void init(final int n, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if ((n == 2 || n == 4) && algorithmParameterSpec == null) {
            throw new InvalidAlgorithmParameterException("Parameters missing");
        }
        if (key == null) {
            throw new InvalidKeyException("Null key");
        }
        final byte[] encoded = key.getEncoded();
        byte[] deriveCipherKey;
        try {
            if (encoded == null || !key.getAlgorithm().regionMatches(true, 0, "PBE", 0, 3)) {
                throw new InvalidKeyException("Missing password");
            }
            if (algorithmParameterSpec == null) {
                secureRandom.nextBytes(this.salt = new byte[8]);
            }
            else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("Wrong parameter type: PBE expected");
                }
                this.salt = ((PBEParameterSpec)algorithmParameterSpec).getSalt();
                if (this.salt.length != 8) {
                    throw new InvalidAlgorithmParameterException("Salt must be 8 bytes long");
                }
                this.iCount = ((PBEParameterSpec)algorithmParameterSpec).getIterationCount();
                if (this.iCount <= 0) {
                    throw new InvalidAlgorithmParameterException("IterationCount must be a positive number");
                }
            }
            deriveCipherKey = this.deriveCipherKey(encoded);
        }
        finally {
            if (encoded != null) {
                Arrays.fill(encoded, (byte)0);
            }
        }
        this.cipher.init(n, new SecretKeySpec(deriveCipherKey, 0, deriveCipherKey.length - 8, this.algo), new IvParameterSpec(deriveCipherKey, deriveCipherKey.length - 8, 8), secureRandom);
    }
    
    private byte[] deriveCipherKey(final byte[] array) {
        byte[] array2 = null;
        if (this.algo.equals("DES")) {
            this.md.update(array);
            this.md.update(this.salt);
            final byte[] digest = this.md.digest();
            for (int i = 1; i < this.iCount; ++i) {
                this.md.update(digest);
                try {
                    this.md.digest(digest, 0, digest.length);
                }
                catch (final DigestException ex) {
                    throw new ProviderException("Internal error", ex);
                }
            }
            array2 = digest;
        }
        else if (this.algo.equals("DESede")) {
            int n;
            for (n = 0; n < 4 && this.salt[n] == this.salt[n + 4]; ++n) {}
            if (n == 4) {
                for (int j = 0; j < 2; ++j) {
                    final byte b = this.salt[j];
                    this.salt[j] = this.salt[3 - j];
                    this.salt[3 - j] = b;
                }
            }
            array2 = new byte[32];
            for (int k = 0; k < 2; ++k) {
                this.md.update(this.salt, k * (this.salt.length / 2), this.salt.length / 2);
                this.md.update(array);
                final byte[] digest2 = this.md.digest();
                for (int l = 1; l < this.iCount; ++l) {
                    this.md.update(digest2);
                    this.md.update(array);
                    try {
                        this.md.digest(digest2, 0, digest2.length);
                    }
                    catch (final DigestException ex2) {
                        throw new ProviderException("Internal error", ex2);
                    }
                }
                System.arraycopy(digest2, 0, array2, k * 16, digest2.length);
            }
        }
        this.md.reset();
        return array2;
    }
    
    void init(final int n, final Key key, final AlgorithmParameters algorithmParameters, final SecureRandom secureRandom) throws InvalidKeyException, InvalidAlgorithmParameterException {
        AlgorithmParameterSpec algorithmParameterSpec = null;
        if (algorithmParameters != null) {
            try {
                algorithmParameterSpec = algorithmParameters.getParameterSpec(PBEParameterSpec.class);
            }
            catch (final InvalidParameterSpecException ex) {
                throw new InvalidAlgorithmParameterException("Wrong parameter type: PBE expected");
            }
        }
        this.init(n, key, algorithmParameterSpec, secureRandom);
    }
    
    byte[] update(final byte[] array, final int n, final int n2) {
        return this.cipher.update(array, n, n2);
    }
    
    int update(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException {
        return this.cipher.update(array, n, n2, array2, n3);
    }
    
    byte[] doFinal(final byte[] array, final int n, final int n2) throws IllegalBlockSizeException, BadPaddingException {
        return this.cipher.doFinal(array, n, n2);
    }
    
    int doFinal(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.cipher.doFinal(array, n, n2, array2, n3);
    }
    
    byte[] wrap(final Key key) throws IllegalBlockSizeException, InvalidKeyException {
        byte[] doFinal = null;
        byte[] encoded = null;
        try {
            encoded = key.getEncoded();
            if (encoded == null || encoded.length == 0) {
                throw new InvalidKeyException("Cannot get an encoding of the key to be wrapped");
            }
            doFinal = this.doFinal(encoded, 0, encoded.length);
        }
        catch (final BadPaddingException ex) {}
        finally {
            if (encoded != null) {
                Arrays.fill(encoded, (byte)0);
            }
        }
        return doFinal;
    }
    
    Key unwrap(final byte[] array, final String s, final int n) throws InvalidKeyException, NoSuchAlgorithmException {
        byte[] doFinal;
        try {
            doFinal = this.doFinal(array, 0, array.length);
        }
        catch (final BadPaddingException ex) {
            throw new InvalidKeyException("The wrapped key is not padded correctly");
        }
        catch (final IllegalBlockSizeException ex2) {
            throw new InvalidKeyException("The wrapped key does not have the correct length");
        }
        return ConstructKeys.constructKey(doFinal, s, n);
    }
}
