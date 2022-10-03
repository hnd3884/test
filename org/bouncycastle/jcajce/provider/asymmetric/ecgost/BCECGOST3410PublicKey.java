package org.bouncycastle.jcajce.provider.asymmetric.ecgost;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.security.spec.ECPoint;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import java.math.BigInteger;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.ECGOST3410NamedCurveTable;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
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
import org.bouncycastle.asn1.ASN1Encodable;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import java.security.interfaces.ECPublicKey;

public class BCECGOST3410PublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder
{
    static final long serialVersionUID = 7026240464295649314L;
    private String algorithm;
    private boolean withCompression;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private transient ASN1Encodable gostParams;
    
    public BCECGOST3410PublicKey(final BCECGOST3410PublicKey bcecgost3410PublicKey) {
        this.algorithm = "ECGOST3410";
        this.ecPublicKey = bcecgost3410PublicKey.ecPublicKey;
        this.ecSpec = bcecgost3410PublicKey.ecSpec;
        this.withCompression = bcecgost3410PublicKey.withCompression;
        this.gostParams = bcecgost3410PublicKey.gostParams;
    }
    
    public BCECGOST3410PublicKey(final ECPublicKeySpec ecPublicKeySpec) {
        this.algorithm = "ECGOST3410";
        this.ecSpec = ecPublicKeySpec.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, ecPublicKeySpec.getW(), false), EC5Util.getDomainParameters(null, ecPublicKeySpec.getParams()));
    }
    
    public BCECGOST3410PublicKey(final org.bouncycastle.jce.spec.ECPublicKeySpec ecPublicKeySpec, final ProviderConfiguration providerConfiguration) {
        this.algorithm = "ECGOST3410";
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
    
    public BCECGOST3410PublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey, final ECParameterSpec ecSpec) {
        this.algorithm = "ECGOST3410";
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
    
    public BCECGOST3410PublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        this.algorithm = "ECGOST3410";
        final ECDomainParameters parameters = ecPublicKey.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = ecPublicKey;
        if (ecParameterSpec == null) {
            this.ecSpec = this.createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters);
        }
        else {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecParameterSpec.getCurve(), ecParameterSpec.getSeed()), ecParameterSpec);
        }
    }
    
    public BCECGOST3410PublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey) {
        this.algorithm = "ECGOST3410";
        this.algorithm = algorithm;
        this.ecPublicKey = ecPublicKey;
        this.ecSpec = null;
    }
    
    private ECParameterSpec createSpec(final EllipticCurve ellipticCurve, final ECDomainParameters ecDomainParameters) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(ecDomainParameters.getG()), ecDomainParameters.getN(), ecDomainParameters.getH().intValue());
    }
    
    public BCECGOST3410PublicKey(final ECPublicKey ecPublicKey) {
        this.algorithm = "ECGOST3410";
        this.algorithm = ecPublicKey.getAlgorithm();
        this.ecSpec = ecPublicKey.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, ecPublicKey.getW(), false), EC5Util.getDomainParameters(null, ecPublicKey.getParams()));
    }
    
    BCECGOST3410PublicKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.algorithm = "ECGOST3410";
        this.populateFromPubKeyInfo(subjectPublicKeyInfo);
    }
    
    private void populateFromPubKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final DERBitString publicKeyData = subjectPublicKeyInfo.getPublicKeyData();
        this.algorithm = "ECGOST3410";
        ASN1OctetString asn1OctetString;
        try {
            asn1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(publicKeyData.getBytes());
        }
        catch (final IOException ex) {
            throw new IllegalArgumentException("error recovering public key");
        }
        final byte[] octets = asn1OctetString.getOctets();
        final byte[] array = new byte[65];
        array[0] = 4;
        for (int i = 1; i <= 32; ++i) {
            array[i] = octets[32 - i];
            array[i + 32] = octets[64 - i];
        }
        ASN1ObjectIdentifier gostParams;
        if (subjectPublicKeyInfo.getAlgorithm().getParameters() instanceof ASN1ObjectIdentifier) {
            gostParams = ASN1ObjectIdentifier.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            this.gostParams = gostParams;
        }
        else {
            final GOST3410PublicKeyAlgParameters instance = GOST3410PublicKeyAlgParameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            this.gostParams = instance;
            gostParams = instance.getPublicKeyParamSet();
        }
        final ECNamedCurveParameterSpec parameterSpec = ECGOST3410NamedCurveTable.getParameterSpec(ECGOST3410NamedCurves.getName(gostParams));
        final ECCurve curve = parameterSpec.getCurve();
        final EllipticCurve convertCurve = EC5Util.convertCurve(curve, parameterSpec.getSeed());
        this.ecPublicKey = new ECPublicKeyParameters(curve.decodePoint(array), ECUtil.getDomainParameters(null, parameterSpec));
        this.ecSpec = new ECNamedCurveSpec(ECGOST3410NamedCurves.getName(gostParams), convertCurve, EC5Util.convertPoint(parameterSpec.getG()), parameterSpec.getN(), parameterSpec.getH());
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        ASN1Encodable gostParams = this.getGostParams();
        if (gostParams == null) {
            if (this.ecSpec instanceof ECNamedCurveSpec) {
                gostParams = new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet);
            }
            else {
                final ECCurve convertCurve = EC5Util.convertCurve(this.ecSpec.getCurve());
                gostParams = new X962Parameters(new X9ECParameters(convertCurve, EC5Util.convertPoint(convertCurve, this.ecSpec.getGenerator(), this.withCompression), this.ecSpec.getOrder(), BigInteger.valueOf(this.ecSpec.getCofactor()), this.ecSpec.getCurve().getSeed()));
            }
        }
        final BigInteger bigInteger = this.ecPublicKey.getQ().getAffineXCoord().toBigInteger();
        final BigInteger bigInteger2 = this.ecPublicKey.getQ().getAffineYCoord().toBigInteger();
        final byte[] array = new byte[64];
        this.extractBytes(array, 0, bigInteger);
        this.extractBytes(array, 32, bigInteger2);
        SubjectPublicKeyInfo subjectPublicKeyInfo;
        try {
            subjectPublicKeyInfo = new SubjectPublicKeyInfo(new AlgorithmIdentifier(CryptoProObjectIdentifiers.gostR3410_2001, gostParams), new DEROctetString(array));
        }
        catch (final IOException ex) {
            return null;
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(subjectPublicKeyInfo);
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
    
    public ECPoint getW() {
        return EC5Util.convertPoint(this.ecPublicKey.getQ());
    }
    
    public org.bouncycastle.math.ec.ECPoint getQ() {
        if (this.ecSpec == null) {
            return this.ecPublicKey.getQ().getDetachedPoint();
        }
        return this.ecPublicKey.getQ();
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
        if (!(o instanceof BCECGOST3410PublicKey)) {
            return false;
        }
        final BCECGOST3410PublicKey bcecgost3410PublicKey = (BCECGOST3410PublicKey)o;
        return this.ecPublicKey.getQ().equals(bcecgost3410PublicKey.ecPublicKey.getQ()) && this.engineGetSpec().equals(bcecgost3410PublicKey.engineGetSpec());
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
    
    ASN1Encodable getGostParams() {
        if (this.gostParams == null && this.ecSpec instanceof ECNamedCurveSpec) {
            this.gostParams = new GOST3410PublicKeyAlgParameters(ECGOST3410NamedCurves.getOID(((ECNamedCurveSpec)this.ecSpec).getName()), CryptoProObjectIdentifiers.gostR3411_94_CryptoProParamSet);
        }
        return this.gostParams;
    }
}
