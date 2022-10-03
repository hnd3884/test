package sun.security.krb5.internal.crypto;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.util.Arrays;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;
import javax.crypto.Cipher;

public final class Des
{
    private static final String CHARSET;
    private static final long[] bad_keys;
    private static final byte[] good_parity;
    
    public static final byte[] set_parity(final byte[] array) {
        for (int i = 0; i < 8; ++i) {
            array[i] = Des.good_parity[array[i] & 0xFF];
        }
        return array;
    }
    
    public static final long set_parity(final long n) {
        return octet2long(set_parity(long2octet(n)));
    }
    
    public static final boolean bad_key(final long n) {
        for (int i = 0; i < Des.bad_keys.length; ++i) {
            if (Des.bad_keys[i] == n) {
                return true;
            }
        }
        return false;
    }
    
    public static final boolean bad_key(final byte[] array) {
        return bad_key(octet2long(array));
    }
    
    public static long octet2long(final byte[] array) {
        return octet2long(array, 0);
    }
    
    public static long octet2long(final byte[] array, final int n) {
        long n2 = 0L;
        for (int i = 0; i < 8; ++i) {
            if (i + n < array.length) {
                n2 |= ((long)array[i + n] & 0xFFL) << (7 - i) * 8;
            }
        }
        return n2;
    }
    
    public static byte[] long2octet(final long n) {
        final byte[] array = new byte[8];
        for (int i = 0; i < 8; ++i) {
            array[i] = (byte)(n >>> (7 - i) * 8 & 0xFFL);
        }
        return array;
    }
    
    public static void long2octet(final long n, final byte[] array) {
        long2octet(n, array, 0);
    }
    
    public static void long2octet(final long n, final byte[] array, final int n2) {
        for (int i = 0; i < 8; ++i) {
            if (i + n2 < array.length) {
                array[i + n2] = (byte)(n >>> (7 - i) * 8 & 0xFFL);
            }
        }
    }
    
