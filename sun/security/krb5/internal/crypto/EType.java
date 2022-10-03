package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.Krb5;
import java.util.ArrayList;
import sun.security.krb5.EncryptionKey;
import sun.security.krb5.KrbException;
import java.util.Arrays;
import javax.crypto.Cipher;
import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KdcErrException;
import sun.security.krb5.Config;

public abstract class EType
{
    private static final boolean DEBUG;
    private static boolean allowWeakCrypto;
    private static final int[] BUILTIN_ETYPES;
    private static final int[] BUILTIN_ETYPES_NOAES256;
    
    public static void initStatic() {
        boolean allowWeakCrypto = false;
        try {
            final String value = Config.getInstance().get("libdefaults", "allow_weak_crypto");
            if (value != null && value.equals("true")) {
                allowWeakCrypto = true;
            }
        }
        catch (final Exception ex) {
            if (EType.DEBUG) {
                System.out.println("Exception in getting allow_weak_crypto, using default value " + ex.getMessage());
            }
        }
        EType.allowWeakCrypto = allowWeakCrypto;
    }
    
    public static EType getInstance(final int n) throws KdcErrException {
        EType eType = null;
        String s = null;
        switch (n) {
            case 0: {
                eType = new NullEType();
                s = "sun.security.krb5.internal.crypto.NullEType";
                break;
            }
            case 1: {
                eType = new DesCbcCrcEType();
                s = "sun.security.krb5.internal.crypto.DesCbcCrcEType";
                break;
            }
            case 3: {
                eType = new DesCbcMd5EType();
                s = "sun.security.krb5.internal.crypto.DesCbcMd5EType";
                break;
            }
            case 16: {
                eType = new Des3CbcHmacSha1KdEType();
                s = "sun.security.krb5.internal.crypto.Des3CbcHmacSha1KdEType";
                break;
            }
            case 17: {
                eType = new Aes128CtsHmacSha1EType();
                s = "sun.security.krb5.internal.crypto.Aes128CtsHmacSha1EType";
                break;
            }
            case 18: {
                eType = new Aes256CtsHmacSha1EType();
                s = "sun.security.krb5.internal.crypto.Aes256CtsHmacSha1EType";
                break;
            }
            case 23: {
                eType = new ArcFourHmacEType();
                s = "sun.security.krb5.internal.crypto.ArcFourHmacEType";
                break;
            }
            default: {
                throw new KdcErrException(14, "encryption type = " + toString(n) + " (" + n + ")");
            }
        }
        if (EType.DEBUG) {
            System.out.println(">>> EType: " + s);
        }
        return eType;
    }
    
    public abstract int eType();
    
    public abstract int minimumPadSize();
    
    public abstract int confounderSize();
    
    public abstract int checksumType();
    
    public abstract int checksumSize();
    
    public abstract int blockSize();
    
    public abstract int keyType();
    
    public abstract int keySize();
    
    public abstract byte[] encrypt(final byte[] p0, final byte[] p1, final int p2) throws KrbCryptoException;
    
    public abstract byte[] encrypt(final byte[] p0, final byte[] p1, final byte[] p2, final int p3) throws KrbCryptoException;
    
    public abstract byte[] decrypt(final byte[] p0, final byte[] p1, final int p2) throws KrbApErrException, KrbCryptoException;
    
    public abstract byte[] decrypt(final byte[] p0, final byte[] p1, final byte[] p2, final int p3) throws KrbApErrException, KrbCryptoException;
    
    public int dataSize(final byte[] array) {
        return array.length - this.startOfData();
    }
    
    public int padSize(final byte[] array) {
        return array.length - this.confounderSize() - this.checksumSize() - this.dataSize(array);
    }
    
    public int startOfChecksum() {
        return this.confounderSize();
    }
    
    public int startOfData() {
        return this.confounderSize() + this.checksumSize();
    }
    
    public int startOfPad(final byte[] array) {
        return this.confounderSize() + this.checksumSize() + this.dataSize(array);
    }
    
    public byte[] decryptedData(final byte[] array) {
        final int dataSize = this.dataSize(array);
        final byte[] array2 = new byte[dataSize];
        System.arraycopy(array, this.startOfData(), array2, 0, dataSize);
        return array2;
    }
    
