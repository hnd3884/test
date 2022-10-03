package org.bouncycastle.jcajce.provider.asymmetric.dh;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import javax.crypto.spec.DHPrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import javax.crypto.spec.DHParameterSpec;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import javax.crypto.interfaces.DHPrivateKey;

public class BCDHPrivateKey implements DHPrivateKey, PKCS12BagAttributeCarrier
{
    static final long serialVersionUID = 311058815616901812L;
    private BigInteger x;
    private transient DHParameterSpec dhSpec;
    private transient PrivateKeyInfo info;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCDHPrivateKey() {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    BCDHPrivateKey(final DHPrivateKey dhPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKey.getX();
        this.dhSpec = dhPrivateKey.getParams();
    }
    
    BCDHPrivateKey(final DHPrivateKeySpec dhPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKeySpec.getX();
        this.dhSpec = new DHParameterSpec(dhPrivateKeySpec.getP(), dhPrivateKeySpec.getG());
    }
    
    public BCDHPrivateKey(final PrivateKeyInfo info) throws IOException {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final ASN1Sequence instance = ASN1Sequence.getInstance(info.getPrivateKeyAlgorithm().getParameters());
        final ASN1Integer asn1Integer = (ASN1Integer)info.parsePrivateKey();
        final ASN1ObjectIdentifier algorithm = info.getPrivateKeyAlgorithm().getAlgorithm();
        this.info = info;
        this.x = asn1Integer.getValue();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            final DHParameter instance2 = DHParameter.getInstance(instance);
            if (instance2.getL() != null) {
                this.dhSpec = new DHParameterSpec(instance2.getP(), instance2.getG(), instance2.getL().intValue());
            }
            else {
                this.dhSpec = new DHParameterSpec(instance2.getP(), instance2.getG());
            }
        }
        else {
            if (!algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
                throw new IllegalArgumentException("unknown algorithm type: " + algorithm);
            }
            final DomainParameters instance3 = DomainParameters.getInstance(instance);
            this.dhSpec = new DHParameterSpec(instance3.getP(), instance3.getG());
        }
    }
    
    BCDHPrivateKey(final DHPrivateKeyParameters dhPrivateKeyParameters) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKeyParameters.getX();
        this.dhSpec = new DHParameterSpec(dhPrivateKeyParameters.getParameters().getP(), dhPrivateKeyParameters.getParameters().getG(), dhPrivateKeyParameters.getParameters().getL());
    }
    
    public String getAlgorithm() {
        return "DH";
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        try {
            if (this.info != null) {
                return this.info.getEncoded("DER");
            }
            return new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL()).toASN1Primitive()), new ASN1Integer(this.getX())).getEncoded("DER");
        }
        catch (final Exception ex) {
            return null;
        }
    }
    
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }
    
    public BigInteger getX() {
        return this.x;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof DHPrivateKey)) {
            return false;
        }
        final DHPrivateKey dhPrivateKey = (DHPrivateKey)o;
        return this.getX().equals(dhPrivateKey.getX()) && this.getParams().getG().equals(dhPrivateKey.getParams().getG()) && this.getParams().getP().equals(dhPrivateKey.getParams().getP()) && this.getParams().getL() == dhPrivateKey.getParams().getL();
    }
    
    @Override
    public int hashCode() {
        return this.getX().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getL();
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
        this.dhSpec = new DHParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), objectInputStream.readInt());
        this.info = null;
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.dhSpec.getP());
        objectOutputStream.writeObject(this.dhSpec.getG());
        objectOutputStream.writeInt(this.dhSpec.getL());
    }
}
