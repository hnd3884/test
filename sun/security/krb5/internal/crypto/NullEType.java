package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.KrbApErrException;

public class NullEType extends EType
{
    @Override
    public int eType() {
        return 0;
    }
    
    @Override
    public int minimumPadSize() {
        return 0;
    }
    
    @Override
    public int confounderSize() {
        return 0;
    }
    
    @Override
    public int checksumType() {
        return 0;
    }
    
    @Override
    public int checksumSize() {
        return 0;
    }
    
    @Override
    public int blockSize() {
        return 1;
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
    public byte[] encrypt(final byte[] array, final byte[] array2, final int n) {
        final byte[] array3 = new byte[array.length];
        System.arraycopy(array, 0, array3, 0, array.length);
        return array3;
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n) {
        final byte[] array4 = new byte[array.length];
        System.arraycopy(array, 0, array4, 0, array.length);
        return array4;
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final byte[] array2, final int n) throws KrbApErrException {
        return array.clone();
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n) throws KrbApErrException {
        return array.clone();
    }
}
