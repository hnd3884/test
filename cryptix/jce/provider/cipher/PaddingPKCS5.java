package cryptix.jce.provider.cipher;

import javax.crypto.IllegalBlockSizeException;

final class PaddingPKCS5 extends Padding
{
    final byte[] corePad(byte[] input, final int inputLen) throws IllegalBlockSizeException {
        if (input == null) {
            input = new byte[0];
        }
        final int pad = this.getPadSize(inputLen);
        final byte[] b = new byte[pad + inputLen];
        System.arraycopy(input, 0, b, 0, inputLen);
        for (int i = 0; i < pad; ++i) {
            b[inputLen + i] = (byte)pad;
        }
        return b;
    }
    
    final int coreUnPad(final byte[] input, final int inputLen) {
        return inputLen - input[inputLen - 1];
    }
    
    final int getPadSize(final int inputLen) {
        final int bs = this.getBlockSize();
        return bs - (inputLen + this.getBufSize()) % bs;
    }
    
    PaddingPKCS5(final Mode mode) {
        super(mode);
    }
}
