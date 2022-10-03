package cryptix.jce.provider.rsa;

import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;

public final class RSAPrivateCrtKeyCryptix implements RSAPrivateCrtKey
{
    private final BigInteger modulus;
    private final BigInteger publicExponent;
    private final BigInteger privateExponent;
    private final BigInteger primeP;
    private final BigInteger primeQ;
    private final BigInteger primeExponentP;
    private final BigInteger primeExponentQ;
    private final BigInteger crtCoefficient;
    
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
    
    public BigInteger getPrimeP() {
        return this.primeP;
    }
    
    public BigInteger getPrimeQ() {
        return this.primeQ;
    }
    
    public BigInteger getPrimeExponentP() {
        return this.primeExponentP;
    }
    
    public BigInteger getPrimeExponentQ() {
        return this.primeExponentQ;
    }
    
    public BigInteger getCrtCoefficient() {
        return this.crtCoefficient;
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
    }
    
    public String getAlgorithm() {
        return "RSA";
    }
    
    public String getFormat() {
        return "Cryptix";
    }
    
    public byte[] getEncoded() {
        throw new RuntimeException("NYI");
    }
    
    public RSAPrivateCrtKeyCryptix(final BigInteger modulus, final BigInteger publicExponent, final BigInteger privateExponent, final BigInteger primeP, final BigInteger primeQ, final BigInteger primeExponentP, final BigInteger primeExponentQ, final BigInteger crtCoefficient) {
        this.modulus = modulus;
        this.publicExponent = publicExponent;
        this.privateExponent = privateExponent;
        this.primeP = primeP;
        this.primeQ = primeQ;
        this.primeExponentP = primeExponentP;
        this.primeExponentQ = primeExponentQ;
        this.crtCoefficient = crtCoefficient;
    }
}
