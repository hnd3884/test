package org.bouncycastle.jcajce.provider.asymmetric.ec;

import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.x9.X962Parameters;
import java.io.IOException;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECParameterSpec;

public class AlgorithmParametersSpi extends java.security.AlgorithmParametersSpi
{
    private ECParameterSpec ecParameterSpec;
    private String curveName;
    
    protected boolean isASN1FormatString(final String s) {
        return s == null || s.equals("ASN.1");
    }
    
    @Override
    protected void engineInit(final AlgorithmParameterSpec algorithmParameterSpec) throws InvalidParameterSpecException {
        if (algorithmParameterSpec instanceof ECGenParameterSpec) {
            final ECGenParameterSpec ecGenParameterSpec = (ECGenParameterSpec)algorithmParameterSpec;
            final X9ECParameters domainParametersFromGenSpec = ECUtils.getDomainParametersFromGenSpec(ecGenParameterSpec);
            if (domainParametersFromGenSpec == null) {
                throw new InvalidParameterSpecException("EC curve name not recognized: " + ecGenParameterSpec.getName());
            }
            this.curveName = ecGenParameterSpec.getName();
            this.ecParameterSpec = EC5Util.convertToSpec(domainParametersFromGenSpec);
        }
        else {
            if (!(algorithmParameterSpec instanceof ECParameterSpec)) {
                throw new InvalidParameterSpecException("AlgorithmParameterSpec class not recognized: " + algorithmParameterSpec.getClass().getName());
            }
            if (algorithmParameterSpec instanceof ECNamedCurveSpec) {
                this.curveName = ((ECNamedCurveSpec)algorithmParameterSpec).getName();
            }
            else {
                this.curveName = null;
            }
            this.ecParameterSpec = (ECParameterSpec)algorithmParameterSpec;
        }
    }
    
    @Override
    protected void engineInit(final byte[] array) throws IOException {
        this.engineInit(array, "ASN.1");
    }
    
    @Override
    protected void engineInit(final byte[] array, final String s) throws IOException {
        if (this.isASN1FormatString(s)) {
            final X962Parameters instance = X962Parameters.getInstance(array);
            final ECCurve curve = EC5Util.getCurve(BouncyCastleProvider.CONFIGURATION, instance);
            if (instance.isNamedCurve()) {
                final ASN1ObjectIdentifier instance2 = ASN1ObjectIdentifier.getInstance(instance.getParameters());
                this.curveName = ECNamedCurveTable.getName(instance2);
                if (this.curveName == null) {
                    this.curveName = instance2.getId();
                }
            }
            this.ecParameterSpec = EC5Util.convertToSpec(instance, curve);
            return;
        }
        throw new IOException("Unknown encoded parameters format in AlgorithmParameters object: " + s);
    }
    
    @Override
    protected <T extends AlgorithmParameterSpec> T engineGetParameterSpec(final Class<T> clazz) throws InvalidParameterSpecException {
        if (ECParameterSpec.class.isAssignableFrom(clazz) || clazz == AlgorithmParameterSpec.class) {
            return (T)this.ecParameterSpec;
        }
        if (ECGenParameterSpec.class.isAssignableFrom(clazz)) {
            if (this.curveName != null) {
                final ASN1ObjectIdentifier namedCurveOid = ECUtil.getNamedCurveOid(this.curveName);
                if (namedCurveOid != null) {
                    return (T)new ECGenParameterSpec(namedCurveOid.getId());
                }
                return (T)new ECGenParameterSpec(this.curveName);
            }
            else {
                final ASN1ObjectIdentifier namedCurveOid2 = ECUtil.getNamedCurveOid(EC5Util.convertSpec(this.ecParameterSpec, false));
                if (namedCurveOid2 != null) {
                    return (T)new ECGenParameterSpec(namedCurveOid2.getId());
                }
            }
        }
        throw new InvalidParameterSpecException("EC AlgorithmParameters cannot convert to " + clazz.getName());
    }
    
    @Override
    protected byte[] engineGetEncoded() throws IOException {
        return this.engineGetEncoded("ASN.1");
    }
    
    @Override
    protected byte[] engineGetEncoded(final String s) throws IOException {
        if (this.isASN1FormatString(s)) {
            X962Parameters x962Parameters;
            if (this.ecParameterSpec == null) {
                x962Parameters = new X962Parameters(DERNull.INSTANCE);
            }
            else if (this.curveName != null) {
                x962Parameters = new X962Parameters(ECUtil.getNamedCurveOid(this.curveName));
            }
            else {
                final org.bouncycastle.jce.spec.ECParameterSpec convertSpec = EC5Util.convertSpec(this.ecParameterSpec, false);
                x962Parameters = new X962Parameters(new X9ECParameters(convertSpec.getCurve(), convertSpec.getG(), convertSpec.getN(), convertSpec.getH(), convertSpec.getSeed()));
            }
            return x962Parameters.getEncoded();
        }
        throw new IOException("Unknown parameters format in AlgorithmParameters object: " + s);
    }
    
    @Override
    protected String engineToString() {
        return "EC AlgorithmParameters ";
    }
}
