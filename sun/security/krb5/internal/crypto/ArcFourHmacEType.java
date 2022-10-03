package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.KrbApErrException;
import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;

public final class ArcFourHmacEType extends EType
{
    @Override
    public int eType() {
        return 23;
    }
    
    @Override
    public int minimumPadSize() {
        return 1;
    }
    
    @Override
    public int confounderSize() {
        return 8;
    }
    
    @Override
    public int checksumType() {
        return -138;
    }
    
    @Override
    public int checksumSize() {
        return ArcFourHmac.getChecksumLength();
    }
    
    @Override
    public int blockSize() {
        return 1;
    }
    
    @Override
    public int keyType() {
        return 4;
    }
    
    @Override
    public int keySize() {
        return 16;
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final byte[] array2, final int n) throws KrbCryptoException {
        return this.encrypt(array, array2, new byte[this.blockSize()], n);
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n) throws KrbCryptoException {
        try {
            return ArcFourHmac.encrypt(array2, n, array3, array, 0, array.length);
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
            return ArcFourHmac.decrypt(array2, n, array3, array, 0, array.length);
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
