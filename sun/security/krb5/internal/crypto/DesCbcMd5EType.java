package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.KrbCryptoException;
import java.security.MessageDigest;

public final class DesCbcMd5EType extends DesCbcEType
{
    @Override
    public int eType() {
        return 3;
    }
    
    @Override
    public int minimumPadSize() {
        return 0;
    }
    
    @Override
    public int confounderSize() {
        return 8;
    }
    
    @Override
    public int checksumType() {
        return 7;
    }
    
    @Override
    public int checksumSize() {
        return 16;
    }
    
    @Override
    protected byte[] calculateChecksum(final byte[] array, final int n) throws KrbCryptoException {
        MessageDigest instance;
        try {
            instance = MessageDigest.getInstance("MD5");
        }
        catch (final Exception ex) {
            throw new KrbCryptoException("JCE provider may not be installed. " + ex.getMessage());
        }
        try {
            instance.update(array);
            return instance.digest();
        }
        catch (final Exception ex2) {
            throw new KrbCryptoException(ex2.getMessage());
        }
    }
}
