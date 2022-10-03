package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithSalt implements CipherParameters
{
    private byte[] salt;
    private CipherParameters parameters;
    
    public ParametersWithSalt(final CipherParameters cipherParameters, final byte[] array) {
        this(cipherParameters, array, 0, array.length);
    }
    
    public ParametersWithSalt(final CipherParameters parameters, final byte[] array, final int n, final int n2) {
        this.salt = new byte[n2];
        this.parameters = parameters;
        System.arraycopy(array, n, this.salt, 0, n2);
    }
    
    public byte[] getSalt() {
        return this.salt;
    }
    
    public CipherParameters getParameters() {
        return this.parameters;
    }
}
