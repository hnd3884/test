package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.ObjectOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.ObjectInputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.math.ec.ECPoint;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x9.X962Parameters;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import java.security.spec.ECParameterSpec;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.security.interfaces.ECPrivateKey;

public class BCECPrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder
{
    static final long serialVersionUID = 994553197664784084L;
    private String algorithm;
    private boolean withCompression;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient ProviderConfiguration configuration;
    private transient DERBitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCECPrivateKey() {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    public BCECPrivateKey(final ECPrivateKey ecPrivateKey, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKey.getS();
        this.algorithm = ecPrivateKey.getAlgorithm();
        this.ecSpec = ecPrivateKey.getParams();
        this.configuration = configuration;
    }
    
    public BCECPrivateKey(final String algorithm, final ECPrivateKeySpec ecPrivateKeySpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeySpec.getD();
        if (ecPrivateKeySpec.getParams() != null) {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecPrivateKeySpec.getParams().getCurve(), ecPrivateKeySpec.getParams().getSeed()), ecPrivateKeySpec.getParams());
        }
        else {
            this.ecSpec = null;
        }
        this.configuration = configuration;
    }
    
    public BCECPrivateKey(final String algorithm, final java.security.spec.ECPrivateKeySpec ecPrivateKeySpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeySpec.getS();
        this.ecSpec = ecPrivateKeySpec.getParams();
        this.configuration = configuration;
    }
    
    public BCECPrivateKey(final String algorithm, final BCECPrivateKey bcecPrivateKey) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = bcecPrivateKey.d;
        this.ecSpec = bcecPrivateKey.ecSpec;
        this.withCompression = bcecPrivateKey.withCompression;
        this.attrCarrier = bcecPrivateKey.attrCarrier;
        this.publicKey = bcecPrivateKey.publicKey;
        this.configuration = bcecPrivateKey.configuration;
    }
    
    public BCECPrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCECPublicKey bcecPublicKey, final ECParameterSpec ecSpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        this.configuration = configuration;
        if (ecSpec == null) {
            final ECDomainParameters parameters = ecPrivateKeyParameters.getParameters();
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), EC5Util.convertPoint(parameters.getG()), parameters.getN(), parameters.getH().intValue());
        }
        else {
            this.ecSpec = ecSpec;
        }
        this.publicKey = this.getPublicKeyDetails(bcecPublicKey);
    }
    
    public BCECPrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCECPublicKey bcecPublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        this.configuration = configuration;
        if (ecParameterSpec == null) {
            final ECDomainParameters parameters = ecPrivateKeyParameters.getParameters();
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), EC5Util.convertPoint(parameters.getG()), parameters.getN(), parameters.getH().intValue());
        }
        else {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecParameterSpec.getCurve(), ecParameterSpec.getSeed()), ecParameterSpec);
        }
        try {
            this.publicKey = this.getPublicKeyDetails(bcecPublicKey);
        }
        catch (final Exception ex) {
            this.publicKey = null;
        }
    }
    
    public BCECPrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        this.ecSpec = null;
        this.configuration = configuration;
    }
    
    BCECPrivateKey(final String algorithm, final PrivateKeyInfo privateKeyInfo, final ProviderConfiguration configuration) throws IOException {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.configuration = configuration;
        this.populateFromPrivKeyInfo(privateKeyInfo);
    }
    
    private void populateFromPrivKeyInfo(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final X962Parameters instance = X962Parameters.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        this.ecSpec = EC5Util.convertToSpec(instance, EC5Util.getCurve(this.configuration, instance));
        final ASN1Encodable privateKey = privateKeyInfo.parsePrivateKey();
        if (privateKey instanceof ASN1Integer) {
            this.d = ASN1Integer.getInstance(privateKey).getValue();
        }
        else {
            final org.bouncycastle.asn1.sec.ECPrivateKey instance2 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privateKey);
            this.d = instance2.getKey();
            this.publicKey = instance2.getPublicKey();
        }
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        final X962Parameters domainParametersFromName = ECUtils.getDomainParametersFromName(this.ecSpec, this.withCompression);
        int n;
        if (this.ecSpec == null) {
            n = ECUtil.getOrderBitLength(this.configuration, null, this.getS());
        }
        else {
            n = ECUtil.getOrderBitLength(this.configuration, this.ecSpec.getOrder(), this.getS());
        }
        org.bouncycastle.asn1.sec.ECPrivateKey ecPrivateKey;
        if (this.publicKey != null) {
            ecPrivateKey = new org.bouncycastle.asn1.sec.ECPrivateKey(n, this.getS(), this.publicKey, domainParametersFromName);
        }
        else {
            ecPrivateKey = new org.bouncycastle.asn1.sec.ECPrivateKey(n, this.getS(), domainParametersFromName);
        }
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, domainParametersFromName), ecPrivateKey).getEncoded("DER");
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    public ECParameterSpec getParams() {
        return this.ecSpec;
    }
    
    public org.bouncycastle.jce.spec.ECParameterSpec getParameters() {
        if (this.ecSpec == null) {
            return null;
        }
        return EC5Util.convertSpec(this.ecSpec, this.withCompression);
    }
    
    org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec, this.withCompression);
        }
        return this.configuration.getEcImplicitlyCa();
    }
    
    public BigInteger getS() {
        return this.d;
    }
    
    public BigInteger getD() {
        return this.d;
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
    
    public void setPointFormat(final String s) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(s);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BCECPrivateKey)) {
            return false;
        }
        final BCECPrivateKey bcecPrivateKey = (BCECPrivateKey)o;
        return this.getD().equals(bcecPrivateKey.getD()) && this.engineGetSpec().equals(bcecPrivateKey.engineGetSpec());
    }
    
    @Override
    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }
    
    @Override
    public String toString() {
        return ECUtil.privateKeyToString("EC", this.d, this.engineGetSpec());
    }
    
    private ECPoint calculateQ(final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        return ecParameterSpec.getG().multiply(this.d).normalize();
    }
    
    private DERBitString getPublicKeyDetails(final BCECPublicKey bcecPublicKey) {
        try {
            return SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(bcecPublicKey.getEncoded())).getPublicKeyData();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final byte[] array = (byte[])objectInputStream.readObject();
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(array)));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}
