package org.bouncycastle.jcajce.provider.asymmetric.util;

import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import java.util.Enumeration;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import java.math.BigInteger;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import java.security.PrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.security.InvalidKeyException;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.security.PublicKey;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jcajce.provider.config.ProviderConfiguration;

public class ECUtil
{
    static int[] convertMidTerms(final int[] array) {
        final int[] array2 = new int[3];
        if (array.length == 1) {
            array2[0] = array[0];
        }
        else {
            if (array.length != 3) {
                throw new IllegalArgumentException("Only Trinomials and pentanomials supported");
            }
            if (array[0] < array[1] && array[0] < array[2]) {
                array2[0] = array[0];
                if (array[1] < array[2]) {
                    array2[1] = array[1];
                    array2[2] = array[2];
                }
                else {
                    array2[1] = array[2];
                    array2[2] = array[1];
                }
            }
            else if (array[1] < array[2]) {
                array2[0] = array[1];
                if (array[0] < array[2]) {
                    array2[1] = array[0];
                    array2[2] = array[2];
                }
                else {
                    array2[1] = array[2];
                    array2[2] = array[0];
                }
            }
            else {
                array2[0] = array[2];
                if (array[0] < array[1]) {
                    array2[1] = array[0];
                    array2[2] = array[1];
                }
                else {
                    array2[1] = array[1];
                    array2[2] = array[0];
                }
            }
        }
        return array2;
    }
    
