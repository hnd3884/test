package sun.security.rsa;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import sun.security.util.DerInputStream;
import java.security.ProviderException;
import java.io.IOException;
import sun.security.util.BitArray;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import sun.security.x509.AlgorithmId;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import sun.security.x509.X509Key;

public final class RSAPublicKeyImpl extends X509Key implements RSAPublicKey
{
    private static final long serialVersionUID = 2644735423591199609L;
    private static final BigInteger THREE;
    private BigInteger n;
    private BigInteger e;
    private AlgorithmParameterSpec keyParams;
    
    public static RSAPublicKey newKey(final byte[] array) throws InvalidKeyException {
        return new RSAPublicKeyImpl(array);
    }
    
    public static RSAPublicKey newKey(final RSAUtil.KeyType keyType, final AlgorithmParameterSpec algorithmParameterSpec, final BigInteger bigInteger, final BigInteger bigInteger2) throws InvalidKeyException {
        return new RSAPublicKeyImpl(RSAUtil.createAlgorithmId(keyType, algorithmParameterSpec), bigInteger, bigInteger2);
    }
    
    RSAPublicKeyImpl(final AlgorithmId algid, final BigInteger n, final BigInteger e) throws InvalidKeyException {
        RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), e);
        checkExponentRange(n, e);
        this.n = n;
        this.e = e;
        this.keyParams = RSAUtil.getParamSpec(algid);
        this.algid = algid;
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putInteger(n);
            derOutputStream.putInteger(e);
            final byte[] byteArray = new DerValue((byte)48, derOutputStream.toByteArray()).toByteArray();
            this.setKey(new BitArray(byteArray.length * 8, byteArray));
        }
        catch (final IOException ex) {
            throw new InvalidKeyException(ex);
        }
    }
    
    RSAPublicKeyImpl(final byte[] array) throws InvalidKeyException {
        if (array == null || array.length == 0) {
            throw new InvalidKeyException("Missing key encoding");
        }
        this.decode(array);
        RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
        checkExponentRange(this.n, this.e);
        try {
            this.keyParams = RSAUtil.getParamSpec(this.algid);
        }
        catch (final ProviderException ex) {
            throw new InvalidKeyException(ex);
        }
    }
    
    static void checkExponentRange(final BigInteger bigInteger, final BigInteger bigInteger2) throws InvalidKeyException {
        if (bigInteger2.compareTo(bigInteger) >= 0) {
            throw new InvalidKeyException("exponent is larger than modulus");
        }
        if (bigInteger2.compareTo(RSAPublicKeyImpl.THREE) < 0) {
            throw new InvalidKeyException("exponent is smaller than 3");
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
    public BigInteger getPublicExponent() {
        return this.e;
    }
    
    @Override
    public AlgorithmParameterSpec getParams() {
        return this.keyParams;
    }
    
    @Override
    protected void parseKeyBits() throws InvalidKeyException {
        try {
            final DerValue derValue = new DerInputStream(this.getKey().toByteArray()).getDerValue();
            if (derValue.tag != 48) {
                throw new IOException("Not a SEQUENCE");
            }
            final DerInputStream data = derValue.data;
            this.n = data.getPositiveBigInteger();
            this.e = data.getPositiveBigInteger();
            if (derValue.data.available() != 0) {
                throw new IOException("Extra data available");
            }
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("Invalid RSA public key", ex);
        }
    }
    
    @Override
    public String toString() {
        return "Sun " + this.getAlgorithm() + " public key, " + this.n.bitLength() + " bits\n  params: " + this.keyParams + "\n  modulus: " + this.n + "\n  public exponent: " + this.e;
    }
    
    protected Object writeReplace() throws ObjectStreamException {
        return new KeyRep(KeyRep.Type.PUBLIC, this.getAlgorithm(), this.getFormat(), this.getEncoded());
    }
    
    static {
        THREE = BigInteger.valueOf(3L);
    }
}
