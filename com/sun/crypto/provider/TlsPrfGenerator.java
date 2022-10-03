package com.sun.crypto.provider;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.ProviderException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidParameterException;
import java.security.SecureRandom;
import java.util.Arrays;
import sun.security.internal.spec.TlsPrfParameterSpec;
import javax.crypto.KeyGeneratorSpi;

abstract class TlsPrfGenerator extends KeyGeneratorSpi
{
    private static final byte[] B0;
    static final byte[] LABEL_MASTER_SECRET;
    static final byte[] LABEL_EXTENDED_MASTER_SECRET;
    static final byte[] LABEL_KEY_EXPANSION;
    static final byte[] LABEL_CLIENT_WRITE_KEY;
    static final byte[] LABEL_SERVER_WRITE_KEY;
    static final byte[] LABEL_IV_BLOCK;
    private static final byte[] HMAC_ipad64;
    private static final byte[] HMAC_ipad128;
    private static final byte[] HMAC_opad64;
    private static final byte[] HMAC_opad128;
    static final byte[][] SSL3_CONST;
    private static final String MSG = "TlsPrfGenerator must be initialized using a TlsPrfParameterSpec";
    private TlsPrfParameterSpec spec;
    
    static byte[] genPad(final byte b, final int n) {
        final byte[] array = new byte[n];
        Arrays.fill(array, b);
        return array;
    }
    
    static byte[] concat(final byte[] array, final byte[] array2) {
        final int length = array.length;
        final int length2 = array2.length;
        final byte[] array3 = new byte[length + length2];
        System.arraycopy(array, 0, array3, 0, length);
        System.arraycopy(array2, 0, array3, length, length2);
        return array3;
    }
    
    private static byte[][] genConst() {
        final int n = 10;
        final byte[][] array = new byte[n][];
        for (int i = 0; i < n; ++i) {
            final byte[] array2 = new byte[i + 1];
            Arrays.fill(array2, (byte)(65 + i));
            array[i] = array2;
        }
        return array;
    }
    
    public TlsPrfGenerator() {
    }
    
