package cryptix.jce.provider.cipher;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

abstract class Mode
{
    protected final BlockCipher cipher;
    protected final int CIPHER_BLOCK_SIZE;
    protected boolean decrypt;
    protected int bufCount;
    
    static Mode getInstance(final String mode, final BlockCipher cipher) throws NoSuchAlgorithmException {
        try {
            if (mode.equalsIgnoreCase("CBC")) {
                return new ModeCBC(cipher);
            }
            if (mode.substring(0, 3).equalsIgnoreCase("CFB")) {
                final String fbs = mode.substring(3, mode.length());
                if (fbs.length() > 0) {
                    return new ModeCFB(cipher, Integer.parseInt(fbs));
                }
                return new ModeCFB(cipher);
            }
            else {
                if (mode.equalsIgnoreCase("ECB")) {
                    return new ModeECB(cipher);
                }
                if (mode.equalsIgnoreCase("OFB")) {
                    return new ModeOFB(cipher);
                }
                if (mode.equalsIgnoreCase("openpgpCFB")) {
                    return new ModeOpenpgpCFB(cipher);
                }
            }
        }
        catch (final IndexOutOfBoundsException ex) {}
        throw new NoSuchAlgorithmException("Mode (" + mode + ") not available.");
    }
    
    void init(final boolean decrypt, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        this.coreInit(this.decrypt = decrypt, key, params, random);
    }
    
    final byte[] getIV() {
        return this.coreGetIV();
    }
    
    final AlgorithmParameterSpec getParamSpec() {
        return this.coreGetParamSpec();
    }
    
    final int getOutputSize(final int inputLen) {
        return this.coreGetOutputSize(inputLen);
    }
    
    final int getBlockSize() {
        return this.CIPHER_BLOCK_SIZE;
    }
    
    final int update(final byte[] input, final int inputOffset, final int inputLen, final byte[] output, final int outputOffset) {
        return this.coreUpdate(input, inputOffset, inputLen, output, outputOffset);
    }
    
    final int getBufSize() {
        return this.bufCount;
    }
    
    protected byte[] generateIV() {
        final byte[] b = new byte[this.CIPHER_BLOCK_SIZE];
        final SecureRandom sr = new SecureRandom();
        sr.nextBytes(b);
        return b;
    }
    
    abstract int coreGetOutputSize(final int p0);
    
    abstract void coreInit(final boolean p0, final Key p1, final AlgorithmParameterSpec p2, final SecureRandom p3) throws InvalidKeyException, InvalidAlgorithmParameterException;
    
    abstract int coreUpdate(final byte[] p0, final int p1, final int p2, final byte[] p3, final int p4);
    
    abstract byte[] coreGetIV();
    
    abstract AlgorithmParameterSpec coreGetParamSpec();
    
    abstract boolean needsPadding();
    
    Mode(final BlockCipher cipher) {
        this.cipher = cipher;
        this.CIPHER_BLOCK_SIZE = cipher.coreGetBlockSize();
    }
}
