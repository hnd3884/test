package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.Krb5;
import sun.security.krb5.KrbCryptoException;
import sun.security.krb5.internal.KdcErrException;

public abstract class CksumType
{
    private static boolean DEBUG;
    
    public static CksumType getInstance(final int n) throws KdcErrException {
        CksumType cksumType = null;
        String s = null;
        switch (n) {
            case 1: {
                cksumType = new Crc32CksumType();
                s = "sun.security.krb5.internal.crypto.Crc32CksumType";
                break;
            }
            case 4: {
                cksumType = new DesMacCksumType();
                s = "sun.security.krb5.internal.crypto.DesMacCksumType";
                break;
            }
            case 5: {
                cksumType = new DesMacKCksumType();
                s = "sun.security.krb5.internal.crypto.DesMacKCksumType";
                break;
            }
            case 7: {
                cksumType = new RsaMd5CksumType();
                s = "sun.security.krb5.internal.crypto.RsaMd5CksumType";
                break;
            }
            case 8: {
                cksumType = new RsaMd5DesCksumType();
                s = "sun.security.krb5.internal.crypto.RsaMd5DesCksumType";
                break;
            }
            case 12: {
                cksumType = new HmacSha1Des3KdCksumType();
                s = "sun.security.krb5.internal.crypto.HmacSha1Des3KdCksumType";
                break;
            }
            case 15: {
                cksumType = new HmacSha1Aes128CksumType();
                s = "sun.security.krb5.internal.crypto.HmacSha1Aes128CksumType";
                break;
            }
            case 16: {
                cksumType = new HmacSha1Aes256CksumType();
                s = "sun.security.krb5.internal.crypto.HmacSha1Aes256CksumType";
                break;
            }
            case -138: {
                cksumType = new HmacMd5ArcFourCksumType();
                s = "sun.security.krb5.internal.crypto.HmacMd5ArcFourCksumType";
                break;
            }
            default: {
                throw new KdcErrException(15);
            }
        }
        if (CksumType.DEBUG) {
            System.out.println(">>> CksumType: " + s);
        }
        return cksumType;
    }
    
    public abstract int confounderSize();
    
    public abstract int cksumType();
    
    public abstract boolean isKeyed();
    
    public abstract int cksumSize();
    
    public abstract int keyType();
    
    public abstract int keySize();
    
    public abstract byte[] calculateChecksum(final byte[] p0, final int p1, final byte[] p2, final int p3) throws KrbCryptoException;
    
    public abstract boolean verifyChecksum(final byte[] p0, final int p1, final byte[] p2, final byte[] p3, final int p4) throws KrbCryptoException;
    
    public static boolean isChecksumEqual(final byte[] array, final byte[] array2) {
        if (array == array2) {
            return true;
        }
        if ((array == null && array2 != null) || (array != null && array2 == null)) {
            return false;
        }
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        CksumType.DEBUG = Krb5.DEBUG;
    }
}
