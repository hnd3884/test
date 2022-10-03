package cryptix.jce.provider.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;

final class ModeCBC extends Mode
{
    private final byte[] buf;
    private final byte[] prevBlock;
    private byte[] IV;
    
    final int coreGetOutputSize(final int inputLen) {
        return (super.bufCount + inputLen) / super.CIPHER_BLOCK_SIZE * super.CIPHER_BLOCK_SIZE;
    }
    
    final void coreInit(final boolean decrypt, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        super.cipher.coreInit(key, decrypt);
        if (decrypt) {
            final IvParameterSpec iv = (IvParameterSpec)params;
            System.arraycopy(this.IV = iv.getIV(), 0, this.prevBlock, 0, super.CIPHER_BLOCK_SIZE);
            super.bufCount = 0;
        }
        else {
            final IvParameterSpec iv = (IvParameterSpec)params;
            System.arraycopy(this.IV = iv.getIV(), 0, this.buf, 0, super.CIPHER_BLOCK_SIZE);
            super.bufCount = 0;
        }
    }
    
    int coreUpdate(final byte[] input, int inputOffset, int inputLen, final byte[] output, int outputOffset) {
        if (super.decrypt) {
            int ret;
            int n;
            for (ret = 0; inputLen >= (n = super.CIPHER_BLOCK_SIZE - super.bufCount); inputLen -= super.CIPHER_BLOCK_SIZE, ret += super.CIPHER_BLOCK_SIZE, super.bufCount = 0) {
                for (int i = 0; i < n; ++i) {
                    this.buf[super.bufCount++] = input[inputOffset++];
                }
                super.cipher.coreCrypt(this.buf, 0, output, outputOffset);
                for (int i = 0; i < super.CIPHER_BLOCK_SIZE; ++i) {
                    final int n2 = outputOffset++;
                    output[n2] ^= this.prevBlock[i];
                }
                for (int i = 0; i < super.CIPHER_BLOCK_SIZE; ++i) {
                    this.prevBlock[i] = this.buf[i];
                }
            }
            for (int i = 0; i < inputLen; ++i) {
                this.buf[super.bufCount++] = input[inputOffset++];
            }
            return ret;
        }
        int ret;
        int n3;
        for (ret = 0; inputLen >= (n3 = super.CIPHER_BLOCK_SIZE - super.bufCount); inputLen -= n3, outputOffset += super.CIPHER_BLOCK_SIZE, ret += super.CIPHER_BLOCK_SIZE, super.bufCount = 0) {
            for (int i = 0; i < n3; ++i) {
                final byte[] buf = this.buf;
                final int n4 = super.bufCount++;
                buf[n4] ^= input[inputOffset++];
            }
            super.cipher.coreCrypt(this.buf, 0, this.buf, 0);
            System.arraycopy(this.buf, 0, output, outputOffset, super.CIPHER_BLOCK_SIZE);
        }
        for (int i = 0; i < inputLen; ++i) {
            final byte[] buf2 = this.buf;
            final int n5 = super.bufCount++;
            buf2[n5] ^= input[inputOffset++];
        }
        return ret;
    }
    
    final byte[] coreGetIV() {
        return this.IV;
    }
    
    final AlgorithmParameterSpec coreGetParamSpec() {
        if (this.IV == null) {
            return new IvParameterSpec(this.generateIV());
        }
        return new IvParameterSpec(this.IV);
    }
    
    final boolean needsPadding() {
        return true;
    }
    
    ModeCBC(final BlockCipher cipher) {
        super(cipher);
        this.IV = null;
        this.buf = new byte[super.CIPHER_BLOCK_SIZE];
        this.prevBlock = new byte[super.CIPHER_BLOCK_SIZE];
    }
}
