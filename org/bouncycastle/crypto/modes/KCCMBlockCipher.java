package org.bouncycastle.crypto.modes;

import java.io.ByteArrayOutputStream;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;

public class KCCMBlockCipher implements AEADBlockCipher
{
    private static final int BYTES_IN_INT = 4;
    private static final int BITS_IN_BYTE = 8;
    private static final int MAX_MAC_BIT_LENGTH = 512;
    private static final int MIN_MAC_BIT_LENGTH = 64;
    private BlockCipher engine;
    private int macSize;
    private boolean forEncryption;
    private byte[] initialAssociatedText;
    private byte[] mac;
    private byte[] macBlock;
    private byte[] nonce;
    private byte[] G1;
    private byte[] buffer;
    private byte[] s;
    private byte[] counter;
    private ExposedByteArrayOutputStream associatedText;
    private ExposedByteArrayOutputStream data;
    private int Nb_;
    
    private void setNb(final int nb_) {
        if (nb_ == 4 || nb_ == 6 || nb_ == 8) {
            this.Nb_ = nb_;
            return;
        }
        throw new IllegalArgumentException("Nb = 4 is recommended by DSTU7624 but can be changed to only 6 or 8 in this implementation");
    }
    
    public KCCMBlockCipher(final BlockCipher blockCipher) {
        this(blockCipher, 4);
    }
    
    public KCCMBlockCipher(final BlockCipher engine, final int nb) {
        this.associatedText = new ExposedByteArrayOutputStream();
        this.data = new ExposedByteArrayOutputStream();
        this.Nb_ = 4;
        this.engine = engine;
        this.macSize = engine.getBlockSize();
        this.nonce = new byte[engine.getBlockSize()];
        this.initialAssociatedText = new byte[engine.getBlockSize()];
        this.mac = new byte[engine.getBlockSize()];
        this.macBlock = new byte[engine.getBlockSize()];
        this.G1 = new byte[engine.getBlockSize()];
        this.buffer = new byte[engine.getBlockSize()];
        this.s = new byte[engine.getBlockSize()];
        this.counter = new byte[engine.getBlockSize()];
        this.setNb(nb);
    }
    
