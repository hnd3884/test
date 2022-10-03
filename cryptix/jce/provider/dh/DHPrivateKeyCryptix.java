package cryptix.jce.provider.dh;

import java.io.IOException;
import java.io.OutputStream;
import cryptix.jce.util.MPIOutputStream;
import java.io.ByteArrayOutputStream;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import javax.crypto.interfaces.DHPrivateKey;

final class DHPrivateKeyCryptix implements DHPrivateKey
{
    private final BigInteger x;
    private final DHParameterSpec params;
    
    public BigInteger getX() {
        return this.x;
    }
    
    public DHParameterSpec getParams() {
        return this.params;
    }
    
    public String getAlgorithm() {
        return "DH";
    }
    
    public String getFormat() {
        return "Cryptix";
    }
    
    public byte[] getEncoded() {
        try {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final MPIOutputStream mos = new MPIOutputStream((OutputStream)byteArrayOutputStream);
            mos.write(this.params.getP());
            mos.write(BigInteger.valueOf(this.params.getL()));
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
    
    DHPrivateKeyCryptix(final BigInteger x, final DHParameterSpec params) {
        this.x = x;
        this.params = params;
    }
}
