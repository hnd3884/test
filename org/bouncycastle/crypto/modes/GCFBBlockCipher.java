package org.bouncycastle.crypto.modes;

import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithSBox;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.StreamBlockCipher;

public class GCFBBlockCipher extends StreamBlockCipher
{
    private static final byte[] C;
    private final CFBBlockCipher cfbEngine;
    private KeyParameter key;
    private long counter;
    private boolean forEncryption;
    
    public GCFBBlockCipher(final BlockCipher blockCipher) {
        super(blockCipher);
        this.counter = 0L;
        this.cfbEngine = new CFBBlockCipher(blockCipher, blockCipher.getBlockSize() * 8);
    }
    
    public void init(final boolean forEncryption, CipherParameters cipherParameters) throws IllegalArgumentException {
        this.counter = 0L;
        this.cfbEngine.init(forEncryption, cipherParameters);
        this.forEncryption = forEncryption;
        if (cipherParameters instanceof ParametersWithIV) {
            cipherParameters = ((ParametersWithIV)cipherParameters).getParameters();
        }
        if (cipherParameters instanceof ParametersWithRandom) {
            cipherParameters = ((ParametersWithRandom)cipherParameters).getParameters();
        }
        if (cipherParameters instanceof ParametersWithSBox) {
            cipherParameters = ((ParametersWithSBox)cipherParameters).getParameters();
        }
        this.key = (KeyParameter)cipherParameters;
    }
    
    public String getAlgorithmName() {
        final String algorithmName = this.cfbEngine.getAlgorithmName();
        return algorithmName.substring(0, algorithmName.indexOf(47)) + "/G" + algorithmName.substring(algorithmName.indexOf(47) + 1);
    }
    
    public int getBlockSize() {
        return this.cfbEngine.getBlockSize();
    }
    
    public int processBlock(final byte[] array, final int n, final byte[] array2, final int n2) throws DataLengthException, IllegalStateException {
        this.processBytes(array, n, this.cfbEngine.getBlockSize(), array2, n2);
        return this.cfbEngine.getBlockSize();
    }
    
    @Override
    protected byte calculateByte(final byte b) {
        if (this.counter > 0L && this.counter % 1024L == 0L) {
            final BlockCipher underlyingCipher = this.cfbEngine.getUnderlyingCipher();
            underlyingCipher.init(false, this.key);
            final byte[] array = new byte[32];
            underlyingCipher.processBlock(GCFBBlockCipher.C, 0, array, 0);
            underlyingCipher.processBlock(GCFBBlockCipher.C, 8, array, 8);
            underlyingCipher.processBlock(GCFBBlockCipher.C, 16, array, 16);
            underlyingCipher.processBlock(GCFBBlockCipher.C, 24, array, 24);
            underlyingCipher.init(true, this.key = new KeyParameter(array));
            final byte[] currentIV = this.cfbEngine.getCurrentIV();
            underlyingCipher.processBlock(currentIV, 0, currentIV, 0);
            this.cfbEngine.init(this.forEncryption, new ParametersWithIV(this.key, currentIV));
        }
        ++this.counter;
        return this.cfbEngine.calculateByte(b);
    }
    
    public void reset() {
        this.counter = 0L;
        this.cfbEngine.reset();
    }
    
    static {
        C = new byte[] { 105, 0, 114, 34, 100, -55, 4, 35, -115, 58, -37, -106, 70, -23, 42, -60, 24, -2, -84, -108, 0, -19, 7, 18, -64, -122, -36, -62, -17, 76, -87, 43 };
    }
}
