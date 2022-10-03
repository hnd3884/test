package sun.security.pkcs11;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.ProviderException;
import java.security.Security;
import java.security.Provider;

public final class P11Util
{
    private static Object LOCK;
    private static volatile Provider sun;
    private static volatile Provider sunRsaSign;
    private static volatile Provider sunJce;
    private static final char[] hexDigits;
    
    private P11Util() {
    }
    
    static Provider getSunProvider() {
        Provider sun = P11Util.sun;
        if (sun == null) {
            synchronized (P11Util.LOCK) {
                sun = (P11Util.sun = getProvider(P11Util.sun, "SUN", "sun.security.provider.Sun"));
            }
        }
        return sun;
    }
    
    static Provider getSunRsaSignProvider() {
        Provider sunRsaSign = P11Util.sunRsaSign;
        if (sunRsaSign == null) {
            synchronized (P11Util.LOCK) {
                sunRsaSign = (P11Util.sunRsaSign = getProvider(P11Util.sunRsaSign, "SunRsaSign", "sun.security.rsa.SunRsaSign"));
            }
        }
        return sunRsaSign;
    }
    
    static Provider getSunJceProvider() {
        Provider sunJce = P11Util.sunJce;
        if (sunJce == null) {
            synchronized (P11Util.LOCK) {
                sunJce = (P11Util.sunJce = getProvider(P11Util.sunJce, "SunJCE", "com.sun.crypto.provider.SunJCE"));
            }
        }
        return sunJce;
    }
    
    private static Provider getProvider(Provider provider, final String s, final String s2) {
        if (provider != null) {
            return provider;
        }
        provider = Security.getProvider(s);
        if (provider == null) {
            try {
                provider = (Provider)Class.forName(s2).newInstance();
            }
            catch (final Exception ex) {
                throw new ProviderException("Could not find provider " + s, ex);
            }
        }
        return provider;
    }
    
    static byte[] convert(final byte[] array, final int n, final int n2) {
        if (n == 0 && n2 == array.length) {
            return array;
        }
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    static byte[] subarray(final byte[] array, final int n, final int n2) {
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        return array2;
    }
    
    static byte[] concat(final byte[] array, final byte[] array2) {
        final byte[] array3 = new byte[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    static long[] concat(final long[] array, final long[] array2) {
        if (array.length == 0) {
            return array2;
        }
        final long[] array3 = new long[array.length + array2.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        System.arraycopy(array2, 0, array3, array.length, array2.length);
        return array3;
    }
    
    public static byte[] getMagnitude(final BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length > 1 && byteArray[0] == 0) {
            final int n = byteArray.length - 1;
            final byte[] array = new byte[n];
            System.arraycopy(byteArray, 1, array, 0, n);
            byteArray = array;
        }
        return byteArray;
    }
    
    static byte[] getBytesUTF8(final String s) {
        try {
            return s.getBytes("UTF8");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    static byte[] sha1(final byte[] array) {
        try {
            final MessageDigest instance = MessageDigest.getInstance("SHA-1");
            instance.update(array);
            return instance.digest();
        }
        catch (final GeneralSecurityException ex) {
            throw new ProviderException(ex);
        }
    }
    
    static String toString(final byte[] array) {
        if (array == null) {
            return "(null)";
        }
        final StringBuffer sb = new StringBuffer(array.length * 3);
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i] & 0xFF;
            if (i != 0) {
                sb.append(':');
            }
            sb.append(P11Util.hexDigits[n >>> 4]);
            sb.append(P11Util.hexDigits[n & 0xF]);
        }
        return sb.toString();
    }
    
    static {
        P11Util.LOCK = new Object();
        hexDigits = "0123456789abcdef".toCharArray();
    }
}
