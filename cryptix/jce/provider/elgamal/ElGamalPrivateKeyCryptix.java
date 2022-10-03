package cryptix.jce.provider.elgamal;

import java.io.IOException;
import java.io.OutputStream;
import cryptix.jce.util.MPIOutputStream;
import java.io.ByteArrayOutputStream;
import cryptix.jce.ElGamalParams;
import java.math.BigInteger;
import cryptix.jce.ElGamalPrivateKey;

final class ElGamalPrivateKeyCryptix implements ElGamalPrivateKey
{
    private final BigInteger x;
    private final ElGamalParams params;
    
    public BigInteger getX() {
        return this.x;
    }
    
    public ElGamalParams getParams() {
        return this.params;
    }
    
    public String getAlgorithm() {
        return "ElGamal";
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
    
    ElGamalPrivateKeyCryptix(final BigInteger x, final ElGamalParams params) {
        this.x = x;
        this.params = params;
    }
}
