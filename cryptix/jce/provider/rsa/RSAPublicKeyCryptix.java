package cryptix.jce.provider.rsa;

import java.io.IOException;
import java.io.OutputStream;
import cryptix.jce.util.MPIOutputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

public final class RSAPublicKeyCryptix implements RSAPublicKey
{
    private final BigInteger n;
    private final BigInteger e;
    
    public BigInteger getModulus() {
        return this.n;
    }
    
    public BigInteger getPublicExponent() {
        return this.e;
    }
    
    public String getAlgorithm() {
        return "RSA";
    }
    
    public String getFormat() {
        return "Cryptix";
    }
    
    public byte[] getEncoded() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final MPIOutputStream mos = new MPIOutputStream((OutputStream)byteArrayOutputStream);
            mos.write(this.n);
            mos.write(this.e);
            mos.flush();
            mos.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException e) {
            throw new RuntimeException("PANIC");
        }
    }
    
    public RSAPublicKeyCryptix(final BigInteger n, final BigInteger e) {
        this.n = n;
        this.e = e;
    }
}
