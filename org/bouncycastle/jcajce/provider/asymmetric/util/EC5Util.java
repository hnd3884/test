package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.util.Enumeration;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import java.util.HashMap;
import java.security.spec.ECPoint;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.field.Polynomial;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.field.FiniteField;
import java.math.BigInteger;
import java.security.spec.ECField;
import java.security.spec.ECFieldF2m;
import java.security.spec.ECFieldFp;
import java.security.spec.EllipticCurve;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.crypto.params.ECDomainParameters;
import java.security.spec.ECParameterSpec;
import java.util.Set;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;
import java.util.Map;

public class EC5Util
{
    private static Map customCurves;
    
    public static ECCurve getCurve(final ProviderConfiguration providerConfiguration, final X962Parameters x962Parameters) {
        final Set acceptableNamedCurves = providerConfiguration.getAcceptableNamedCurves();
        ECCurve ecCurve;
        if (x962Parameters.isNamedCurve()) {
            final ASN1ObjectIdentifier instance = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
            if (!acceptableNamedCurves.isEmpty() && !acceptableNamedCurves.contains(instance)) {
                throw new IllegalStateException("named curve not acceptable");
            }
            X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(instance);
            if (namedCurveByOid == null) {
                namedCurveByOid = providerConfiguration.getAdditionalECParameters().get(instance);
            }
            ecCurve = namedCurveByOid.getCurve();
        }
        else if (x962Parameters.isImplicitlyCA()) {
            ecCurve = providerConfiguration.getEcImplicitlyCa().getCurve();
        }
        else {
            if (!acceptableNamedCurves.isEmpty()) {
                throw new IllegalStateException("encoded parameters not acceptable");
            }
            ecCurve = X9ECParameters.getInstance(x962Parameters.getParameters()).getCurve();
        }
        return ecCurve;
    }
    
    public static ECDomainParameters getDomainParameters(final ProviderConfiguration providerConfiguration, final ECParameterSpec ecParameterSpec) {
        ECDomainParameters domainParameters;
        if (ecParameterSpec == null) {
            final org.bouncycastle.jce.spec.ECParameterSpec ecImplicitlyCa = providerConfiguration.getEcImplicitlyCa();
            domainParameters = new ECDomainParameters(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getG(), ecImplicitlyCa.getN(), ecImplicitlyCa.getH(), ecImplicitlyCa.getSeed());
        }
        else {
            domainParameters = ECUtil.getDomainParameters(providerConfiguration, convertSpec(ecParameterSpec, false));
        }
        return domainParameters;
    }
    
    public static ECParameterSpec convertToSpec(final X962Parameters x962Parameters, final ECCurve ecCurve) {
        ECParameterSpec ecParameterSpec;
        if (x962Parameters.isNamedCurve()) {
            final ASN1ObjectIdentifier asn1ObjectIdentifier = (ASN1ObjectIdentifier)x962Parameters.getParameters();
            X9ECParameters namedCurveByOid = ECUtil.getNamedCurveByOid(asn1ObjectIdentifier);
            if (namedCurveByOid == null) {
                final Map additionalECParameters = BouncyCastleProvider.CONFIGURATION.getAdditionalECParameters();
                if (!additionalECParameters.isEmpty()) {
                    namedCurveByOid = (X9ECParameters)additionalECParameters.get(asn1ObjectIdentifier);
                }
            }
            ecParameterSpec = new ECNamedCurveSpec(ECUtil.getCurveName(asn1ObjectIdentifier), convertCurve(ecCurve, namedCurveByOid.getSeed()), convertPoint(namedCurveByOid.getG()), namedCurveByOid.getN(), namedCurveByOid.getH());
        }
        else if (x962Parameters.isImplicitlyCA()) {
            ecParameterSpec = null;
        }
        else {
            final X9ECParameters instance = X9ECParameters.getInstance(x962Parameters.getParameters());
            final EllipticCurve convertCurve = convertCurve(ecCurve, instance.getSeed());
            if (instance.getH() != null) {
                ecParameterSpec = new ECParameterSpec(convertCurve, convertPoint(instance.getG()), instance.getN(), instance.getH().intValue());
            }
            else {
                ecParameterSpec = new ECParameterSpec(convertCurve, convertPoint(instance.getG()), instance.getN(), 1);
            }
        }
        return ecParameterSpec;
    }
    
    public static ECParameterSpec convertToSpec(final X9ECParameters x9ECParameters) {
        return new ECParameterSpec(convertCurve(x9ECParameters.getCurve(), null), convertPoint(x9ECParameters.getG()), x9ECParameters.getN(), x9ECParameters.getH().intValue());
    }
    
