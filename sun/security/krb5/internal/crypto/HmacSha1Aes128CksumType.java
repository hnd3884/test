package sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import sun.security.krb5.KrbCryptoException;

public class HmacSha1Aes128CksumType extends CksumType
{
    @Override
    public int confounderSize() {
        return 16;
    }
    
    @Override
    public int cksumType() {
        return 15;
    }
    
    @Override
    public boolean isKeyed() {
        return true;
    }
    
    @Override
    public int cksumSize() {
        return 12;
    }
    
    @Override
    public int keyType() {
        return 3;
    }
    
    @Override
    public int keySize() {
        return 16;
    }
    
    @Override
    public byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2) throws KrbCryptoException {
        try {
            return Aes128.calculateChecksum(array2, n2, array, 0, n);
        }
        catch (final GeneralSecurityException ex) {
            final KrbCryptoException ex2 = new KrbCryptoException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    @Override
    public boolean verifyChecksum(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2) throws KrbCryptoException {
        try {
            return CksumType.isChecksumEqual(array3, Aes128.calculateChecksum(array2, n2, array, 0, n));
        }
        catch (final GeneralSecurityException ex) {
            final KrbCryptoException ex2 = new KrbCryptoException(ex.getMessage());
            ex2.initCause(ex);
            throw ex2;
        }
    }
}
