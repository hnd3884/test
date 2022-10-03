package sun.security.krb5.internal.crypto;

import sun.security.krb5.internal.KrbApErrException;
import sun.security.krb5.Confounder;
import sun.security.krb5.KrbCryptoException;

abstract class DesCbcEType extends EType
{
    protected abstract byte[] calculateChecksum(final byte[] p0, final int p1) throws KrbCryptoException;
    
    @Override
    public int blockSize() {
        return 8;
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
    public byte[] encrypt(final byte[] array, final byte[] array2, final int n) throws KrbCryptoException {
        return this.encrypt(array, array2, new byte[this.keySize()], n);
    }
    
    @Override
    public byte[] encrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n) throws KrbCryptoException {
        if (array2.length > 8) {
            throw new KrbCryptoException("Invalid DES Key!");
        }
        final int n2 = array.length + this.confounderSize() + this.checksumSize();
        byte[] array4;
        byte b;
        if (n2 % this.blockSize() == 0) {
            array4 = new byte[n2 + this.blockSize()];
            b = 8;
        }
        else {
            array4 = new byte[n2 + this.blockSize() - n2 % this.blockSize()];
            b = (byte)(this.blockSize() - n2 % this.blockSize());
        }
        for (int i = n2; i < array4.length; ++i) {
            array4[i] = b;
        }
        System.arraycopy(Confounder.bytes(this.confounderSize()), 0, array4, 0, this.confounderSize());
        System.arraycopy(array, 0, array4, this.startOfData(), array.length);
        System.arraycopy(this.calculateChecksum(array4, array4.length), 0, array4, this.startOfChecksum(), this.checksumSize());
        final byte[] array5 = new byte[array4.length];
        Des.cbc_encrypt(array4, array5, array2, array3, true);
        return array5;
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final byte[] array2, final int n) throws KrbApErrException, KrbCryptoException {
        return this.decrypt(array, array2, new byte[this.keySize()], n);
    }
    
    @Override
    public byte[] decrypt(final byte[] array, final byte[] array2, final byte[] array3, final int n) throws KrbApErrException, KrbCryptoException {
        if (array2.length > 8) {
            throw new KrbCryptoException("Invalid DES Key!");
        }
        final byte[] array4 = new byte[array.length];
        Des.cbc_encrypt(array, array4, array2, array3, false);
        if (!this.isChecksumValid(array4)) {
            throw new KrbApErrException(31);
        }
        return array4;
    }
    
    private void copyChecksumField(final byte[] array, final byte[] array2) {
        for (int i = 0; i < this.checksumSize(); ++i) {
            array[this.startOfChecksum() + i] = array2[i];
        }
    }
    
    private byte[] checksumField(final byte[] array) {
        final byte[] array2 = new byte[this.checksumSize()];
        for (int i = 0; i < this.checksumSize(); ++i) {
            array2[i] = array[this.startOfChecksum() + i];
        }
        return array2;
    }
    
    private void resetChecksumField(final byte[] array) {
        for (int i = this.startOfChecksum(); i < this.startOfChecksum() + this.checksumSize(); ++i) {
            array[i] = 0;
        }
    }
    
    private byte[] generateChecksum(final byte[] array) throws KrbCryptoException {
        final byte[] checksumField = this.checksumField(array);
        this.resetChecksumField(array);
        final byte[] calculateChecksum = this.calculateChecksum(array, array.length);
        this.copyChecksumField(array, checksumField);
        return calculateChecksum;
    }
    
    private boolean isChecksumEqual(final byte[] array, final byte[] array2) {
        if (array == array2) {
            return true;
        }
        if ((array == null && array2 != null) || (array != null && array2 == null)) {
            return false;
        }
        if (array.length != array2.length) {
            return false;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != array2[i]) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isChecksumValid(final byte[] array) throws KrbCryptoException {
        return this.isChecksumEqual(this.checksumField(array), this.generateChecksum(array));
    }
}
