package org.xbill.DNS.security;

import java.math.BigInteger;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.interfaces.DHPublicKey;

class DHPubKey implements DHPublicKey
{
    private DHParameterSpec params;
    private BigInteger Y;
    
    public DHPubKey(final BigInteger p, final BigInteger g, final BigInteger y) {
        this.params = new DHParameterSpec(p, g);
        this.Y = y;
    }
    
    public BigInteger getY() {
        return this.Y;
    }
    
    public DHParameterSpec getParams() {
        return this.params;
    }
    
    public String getAlgorithm() {
        return "DH";
    }
    
    public String getFormat() {
        return null;
    }
    
    public byte[] getEncoded() {
        return null;
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("P = ");
        sb.append(this.params.getP());
        sb.append("\nG = ");
        sb.append(this.params.getG());
        sb.append("\nY = ");
        sb.append(this.Y);
        return sb.toString();
    }
}
