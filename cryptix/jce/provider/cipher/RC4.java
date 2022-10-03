package cryptix.jce.provider.cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Key;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.CipherSpi;

public final class RC4 extends CipherSpi
{
    private static final int BLOCK_SIZE = 1;
    private final int[] sBox;
    private int x;
    private int y;
    
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    protected final void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        throw new NoSuchAlgorithmException();
    }
    
    protected final void engineSetPadding(final String padding) throws NoSuchPaddingException {
        throw new NoSuchPaddingException();
    }
    
    public int engineGetBlockSize() {
        return 1;
    }
    
    protected final int engineGetOutputSize(final int inputLen) {
        return inputLen;
    }
    
    protected final byte[] engineGetIV() {
        return null;
    }
    
    protected final AlgorithmParameters engineGetParameters() {
        return null;
    }
    
    protected final void engineInit(final int opmode, final Key key, final SecureRandom random) throws InvalidKeyException {
        this.makeKey(key);
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.engineInit(opmode, key, random);
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameters params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.engineInit(opmode, key, random);
    }
    
    protected final int engineUpdate(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException {
        final int bufSize = output.length - outputOffset;
        if (bufSize < inputLen) {
            throw new ShortBufferException();
        }
        return this.privateEngineUpdate(input, inputOffset, inputLen, output, outputOffset);
    }
    
    protected final byte[] engineUpdate(final byte[] input, final int inputOffset, final int inputLen) {
        final byte[] tmp = new byte[this.engineGetOutputSize(inputLen)];
        this.privateEngineUpdate(input, inputOffset, inputLen, tmp, 0);
        return tmp;
    }
    
    private final int privateEngineUpdate(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) {
        this.rc4(input, inputOffset, inputLen, output, outputOffset);
        return inputLen;
    }
    
    protected final int engineDoFinal(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.engineUpdate(input, inputOffset, inputLen, output, outputOffset);
    }
    
    protected final byte[] engineDoFinal(final byte[] input, final int inputOffset, final int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        return this.engineUpdate(input, inputOffset, inputLen);
    }
    
    private void rc4(final byte[] in, int inOffset, final int inLen, final byte[] out, int outOffset) {
        for (int i = 0; i < inLen; ++i) {
            this.x = (this.x + 1 & 0xFF);
            this.y = (this.sBox[this.x] + this.y & 0xFF);
            final int t = this.sBox[this.x];
            this.sBox[this.x] = this.sBox[this.y];
            this.sBox[this.y] = t;
            final int xorIndex = this.sBox[this.x] + this.sBox[this.y] & 0xFF;
            out[outOffset++] = (byte)(in[inOffset++] ^ this.sBox[xorIndex]);
        }
    }
    
    private void makeKey(final Key key) throws InvalidKeyException {
        final byte[] userkey = key.getEncoded();
        if (userkey == null) {
            throw new InvalidKeyException("Null user key");
        }
        final int len = userkey.length;
        if (len == 0) {
            throw new InvalidKeyException("Invalid user key length");
        }
        final int n = 0;
        this.y = n;
        this.x = n;
        for (int i = 0; i < 256; ++i) {
            this.sBox[i] = i;
        }
        int i2 = 0;
        int i3 = 0;
        for (int j = 0; j < 256; ++j) {
            i3 = ((userkey[i2] & 0xFF) + this.sBox[j] + i3 & 0xFF);
            final int t = this.sBox[j];
            this.sBox[j] = this.sBox[i3];
            this.sBox[i3] = t;
            i2 = (i2 + 1) % len;
        }
    }
    
    public RC4() {
        this.sBox = new int[256];
    }
}
