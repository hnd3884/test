package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.Wrapper;

public class DESedeWrapEngine implements Wrapper
{
    private CBCBlockCipher engine;
    private KeyParameter param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private static final byte[] IV2;
    Digest sha1;
    byte[] digest;
    
    public DESedeWrapEngine() {
        this.sha1 = DigestFactory.createSHA1();
        this.digest = new byte[20];
    }
    
    public void init(final boolean forWrapping, CipherParameters parameters) {
        this.forWrapping = forWrapping;
        this.engine = new CBCBlockCipher(new DESedeEngine());
        SecureRandom random;
        if (parameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)parameters;
            parameters = parametersWithRandom.getParameters();
            random = parametersWithRandom.getRandom();
        }
        else {
            random = new SecureRandom();
        }
        if (parameters instanceof KeyParameter) {
            this.param = (KeyParameter)parameters;
            if (this.forWrapping) {
                random.nextBytes(this.iv = new byte[8]);
                this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
            }
        }
        else if (parameters instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV)parameters;
            this.iv = this.paramPlusIV.getIV();
            this.param = (KeyParameter)this.paramPlusIV.getParameters();
            if (!this.forWrapping) {
                throw new IllegalArgumentException("You should not supply an IV for unwrapping");
            }
            if (this.iv == null || this.iv.length != 8) {
                throw new IllegalArgumentException("IV is not 8 octets");
            }
        }
    }
    
    public String getAlgorithmName() {
        return "DESede";
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        final byte[] calculateCMSKeyChecksum = this.calculateCMSKeyChecksum(array2);
        final byte[] array3 = new byte[array2.length + calculateCMSKeyChecksum.length];
        System.arraycopy(array2, 0, array3, 0, array2.length);
        System.arraycopy(calculateCMSKeyChecksum, 0, array3, array2.length, calculateCMSKeyChecksum.length);
        final int blockSize = this.engine.getBlockSize();
        if (array3.length % blockSize != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        final byte[] array4 = new byte[array3.length];
        for (int i = 0; i != array3.length; i += blockSize) {
            this.engine.processBlock(array3, i, array4, i);
        }
        final byte[] array5 = new byte[this.iv.length + array4.length];
        System.arraycopy(this.iv, 0, array5, 0, this.iv.length);
        System.arraycopy(array4, 0, array5, this.iv.length, array4.length);
        final byte[] reverse = reverse(array5);
        this.engine.init(true, new ParametersWithIV(this.param, DESedeWrapEngine.IV2));
        for (int j = 0; j != reverse.length; j += blockSize) {
            this.engine.processBlock(reverse, j, reverse, j);
        }
        return reverse;
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (array == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        final int blockSize = this.engine.getBlockSize();
        if (n2 % blockSize != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + blockSize);
        }
        this.engine.init(false, new ParametersWithIV(this.param, DESedeWrapEngine.IV2));
        final byte[] array2 = new byte[n2];
        for (int i = 0; i != n2; i += blockSize) {
            this.engine.processBlock(array, n + i, array2, i);
        }
        final byte[] reverse = reverse(array2);
        this.iv = new byte[8];
        final byte[] array3 = new byte[reverse.length - 8];
        System.arraycopy(reverse, 0, this.iv, 0, 8);
        System.arraycopy(reverse, 8, array3, 0, reverse.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        final byte[] array4 = new byte[array3.length];
        for (int j = 0; j != array4.length; j += blockSize) {
            this.engine.processBlock(array3, j, array4, j);
        }
        final byte[] array5 = new byte[array4.length - 8];
        final byte[] array6 = new byte[8];
        System.arraycopy(array4, 0, array5, 0, array4.length - 8);
        System.arraycopy(array4, array4.length - 8, array6, 0, 8);
        if (!this.checkCMSKeyChecksum(array5, array6)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        return array5;
    }
    
    private byte[] calculateCMSKeyChecksum(final byte[] array) {
        final byte[] array2 = new byte[8];
        this.sha1.update(array, 0, array.length);
        this.sha1.doFinal(this.digest, 0);
        System.arraycopy(this.digest, 0, array2, 0, 8);
        return array2;
    }
    
    private boolean checkCMSKeyChecksum(final byte[] array, final byte[] array2) {
        return Arrays.constantTimeAreEqual(this.calculateCMSKeyChecksum(array), array2);
    }
    
    private static byte[] reverse(final byte[] array) {
        final byte[] array2 = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            array2[i] = array[array.length - (i + 1)];
        }
        return array2;
    }
    
    static {
        IV2 = new byte[] { 74, -35, -94, 44, 121, -24, 33, 5 };
    }
}
