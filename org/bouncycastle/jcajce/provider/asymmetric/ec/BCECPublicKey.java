package org.bouncycastle.jcajce.provider.asymmetric.ec;

import java.io.ObjectOutputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.ObjectInputStream;
import java.security.spec.ECPoint;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.x9.X9ECPoint;
import java.io.IOException;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.ECDomainParameters;
import java.security.spec.EllipticCurve;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import java.security.spec.ECPublicKeySpec;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ECPointEncoder;
import java.security.interfaces.ECPublicKey;

public class BCECPublicKey implements ECPublicKey, org.bouncycastle.jce.interfaces.ECPublicKey, ECPointEncoder
{
    static final long serialVersionUID = 2422789860422731812L;
    private String algorithm;
    private boolean withCompression;
    private transient ECPublicKeyParameters ecPublicKey;
    private transient ECParameterSpec ecSpec;
    private transient ProviderConfiguration configuration;
    
    public BCECPublicKey(final String algorithm, final BCECPublicKey bcecPublicKey) {
        this.algorithm = "EC";
        this.algorithm = algorithm;
        this.ecPublicKey = bcecPublicKey.ecPublicKey;
        this.ecSpec = bcecPublicKey.ecSpec;
        this.withCompression = bcecPublicKey.withCompression;
        this.configuration = bcecPublicKey.configuration;
    }
    
    public BCECPublicKey(final String algorithm, final ECPublicKeySpec ecPublicKeySpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.algorithm = algorithm;
        this.ecSpec = ecPublicKeySpec.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, ecPublicKeySpec.getW(), false), EC5Util.getDomainParameters(configuration, ecPublicKeySpec.getParams()));
        this.configuration = configuration;
    }
    
    public BCECPublicKey(final String algorithm, final org.bouncycastle.jce.spec.ECPublicKeySpec ecPublicKeySpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.algorithm = algorithm;
        if (ecPublicKeySpec.getParams() != null) {
            final EllipticCurve convertCurve = EC5Util.convertCurve(ecPublicKeySpec.getParams().getCurve(), ecPublicKeySpec.getParams().getSeed());
            this.ecPublicKey = new ECPublicKeyParameters(ecPublicKeySpec.getQ(), ECUtil.getDomainParameters(configuration, ecPublicKeySpec.getParams()));
            this.ecSpec = EC5Util.convertSpec(convertCurve, ecPublicKeySpec.getParams());
        }
        else {
            this.ecPublicKey = new ECPublicKeyParameters(configuration.getEcImplicitlyCa().getCurve().createPoint(ecPublicKeySpec.getQ().getAffineXCoord().toBigInteger(), ecPublicKeySpec.getQ().getAffineYCoord().toBigInteger()), EC5Util.getDomainParameters(configuration, null));
            this.ecSpec = null;
        }
        this.configuration = configuration;
    }
    
    public BCECPublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey, final ECParameterSpec ecSpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        final ECDomainParameters parameters = ecPublicKey.getParameters();
        this.algorithm = algorithm;
        this.ecPublicKey = ecPublicKey;
        if (ecSpec == null) {
            this.ecSpec = this.createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters);
        }
        else {
            this.ecSpec = ecSpec;
        }
        this.configuration = configuration;
    }
    
    public BCECPublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        final ECDomainParameters parameters = ecPublicKey.getParameters();
        this.algorithm = algorithm;
        if (ecParameterSpec == null) {
            this.ecSpec = this.createSpec(EC5Util.convertCurve(parameters.getCurve(), parameters.getSeed()), parameters);
        }
        else {
            this.ecSpec = EC5Util.convertSpec(EC5Util.convertCurve(ecParameterSpec.getCurve(), ecParameterSpec.getSeed()), ecParameterSpec);
        }
        this.ecPublicKey = ecPublicKey;
        this.configuration = configuration;
    }
    
    public BCECPublicKey(final String algorithm, final ECPublicKeyParameters ecPublicKey, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.algorithm = algorithm;
        this.ecPublicKey = ecPublicKey;
        this.ecSpec = null;
        this.configuration = configuration;
    }
    
    public BCECPublicKey(final ECPublicKey ecPublicKey, final ProviderConfiguration providerConfiguration) {
        this.algorithm = "EC";
        this.algorithm = ecPublicKey.getAlgorithm();
        this.ecSpec = ecPublicKey.getParams();
        this.ecPublicKey = new ECPublicKeyParameters(EC5Util.convertPoint(this.ecSpec, ecPublicKey.getW(), false), EC5Util.getDomainParameters(providerConfiguration, ecPublicKey.getParams()));
    }
    
    BCECPublicKey(final String algorithm, final SubjectPublicKeyInfo subjectPublicKeyInfo, final ProviderConfiguration configuration) {
        this.algorithm = "EC";
        this.algorithm = algorithm;
        this.configuration = configuration;
        this.populateFromPubKeyInfo(subjectPublicKeyInfo);
    }
    
    private ECParameterSpec createSpec(final EllipticCurve ellipticCurve, final ECDomainParameters ecDomainParameters) {
        return new ECParameterSpec(ellipticCurve, EC5Util.convertPoint(ecDomainParameters.getG()), ecDomainParameters.getN(), ecDomainParameters.getH().intValue());
    }
    
    private void populateFromPubKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        final X962Parameters instance = X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
        final ECCurve curve = EC5Util.getCurve(this.configuration, instance);
        this.ecSpec = EC5Util.convertToSpec(instance, curve);
        final byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        ASN1OctetString asn1OctetString = new DEROctetString(bytes);
        if (bytes[0] == 4 && bytes[1] == bytes.length - 2 && (bytes[2] == 2 || bytes[2] == 3) && new X9IntegerConverter().getByteLength(curve) >= bytes.length - 3) {
            try {
                asn1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(bytes);
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("error recovering public key");
            }
        }
        this.ecPublicKey = new ECPublicKeyParameters(new X9ECPoint(curve, asn1OctetString).getPoint(), ECUtil.getDomainParameters(this.configuration, instance));
    }
    
    public String getAlgorithm() {
        return this.algorithm;
    }
    
    public String getFormat() {
        return "X.509";
    }
    
    public byte[] getEncoded() {
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, ECUtils.getDomainParametersFromName(this.ecSpec, this.withCompression)), ASN1OctetString.getInstance(new X9ECPoint(this.ecPublicKey.getQ(), this.withCompression).toASN1Primitive()).getOctets()));
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
        return this.configuration.getEcImplicitlyCa();
    }
    
    @Override
    public String toString() {
        return ECUtil.publicKeyToString("EC", this.ecPublicKey.getQ(), this.engineGetSpec());
    }
    
    public void setPointFormat(final String s) {
        this.withCompression = !"UNCOMPRESSED".equalsIgnoreCase(s);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BCECPublicKey)) {
            return false;
        }
        final BCECPublicKey bcecPublicKey = (BCECPublicKey)o;
        return this.ecPublicKey.getQ().equals(bcecPublicKey.ecPublicKey.getQ()) && this.engineGetSpec().equals(bcecPublicKey.engineGetSpec());
    }
    
    @Override
    public int hashCode() {
        return this.ecPublicKey.getQ().hashCode() ^ this.engineGetSpec().hashCode();
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        final byte[] array = (byte[])objectInputStream.readObject();
        this.configuration = BouncyCastleProvider.CONFIGURATION;
        this.populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(array)));
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(this.getEncoded());
    }
}
