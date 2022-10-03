package cryptix.jce.provider.elgamal;

import java.io.IOException;
import java.io.OutputStream;
import cryptix.jce.util.MPIOutputStream;
import java.io.ByteArrayOutputStream;
import cryptix.jce.ElGamalParams;
import java.math.BigInteger;
import cryptix.jce.ElGamalPublicKey;

final class ElGamalPublicKeyCryptix implements ElGamalPublicKey
{
    private final BigInteger y;
    private final ElGamalParams params;
    
    public BigInteger getY() {
        return this.y;
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
            mos.write(this.y);
            mos.flush();
            mos.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException e) {
            throw new RuntimeException("PANIC");
        }
    }
    
    ElGamalPublicKeyCryptix(final BigInteger y, final ElGamalParams params) {
        this.y = y;
        this.params = params;
    }
}
