package cryptix.jce.provider.rsa;

import java.io.IOException;
import java.io.OutputStream;
import cryptix.jce.util.MPIOutputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;

public final class RSAPrivateKeyCryptix implements RSAPrivateKey
{
    private final BigInteger n;
    private final BigInteger d;
    
    public BigInteger getModulus() {
        return this.n;
    }
    
    public BigInteger getPrivateExponent() {
        return this.d;
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
            mos.write(this.d);
            mos.flush();
            mos.close();
            return byteArrayOutputStream.toByteArray();
        }
        catch (final IOException e) {
            throw new RuntimeException("PANIC");
        }
    }
    
    public RSAPrivateKeyCryptix(final BigInteger n, final BigInteger d) {
        this.n = n;
        this.d = d;
    }
}
