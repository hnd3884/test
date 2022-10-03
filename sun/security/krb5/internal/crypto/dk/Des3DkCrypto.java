package sun.security.krb5.internal.crypto.dk;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.KeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.Cipher;
import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import java.security.GeneralSecurityException;
import java.util.Arrays;

public class Des3DkCrypto extends DkCrypto
{
    private static final byte[] ZERO_IV;
    
    @Override
    protected int getKeySeedLength() {
        return 168;
    }
    
    public byte[] stringToKey(final char[] array) throws GeneralSecurityException {
        byte[] charToUtf8 = null;
        try {
            charToUtf8 = DkCrypto.charToUtf8(array);
            return this.stringToKey(charToUtf8, null);
        }
        finally {
            if (charToUtf8 != null) {
                Arrays.fill(charToUtf8, (byte)0);
            }
        }
    }
    
    private byte[] stringToKey(final byte[] array, final byte[] array2) throws GeneralSecurityException {
        if (array2 != null && array2.length > 0) {
            throw new RuntimeException("Invalid parameter to stringToKey");
        }
        return this.dk(this.randomToKey(DkCrypto.nfold(array, this.getKeySeedLength())), Des3DkCrypto.KERBEROS_CONSTANT);
    }
    
    public byte[] parityFix(final byte[] parityBit) throws GeneralSecurityException {
        setParityBit(parityBit);
        return parityBit;
    }
    
    @Override
    protected byte[] randomToKey(final byte[] array) {
        if (array.length != 21) {
            throw new IllegalArgumentException("input must be 168 bits");
        }
        final byte[] keyCorrection = keyCorrection(des3Expand(array, 0, 7));
        final byte[] keyCorrection2 = keyCorrection(des3Expand(array, 7, 14));
        final byte[] keyCorrection3 = keyCorrection(des3Expand(array, 14, 21));
        final byte[] array2 = new byte[24];
        System.arraycopy(keyCorrection, 0, array2, 0, 8);
        System.arraycopy(keyCorrection2, 0, array2, 8, 8);
        System.arraycopy(keyCorrection3, 0, array2, 16, 8);
        return array2;
    }
    
    private static byte[] keyCorrection(final byte[] array) {
        try {
            if (DESKeySpec.isWeak(array, 0)) {
                array[7] ^= (byte)240;
            }
        }
        catch (final InvalidKeyException ex) {}
        return array;
    }
    
    private static byte[] des3Expand(final byte[] array, final int n, final int n2) {
        if (n2 - n != 7) {
            throw new IllegalArgumentException("Invalid length of DES Key Value:" + n + "," + n2);
        }
        final byte[] parityBit = new byte[8];
        byte b = 0;
        System.arraycopy(array, n, parityBit, 0, 7);
        int n3 = 0;
        for (int i = n; i < n2; ++i) {
            final byte b2 = (byte)(array[i] & 0x1);
            n3 = (byte)(n3 + 1);
            if (b2 != 0) {
                b |= (byte)(b2 << n3);
            }
        }
        parityBit[7] = b;
        setParityBit(parityBit);
        return parityBit;
    }
    
    private static void setParityBit(final byte[] array) {
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFE;
            array[i] = (byte)(n | ((Integer.bitCount(n) & 0x1) ^ 0x1));
        }
    }
    
    @Override
    protected Cipher getCipher(final byte[] array, byte[] zero_IV, final int n) throws GeneralSecurityException {
        final SecretKey generateSecret = SecretKeyFactory.getInstance("desede").generateSecret(new DESedeKeySpec(array, 0));
        if (zero_IV == null) {
            zero_IV = Des3DkCrypto.ZERO_IV;
        }
        final Cipher instance = Cipher.getInstance("DESede/CBC/NoPadding");
        instance.init(n, generateSecret, new IvParameterSpec(zero_IV, 0, zero_IV.length));
        return instance;
    }
    
    @Override
    public int getChecksumLength() {
        return 20;
    }
    
    @Override
    protected byte[] getHmac(final byte[] array, final byte[] array2) throws GeneralSecurityException {
        final SecretKeySpec secretKeySpec = new SecretKeySpec(array, "HmacSHA1");
        final Mac instance = Mac.getInstance("HmacSHA1");
        instance.init(secretKeySpec);
        return instance.doFinal(array2);
    }
    
    static {
        ZERO_IV = new byte[] { 0, 0, 0, 0, 0, 0, 0, 0 };
    }
}
