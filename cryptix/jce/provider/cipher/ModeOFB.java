package cryptix.jce.provider.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;

final class ModeOFB extends Mode
{
    private final byte[] keyStreamBuf;
    private int keyStreamBufOffset;
    private byte[] IV;
    
    final int coreGetOutputSize(final int inputLen) {
        return inputLen;
    }
    
    void coreInit(final boolean decrypt, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        super.cipher.coreInit(key, false);
        final IvParameterSpec iv = (IvParameterSpec)params;
        System.arraycopy(this.IV = iv.getIV(), 0, this.keyStreamBuf, 0, super.CIPHER_BLOCK_SIZE);
        super.cipher.coreCrypt(this.keyStreamBuf, 0, this.keyStreamBuf, 0);
        this.keyStreamBufOffset = 0;
    }
    
    final int coreUpdate(final byte[] input, int inputOffset, final int inputLen, final byte[] output, int outputOffset) {
        int todo = inputLen;
        while (todo-- > 0) {
            if (this.keyStreamBufOffset >= super.CIPHER_BLOCK_SIZE) {
                super.cipher.coreCrypt(this.keyStreamBuf, 0, this.keyStreamBuf, 0);
                this.keyStreamBufOffset = 0;
            }
            output[outputOffset++] = (byte)(input[inputOffset++] ^ this.keyStreamBuf[this.keyStreamBufOffset++]);
        }
        return inputLen;
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
        return false;
    }
    
    ModeOFB(final BlockCipher cipher) {
        super(cipher);
        this.IV = null;
        this.keyStreamBuf = new byte[super.CIPHER_BLOCK_SIZE];
    }
}
