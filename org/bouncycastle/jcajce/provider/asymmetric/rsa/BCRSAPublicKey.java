package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import java.security.interfaces.RSAPublicKey;

public class BCRSAPublicKey implements RSAPublicKey
{
    private static final AlgorithmIdentifier DEFAULT_ALGORITHM_IDENTIFIER;
    static final long serialVersionUID = 2675817738516720772L;
    private BigInteger modulus;
    private BigInteger publicExponent;
    private transient AlgorithmIdentifier algorithmIdentifier;
    
    BCRSAPublicKey(final RSAKeyParameters rsaKeyParameters) {
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = rsaKeyParameters.getModulus();
        this.publicExponent = rsaKeyParameters.getExponent();
    }
    
    BCRSAPublicKey(final RSAPublicKeySpec rsaPublicKeySpec) {
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = rsaPublicKeySpec.getModulus();
        this.publicExponent = rsaPublicKeySpec.getPublicExponent();
    }
    
    BCRSAPublicKey(final RSAPublicKey rsaPublicKey) {
        this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        this.modulus = rsaPublicKey.getModulus();
        this.publicExponent = rsaPublicKey.getPublicExponent();
    }
    
    BCRSAPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.populateFromPublicKeyInfo(subjectPublicKeyInfo);
    }
    
    private void populateFromPublicKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        try {
            final org.bouncycastle.asn1.pkcs.RSAPublicKey instance = org.bouncycastle.asn1.pkcs.RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
            this.algorithmIdentifier = subjectPublicKeyInfo.getAlgorithm();
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
        return KeyUtil.getEncodedSubjectPublicKeyInfo(this.algorithmIdentifier, new org.bouncycastle.asn1.pkcs.RSAPublicKey(this.getModulus(), this.getPublicExponent()));
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
        sb.append("RSA Public Key [").append(RSAUtil.generateKeyFingerprint(this.getModulus(), this.getPublicExponent())).append("]").append(lineSeparator);
        sb.append("            modulus: ").append(this.getModulus().toString(16)).append(lineSeparator);
        sb.append("    public exponent: ").append(this.getPublicExponent().toString(16)).append(lineSeparator);
        return sb.toString();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.algorithmIdentifier = AlgorithmIdentifier.getInstance(objectInputStream.readObject());
        }
        catch (final Exception ex) {
            this.algorithmIdentifier = BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER;
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (!this.algorithmIdentifier.equals(BCRSAPublicKey.DEFAULT_ALGORITHM_IDENTIFIER)) {
            objectOutputStream.writeObject(this.algorithmIdentifier.getEncoded());
        }
    }
    
    static {
        DEFAULT_ALGORITHM_IDENTIFIER = new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
    }
}
