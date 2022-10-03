package cryptix.jce.provider.dsa;

import java.io.IOException;
import java.io.OutputStream;
import cryptix.jce.util.MPIOutputStream;
import java.io.ByteArrayOutputStream;
import java.security.interfaces.DSAParams;
import java.math.BigInteger;
import java.security.interfaces.DSAPrivateKey;

final class DSAPrivateKeyCryptix implements DSAPrivateKey
{
    private final BigInteger x;
    private final DSAParams params;
    
    public BigInteger getX() {
        return this.x;
    }
    
    public DSAParams getParams() {
        return this.params;
    }
    
    public String getAlgorithm() {
        return "DSA";
    }
    
    public String getFormat() {
        return "Cryptix";
    }
    
    public byte[] getEncoded() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final MPIOutputStream mos = new MPIOutputStream((OutputStream)byteArrayOutputStream);
            mos.write(this.params.getP());
            mos.write(this.params.getQ());
            mos.write(this.params.getG());
            mos.write(this.x);
            mos.flush();
            mos.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException e) {
            throw new RuntimeException("PANIC");
        }
    }
    
    DSAPrivateKeyCryptix(final BigInteger x, final DSAParams params) {
        this.x = x;
        this.params = params;
    }
}
