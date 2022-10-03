package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.StreamBlockCipher;

public class KCTRBlockCipher extends StreamBlockCipher
{
    private byte[] iv;
    private byte[] ofbV;
    private byte[] ofbOutV;
    private int byteCount;
    private boolean initialised;
    private BlockCipher engine;
    
    public KCTRBlockCipher(final BlockCipher engine) {
        super(engine);
        this.engine = engine;
        this.iv = new byte[engine.getBlockSize()];
        this.ofbV = new byte[engine.getBlockSize()];
        this.ofbOutV = new byte[engine.getBlockSize()];
    }
    
    public void init(final boolean b, CipherParameters parameters) throws IllegalArgumentException {
        this.initialised = true;
        if (parameters instanceof ParametersWithIV) {
            final ParametersWithIV parametersWithIV = (ParametersWithIV)parameters;
            final byte[] iv = parametersWithIV.getIV();
            final int n = this.iv.length - iv.length;
            Arrays.fill(this.iv, (byte)0);
            System.arraycopy(iv, 0, this.iv, n, iv.length);
            parameters = parametersWithIV.getParameters();
            if (parameters != null) {
                this.engine.init(true, parameters);
            }
            this.reset();
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed");
    }
    
    public String getAlgorithmName() {
        return this.engine.getAlgorithmName() + "/KCTR";
    }
    
    public int getBlockSize() {
        return this.engine.getBlockSize();
    }
    
    @Override
    protected byte calculateByte(final byte b) {
        if (this.byteCount == 0) {
            this.incrementCounterAt(0);
            this.checkCounter();
            this.engine.processBlock(this.ofbV, 0, this.ofbOutV, 0);
            return (byte)(this.ofbOutV[this.byteCount++] ^ b);
        }
        final byte b2 = (byte)(this.ofbOutV[this.byteCount++] ^ b);
        if (this.byteCount == this.ofbV.length) {
            this.byteCount = 0;
        }
        return b2;
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        if (array.length - n < this.getBlockSize()) {
            throw new DataLengthException("input buffer too short");
        }
        if (array2.length - n2 < this.getBlockSize()) {
            throw new OutputLengthException("output buffer too short");
        }
        this.processBytes(array, n, this.getBlockSize(), array2, n2);
        return this.getBlockSize();
    }
    
    public void reset() {
        if (this.initialised) {
            this.engine.processBlock(this.iv, 0, this.ofbV, 0);
        }
        this.engine.reset();
        this.byteCount = 0;
    }
    
    private void incrementCounterAt(final int n) {
        int i = n;
        while (i < this.ofbV.length) {
            final byte[] ofbV = this.ofbV;
            final int n2 = i++;
            if (++ofbV[n2] != 0) {
                break;
            }
        }
    }
    
    private void checkCounter() {
    }
}