    @Override
    protected void engineInit(final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws InvalidAlgorithmParameterException {
        if (!(algorithmParameterSpec instanceof TlsPrfParameterSpec)) {
            throw new InvalidAlgorithmParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
        }
        this.spec = (TlsPrfParameterSpec)algorithmParameterSpec;
        final SecretKey secret = this.spec.getSecret();
        if (secret != null && !"RAW".equals(secret.getFormat())) {
            throw new InvalidAlgorithmParameterException("Key encoding format must be RAW");
        }
    }
    
    @Override
    protected void engineInit(final int n, final SecureRandom secureRandom) {
        throw new InvalidParameterException("TlsPrfGenerator must be initialized using a TlsPrfParameterSpec");
    }
    
    SecretKey engineGenerateKey0(final boolean b) {
        if (this.spec == null) {
            throw new IllegalStateException("TlsPrfGenerator must be initialized");
        }
        final SecretKey secret = this.spec.getSecret();
        final byte[] array = (byte[])((secret == null) ? null : secret.getEncoded());
        try {
            final byte[] bytes = this.spec.getLabel().getBytes("UTF8");
            final int outputLength = this.spec.getOutputLength();
            return new SecretKeySpec(b ? doTLS12PRF(array, bytes, this.spec.getSeed(), outputLength, this.spec.getPRFHashAlg(), this.spec.getPRFHashLength(), this.spec.getPRFBlockSize()) : doTLS10PRF(array, bytes, this.spec.getSeed(), outputLength), "TlsPrf");
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException("Could not generate PRF", ex);
        }
        catch (final UnsupportedEncodingException ex2) {
            throw new ProviderException("Could not generate PRF", ex2);
        }
    }
    
    static byte[] doTLS12PRF(final byte[] array, final byte[] array2, final byte[] array3, final int n, final String s, final int n2, final int n3) throws NoSuchAlgorithmException, DigestException {
        if (s == null) {
            throw new NoSuchAlgorithmException("Unspecified PRF algorithm");
        }
        return doTLS12PRF(array, array2, array3, n, MessageDigest.getInstance(s), n2, n3);
    }
    
    static byte[] doTLS12PRF(byte[] array, final byte[] array2, final byte[] array3, final int n, final MessageDigest messageDigest, final int n2, final int n3) throws DigestException {
        if (array == null) {
            array = TlsPrfGenerator.B0;
        }
        if (array.length > n3) {
            array = messageDigest.digest(array);
        }
        final byte[] array4 = new byte[n];
        byte[] array5 = null;
        byte[] array6 = null;
        switch (n3) {
            case 64: {
                array5 = TlsPrfGenerator.HMAC_ipad64.clone();
                array6 = TlsPrfGenerator.HMAC_opad64.clone();
                break;
            }
            case 128: {
                array5 = TlsPrfGenerator.HMAC_ipad128.clone();
                array6 = TlsPrfGenerator.HMAC_opad128.clone();
                break;
            }
            default: {
                throw new DigestException("Unexpected block size.");
            }
        }
        expand(messageDigest, n2, array, 0, array.length, array2, array3, array4, array5, array6);
        return array4;
    }
    
    static byte[] doTLS10PRF(final byte[] array, final byte[] array2, final byte[] array3, final int n) throws NoSuchAlgorithmException, DigestException {
        return doTLS10PRF(array, array2, array3, n, MessageDigest.getInstance("MD5"), MessageDigest.getInstance("SHA1"));
    }
    
    static byte[] doTLS10PRF(byte[] b0, final byte[] array, final byte[] array2, final int n, final MessageDigest messageDigest, final MessageDigest messageDigest2) throws DigestException {
        if (b0 == null) {
            b0 = TlsPrfGenerator.B0;
        }
        int n2 = b0.length >> 1;
        final int n3 = n2 + (b0.length & 0x1);
        byte[] array3 = b0;
        int n4 = n3;
        final byte[] array4 = new byte[n];
        if (n3 > 64) {
            messageDigest.update(b0, 0, n3);
            array3 = messageDigest.digest();
            n4 = array3.length;
        }
        expand(messageDigest, 16, array3, 0, n4, array, array2, array4, TlsPrfGenerator.HMAC_ipad64.clone(), TlsPrfGenerator.HMAC_opad64.clone());
        if (n3 > 64) {
            messageDigest2.update(b0, n2, n3);
            array3 = messageDigest2.digest();
            n4 = array3.length;
            n2 = 0;
        }
        expand(messageDigest2, 20, array3, n2, n4, array, array2, array4, TlsPrfGenerator.HMAC_ipad64.clone(), TlsPrfGenerator.HMAC_opad64.clone());
        return array4;
    }
    
    private static void expand(final MessageDigest messageDigest, final int n, final byte[] array, final int n2, final int n3, final byte[] array2, final byte[] array3, final byte[] array4, final byte[] array5, final byte[] array6) throws DigestException {
        for (int i = 0; i < n3; ++i) {
            final int n4 = i;
            array5[n4] ^= array[i + n2];
            final int n5 = i;
            array6[n5] ^= array[i + n2];
        }
        final byte[] array7 = new byte[n];
        byte[] array8 = null;
        int j = array4.length;
        int n6 = 0;
        while (j > 0) {
            messageDigest.update(array5);
            if (array8 == null) {
                messageDigest.update(array2);
                messageDigest.update(array3);
            }
            else {
                messageDigest.update(array8);
            }
            messageDigest.digest(array7, 0, n);
            messageDigest.update(array6);
            messageDigest.update(array7);
            if (array8 == null) {
                array8 = new byte[n];
            }
            messageDigest.digest(array8, 0, n);
            messageDigest.update(array5);
            messageDigest.update(array8);
            messageDigest.update(array2);
            messageDigest.update(array3);
            messageDigest.digest(array7, 0, n);
            messageDigest.update(array6);
            messageDigest.update(array7);
            messageDigest.digest(array7, 0, n);
            final int min = Math.min(n, j);
            for (int k = 0; k < min; ++k) {
                final int n7 = n6++;
                array4[n7] ^= array7[k];
            }
            j -= min;
        }
    }
    
    static {
        B0 = new byte[0];
        LABEL_MASTER_SECRET = new byte[] { 109, 97, 115, 116, 101, 114, 32, 115, 101, 99, 114, 101, 116 };
        LABEL_EXTENDED_MASTER_SECRET = new byte[] { 101, 120, 116, 101, 110, 100, 101, 100, 32, 109, 97, 115, 116, 101, 114, 32, 115, 101, 99, 114, 101, 116 };
        LABEL_KEY_EXPANSION = new byte[] { 107, 101, 121, 32, 101, 120, 112, 97, 110, 115, 105, 111, 110 };
        LABEL_CLIENT_WRITE_KEY = new byte[] { 99, 108, 105, 101, 110, 116, 32, 119, 114, 105, 116, 101, 32, 107, 101, 121 };
        LABEL_SERVER_WRITE_KEY = new byte[] { 115, 101, 114, 118, 101, 114, 32, 119, 114, 105, 116, 101, 32, 107, 101, 121 };
        LABEL_IV_BLOCK = new byte[] { 73, 86, 32, 98, 108, 111, 99, 107 };
        HMAC_ipad64 = genPad((byte)54, 64);
        HMAC_ipad128 = genPad((byte)54, 128);
        HMAC_opad64 = genPad((byte)92, 64);
        HMAC_opad128 = genPad((byte)92, 128);
        SSL3_CONST = genConst();
    }
    
    public static class V12 extends TlsPrfGenerator
    {
        @Override
        protected SecretKey engineGenerateKey() {
            return this.engineGenerateKey0(true);
        }
    }
    
    public static class V10 extends TlsPrfGenerator
    {
        @Override
        protected SecretKey engineGenerateKey() {
            return this.engineGenerateKey0(false);
        }
    }
}
