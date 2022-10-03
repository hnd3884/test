package cryptix.jce.provider.dsa;

import java.security.interfaces.DSAParams;
import java.math.BigInteger;
import java.security.interfaces.DSAPrivateKey;

final class DSAPrivateKeyImpl implements DSAPrivateKey
{
    public static final long serialVersionUID = 0L;
    private final BigInteger x;
    private final DSAParams params;
    
    public BigInteger getX() {
        return this.x;
    }
    
    public DSAParams getParams() {
        return this.params;
    }
    
    public String getAlgorithm() {
        throw new RuntimeException();
    }
    
    public String getFormat() {
        throw new RuntimeException();
    }
    
    public byte[] getEncoded() {
        throw new RuntimeException();
    }
    
    DSAPrivateKeyImpl(final BigInteger x, final DSAParams params) {
        this.x = x;
        this.params = params;
    }
}
