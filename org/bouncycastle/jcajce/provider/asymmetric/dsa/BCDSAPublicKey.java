package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.util.Strings;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.DSAParameter;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.interfaces.DSAParams;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import java.math.BigInteger;
import java.security.interfaces.DSAPublicKey;

public class BCDSAPublicKey implements DSAPublicKey
{
    private static final long serialVersionUID = 1752452449903495175L;
    private static BigInteger ZERO;
    private BigInteger y;
    private transient DSAPublicKeyParameters lwKeyParams;
    private transient DSAParams dsaSpec;
    
    BCDSAPublicKey(final DSAPublicKeySpec dsaPublicKeySpec) {
        this.y = dsaPublicKeySpec.getY();
        this.dsaSpec = new DSAParameterSpec(dsaPublicKeySpec.getP(), dsaPublicKeySpec.getQ(), dsaPublicKeySpec.getG());
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }
    
    BCDSAPublicKey(final DSAPublicKey dsaPublicKey) {
        this.y = dsaPublicKey.getY();
        this.dsaSpec = dsaPublicKey.getParams();
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }
    
    BCDSAPublicKey(final DSAPublicKeyParameters lwKeyParams) {
        this.y = lwKeyParams.getY();
        if (lwKeyParams != null) {
            this.dsaSpec = new DSAParameterSpec(lwKeyParams.getParameters().getP(), lwKeyParams.getParameters().getQ(), lwKeyParams.getParameters().getG());
        }
        else {
            this.dsaSpec = null;
        }
        this.lwKeyParams = lwKeyParams;
    }
    
    public BCDSAPublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
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
        else {
            this.dsaSpec = null;
        }
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }
    
    private boolean isNotNull(final ASN1Encodable asn1Encodable) {
        return asn1Encodable != null && !DERNull.INSTANCE.equals(asn1Encodable.toASN1Primitive());
    }
    
    public String getAlgorithm() {
        return "DSA";
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    DSAPublicKeyParameters engineGetKeyParameters() {
        return this.lwKeyParams;
    }
    
    public byte[] getEncoded() {
        if (this.dsaSpec == null) {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(this.y));
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG()).toASN1Primitive()), new ASN1Integer(this.y));
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
        sb.append("DSA Public Key [").append(DSAUtil.generateKeyFingerprint(this.y, this.getParams())).append("]").append(lineSeparator);
        sb.append("            y: ").append(this.getY().toString(16)).append(lineSeparator);
        return sb.toString();
    }
    
    @Override
    public int hashCode() {
        if (this.dsaSpec != null) {
            return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getQ().hashCode();
        }
        return this.getY().hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DSAPublicKey)) {
            return false;
        }
        final DSAPublicKey dsaPublicKey = (DSAPublicKey)o;
        if (this.dsaSpec != null) {
            return this.getY().equals(dsaPublicKey.getY()) && dsaPublicKey.getParams() != null && this.getParams().getG().equals(dsaPublicKey.getParams().getG()) && this.getParams().getP().equals(dsaPublicKey.getParams().getP()) && this.getParams().getQ().equals(dsaPublicKey.getParams().getQ());
        }
        return this.getY().equals(dsaPublicKey.getY()) && dsaPublicKey.getParams() == null;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final BigInteger bigInteger = (BigInteger)objectInputStream.readObject();
        if (bigInteger.equals(BCDSAPublicKey.ZERO)) {
            this.dsaSpec = null;
        }
        else {
            this.dsaSpec = new DSAParameterSpec(bigInteger, (BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject());
        }
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.dsaSpec == null) {
            objectOutputStream.writeObject(BCDSAPublicKey.ZERO);
        }
        else {
            objectOutputStream.writeObject(this.dsaSpec.getP());
            objectOutputStream.writeObject(this.dsaSpec.getQ());
            objectOutputStream.writeObject(this.dsaSpec.getG());
        }
    }
    
    static {
        BCDSAPublicKey.ZERO = BigInteger.valueOf(0L);
    }
}
