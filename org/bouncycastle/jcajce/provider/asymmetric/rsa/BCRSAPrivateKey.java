package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.security.spec.RSAPrivateKeySpec;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.security.interfaces.RSAPrivateKey;

public class BCRSAPrivateKey implements RSAPrivateKey, PKCS12BagAttributeCarrier
{
    static final long serialVersionUID = 5110188922551353628L;
    private static BigInteger ZERO;
    protected BigInteger modulus;
    protected BigInteger privateExponent;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCRSAPrivateKey() {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    BCRSAPrivateKey(final RSAKeyParameters rsaKeyParameters) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.modulus = rsaKeyParameters.getModulus();
        this.privateExponent = rsaKeyParameters.getExponent();
    }
    
    BCRSAPrivateKey(final RSAPrivateKeySpec rsaPrivateKeySpec) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.modulus = rsaPrivateKeySpec.getModulus();
        this.privateExponent = rsaPrivateKeySpec.getPrivateExponent();
    }
    
    BCRSAPrivateKey(final RSAPrivateKey rsaPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.modulus = rsaPrivateKey.getModulus();
        this.privateExponent = rsaPrivateKey.getPrivateExponent();
    }
    
    BCRSAPrivateKey(final org.bouncycastle.asn1.pkcs.RSAPrivateKey rsaPrivateKey) {
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.modulus = rsaPrivateKey.getModulus();
        this.privateExponent = rsaPrivateKey.getPrivateExponent();
    }
    
    public BigInteger getModulus() {
        return this.modulus;
    }
    
    public BigInteger getPrivateExponent() {
        return this.privateExponent;
    }
    
    public String getAlgorithm() {
        return "RSA";
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        return KeyUtil.getEncodedPrivateKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new org.bouncycastle.asn1.pkcs.RSAPrivateKey(this.getModulus(), BCRSAPrivateKey.ZERO, this.getPrivateExponent(), BCRSAPrivateKey.ZERO, BCRSAPrivateKey.ZERO, BCRSAPrivateKey.ZERO, BCRSAPrivateKey.ZERO, BCRSAPrivateKey.ZERO));
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RSAPrivateKey)) {
            return false;
        }
        if (o == this) {
            return true;
        }
        final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey)o;
        return this.getModulus().equals(rsaPrivateKey.getModulus()) && this.getPrivateExponent().equals(rsaPrivateKey.getPrivateExponent());
    }
    
    @Override
    public int hashCode() {
        return this.getModulus().hashCode() ^ this.getPrivateExponent().hashCode();
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
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
    }
    
    static {
        BCRSAPrivateKey.ZERO = BigInteger.valueOf(0L);
    }
}
