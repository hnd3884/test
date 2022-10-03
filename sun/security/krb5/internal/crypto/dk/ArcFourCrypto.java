package sun.security.krb5.internal.crypto.dk;

import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import java.security.NoSuchAlgorithmException;
import sun.security.krb5.internal.crypto.KeyUsage;
import javax.crypto.Mac;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.security.MessageDigest;
import java.util.Arrays;
import sun.security.provider.MD4;
import java.security.GeneralSecurityException;

public class ArcFourCrypto extends DkCrypto
{
    private static final boolean debug = false;
    private static final int confounderSize = 8;
    private static final byte[] ZERO_IV;
    private static final int hashSize = 16;
    private final int keyLength;
    
    public ArcFourCrypto(final int keyLength) {
        this.keyLength = keyLength;
    }
    
    @Override
    protected int getKeySeedLength() {
        return this.keyLength;
    }
    
    @Override
    protected byte[] randomToKey(final byte[] array) {
        return array;
    }
    
    public byte[] stringToKey(final char[] array) throws GeneralSecurityException {
        return this.stringToKey(array, null);
    }
    
    private byte[] stringToKey(final char[] array, final byte[] array2) throws GeneralSecurityException {
        if (array2 != null && array2.length > 0) {
            throw new RuntimeException("Invalid parameter to stringToKey");
        }
        byte[] charToUtf16 = null;
        byte[] digest = null;
        try {
            charToUtf16 = DkCrypto.charToUtf16(array);
            final MessageDigest instance = MD4.getInstance();
            instance.update(charToUtf16);
            digest = instance.digest();
        }
        catch (final Exception ex) {
            return null;
        }
        finally {
            if (charToUtf16 != null) {
                Arrays.fill(charToUtf16, (byte)0);
            }
        }
        return digest;
    }
    
    @Override
    protected Cipher getCipher(final byte[] array, byte[] zero_IV, final int n) throws GeneralSecurityException {
        if (zero_IV == null) {
            zero_IV = ArcFourCrypto.ZERO_IV;
        }
        final SecretKeySpec secretKeySpec = new SecretKeySpec(array, "ARCFOUR");
        final Cipher instance = Cipher.getInstance("ARCFOUR");
        instance.init(n, secretKeySpec, new IvParameterSpec(zero_IV, 0, zero_IV.length));
        return instance;
    }
    
    @Override
    public int getChecksumLength() {
        return 16;
    }
    
