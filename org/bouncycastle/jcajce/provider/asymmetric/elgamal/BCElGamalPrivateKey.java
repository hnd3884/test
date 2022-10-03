package org.bouncycastle.jcajce.provider.asymmetric.elgamal;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import javax.crypto.spec.DHPrivateKeySpec;
import org.bouncycastle.jce.spec.ElGamalPrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.spec.ElGamalParameterSpec;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import javax.crypto.interfaces.DHPrivateKey;
import org.bouncycastle.jce.interfaces.ElGamalPrivateKey;

public class BCElGamalPrivateKey implements ElGamalPrivateKey, DHPrivateKey, PKCS12BagAttributeCarrier
{
    static final long serialVersionUID = 4819350091141529678L;
    private BigInteger x;
    private transient ElGamalParameterSpec elSpec;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCElGamalPrivateKey() {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    BCElGamalPrivateKey(final ElGamalPrivateKey elGamalPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = elGamalPrivateKey.getX();
        this.elSpec = elGamalPrivateKey.getParameters();
    }
    
    BCElGamalPrivateKey(final DHPrivateKey dhPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKey.getX();
        this.elSpec = new ElGamalParameterSpec(dhPrivateKey.getParams().getP(), dhPrivateKey.getParams().getG());
    }
    
    BCElGamalPrivateKey(final ElGamalPrivateKeySpec elGamalPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = elGamalPrivateKeySpec.getX();
        this.elSpec = new ElGamalParameterSpec(elGamalPrivateKeySpec.getParams().getP(), elGamalPrivateKeySpec.getParams().getG());
    }
    
    BCElGamalPrivateKey(final DHPrivateKeySpec dhPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKeySpec.getX();
        this.elSpec = new ElGamalParameterSpec(dhPrivateKeySpec.getP(), dhPrivateKeySpec.getG());
    }
    
    BCElGamalPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final ElGamalParameter instance = ElGamalParameter.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        this.x = ASN1Integer.getInstance(privateKeyInfo.parsePrivateKey()).getValue();
        this.elSpec = new ElGamalParameterSpec(instance.getP(), instance.getG());
    }
    
    BCElGamalPrivateKey(final ElGamalPrivateKeyParameters elGamalPrivateKeyParameters) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = elGamalPrivateKeyParameters.getX();
        this.elSpec = new ElGamalParameterSpec(elGamalPrivateKeyParameters.getParameters().getP(), elGamalPrivateKeyParameters.getParameters().getG());
    }
    
    public String getAlgorithm() {
        return "ElGamal";
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), new ASN1Integer(this.getX())).getEncoded("DER");
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public ElGamalParameterSpec getParameters() {
        return this.elSpec;
    }
    
    public DHParameterSpec getParams() {
        return new DHParameterSpec(this.elSpec.getP(), this.elSpec.getG());
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
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.elSpec = new ElGamalParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject());
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.elSpec.getP());
        objectOutputStream.writeObject(this.elSpec.getG());
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
