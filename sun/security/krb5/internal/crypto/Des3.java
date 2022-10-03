package sun.security.krb5.internal.crypto;

import sun.security.krb5.KrbCryptoException;
import java.security.GeneralSecurityException;
import sun.security.krb5.internal.crypto.dk.Des3DkCrypto;

public class Des3
{
    private static final Des3DkCrypto CRYPTO;
    
    private Des3() {
    }
    
    public static byte[] stringToKey(final char[] array) throws GeneralSecurityException {
        return Des3.CRYPTO.stringToKey(array);
    }
    
    public static byte[] parityFix(final byte[] array) throws GeneralSecurityException {
        return Des3.CRYPTO.parityFix(array);
    }
    
    public static int getChecksumLength() {
        return Des3.CRYPTO.getChecksumLength();
    }
    
    public static byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws GeneralSecurityException {
        return Des3.CRYPTO.calculateChecksum(array, n, array2, n2, n3);
    }
    
    public static byte[] encrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return Des3.CRYPTO.encrypt(array, n, array2, null, array3, n2, n3);
    }
    
    public static byte[] encryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return Des3.CRYPTO.encryptRaw(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] decrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        return Des3.CRYPTO.decrypt(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] decryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        return Des3.CRYPTO.decryptRaw(array, n, array2, array3, n2, n3);
    }
    
    static {
        CRYPTO = new Des3DkCrypto();
    }
}
