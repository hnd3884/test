package org.xbill.DNS.security;

import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;

class DSAPubKey implements DSAPublicKey
{
    private DSAParams params;
    private BigInteger Y;
    
    public DSAPubKey(final BigInteger p, final BigInteger q, final BigInteger g, final BigInteger y) {
        this.params = new SimpleDSAParams(p, q, g);
        this.Y = y;
    }
    
    public BigInteger getY() {
        return this.Y;
    }
    
    public DSAParams getParams() {
        return this.params;
    }
    
    public String getAlgorithm() {
        return "DSA";
    }
    
    public String getFormat() {
        return null;
    }
    
    public byte[] getEncoded() {
        return null;
    }
    
    static class SimpleDSAParams implements DSAParams
    {
        private BigInteger P;
        private BigInteger Q;
        private BigInteger G;
        
        public SimpleDSAParams(final BigInteger p, final BigInteger q, final BigInteger g) {
            this.P = p;
            this.Q = q;
            this.G = g;
        }
        
        public BigInteger getP() {
            return this.P;
        }
        
        public BigInteger getQ() {
            return this.Q;
        }
        
        public BigInteger getG() {
            return this.G;
        }
    }
}