    public void init(final boolean forEncryption, final CipherParameters cipherParameters) throws IllegalArgumentException {
        CipherParameters cipherParameters2;
        if (cipherParameters instanceof AEADParameters) {
            final AEADParameters aeadParameters = (AEADParameters)cipherParameters;
            if (aeadParameters.getMacSize() > 512 || aeadParameters.getMacSize() < 64 || aeadParameters.getMacSize() % 8 != 0) {
                throw new IllegalArgumentException("Invalid mac size specified");
            }
            this.nonce = aeadParameters.getNonce();
            this.macSize = aeadParameters.getMacSize() / 8;
            this.initialAssociatedText = aeadParameters.getAssociatedText();
            cipherParameters2 = aeadParameters.getKey();
        }
        else {
            if (!(cipherParameters instanceof ParametersWithIV)) {
                throw new IllegalArgumentException("Invalid parameters specified");
            }
            this.nonce = ((ParametersWithIV)cipherParameters).getIV();
            this.macSize = this.engine.getBlockSize();
            this.initialAssociatedText = null;
            cipherParameters2 = ((ParametersWithIV)cipherParameters).getParameters();
        }
        this.mac = new byte[this.macSize];
        this.forEncryption = forEncryption;
        this.engine.init(true, cipherParameters2);
        this.counter[0] = 1;
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KCCM";
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
    
    private void processAAD(final byte[] array, int n, final int n2, final int n3) {
        if (n2 - n < this.engine.getBlockSize()) {
            throw new IllegalArgumentException("authText buffer too short");
        }
        if (n2 % this.engine.getBlockSize() != 0) {
            throw new IllegalArgumentException("padding not supported");
        }
        System.arraycopy(this.nonce, 0, this.G1, 0, this.nonce.length - this.Nb_ - 1);
        this.intToBytes(n3, this.buffer, 0);
        System.arraycopy(this.buffer, 0, this.G1, this.nonce.length - this.Nb_ - 1, 4);
        this.G1[this.G1.length - 1] = this.getFlag(true, this.macSize);
        this.engine.processBlock(this.G1, 0, this.macBlock, 0);
        this.intToBytes(n2, this.buffer, 0);
        if (n2 <= this.engine.getBlockSize() - this.Nb_) {
            for (int i = 0; i < n2; ++i) {
                final byte[] buffer = this.buffer;
                final int n4 = i + this.Nb_;
                buffer[n4] ^= array[n + i];
            }
            for (int j = 0; j < this.engine.getBlockSize(); ++j) {
                final byte[] macBlock = this.macBlock;
                final int n5 = j;
                macBlock[n5] ^= this.buffer[j];
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            return;
        }
        for (int k = 0; k < this.engine.getBlockSize(); ++k) {
            final byte[] macBlock2 = this.macBlock;
            final int n6 = k;
            macBlock2[n6] ^= this.buffer[k];
        }
        this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
        for (int l = n2; l != 0; l -= this.engine.getBlockSize()) {
            for (int n7 = 0; n7 < this.engine.getBlockSize(); ++n7) {
                final byte[] macBlock3 = this.macBlock;
                final int n8 = n7;
                macBlock3[n8] ^= array[n7 + n];
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
            n += this.engine.getBlockSize();
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
    
    public int processPacket(final byte[] array, int n, final int n2, final byte[] array2, int n3) throws IllegalStateException, InvalidCipherTextException {
        if (array.length - n < n2) {
            throw new DataLengthException("input buffer too short");
        }
        if (array2.length - n3 < n2) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.associatedText.size() > 0) {
            if (this.forEncryption) {
                this.processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size());
            }
            else {
                this.processAAD(this.associatedText.getBuffer(), 0, this.associatedText.size(), this.data.size() - this.macSize);
            }
        }
        if (this.forEncryption) {
            if (n2 % this.engine.getBlockSize() != 0) {
                throw new DataLengthException("partial blocks not supported");
            }
            this.CalculateMac(array, n, n2);
            this.engine.processBlock(this.nonce, 0, this.s, 0);
            for (int i = n2; i > 0; i -= this.engine.getBlockSize(), n += this.engine.getBlockSize(), n3 += this.engine.getBlockSize()) {
                this.ProcessBlock(array, n, n2, array2, n3);
            }
            for (int j = 0; j < this.counter.length; ++j) {
                final byte[] s = this.s;
                final int n4 = j;
                s[n4] += this.counter[j];
            }
            this.engine.processBlock(this.s, 0, this.buffer, 0);
            for (int k = 0; k < this.macSize; ++k) {
                array2[n3 + k] = (byte)(this.buffer[k] ^ this.macBlock[k]);
            }
            System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
            this.reset();
            return n2 + this.macSize;
        }
        else {
            if ((n2 - this.macSize) % this.engine.getBlockSize() != 0) {
                throw new DataLengthException("partial blocks not supported");
            }
            this.engine.processBlock(this.nonce, 0, this.s, 0);
            for (int n5 = n2 / this.engine.getBlockSize(), l = 0; l < n5; ++l) {
                this.ProcessBlock(array, n, n2, array2, n3);
                n += this.engine.getBlockSize();
                n3 += this.engine.getBlockSize();
            }
            if (n2 > n) {
                for (int n6 = 0; n6 < this.counter.length; ++n6) {
                    final byte[] s2 = this.s;
                    final int n7 = n6;
                    s2[n7] += this.counter[n6];
                }
                this.engine.processBlock(this.s, 0, this.buffer, 0);
                for (int n8 = 0; n8 < this.macSize; ++n8) {
                    array2[n3 + n8] = (byte)(this.buffer[n8] ^ array[n + n8]);
                }
                n3 += this.macSize;
            }
            for (int n9 = 0; n9 < this.counter.length; ++n9) {
                final byte[] s3 = this.s;
                final int n10 = n9;
                s3[n10] += this.counter[n9];
            }
            this.engine.processBlock(this.s, 0, this.buffer, 0);
            System.arraycopy(array2, n3 - this.macSize, this.buffer, 0, this.macSize);
            this.CalculateMac(array2, 0, n3 - this.macSize);
            System.arraycopy(this.macBlock, 0, this.mac, 0, this.macSize);
            final byte[] array3 = new byte[this.macSize];
            System.arraycopy(this.buffer, 0, array3, 0, this.macSize);
            if (!Arrays.constantTimeAreEqual(this.mac, array3)) {
                throw new InvalidCipherTextException("mac check failed");
            }
            this.reset();
            return n2 - this.macSize;
        }
    }
    
    private void ProcessBlock(final byte[] array, final int n, final int n2, final byte[] array2, final int n3) {
        for (int i = 0; i < this.counter.length; ++i) {
            final byte[] s = this.s;
            final int n4 = i;
            s[n4] += this.counter[i];
        }
        this.engine.processBlock(this.s, 0, this.buffer, 0);
        for (int j = 0; j < this.engine.getBlockSize(); ++j) {
            array2[n3 + j] = (byte)(this.buffer[j] ^ array[n + j]);
        }
    }
    
    private void CalculateMac(final byte[] array, int n, final int n2) {
        for (int i = n2; i > 0; i -= this.engine.getBlockSize(), n += this.engine.getBlockSize()) {
            for (int j = 0; j < this.engine.getBlockSize(); ++j) {
                final byte[] macBlock = this.macBlock;
                final int n3 = j;
                macBlock[n3] ^= array[n + j];
            }
            this.engine.processBlock(this.macBlock, 0, this.macBlock, 0);
        }
    }
    
    public int doFinal(final byte[] array, final int n) throws IllegalStateException, InvalidCipherTextException {
        final int processPacket = this.processPacket(this.data.getBuffer(), 0, this.data.size(), array, n);
        this.reset();
        return processPacket;
    }
    
    public byte[] getMac() {
        return Arrays.clone(this.mac);
    }
    
    public int getUpdateOutputSize(final int n) {
        return n;
    }
    
    public int getOutputSize(final int n) {
        return n + this.macSize;
    }
    
    public void reset() {
        Arrays.fill(this.G1, (byte)0);
        Arrays.fill(this.buffer, (byte)0);
        Arrays.fill(this.counter, (byte)0);
        Arrays.fill(this.macBlock, (byte)0);
        this.counter[0] = 1;
        this.data.reset();
        this.associatedText.reset();
        if (this.initialAssociatedText != null) {
            this.processAADBytes(this.initialAssociatedText, 0, this.initialAssociatedText.length);
        }
    }
    
    private void intToBytes(final int n, final byte[] array, final int n2) {
        array[n2 + 3] = (byte)(n >> 24);
        array[n2 + 2] = (byte)(n >> 16);
        array[n2 + 1] = (byte)(n >> 8);
        array[n2] = (byte)n;
    }
    
    private byte getFlag(final boolean b, final int n) {
        final StringBuffer sb = new StringBuffer();
        if (b) {
            sb.append("1");
        }
        else {
            sb.append("0");
        }
        switch (n) {
            case 8: {
                sb.append("010");
                break;
            }
            case 16: {
                sb.append("011");
                break;
            }
            case 32: {
                sb.append("100");
                break;
            }
            case 48: {
                sb.append("101");
                break;
            }
            case 64: {
                sb.append("110");
                break;
            }
        }
        String s;
        for (s = Integer.toBinaryString(this.Nb_ - 1); s.length() < 4; s = new StringBuffer(s).insert(0, "0").toString()) {}
        sb.append(s);
        return (byte)Integer.parseInt(sb.toString(), 2);
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
