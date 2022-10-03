package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithUKM implements CipherParameters
{
    private byte[] ukm;
    private CipherParameters parameters;
    
    public ParametersWithUKM(final CipherParameters cipherParameters, final byte[] array) {
        this(cipherParameters, array, 0, array.length);
    }
    
    public ParametersWithUKM(final CipherParameters parameters, final byte[] array, final int n, final int n2) {
        this.ukm = new byte[n2];
        this.parameters = parameters;
        System.arraycopy(array, n, this.ukm, 0, n2);
    }
    
    public byte[] getUKM() {
        return this.ukm;
    }
    
    public CipherParameters getParameters() {
        return this.parameters;
    }
}
