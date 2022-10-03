package org.bouncycastle.jce.provider;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Strings;
import java.util.Enumeration;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.sec.ECPrivateKeyStructure;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
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

public class JCEECPrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder
{
    private String algorithm;
    private BigInteger d;
    private ECParameterSpec ecSpec;
    private boolean withCompression;
    private DERBitString publicKey;
    private PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected JCEECPrivateKey() {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    public JCEECPrivateKey(final ECPrivateKey ecPrivateKey) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKey.getS();
        this.algorithm = ecPrivateKey.getAlgorithm();
        this.ecSpec = ecPrivateKey.getParams();
    }
    
    public JCEECPrivateKey(final String algorithm, final ECPrivateKeySpec ecPrivateKeySpec) {
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
    }
    
    public JCEECPrivateKey(final String algorithm, final java.security.spec.ECPrivateKeySpec ecPrivateKeySpec) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeySpec.getS();
        this.ecSpec = ecPrivateKeySpec.getParams();
    }
    
    public JCEECPrivateKey(final String algorithm, final JCEECPrivateKey jceecPrivateKey) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = jceecPrivateKey.d;
        this.ecSpec = jceecPrivateKey.ecSpec;
        this.withCompression = jceecPrivateKey.withCompression;
        this.attrCarrier = jceecPrivateKey.attrCarrier;
        this.publicKey = jceecPrivateKey.publicKey;
    }
    
    public JCEECPrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final JCEECPublicKey jceecPublicKey, final ECParameterSpec ecSpec) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        if (ecSpec == null) {
            final ECDomainParameters parameters = ecPrivateKeyParameters.getParameters();
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), EC5Util.convertPoint(parameters.getG()), parameters.getN(), parameters.getH().intValue());
        }
        else {
            this.ecSpec = ecSpec;
        }
        this.publicKey = this.getPublicKeyDetails(jceecPublicKey);
    }
    
    public JCEECPrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final JCEECPublicKey jceecPublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        if (ecParameterSpec == null) {
            final ECDomainParameters parameters = ecPrivateKeyParameters.getParameters();
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), EC5Util.convertPoint(parameters.getG()), parameters.getN(), parameters.getH().intValue());
        }
        else {
            this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(ecParameterSpec.getCurve(), ecParameterSpec.getSeed()), EC5Util.convertPoint(ecParameterSpec.getG()), ecParameterSpec.getN(), ecParameterSpec.getH().intValue());
        }
        this.publicKey = this.getPublicKeyDetails(jceecPublicKey);
    }
    
    public JCEECPrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters) {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        this.ecSpec = null;
    }
    
    JCEECPrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.algorithm = "EC";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.populateFromPrivKeyInfo(privateKeyInfo);
    }
    
    private void populateFromPrivKeyInfo(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final X962Parameters x962Parameters = new X962Parameters((ASN1Primitive)privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
        if (x962Parameters.isNamedCurve()) {
            final ASN1ObjectIdentifier instance = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
            final X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(instance);
            if (namedCurveByOid == null) {
                final ECDomainParameters byOID = ECGOST3410NamedCurves.getByOID(instance);
                this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(instance), EC5Util.convertCurve(byOID.getCurve(), byOID.getSeed()), EC5Util.convertPoint(byOID.getG()), byOID.getN(), byOID.getH());
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
            final ECPrivateKeyStructure ecPrivateKeyStructure = new ECPrivateKeyStructure((ASN1Sequence)privateKey);
            this.d = ecPrivateKeyStructure.getKey();
            this.publicKey = ecPrivateKeyStructure.getPublicKey();
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
        if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
            if (namedCurveOid == null) {
                namedCurveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
            }
            x962Parameters = new X962Parameters(namedCurveOid);
        }
        else if (this.ecSpec == null) {
            x962Parameters = new X962Parameters(DERNull.INSTANCE);
        }
        else {
            final ECCurve convertCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
            x962Parameters = new X962Parameters(new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed()));
        }
        ECPrivateKeyStructure ecPrivateKeyStructure;
        if (this.publicKey != null) {
            ecPrivateKeyStructure = new ECPrivateKeyStructure(this.getS(), this.publicKey, x962Parameters);
        }
        else {
            ecPrivateKeyStructure = new ECPrivateKeyStructure(this.getS(), x962Parameters);
        }
        try {
            PrivateKeyInfo privateKeyInfo;
            if (this.algorithm.equals("ECGOST3410")) {
                privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, x962Parameters.toASN1Primitive()), ecPrivateKeyStructure.toASN1Primitive());
            }
            else {
                privateKeyInfo = new PrivateKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, x962Parameters.toASN1Primitive()), ecPrivateKeyStructure.toASN1Primitive());
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
        if (!(o instanceof JCEECPrivateKey)) {
            return false;
        }
        final JCEECPrivateKey jceecPrivateKey = (JCEECPrivateKey)o;
        return this.getD().equals(jceecPrivateKey.getD()) && this.engineGetSpec().equals(jceecPrivateKey.engineGetSpec());
    }
    
    @Override
    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append("EC Private Key").append(lineSeparator);
        sb.append("             S: ").append(this.d.toString(16)).append(lineSeparator);
        return sb.toString();
    }
    
    private DERBitString getPublicKeyDetails(final JCEECPublicKey jceecPublicKey) {
        try {
            return SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(jceecPublicKey.getEncoded())).getPublicKeyData();
        }
        catch (final IOException ex) {
            return null;
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.populateFromPrivKeyInfo(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray((byte[])objectInputStream.readObject())));
        this.algorithm = (String)objectInputStream.readObject();
        this.withCompression = objectInputStream.readBoolean();
        (this.attrCarrier = new PKCS12BagAttributeCarrierImpl()).readObject(objectInputStream);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(this.getEncoded());
        objectOutputStream.writeObject(this.algorithm);
        objectOutputStream.writeBoolean(this.withCompression);
        this.attrCarrier.writeObject(objectOutputStream);
    }
}
