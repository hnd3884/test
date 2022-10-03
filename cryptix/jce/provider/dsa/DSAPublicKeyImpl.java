package cryptix.jce.provider.dsa;

import java.security.interfaces.DSAParams;
import java.math.BigInteger;
import java.security.interfaces.DSAPublicKey;

final class DSAPublicKeyImpl implements DSAPublicKey
{
    public static final long serialVersionUID = 0L;
    private final BigInteger y;
    private final DSAParams params;
    
    public BigInteger getY() {
        return this.y;
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
    
    DSAPublicKeyImpl(final BigInteger y, final DSAParams params) {
        this.y = y;
        this.params = params;
    }
}
