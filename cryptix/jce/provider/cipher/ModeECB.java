package cryptix.jce.provider.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;

final class ModeECB extends Mode
{
    private final byte[] buf;
    
    final int coreGetOutputSize(final int inputLen) {
        return (super.bufCount + inputLen) / super.CIPHER_BLOCK_SIZE * super.CIPHER_BLOCK_SIZE;
    }
    
    final void coreInit(final boolean decrypt, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        super.cipher.coreInit(key, decrypt);
    }
    
    int coreUpdate(final byte[] input, int inputOffset, int inputLen, final byte[] output, int outputOffset) {
        int ret;
        int n;
        for (ret = 0; inputLen >= (n = super.CIPHER_BLOCK_SIZE - super.bufCount); inputLen -= n, inputOffset += n, outputOffset += super.CIPHER_BLOCK_SIZE, ret += super.CIPHER_BLOCK_SIZE, super.bufCount = 0) {
            System.arraycopy(input, inputOffset, this.buf, super.bufCount, n);
            super.cipher.coreCrypt(this.buf, 0, output, outputOffset);
        }
        System.arraycopy(input, inputOffset, this.buf, super.bufCount, inputLen);
        super.bufCount += inputLen;
        return ret;
    }
    
    final byte[] coreGetIV() {
        return null;
    }
    
    final AlgorithmParameterSpec coreGetParamSpec() {
        return null;
    }
    
    final boolean needsPadding() {
        return true;
    }
    
    ModeECB(final BlockCipher cipher) {
        super(cipher);
        this.buf = new byte[super.CIPHER_BLOCK_SIZE];
    }
}
