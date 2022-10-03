package sun.security.rsa;

import java.io.IOException;
import java.security.InvalidKeyException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import sun.security.x509.AlgorithmId;
import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateKey;
import sun.security.pkcs.PKCS8Key;

public final class RSAPrivateKeyImpl extends PKCS8Key implements RSAPrivateKey
{
    private static final long serialVersionUID = -33106691987952810L;
    private final BigInteger n;
    private final BigInteger d;
    private final AlgorithmParameterSpec keyParams;
    
    RSAPrivateKeyImpl(final AlgorithmId algid, final BigInteger n, final BigInteger d) throws InvalidKeyException {
        RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), null);
        this.n = n;
        this.d = d;
        this.keyParams = RSAUtil.getParamSpec(algid);
        this.algid = algid;
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putInteger(0);
            derOutputStream.putInteger(n);
            derOutputStream.putInteger(0);
            derOutputStream.putInteger(d);
            derOutputStream.putInteger(0);
            derOutputStream.putInteger(0);
            derOutputStream.putInteger(0);
            derOutputStream.putInteger(0);
            derOutputStream.putInteger(0);
            this.key = new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
        }
        catch (final IOException ex) {
            throw new InvalidKeyException(ex);
        }
    }
    
    @Override
    public String getAlgorithm() {
        return this.algid.getName();
    }
    
    @Override
    public BigInteger getModulus() {
        return this.n;
    }
    
    @Override
    public BigInteger getPrivateExponent() {
        return this.d;
    }
    
    @Override
    public AlgorithmParameterSpec getParams() {
        return this.keyParams;
    }
    
    @Override
    public String toString() {
        return "Sun " + this.getAlgorithm() + " private key, " + this.n.bitLength() + " bits\n  params: " + this.keyParams + "\n  modulus: " + this.n + "\n  private exponent: " + this.d;
    }
}
