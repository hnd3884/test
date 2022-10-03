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

public final class Null extends CipherSpi
{
    private static final int BLOCK_SIZE = 1;
    
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    protected final void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        if (mode.equalsIgnoreCase("ECB")) {
            return;
        }
        throw new NoSuchAlgorithmException();
    }
    
    protected final void engineSetPadding(final String padding) throws NoSuchPaddingException {
        if (padding.equalsIgnoreCase("None") || padding.equalsIgnoreCase("NoPadding")) {
            return;
        }
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
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.engineInit(opmode, key, random);
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameters params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.engineInit(opmode, key, random);
    }
    
    protected final int engineUpdate(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException {
        if (output.length - outputOffset < inputLen) {
            throw new ShortBufferException();
        }
        return this.internalUpdate(input, inputOffset, inputLen, output, outputOffset);
    }
    
    protected final byte[] engineUpdate(final byte[] input, final int inputOffset, final int inputLen) {
        final byte[] tmp = new byte[this.engineGetOutputSize(inputLen)];
        this.internalUpdate(input, inputOffset, inputLen, tmp, 0);
        return tmp;
    }
    
    protected final int engineDoFinal(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.engineUpdate(input, inputOffset, inputLen, output, outputOffset);
    }
    
    protected final byte[] engineDoFinal(final byte[] input, final int inputOffset, final int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        final byte[] tmp = new byte[this.engineGetOutputSize(inputLen)];
        this.internalUpdate(input, inputOffset, inputLen, tmp, 0);
        return tmp;
    }
    
    private final int internalUpdate(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) {
        System.arraycopy(input, inputOffset, output, outputOffset, inputLen);
        return inputLen;
    }
}
