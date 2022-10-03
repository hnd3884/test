package cryptix.jce.provider.cipher;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.security.InvalidKeyException;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidParameterSpecException;
import java.security.AlgorithmParameters;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import javax.crypto.CipherSpi;

public abstract class BlockCipher extends CipherSpi
{
    private static final int STATE_UNINITIALIZED = 0;
    private static final int STATE_DECRYPT = 1;
    private static final int STATE_ENCRYPT = 2;
    private int state;
    private final int BLOCK_SIZE;
    private Key key;
    private String algorithm;
    private Padding padding;
    private Mode mode;
    
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    protected final void engineSetMode(final String mode) throws NoSuchAlgorithmException {
        this.mode = Mode.getInstance(mode, this);
    }
    
    protected final void engineSetPadding(final String padding) throws NoSuchPaddingException {
        this.padding = Padding.getInstance(padding, this.mode);
    }
    
    protected final int engineGetBlockSize() {
        return this.padding.getBlockSize();
    }
    
    protected final int engineGetOutputSize(final int inputLen) {
        return this.padding.getOutputSize(inputLen);
    }
    
    protected final byte[] engineGetIV() {
        return this.padding.getIV();
    }
    
    protected final AlgorithmParameters engineGetParameters() {
        final AlgorithmParameterSpec aps = this.padding.getParamSpec();
        if (aps == null) {
            return null;
        }
        AlgorithmParameters ap = null;
        try {
            ap = AlgorithmParameters.getInstance(this.algorithm, "CryptixCrypto");
            ap.init(aps);
        }
        catch (final InvalidParameterSpecException ex) {
            throw new RuntimeException("PANIC: Unreachable code reached.");
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new RuntimeException("PANIC: Unreachable code reached.");
        }
        catch (final NoSuchProviderException e) {
            throw new RuntimeException("PANIC: Unreachable code reached.");
        }
        return ap;
    }
    
    protected final void engineInit(final int opmode, final Key key, final SecureRandom random) throws InvalidKeyException {
        final AlgorithmParameterSpec aps = this.padding.getParamSpec();
        try {
            this.engineInit(opmode, key, aps, random);
        }
        catch (final InvalidAlgorithmParameterException e) {
            throw new InternalError("Unreachable code reached.");
        }
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        final boolean decrypt = opmode == 2;
        this.padding.init(decrypt, key, params, random);
    }
    
    protected final void engineInit(final int opmode, final Key key, final AlgorithmParameters params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        throw new RuntimeException("Not supported on this cipher.");
    }
    
    protected final int engineUpdate(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException {
        if (inputLen == 0) {
            return 0;
        }
        return this.padding.update(input, inputOffset, inputLen, output, outputOffset);
    }
    
    protected final byte[] engineUpdate(final byte[] input, final int inputOffset, final int inputLen) {
        if (inputLen == 0) {
            return null;
        }
        try {
            byte[] output = new byte[this.engineGetOutputSize(inputLen)];
            final int i = this.engineUpdate(input, inputOffset, inputLen, output, 0);
            if (i != output.length) {
                final byte[] t = new byte[i];
                System.arraycopy(output, 0, t, 0, i);
                output = t;
            }
            return output;
        }
        catch (final ShortBufferException e) {
            throw new RuntimeException("PANIC: Unreachable code reached.");
        }
    }
    
    protected final int engineDoFinal(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) throws ShortBufferException, IllegalBlockSizeException, BadPaddingException {
        return this.padding.doFinal(input, inputOffset, inputLen, output, outputOffset);
    }
    
    protected final byte[] engineDoFinal(final byte[] input, final int inputOffset, final int inputLen) throws IllegalBlockSizeException, BadPaddingException {
        try {
            byte[] output = new byte[this.engineGetOutputSize(inputLen)];
            final int i = this.engineDoFinal(input, inputOffset, inputLen, output, 0);
            if (i != output.length) {
                final byte[] t = new byte[i];
                System.arraycopy(output, 0, t, 0, i);
                output = t;
            }
            return output;
        }
        catch (final ShortBufferException e) {
            throw new RuntimeException("PANIC: Unreachable code reached.");
        }
    }
    
    abstract void coreInit(final Key p0, final boolean p1) throws InvalidKeyException;
    
    abstract void coreCrypt(final byte[] p0, final int p1, final byte[] p2, final int p3);
    
    int coreGetBlockSize() {
        return this.BLOCK_SIZE;
    }
    
    protected BlockCipher(final int blockSize) {
        this("", blockSize);
    }
    
    protected BlockCipher(final String algorithm, final int blockSize) {
        this.state = 0;
        this.BLOCK_SIZE = blockSize;
        this.algorithm = algorithm;
        try {
            this.mode = Mode.getInstance("ECB", this);
            this.padding = Padding.getInstance("None", this.mode);
        }
        catch (final NoSuchPaddingException ex) {
            throw new InternalError("PANIC: Installation corrupt, default padding not available.");
        }
        catch (final NoSuchAlgorithmException E) {
            throw new InternalError("PANIC: Installation corrupt, default mode not available.");
        }
    }
}