    public static int[] getBuiltInDefaults() {
        int maxAllowedKeyLength = 0;
        try {
            maxAllowedKeyLength = Cipher.getMaxAllowedKeyLength("AES");
        }
        catch (final Exception ex) {}
        int[] array;
        if (maxAllowedKeyLength < 256) {
            array = EType.BUILTIN_ETYPES_NOAES256;
        }
        else {
            array = EType.BUILTIN_ETYPES;
        }
        if (!EType.allowWeakCrypto) {
            return Arrays.copyOfRange(array, 0, array.length - 2);
        }
        return array;
    }
    
    public static int[] getDefaults(final String s) throws KrbException {
        Config instance;
        try {
            instance = Config.getInstance();
        }
        catch (final KrbException ex) {
            if (EType.DEBUG) {
                System.out.println("Exception while getting " + s + ex.getMessage());
                System.out.println("Using default builtin etypes");
            }
            return getBuiltInDefaults();
        }
        return instance.defaultEtype(s);
    }
    
    public static int[] getDefaults(final String s, final EncryptionKey[] array) throws KrbException {
        final int[] defaults = getDefaults(s);
        final ArrayList list = new ArrayList(defaults.length);
        for (int i = 0; i < defaults.length; ++i) {
            if (EncryptionKey.findKey(defaults[i], array) != null) {
                list.add((Object)defaults[i]);
            }
        }
        final int size = list.size();
        if (size <= 0) {
            final StringBuffer sb = new StringBuffer();
            for (int j = 0; j < array.length; ++j) {
                sb.append(toString(array[j].getEType()));
                sb.append(" ");
            }
            throw new KrbException("Do not have keys of types listed in " + s + " available; only have keys of following type: " + sb.toString());
        }
        final int[] array2 = new int[size];
        for (int k = 0; k < size; ++k) {
            array2[k] = (int)list.get(k);
        }
        return array2;
    }
    
    public static boolean isSupported(final int n, final int[] array) {
        for (int i = 0; i < array.length; ++i) {
            if (n == array[i]) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isSupported(final int n) {
        return isSupported(n, getBuiltInDefaults());
    }
    
    public static boolean isNewer(final int n) {
        return n != 1 && n != 2 && n != 3 && n != 16 && n != 23 && n != 24;
    }
    
    public static String toString(final int n) {
        switch (n) {
            case 0: {
                return "NULL";
            }
            case 1: {
                return "DES CBC mode with CRC-32";
            }
            case 2: {
                return "DES CBC mode with MD4";
            }
            case 3: {
                return "DES CBC mode with MD5";
            }
            case 4: {
                return "reserved";
            }
            case 5: {
                return "DES3 CBC mode with MD5";
            }
            case 6: {
                return "reserved";
            }
            case 7: {
                return "DES3 CBC mode with SHA1";
            }
            case 9: {
                return "DSA with SHA1- Cms0ID";
            }
            case 10: {
                return "MD5 with RSA encryption - Cms0ID";
            }
            case 11: {
                return "SHA1 with RSA encryption - Cms0ID";
            }
            case 12: {
                return "RC2 CBC mode with Env0ID";
            }
            case 13: {
                return "RSA encryption with Env0ID";
            }
            case 14: {
                return "RSAES-0AEP-ENV-0ID";
            }
            case 15: {
                return "DES-EDE3-CBC-ENV-0ID";
            }
            case 16: {
                return "DES3 CBC mode with SHA1-KD";
            }
            case 17: {
                return "AES128 CTS mode with HMAC SHA1-96";
            }
            case 18: {
                return "AES256 CTS mode with HMAC SHA1-96";
            }
            case 23: {
                return "RC4 with HMAC";
            }
            case 24: {
                return "RC4 with HMAC EXP";
            }
            default: {
                return "Unknown (" + n + ")";
            }
        }
    }
    
    static {
        DEBUG = Krb5.DEBUG;
        initStatic();
        BUILTIN_ETYPES = new int[] { 18, 17, 16, 23, 1, 3 };
        BUILTIN_ETYPES_NOAES256 = new int[] { 17, 16, 23, 1, 3 };
    }
}
