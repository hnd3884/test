package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class CCMBlockCipher implements AEADBlockCipher
{
    private BlockCipher cipher;
    private int blockSize;
    private boolean forEncryption;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private int macSize;
    private CipherParameters keyParam;
    private byte[] macBlock;
    private ExposedByteArrayOutputStream associatedText;
    private ExposedByteArrayOutputStream data;
    
    public CCMBlockCipher(final BlockCipher cipher) {
        this.associatedText = new ExposedByteArrayOutputStream();
        this.data = new ExposedByteArrayOutputStream();
        this.cipher = cipher;
        this.blockSize = cipher.getBlockSize();
        this.macBlock = new byte[this.blockSize];
        if (this.blockSize != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        CipherParameters keyParam;
        if (cipherParameters instanceof AEADParameters) {
            final AEADParameters aeadParameters = (AEADParameters)cipherParameters;
            this.nonce = aeadParameters.getNonce();
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            this.macSize = aeadParameters.getMacSize() / 8;
            keyParam = aeadParameters.getKey();
        }
        else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to CCM: " + cipherParameters.getClass().getName());
            }
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            this.nonce = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = this.macBlock.length / 2;
            keyParam = parametersWithIV.getParameters();
        }
        if (keyParam != null) {
            this.keyParam = keyParam;
        }
        if (this.nonce == null || this.nonce.length < 7 || this.nonce.length > 13) {
            throw new IllegalArgumentException("nonce must have length from 7 to 13 octets");
        }
        this.reset();
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/CCM";
    }
    
    public void processAADByte(final byte b) {
        this.associatedText.write(b);
    }
    
    public void processAADBytes(final byte[] array, final int n, final int n2) {
        this.associatedText.write(array, n, n2);
    }
    
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        this.data.write(b);
        return 0;
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException, IllegalStateException {
        if (array.length < n + n2) {
            throw new DataLengthException("Input buffer too short");
        }
        this.data.write(array, n, n2);
        return 0;
    }
    
    public int doFinal(final byte[] array, final int n) throws IllegalStateException, InvalidCipherTextException {
        final int processPacket = this.processPacket(this.data.getBuffer(), 0, this.data.size(), array, n);
        this.reset();
        return processPacket;
    }
    
    public void reset() {
        this.cipher.reset();
        this.associatedText.reset();
        this.data.reset();
    }
    
    public byte[] getMac() {
        final byte[] array = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, array, 0, array.length);
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
    
    public byte[] processPacket(final byte[] array, final int n, final int n2) throws IllegalStateException, InvalidCipherTextException {
        byte[] array2;
        if (this.forEncryption) {
            array2 = new byte[n2 + this.macSize];
        }
        else {
            if (n2 < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            array2 = new byte[n2 - this.macSize];
        }
        this.processPacket(array, n, n2, array2, 0);
        return array2;
    }
    
    public int processPacket(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws IllegalStateException, InvalidCipherTextException, DataLengthException {
        if (this.keyParam == null) {
            throw new IllegalStateException("CCM cipher unitialized.");
        }
        final int n4 = 15 - this.nonce.length;
        if (n4 < 4 && n2 >= 1 << 8 * n4) {
            throw new IllegalStateException("CCM packet too large for choice of q.");
        }
        final byte[] array3 = new byte[this.blockSize];
        array3[0] = (byte)(n4 - 1 & 0x7);
        System.arraycopy(this.nonce, 0, array3, 1, this.nonce.length);
        final SICBlockCipher sicBlockCipher = new SICBlockCipher(this.cipher);
        sicBlockCipher.init(this.forEncryption, new ParametersWithIV(this.keyParam, array3));
        int i = n;
        int n5 = n3;
        int n6;
        if (this.forEncryption) {
            n6 = n2 + this.macSize;
            if (array2.length < n6 + n3) {
                throw new OutputLengthException("Output buffer too short.");
            }
            this.calculateMac(array, n, n2, this.macBlock);
            final byte[] array4 = new byte[this.blockSize];
            sicBlockCipher.processBlock(this.macBlock, 0, array4, 0);
            while (i < n + n2 - this.blockSize) {
                sicBlockCipher.processBlock(array, i, array2, n5);
                n5 += this.blockSize;
                i += this.blockSize;
            }
            final byte[] array5 = new byte[this.blockSize];
            System.arraycopy(array, i, array5, 0, n2 + n - i);
            sicBlockCipher.processBlock(array5, 0, array5, 0);
            System.arraycopy(array5, 0, array2, n5, n2 + n - i);
            System.arraycopy(array4, 0, array2, n3 + n2, this.macSize);
        }
        else {
            if (n2 < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            n6 = n2 - this.macSize;
            if (array2.length < n6 + n3) {
                throw new OutputLengthException("Output buffer too short.");
            }
            System.arraycopy(array, n + n6, this.macBlock, 0, this.macSize);
            sicBlockCipher.processBlock(this.macBlock, 0, this.macBlock, 0);
            for (int j = this.macSize; j != this.macBlock.length; ++j) {
                this.macBlock[j] = 0;
            }
            while (i < n + n6 - this.blockSize) {
                sicBlockCipher.processBlock(array, i, array2, n5);
                n5 += this.blockSize;
                i += this.blockSize;
            }
            final byte[] array6 = new byte[this.blockSize];
            System.arraycopy(array, i, array6, 0, n6 - (i - n));
            sicBlockCipher.processBlock(array6, 0, array6, 0);
            System.arraycopy(array6, 0, array2, n5, n6 - (i - n));
            final byte[] array7 = new byte[this.blockSize];
            this.calculateMac(array2, n3, n6, array7);
            if (!Arrays.constantTimeAreEqual(this.macBlock, array7)) {
                throw new InvalidCipherTextException("mac check in CCM failed");
            }
        }
        return n6;
    }
    
    private int calculateMac(final byte[] array, final int n, final int n2, final byte[] array2) {
        final CBCBlockCipherMac cbcBlockCipherMac = new CBCBlockCipherMac(this.cipher, this.macSize * 8);
        cbcBlockCipherMac.init(this.keyParam);
        final byte[] array3 = new byte[16];
        if (this.hasAssociatedText()) {
            final byte[] array4 = array3;
            final int n3 = 0;
            array4[n3] |= 0x40;
        }
        final byte[] array5 = array3;
        final int n4 = 0;
        array5[n4] |= (byte)(((cbcBlockCipherMac.getMacSize() - 2) / 2 & 0x7) << 3);
        final byte[] array6 = array3;
        final int n5 = 0;
        array6[n5] |= (byte)(15 - this.nonce.length - 1 & 0x7);
        System.arraycopy(this.nonce, 0, array3, 1, this.nonce.length);
        for (int i = n2, n6 = 1; i > 0; i >>>= 8, ++n6) {
            array3[array3.length - n6] = (byte)(i & 0xFF);
        }
        cbcBlockCipherMac.update(array3, 0, array3.length);
        if (this.hasAssociatedText()) {
            final int associatedTextLength = this.getAssociatedTextLength();
            int n7;
            if (associatedTextLength < 65280) {
                cbcBlockCipherMac.update((byte)(associatedTextLength >> 8));
                cbcBlockCipherMac.update((byte)associatedTextLength);
                n7 = 2;
            }
            else {
                cbcBlockCipherMac.update((byte)(-1));
                cbcBlockCipherMac.update((byte)(-2));
                cbcBlockCipherMac.update((byte)(associatedTextLength >> 24));
                cbcBlockCipherMac.update((byte)(associatedTextLength >> 16));
                cbcBlockCipherMac.update((byte)(associatedTextLength >> 8));
                cbcBlockCipherMac.update((byte)associatedTextLength);
                n7 = 6;
            }
            if (this.initialAssociatedText != null) {
                cbcBlockCipherMac.update(this.initialAssociatedText, 0, this.initialAssociatedText.length);
            }
            if (this.associatedText.size() > 0) {
                cbcBlockCipherMac.update(this.associatedText.getBuffer(), 0, this.associatedText.size());
            }
            final int n8 = (n7 + associatedTextLength) % 16;
            if (n8 != 0) {
                for (int j = n8; j != 16; ++j) {
                    cbcBlockCipherMac.update((byte)0);
                }
            }
        }
        cbcBlockCipherMac.update(array, n, n2);
        return cbcBlockCipherMac.doFinal(array2, 0);
    }
    
    private int getAssociatedTextLength() {
        return this.associatedText.size() + ((this.initialAssociatedText == null) ? 0 : this.initialAssociatedText.length);
    }
    
    private boolean hasAssociatedText() {
        return this.getAssociatedTextLength() > 0;
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
