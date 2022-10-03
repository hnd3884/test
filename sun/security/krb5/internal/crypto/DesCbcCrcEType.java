package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.KrbCryptoException;

public class DesCbcCrcEType extends DesCbcEType
{
    @Override
    public int eType() {
        return 1;
    }
    
    @Override
    public int minimumPadSize() {
        return 4;
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
        return 4;
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final byte[] array2, final int n) throws KrbCryptoException {
        return this.encrypt(array, array2, array2, n);
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final byte[] array2, final int n) throws KrbApErrException, KrbCryptoException {
        return this.decrypt(array, array2, array2, n);
    }
    
    @Override
    protected byte[] calculateChecksum(final byte[] array, final int n) {
        return crc32.byte2crc32sum_bytes(array, n);
    }
}
