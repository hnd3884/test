package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.kgcm.Tables16kKGCMMultiplier_512;
import org.bouncycastle.crypto.modes.kgcm.Tables8kKGCMMultiplier_256;
import org.bouncycastle.crypto.modes.kgcm.Tables4kKGCMMultiplier_128;
import org.bouncycastle.crypto.modes.kgcm.KGCMMultiplier;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.BlockCipher;

public class KGCMBlockCipher implements AEADBlockCipher
{
    private static final int MIN_MAC_BITS = 64;
    private BlockCipher engine;
    private BufferedBlockCipher ctrEngine;
    private int macSize;
    private boolean forEncryption;
    private byte[] initialAssociatedText;
    private byte[] macBlock;
    private byte[] iv;
    private KGCMMultiplier multiplier;
    private long[] b;
    private final int blockSize;
    private ExposedByteArrayOutputStream associatedText;
    private ExposedByteArrayOutputStream data;
    
    private static KGCMMultiplier createDefaultMultiplier(final int n) {
        switch (n) {
            case 16: {
                return new Tables4kKGCMMultiplier_128();
            }
            case 32: {
                return new Tables8kKGCMMultiplier_256();
            }
            case 64: {
                return new Tables16kKGCMMultiplier_512();
            }
            default: {
                throw new IllegalArgumentException("Only 128, 256, and 512 -bit block sizes supported");
            }
        }
    }
    
