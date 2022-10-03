package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.crypto.Digest;
import java.security.SecureRandom;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.Wrapper;

public class RC2WrapEngine implements Wrapper
{
    private CBCBlockCipher engine;
    private CipherParameters param;
    private ParametersWithIV paramPlusIV;
    private byte[] iv;
    private boolean forWrapping;
    private SecureRandom sr;
    private static final byte[] IV2;
    Digest sha1;
    byte[] digest;
    
    public RC2WrapEngine() {
        this.sha1 = DigestFactory.createSHA1();
        this.digest = new byte[20];
    }
    
    public void init(final boolean forWrapping, CipherParameters parameters) {
        this.forWrapping = forWrapping;
        this.engine = new CBCBlockCipher(new RC2Engine());
        if (parameters instanceof ParametersWithRandom) {
            final ParametersWithRandom parametersWithRandom = (ParametersWithRandom)parameters;
            this.sr = parametersWithRandom.getRandom();
            parameters = parametersWithRandom.getParameters();
        }
        else {
            this.sr = new SecureRandom();
        }
        if (parameters instanceof ParametersWithIV) {
            this.paramPlusIV = (ParametersWithIV)parameters;
            this.iv = this.paramPlusIV.getIV();
            this.param = this.paramPlusIV.getParameters();
            if (!this.forWrapping) {
                throw new IllegalArgumentException("You should not supply an IV for unwrapping");
            }
            if (this.iv == null || this.iv.length != 8) {
                throw new IllegalArgumentException("IV is not 8 octets");
            }
        }
        else {
            this.param = parameters;
            if (this.forWrapping) {
                this.iv = new byte[8];
                this.sr.nextBytes(this.iv);
                this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
            }
        }
    }
    
    public String getAlgorithmName() {
        return "RC2";
    }
    
    public byte[] wrap(final byte[] array, final int n, final int n2) {
        if (!this.forWrapping) {
            throw new IllegalStateException("Not initialized for wrapping");
        }
        int n3 = n2 + 1;
        if (n3 % 8 != 0) {
            n3 += 8 - n3 % 8;
        }
        final byte[] array2 = new byte[n3];
        array2[0] = (byte)n2;
        System.arraycopy(array, n, array2, 1, n2);
        final byte[] array3 = new byte[array2.length - n2 - 1];
        if (array3.length > 0) {
            this.sr.nextBytes(array3);
            System.arraycopy(array3, 0, array2, n2 + 1, array3.length);
        }
        final byte[] calculateCMSKeyChecksum = this.calculateCMSKeyChecksum(array2);
        final byte[] array4 = new byte[array2.length + calculateCMSKeyChecksum.length];
        System.arraycopy(array2, 0, array4, 0, array2.length);
        System.arraycopy(calculateCMSKeyChecksum, 0, array4, array2.length, calculateCMSKeyChecksum.length);
        final byte[] array5 = new byte[array4.length];
        System.arraycopy(array4, 0, array5, 0, array4.length);
        final int n4 = array4.length / this.engine.getBlockSize();
        if (array4.length % this.engine.getBlockSize() != 0) {
            throw new IllegalStateException("Not multiple of block length");
        }
        this.engine.init(true, this.paramPlusIV);
        for (int i = 0; i < n4; ++i) {
            final int n5 = i * this.engine.getBlockSize();
            this.engine.processBlock(array5, n5, array5, n5);
        }
        final byte[] array6 = new byte[this.iv.length + array5.length];
        System.arraycopy(this.iv, 0, array6, 0, this.iv.length);
        System.arraycopy(array5, 0, array6, this.iv.length, array5.length);
        final byte[] array7 = new byte[array6.length];
        for (int j = 0; j < array6.length; ++j) {
            array7[j] = array6[array6.length - (j + 1)];
        }
        this.engine.init(true, new ParametersWithIV(this.param, RC2WrapEngine.IV2));
        for (int k = 0; k < n4 + 1; ++k) {
            final int n6 = k * this.engine.getBlockSize();
            this.engine.processBlock(array7, n6, array7, n6);
        }
        return array7;
    }
    
    public byte[] unwrap(final byte[] array, final int n, final int n2) throws InvalidCipherTextException {
        if (this.forWrapping) {
            throw new IllegalStateException("Not set for unwrapping");
        }
        if (array == null) {
            throw new InvalidCipherTextException("Null pointer as ciphertext");
        }
        if (n2 % this.engine.getBlockSize() != 0) {
            throw new InvalidCipherTextException("Ciphertext not multiple of " + this.engine.getBlockSize());
        }
        this.engine.init(false, new ParametersWithIV(this.param, RC2WrapEngine.IV2));
        final byte[] array2 = new byte[n2];
        System.arraycopy(array, n, array2, 0, n2);
        for (int i = 0; i < array2.length / this.engine.getBlockSize(); ++i) {
            final int n3 = i * this.engine.getBlockSize();
            this.engine.processBlock(array2, n3, array2, n3);
        }
        final byte[] array3 = new byte[array2.length];
        for (int j = 0; j < array2.length; ++j) {
            array3[j] = array2[array2.length - (j + 1)];
        }
        this.iv = new byte[8];
        final byte[] array4 = new byte[array3.length - 8];
        System.arraycopy(array3, 0, this.iv, 0, 8);
        System.arraycopy(array3, 8, array4, 0, array3.length - 8);
        this.paramPlusIV = new ParametersWithIV(this.param, this.iv);
        this.engine.init(false, this.paramPlusIV);
        final byte[] array5 = new byte[array4.length];
        System.arraycopy(array4, 0, array5, 0, array4.length);
        for (int k = 0; k < array5.length / this.engine.getBlockSize(); ++k) {
            final int n4 = k * this.engine.getBlockSize();
            this.engine.processBlock(array5, n4, array5, n4);
        }
        final byte[] array6 = new byte[array5.length - 8];
        final byte[] array7 = new byte[8];
        System.arraycopy(array5, 0, array6, 0, array5.length - 8);
        System.arraycopy(array5, array5.length - 8, array7, 0, 8);
        if (!this.checkCMSKeyChecksum(array6, array7)) {
            throw new InvalidCipherTextException("Checksum inside ciphertext is corrupted");
        }
        if (array6.length - ((array6[0] & 0xFF) + 1) > 7) {
            throw new InvalidCipherTextException("too many pad bytes (" + (array6.length - ((array6[0] & 0xFF) + 1)) + ")");
        }
        final byte[] array8 = new byte[array6[0]];
        System.arraycopy(array6, 1, array8, 0, array8.length);
        return array8;
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
    
    static {
        IV2 = new byte[] { 74, -35, -94, 44, 121, -24, 33, 5 };
    }
}
