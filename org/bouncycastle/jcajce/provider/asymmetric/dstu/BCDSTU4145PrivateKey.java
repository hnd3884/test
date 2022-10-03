package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.Enumeration;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.ASN1Primitive;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.PKCS12BagAttributeCarrierImpl;
import org.bouncycastle.asn1.DERBitString;
import java.security.spec.ECParameterSpec;
import java.math.BigInteger;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.security.interfaces.ECPrivateKey;

public class BCDSTU4145PrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder
{
    static final long serialVersionUID = 7245981689601667138L;
    private String algorithm;
    private boolean withCompression;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient DERBitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCDSTU4145PrivateKey() {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    public BCDSTU4145PrivateKey(final ECPrivateKey ecPrivateKey) {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKey.getS();
        this.algorithm = ecPrivateKey.getAlgorithm();
        this.ecSpec = ecPrivateKey.getParams();
    }
    
    public BCDSTU4145PrivateKey(final ECPrivateKeySpec ecPrivateKeySpec) {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKeySpec.getD();
        if (ecPrivateKeySpec.getParams() != null) {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecPrivateKeySpec.getParams().getCurve(), ecPrivateKeySpec.getParams().getSeed()), ecPrivateKeySpec.getParams());
        }
        else {
            this.ecSpec = null;
        }
    }
    
    public BCDSTU4145PrivateKey(final java.security.spec.ECPrivateKeySpec ecPrivateKeySpec) {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKeySpec.getS();
        this.ecSpec = ecPrivateKeySpec.getParams();
    }
    
    public BCDSTU4145PrivateKey(final BCDSTU4145PrivateKey bcdstu4145PrivateKey) {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = bcdstu4145PrivateKey.d;
        this.ecSpec = bcdstu4145PrivateKey.ecSpec;
        this.withCompression = bcdstu4145PrivateKey.withCompression;
        this.attrCarrier = bcdstu4145PrivateKey.attrCarrier;
        this.publicKey = bcdstu4145PrivateKey.publicKey;
    }
    
    public BCDSTU4145PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCDSTU4145PublicKey bcdstu4145PublicKey, final ECParameterSpec ecSpec) {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final ECDomainParameters parameters = ecPrivateKeyParameters.getParameters();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        if (ecSpec == null) {
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), EC5Util.convertPoint(parameters.getG()), parameters.getN(), parameters.getH().intValue());
        }
        else {
            this.ecSpec = ecSpec;
        }
        this.publicKey = this.getPublicKeyDetails(bcdstu4145PublicKey);
    }
    
    public BCDSTU4145PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCDSTU4145PublicKey bcdstu4145PublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        final ECDomainParameters parameters = ecPrivateKeyParameters.getParameters();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        if (ecParameterSpec == null) {
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), EC5Util.convertPoint(parameters.getG()), parameters.getN(), parameters.getH().intValue());
        }
        else {
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(ecParameterSpec.getCurve(), ecParameterSpec.getSeed()), EC5Util.convertPoint(ecParameterSpec.getG()), ecParameterSpec.getN(), ecParameterSpec.getH().intValue());
        }
        this.publicKey = this.getPublicKeyDetails(bcdstu4145PublicKey);
    }
    
    public BCDSTU4145PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters) {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        this.ecSpec = null;
    }
    
    BCDSTU4145PrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.algorithm = "DSTU4145";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.populateFromPrivKeyInfo(privateKeyInfo);
    }
    
    private void populateFromPrivKeyInfo(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final X962Parameters x962Parameters = new X962Parameters((ASN1Primitive)privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        if (x962Parameters.isNamedCurve()) {
            final ASN1ObjectIdentifier instance = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
            final X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(instance);
            if (namedCurveByOid == null) {
                final ECDomainParameters byOID = DSTU4145NamedCurves.getByOID(instance);
                this.ecSpec = new ECNamedCurveSpec(instance.getId(), EC5Util.convertCurve(byOID.getCurve(), byOID.getSeed()), EC5Util.convertPoint(byOID.getG()), byOID.getN(), byOID.getH());
            }
            else {
                this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(instance), EC5Util.convertCurve(namedCurveByOid.getCurve(), namedCurveByOid.getSeed()), EC5Util.convertPoint(namedCurveByOid.getG()), namedCurveByOid.getN(), namedCurveByOid.getH());
            }
        }
        else if (x962Parameters.isImplicitlyCA()) {
            this.ecSpec = null;
        }
        else {
            final X9ECParameters instance2 = X9ECParameters.getInstance(x962Parameters.getParameters());
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(instance2.getCurve(), instance2.getSeed()), EC5Util.convertPoint(instance2.getG()), instance2.getN(), instance2.getH().intValue());
        }
        final ASN1Encodable privateKey = privateKeyInfo.parsePrivateKey();
        if (privateKey instanceof ASN1Integer) {
            this.d = ASN1Integer.getInstance(privateKey).getValue();
        }
        else {
            final org.bouncycastle.asn1.sec.ECPrivateKey instance3 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privateKey);
            this.d = instance3.getKey();
            this.publicKey = instance3.getPublicKey();
        }
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        X962Parameters x962Parameters;
        int n;
        if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
            if (namedCurveOid == null) {
                namedCurveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
            }
            x962Parameters = new X962Parameters(namedCurveOid);
            n = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
        }
        else if (this.ecSpec == null) {
            x962Parameters = new X962Parameters(DERNull.INSTANCE);
            n = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, null, this.getS());
        }
        else {
            final ECCurve convertCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
            x962Parameters = new X962Parameters(new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed()));
            n = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
        }
        org.bouncycastle.asn1.sec.ECPrivateKey ecPrivateKey;
        if (this.publicKey != null) {
            ecPrivateKey = new org.bouncycastle.asn1.sec.ECPrivateKey(n, this.getS(), this.publicKey, x962Parameters);
        }
        else {
            ecPrivateKey = new org.bouncycastle.asn1.sec.ECPrivateKey(n, this.getS(), x962Parameters);
        }
        try {
            PrivateKeyInfo privateKeyInfo;
            if (this.algorithm.equals("DSTU4145")) {
                privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, x962Parameters.toASN1Primitive()), ecPrivateKey.toASN1Primitive());
            }
            else {
                privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, x962Parameters.toASN1Primitive()), ecPrivateKey.toASN1Primitive());
            }
            return privateKeyInfo.getEncoded("DER");
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
        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
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
        if (!(o instanceof BCDSTU4145PrivateKey)) {
            return false;
        }
        final BCDSTU4145PrivateKey bcdstu4145PrivateKey = (BCDSTU4145PrivateKey)o;
        return this.getD().equals(bcdstu4145PrivateKey.getD()) && this.engineGetSpec().equals(bcdstu4145PrivateKey.engineGetSpec());
    }
    
    @Override
    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }
    
    @Override
    public String toString() {
        return ECUtil.privateKeyToString(this.algorithm, this.d, this.engineGetSpec());
    }
    
    private DERBitString getPublicKeyDetails(final BCDSTU4145PublicKey bcdstu4145PublicKey) {
        try {
            return SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(bcdstu4145PublicKey.getEncoded())).getPublicKeyData();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray((byte[])objectInputStream.readObject())));
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}
