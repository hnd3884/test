package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.CipherParameters;
import java.util.Vector;
import org.bouncycastle.crypto.BlockCipher;

public class OCBBlockCipher implements AEADBlockCipher
{
    private static final int BLOCK_SIZE = 16;
    private BlockCipher hashCipher;
    private BlockCipher mainCipher;
    private boolean forEncryption;
    private int macSize;
    private byte[] initialAssociatedText;
    private Vector L;
    private byte[] L_Asterisk;
    private byte[] L_Dollar;
    private byte[] KtopInput;
    private byte[] Stretch;
    private byte[] OffsetMAIN_0;
    private byte[] hashBlock;
    private byte[] mainBlock;
    private int hashBlockPos;
    private int mainBlockPos;
    private long hashBlockCount;
    private long mainBlockCount;
    private byte[] OffsetHASH;
    private byte[] Sum;
    private byte[] OffsetMAIN;
    private byte[] Checksum;
    private byte[] macBlock;
    
    public OCBBlockCipher(final BlockCipher hashCipher, final BlockCipher mainCipher) {
        this.KtopInput = null;
        this.Stretch = new byte[24];
        this.OffsetMAIN_0 = new byte[16];
        this.OffsetMAIN = new byte[16];
        if (hashCipher == null) {
            throw new IllegalArgumentException("'hashCipher' cannot be null");
        }
        if (hashCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("'hashCipher' must have a block size of 16");
        }
        if (mainCipher == null) {
            throw new IllegalArgumentException("'mainCipher' cannot be null");
        }
        if (mainCipher.getBlockSize() != 16) {
            throw new IllegalArgumentException("'mainCipher' must have a block size of 16");
        }
        if (!hashCipher.getAlgorithmName().equals(mainCipher.getAlgorithmName())) {
            throw new IllegalArgumentException("'hashCipher' and 'mainCipher' must be the same algorithm");
        }
        this.hashCipher = hashCipher;
        this.mainCipher = mainCipher;
    }
    
    public BlockCipher getUnderlyingCipher() {
        return this.mainCipher;
    }
    