    public static ECDomainParameters getDomainParameters(final ProviderConfiguration providerConfiguration, final ECParameterSpec ecParameterSpec) {
        ECDomainParameters ecDomainParameters;
        if (ecParameterSpec instanceof ECNamedCurveParameterSpec) {
            final ECNamedCurveParameterSpec ecNamedCurveParameterSpec = (ECNamedCurveParameterSpec)ecParameterSpec;
            ecDomainParameters = new ECNamedDomainParameters(getNamedCurveOid(ecNamedCurveParameterSpec.getName()), ecNamedCurveParameterSpec.getCurve(), ecNamedCurveParameterSpec.getG(), ecNamedCurveParameterSpec.getN(), ecNamedCurveParameterSpec.getH(), ecNamedCurveParameterSpec.getSeed());
        }
        else if (ecParameterSpec == null) {
            final ECParameterSpec ecImplicitlyCa = providerConfiguration.getEcImplicitlyCa();
            ecDomainParameters = new ECDomainParameters(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getG(), ecImplicitlyCa.getN(), ecImplicitlyCa.getH(), ecImplicitlyCa.getSeed());
        }
        else {
            ecDomainParameters = new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN(), ecParameterSpec.getH(), ecParameterSpec.getSeed());
        }
        return ecDomainParameters;
    }
    
    public static ECDomainParameters getDomainParameters(final ProviderConfiguration providerConfiguration, final X962Parameters x962Parameters) {
        ECDomainParameters ecDomainParameters;
        if (x962Parameters.isNamedCurve()) {
            final ASN1ObjectIdentifier instance = ASN1ObjectIdentifier.getInstance(x962Parameters.getParameters());
            X9ECParameters namedCurveByOid = getNamedCurveByOid(instance);
            if (namedCurveByOid == null) {
                namedCurveByOid = providerConfiguration.getAdditionalECParameters().get(instance);
            }
            ecDomainParameters = new ECNamedDomainParameters(instance, namedCurveByOid.getCurve(), namedCurveByOid.getG(), namedCurveByOid.getN(), namedCurveByOid.getH(), namedCurveByOid.getSeed());
        }
        else if (x962Parameters.isImplicitlyCA()) {
            final ECParameterSpec ecImplicitlyCa = providerConfiguration.getEcImplicitlyCa();
            ecDomainParameters = new ECDomainParameters(ecImplicitlyCa.getCurve(), ecImplicitlyCa.getG(), ecImplicitlyCa.getN(), ecImplicitlyCa.getH(), ecImplicitlyCa.getSeed());
        }
        else {
            final X9ECParameters instance2 = X9ECParameters.getInstance(x962Parameters.getParameters());
            ecDomainParameters = new ECDomainParameters(instance2.getCurve(), instance2.getG(), instance2.getN(), instance2.getH(), instance2.getSeed());
        }
        return ecDomainParameters;
    }
    
    public static AsymmetricKeyParameter generatePublicKeyParameter(final PublicKey publicKey) throws InvalidKeyException {
        if (publicKey instanceof ECPublicKey) {
            final ECPublicKey ecPublicKey = (ECPublicKey)publicKey;
            final ECParameterSpec parameters = ecPublicKey.getParameters();
            return new ECPublicKeyParameters(ecPublicKey.getQ(), new ECDomainParameters(parameters.getCurve(), parameters.getG(), parameters.getN(), parameters.getH(), parameters.getSeed()));
        }
        if (publicKey instanceof java.security.interfaces.ECPublicKey) {
            final java.security.interfaces.ECPublicKey ecPublicKey2 = (java.security.interfaces.ECPublicKey)publicKey;
            final ECParameterSpec convertSpec = EC5Util.convertSpec(ecPublicKey2.getParams(), false);
            return new ECPublicKeyParameters(EC5Util.convertPoint(ecPublicKey2.getParams(), ecPublicKey2.getW(), false), new ECDomainParameters(convertSpec.getCurve(), convertSpec.getG(), convertSpec.getN(), convertSpec.getH(), convertSpec.getSeed()));
        }
        try {
            final byte[] encoded = publicKey.getEncoded();
            if (encoded == null) {
                throw new InvalidKeyException("no encoding for EC public key");
            }
            final PublicKey publicKey2 = BouncyCastleProvider.getPublicKey(SubjectPublicKeyInfo.getInstance(encoded));
            if (publicKey2 instanceof java.security.interfaces.ECPublicKey) {
                return generatePublicKeyParameter(publicKey2);
            }
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("cannot identify EC public key: " + ex.toString());
        }
        throw new InvalidKeyException("cannot identify EC public key.");
    }
    
    public static AsymmetricKeyParameter generatePrivateKeyParameter(final PrivateKey privateKey) throws InvalidKeyException {
        if (privateKey instanceof ECPrivateKey) {
            final ECPrivateKey ecPrivateKey = (ECPrivateKey)privateKey;
            ECParameterSpec ecParameterSpec = ecPrivateKey.getParameters();
            if (ecParameterSpec == null) {
                ecParameterSpec = BouncyCastleProvider.CONFIGURATION.getEcImplicitlyCa();
            }
            return new ECPrivateKeyParameters(ecPrivateKey.getD(), new ECDomainParameters(ecParameterSpec.getCurve(), ecParameterSpec.getG(), ecParameterSpec.getN(), ecParameterSpec.getH(), ecParameterSpec.getSeed()));
        }
        if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
            final java.security.interfaces.ECPrivateKey ecPrivateKey2 = (java.security.interfaces.ECPrivateKey)privateKey;
            final ECParameterSpec convertSpec = EC5Util.convertSpec(ecPrivateKey2.getParams(), false);
            return new ECPrivateKeyParameters(ecPrivateKey2.getS(), new ECDomainParameters(convertSpec.getCurve(), convertSpec.getG(), convertSpec.getN(), convertSpec.getH(), convertSpec.getSeed()));
        }
        try {
            final byte[] encoded = privateKey.getEncoded();
            if (encoded == null) {
                throw new InvalidKeyException("no encoding for EC private key");
            }
            final PrivateKey privateKey2 = BouncyCastleProvider.getPrivateKey(PrivateKeyInfo.getInstance(encoded));
            if (privateKey2 instanceof java.security.interfaces.ECPrivateKey) {
                return generatePrivateKeyParameter(privateKey2);
            }
        }
        catch (final Exception ex) {
            throw new InvalidKeyException("cannot identify EC private key: " + ex.toString());
        }
        throw new InvalidKeyException("can't identify EC private key.");
    }
    
    public static int getOrderBitLength(final ProviderConfiguration providerConfiguration, final BigInteger bigInteger, final BigInteger bigInteger2) {
        if (bigInteger != null) {
            return bigInteger.bitLength();
        }
        final ECParameterSpec ecImplicitlyCa = providerConfiguration.getEcImplicitlyCa();
        if (ecImplicitlyCa == null) {
            return bigInteger2.bitLength();
        }
        return ecImplicitlyCa.getN().bitLength();
    }
    
    public static ASN1ObjectIdentifier getNamedCurveOid(final String s) {
        String substring = s;
        final int index = substring.indexOf(32);
        if (index > 0) {
            substring = substring.substring(index + 1);
        }
        try {
            if (substring.charAt(0) >= '0' && substring.charAt(0) <= '2') {
                return new ASN1ObjectIdentifier(substring);
            }
        }
        catch (final IllegalArgumentException ex) {}
        return ECNamedCurveTable.getOID(substring);
    }
    
    public static ASN1ObjectIdentifier getNamedCurveOid(final ECParameterSpec ecParameterSpec) {
        final Enumeration names = ECNamedCurveTable.getNames();
        while (names.hasMoreElements()) {
            final String s = names.nextElement();
            final X9ECParameters byName = ECNamedCurveTable.getByName(s);
            if (byName.getN().equals(ecParameterSpec.getN()) && byName.getH().equals(ecParameterSpec.getH()) && byName.getCurve().equals(ecParameterSpec.getCurve()) && byName.getG().equals(ecParameterSpec.getG())) {
                return ECNamedCurveTable.getOID(s);
            }
        }
        return null;
    }
    
    public static X9ECParameters getNamedCurveByOid(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        X9ECParameters x9ECParameters = CustomNamedCurves.getByOID(asn1ObjectIdentifier);
        if (x9ECParameters == null) {
            x9ECParameters = ECNamedCurveTable.getByOID(asn1ObjectIdentifier);
        }
        return x9ECParameters;
    }
    
    public static X9ECParameters getNamedCurveByName(final String s) {
        X9ECParameters x9ECParameters = CustomNamedCurves.getByName(s);
        if (x9ECParameters == null) {
            x9ECParameters = ECNamedCurveTable.getByName(s);
        }
        return x9ECParameters;
    }
    
    public static String getCurveName(final ASN1ObjectIdentifier asn1ObjectIdentifier) {
        return ECNamedCurveTable.getName(asn1ObjectIdentifier);
    }
    
    public static String privateKeyToString(final String s, final BigInteger bigInteger, final ECParameterSpec ecParameterSpec) {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        final ECPoint calculateQ = calculateQ(bigInteger, ecParameterSpec);
        sb.append(s);
        sb.append(" Private Key [").append(generateKeyFingerprint(calculateQ, ecParameterSpec)).append("]").append(lineSeparator);
        sb.append("            X: ").append(calculateQ.getAffineXCoord().toBigInteger().toString(16)).append(lineSeparator);
        sb.append("            Y: ").append(calculateQ.getAffineYCoord().toBigInteger().toString(16)).append(lineSeparator);
        return sb.toString();
    }
    
    private static ECPoint calculateQ(final BigInteger bigInteger, final ECParameterSpec ecParameterSpec) {
        return ecParameterSpec.getG().multiply(bigInteger).normalize();
    }
    
    public static String publicKeyToString(final String s, final ECPoint ecPoint, final ECParameterSpec ecParameterSpec) {
        final StringBuffer sb = new StringBuffer();
        final String lineSeparator = Strings.lineSeparator();
        sb.append(s);
        sb.append(" Public Key [").append(generateKeyFingerprint(ecPoint, ecParameterSpec)).append("]").append(lineSeparator);
        sb.append("            X: ").append(ecPoint.getAffineXCoord().toBigInteger().toString(16)).append(lineSeparator);
        sb.append("            Y: ").append(ecPoint.getAffineYCoord().toBigInteger().toString(16)).append(lineSeparator);
        return sb.toString();
    }
    
    public static String generateKeyFingerprint(final ECPoint ecPoint, final ECParameterSpec ecParameterSpec) {
        final ECCurve curve = ecParameterSpec.getCurve();
        final ECPoint g = ecParameterSpec.getG();
        if (curve != null) {
            return new Fingerprint(Arrays.concatenate(ecPoint.getEncoded(false), curve.getA().getEncoded(), curve.getB().getEncoded(), g.getEncoded(false))).toString();
        }
        return new Fingerprint(ecPoint.getEncoded(false)).toString();
    }
}
