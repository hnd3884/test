package sun.security.krb5.internal.crypto;

import sun.security.krb5.KrbCryptoException;
import java.security.MessageDigest;

public final class RsaMd5CksumType extends CksumType
{
    @Override
    public int confounderSize() {
        return 0;
    }
    
    @Override
    public int cksumType() {
        return 7;
    }
    
    @Override
    public boolean isKeyed() {
        return false;
    }
    
    @Override
    public int cksumSize() {
        return 16;
    }
    
    @Override
    public int keyType() {
        return 0;
    }
    
    @Override
    public int keySize() {
        return 0;
    }
    
    @Override
    public byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2) throws KrbCryptoException {
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("MD5");
        }
        catch (final Exception ex) {
            throw new KrbCryptoException("JCE provider may not be installed. " + ex.getMessage());
        }
        byte[] digest;
        try {
            instance.update(array);
            digest = instance.digest();
        }
        catch (final Exception ex2) {
            throw new KrbCryptoException(ex2.getMessage());
        }
        return digest;
    }
    
    @Override
    public boolean verifyChecksum(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2) throws KrbCryptoException {
        try {
            return CksumType.isChecksumEqual(MessageDigest.getInstance("MD5").digest(array), array3);
        }
        catch (final Exception ex) {
            return false;
        }
    }
}