    public String getAlgorithmName() {
        return this.mainCipher.getAlgorithmName() + "/OCB";
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        final boolean forEncryption2 = this.forEncryption;
        this.forEncryption = forEncryption;
        this.macBlock = null;
        byte[] array;
        KeyParameter key;
        if (cipherParameters instanceof AEADParameters) {
            final AEADParameters aeadParameters = (AEADParameters)cipherParameters;
            array = aeadParameters.getNonce();
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            final int macSize = aeadParameters.getMacSize();
            if (macSize < 64 || macSize > 128 || macSize % 8 != 0) {
                throw new IllegalArgumentException("Invalid value for MAC size: " + macSize);
            }
            this.macSize = macSize / 8;
            key = aeadParameters.getKey();
        }
        else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("invalid parameters passed to OCB");
            }
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            array = parametersWithIV.getIV();
            this.initialAssociatedText = null;
            this.macSize = 16;
            key = (KeyParameter)parametersWithIV.getParameters();
        }
        this.hashBlock = new byte[16];
        this.mainBlock = new byte[forEncryption ? 16 : (16 + this.macSize)];
        if (array == null) {
            array = new byte[0];
        }
        if (array.length > 15) {
            throw new IllegalArgumentException("IV must be no more than 15 bytes");
        }
        if (key != null) {
            this.hashCipher.init(true, key);
            this.mainCipher.init(forEncryption, key);
            this.KtopInput = null;
        }
        else if (forEncryption2 != forEncryption) {
            throw new IllegalArgumentException("cannot change encrypting state without providing key.");
        }
        this.L_Asterisk = new byte[16];
        this.hashCipher.processBlock(this.L_Asterisk, 0, this.L_Asterisk, 0);
        this.L_Dollar = OCB_double(this.L_Asterisk);
        (this.L = new Vector()).addElement(OCB_double(this.L_Dollar));
        final int processNonce = this.processNonce(array);
        final int n = processNonce % 8;
        int n2 = processNonce / 8;
        if (n == 0) {
            System.arraycopy(this.Stretch, n2, this.OffsetMAIN_0, 0, 16);
        }
        else {
            for (int i = 0; i < 16; ++i) {
                this.OffsetMAIN_0[i] = (byte)((this.Stretch[n2] & 0xFF) << n | (this.Stretch[++n2] & 0xFF) >>> 8 - n);
            }
        }
        this.hashBlockPos = 0;
        this.mainBlockPos = 0;
        this.hashBlockCount = 0L;
        this.mainBlockCount = 0L;
        this.OffsetHASH = new byte[16];
        this.Sum = new byte[16];
        System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
        this.Checksum = new byte[16];
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    protected int processNonce(final byte[] array) {
        final byte[] ktopInput = new byte[16];
        System.arraycopy(array, 0, ktopInput, ktopInput.length - array.length, array.length);
        ktopInput[0] = (byte)(this.macSize << 4);
        final byte[] array2 = ktopInput;
        final int n = 15 - array.length;
        array2[n] |= 0x1;
        final int n2 = ktopInput[15] & 0x3F;
        final byte[] array3 = ktopInput;
        final int n3 = 15;
        array3[n3] &= (byte)192;
        if (this.KtopInput == null || !Arrays.areEqual(ktopInput, this.KtopInput)) {
            final byte[] array4 = new byte[16];
            this.KtopInput = ktopInput;
            this.hashCipher.processBlock(this.KtopInput, 0, array4, 0);
            System.arraycopy(array4, 0, this.Stretch, 0, 16);
            for (int i = 0; i < 8; ++i) {
                this.Stretch[16 + i] = (byte)(array4[i] ^ array4[i + 1]);
            }
        }
        return n2;
    }
    
    public byte[] getMac() {
        if (this.macBlock == null) {
            return new byte[this.macSize];
        }
        return Arrays.clone(this.macBlock);
    }
    
    public int getOutputSize(final int n) {
        final int n2 = n + this.mainBlockPos;
        if (this.forEncryption) {
            return n2 + this.macSize;
        }
        return (n2 < this.macSize) ? 0 : (n2 - this.macSize);
    }
    
    public int getUpdateOutputSize(final int n) {
        int n2 = n + this.mainBlockPos;
        if (!this.forEncryption) {
            if (n2 < this.macSize) {
                return 0;
            }
            n2 -= this.macSize;
        }
        return n2 - n2 % 16;
    }
    
    public void processAADByte(final byte b) {
        this.hashBlock[this.hashBlockPos] = b;
        if (++this.hashBlockPos == this.hashBlock.length) {
            this.processHashBlock();
        }
    }
    
    public void processAADBytes(final byte[] array, final int n, final int n2) {
        for (int i = 0; i < n2; ++i) {
            this.hashBlock[this.hashBlockPos] = array[n + i];
            if (++this.hashBlockPos == this.hashBlock.length) {
                this.processHashBlock();
            }
        }
    }
    
    public int processByte(final byte b, final byte[] array, final int n) throws DataLengthException {
        this.mainBlock[this.mainBlockPos] = b;
        if (++this.mainBlockPos == this.mainBlock.length) {
            this.processMainBlock(array, n);
            return 16;
        }
        return 0;
    }
    
    public int processBytes(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) throws DataLengthException {
        if (array.length < n + n2) {
            throw new DataLengthException("Input buffer too short");
        }
        int n4 = 0;
        for (int i = 0; i < n2; ++i) {
            this.mainBlock[this.mainBlockPos] = array[n + i];
            if (++this.mainBlockPos == this.mainBlock.length) {
                this.processMainBlock(array2, n3 + n4);
                n4 += 16;
            }
        }
        return n4;
    }
    
    public int doFinal(final byte[] array, final int n) throws IllegalStateException, InvalidCipherTextException {
        byte[] array2 = null;
        if (!this.forEncryption) {
            if (this.mainBlockPos < this.macSize) {
                throw new InvalidCipherTextException("data too short");
            }
            this.mainBlockPos -= this.macSize;
            array2 = new byte[this.macSize];
            System.arraycopy(this.mainBlock, this.mainBlockPos, array2, 0, this.macSize);
        }
        if (this.hashBlockPos > 0) {
            OCB_extend(this.hashBlock, this.hashBlockPos);
            this.updateHASH(this.L_Asterisk);
        }
        if (this.mainBlockPos > 0) {
            if (this.forEncryption) {
                OCB_extend(this.mainBlock, this.mainBlockPos);
                xor(this.Checksum, this.mainBlock);
            }
            xor(this.OffsetMAIN, this.L_Asterisk);
            final byte[] array3 = new byte[16];
            this.hashCipher.processBlock(this.OffsetMAIN, 0, array3, 0);
            xor(this.mainBlock, array3);
            if (array.length < n + this.mainBlockPos) {
                throw new OutputLengthException("Output buffer too short");
            }
            System.arraycopy(this.mainBlock, 0, array, n, this.mainBlockPos);
            if (!this.forEncryption) {
                OCB_extend(this.mainBlock, this.mainBlockPos);
                xor(this.Checksum, this.mainBlock);
            }
        }
        xor(this.Checksum, this.OffsetMAIN);
        xor(this.Checksum, this.L_Dollar);
        this.hashCipher.processBlock(this.Checksum, 0, this.Checksum, 0);
        xor(this.Checksum, this.Sum);
        this.macBlock = new byte[this.macSize];
        System.arraycopy(this.Checksum, 0, this.macBlock, 0, this.macSize);
        int mainBlockPos = this.mainBlockPos;
        if (this.forEncryption) {
            if (array.length < n + mainBlockPos + this.macSize) {
                throw new OutputLengthException("Output buffer too short");
            }
            System.arraycopy(this.macBlock, 0, array, n + mainBlockPos, this.macSize);
            mainBlockPos += this.macSize;
        }
        else if (!Arrays.constantTimeAreEqual(this.macBlock, array2)) {
            throw new InvalidCipherTextException("mac check in OCB failed");
        }
        this.reset(false);
        return mainBlockPos;
    }
    
    public void reset() {
        this.reset(true);
    }
    
    protected void clear(final byte[] array) {
        if (array != null) {
            Arrays.fill(array, (byte)0);
        }
    }
    
    protected byte[] getLSub(final int i) {
        while (i >= this.L.size()) {
            this.L.addElement(OCB_double(this.L.lastElement()));
        }
        return this.L.elementAt(i);
    }
    
    protected void processHashBlock() {
        final long hashBlockCount = this.hashBlockCount + 1L;
        this.hashBlockCount = hashBlockCount;
        this.updateHASH(this.getLSub(OCB_ntz(hashBlockCount)));
        this.hashBlockPos = 0;
    }
    
    protected void processMainBlock(final byte[] array, final int n) {
        if (array.length < n + 16) {
            throw new OutputLengthException("Output buffer too short");
        }
        if (this.forEncryption) {
            xor(this.Checksum, this.mainBlock);
            this.mainBlockPos = 0;
        }
        final byte[] offsetMAIN = this.OffsetMAIN;
        final long mainBlockCount = this.mainBlockCount + 1L;
        this.mainBlockCount = mainBlockCount;
        xor(offsetMAIN, this.getLSub(OCB_ntz(mainBlockCount)));
        xor(this.mainBlock, this.OffsetMAIN);
        this.mainCipher.processBlock(this.mainBlock, 0, this.mainBlock, 0);
        xor(this.mainBlock, this.OffsetMAIN);
        System.arraycopy(this.mainBlock, 0, array, n, 16);
        if (!this.forEncryption) {
            xor(this.Checksum, this.mainBlock);
            System.arraycopy(this.mainBlock, 16, this.mainBlock, 0, this.macSize);
            this.mainBlockPos = this.macSize;
        }
    }
    
    protected void reset(final boolean b) {
        this.hashCipher.reset();
        this.mainCipher.reset();
        this.clear(this.hashBlock);
        this.clear(this.mainBlock);
        this.hashBlockPos = 0;
        this.mainBlockPos = 0;
        this.hashBlockCount = 0L;
        this.mainBlockCount = 0L;
        this.clear(this.OffsetHASH);
        this.clear(this.Sum);
        System.arraycopy(this.OffsetMAIN_0, 0, this.OffsetMAIN, 0, 16);
        this.clear(this.Checksum);
        if (b) {
            this.macBlock = null;
        }
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    protected void updateHASH(final byte[] array) {
        xor(this.OffsetHASH, array);
        xor(this.hashBlock, this.OffsetHASH);
        this.hashCipher.processBlock(this.hashBlock, 0, this.hashBlock, 0);
        xor(this.Sum, this.hashBlock);
    }
    
    protected static byte[] OCB_double(final byte[] array) {
        final byte[] array2 = new byte[16];
        final int shiftLeft = shiftLeft(array, array2);
        final byte[] array3 = array2;
        final int n = 15;
        array3[n] ^= (byte)(135 >>> (1 - shiftLeft << 3));
        return array2;
    }
    
    protected static void OCB_extend(final byte[] array, int n) {
        array[n] = -128;
        while (++n < 16) {
            array[n] = 0;
        }
    }
    
    protected static int OCB_ntz(long n) {
        if (n == 0L) {
            return 64;
        }
        int n2 = 0;
        while ((n & 0x1L) == 0x0L) {
            ++n2;
            n >>>= 1;
        }
        return n2;
    }
    
    protected static int shiftLeft(final byte[] array, final byte[] array2) {
        int n = 16;
        int n2 = 0;
        while (--n >= 0) {
            final int n3 = array[n] & 0xFF;
            array2[n] = (byte)(n3 << 1 | n2);
            n2 = (n3 >>> 7 & 0x1);
        }
        return n2;
    }
    
    protected static void xor(final byte[] array, final byte[] array2) {
        for (int i = 15; i >= 0; --i) {
            final int n = i;
            array[n] ^= array2[i];
        }
    }
}