    public static EllipticCurve convertCurve(final ECCurve ecCurve, final byte[] array) {
        return new EllipticCurve(convertField(ecCurve.getField()), ecCurve.getA().toBigInteger(), ecCurve.getB().toBigInteger(), null);
    }
    
    public static ECCurve convertCurve(final EllipticCurve ellipticCurve) {
        final ECField field = ellipticCurve.getField();
        final BigInteger a = ellipticCurve.getA();
        final BigInteger b = ellipticCurve.getB();
        if (!(field instanceof ECFieldFp)) {
            final ECFieldF2m ecFieldF2m = (ECFieldF2m)field;
            final int m = ecFieldF2m.getM();
            final int[] convertMidTerms = ECUtil.convertMidTerms(ecFieldF2m.getMidTermsOfReductionPolynomial());
            return new ECCurve.F2m(m, convertMidTerms[0], convertMidTerms[1], convertMidTerms[2], a, b);
        }
        final ECCurve.Fp fp = new ECCurve.Fp(((ECFieldFp)field).getP(), a, b);
        if (EC5Util.customCurves.containsKey(fp)) {
            return (ECCurve)EC5Util.customCurves.get(fp);
        }
        return fp;
    }
    
    public static ECField convertField(final FiniteField finiteField) {
        if (ECAlgorithms.isFpField(finiteField)) {
            return new ECFieldFp(finiteField.getCharacteristic());
        }
        final Polynomial minimalPolynomial = ((PolynomialExtensionField)finiteField).getMinimalPolynomial();
        final int[] exponentsPresent = minimalPolynomial.getExponentsPresent();
        return new ECFieldF2m(minimalPolynomial.getDegree(), Arrays.reverse(Arrays.copyOfRange(exponentsPresent, 1, exponentsPresent.length - 1)));
    }
    
    public static ECParameterSpec convertSpec(final EllipticCurve ellipticCurve, final org.bouncycastle.jce.spec.ECParameterSpec ecParameterSpec) {
        if (ecParameterSpec instanceof ECNamedCurveParameterSpec) {
            return new ECNamedCurveSpec(((ECNamedCurveParameterSpec)ecParameterSpec).getName(), ellipticCurve, convertPoint(ecParameterSpec.getG()), ecParameterSpec.getN(), ecParameterSpec.getH());
        }
        return new ECParameterSpec(ellipticCurve, convertPoint(ecParameterSpec.getG()), ecParameterSpec.getN(), ecParameterSpec.getH().intValue());
    }
    
    public static org.bouncycastle.jce.spec.ECParameterSpec convertSpec(final ECParameterSpec ecParameterSpec, final boolean b) {
        final ECCurve convertCurve = convertCurve(ecParameterSpec.getCurve());
        return new org.bouncycastle.jce.spec.ECParameterSpec(convertCurve, convertPoint(convertCurve, ecParameterSpec.getGenerator(), b), ecParameterSpec.getOrder(), BigInteger.valueOf(ecParameterSpec.getCofactor()), ecParameterSpec.getCurve().getSeed());
    }
    
    public static org.bouncycastle.math.ec.ECPoint convertPoint(final ECParameterSpec ecParameterSpec, final ECPoint ecPoint, final boolean b) {
        return convertPoint(convertCurve(ecParameterSpec.getCurve()), ecPoint, b);
    }
    
    public static org.bouncycastle.math.ec.ECPoint convertPoint(final ECCurve ecCurve, final ECPoint ecPoint, final boolean b) {
        return ecCurve.createPoint(ecPoint.getAffineX(), ecPoint.getAffineY());
    }
    
    public static ECPoint convertPoint(org.bouncycastle.math.ec.ECPoint normalize) {
        normalize = normalize.normalize();
        return new ECPoint(normalize.getAffineXCoord().toBigInteger(), normalize.getAffineYCoord().toBigInteger());
    }
    
    static {
        EC5Util.customCurves = new HashMap();
        final Enumeration names = CustomNamedCurves.getNames();
        while (names.hasMoreElements()) {
            final String s = names.nextElement();
            final X9ECParameters byName = ECNamedCurveTable.getByName(s);
            if (byName != null) {
                EC5Util.customCurves.put(byName.getCurve(), CustomNamedCurves.getByName(s).getCurve());
            }
        }
        final X9ECParameters byName2 = CustomNamedCurves.getByName("Curve25519");
        EC5Util.customCurves.put(new ECCurve.Fp(byName2.getCurve().getField().getCharacteristic(), byName2.getCurve().getA().toBigInteger(), byName2.getCurve().getB().toBigInteger()), byName2.getCurve());
    }
}
