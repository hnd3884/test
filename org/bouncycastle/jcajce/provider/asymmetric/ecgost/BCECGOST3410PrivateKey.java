package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.Enumeration;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.ASN1Sequence;
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
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.security.interfaces.ECPrivateKey;

public class BCECGOST3410PrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder
{
    static final long serialVersionUID = 7245981689601667138L;
    private String algorithm;
    private boolean withCompression;
    private transient ASN1Encodable gostParams;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient DERBitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCECGOST3410PrivateKey() {
        this.algorithm = "ECGOST3410";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    public BCECGOST3410PrivateKey(final ECPrivateKey ecPrivateKey) {
        this.algorithm = "ECGOST3410";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKey.getS();
        this.algorithm = ecPrivateKey.getAlgorithm();
        this.ecSpec = ecPrivateKey.getParams();
    }
    
    public BCECGOST3410PrivateKey(final ECPrivateKeySpec ecPrivateKeySpec) {
        this.algorithm = "ECGOST3410";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKeySpec.getD();
        if (ecPrivateKeySpec.getParams() != null) {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecPrivateKeySpec.getParams().getCurve(), ecPrivateKeySpec.getParams().getSeed()), ecPrivateKeySpec.getParams());
        }
        else {
            this.ecSpec = null;
        }
    }
    
    public BCECGOST3410PrivateKey(final java.security.spec.ECPrivateKeySpec ecPrivateKeySpec) {
        this.algorithm = "ECGOST3410";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKeySpec.getS();
        this.ecSpec = ecPrivateKeySpec.getParams();
    }
    
    public BCECGOST3410PrivateKey(final BCECGOST3410PrivateKey bcecgost3410PrivateKey) {
        this.algorithm = "ECGOST3410";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = bcecgost3410PrivateKey.d;
        this.ecSpec = bcecgost3410PrivateKey.ecSpec;
        this.withCompression = bcecgost3410PrivateKey.withCompression;
        this.attrCarrier = bcecgost3410PrivateKey.attrCarrier;
        this.publicKey = bcecgost3410PrivateKey.publicKey;
        this.gostParams = bcecgost3410PrivateKey.gostParams;
    }
    
    public BCECGOST3410PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCECGOST3410PublicKey bcecgost3410PublicKey, final ECParameterSpec ecSpec) {
        this.algorithm = "ECGOST3410";
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
        this.gostParams = bcecgost3410PublicKey.getGostParams();
        this.publicKey = this.getPublicKeyDetails(bcecgost3410PublicKey);
    }
    
    public BCECGOST3410PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCECGOST3410PublicKey bcecgost3410PublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        this.algorithm = "ECGOST3410";
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
        this.gostParams = bcecgost3410PublicKey.getGostParams();
        this.publicKey = this.getPublicKeyDetails(bcecgost3410PublicKey);
    }
    
    public BCECGOST3410PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters) {
        this.algorithm = "ECGOST3410";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        this.ecSpec = null;
    }
    
    BCECGOST3410PrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.algorithm = "ECGOST3410";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.populateFromPrivKeyInfo(privateKeyInfo);
    }
    
    private void populateFromPrivKeyInfo(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1Encodable parameters = privateKeyInfo.getPrivateKeyAlgorithm().getParameters();
        final ASN1Primitive asn1Primitive = parameters.toASN1Primitive();
        if (asn1Primitive instanceof ASN1Sequence && (ASN1Sequence.getInstance(asn1Primitive).size() == 2 || ASN1Sequence.getInstance(asn1Primitive).size() == 3)) {
            final GOST3410PublicKeyAlgParameters instance = GOST3410PublicKeyAlgParameters.getInstance(parameters);
            this.gostParams = instance;
            final ECNamedCurveParameterSpec parameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(instance.getPublicKeyParamSet()));
            this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(instance.getPublicKeyParamSet()), EC5Util.convertCurve(parameterSpec.getCurve(), parameterSpec.getSeed()), EC5Util.convertPoint(parameterSpec.getG()), parameterSpec.getN(), parameterSpec.getH());
            final ASN1Encodable privateKey = privateKeyInfo.parsePrivateKey();
            if (privateKey instanceof ASN1Integer) {
                this.d = ASN1Integer.getInstance(privateKey).getPositiveValue();
            }
            else {
                final byte[] octets = ASN1OctetString.getInstance(privateKey).getOctets();
                final byte[] array = new byte[octets.length];
                for (int i = 0; i != octets.length; ++i) {
                    array[i] = octets[octets.length - 1 - i];
                }
                this.d = new BigInteger(1, array);
            }
        }
        else {
            final X962Parameters instance2 = X962Parameters.getInstance(parameters);
            if (instance2.isNamedCurve()) {
                final ASN1ObjectIdentifier instance3 = ASN1ObjectIdentifier.getInstance(instance2.getParameters());
                X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(instance3);
                String s;
                if (namedCurveByOid == null) {
                    final ECDomainParameters byOID = ECGOST3410NamedCurves.getByOID(instance3);
                    namedCurveByOid = new X9ECParameters(byOID.getCurve(), byOID.getG(), byOID.getN(), byOID.getH(), byOID.getSeed());
                    s = ECGOST3410NamedCurves.getName(instance3);
                }
                else {
                    s = ECUtil.getCurveName(instance3);
                }
                this.ecSpec = new ECNamedCurveSpec(s, EC5Util.convertCurve(namedCurveByOid.getCurve(), namedCurveByOid.getSeed()), EC5Util.convertPoint(namedCurveByOid.getG()), namedCurveByOid.getN(), namedCurveByOid.getH());
            }
            else if (instance2.isImplicitlyCA()) {
                this.ecSpec = null;
            }
            else {
                final X9ECParameters instance4 = X9ECParameters.getInstance(instance2.getParameters());
                this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(instance4.getCurve(), instance4.getSeed()), EC5Util.convertPoint(instance4.getG()), instance4.getN(), instance4.getH().intValue());
            }
            final ASN1Encodable privateKey2 = privateKeyInfo.parsePrivateKey();
            if (privateKey2 instanceof ASN1Integer) {
                this.d = ASN1Integer.getInstance(privateKey2).getValue();
            }
            else {
                final org.bouncycastle.asn1.sec.ECPrivateKey instance5 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privateKey2);
                this.d = instance5.getKey();
                this.publicKey = instance5.getPublicKey();
            }
        }
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "PKCS#8";
    }
    
    public byte[] getEncoded() {
        if (this.gostParams != null) {
            final byte[] array = new byte[32];
            this.extractBytes(array, 0, this.getS());
            try {
                return new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, this.gostParams), new DEROctetString(array)).getEncoded("DER");
            }
            catch (final IOException ex) {
                return null;
            }
        }
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
            return new PrivateKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, x962Parameters.toASN1Primitive()), ecPrivateKey.toASN1Primitive()).getEncoded("DER");
        }
        catch (final IOException ex2) {
            return null;
        }
    }
    
    private void extractBytes(final byte[] array, final int n, final BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length < 32) {
            final byte[] array2 = new byte[32];
            System.arraycopy(byteArray, 0, array2, array2.length - byteArray.length, byteArray.length);
            byteArray = array2;
        }
        for (int i = 0; i != 32; ++i) {
            array[n + i] = byteArray[byteArray.length - 1 - i];
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
        if (!(o instanceof BCECGOST3410PrivateKey)) {
            return false;
        }
        final BCECGOST3410PrivateKey bcecgost3410PrivateKey = (BCECGOST3410PrivateKey)o;
        return this.getD().equals(bcecgost3410PrivateKey.getD()) && this.engineGetSpec().equals(bcecgost3410PrivateKey.engineGetSpec());
    }
    
    @Override
    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }
    
    @Override
    public String toString() {
        return ECUtil.privateKeyToString(this.algorithm, this.d, this.engineGetSpec());
    }
    
    private DERBitString getPublicKeyDetails(final BCECGOST3410PublicKey bcecgost3410PublicKey) {
        try {
            return SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(bcecgost3410PublicKey.getEncoded())).getPublicKeyData();
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
