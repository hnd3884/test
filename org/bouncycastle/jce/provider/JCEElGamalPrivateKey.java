package org.bouncycastle.jce.provider;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import javax.crypto.spec.DHParameterSpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
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

public class JCEElGamalPrivateKey implements ElGamalPrivateKey, DHPrivateKey, PKCS12BagAttributeCarrier
{
    static final long serialVersionUID = 4819350091141529678L;
    BigInteger x;
    ElGamalParameterSpec elSpec;
    private PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected JCEElGamalPrivateKey() {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    JCEElGamalPrivateKey(final ElGamalPrivateKey elGamalPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = elGamalPrivateKey.getX();
        this.elSpec = elGamalPrivateKey.getParameters();
    }
    
    JCEElGamalPrivateKey(final DHPrivateKey dhPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKey.getX();
        this.elSpec = new ElGamalParameterSpec(dhPrivateKey.getParams().getP(), dhPrivateKey.getParams().getG());
    }
    
    JCEElGamalPrivateKey(final ElGamalPrivateKeySpec elGamalPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = elGamalPrivateKeySpec.getX();
        this.elSpec = new ElGamalParameterSpec(elGamalPrivateKeySpec.getParams().getP(), elGamalPrivateKeySpec.getParams().getG());
    }
    
    JCEElGamalPrivateKey(final DHPrivateKeySpec dhPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = dhPrivateKeySpec.getX();
        this.elSpec = new ElGamalParameterSpec(dhPrivateKeySpec.getP(), dhPrivateKeySpec.getG());
    }
    
    JCEElGamalPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final ElGamalParameter instance = ElGamalParameter.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        this.x = ASN1Integer.getInstance(privateKeyInfo.parsePrivateKey()).getValue();
        this.elSpec = new ElGamalParameterSpec(instance.getP(), instance.getG());
    }
    
    JCEElGamalPrivateKey(final ElGamalPrivateKeyParameters elGamalPrivateKeyParameters) {
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
        return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalParameter(this.elSpec.getP(), this.elSpec.getG())), new ASN1Integer(this.getX()));
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
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.x = (BigInteger)objectInputStream.readObject();
        this.elSpec = new ElGamalParameterSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject());
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.getX());
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
