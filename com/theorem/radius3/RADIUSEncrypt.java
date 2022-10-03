package com.theorem.radius3;

import java.security.MessageDigest;
import com.theorem.radius3.radutil.MD5Digest;
import com.theorem.radius3.radutil.Util;
import com.theorem.radius3.radutil.RadRand;

public final class RADIUSEncrypt
{
    private static RadRand a;
    public static final String encoding = "UTF8";
    
    public static byte[] password(final String s, final String s2, final String s3, final String s4, final byte[] array) {
        return password(s, s2, Util.toUTF8(s3), array);
    }
    
    public static byte[] password(final String s, final String s2, final byte[] array) {
        return password(s, "UTF8", s2, "UTF8", array);
    }
    
    public static byte[] password(final String s, final String s2, final byte[] array, final byte[] array2) {
        return password(Util.toUTF8(s), array, array2);
    }
    
    public static byte[] password(final String s, final byte[] array, final byte[] array2) {
        return password(s, "UTF8", array, array2);
    }
    
    public static byte[] password(final byte[] array, final byte[] array2, final byte[] array3) {
        return encrypt(array, array2, array3);
    }
    
    public static byte[] saltEncode(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
        final byte[] a = a(array2.length + 1, 16);
        a[0] = (byte)array2.length;
        System.arraycopy(array2, 0, a, 1, array2.length);
        return a(array, a, array3, array4);
    }
    
