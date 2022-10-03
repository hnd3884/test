package jcifs.spnego;

import java.io.IOException;

public abstract class SpnegoToken
{
    private byte[] mechanismToken;
    private byte[] mechanismListMIC;
    
    public byte[] getMechanismToken() {
        return this.mechanismToken;
    }
    
    public void setMechanismToken(final byte[] mechanismToken) {
        this.mechanismToken = mechanismToken;
    }
    
    public byte[] getMechanismListMIC() {
        return this.mechanismListMIC;
    }
    
    public void setMechanismListMIC(final byte[] mechanismListMIC) {
        this.mechanismListMIC = mechanismListMIC;
    }
    
    public abstract byte[] toByteArray();
    
    protected abstract void parse(final byte[] p0) throws IOException;
}
