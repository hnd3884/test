package org.bouncycastle.crypto.params;

import org.bouncycastle.crypto.CipherParameters;

public class ParametersWithID implements CipherParameters
{
    private CipherParameters parameters;
    private byte[] id;
    
    public ParametersWithID(final CipherParameters parameters, final byte[] id) {
        this.parameters = parameters;
        this.id = id;
    }
    
    public byte[] getID() {
        return this.id;
    }
    
    public CipherParameters getParameters() {
        return this.parameters;
    }
}
