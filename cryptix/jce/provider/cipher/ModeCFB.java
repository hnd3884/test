package cryptix.jce.provider.cipher;

import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import javax.crypto.spec.IvParameterSpec;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;

class ModeCFB extends Mode
{
    private final byte[] keyStreamBuf;
    private int keyStreamPtr;
    private final byte[] shiftReg;
    private int shiftRegPtr;
    protected long byteCount;
    private int feedbackSize;
    private boolean decrypt;
    private byte[] iVec;
    
    private void shiftInByte(final byte b) {
        this.shiftReg[this.shiftRegPtr++ % super.CIPHER_BLOCK_SIZE] = b;
        ++this.byteCount;
        if (this.needCrank()) {
            this.crank();
        }
    }
    
    private void crank() {
        for (int i = 0; i < super.CIPHER_BLOCK_SIZE; ++i) {
            this.keyStreamBuf[i] = this.shiftReg[this.shiftRegPtr++ % super.CIPHER_BLOCK_SIZE];
        }
        super.cipher.coreCrypt(this.keyStreamBuf, 0, this.keyStreamBuf, 0);
        this.keyStreamPtr = 0;
    }
    
    protected boolean needCrank() {
        return this.byteCount % this.feedbackSize == 0L;
    }
    
    final int coreGetOutputSize(final int inputLen) {
        return inputLen;
    }
    
    void coreInit(final boolean decrypt, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        super.cipher.coreInit(key, false);
        this.decrypt = decrypt;
        final IvParameterSpec iv = (IvParameterSpec)params;
        this.iVec = iv.getIV();
        final int iVecLen = this.iVec.length;
        if (iVecLen != super.CIPHER_BLOCK_SIZE) {
            throw new InvalidAlgorithmParameterException("Invalid IV specified, incorrect length.");
        }
        this.byteCount = 0L;
        System.arraycopy(this.iVec, 0, this.shiftReg, 0, iVecLen);
        this.crank();
    }
    
    int coreUpdate(final byte[] input, int inputOffset, final int inputLen, final byte[] output, int outputOffset) {
        int todo = inputLen;
        while (todo-- > 0) {
            final byte kb = this.keyStreamBuf[this.keyStreamPtr++];
            final byte ib = input[inputOffset++];
            final byte ob = (byte)(ib ^ kb);
            this.shiftInByte(this.decrypt ? ib : ob);
            output[outputOffset++] = ob;
        }
        return inputLen;
    }
    
    final byte[] coreGetIV() {
        return this.iVec;
    }
    
    final AlgorithmParameterSpec coreGetParamSpec() {
        if (this.iVec == null) {
            return new IvParameterSpec(this.generateIV());
        }
        return new IvParameterSpec(this.iVec);
    }
    
    final boolean needsPadding() {
        return false;
    }
    
    ModeCFB(final BlockCipher cipher) {
        super(cipher);
        this.iVec = null;
        this.keyStreamBuf = new byte[super.CIPHER_BLOCK_SIZE];
        this.shiftReg = new byte[super.CIPHER_BLOCK_SIZE];
        this.feedbackSize = super.CIPHER_BLOCK_SIZE;
    }
    
    ModeCFB(final BlockCipher cipher, int feedbackSize) throws NoSuchAlgorithmException {
        super(cipher);
        this.iVec = null;
        if (feedbackSize == 0 || feedbackSize % 8 != 0) {
            throw new NoSuchAlgorithmException("Feedback size is 0 or not a multiple of 8 bits.");
        }
        feedbackSize /= 8;
        if (feedbackSize < 1 || feedbackSize > super.CIPHER_BLOCK_SIZE) {
            throw new NoSuchAlgorithmException("Feedback size <1 or >CIPHER_BLOCK_SIZE");
        }
        this.keyStreamBuf = new byte[super.CIPHER_BLOCK_SIZE];
        this.shiftReg = new byte[super.CIPHER_BLOCK_SIZE];
        this.feedbackSize = feedbackSize;
    }
}
