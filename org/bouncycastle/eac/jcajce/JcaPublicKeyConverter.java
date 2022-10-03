package org.bouncycastle.eac.jcajce;

import org.bouncycastle.math.field.Polynomial;
import java.security.spec.ECFieldF2m;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.math.field.PolynomialExtensionField;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.field.FiniteField;
import java.security.spec.ECField;
import java.math.BigInteger;
import java.security.spec.EllipticCurve;
import java.security.spec.ECFieldFp;
import java.security.interfaces.ECPublicKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import java.security.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import java.security.spec.ECPoint;
import java.security.KeyFactory;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.bouncycastle.eac.EACException;
import java.security.spec.KeySpec;
import java.security.spec.RSAPublicKeySpec;
import org.bouncycastle.asn1.eac.RSAPublicKey;
import org.bouncycastle.asn1.eac.ECDSAPublicKey;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import java.security.PublicKey;
import org.bouncycastle.asn1.eac.PublicKeyDataObject;
import java.security.Provider;

public class JcaPublicKeyConverter
{
    private EACHelper helper;
    
    public JcaPublicKeyConverter() {
        this.helper = new DefaultEACHelper();
    }
    
    public JcaPublicKeyConverter setProvider(final String s) {
        this.helper = new NamedEACHelper(s);
        return this;
    }
    
    public JcaPublicKeyConverter setProvider(final Provider provider) {
        this.helper = new ProviderEACHelper(provider);
        return this;
    }
    
    public PublicKey getKey(final PublicKeyDataObject publicKeyDataObject) throws EACException, InvalidKeySpecException {
        if (publicKeyDataObject.getUsage().on(EACObjectIdentifiers.id_TA_ECDSA)) {
            return this.getECPublicKeyPublicKey((ECDSAPublicKey)publicKeyDataObject);
        }
        final RSAPublicKey rsaPublicKey = (RSAPublicKey)publicKeyDataObject;
        final RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
        try {
            return this.helper.createKeyFactory("RSA").generatePublic(rsaPublicKeySpec);
        }
        catch (final NoSuchProviderException ex) {
            throw new EACException("cannot find provider: " + ex.getMessage(), ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new EACException("cannot find algorithm ECDSA: " + ex2.getMessage(), ex2);
        }
    }
    
    private PublicKey getECPublicKeyPublicKey(final ECDSAPublicKey ecdsaPublicKey) throws EACException, InvalidKeySpecException {
        final ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(this.getPublicPoint(ecdsaPublicKey), this.getParams(ecdsaPublicKey));
        KeyFactory keyFactory;
        try {
            keyFactory = this.helper.createKeyFactory("ECDSA");
        }
        catch (final NoSuchProviderException ex) {
            throw new EACException("cannot find provider: " + ex.getMessage(), ex);
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new EACException("cannot find algorithm ECDSA: " + ex2.getMessage(), ex2);
        }
        return keyFactory.generatePublic(ecPublicKeySpec);
    }
    
    private ECPoint getPublicPoint(final ECDSAPublicKey ecdsaPublicKey) {
        if (!ecdsaPublicKey.hasParameters()) {
            throw new IllegalArgumentException("Public key does not contains EC Params");
        }
        final org.bouncycastle.math.ec.ECPoint.Fp fp = (org.bouncycastle.math.ec.ECPoint.Fp)new ECCurve.Fp(ecdsaPublicKey.getPrimeModulusP(), ecdsaPublicKey.getFirstCoefA(), ecdsaPublicKey.getSecondCoefB(), ecdsaPublicKey.getOrderOfBasePointR(), ecdsaPublicKey.getCofactorF()).decodePoint(ecdsaPublicKey.getPublicPointY());
        return new ECPoint(fp.getAffineXCoord().toBigInteger(), fp.getAffineYCoord().toBigInteger());
    }
    
    private ECParameterSpec getParams(final ECDSAPublicKey ecdsaPublicKey) {
        if (!ecdsaPublicKey.hasParameters()) {
            throw new IllegalArgumentException("Public key does not contains EC Params");
        }
        final ECCurve.Fp fp = new ECCurve.Fp(ecdsaPublicKey.getPrimeModulusP(), ecdsaPublicKey.getFirstCoefA(), ecdsaPublicKey.getSecondCoefB(), ecdsaPublicKey.getOrderOfBasePointR(), ecdsaPublicKey.getCofactorF());
        final org.bouncycastle.math.ec.ECPoint decodePoint = fp.decodePoint(ecdsaPublicKey.getBasePointG());
        return new ECParameterSpec(convertCurve((ECCurve)fp), new ECPoint(decodePoint.getAffineXCoord().toBigInteger(), decodePoint.getAffineYCoord().toBigInteger()), ecdsaPublicKey.getOrderOfBasePointR(), ecdsaPublicKey.getCofactorF().intValue());
    }
    
    public PublicKeyDataObject getPublicKeyDataObject(final ASN1ObjectIdentifier asn1ObjectIdentifier, final PublicKey publicKey) {
        if (publicKey instanceof java.security.interfaces.RSAPublicKey) {
            final java.security.interfaces.RSAPublicKey rsaPublicKey = (java.security.interfaces.RSAPublicKey)publicKey;
            return (PublicKeyDataObject)new RSAPublicKey(asn1ObjectIdentifier, rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());
        }
        final ECPublicKey ecPublicKey = (ECPublicKey)publicKey;
        final ECParameterSpec params = ecPublicKey.getParams();
        return (PublicKeyDataObject)new ECDSAPublicKey(asn1ObjectIdentifier, ((ECFieldFp)params.getCurve().getField()).getP(), params.getCurve().getA(), params.getCurve().getB(), convertPoint(convertCurve(params.getCurve(), params.getOrder(), params.getCofactor()), params.getGenerator()).getEncoded(), params.getOrder(), convertPoint(convertCurve(params.getCurve(), params.getOrder(), params.getCofactor()), ecPublicKey.getW()).getEncoded(), params.getCofactor());
    }
    
    private static org.bouncycastle.math.ec.ECPoint convertPoint(final ECCurve ecCurve, final ECPoint ecPoint) {
        return ecCurve.createPoint(ecPoint.getAffineX(), ecPoint.getAffineY());
    }
    
    private static ECCurve convertCurve(final EllipticCurve ellipticCurve, final BigInteger bigInteger, final int n) {
        final ECField field = ellipticCurve.getField();
        final BigInteger a = ellipticCurve.getA();
        final BigInteger b = ellipticCurve.getB();
        if (field instanceof ECFieldFp) {
            return (ECCurve)new ECCurve.Fp(((ECFieldFp)field).getP(), a, b, bigInteger, BigInteger.valueOf(n));
        }
        throw new IllegalStateException("not implemented yet!!!");
    }
    
    private static EllipticCurve convertCurve(final ECCurve ecCurve) {
        return new EllipticCurve(convertField(ecCurve.getField()), ecCurve.getA().toBigInteger(), ecCurve.getB().toBigInteger(), null);
    }
    
    private static ECField convertField(final FiniteField finiteField) {
        if (ECAlgorithms.isFpField(finiteField)) {
            return new ECFieldFp(finiteField.getCharacteristic());
        }
        final Polynomial minimalPolynomial = ((PolynomialExtensionField)finiteField).getMinimalPolynomial();
        final int[] exponentsPresent = minimalPolynomial.getExponentsPresent();
        return new ECFieldF2m(minimalPolynomial.getDegree(), Arrays.reverse(Arrays.copyOfRange(exponentsPresent, 1, exponentsPresent.length - 1)));
    }
}
