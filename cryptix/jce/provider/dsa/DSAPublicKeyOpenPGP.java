package cryptix.jce.provider.dsa;

import java.io.IOException;
import java.io.OutputStream;
import cryptix.jce.util.MPIOutputStream;
import java.io.ByteArrayOutputStream;
import java.security.interfaces.DSAParams;
import java.math.BigInteger;
import java.security.interfaces.DSAPublicKey;

final class DSAPublicKeyOpenPGP implements DSAPublicKey
{
    private final BigInteger y;
    private final DSAParams params;
    
    public BigInteger getY() {
        return this.y;
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
            mos.write(this.y);
            mos.flush();
            mos.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException e) {
            throw new RuntimeException("PANIC");
        }
    }
    
    DSAPublicKeyOpenPGP(final BigInteger y, final DSAParams params) {
        this.y = y;
        this.params = params;
    }
}
