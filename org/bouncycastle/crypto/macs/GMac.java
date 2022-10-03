package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.Mac;

public class GMac implements Mac
{
    private final GCMBlockCipher cipher;
    private final int macSizeBits;
    
    public GMac(final GCMBlockCipher cipher) {
        this.cipher = cipher;
        this.macSizeBits = 128;
    }
    
    public GMac(final GCMBlockCipher cipher, final int macSizeBits) {
        this.cipher = cipher;
        this.macSizeBits = macSizeBits;
    }
    
    public void init(final CipherParameters cipherParameters) throws IllegalArgumentException {
        if (cipherParameters instanceof ParametersWithIV) {
            final ParametersWithIV parametersWithIV = (ParametersWithIV)cipherParameters;
            this.cipher.init(true, new AEADParameters((KeyParameter)parametersWithIV.getParameters(), this.macSizeBits, parametersWithIV.getIV()));
            return;
        }
        throw new IllegalArgumentException("GMAC requires ParametersWithIV");
    }
    
    public String getAlgorithmName() {
        return this.cipher.getUnderlyingCipher().getAlgorithmName() + "-GMAC";
    }
    
    public int getMacSize() {
        return this.macSizeBits / 8;
    }
    
    public void update(final byte b) throws IllegalStateException {
        this.cipher.processAADByte(b);
    }
    
    public void update(final byte[] array, final int n, final int n2) throws DataLengthException, IllegalStateException {
        this.cipher.processAADBytes(array, n, n2);
    }
    
    public int doFinal(final byte[] array, final int n) throws DataLengthException, IllegalStateException {
        try {
            return this.cipher.doFinal(array, n);
        }
        catch (final InvalidCipherTextException ex) {
            throw new IllegalStateException(ex.toString());
        }
    }
    
    public void reset() {
        this.cipher.reset();
    }
}
