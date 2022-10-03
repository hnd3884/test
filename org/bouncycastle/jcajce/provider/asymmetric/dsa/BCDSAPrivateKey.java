package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import org.bouncycastle.util.Strings;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import java.security.interfaces.DSAParams;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.security.interfaces.DSAPrivateKey;

public class BCDSAPrivateKey implements DSAPrivateKey, PKCS12BagAttributeCarrier
{
    private static final long serialVersionUID = -4677259546958385734L;
    private BigInteger x;
    private transient DSAParams dsaSpec;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCDSAPrivateKey() {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    BCDSAPrivateKey(final DSAPrivateKey dsaPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dsaPrivateKey.getX();
        this.dsaSpec = dsaPrivateKey.getParams();
    }
    
    BCDSAPrivateKey(final DSAPrivateKeySpec dsaPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dsaPrivateKeySpec.getX();
        this.dsaSpec = new DSAParameterSpec(dsaPrivateKeySpec.getP(), dsaPrivateKeySpec.getQ(), dsaPrivateKeySpec.getG());
    }
    
    public BCDSAPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final DSAParameter instance = DSAParameter.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        this.x = ((ASN1Integer)privateKeyInfo.parsePrivateKey()).getValue();
        this.dsaSpec = new DSAParameterSpec(instance.getP(), instance.getQ(), instance.getG());
    }
    
    BCDSAPrivateKey(final DSAPrivateKeyParameters dsaPrivateKeyParameters) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dsaPrivateKeyParameters.getX();
        this.dsaSpec = new DSAParameterSpec(dsaPrivateKeyParameters.getParameters().getP(), dsaPrivateKeyParameters.getParameters().getQ(), dsaPrivateKeyParameters.getParameters().getG());
    }
    
    public String getAlgorithm() {
        return "DSA";
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG()).toASN1Primitive()), new ASN1Integer(this.getX()));
    }
    
    public DSAParams getParams() {
        return this.dsaSpec;
    }
    
    public BigInteger getX() {
        return this.x;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DSAPrivateKey)) {
            return false;
        }
        final DSAPrivateKey dsaPrivateKey = (DSAPrivateKey)o;
        return this.getX().equals(dsaPrivateKey.getX()) && this.getParams().getG().equals(dsaPrivateKey.getParams().getG()) && this.getParams().getP().equals(dsaPrivateKey.getParams().getP()) && this.getParams().getQ().equals(dsaPrivateKey.getParams().getQ());
    }
    
    @Override
    public int hashCode() {
        return this.getX().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getQ().hashCode();
    }
    
    public void setBagAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier, final ASN1Encodable asn1Encodable) {
        this.attrCarrier.setBagAttribute(asn1ObjectIdentifier, asn1Encodable);
    }
    
    public ASN1Encodable getBagAttribute(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return this.attrCarrier.getBagAttribute(asn1ObjectIdentifier);
    }
    
    public Enumeration getBagAttributeKeys() {
        return this.attrCarrier.getBagAttributeKeys();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.dsaSpec = new DSAParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject());
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.dsaSpec.getP());
        objectOutputStream.writeObject(this.dsaSpec.getQ());
        objectOutputStream.writeObject(this.dsaSpec.getG());
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        final BigInteger modPow = this.getParams().getG().modPow(this.x, this.getParams().getP());
        sb.append("DSA Private Key [").append(DSAUtil.generateKeyFingerprint(modPow, this.getParams())).append("]").append(lineSeparator);
        sb.append("            y: ").append(modPow.toString(16)).append(lineSeparator);
        return sb.toString();
    }
}
