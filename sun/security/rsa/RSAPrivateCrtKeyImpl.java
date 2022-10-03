package sun.security.rsa;

import sun.security.util.DerInputStream;
import java.io.IOException;
import sun.security.util.DerValue;
import sun.security.util.DerOutputStream;
import java.security.ProviderException;
import sun.security.x509.AlgorithmId;
import java.security.InvalidKeyException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.AlgorithmParameterSpec;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import sun.security.pkcs.PKCS8Key;

public final class RSAPrivateCrtKeyImpl extends PKCS8Key implements RSAPrivateCrtKey
{
    private static final long serialVersionUID = -1326088454257084918L;
    private BigInteger n;
    private BigInteger e;
    private BigInteger d;
    private BigInteger p;
    private BigInteger q;
    private BigInteger pe;
    private BigInteger qe;
    private BigInteger coeff;
    private AlgorithmParameterSpec keyParams;
    
    public static RSAPrivateKey newKey(final byte[] array) throws InvalidKeyException {
        final RSAPrivateCrtKeyImpl rsaPrivateCrtKeyImpl = new RSAPrivateCrtKeyImpl(array);
        if (rsaPrivateCrtKeyImpl.getPublicExponent().signum() == 0 || rsaPrivateCrtKeyImpl.getPrimeExponentP().signum() == 0 || rsaPrivateCrtKeyImpl.getPrimeExponentQ().signum() == 0 || rsaPrivateCrtKeyImpl.getPrimeP().signum() == 0 || rsaPrivateCrtKeyImpl.getPrimeQ().signum() == 0 || rsaPrivateCrtKeyImpl.getCrtCoefficient().signum() == 0) {
            return new RSAPrivateKeyImpl(rsaPrivateCrtKeyImpl.algid, rsaPrivateCrtKeyImpl.getModulus(), rsaPrivateCrtKeyImpl.getPrivateExponent());
        }
        return rsaPrivateCrtKeyImpl;
    }
    
    public static RSAPrivateKey newKey(final RSAUtil.KeyType keyType, final AlgorithmParameterSpec algorithmParameterSpec, final BigInteger bigInteger, final BigInteger bigInteger2, final BigInteger bigInteger3, final BigInteger bigInteger4, final BigInteger bigInteger5, final BigInteger bigInteger6, final BigInteger bigInteger7, final BigInteger bigInteger8) throws InvalidKeyException {
        final AlgorithmId algorithmId = RSAUtil.createAlgorithmId(keyType, algorithmParameterSpec);
        if (bigInteger2.signum() == 0 || bigInteger4.signum() == 0 || bigInteger5.signum() == 0 || bigInteger6.signum() == 0 || bigInteger7.signum() == 0 || bigInteger8.signum() == 0) {
            return new RSAPrivateKeyImpl(algorithmId, bigInteger, bigInteger3);
        }
        return new RSAPrivateCrtKeyImpl(algorithmId, bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8);
    }
    
    RSAPrivateCrtKeyImpl(final byte[] array) throws InvalidKeyException {
        if (array == null || array.length == 0) {
            throw new InvalidKeyException("Missing key encoding");
        }
        this.decode(array);
        RSAKeyFactory.checkRSAProviderKeyLengths(this.n.bitLength(), this.e);
        try {
            this.keyParams = RSAUtil.getParamSpec(this.algid);
        }
        catch (final ProviderException ex) {
            throw new InvalidKeyException(ex);
        }
    }
    
    RSAPrivateCrtKeyImpl(final AlgorithmId algid, final BigInteger n, final BigInteger e, final BigInteger d, final BigInteger p9, final BigInteger q, final BigInteger pe, final BigInteger qe, final BigInteger coeff) throws InvalidKeyException {
        RSAKeyFactory.checkRSAProviderKeyLengths(n.bitLength(), e);
        this.n = n;
        this.e = e;
        this.d = d;
        this.p = p9;
        this.q = q;
        this.pe = pe;
        this.qe = qe;
        this.coeff = coeff;
        this.keyParams = RSAUtil.getParamSpec(algid);
        this.algid = algid;
        try {
            final DerOutputStream derOutputStream = new DerOutputStream();
            derOutputStream.putInteger(0);
            derOutputStream.putInteger(n);
            derOutputStream.putInteger(e);
            derOutputStream.putInteger(d);
            derOutputStream.putInteger(p9);
            derOutputStream.putInteger(q);
            derOutputStream.putInteger(pe);
            derOutputStream.putInteger(qe);
            derOutputStream.putInteger(coeff);
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
    public BigInteger getPublicExponent() {
        return this.e;
    }
    
    @Override
    public BigInteger getPrivateExponent() {
        return this.d;
    }
    
    @Override
    public BigInteger getPrimeP() {
        return this.p;
    }
    
    @Override
    public BigInteger getPrimeQ() {
        return this.q;
    }
    
    @Override
    public BigInteger getPrimeExponentP() {
        return this.pe;
    }
    
    @Override
    public BigInteger getPrimeExponentQ() {
        return this.qe;
    }
    
    @Override
    public BigInteger getCrtCoefficient() {
        return this.coeff;
    }
    
    @Override
    public AlgorithmParameterSpec getParams() {
        return this.keyParams;
    }
    
    @Override
    public String toString() {
        return "SunRsaSign " + this.getAlgorithm() + " private CRT key, " + this.n.bitLength() + " bits\n  params: " + this.keyParams + "\n  modulus: " + this.n + "\n  private exponent: " + this.d;
    }
    
    @Override
    protected void parseKeyBits() throws InvalidKeyException {
        try {
            final DerValue derValue = new DerInputStream(this.key).getDerValue();
            if (derValue.tag != 48) {
                throw new IOException("Not a SEQUENCE");
            }
            final DerInputStream data = derValue.data;
            if (data.getInteger() != 0) {
                throw new IOException("Version must be 0");
            }
            this.n = data.getPositiveBigInteger();
            this.e = data.getPositiveBigInteger();
            this.d = data.getPositiveBigInteger();
            this.p = data.getPositiveBigInteger();
            this.q = data.getPositiveBigInteger();
            this.pe = data.getPositiveBigInteger();
            this.qe = data.getPositiveBigInteger();
            this.coeff = data.getPositiveBigInteger();
            if (derValue.data.available() != 0) {
                throw new IOException("Extra data available");
            }
        }
        catch (final IOException ex) {
            throw new InvalidKeyException("Invalid RSA private key", ex);
        }
    }
}
