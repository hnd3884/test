package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;
import org.bouncycastle.jce.spec.GOST3410PrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.jce.interfaces.GOST3410Params;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.jce.interfaces.GOST3410PrivateKey;

public class BCGOST3410PrivateKey implements GOST3410PrivateKey, PKCS12BagAttributeCarrier
{
    static final long serialVersionUID = 8581661527592305464L;
    private BigInteger x;
    private transient GOST3410Params gost3410Spec;
    private transient PKCS12BagAttributeCarrier attrCarrier;
    
    protected BCGOST3410PrivateKey() {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    BCGOST3410PrivateKey(final GOST3410PrivateKey gost3410PrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = gost3410PrivateKey.getX();
        this.gost3410Spec = gost3410PrivateKey.getParameters();
    }
    
    BCGOST3410PrivateKey(final GOST3410PrivateKeySpec gost3410PrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = gost3410PrivateKeySpec.getX();
        this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec(gost3410PrivateKeySpec.getP(), gost3410PrivateKeySpec.getQ(), gost3410PrivateKeySpec.getA()));
    }
    
    BCGOST3410PrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final GOST3410PublicKeyAlgParameters instance = GOST3410PublicKeyAlgParameters.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        final ASN1Encodable privateKey = privateKeyInfo.parsePrivateKey();
        if (privateKey instanceof ASN1Integer) {
            this.x = ASN1Integer.getInstance(privateKey).getPositiveValue();
        }
        else {
            final byte[] octets = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
            final byte[] array = new byte[octets.length];
            for (int i = 0; i != octets.length; ++i) {
                array[i] = octets[octets.length - 1 - i];
            }
            this.x = new BigInteger(1, array);
        }
        this.gost3410Spec = GOST3410ParameterSpec.fromPublicKeyAlg(instance);
    }
    
    BCGOST3410PrivateKey(final GOST3410PrivateKeyParameters gost3410PrivateKeyParameters, final GOST3410ParameterSpec gost3410Spec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.x = gost3410PrivateKeyParameters.getX();
        this.gost3410Spec = gost3410Spec;
        if (gost3410Spec == null) {
            throw new IllegalArgumentException("spec is null");
        }
    }
    
    public String getAlgorithm() {
        return "GOST3410";
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        final byte[] byteArray = this.getX().toByteArray();
        byte[] array;
        if (byteArray[0] == 0) {
            array = new byte[byteArray.length - 1];
        }
        else {
            array = new byte[byteArray.length];
        }
        for (int i = 0; i != array.length; ++i) {
            array[i] = byteArray[byteArray.length - 1 - i];
        }
        try {
            PrivateKeyInfo privateKeyInfo;
            if (this.gost3410Spec instanceof GOST3410ParameterSpec) {
                privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94, new GOST3410PublicKeyAlgParameters(new ASN1ObjectIdentifier(this.gost3410Spec.getPublicKeyParamSetOID()), new ASN1ObjectIdentifier(this.gost3410Spec.getDigestParamSetOID()))), new DEROctetString(array));
            }
            else {
                privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_94), new DEROctetString(array));
            }
            return privateKeyInfo.getEncoded("DER");
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public GOST3410Params getParameters() {
        return this.gost3410Spec;
    }
    
    public BigInteger getX() {
        return this.x;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof GOST3410PrivateKey)) {
            return false;
        }
        final GOST3410PrivateKey gost3410PrivateKey = (GOST3410PrivateKey)o;
        return this.getX().equals(gost3410PrivateKey.getX()) && this.getParameters().getPublicKeyParameters().equals(gost3410PrivateKey.getParameters().getPublicKeyParameters()) && this.getParameters().getDigestParamSetOID().equals(gost3410PrivateKey.getParameters().getDigestParamSetOID()) && this.compareObj(this.getParameters().getEncryptionParamSetOID(), gost3410PrivateKey.getParameters().getEncryptionParamSetOID());
    }
    
    private boolean compareObj(final Object o, final Object o2) {
        return o == o2 || (o != null && o.equals(o2));
    }
    
    @Override
    public int hashCode() {
        return this.getX().hashCode() ^ this.gost3410Spec.hashCode();
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
        final String s = (String)objectInputStream.readObject();
        if (s != null) {
            this.gost3410Spec = new GOST3410ParameterSpec(s, (String)objectInputStream.readObject(), (String)objectInputStream.readObject());
        }
        else {
            this.gost3410Spec = new GOST3410ParameterSpec(new GOST3410PublicKeyParameterSetSpec((BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject(), (BigInteger)objectInputStream.readObject()));
            objectInputStream.readObject();
            objectInputStream.readObject();
        }
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        if (this.gost3410Spec.getPublicKeyParamSetOID() != null) {
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParamSetOID());
            objectOutputStream.writeObject(this.gost3410Spec.getDigestParamSetOID());
            objectOutputStream.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        }
        else {
            objectOutputStream.writeObject(null);
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getP());
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getQ());
            objectOutputStream.writeObject(this.gost3410Spec.getPublicKeyParameters().getA());
            objectOutputStream.writeObject(this.gost3410Spec.getDigestParamSetOID());
            objectOutputStream.writeObject(this.gost3410Spec.getEncryptionParamSetOID());
        }
    }
}
