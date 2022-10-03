package sun.security.krb5.internal.crypto;

import sun.security.krb5.KrbCryptoException;
import java.security.GeneralSecurityException;
import sun.security.krb5.internal.crypto.dk.ArcFourCrypto;

public class ArcFourHmac
{
    private static final ArcFourCrypto CRYPTO;
    
    private ArcFourHmac() {
    }
    
    public static byte[] stringToKey(final char[] array) throws GeneralSecurityException {
        return ArcFourHmac.CRYPTO.stringToKey(array);
    }
    
    public static int getChecksumLength() {
        return ArcFourHmac.CRYPTO.getChecksumLength();
    }
    
    public static byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws GeneralSecurityException {
        return ArcFourHmac.CRYPTO.calculateChecksum(array, n, array2, n2, n3);
    }
    
    public static byte[] encryptSeq(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return ArcFourHmac.CRYPTO.encryptSeq(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] decryptSeq(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return ArcFourHmac.CRYPTO.decryptSeq(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] encrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return ArcFourHmac.CRYPTO.encrypt(array, n, array2, null, array3, n2, n3);
    }
    
    public static byte[] encryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return ArcFourHmac.CRYPTO.encryptRaw(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] decrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        return ArcFourHmac.CRYPTO.decrypt(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] decryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3, final byte[] array4) throws GeneralSecurityException {
        return ArcFourHmac.CRYPTO.decryptRaw(array, n, array2, array3, n2, n3, array4);
    }
    
    static {
        CRYPTO = new ArcFourCrypto(128);
    }
}
