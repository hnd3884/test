package org.bouncycastle.jce.provider;

import org.bouncycastle.util.Strings;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;

public class JCERSAPublicKey implements RSAPublicKey
{
    static final long serialVersionUID = 2675817738516720772L;
    private BigInteger modulus;
    private BigInteger publicExponent;
    
    JCERSAPublicKey(final RSAKeyParameters rsaKeyParameters) {
        this.modulus = rsaKeyParameters.getModulus();
        this.publicExponent = rsaKeyParameters.getExponent();
    }
    
    JCERSAPublicKey(final RSAPublicKeySpec rsaPublicKeySpec) {
        this.modulus = rsaPublicKeySpec.getModulus();
        this.publicExponent = rsaPublicKeySpec.getPublicExponent();
    }
    
    JCERSAPublicKey(final RSAPublicKey rsaPublicKey) {
        this.modulus = rsaPublicKey.getModulus();
        this.publicExponent = rsaPublicKey.getPublicExponent();
    }
    
    JCERSAPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        try {
            final org.bouncycastle.asn1.pkcs.RSAPublicKey instance = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
            this.modulus = instance.getModulus();
            this.publicExponent = instance.getPublicExponent();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("invalid info structure in RSA public key");
        }
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    public BigInteger getPublicExponent() {
        return this.publicExponent;
    }
    
    public String getAlgorithm() {
        return "RSA";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new org.bouncycastle.asn1.pkcs.RSAPublicKey(this.getModulus(), this.getPublicExponent()));
    }
    
    @Override
    public int hashCode() {
        return this.getModulus().hashCode() ^ this.getPublicExponent().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RSAPublicKey)) {
            return false;
        }
        final RSAPublicKey rsaPublicKey = (RSAPublicKey)o;
        return this.getModulus().equals(rsaPublicKey.getModulus()) && this.getPublicExponent().equals(rsaPublicKey.getPublicExponent());
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("RSA Public Key").append(lineSeparator);
        sb.append("            modulus: ").append(this.getModulus().toString(16)).append(lineSeparator);
        sb.append("    public exponent: ").append(this.getPublicExponent().toString(16)).append(lineSeparator);
        return sb.toString();
    }
}
