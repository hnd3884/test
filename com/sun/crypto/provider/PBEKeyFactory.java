package com.sun.crypto.provider;

import java.security.InvalidKeyException;
import java.util.Arrays;
import java.util.Locale;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.KeySpec;
import java.util.HashSet;
import javax.crypto.SecretKeyFactorySpi;

abstract class PBEKeyFactory extends SecretKeyFactorySpi
{
    private String type;
    private static HashSet<String> validTypes;
    
    private PBEKeyFactory(final String type) {
        this.type = type;
    }
    
    @Override
    protected SecretKey engineGenerateSecret(final KeySpec keySpec) throws InvalidKeySpecException {
        if (!(keySpec instanceof PBEKeySpec)) {
            throw new InvalidKeySpecException("Invalid key spec");
        }
        return new PBEKey((PBEKeySpec)keySpec, this.type);
    }
    
    @Override
    protected KeySpec engineGetKeySpec(final SecretKey secretKey, final Class<?> clazz) throws InvalidKeySpecException {
        if (!(secretKey instanceof SecretKey) || !PBEKeyFactory.validTypes.contains(secretKey.getAlgorithm().toUpperCase(Locale.ENGLISH)) || !secretKey.getFormat().equalsIgnoreCase("RAW")) {
            throw new InvalidKeySpecException("Invalid key format/algorithm");
        }
        if (clazz != null && PBEKeySpec.class.isAssignableFrom(clazz)) {
            final byte[] encoded = secretKey.getEncoded();
            final char[] array = new char[encoded.length];
            for (int i = 0; i < array.length; ++i) {
                array[i] = (char)(encoded[i] & 0x7F);
            }
            final PBEKeySpec pbeKeySpec = new PBEKeySpec(array);
            Arrays.fill(array, ' ');
            Arrays.fill(encoded, (byte)0);
            return pbeKeySpec;
        }
        throw new InvalidKeySpecException("Invalid key spec");
    }
    
    @Override
    protected SecretKey engineTranslateKey(final SecretKey secretKey) throws InvalidKeyException {
        try {
            if (secretKey == null || !PBEKeyFactory.validTypes.contains(secretKey.getAlgorithm().toUpperCase(Locale.ENGLISH)) || !secretKey.getFormat().equalsIgnoreCase("RAW")) {
                throw new InvalidKeyException("Invalid key format/algorithm");
            }
            if (secretKey instanceof PBEKey) {
                return secretKey;
            }
            return this.engineGenerateSecret(this.engineGetKeySpec(secretKey, PBEKeySpec.class));
        }
        catch (final InvalidKeySpecException ex) {
            throw new InvalidKeyException("Cannot translate key: " + ex.getMessage());
        }
    }
    
    static {
        (PBEKeyFactory.validTypes = new HashSet<String>(17)).add("PBEWithMD5AndDES".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithSHA1AndDESede".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithSHA1AndRC2_40".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithSHA1AndRC2_128".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithSHA1AndRC4_40".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithSHA1AndRC4_128".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithMD5AndTripleDES".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA1AndAES_128".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA224AndAES_128".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA256AndAES_128".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA384AndAES_128".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA512AndAES_128".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA1AndAES_256".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA224AndAES_256".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA256AndAES_256".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA384AndAES_256".toUpperCase(Locale.ENGLISH));
        PBEKeyFactory.validTypes.add("PBEWithHmacSHA512AndAES_256".toUpperCase(Locale.ENGLISH));
    }
    
    public static final class PBEWithMD5AndDES extends PBEKeyFactory
    {
        public PBEWithMD5AndDES() {
            super("PBEWithMD5AndDES", null);
        }
    }
    
    public static final class PBEWithSHA1AndDESede extends PBEKeyFactory
    {
        public PBEWithSHA1AndDESede() {
            super("PBEWithSHA1AndDESede", null);
        }
    }
    
    public static final class PBEWithSHA1AndRC2_40 extends PBEKeyFactory
    {
        public PBEWithSHA1AndRC2_40() {
            super("PBEWithSHA1AndRC2_40", null);
        }
    }
    
    public static final class PBEWithSHA1AndRC2_128 extends PBEKeyFactory
    {
        public PBEWithSHA1AndRC2_128() {
            super("PBEWithSHA1AndRC2_128", null);
        }
    }
    
    public static final class PBEWithSHA1AndRC4_40 extends PBEKeyFactory
    {
        public PBEWithSHA1AndRC4_40() {
            super("PBEWithSHA1AndRC4_40", null);
        }
    }
    
    public static final class PBEWithSHA1AndRC4_128 extends PBEKeyFactory
    {
        public PBEWithSHA1AndRC4_128() {
            super("PBEWithSHA1AndRC4_128", null);
        }
    }
    
    public static final class PBEWithMD5AndTripleDES extends PBEKeyFactory
    {
        public PBEWithMD5AndTripleDES() {
            super("PBEWithMD5AndTripleDES", null);
        }
    }
    
    public static final class PBEWithHmacSHA1AndAES_128 extends PBEKeyFactory
    {
        public PBEWithHmacSHA1AndAES_128() {
            super("PBEWithHmacSHA1AndAES_128", null);
        }
    }
    
    public static final class PBEWithHmacSHA224AndAES_128 extends PBEKeyFactory
    {
        public PBEWithHmacSHA224AndAES_128() {
            super("PBEWithHmacSHA224AndAES_128", null);
        }
    }
    
    public static final class PBEWithHmacSHA256AndAES_128 extends PBEKeyFactory
    {
        public PBEWithHmacSHA256AndAES_128() {
            super("PBEWithHmacSHA256AndAES_128", null);
        }
    }
    
    public static final class PBEWithHmacSHA384AndAES_128 extends PBEKeyFactory
    {
        public PBEWithHmacSHA384AndAES_128() {
            super("PBEWithHmacSHA384AndAES_128", null);
        }
    }
    
    public static final class PBEWithHmacSHA512AndAES_128 extends PBEKeyFactory
    {
        public PBEWithHmacSHA512AndAES_128() {
            super("PBEWithHmacSHA512AndAES_128", null);
        }
    }
    
    public static final class PBEWithHmacSHA1AndAES_256 extends PBEKeyFactory
    {
        public PBEWithHmacSHA1AndAES_256() {
            super("PBEWithHmacSHA1AndAES_256", null);
        }
    }
    
    public static final class PBEWithHmacSHA224AndAES_256 extends PBEKeyFactory
    {
        public PBEWithHmacSHA224AndAES_256() {
            super("PBEWithHmacSHA224AndAES_256", null);
        }
    }
    
    public static final class PBEWithHmacSHA256AndAES_256 extends PBEKeyFactory
    {
        public PBEWithHmacSHA256AndAES_256() {
            super("PBEWithHmacSHA256AndAES_256", null);
        }
    }
    
    public static final class PBEWithHmacSHA384AndAES_256 extends PBEKeyFactory
    {
        public PBEWithHmacSHA384AndAES_256() {
            super("PBEWithHmacSHA384AndAES_256", null);
        }
    }
    
    public static final class PBEWithHmacSHA512AndAES_256 extends PBEKeyFactory
    {
        public PBEWithHmacSHA512AndAES_256() {
            super("PBEWithHmacSHA512AndAES_256", null);
        }
    }
}
