package sun.security.krb5.internal.crypto.dk;

import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.crypto.KeyUsage;
import javax.crypto.Mac;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class AesDkCrypto extends DkCrypto
{
    private static final boolean debug = false;
    private static final int BLOCK_SIZE = 16;
    private static final int DEFAULT_ITERATION_COUNT = 4096;
    private static final byte[] ZERO_IV;
    private static final int hashSize = 12;
    private final int keyLength;
    
    public AesDkCrypto(final int keyLength) {
        this.keyLength = keyLength;
    }
    
    @Override
    protected int getKeySeedLength() {
        return this.keyLength;
    }
    
    public byte[] stringToKey(final char[] array, final String s, final byte[] array2) throws GeneralSecurityException {
        byte[] bytes = null;
        try {
            bytes = s.getBytes("UTF-8");
            return this.stringToKey(array, bytes, array2);
        }
        catch (final Exception ex) {
            return null;
        }
        finally {
            if (bytes != null) {
                Arrays.fill(bytes, (byte)0);
            }
        }
    }
    
    private byte[] stringToKey(final char[] array, final byte[] array2, final byte[] array3) throws GeneralSecurityException {
        int bigEndian = 4096;
        if (array3 != null) {
            if (array3.length != 4) {
                throw new RuntimeException("Invalid parameter to stringToKey");
            }
            bigEndian = readBigEndian(array3, 0, 4);
        }
        return this.dk(this.randomToKey(PBKDF2(array, array2, bigEndian, this.getKeySeedLength())), AesDkCrypto.KERBEROS_CONSTANT);
    }
    
    @Override
    protected byte[] randomToKey(final byte[] array) {
        return array;
    }
    
    @Override
    protected Cipher getCipher(final byte[] array, byte[] zero_IV, final int n) throws GeneralSecurityException {
        if (zero_IV == null) {
            zero_IV = AesDkCrypto.ZERO_IV;
        }
        final SecretKeySpec secretKeySpec = new SecretKeySpec(array, "AES");
        final Cipher instance = Cipher.getInstance("AES/CBC/NoPadding");
        instance.init(n, secretKeySpec, new IvParameterSpec(zero_IV, 0, zero_IV.length));
        return instance;
    }
    
    @Override
    public int getChecksumLength() {
        return 12;
    }
    
    @Override
    protected byte[] getHmac(final byte[] array, final byte[] array2) throws GeneralSecurityException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(array, "HMAC");
        final Mac instance = Mac.getInstance("HmacSHA1");
        instance.init(secretKeySpec);
        final byte[] doFinal = instance.doFinal(array2);
        final byte[] array3 = new byte[12];
        System.arraycopy(doFinal, 0, array3, 0, 12);
        return array3;
    }
    
    @Override
    public byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        final byte[] dk = this.dk(array, new byte[] { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF), -103 });
        try {
            final byte[] hmac = this.getHmac(dk, array2);
            if (hmac.length == this.getChecksumLength()) {
                return hmac;
            }
            if (hmac.length > this.getChecksumLength()) {
                final byte[] array3 = new byte[this.getChecksumLength()];
                System.arraycopy(hmac, 0, array3, 0, array3.length);
                return array3;
            }
            throw new GeneralSecurityException("checksum size too short: " + hmac.length + "; expecting : " + this.getChecksumLength());
        }
        finally {
            Arrays.fill(dk, 0, dk.length, (byte)0);
        }
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final byte[] array4, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        return this.encryptCTS(array, n, array2, array3, array4, n2, n3, true);
    }
    
    @Override
    public byte[] encryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        return this.encryptCTS(array, n, array2, null, array3, n2, n3, false);
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        return this.decryptCTS(array, n, array2, array3, n2, n3, true);
    }
    
    @Override
    public byte[] decryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        if (!KeyUsage.isValid(n)) {
            throw new GeneralSecurityException("Invalid key usage number: " + n);
        }
        return this.decryptCTS(array, n, array2, array3, n2, n3, false);
    }
    
    private byte[] encryptCTS(final byte[] array, final int n, final byte[] array2, final byte[] array3, final byte[] array4, final int n2, final int n3, final boolean b) throws GeneralSecurityException, KrbCryptoException {
        byte[] dk = null;
        byte[] dk2 = null;
        try {
            final byte[] array5 = { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF), -86 };
            dk = this.dk(array, array5);
            byte[] array6;
            if (b) {
                final byte[] bytes = Confounder.bytes(16);
                array6 = new byte[bytes.length + n3];
                System.arraycopy(bytes, 0, array6, 0, bytes.length);
                System.arraycopy(array4, n2, array6, bytes.length, n3);
            }
            else {
                array6 = new byte[n3];
                System.arraycopy(array4, n2, array6, 0, n3);
            }
            final byte[] array7 = new byte[array6.length + 12];
            final Cipher instance = Cipher.getInstance("AES/CTS/NoPadding");
            instance.init(1, new SecretKeySpec(dk, "AES"), new IvParameterSpec(array2, 0, array2.length));
            instance.doFinal(array6, 0, array6.length, array7);
            array5[4] = 85;
            dk2 = this.dk(array, array5);
            final byte[] hmac = this.getHmac(dk2, array6);
            System.arraycopy(hmac, 0, array7, array6.length, hmac.length);
            return array7;
        }
        finally {
            if (dk != null) {
                Arrays.fill(dk, 0, dk.length, (byte)0);
            }
            if (dk2 != null) {
                Arrays.fill(dk2, 0, dk2.length, (byte)0);
            }
        }
    }
    
    private byte[] decryptCTS(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3, final boolean b) throws GeneralSecurityException {
        byte[] dk = null;
        byte[] dk2 = null;
        try {
            final byte[] array4 = { (byte)(n >> 24 & 0xFF), (byte)(n >> 16 & 0xFF), (byte)(n >> 8 & 0xFF), (byte)(n & 0xFF), -86 };
            dk = this.dk(array, array4);
            final Cipher instance = Cipher.getInstance("AES/CTS/NoPadding");
            instance.init(2, new SecretKeySpec(dk, "AES"), new IvParameterSpec(array2, 0, array2.length));
            final byte[] doFinal = instance.doFinal(array3, n2, n3 - 12);
            array4[4] = 85;
            dk2 = this.dk(array, array4);
            final byte[] hmac = this.getHmac(dk2, doFinal);
            final int n4 = n2 + n3 - 12;
            boolean b2 = false;
            if (hmac.length >= 12) {
                for (int i = 0; i < 12; ++i) {
                    if (hmac[i] != array3[n4 + i]) {
                        b2 = true;
                        break;
                    }
                }
            }
            if (b2) {
                throw new GeneralSecurityException("Checksum failed");
            }
            if (b) {
                final byte[] array5 = new byte[doFinal.length - 16];
                System.arraycopy(doFinal, 16, array5, 0, array5.length);
                return array5;
            }
            return doFinal;
        }
        finally {
            if (dk != null) {
                Arrays.fill(dk, 0, dk.length, (byte)0);
            }
            if (dk2 != null) {
                Arrays.fill(dk2, 0, dk2.length, (byte)0);
            }
        }
    }
    
    private static byte[] PBKDF2(final char[] array, final byte[] array2, final int n, final int n2) throws GeneralSecurityException {
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(new PBEKeySpec(array, array2, n, n2)).getEncoded();
    }
    
    public static final int readBigEndian(final byte[] array, int n, int i) {
        int n2 = 0;
        int n3 = (i - 1) * 8;
        while (i > 0) {
            n2 += (array[n] & 0xFF) << n3;
            n3 -= 8;
            ++n;
            --i;
        }
        return n2;
    }
    
    static {
        ZERO_IV = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
