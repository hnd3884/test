package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.KrbApErrException;
import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;

public final class Des3CbcHmacSha1KdEType extends EType
{
    @Override
    public int eType() {
        return 16;
    }
    
    @Override
    public int minimumPadSize() {
        return 0;
    }
    
    @Override
    public int confounderSize() {
        return this.blockSize();
    }
    
    @Override
    public int checksumType() {
        return 12;
    }
    
    @Override
    public int checksumSize() {
        return Des3.getChecksumLength();
    }
    
    @Override
    public int blockSize() {
        return 8;
    }
    
    @Override
    public int keyType() {
        return 2;
    }
    
    @Override
    public int keySize() {
        return 24;
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final byte[] array2, final int n) throws KrbCryptoException {
        return this.encrypt(array, array2, new byte[this.blockSize()], n);
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n) throws KrbCryptoException {
        try {
            return Des3.encrypt(array2, n, array3, array, 0, array.length);
        }
        catch (final GeneralSecurityException ex) {
            final KrbCryptoException ex2 = new KrbCryptoException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final byte[] array2, final int n) throws KrbApErrException, KrbCryptoException {
        return this.decrypt(array, array2, new byte[this.blockSize()], n);
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n) throws KrbApErrException, KrbCryptoException {
        try {
            return Des3.decrypt(array2, n, array3, array, 0, array.length);
        }
        catch (final GeneralSecurityException ex) {
            final KrbCryptoException ex2 = new KrbCryptoException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public byte[] decryptedData(final byte[] array) {
        return array;
    }
}
