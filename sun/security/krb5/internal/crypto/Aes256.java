package sun.security.krb5.internal.crypto;

import sun.security.krb5.KrbCryptoException;
import java.security.GeneralSecurityException;
import sun.security.krb5.internal.crypto.dk.AesDkCrypto;

public class Aes256
{
    private static final AesDkCrypto CRYPTO;
    
    private Aes256() {
    }
    
    public static byte[] stringToKey(final char[] array, final String s, final byte[] array2) throws GeneralSecurityException {
        return Aes256.CRYPTO.stringToKey(array, s, array2);
    }
    
    public static int getChecksumLength() {
        return Aes256.CRYPTO.getChecksumLength();
    }
    
    public static byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2, final int n3) throws GeneralSecurityException {
        return Aes256.CRYPTO.calculateChecksum(array, n, array2, n2, n3);
    }
    
    public static byte[] encrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return Aes256.CRYPTO.encrypt(array, n, array2, null, array3, n2, n3);
    }
    
    public static byte[] encryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException, KrbCryptoException {
        return Aes256.CRYPTO.encryptRaw(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] decrypt(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        return Aes256.CRYPTO.decrypt(array, n, array2, array3, n2, n3);
    }
    
    public static byte[] decryptRaw(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2, final int n3) throws GeneralSecurityException {
        return Aes256.CRYPTO.decryptRaw(array, n, array2, array3, n2, n3);
    }
    
    static {
        CRYPTO = new AesDkCrypto(256);
    }
}
