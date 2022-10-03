package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Mac;

public class EAXBlockCipher implements AEADBlockCipher
{
    private static final byte nTAG = 0;
    private static final byte hTAG = 1;
    private static final byte cTAG = 2;
    private SICBlockCipher cipher;
    private boolean forEncryption;
    private int blockSize;
    private Mac mac;
    private byte[] nonceMac;
    private byte[] associatedTextMac;
    private byte[] macBlock;
    private int macSize;
    private byte[] bufBlock;
    private int bufOff;
    private boolean cipherInitialized;
    private byte[] initialAssociatedText;
    
    public EAXBlockCipher(final BlockCipher blockCipher) {
        this.blockSize = blockCipher.getBlockSize();
        this.mac = new CMac(blockCipher);
        this.macBlock = new byte[this.blockSize];
        this.associatedTextMac = new byte[this.mac.getMacSize()];
        this.nonceMac = new byte[this.mac.getMacSize()];
        this.cipher = new SICBlockCipher(blockCipher);
    }
    
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "/EAX";
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher.getUnderlyingCipher();
    }
    
    public int getBlockSize() {
        return this.cipher.getBlockSize();
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        byte[] array;
        CipherParameters cipherParameters2;
        if (cipherParameters instanceof AEADParameters) {
            final AEADParameters aeadParameters = (AEADParameters)cipherParameters;
            array = aeadParameters.getNonce();
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            this.macSize = aeadParameters.getMacSize() / 8;
            cipherParameters2 = aeadParameters.getKey();
        }
        else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to EAX");
            }
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            array = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = this.mac.getMacSize() / 2;
            cipherParameters2 = parametersWithIV.getParameters();
        }
        this.bufBlock = new byte[forEncryption ? this.blockSize : (this.blockSize + this.macSize)];
        final byte[] array2 = new byte[this.blockSize];
        this.mac.init(cipherParameters2);
        array2[this.blockSize - 1] = 0;
        this.mac.update(array2, 0, this.blockSize);
        this.mac.update(array, 0, array.length);
        this.mac.doFinal(this.nonceMac, 0);
        this.cipher.init(true, new ParametersWithIV(null, this.nonceMac));
        this.reset();
    }
    
    private void initCipher() {
        if (this.cipherInitialized) {
            return;
        }
        this.cipherInitialized = true;
        this.mac.doFinal(this.associatedTextMac, 0);
        final byte[] array = new byte[this.blockSize];
        array[this.blockSize - 1] = 2;
        this.mac.update(array, 0, this.blockSize);
    }
    
    private void calculateMac() {
        final byte[] array = new byte[this.blockSize];
        this.mac.doFinal(array, 0);
        for (int i = 0; i < this.macBlock.length; ++i) {
            this.macBlock[i] = (byte)(this.nonceMac[i] ^ this.associatedTextMac[i] ^ array[i]);
        }
    }
    
    public void reset() {
        this.reset(true);
    }
    
    private void reset(final boolean b) {
        this.cipher.reset();
        this.mac.reset();
        this.bufOff = 0;
        Arrays.fill(this.bufBlock, (byte)0);
        if (b) {
            Arrays.fill(this.macBlock, (byte)0);
        }
        final byte[] array = new byte[this.blockSize];
        array[this.blockSize - 1] = 1;
        this.mac.update(array, 0, this.blockSize);
        this.cipherInitialized = false;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    public void processAADByte(final byte b) {
        if (this.cipherInitialized) {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        this.mac.update(b);
    }
    
    public void processAADBytes(final byte[] array, final int n, final int n2) {
        if (this.cipherInitialized) {
            throw new IllegalStateException("AAD data cannot be added after encryption/decryption processing has begun.");
        }
        this.mac.update(array, n, n2);
    }
    
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException {
        this.initCipher();
        return this.process(b, array, n);
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException {
        this.initCipher();
        if (array.length < n + n2) {
            throw new DataLengthException("Input buffer too short");
        }
        int n4 = 0;
        for (int i = 0; i != n2; ++i) {
            n4 += this.process(array[n + i], array2, n3 + n4);
        }
        return n4;
    }
    
    public int doFinal(final byte[] array, final int n) throws IllegalStateException, InvalidCipherTextException {
        this.initCipher();
        final int bufOff = this.bufOff;
        final byte[] array2 = new byte[this.bufBlock.length];
        this.bufOff = 0;
        if (this.forEncryption) {
            if (array.length < n + bufOff + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            this.cipher.processBlock(this.bufBlock, 0, array2, 0);
            System.arraycopy(array2, 0, array, n, bufOff);
            this.mac.update(array2, 0, bufOff);
            this.calculateMac();
            System.arraycopy(this.macBlock, 0, array, n + bufOff, this.macSize);
            this.reset(false);
            return bufOff + this.macSize;
        }
        else {
            if (bufOff < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            if (array.length < n + bufOff - this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            if (bufOff > this.macSize) {
                this.mac.update(this.bufBlock, 0, bufOff - this.macSize);
                this.cipher.processBlock(this.bufBlock, 0, array2, 0);
                System.arraycopy(array2, 0, array, n, bufOff - this.macSize);
            }
            this.calculateMac();
            if (!this.verifyMac(this.bufBlock, bufOff - this.macSize)) {
                throw new InvalidCipherTextException("mac check in EAX failed");
            }
            this.reset(false);
            return bufOff - this.macSize;
        }
    }
    
    public byte[] getMac() {
        final byte[] array = new byte[this.macSize];
        System.arraycopy(this.macBlock, 0, array, 0, this.macSize);
        return array;
    }
    
    public int getUpdateOutputSize(final int n) {
        int n2 = n + this.bufOff;
        if (!this.forEncryption) {
            if (n2 < this.macSize) {
                return 0;
            }
            n2 -= this.macSize;
        }
        return n2 - n2 % this.blockSize;
    }
    
    public int getOutputSize(final int n) {
        final int n2 = n + this.bufOff;
        if (this.forEncryption) {
            return n2 + this.macSize;
        }
        return (n2 < this.macSize) ? 0 : (n2 - this.macSize);
    }
    
    private int process(final byte b, final byte[] array, final int n) {
        this.bufBlock[this.bufOff++] = b;
        if (this.bufOff != this.bufBlock.length) {
            return 0;
        }
        if (array.length < n + this.blockSize) {
            throw new OutputLengthException("Output buffer is too short");
        }
        int n2;
        if (this.forEncryption) {
            n2 = this.cipher.processBlock(this.bufBlock, 0, array, n);
            this.mac.update(array, n, this.blockSize);
        }
        else {
            this.mac.update(this.bufBlock, 0, this.blockSize);
            n2 = this.cipher.processBlock(this.bufBlock, 0, array, n);
        }
        this.bufOff = 0;
        if (!this.forEncryption) {
            System.arraycopy(this.bufBlock, this.blockSize, this.bufBlock, 0, this.macSize);
            this.bufOff = this.macSize;
        }
        return n2;
    }
    
    private boolean verifyMac(final byte[] array, final int n) {
        int n2 = 0;
        for (int i = 0; i < this.macSize; ++i) {
            n2 |= (this.macBlock[i] ^ array[n + i]);
        }
        return n2 == 0;
    }
}
