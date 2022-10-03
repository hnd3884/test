package org.bouncycastle.crypto.modes;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class CBCBlockCipher implements BlockCipher
{
    private byte[] IV;
    private byte[] cbcV;
    private byte[] cbcNextV;
    private int blockSize;
    private BlockCipher cipher;
    private boolean encrypting;
    
    public CBCBlockCipher(final BlockCipher cipher) {
        this.cipher = null;
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.IV = new byte[this.blockSize];
        this.cbcV = new byte[this.blockSize];
        this.cbcNextV = new byte[this.blockSize];
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public void init(final boolean encrypting, final CipherParameters cipherParameters) throws IllegalArgumentException {
        final boolean encrypting2 = this.encrypting;
        this.encrypting = encrypting;
        if (cipherParameters instanceof ParametersWithIV) {
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            final byte[] iv = parametersWithIV.getIV();
            if (iv.length != this.blockSize) {
                throw new IllegalArgumentException("initialisation vector must be the same length as block size");
            }
            System.arraycopy(iv, 0, this.IV, 0, iv.length);
            this.reset();
            if (parametersWithIV.getParameters() != null) {
                this.cipher.init(encrypting, parametersWithIV.getParameters());
            }
            else if (encrypting2 != encrypting) {
                throw new IllegalArgumentException("cannot change encrypting state without providing key.");
            }
        }
        else {
            this.reset();
            if (cipherParameters != null) {
                this.cipher.init(encrypting, cipherParameters);
            }
            else if (encrypting2 != encrypting) {
                throw new IllegalArgumentException("cannot change encrypting state without providing key.");
            }
        }
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CBC";
    }
    
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        return this.encrypting ? this.encryptBlock(array, n, array2, n2) : this.decryptBlock(array, n, array2, n2);
    }
    
    public void reset() {
        System.arraycopy(this.IV, 0, this.cbcV, 0, this.IV.length);
        Arrays.fill(this.cbcNextV, (byte)0);
        this.cipher.reset();
    }
    
    private int encryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        for (int i = 0; i < this.blockSize; ++i) {
            final byte[] cbcV = this.cbcV;
            final int n3 = i;
            cbcV[n3] ^= array[n + i];
        }
        final int processBlock = this.cipher.processBlock(this.cbcV, 0, array2, n2);
        System.arraycopy(array2, n2, this.cbcV, 0, this.cbcV.length);
        return processBlock;
    }
    
    private int decryptBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (n + this.blockSize > array.length) {
            throw new DataLengthException("input buffer too short");
        }
        System.arraycopy(array, n, this.cbcNextV, 0, this.blockSize);
        final int processBlock = this.cipher.processBlock(array, n, array2, n2);
        for (int i = 0; i < this.blockSize; ++i) {
            final int n3 = n2 + i;
            array2[n3] ^= this.cbcV[i];
        }
        final byte[] cbcV = this.cbcV;
        this.cbcV = this.cbcNextV;
        this.cbcNextV = cbcV;
        return processBlock;
    }
}
