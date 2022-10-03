package org.bouncycastle.jce.provider;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.DSAParameter;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.interfaces.DSAParams;
import java.math.BigInteger;
import java.security.interfaces.DSAPublicKey;

public class JDKDSAPublicKey implements DSAPublicKey
{
    private static final long serialVersionUID = 1752452449903495175L;
    private BigInteger y;
    private DSAParams dsaSpec;
    
    JDKDSAPublicKey(final DSAPublicKeySpec dsaPublicKeySpec) {
        this.y = dsaPublicKeySpec.getY();
        this.dsaSpec = new DSAParameterSpec(dsaPublicKeySpec.getP(), dsaPublicKeySpec.getQ(), dsaPublicKeySpec.getG());
    }
    
    JDKDSAPublicKey(final DSAPublicKey dsaPublicKey) {
        this.y = dsaPublicKey.getY();
        this.dsaSpec = dsaPublicKey.getParams();
    }
    
    JDKDSAPublicKey(final DSAPublicKeyParameters dsaPublicKeyParameters) {
        this.y = dsaPublicKeyParameters.getY();
        this.dsaSpec = new DSAParameterSpec(dsaPublicKeyParameters.getParameters().getP(), dsaPublicKeyParameters.getParameters().getQ(), dsaPublicKeyParameters.getParameters().getG());
    }
    
    JDKDSAPublicKey(final BigInteger y, final DSAParameterSpec dsaSpec) {
        this.y = y;
        this.dsaSpec = dsaSpec;
    }
    
    JDKDSAPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        ASN1Integer asn1Integer;
        try {
            asn1Integer = (ASN1Integer)subjectPublicKeyInfo.parsePublicKey();
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("invalid info structure in DSA public key");
        }
        this.y = asn1Integer.getValue();
        if (this.isNotNull(subjectPublicKeyInfo.getAlgorithm().getParameters())) {
            final DSAParameter instance = DSAParameter.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            this.dsaSpec = new DSAParameterSpec(instance.getP(), instance.getQ(), instance.getG());
        }
    }
    
    private boolean isNotNull(final ASN1Encodable asn1Encodable) {
        return asn1Encodable != null && !DERNull.INSTANCE.equals(asn1Encodable);
    }
    
    public String getAlgorithm() {
        return "DSA";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        try {
            if (this.dsaSpec == null) {
                return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(this.y)).getEncoded("DER");
            }
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG())), new ASN1Integer(this.y)).getEncoded("DER");
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public DSAParams getParams() {
        return this.dsaSpec;
    }
    
    public BigInteger getY() {
        return this.y;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("DSA Public Key").append(lineSeparator);
        sb.append("            y: ").append(this.getY().toString(16)).append(lineSeparator);
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getQ().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DSAPublicKey)) {
            return false;
        }
        final DSAPublicKey dsaPublicKey = (DSAPublicKey)o;
        return this.getY().equals(dsaPublicKey.getY()) && this.getParams().getG().equals(dsaPublicKey.getParams().getG()) && this.getParams().getP().equals(dsaPublicKey.getParams().getP()) && this.getParams().getQ().equals(dsaPublicKey.getParams().getQ());
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.y = (BigInteger)objectInputStream.readObject();
        this.dsaSpec = new DSAParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.y);
        objectOutputStream.writeObject(this.dsaSpec.getP());
        objectOutputStream.writeObject(this.dsaSpec.getQ());
        objectOutputStream.writeObject(this.dsaSpec.getG());
    }
}
