package com.sun.crypto.provider;

import javax.crypto.spec.SecretKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.security.InvalidKeyException;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.PBEKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.ProviderException;
import java.security.NoSuchAlgorithmException;

abstract class PBMAC1Core extends HmacCore
{
    private final String kdfAlgo;
    private final String hashAlgo;
    private final int blockLength;
    
    PBMAC1Core(final String kdfAlgo, final String hashAlgo, final int blockLength) throws NoSuchAlgorithmException {
        super(hashAlgo, blockLength);
        this.kdfAlgo = kdfAlgo;
        this.hashAlgo = hashAlgo;
        this.blockLength = blockLength;
    }
    
    private static PBKDF2Core getKDFImpl(final String s) {
        PBKDF2Core pbkdf2Core = null;
        switch (s) {
            case "HmacSHA1": {
                pbkdf2Core = new PBKDF2Core.HmacSHA1();
                break;
            }
            case "HmacSHA224": {
                pbkdf2Core = new PBKDF2Core.HmacSHA224();
                break;
            }
            case "HmacSHA256": {
                pbkdf2Core = new PBKDF2Core.HmacSHA256();
                break;
            }
            case "HmacSHA384": {
                pbkdf2Core = new PBKDF2Core.HmacSHA384();
                break;
            }
            case "HmacSHA512": {
                pbkdf2Core = new PBKDF2Core.HmacSHA512();
                break;
            }
            default: {
                throw new ProviderException("No MAC implementation for " + s);
            }
        }
        return pbkdf2Core;
    }
    
    @Override
    protected void engineInit(final Key key, final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidKeyException, InvalidAlgorithmParameterException {
        byte[] array = null;
        int n = 0;
        char[] password;
        if (key instanceof PBEKey) {
            final Object encoded = key;
            password = ((PBEKey)encoded).getPassword();
            array = ((PBEKey)encoded).getSalt();
            n = ((PBEKey)encoded).getIterationCount();
        }
        else {
            if (!(key instanceof SecretKey)) {
                throw new InvalidKeyException("SecretKey of PBE type required");
            }
            final Object encoded;
            if (!key.getAlgorithm().regionMatches(true, 0, "PBE", 0, 3) || (encoded = key.getEncoded()) == null) {
                throw new InvalidKeyException("Missing password");
            }
            password = new char[((PBEKey)encoded).length];
            for (int i = 0; i < password.length; ++i) {
                password[i] = (char)(encoded[i] & 0x7F);
            }
            Arrays.fill((byte[])encoded, (byte)0);
        }
        Object encoded;
        try {
            if (algorithmParameterSpec == null) {
                if (array == null || n == 0) {
                    throw new InvalidAlgorithmParameterException("PBEParameterSpec required for salt and iteration count");
                }
            }
            else {
                if (!(algorithmParameterSpec instanceof PBEParameterSpec)) {
                    throw new InvalidAlgorithmParameterException("PBEParameterSpec type required");
                }
                final PBEParameterSpec pbeParameterSpec = (PBEParameterSpec)algorithmParameterSpec;
                if (array != null) {
                    if (!Arrays.equals(array, pbeParameterSpec.getSalt())) {
                        throw new InvalidAlgorithmParameterException("Inconsistent value of salt between key and params");
                    }
                }
                else {
                    array = pbeParameterSpec.getSalt();
                }
                if (n != 0) {
                    if (n != pbeParameterSpec.getIterationCount()) {
                        throw new InvalidAlgorithmParameterException("Different iteration count between key and params");
                    }
                }
                else {
                    n = pbeParameterSpec.getIterationCount();
                }
            }
            if (array.length < 8) {
                throw new InvalidAlgorithmParameterException("Salt must be at least 8 bytes long");
            }
            if (n <= 0) {
                throw new InvalidAlgorithmParameterException("IterationCount must be a positive number");
            }
            encoded = new PBEKeySpec(password, array, n, this.blockLength);
        }
        finally {
            Arrays.fill(password, '\0');
        }
        final PBKDF2Core kdfImpl = getKDFImpl(this.kdfAlgo);
        SecretKey engineGenerateSecret;
        try {
            engineGenerateSecret = kdfImpl.engineGenerateSecret((KeySpec)encoded);
        }
        catch (final InvalidKeySpecException ex) {
            final InvalidKeyException ex2 = new InvalidKeyException("Cannot construct PBE key");
            ex2.initCause(ex);
            throw ex2;
        }
        super.engineInit(new SecretKeySpec(engineGenerateSecret.getEncoded(), this.kdfAlgo), null);
    }
    
    public static final class HmacSHA1 extends PBMAC1Core
    {
        public HmacSHA1() throws NoSuchAlgorithmException {
            super("HmacSHA1", "SHA1", 64);
        }
    }
    
    public static final class HmacSHA224 extends PBMAC1Core
    {
        public HmacSHA224() throws NoSuchAlgorithmException {
            super("HmacSHA224", "SHA-224", 64);
        }
    }
    
    public static final class HmacSHA256 extends PBMAC1Core
    {
        public HmacSHA256() throws NoSuchAlgorithmException {
            super("HmacSHA256", "SHA-256", 64);
        }
    }
    
    public static final class HmacSHA384 extends PBMAC1Core
    {
        public HmacSHA384() throws NoSuchAlgorithmException {
            super("HmacSHA384", "SHA-384", 128);
        }
    }
    
    public static final class HmacSHA512 extends PBMAC1Core
    {
        public HmacSHA512() throws NoSuchAlgorithmException {
            super("HmacSHA512", "SHA-512", 128);
        }
    }
}
