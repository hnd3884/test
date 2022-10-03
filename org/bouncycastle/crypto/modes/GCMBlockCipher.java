package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.modes.gcm.BasicGCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMUtil;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.GCMExponentiator;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.BlockCipher;

public class GCMBlockCipher implements AEADBlockCipher
{
    private static final int BLOCK_SIZE = 16;
    private BlockCipher cipher;
    private GCMMultiplier multiplier;
    private GCMExponentiator exp;
    private boolean forEncryption;
    private boolean initialised;
    private int macSize;
    private byte[] lastKey;
    private byte[] nonce;
    private byte[] initialAssociatedText;
    private byte[] H;
    private byte[] J0;
    private byte[] bufBlock;
    private byte[] macBlock;
    private byte[] S;
    private byte[] S_at;
    private byte[] S_atPre;
    private byte[] counter;
    private int blocksRemaining;
    private int bufOff;
    private long totalLength;
    private byte[] atBlock;
    private int atBlockPos;
    private long atLength;
    private long atLengthPre;
    
    public GCMBlockCipher(final BlockCipher blockCipher) {
        this(blockCipher, null);
    }
    
    public GCMBlockCipher(final BlockCipher cipher, GCMMultiplier multiplier) {
        if (cipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("cipher required with a block size of 16.");
        }
        if (multiplier == null) {
            multiplier = new Tables4kGCMMultiplier();
        }
        this.cipher = cipher;
        this.multiplier = multiplier;
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }
    