    @Override
    protected byte[] getHmac(final byte[] array, final byte[] array2) throws GeneralSecurityException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(array, "HmacMD5");
        final Mac instance = Mac.getInstance("HmacMD5");
        instance.init(secretKeySpec);
        return instance.doFinal(array2);
    }
    
    @Override
    public byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        byte[] hmac;
        try {
            final byte[] bytes = "signaturekey".getBytes();
            final byte[] array3 = new byte[bytes.length + 1];
            System.arraycopy(bytes, 0, array3, 0, bytes.length);
            hmac = this.getHmac(array, array3);
        }
        catch (final Exception ex) {
            final GeneralSecurityException ex2 = new GeneralSecurityException("Calculate Checkum Failed!");
            ex2.initCause(ex);
            throw ex2;
        }
        final byte[] salt = this.getSalt(n);
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("MD5");
        }
        catch (final NoSuchAlgorithmException ex3) {
            final GeneralSecurityException ex4 = new GeneralSecurityException("Calculate Checkum Failed!");
            ex4.initCause(ex3);
            throw ex4;
        }
        instance.update(salt);
        instance.update(array2, n2, n3);
        final byte[] hmac2 = this.getHmac(hmac, instance.digest());
        if (hmac2.length == this.getChecksumLength()) {
            return hmac2;
        }
        if (hmac2.length > this.getChecksumLength()) {
            final byte[] array4 = new byte[this.getChecksumLength()];
            System.arraycopy(hmac2, 0, array4, 0, array4.length);
            return array4;
        }
        throw new GeneralSecurityException("checksum size too short: " + hmac2.length + "; expecting : " + this.getChecksumLength());
    }
    
    public byte[] encryptSeq(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] hmac = this.getHmac(this.getHmac(array, new byte[4]), array2);
        final Cipher instance = Cipher.getInstance("ARCFOUR");
        instance.init(1, new SecretKeySpec(hmac, "ARCFOUR"));
        return instance.doFinal(array3, n2, n3);
    }
    
    public byte[] decryptSeq(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] hmac = this.getHmac(this.getHmac(array, new byte[4]), array2);
        final Cipher instance = Cipher.getInstance("ARCFOUR");
        instance.init(2, new SecretKeySpec(hmac, "ARCFOUR"));
        return instance.doFinal(array3, n2, n3);
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final byte[] array4, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] bytes = Confounder.bytes(8);
        final byte[] array5 = new byte[this.roundup(bytes.length + n3, 1)];
        System.arraycopy(bytes, 0, array5, 0, bytes.length);
        System.arraycopy(array4, n2, array5, bytes.length, n3);
        final byte[] array6 = new byte[array.length];
        System.arraycopy(array, 0, array6, 0, array.length);
        final byte[] hmac = this.getHmac(array6, this.getSalt(n));
        final byte[] hmac2 = this.getHmac(hmac, array5);
        final byte[] hmac3 = this.getHmac(hmac, hmac2);
        final Cipher instance = Cipher.getInstance("ARCFOUR");
        instance.init(1, new SecretKeySpec(hmac3, "ARCFOUR"));
        final byte[] doFinal = instance.doFinal(array5, 0, array5.length);
        final byte[] array7 = new byte[16 + doFinal.length];
        System.arraycopy(hmac2, 0, array7, 0, 16);
        System.arraycopy(doFinal, 0, array7, 16, doFinal.length);
        return array7;
    }
    
    @Override
    public byte[] encryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] array4 = new byte[array.length];
        for (int i = 0; i <= 15; ++i) {
            array4[i] = (byte)(array[i] ^ 0xF0);
        }
        final byte[] hmac = this.getHmac(this.getHmac(array4, new byte[4]), array2);
        final Cipher instance = Cipher.getInstance("ARCFOUR");
        instance.init(1, new SecretKeySpec(hmac, "ARCFOUR"));
        return instance.doFinal(array3, n2, n3);
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] array4 = new byte[array.length];
        System.arraycopy(array, 0, array4, 0, array.length);
        final byte[] hmac = this.getHmac(array4, this.getSalt(n));
        final byte[] array5 = new byte[16];
        System.arraycopy(array3, n2, array5, 0, 16);
        final byte[] hmac2 = this.getHmac(hmac, array5);
        final Cipher instance = Cipher.getInstance("ARCFOUR");
        instance.init(2, new SecretKeySpec(hmac2, "ARCFOUR"));
        final byte[] doFinal = instance.doFinal(array3, n2 + 16, n3 - 16);
        final byte[] hmac3 = this.getHmac(hmac, doFinal);
        boolean b = false;
        if (hmac3.length >= 16) {
            for (int i = 0; i < 16; ++i) {
                if (hmac3[i] != array3[i]) {
                    b = true;
                    break;
                }
            }
        }
        if (b) {
            throw new GeneralSecurityException("Checksum failed");
        }
        final byte[] array6 = new byte[doFinal.length - 8];
        System.arraycopy(doFinal, 8, array6, 0, array6.length);
        return array6;
    }
    
    public byte[] decryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3, final byte[] array4) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] array5 = new byte[array.length];
        for (int i = 0; i <= 15; ++i) {
            array5[i] = (byte)(array[i] ^ 0xF0);
        }
        final byte[] hmac = this.getHmac(array5, new byte[4]);
        final byte[] array6 = new byte[4];
        System.arraycopy(array4, 0, array6, 0, array6.length);
        final byte[] hmac2 = this.getHmac(hmac, array6);
        final Cipher instance = Cipher.getInstance("ARCFOUR");
        instance.init(2, new SecretKeySpec(hmac2, "ARCFOUR"));
        return instance.doFinal(array3, n2, n3);
    }
    
    private byte[] getSalt(final int n) {
        final int arcfour_translate_usage = this.arcfour_translate_usage(n);
        return new byte[] { (byte)(arcfour_translate_usage & 0xFF), (byte)(arcfour_translate_usage >> 8 & 0xFF), (byte)(arcfour_translate_usage >> 16 & 0xFF), (byte)(arcfour_translate_usage >> 24 & 0xFF) };
    }
    
    private int arcfour_translate_usage(final int n) {
        switch (n) {
            case 3: {
                return 8;
            }
            case 9: {
                return 8;
            }
            case 23: {
                return 13;
            }
            default: {
                return n;
            }
        }
    }
    
    static {
        ZERO_IV = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
