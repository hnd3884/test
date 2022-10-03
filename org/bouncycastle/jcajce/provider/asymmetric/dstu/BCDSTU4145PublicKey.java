package org.bouncycastle.jcajce.provider.asymmetric.dstu;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.spec.ECPoint;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.math.ec.ECCurve;
import java.math.BigInteger;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ECDomainParameters;
import java.security.spec.EllipticCurve;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import java.security.spec.ECPublicKeySpec;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import java.security.interfaces.ECPublicKey;

public class BCDSTU4145PublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder
{
    static final long serialVersionUID = 7026240464295649314L;
    private String algorithm;
    private boolean withCompression;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private transient DSTU4145Params dstuParams;
    
    public BCDSTU4145PublicKey(final BCDSTU4145PublicKey bcdstu4145PublicKey) {
        this.algorithm = "DSTU4145";
        this.ecPublicKey = bcdstu4145PublicKey.ecPublicKey;
        this.ecSpec = bcdstu4145PublicKey.ecSpec;
        this.withCompression = bcdstu4145PublicKey.withCompression;
        this.dstuParams = bcdstu4145PublicKey.dstuParams;
    }
    
    public BCDSTU4145PublicKey(final ECPublicKeySpec ecPublicKeySpec) {
        this.algorithm = "DSTU4145";
        this.ecSpec = ecPublicKeySpec.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, ecPublicKeySpec.getW(), false), EC5Util.getDomainParameters(null, this.ecSpec));
    }
    
    public BCDSTU4145PublicKey(final org.bouncycastle.jce.spec.ECPublicKeySpec ecPublicKeySpec, final ProviderConfiguration providerConfiguration) {
        this.algorithm = "DSTU4145";
        if (ecPublicKeySpec.getParams() != null) {
            final EllipticCurve convertCurve = EC5Util.convertCurve(ecPublicKeySpec.getParams().getCurve(), ecPublicKeySpec.getParams().getSeed());
            this.ecPublicKey = new ECPublicKeyParameters(ecPublicKeySpec.getQ(), ECUtil.getDomainParameters(providerConfiguration, ecPublicKeySpec.getParams()));
            this.ecSpec = EC5Util.convertSpec(convertCurve, ecPublicKeySpec.getParams());
        }
        else {
            this.ecPublicKey = new ECPublicKeyParameters(providerConfiguration.getEcImplicitlyCa().getCurve().createPoint(ecPublicKeySpec.getQ().getAffineXCoord().toBigInteger(), ecPublicKeySpec.getQ().getAffineYCoord().toBigInteger()), EC5Util.getDomainParameters(providerConfiguration, null));
            this.ecSpec = null;
        }
    }
    
    public BCDSTU4145PublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey, final ECParameterSpec ecSpec) {
        this.algorithm = "DSTU4145";
        final ECDomainParameters parameters = ecPublicKey.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = ecPublicKey;
        if (ecSpec == null) {
            this.ecSpec = this.createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters);
        }
        else {
            this.ecSpec = ecSpec;
        }
    }
    
    public BCDSTU4145PublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        this.algorithm = "DSTU4145";
        final ECDomainParameters parameters = ecPublicKey.getParameters();
        this.algorithm = algorithm;
        if (ecParameterSpec == null) {
            this.ecSpec = this.createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters);
        }
        else {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecParameterSpec.getCurve(), ecParameterSpec.getSeed()), ecParameterSpec);
        }
        this.ecPublicKey = ecPublicKey;
    }
    
    public BCDSTU4145PublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey) {
        this.algorithm = "DSTU4145";
        this.algorithm = algorithm;
        this.ecPublicKey = ecPublicKey;
        this.ecSpec = null;
    }
    
    private ECParameterSpec createSpec(final EllipticCurve ellipticCurve, final ECDomainParameters ecDomainParameters) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(ecDomainParameters.getG()), ecDomainParameters.getN(), ecDomainParameters.getH().intValue());
    }
    
    BCDSTU4145PublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.algorithm = "DSTU4145";
        this.populateFromPubKeyInfo(subjectPublicKeyInfo);
    }
    
    private void reverseBytes(final byte[] array) {
        for (int i = 0; i < array.length / 2; ++i) {
            final byte b = array[i];
            array[i] = array[array.length - 1 - i];
            array[array.length - 1 - i] = b;
        }
    }
    
    private void populateFromPubKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final DERBitString publicKeyData = subjectPublicKeyInfo.getPublicKeyData();
        this.algorithm = "DSTU4145";
        ASN1OctetString asn1OctetString;
        try {
            asn1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(publicKeyData.getBytes());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("error recovering public key");
        }
        final byte[] octets = asn1OctetString.getOctets();
        if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
            this.reverseBytes(octets);
        }
        this.dstuParams = DSTU4145Params.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
        org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec;
        if (this.dstuParams.isNamedCurve()) {
            final ASN1ObjectIdentifier namedCurve = this.dstuParams.getNamedCurve();
            final ECDomainParameters byOID = DSTU4145NamedCurves.getByOID(namedCurve);
            ecParameterSpec = new ECNamedCurveParameterSpec(namedCurve.getId(), byOID.getCurve(), byOID.getG(), byOID.getN(), byOID.getH(), byOID.getSeed());
        }
        else {
            final DSTU4145ECBinary ecBinary = this.dstuParams.getECBinary();
            final byte[] b = ecBinary.getB();
            if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                this.reverseBytes(b);
            }
            final DSTU4145BinaryField field = ecBinary.getField();
            final ECCurve.F2m f2m = new ECCurve.F2m(field.getM(), field.getK1(), field.getK2(), field.getK3(), ecBinary.getA(), new BigInteger(1, b));
            final byte[] g = ecBinary.getG();
            if (subjectPublicKeyInfo.getAlgorithm().getAlgorithm().equals(UAObjectIdentifiers.dstu4145le)) {
                this.reverseBytes(g);
            }
            ecParameterSpec = new org.bouncycastle.jce.spec.ECParameterSpec(f2m, DSTU4145PointEncoder.decodePoint(f2m, g), ecBinary.getN());
        }
        final ECCurve curve = ecParameterSpec.getCurve();
        final EllipticCurve convertCurve = EC5Util.convertCurve(curve, ecParameterSpec.getSeed());
        if (this.dstuParams.isNamedCurve()) {
            this.ecSpec = new ECNamedCurveSpec(this.dstuParams.getNamedCurve().getId(), convertCurve, EC5Util.convertPoint(ecParameterSpec.getG()), ecParameterSpec.getN(), ecParameterSpec.getH());
        }
        else {
            this.ecSpec = new ECParameterSpec(convertCurve, EC5Util.convertPoint(ecParameterSpec.getG()), ecParameterSpec.getN(), ecParameterSpec.getH().intValue());
        }
        this.ecPublicKey = new ECPublicKeyParameters(DSTU4145PointEncoder.decodePoint(curve, octets), EC5Util.getDomainParameters(null, this.ecSpec));
    }
    
    public byte[] getSbox() {
        if (null != this.dstuParams) {
            return this.dstuParams.getDKE();
        }
        return DSTU4145Params.getDefaultDKE();
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        ASN1Object dstuParams;
        if (this.dstuParams != null) {
            dstuParams = this.dstuParams;
        }
        else if (this.ecSpec instanceof ECNamedCurveSpec) {
            dstuParams = new DSTU4145Params(new ASN1ObjectIdentifier(((ECNamedCurveSpec)this.ecSpec).getName()));
        }
        else {
            final ECCurve convertCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
            dstuParams = new X962Parameters(new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed()));
        }
        final byte[] encodePoint = DSTU4145PointEncoder.encodePoint(this.ecPublicKey.getQ());
        SubjectPublicKeyInfo subjectPublicKeyInfo;
        try {
            subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(UAObjectIdentifiers.dstu4145be, dstuParams), new DEROctetString(encodePoint));
        }
        catch (final IOException ex) {
            return null;
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
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
    
    public ECPoint getW() {
        return EC5Util.convertPoint(this.ecPublicKey.getQ());
    }
    
    public org.bouncycastle.math.ec.ECPoint getQ() {
        final org.bouncycastle.math.ec.ECPoint q = this.ecPublicKey.getQ();
        if (this.ecSpec == null) {
            return q.getDetachedPoint();
        }
        return q;
    }
    
    ECPublicKeyParameters engineGetKeyParameters() {
        return this.ecPublicKey;
    }
    
    org.bouncycastle.jce.spec.ECParameterSpec engineGetSpec() {
        if (this.ecSpec != null) {
            return EC5Util.convertSpec(this.ecSpec, this.withCompression);
        }
        return BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
    }
    
    @Override
    public String toString() {
        return ECUtil.publicKeyToString(this.algorithm, this.ecPublicKey.getQ(), this.engineGetSpec());
    }
    
    public void setPointFormat(final String s) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(s);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BCDSTU4145PublicKey)) {
            return false;
        }
        final BCDSTU4145PublicKey bcdstu4145PublicKey = (BCDSTU4145PublicKey)o;
        return this.ecPublicKey.getQ().equals(bcdstu4145PublicKey.ecPublicKey.getQ()) && this.engineGetSpec().equals(bcdstu4145PublicKey.engineGetSpec());
    }
    
    @Override
    public int hashCode() {
        return this.ecPublicKey.getQ().hashCode() ^ this.engineGetSpec().hashCode();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray((byte[])objectInputStream.readObject())));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}
