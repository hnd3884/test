package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.Wrapper;

public class RFC3211WrapEngine implements Wrapper
{
    private CBCBlockCipher engine;
    private ParametersWithIV param;
    private boolean forWrapping;
    private SecureRandom rand;
    
    public RFC3211WrapEngine(final BlockCipher blockCipher) {
        this.engine = new CBCBlockCipher(blockCipher);
    }
    
    public void init(final boolean forWrapping, final CipherParameters cipherParameters) {
        this.forWrapping = forWrapping;
        if (cipherParameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)cipherParameters;
            this.rand = parametersWithRandom.getRandom();
            this.param = (ParametersWithIV)parametersWithRandom.getParameters();
        }
        else {
            if (forWrapping) {
                this.rand = new SecureRandom();
            }
            this.param = (ParametersWithIV)cipherParameters;
        }
    }
    
    public String getAlgorithmName() {
        return this.engine.getUnderlyingCipher().getAlgorithmName() + "/RFC3211Wrap";
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("not set for wrapping");
        }
        this.engine.init(true, this.param);
        final int blockSize = this.engine.getBlockSize();
        byte[] array2;
        if (n2 + 4 < blockSize * 2) {
            array2 = new byte[blockSize * 2];
        }
        else {
            array2 = new byte[((n2 + 4) % blockSize == 0) ? (n2 + 4) : (((n2 + 4) / blockSize + 1) * blockSize)];
        }
        array2[0] = (byte)n2;
        array2[1] = (byte)~array[n];
        array2[2] = (byte)~array[n + 1];
        array2[3] = (byte)~array[n + 2];
        System.arraycopy(array, n, array2, 4, n2);
        final byte[] array3 = new byte[array2.length - (n2 + 4)];
        this.rand.nextBytes(array3);
        System.arraycopy(array3, 0, array2, n2 + 4, array3.length);
        for (int i = 0; i < array2.length; i += blockSize) {
            this.engine.processBlock(array2, i, array2, i);
        }
        for (int j = 0; j < array2.length; j += blockSize) {
            this.engine.processBlock(array2, j, array2, j);
        }
        return array2;
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("not set for unwrapping");
        }
        final int blockSize = this.engine.getBlockSize();
        if (n2 < 2 * blockSize) {
            throw new InvalidCipherTextException("input too short");
        }
        final byte[] array2 = new byte[n2];
        final byte[] array3 = new byte[blockSize];
        System.arraycopy(array, n, array2, 0, n2);
        System.arraycopy(array, n, array3, 0, array3.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), array3));
        for (int i = blockSize; i < array2.length; i += blockSize) {
            this.engine.processBlock(array2, i, array2, i);
        }
        System.arraycopy(array2, array2.length - array3.length, array3, 0, array3.length);
        this.engine.init(false, new ParametersWithIV(this.param.getParameters(), array3));
        this.engine.processBlock(array2, 0, array2, 0);
        this.engine.init(false, this.param);
        for (int j = 0; j < array2.length; j += blockSize) {
            this.engine.processBlock(array2, j, array2, j);
        }
        if ((array2[0] & 0xFF) > array2.length - 4) {
            throw new InvalidCipherTextException("wrapped key corrupted");
        }
        final byte[] array4 = new byte[array2[0] & 0xFF];
        System.arraycopy(array2, 4, array4, 0, array2[0]);
        int n3 = 0;
        for (int k = 0; k != 3; ++k) {
            n3 |= ((byte)~array2[1 + k] ^ array4[k]);
        }
        if (n3 != 0) {
            throw new InvalidCipherTextException("wrapped key fails checksum");
        }
        return array4;
    }
}
