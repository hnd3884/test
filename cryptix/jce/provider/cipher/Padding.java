package cryptix.jce.provider.cipher;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.NoSuchPaddingException;

abstract class Padding
{
    private final Mode mode;
    private byte[] scratchBuf;
    private int blSize;
    private boolean isBuffered;
    protected boolean decrypt;
    
    static Padding getInstance(final String padding, final Mode mode) throws NoSuchPaddingException {
        if (padding.equalsIgnoreCase("None") || padding.equalsIgnoreCase("NoPadding")) {
            return new PaddingNone(mode);
        }
        if (padding.equalsIgnoreCase("PKCS5") || padding.equalsIgnoreCase("PKCS#5") || padding.equalsIgnoreCase("PKCS5Padding") || padding.equalsIgnoreCase("PKCS7") || padding.equalsIgnoreCase("PKCS#7")) {
            return new PaddingPKCS5(mode);
        }
        throw new NoSuchPaddingException("Padding not available [" + padding + "]");
    }
    
    final int getBlockSize() {
        return this.mode.getBlockSize();
    }
    
    final int getOutputSize(final int inputLen) {
        return this.mode.getOutputSize(inputLen + this.getPadSize(inputLen));
    }
    
    final AlgorithmParameterSpec getParamSpec() {
        return this.mode.getParamSpec();
    }
    
    final byte[] getIV() {
        return this.mode.getIV();
    }
    
    final void init(final boolean decrypt, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.mode.init(this.decrypt = decrypt, key, params, random);
    }
    
    final int update(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException {
        if (output.length < this.getOutputSize(inputLen)) {
            throw new ShortBufferException("The output buffer is too short");
        }
        if (this.decrypt) {
            int i = 0;
            if (!this.isBuffered) {
                i = this.mode.update(input, inputOffset, inputLen - this.blSize, output, outputOffset);
                System.arraycopy(input, inputOffset + (inputLen - this.blSize), this.scratchBuf, 0, this.blSize);
                this.isBuffered = true;
            }
            else {
                i = this.mode.update(this.scratchBuf, 0, this.blSize, output, outputOffset);
                System.arraycopy(input, inputOffset + (inputLen - this.blSize), this.scratchBuf, 0, this.blSize);
                i += this.mode.update(input, inputOffset, inputLen - this.blSize, output, outputOffset + this.blSize);
            }
            return i;
        }
        return this.mode.update(input, inputOffset, inputLen, output, outputOffset);
    }
    
    final int doFinal(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        if (output.length < this.getOutputSize(inputLen)) {
            throw new ShortBufferException("The output buffer is too short");
        }
        if (!this.decrypt) {
            final byte[] t = this.corePad(input, inputLen);
            return this.mode.update(t, inputOffset, t.length, output, outputOffset);
        }
        if (input == null && !this.isBuffered) {
            return 0;
        }
        if (input != null && inputLen < this.getPadSize(inputLen)) {
            throw new BadPaddingException("Input data not bounded by the padding size");
        }
        int i = 0;
        if (this.isBuffered) {
            i = this.mode.update(this.scratchBuf, 0, this.blSize, output, outputOffset);
            if (input != null) {
                i += this.mode.update(input, inputOffset, inputLen, output, outputOffset + this.blSize);
            }
        }
        else {
            i = this.mode.update(input, inputOffset, inputLen, output, outputOffset);
        }
        this.isBuffered = false;
        return this.coreUnPad(output, i);
    }
    
    protected int getBufSize() {
        return this.mode.getBufSize();
    }
    
    abstract byte[] corePad(final byte[] p0, final int p1) throws IllegalBlockSizeException;
    
    abstract int coreUnPad(final byte[] p0, final int p1);
    
    abstract int getPadSize(final int p0);
    
    Padding(final Mode mode) {
        this.mode = mode;
        this.blSize = this.getBlockSize();
        this.scratchBuf = new byte[this.blSize];
        this.isBuffered = false;
    }
}