    public String getAlgorithmName() {
        return this.cipher.getAlgorithmName() + "/GCM";
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        this.forEncryption = forEncryption;
        this.macBlock = null;
        this.initialised = true;
        byte[] nonce;
        KeyParameter key;
        if (cipherParameters instanceof AEADParameters) {
            final AEADParameters aeadParameters = (AEADParameters)cipherParameters;
            nonce = aeadParameters.getNonce();
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            final int macSize = aeadParameters.getMacSize();
            if (macSize < 32 || macSize > 128 || macSize % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSize);
            }
            this.macSize = macSize / 8;
            key = aeadParameters.getKey();
        }
        else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to GCM");
            }
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            nonce = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            key = (KeyParameter)parametersWithIV.getParameters();
        }
        this.bufBlock = new byte[forEncryption ? 16 : (16 + this.macSize)];
        if (nonce == null || nonce.length < 1) {
            throw new IllegalArgumentException("IV must be at least 1 byte");
        }
        if (forEncryption && this.nonce != null && Arrays.areEqual(this.nonce, nonce)) {
            if (key == null) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
            if (this.lastKey != null && Arrays.areEqual(this.lastKey, key.getKey())) {
                throw new IllegalArgumentException("cannot reuse nonce for GCM encryption");
            }
        }
        this.nonce = nonce;
        if (key != null) {
            this.lastKey = key.getKey();
        }
        if (key != null) {
            this.cipher.init(true, key);
            this.H = new byte[16];
            this.cipher.processBlock(this.H, 0, this.H, 0);
            this.multiplier.init(this.H);
            this.exp = null;
        }
        else if (this.H == null) {
            throw new IllegalArgumentException("Key must be specified in initial init");
        }
        this.J0 = new byte[16];
        if (this.nonce.length == 12) {
            System.arraycopy(this.nonce, 0, this.J0, 0, this.nonce.length);
            this.J0[15] = 1;
        }
        else {
            this.gHASH(this.J0, this.nonce, this.nonce.length);
            final byte[] array = new byte[16];
            Pack.longToBigEndian(this.nonce.length * 8L, array, 8);
            this.gHASHBlock(this.J0, array);
        }
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    public byte[] getMac() {
        if (this.macBlock == null) {
            return new byte[this.macSize];
        }
        return Arrays.clone(this.macBlock);
    }
    
    public int getOutputSize(final int n) {
        final int n2 = n + this.bufOff;
        if (this.forEncryption) {
            return n2 + this.macSize;
        }
        return (n2 < this.macSize) ? 0 : (n2 - this.macSize);
    }
    
    public int getUpdateOutputSize(final int n) {
        int n2 = n + this.bufOff;
        if (!this.forEncryption) {
            if (n2 < this.macSize) {
                return 0;
            }
            n2 -= this.macSize;
        }
        return n2 - n2 % 16;
    }
    
    public void processAADByte(final byte b) {
        this.checkStatus();
        this.atBlock[this.atBlockPos] = b;
        if (++this.atBlockPos == 16) {
            this.gHASHBlock(this.S_at, this.atBlock);
            this.atBlockPos = 0;
            this.atLength += 16L;
        }
    }
    
    public void processAADBytes(final byte[] array, final int n, final int n2) {
        this.checkStatus();
        for (int i = 0; i < n2; ++i) {
            this.atBlock[this.atBlockPos] = array[n + i];
            if (++this.atBlockPos == 16) {
                this.gHASHBlock(this.S_at, this.atBlock);
                this.atBlockPos = 0;
                this.atLength += 16L;
            }
        }
    }
    
    private void initCipher() {
        if (this.atLength > 0L) {
            System.arraycopy(this.S_at, 0, this.S_atPre, 0, 16);
            this.atLengthPre = this.atLength;
        }
        if (this.atBlockPos > 0) {
            this.gHASHPartial(this.S_atPre, this.atBlock, 0, this.atBlockPos);
            this.atLengthPre += this.atBlockPos;
        }
        if (this.atLengthPre > 0L) {
            System.arraycopy(this.S_atPre, 0, this.S, 0, 16);
        }
    }
    
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException {
        this.checkStatus();
        this.bufBlock[this.bufOff] = b;
        if (++this.bufOff == this.bufBlock.length) {
            this.processBlock(this.bufBlock, 0, array, n);
            if (this.forEncryption) {
                this.bufOff = 0;
            }
            else {
                System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
                this.bufOff = this.macSize;
            }
            return 16;
        }
        return 0;
    }
    
    public int processBytes(final byte[] array, int n, int i, final byte[] array2, final int n2) throws DataLengthException {
        this.checkStatus();
        if (array.length - n < i) {
            throw new DataLengthException("Input buffer too short");
        }
        int n3 = 0;
        if (this.forEncryption) {
            if (this.bufOff != 0) {
                while (i > 0) {
                    --i;
                    this.bufBlock[this.bufOff] = array[n++];
                    if (++this.bufOff == 16) {
                        this.processBlock(this.bufBlock, 0, array2, n2);
                        this.bufOff = 0;
                        n3 += 16;
                        break;
                    }
                }
            }
            while (i >= 16) {
                this.processBlock(array, n, array2, n2 + n3);
                n += 16;
                i -= 16;
                n3 += 16;
            }
            if (i > 0) {
                System.arraycopy(array, n, this.bufBlock, 0, i);
                this.bufOff = i;
            }
        }
        else {
            for (int j = 0; j < i; ++j) {
                this.bufBlock[this.bufOff] = array[n + j];
                if (++this.bufOff == this.bufBlock.length) {
                    this.processBlock(this.bufBlock, 0, array2, n2 + n3);
                    System.arraycopy(this.bufBlock, 16, this.bufBlock, 0, this.macSize);
                    this.bufOff = this.macSize;
                    n3 += 16;
                }
            }
        }
        return n3;
    }
    
    public int doFinal(final byte[] array, final int n) throws IllegalStateException, InvalidCipherTextException {
        this.checkStatus();
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        int bufOff = this.bufOff;
        if (this.forEncryption) {
            if (array.length - n < bufOff + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
        }
        else {
            if (bufOff < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            bufOff -= this.macSize;
            if (array.length - n < bufOff) {
                throw new OutputLengthException("Output buffer too short");
            }
        }
        if (bufOff > 0) {
            this.processPartial(this.bufBlock, 0, bufOff, array, n);
        }
        this.atLength += this.atBlockPos;
        if (this.atLength > this.atLengthPre) {
            if (this.atBlockPos > 0) {
                this.gHASHPartial(this.S_at, this.atBlock, 0, this.atBlockPos);
            }
            if (this.atLengthPre > 0L) {
                GCMUtil.xor(this.S_at, this.S_atPre);
            }
            final long n2 = this.totalLength * 8L + 127L >>> 7;
            final byte[] array2 = new byte[16];
            if (this.exp == null) {
                (this.exp = new BasicGCMExponentiator()).init(this.H);
            }
            this.exp.exponentiateX(n2, array2);
            GCMUtil.multiply(this.S_at, array2);
            GCMUtil.xor(this.S, this.S_at);
        }
        final byte[] array3 = new byte[16];
        Pack.longToBigEndian(this.atLength * 8L, array3, 0);
        Pack.longToBigEndian(this.totalLength * 8L, array3, 8);
        this.gHASHBlock(this.S, array3);
        final byte[] array4 = new byte[16];
        this.cipher.processBlock(this.J0, 0, array4, 0);
        GCMUtil.xor(array4, this.S);
        int n3 = bufOff;
        System.arraycopy(array4, 0, this.macBlock = new byte[this.macSize], 0, this.macSize);
        if (this.forEncryption) {
            System.arraycopy(this.macBlock, 0, array, n + this.bufOff, this.macSize);
            n3 += this.macSize;
        }
        else {
            final byte[] array5 = new byte[this.macSize];
            System.arraycopy(this.bufBlock, bufOff, array5, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.macBlock, array5)) {
                throw new InvalidCipherTextException("mac check in GCM failed");
            }
        }
        this.reset(false);
        return n3;
    }
    
    public void reset() {
        this.reset(true);
    }
    
    private void reset(final boolean b) {
        this.cipher.reset();
        this.S = new byte[16];
        this.S_at = new byte[16];
        this.S_atPre = new byte[16];
        this.atBlock = new byte[16];
        this.atBlockPos = 0;
        this.atLength = 0L;
        this.atLengthPre = 0L;
        this.counter = Arrays.clone(this.J0);
        this.blocksRemaining = -2;
        this.bufOff = 0;
        this.totalLength = 0L;
        if (this.bufBlock != null) {
            Arrays.fill(this.bufBlock, (byte)0);
        }
        if (b) {
            this.macBlock = null;
        }
        if (this.forEncryption) {
            this.initialised = false;
        }
        else if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    private void processBlock(final byte[] array, final int n, final byte[] array2, final int n2) {
        if (array2.length - n2 < 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.totalLength == 0L) {
            this.initCipher();
        }
        final byte[] array3 = new byte[16];
        this.getNextCTRBlock(array3);
        if (this.forEncryption) {
            GCMUtil.xor(array3, array, n);
            this.gHASHBlock(this.S, array3);
            System.arraycopy(array3, 0, array2, n2, 16);
        }
        else {
            this.gHASHBlock(this.S, array, n);
            GCMUtil.xor(array3, 0, array, n, array2, n2);
        }
        this.totalLength += 16L;
    }
    
    private void processPartial(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        final byte[] array3 = new byte[16];
        this.getNextCTRBlock(array3);
        if (this.forEncryption) {
            GCMUtil.xor(array, n, array3, 0, n2);
            this.gHASHPartial(this.S, array, n, n2);
        }
        else {
            this.gHASHPartial(this.S, array, n, n2);
            GCMUtil.xor(array, n, array3, 0, n2);
        }
        System.arraycopy(array, n, array2, n3, n2);
        this.totalLength += n2;
    }
    
    private void gHASH(final byte[] array, final byte[] array2, final int n) {
        for (int i = 0; i < n; i += 16) {
            this.gHASHPartial(array, array2, i, Math.min(n - i, 16));
        }
    }
    
    private void gHASHBlock(final byte[] array, final byte[] array2) {
        GCMUtil.xor(array, array2);
        this.multiplier.multiplyH(array);
    }
    
    private void gHASHBlock(final byte[] array, final byte[] array2, final int n) {
        GCMUtil.xor(array, array2, n);
        this.multiplier.multiplyH(array);
    }
    
    private void gHASHPartial(final byte[] array, final byte[] array2, final int n, final int n2) {
        GCMUtil.xor(array, array2, n, n2);
        this.multiplier.multiplyH(array);
    }
    
    private void getNextCTRBlock(final byte[] array) {
        if (this.blocksRemaining == 0) {
            throw new IllegalStateException("Attempt to process too many blocks");
        }
        --this.blocksRemaining;
        final int n = 1 + (this.counter[15] & 0xFF);
        this.counter[15] = (byte)n;
        final int n2 = (n >>> 8) + (this.counter[14] & 0xFF);
        this.counter[14] = (byte)n2;
        final int n3 = (n2 >>> 8) + (this.counter[13] & 0xFF);
        this.counter[13] = (byte)n3;
        this.counter[12] = (byte)((n3 >>> 8) + (this.counter[12] & 0xFF));
        this.cipher.processBlock(this.counter, 0, array, 0);
    }
    
    private void checkStatus() {
        if (this.initialised) {
            return;
        }
        if (this.forEncryption) {
            throw new IllegalStateException("GCM cipher cannot be reused for encryption");
        }
        throw new IllegalStateException("GCM cipher needs to be initialised");
    }
}
