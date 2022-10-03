package cryptix.jce.provider.cipher;

import javax.crypto.IllegalBlockSizeException;

final class PaddingNone extends Padding
{
    private final boolean needsPadding;
    
    final byte[] corePad(byte[] input, final int inputLen) throws IllegalBlockSizeException {
        if (input == null) {
            input = new byte[0];
        }
        if ((this.getBufSize() != 0 || inputLen % this.getBlockSize() != 0) && this.needsPadding) {
            throw new IllegalBlockSizeException("Input buffer not a multiple of BlockSize");
        }
        final byte[] t = new byte[inputLen];
        System.arraycopy(input, 0, t, 0, inputLen);
        return t;
    }
    
    final int coreUnPad(final byte[] input, final int inputLen) {
        return inputLen;
    }
    
    final int getPadSize(final int inputLen) {
        return 0;
    }
    
    PaddingNone(final Mode mode) {
        super(mode);
        this.needsPadding = mode.needsPadding();
    }
}
