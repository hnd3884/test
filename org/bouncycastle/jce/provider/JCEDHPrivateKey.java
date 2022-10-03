package org.bouncycastle.jce.provider;

import java.util.Enumeration;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.DHDomainParameters;
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

public class JCEDHPrivateKey implements DHPrivateKey, PKCS12BagAttributeCarrier
{
    static final long serialVersionUID = 311058815616901812L;
    BigInteger x;
    private DHParameterSpec dhSpec;
    private PrivateKeyInfo info;
    private PKCS12BagAttributeCarrier attrCarrier;
    
    protected JCEDHPrivateKey() {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    JCEDHPrivateKey(final DHPrivateKey dhPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKey.getX();
        this.dhSpec = dhPrivateKey.getParams();
    }
    
    JCEDHPrivateKey(final DHPrivateKeySpec dhPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKeySpec.getX();
        this.dhSpec = new DHParameterSpec(dhPrivateKeySpec.getP(), dhPrivateKeySpec.getG());
    }
    
    JCEDHPrivateKey(final PrivateKeyInfo info) throws IOException {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final ASN1Sequence instance = ASN1Sequence.getInstance(info.getAlgorithmId().getParameters());
        final ASN1Integer instance2 = ASN1Integer.getInstance(info.parsePrivateKey());
        final ASN1ObjectIdentifier algorithm = info.getAlgorithmId().getAlgorithm();
        this.info = info;
        this.x = instance2.getValue();
        if (algorithm.equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            final DHParameter instance3 = DHParameter.getInstance(instance);
            if (instance3.getL() != null) {
                this.dhSpec = new DHParameterSpec(instance3.getP(), instance3.getG(), instance3.getL().intValue());
            }
            else {
                this.dhSpec = new DHParameterSpec(instance3.getP(), instance3.getG());
            }
        }
        else {
            if (!algorithm.equals(X9ObjectIdentifiers.dhpublicnumber)) {
                throw new IllegalArgumentException("unknown algorithm type: " + algorithm);
            }
            final DHDomainParameters instance4 = DHDomainParameters.getInstance(instance);
            this.dhSpec = new DHParameterSpec(instance4.getP().getValue(), instance4.getG().getValue());
        }
    }
    
    JCEDHPrivateKey(final DHPrivateKeyParameters dhPrivateKeyParameters) {
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
            return new PrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.dhKeyAgreement, new DHParameter(this.dhSpec.getP(), this.dhSpec.getG(), this.dhSpec.getL())), new ASN1Integer(this.getX())).getEncoded("DER");
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public DHParameterSpec getParams() {
        return this.dhSpec;
    }
    
    public BigInteger getX() {
        return this.x;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.x = (BigInteger)objectInputStream.readObject();
        this.dhSpec = new DHParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), objectInputStream.readInt());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.getX());
        objectOutputStream.writeObject(this.dhSpec.getP());
        objectOutputStream.writeObject(this.dhSpec.getG());
        objectOutputStream.writeInt(this.dhSpec.getL());
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
}
