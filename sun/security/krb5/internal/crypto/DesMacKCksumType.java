package sun.security.krb5.internal.crypto;

import sun.security.krb5.KrbCryptoException;
import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;

public class DesMacKCksumType extends CksumType
{
    @Override
    public int confounderSize() {
        return 0;
    }
    
    @Override
    public int cksumType() {
        return 5;
    }
    
    @Override
    public boolean isKeyed() {
        return true;
    }
    
    @Override
    public int cksumSize() {
        return 16;
    }
    
    @Override
    public int keyType() {
        return 1;
    }
    
    @Override
    public int keySize() {
        return 8;
    }
    
    @Override
    public byte[] calculateChecksum(final byte[] array, final int n, final byte[] array2, final int n2) throws KrbCryptoException {
        try {
            if (DESKeySpec.isWeak(array2, 0)) {
                array2[7] ^= (byte)240;
            }
        }
        catch (final InvalidKeyException ex) {}
        final byte[] array3 = new byte[array2.length];
        System.arraycopy(array2, 0, array3, 0, array2.length);
        return Des.des_cksum(array3, array, array2);
    }
    
    @Override
    public boolean verifyChecksum(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2) throws KrbCryptoException {
        return CksumType.isChecksumEqual(array3, this.calculateChecksum(array, array.length, array2, n2));
    }
}
