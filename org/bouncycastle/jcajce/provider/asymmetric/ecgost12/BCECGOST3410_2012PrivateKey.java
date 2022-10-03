package org.bouncycastle.jcajce.provider.asymmetric.ecgost12;

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
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.ASN1Encodable;
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
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import java.security.interfaces.ECPrivateKey;

public class BCECGOST3410_2012PrivateKey implements ECPrivateKey, org.bouncycastle.jce.interfaces.ECPrivateKey, PKCS12BagAttributeCarrier, ECPointEncoder
{
    static final long serialVersionUID = 7245981689601667138L;
    private String algorithm;
    private boolean withCompression;
    private transient GOST3410PublicKeyAlgParameters gostParams;
    private transient BigInteger d;
    private transient ECParameterSpec ecSpec;
    private transient DERBitString publicKey;
    private transient PKCS12BagAttributeCarrierImpl attrCarrier;
    
    protected BCECGOST3410_2012PrivateKey() {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
    }
    
    public BCECGOST3410_2012PrivateKey(final ECPrivateKey ecPrivateKey) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKey.getS();
        this.algorithm = ecPrivateKey.getAlgorithm();
        this.ecSpec = ecPrivateKey.getParams();
    }
    
    public BCECGOST3410_2012PrivateKey(final ECPrivateKeySpec ecPrivateKeySpec) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKeySpec.getD();
        if (ecPrivateKeySpec.getParams() != null) {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecPrivateKeySpec.getParams().getCurve(), ecPrivateKeySpec.getParams().getSeed()), ecPrivateKeySpec.getParams());
        }
        else {
            this.ecSpec = null;
        }
    }
    
    public BCECGOST3410_2012PrivateKey(final java.security.spec.ECPrivateKeySpec ecPrivateKeySpec) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = ecPrivateKeySpec.getS();
        this.ecSpec = ecPrivateKeySpec.getParams();
    }
    
    public BCECGOST3410_2012PrivateKey(final BCECGOST3410_2012PrivateKey bcecgost3410_2012PrivateKey) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.d = bcecgost3410_2012PrivateKey.d;
        this.ecSpec = bcecgost3410_2012PrivateKey.ecSpec;
        this.withCompression = bcecgost3410_2012PrivateKey.withCompression;
        this.attrCarrier = bcecgost3410_2012PrivateKey.attrCarrier;
        this.publicKey = bcecgost3410_2012PrivateKey.publicKey;
        this.gostParams = bcecgost3410_2012PrivateKey.gostParams;
    }
    
    public BCECGOST3410_2012PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCECGOST3410_2012PublicKey bcecgost3410_2012PublicKey, final ECParameterSpec ecSpec) {
        this.algorithm = "ECGOST3410-2012";
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
        this.gostParams = bcecgost3410_2012PublicKey.getGostParams();
        this.publicKey = this.getPublicKeyDetails(bcecgost3410_2012PublicKey);
    }
    
    public BCECGOST3410_2012PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters, final BCECGOST3410_2012PublicKey bcecgost3410_2012PublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        this.algorithm = "ECGOST3410-2012";
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
        this.gostParams = bcecgost3410_2012PublicKey.getGostParams();
        this.publicKey = this.getPublicKeyDetails(bcecgost3410_2012PublicKey);
    }
    
    public BCECGOST3410_2012PrivateKey(final String algorithm, final ECPrivateKeyParameters ecPrivateKeyParameters) {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.algorithm = algorithm;
        this.d = ecPrivateKeyParameters.getD();
        this.ecSpec = null;
    }
    
    BCECGOST3410_2012PrivateKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        this.algorithm = "ECGOST3410-2012";
        this.attrCarrier = new PKCS12BagAttributeCarrierImpl();
        this.populateFromPrivKeyInfo(privateKeyInfo);
    }
    
    private void populateFromPrivKeyInfo(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final ASN1Primitive asn1Primitive = privateKeyInfo.getPrivateKeyAlgorithm().getParameters().toASN1Primitive();
        if (asn1Primitive instanceof ASN1Sequence && (ASN1Sequence.getInstance(asn1Primitive).size() == 2 || ASN1Sequence.getInstance(asn1Primitive).size() == 3)) {
            this.gostParams = GOST3410PublicKeyAlgParameters.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
            final ECNamedCurveParameterSpec parameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()));
            this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(this.gostParams.getPublicKeyParamSet()), EC5Util.convertCurve(parameterSpec.getCurve(), parameterSpec.getSeed()), EC5Util.convertPoint(parameterSpec.getG()), parameterSpec.getN(), parameterSpec.getH());
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
            final X962Parameters instance = X962Parameters.getInstance(privateKeyInfo.getPrivateKeyAlgorithm().getParameters());
            if (instance.isNamedCurve()) {
                final ASN1ObjectIdentifier instance2 = ASN1ObjectIdentifier.getInstance(instance.getParameters());
                final X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(instance2);
                if (namedCurveByOid == null) {
                    final ECDomainParameters byOID = ECGOST3410NamedCurves.getByOID(instance2);
                    this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(instance2), EC5Util.convertCurve(byOID.getCurve(), byOID.getSeed()), EC5Util.convertPoint(byOID.getG()), byOID.getN(), byOID.getH());
                }
                else {
                    this.ecSpec = new ECNamedCurveSpec(ECUtil.getCurveName(instance2), EC5Util.convertCurve(namedCurveByOid.getCurve(), namedCurveByOid.getSeed()), EC5Util.convertPoint(namedCurveByOid.getG()), namedCurveByOid.getN(), namedCurveByOid.getH());
                }
            }
            else if (instance.isImplicitlyCA()) {
                this.ecSpec = null;
            }
            else {
                final X9ECParameters instance3 = X9ECParameters.getInstance(instance.getParameters());
                this.ecSpec = new ECParameterSpec(EC5Util.convertCurve(instance3.getCurve(), instance3.getSeed()), EC5Util.convertPoint(instance3.getG()), instance3.getN(), instance3.getH().intValue());
            }
            final ASN1Encodable privateKey2 = privateKeyInfo.parsePrivateKey();
            if (privateKey2 instanceof ASN1Integer) {
                this.d = ASN1Integer.getInstance(privateKey2).getValue();
            }
            else {
                final org.bouncycastle.asn1.sec.ECPrivateKey instance4 = org.bouncycastle.asn1.sec.ECPrivateKey.getInstance(privateKey2);
                this.d = instance4.getKey();
                this.publicKey = instance4.getPublicKey();
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
        final boolean b = this.d.bitLength() > 256;
        final ASN1ObjectIdentifier asn1ObjectIdentifier = b ? RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512 : RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256;
        final int n = b ? 64 : 32;
        if (this.gostParams != null) {
            final byte[] array = new byte[n];
            this.extractBytes(array, n, 0, this.getS());
            try {
                return new PrivateKeyInfo(new AlgorithmIdentifier(asn1ObjectIdentifier, this.gostParams), new DEROctetString(array)).getEncoded("DER");
            }
            catch (final IOException ex) {
                return null;
            }
        }
        X962Parameters x962Parameters;
        int n2;
        if (this.ecSpec instanceof ECNamedCurveSpec) {
            ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(((ECNamedCurveSpec)this.ecSpec).getName());
            if (namedCurveOid == null) {
                namedCurveOid = new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName());
            }
            x962Parameters = new X962Parameters(namedCurveOid);
            n2 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
        }
        else if (this.ecSpec == null) {
            x962Parameters = new X962Parameters(DERNull.INSTANCE);
            n2 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, null, this.getS());
        }
        else {
            final ECCurve convertCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
            x962Parameters = new X962Parameters(new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed()));
            n2 = ECUtil.getOrderBitLength(BouncyCastleProvider.CONFIGURATION, this.ecSpec.getOrder(), this.getS());
        }
        org.bouncycastle.asn1.sec.ECPrivateKey ecPrivateKey;
        if (this.publicKey != null) {
            ecPrivateKey = new org.bouncycastle.asn1.sec.ECPrivateKey(n2, this.getS(), this.publicKey, x962Parameters);
        }
        else {
            ecPrivateKey = new org.bouncycastle.asn1.sec.ECPrivateKey(n2, this.getS(), x962Parameters);
        }
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(asn1ObjectIdentifier, x962Parameters.toASN1Primitive()), ecPrivateKey.toASN1Primitive()).getEncoded("DER");
        }
        catch (final IOException ex2) {
            return null;
        }
    }
    
    private void extractBytes(final byte[] array, final int n, final int n2, final BigInteger bigInteger) {
        byte[] byteArray = bigInteger.toByteArray();
        if (byteArray.length < n) {
            final byte[] array2 = new byte[n];
            System.arraycopy(byteArray, 0, array2, array2.length - byteArray.length, byteArray.length);
            byteArray = array2;
        }
        for (int i = 0; i != n; ++i) {
            array[n2 + i] = byteArray[byteArray.length - 1 - i];
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
        if (!(o instanceof BCECGOST3410_2012PrivateKey)) {
            return false;
        }
        final BCECGOST3410_2012PrivateKey bcecgost3410_2012PrivateKey = (BCECGOST3410_2012PrivateKey)o;
        return this.getD().equals(bcecgost3410_2012PrivateKey.getD()) && this.engineGetSpec().equals(bcecgost3410_2012PrivateKey.engineGetSpec());
    }
    
    @Override
    public int hashCode() {
        return this.getD().hashCode() ^ this.engineGetSpec().hashCode();
    }
    
    @Override
    public String toString() {
        return ECUtil.privateKeyToString(this.algorithm, this.d, this.engineGetSpec());
    }
    
    private DERBitString getPublicKeyDetails(final BCECGOST3410_2012PublicKey bcecgost3410_2012PublicKey) {
        return SubjectPublicKeyInfo.getInstance(bcecgost3410_2012PublicKey.getEncoded()).getPublicKeyData();
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
