package sun.security.krb5.internal.crypto;

import java.security.MessageDigest;
import sun.security.krb5.KrbCryptoException;
import java.security.InvalidKeyException;
import javax.crypto.spec.DESKeySpec;
import sun.security.krb5.Confounder;

public final class RsaMd5DesCksumType extends CksumType
{
    @Override
    public int confounderSize() {
        return 8;
    }
    
    @Override
    public int cksumType() {
        return 8;
    }
    
    @Override
    public boolean isKeyed() {
        return true;
    }
    
    @Override
    public int cksumSize() {
        return 24;
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
        final byte[] array3 = new byte[n + this.confounderSize()];
        final byte[] bytes = Confounder.bytes(this.confounderSize());
        System.arraycopy(bytes, 0, array3, 0, this.confounderSize());
        System.arraycopy(array, 0, array3, this.confounderSize(), n);
        final byte[] calculateRawChecksum = this.calculateRawChecksum(array3, array3.length);
        final byte[] array4 = new byte[this.cksumSize()];
        System.arraycopy(bytes, 0, array4, 0, this.confounderSize());
        System.arraycopy(calculateRawChecksum, 0, array4, this.confounderSize(), this.cksumSize() - this.confounderSize());
        final byte[] array5 = new byte[this.keySize()];
        System.arraycopy(array2, 0, array5, 0, array2.length);
        for (int i = 0; i < array5.length; ++i) {
            array5[i] ^= (byte)240;
        }
        try {
            if (DESKeySpec.isWeak(array5, 0)) {
                array5[7] ^= (byte)240;
            }
        }
        catch (final InvalidKeyException ex) {}
        final byte[] array6 = new byte[array5.length];
        final byte[] array7 = new byte[array4.length];
        Des.cbc_encrypt(array4, array7, array5, array6, true);
        return array7;
    }
    
    @Override
    public boolean verifyChecksum(final byte[] array, final int n, final byte[] array2, final byte[] array3, final int n2) throws KrbCryptoException {
        final byte[] decryptKeyedChecksum = this.decryptKeyedChecksum(array3, array2);
        final byte[] array4 = new byte[n + this.confounderSize()];
        System.arraycopy(decryptKeyedChecksum, 0, array4, 0, this.confounderSize());
        System.arraycopy(array, 0, array4, this.confounderSize(), n);
        final byte[] calculateRawChecksum = this.calculateRawChecksum(array4, array4.length);
        final byte[] array5 = new byte[this.cksumSize() - this.confounderSize()];
        System.arraycopy(decryptKeyedChecksum, this.confounderSize(), array5, 0, this.cksumSize() - this.confounderSize());
        return CksumType.isChecksumEqual(array5, calculateRawChecksum);
    }
    
    private byte[] decryptKeyedChecksum(final byte[] array, final byte[] array2) throws KrbCryptoException {
        final byte[] array3 = new byte[this.keySize()];
        System.arraycopy(array2, 0, array3, 0, array2.length);
        for (int i = 0; i < array3.length; ++i) {
            array3[i] ^= (byte)240;
        }
        try {
            if (DESKeySpec.isWeak(array3, 0)) {
                array3[7] ^= (byte)240;
            }
        }
        catch (final InvalidKeyException ex) {}
        final byte[] array4 = new byte[array3.length];
        final byte[] array5 = new byte[array.length];
        Des.cbc_encrypt(array, array5, array3, array4, false);
        return array5;
    }
    
    private byte[] calculateRawChecksum(final byte[] array, final int n) throws KrbCryptoException {
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
}