    public static byte[] saltEncode(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] a = a(array.length + 1, 16);
        a[0] = (byte)array.length;
        System.arraycopy(array, 0, a, 1, array.length);
        final RadRand radRand = new RadRand();
        final byte[] array4 = new byte[2];
        radRand.nextBytes(array4);
        return a(array4, a, array2, array3);
    }
    
    public static byte[] saltDecode(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] a = a(array, array2, array3);
        int n = a[0] & 0xFF;
        if (n > a.length - 1) {
            n = a.length - 1;
        }
        final byte[] array4 = new byte[n];
        System.arraycopy(a, 1, array4, 0, n);
        return array4;
    }
    
    public static byte[] encipherTunnelPassword(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] array4 = new byte[2];
        new RadRand().nextBytes(array4);
        final byte[] array5 = array4;
        final int n = 0;
        array5[n] |= (byte)128;
        return saltEncode(array4, array, array2, array3);
    }
    
    public static byte[] decipherTunnelPassword(final byte[] array, final byte[] array2, final byte[] array3) {
        return saltDecode(array, array2, array3);
    }
    
    protected static byte[] a(final byte[] array, final byte[] array2, final byte[] array3, final byte[] array4) {
        final int length = array2.length;
        final byte[] a = a(length + 1, 16);
        final int length2 = a.length;
        final byte[] array5 = new byte[length2];
        array5[0] = (byte)length;
        System.arraycopy(array2, 0, array5, 1, length);
        final MessageDigest value = MD5Digest.get();
        value.update(array3);
        value.update(array4);
        value.update(array);
        byte[] array6 = value.digest();
        final int n = (length2 + 16 - 1) / 16;
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < 16; ++j, ++n2) {
                a[n2] = (byte)(array5[n2] ^ array6[j]);
            }
            if (i < n - 1) {
                value.reset();
                value.update(array3);
                value.update(a, n2 - 16, 16);
                array6 = value.digest();
            }
        }
        final byte[] array7 = new byte[a.length + 2];
        System.arraycopy(a, 0, array7, 2, length2);
        array7[0] = array[0];
        array7[1] = array[1];
        return array7;
    }
    
    protected static byte[] a(final byte[] array, final byte[] array2, final byte[] array3) {
        final int n = array.length - 2;
        final byte[] array4 = { array[0], array[1] };
        final byte[] a = a(n - 2, 16);
        final int length = a.length;
        System.arraycopy(array, 2, a, 0, n);
        final byte[] array5 = new byte[length];
        final MessageDigest value = MD5Digest.get();
        value.update(array2);
        value.update(array3);
        value.update(array4);
        byte[] array6 = value.digest();
        final int n2 = length / 16;
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            for (int j = 0; j < 16; ++j, ++n3) {
                array5[n3] = (byte)(a[n3] ^ array6[j]);
            }
            if (i < n2 - 1) {
                value.reset();
                value.update(array2);
                value.update(a, n3 - 16, 16);
                array6 = value.digest();
            }
        }
        final byte[] array7 = new byte[array5[0] & 0xFF];
        System.arraycopy(array5, 1, array7, 0, array7.length);
        return array7;
    }
    
    public static byte[] encrypt(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] a = a(array, 128);
        final int length = a.length;
        final byte[] array4 = new byte[length];
        final MessageDigest value = MD5Digest.get();
        value.update(array2);
        value.update(array3);
        final byte[] digest = value.digest();
        for (int i = 0; i < 16; ++i) {
            array4[i] = (byte)(a[i] ^ digest[i]);
        }
        final int n = length / 16;
        int n2 = 16;
        for (int j = 1; j < n; ++j) {
            value.reset();
            value.update(array2);
            value.update(array4, n2 - 16, 16);
            final byte[] digest2 = value.digest();
            for (int k = 0; k < 16; ++k, ++n2) {
                array4[n2] = (byte)(a[n2] ^ digest2[k]);
            }
        }
        return array4;
    }
    
    public static byte[] decrypt(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] a = a(array, 128);
        final int length = a.length;
        final byte[] array4 = new byte[length];
        final MessageDigest value = MD5Digest.get();
        value.update(array2);
        value.update(array3);
        final byte[] digest = value.digest();
        for (int i = 0; i < 16; ++i) {
            array4[i] = (byte)(a[i] ^ digest[i]);
        }
        final int n = length / 16;
        int n2 = 16;
        for (int j = 1; j < n; ++j) {
            value.reset();
            value.update(array2);
            value.update(a, n2 - 16, 16);
            final byte[] digest2 = value.digest();
            for (int k = 0; k < 16; ++k, ++n2) {
                array4[n2] = (byte)(a[n2] ^ digest2[k]);
            }
        }
        return array4;
    }
    
    public static byte[] encode3(final byte[] array, final byte[] array2, final byte[] array3) {
        final byte[] a = a(array, 16);
        final MessageDigest value = MD5Digest.get();
        value.update(array3);
        value.update(array2);
        final byte[] digest = value.digest();
        final byte[] array4 = new byte[16];
        for (int i = 0; i < 16; ++i) {
            array4[i] = (byte)(a[i] ^ digest[i]);
        }
        return array4;
    }
    
    private static byte[] a(final byte[] array, final int n) {
        final int length = array.length;
        final int n2 = (length > n) ? n : length;
        final byte[] a = a(n2, 16);
        System.arraycopy(array, 0, a, 0, n2);
        return a;
    }
    
    private static byte[] a(final int n, final int n2) {
        int n3 = (n + n2 - 1) / n2 * n2;
        if (n3 == 0) {
            n3 = n2;
        }
        return new byte[n3];
    }
    
    public static byte[] trim(final byte[] array) {
        int n;
        for (n = array.length - 1; n > 0 && array[n] == 0; --n) {}
        final byte[] array2 = new byte[++n];
        System.arraycopy(array, 0, array2, 0, n);
        return array2;
    }
    
    public static boolean cmp(final byte[] array, final byte[] array2) {
        if (array2 == null) {
            return array == null;
        }
        if (array == null) {
            return false;
        }
        final int length = array.length;
        if (length != array2.length) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static synchronized byte[] genAuthenticator() {
        final byte[] array = new byte[16];
        synchronized (RADIUSEncrypt.a) {
            RADIUSEncrypt.a.nextBytes(array);
        }
        return array;
    }
    
    static {
        RADIUSEncrypt.a = new RadRand();
    }
}