    public KGCMBlockCipher(final BlockCipher engine) {
        this.associatedText = new ExposedByteArrayOutputStream();
        this.data = new ExposedByteArrayOutputStream();
        this.engine = engine;
        this.ctrEngine = new BufferedBlockCipher(new KCTRBlockCipher(this.engine));
        this.macSize = -1;
        this.blockSize = this.engine.getBlockSize();
        this.initialAssociatedText = new byte[this.blockSize];
        this.iv = new byte[this.blockSize];
        this.multiplier = createDefaultMultiplier(this.blockSize);
        this.b = new long[this.blockSize >>> 3];
        this.macBlock = null;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        KeyParameter key;
        if (cipherParameters instanceof AEADParameters) {
            final AEADParameters aeadParameters = (AEADParameters)cipherParameters;
            final byte[] nonce = aeadParameters.getNonce();
            final int n = this.iv.length - nonce.length;
            Arrays.fill(this.iv, (byte)0);
            System.arraycopy(nonce, 0, this.iv, n, nonce.length);
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            final int macSize = aeadParameters.getMacSize();
            if (macSize < 64 || macSize > this.blockSize << 3 || (macSize & 0x7) != 0x0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSize);
            }
            this.macSize = macSize >>> 3;
            key = aeadParameters.getKey();
            if (this.initialAssociatedText != null) {
                this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
            }
        }
        else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("Invalid parameter passed");
            }
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            final byte[] iv = parametersWithIV.getIV();
            final int n2 = this.iv.length - iv.length;
            Arrays.fill(this.iv, (byte)0);
            System.arraycopy(iv, 0, this.iv, n2, iv.length);
            this.initialAssociatedText = null;
            this.macSize = this.blockSize;
            key = (KeyParameter)parametersWithIV.getParameters();
        }
        this.macBlock = new byte[this.blockSize];
        this.ctrEngine.init(true, new ParametersWithIV(key, this.iv));
        this.engine.init(true, key);
    }
    
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KGCM";
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.engine;
    }
    
    public void processAADByte(final byte b) {
        this.associatedText.write(b);
    }
    
    public void processAADBytes(final byte[] array, final int n, final int n2) {
        this.associatedText.write(array, n, n2);
    }
    
    private void processAAD(final byte[] array, final int n, final int n2) {
        for (int i = n; i < n + n2; i += this.blockSize) {
            xorWithInput(this.b, array, i);
            this.multiplier.multiplyH(this.b);
        }
    }
    
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        this.data.write(b);
        return 0;
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException, IllegalStateException {
        if (array.length < n + n2) {
            throw new DataLengthException("input buffer too short");
        }
        this.data.write(array, n, n2);
        return 0;
    }
    
    public int doFinal(final byte[] array, final int n) throws IllegalStateException, InvalidCipherTextException {
        final int size = this.data.size();
        if (!this.forEncryption && size < this.macSize) {
            throw new InvalidCipherTextException("data too short");
        }
        final byte[] array2 = new byte[this.blockSize];
        this.engine.processBlock(array2, 0, array2, 0);
        final long[] array3 = new long[this.blockSize >>> 3];
        Pack.littleEndianToLong(array2, 0, array3);
        this.multiplier.init(array3);
        Arrays.fill(array2, (byte)0);
        Arrays.fill(array3, 0L);
        final int size2 = this.associatedText.size();
        if (size2 > 0) {
            this.processAAD(this.associatedText.getBuffer(), 0, size2);
        }
        int n2;
        if (this.forEncryption) {
            if (array.length - n - this.macSize < size) {
                throw new OutputLengthException("Output buffer too short");
            }
            final int processBytes = this.ctrEngine.processBytes(this.data.getBuffer(), 0, size, array, n);
            n2 = processBytes + this.ctrEngine.doFinal(array, n + processBytes);
            this.calculateMac(array, n, size, size2);
        }
        else {
            final int n3 = size - this.macSize;
            if (array.length - n < n3) {
                throw new OutputLengthException("Output buffer too short");
            }
            this.calculateMac(this.data.getBuffer(), 0, n3, size2);
            final int processBytes2 = this.ctrEngine.processBytes(this.data.getBuffer(), 0, n3, array, n);
            n2 = processBytes2 + this.ctrEngine.doFinal(array, n + processBytes2);
        }
        if (this.macBlock == null) {
            throw new IllegalStateException("mac is not calculated");
        }
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, array, n + n2, this.macSize);
            this.reset();
            return n2 + this.macSize;
        }
        final byte[] array4 = new byte[this.macSize];
        System.arraycopy(this.data.getBuffer(), size - this.macSize, array4, 0, this.macSize);
        final byte[] array5 = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, array5, 0, this.macSize);
        if (!Arrays.constantTimeAreEqual(array4, array5)) {
            throw new InvalidCipherTextException("mac verification failed");
        }
        this.reset();
        return n2;
    }
    
    public byte[] getMac() {
        final byte[] array = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, array, 0, this.macSize);
        return array;
    }
    
    public int getUpdateOutputSize(final int n) {
        return 0;
    }
    
    public int getOutputSize(final int n) {
        final int n2 = n + this.data.size();
        if (this.forEncryption) {
            return n2 + this.macSize;
        }
        return (n2 < this.macSize) ? 0 : (n2 - this.macSize);
    }
    
    public void reset() {
        Arrays.fill(this.b, 0L);
        this.engine.reset();
        this.data.reset();
        this.associatedText.reset();
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    private void calculateMac(final byte[] array, final int n, final int n2, final int n3) {
        for (int i = n; i < n + n2; i += this.blockSize) {
            xorWithInput(this.b, array, i);
            this.multiplier.multiplyH(this.b);
        }
        final long n4 = ((long)n3 & 0xFFFFFFFFL) << 3;
        final long n5 = ((long)n2 & 0xFFFFFFFFL) << 3;
        final long[] b = this.b;
        final int n6 = 0;
        b[n6] ^= n4;
        final long[] b2 = this.b;
        final int n7 = this.blockSize >>> 4;
        b2[n7] ^= n5;
        this.macBlock = Pack.longToLittleEndian(this.b);
        this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
    }
    
    private static void xorWithInput(final long[] array, final byte[] array2, int n) {
        for (int i = 0; i < array.length; ++i) {
            final int n2 = i;
            array[n2] ^= Pack.littleEndianToLong(array2, n);
            n += 8;
        }
    }
    
    private class ExposedByteArrayOutputStream extends ByteArrayOutputStream
    {
        public ExposedByteArrayOutputStream() {
        }
        
        public byte[] getBuffer() {
            return this.buf;
        }
    }
}