    public static void cbc_encrypt(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4, final boolean b) throws KrbCryptoException {
        Cipher instance;
        try {
            instance = Cipher.getInstance("DES/CBC/NoPadding");
        }
        catch (final GeneralSecurityException ex) {
            final KrbCryptoException ex2 = new KrbCryptoException("JCE provider may not be installed. " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(array4);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(array3, "DES");
        try {
            SecretKeyFactory.getInstance("DES");
            final SecretKeySpec secretKeySpec2 = secretKeySpec;
            if (b) {
                instance.init(1, secretKeySpec2, ivParameterSpec);
            }
            else {
                instance.init(2, secretKeySpec2, ivParameterSpec);
            }
            final byte[] doFinal = instance.doFinal(array);
            System.arraycopy(doFinal, 0, array2, 0, doFinal.length);
        }
        catch (final GeneralSecurityException ex3) {
            final KrbCryptoException ex4 = new KrbCryptoException(ex3.getMessage());
            ex4.initCause(ex3);
            throw ex4;
        }
    }
    
    public static long char_to_key(final char[] array) throws KrbCryptoException {
        long n = 0L;
        byte[] array2 = null;
        try {
            if (Des.CHARSET == null) {
                array2 = new String(array).getBytes();
            }
            else {
                array2 = new String(array).getBytes(Des.CHARSET);
            }
        }
        catch (final Exception ex) {
            if (array2 != null) {
                Arrays.fill(array2, 0, array2.length, (byte)0);
            }
            final KrbCryptoException ex2 = new KrbCryptoException("Unable to convert passwd, " + ex);
            ex2.initCause(ex);
            throw ex2;
        }
        final byte[] pad = pad(array2);
        final byte[] array3 = new byte[8];
        for (int n2 = pad.length / 8 + ((pad.length % 8 != 0) ? 1 : 0), i = 0; i < n2; ++i) {
            long n3 = octet2long(pad, i * 8) & 0x7F7F7F7F7F7F7F7FL;
            if (i % 2 == 1) {
                long n4 = 0L;
                for (int j = 0; j < 64; ++j) {
                    n4 |= (n3 & 1L << j) >>> j << 63 - j;
                }
                n3 = n4 >>> 1;
            }
            n ^= n3 << 1;
        }
        long n5 = set_parity(n);
        if (bad_key(n5)) {
            final byte[] long2octet;
            final byte[] array4 = long2octet = long2octet(n5);
            final int n6 = 7;
            long2octet[n6] ^= (byte)240;
            n5 = octet2long(array4);
        }
        long n7 = octet2long(set_parity(des_cksum(long2octet(n5), pad, long2octet(n5))));
        if (bad_key(n7)) {
            final byte[] long2octet2;
            final byte[] array5 = long2octet2 = long2octet(n7);
            final int n8 = 7;
            long2octet2[n8] ^= (byte)240;
            n7 = octet2long(array5);
        }
        if (array2 != null) {
            Arrays.fill(array2, 0, array2.length, (byte)0);
        }
        if (pad != null) {
            Arrays.fill(pad, 0, pad.length, (byte)0);
        }
        return n7;
    }
    
    public static byte[] des_cksum(final byte[] array, final byte[] array2, final byte[] array3) throws KrbCryptoException {
        byte[] doFinal = new byte[8];
        Cipher instance;
        try {
            instance = Cipher.getInstance("DES/CBC/NoPadding");
        }
        catch (final Exception ex) {
            final KrbCryptoException ex2 = new KrbCryptoException("JCE provider may not be installed. " + ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(array);
        final SecretKeySpec secretKeySpec = new SecretKeySpec(array3, "DES");
        try {
            SecretKeyFactory.getInstance("DES");
            final SecretKeySpec secretKeySpec2 = secretKeySpec;
            instance.init(1, secretKeySpec2, ivParameterSpec);
            for (int i = 0; i < array2.length / 8; ++i) {
                doFinal = instance.doFinal(array2, i * 8, 8);
                instance.init(1, secretKeySpec2, new IvParameterSpec(doFinal));
            }
        }
        catch (final GeneralSecurityException ex3) {
            final KrbCryptoException ex4 = new KrbCryptoException(ex3.getMessage());
            ex4.initCause(ex3);
            throw ex4;
        }
        return doFinal;
    }
    
    static byte[] pad(final byte[] array) {
        int length;
        if (array.length < 8) {
            length = array.length;
        }
        else {
            length = array.length % 8;
        }
        if (length == 0) {
            return array;
        }
        final byte[] array2 = new byte[8 - length + array.length];
        for (int i = array2.length - 1; i > array.length - 1; --i) {
            array2[i] = 0;
        }
        System.arraycopy(array, 0, array2, 0, array.length);
        return array2;
    }
    
    public static byte[] string_to_key_bytes(final char[] array) throws KrbCryptoException {
        return long2octet(char_to_key(array));
    }
    
    static {
        CHARSET = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.security.krb5.msinterop.des.s2kcharset"));
        bad_keys = new long[] { 72340172838076673L, -72340172838076674L, 2242545357980376863L, -2242545357980376864L, 143554428589179390L, -143554428589179391L, 2296870857142767345L, -2296870857142767346L, 135110050437988849L, -2305315235293957887L, 2305315235293957886L, -135110050437988850L, 80784550989267214L, 2234100979542855169L, -2234100979542855170L, -80784550989267215L };
        good_parity = new byte[] { 1, 1, 2, 2, 4, 4, 7, 7, 8, 8, 11, 11, 13, 13, 14, 14, 16, 16, 19, 19, 21, 21, 22, 22, 25, 25, 26, 26, 28, 28, 31, 31, 32, 32, 35, 35, 37, 37, 38, 38, 41, 41, 42, 42, 44, 44, 47, 47, 49, 49, 50, 50, 52, 52, 55, 55, 56, 56, 59, 59, 61, 61, 62, 62, 64, 64, 67, 67, 69, 69, 70, 70, 73, 73, 74, 74, 76, 76, 79, 79, 81, 81, 82, 82, 84, 84, 87, 87, 88, 88, 91, 91, 93, 93, 94, 94, 97, 97, 98, 98, 100, 100, 103, 103, 104, 104, 107, 107, 109, 109, 110, 110, 112, 112, 115, 115, 117, 117, 118, 118, 121, 121, 122, 122, 124, 124, 127, 127, -128, -128, -125, -125, -123, -123, -122, -122, -119, -119, -118, -118, -116, -116, -113, -113, -111, -111, -110, -110, -108, -108, -105, -105, -104, -104, -101, -101, -99, -99, -98, -98, -95, -95, -94, -94, -92, -92, -89, -89, -88, -88, -85, -85, -83, -83, -82, -82, -80, -80, -77, -77, -75, -75, -74, -74, -71, -71, -70, -70, -68, -68, -65, -65, -63, -63, -62, -62, -60, -60, -57, -57, -56, -56, -53, -53, -51, -51, -50, -50, -48, -48, -45, -45, -43, -43, -42, -42, -39, -39, -38, -38, -36, -36, -33, -33, -32, -32, -29, -29, -27, -27, -26, -26, -23, -23, -22, -22, -20, -20, -17, -17, -15, -15, -14, -14, -12, -12, -9, -9, -8, -8, -5, -5, -3, -3, -2, -2 };
    }
}
