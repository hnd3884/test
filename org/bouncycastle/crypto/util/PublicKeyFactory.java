package org.bouncycastle.crypto.util;

import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.cryptopro.ECGOST3410NamedCurves;
import org.bouncycastle.asn1.cryptopro.GOST3410PublicKeyAlgParameters;
import org.bouncycastle.crypto.params.ElGamalPublicKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.asn1.ua.DSTU4145BinaryField;
import org.bouncycastle.asn1.ua.DSTU4145ECBinary;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.asn1.ua.DSTU4145PointEncoder;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.asn1.ua.DSTU4145NamedCurves;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.ValidationParams;
import org.bouncycastle.crypto.params.DHValidationParameters;
import org.bouncycastle.asn1.x9.DomainParameters;
import org.bouncycastle.asn1.x9.DHPublicKey;
import org.bouncycastle.crypto.params.DHPublicKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import java.util.HashMap;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.InputStream;
import java.io.IOException;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import java.util.Map;

public class PublicKeyFactory
{
    private static Map converters;
    
    public static AsymmetricKeyParameter createKey(final byte[] array) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(ASN1Primitive.fromByteArray(array)));
    }
    
    public static AsymmetricKeyParameter createKey(final InputStream inputStream) throws IOException {
        return createKey(SubjectPublicKeyInfo.getInstance(new ASN1InputStream(inputStream).readObject()));
    }
    
    public static AsymmetricKeyParameter createKey(final SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        return createKey(subjectPublicKeyInfo, null);
    }
    
    public static AsymmetricKeyParameter createKey(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) throws IOException {
        final AlgorithmIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm();
        final SubjectPublicKeyInfoConverter subjectPublicKeyInfoConverter = PublicKeyFactory.converters.get(algorithm.getAlgorithm());
        if (subjectPublicKeyInfoConverter != null) {
            return subjectPublicKeyInfoConverter.getPublicKeyParameters(subjectPublicKeyInfo, o);
        }
        throw new IOException("algorithm identifier in key not recognised: " + algorithm.getAlgorithm());
    }
    
    static {
        (PublicKeyFactory.converters = new HashMap()).put(PKCSObjectIdentifiers.rsaEncryption, new RSAConverter());
        PublicKeyFactory.converters.put(X509ObjectIdentifiers.id_ea_rsa, new RSAConverter());
        PublicKeyFactory.converters.put(X9ObjectIdentifiers.dhpublicnumber, new DHPublicNumberConverter());
        PublicKeyFactory.converters.put(PKCSObjectIdentifiers.dhKeyAgreement, new DHAgreementConverter());
        PublicKeyFactory.converters.put(X9ObjectIdentifiers.id_dsa, new DSAConverter());
        PublicKeyFactory.converters.put(OIWObjectIdentifiers.dsaWithSHA1, new DSAConverter());
        PublicKeyFactory.converters.put(OIWObjectIdentifiers.elGamalAlgorithm, new ElGamalConverter());
        PublicKeyFactory.converters.put(X9ObjectIdentifiers.id_ecPublicKey, new ECConverter());
        PublicKeyFactory.converters.put(CryptoProObjectIdentifiers.gostR3410_2001, new GOST3410_2001Converter());
        PublicKeyFactory.converters.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, new GOST3410_2012Converter());
        PublicKeyFactory.converters.put(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, new GOST3410_2012Converter());
        PublicKeyFactory.converters.put(UAObjectIdentifiers.dstu4145be, new DSTUConverter());
        PublicKeyFactory.converters.put(UAObjectIdentifiers.dstu4145le, new DSTUConverter());
    }
    
    private static class DHAgreementConverter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) throws IOException {
            final DHParameter instance = DHParameter.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            final ASN1Integer asn1Integer = (ASN1Integer)subjectPublicKeyInfo.parsePublicKey();
            final BigInteger l = instance.getL();
            return new DHPublicKeyParameters(asn1Integer.getValue(), new DHParameters(instance.getP(), instance.getG(), null, (l == null) ? 0 : l.intValue()));
        }
    }
    
    private abstract static class SubjectPublicKeyInfoConverter
    {
        abstract AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo p0, final Object p1) throws IOException;
    }
    
    private static class DHPublicNumberConverter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) throws IOException {
            final BigInteger y = DHPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey()).getY();
            final DomainParameters instance = DomainParameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            final BigInteger p2 = instance.getP();
            final BigInteger g = instance.getG();
            final BigInteger q = instance.getQ();
            BigInteger j = null;
            if (instance.getJ() != null) {
                j = instance.getJ();
            }
            DHValidationParameters dhValidationParameters = null;
            final ValidationParams validationParams = instance.getValidationParams();
            if (validationParams != null) {
                dhValidationParameters = new DHValidationParameters(validationParams.getSeed(), validationParams.getPgenCounter().intValue());
            }
            return new DHPublicKeyParameters(y, new DHParameters(p2, g, q, j, dhValidationParameters));
        }
    }
    
    private static class DSAConverter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) throws IOException {
            final ASN1Integer asn1Integer = (ASN1Integer)subjectPublicKeyInfo.parsePublicKey();
            final ASN1Encodable parameters = subjectPublicKeyInfo.getAlgorithm().getParameters();
            DSAParameters dsaParameters = null;
            if (parameters != null) {
                final DSAParameter instance = DSAParameter.getInstance(parameters.toASN1Primitive());
                dsaParameters = new DSAParameters(instance.getP(), instance.getQ(), instance.getG());
            }
            return new DSAPublicKeyParameters(asn1Integer.getValue(), dsaParameters);
        }
    }
    
    private static class DSTUConverter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) throws IOException {
            final DERBitString publicKeyData = subjectPublicKeyInfo.getPublicKeyData();
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
            final DSTU4145Params instance = DSTU4145Params.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            ECDomainParameters byOID;
            if (instance.isNamedCurve()) {
                byOID = DSTU4145NamedCurves.getByOID(instance.getNamedCurve());
            }
            else {
                final DSTU4145ECBinary ecBinary = instance.getECBinary();
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
                byOID = new ECDomainParameters(f2m, DSTU4145PointEncoder.decodePoint(f2m, g), ecBinary.getN());
            }
            return new ECPublicKeyParameters(DSTU4145PointEncoder.decodePoint(byOID.getCurve(), octets), byOID);
        }
        
        private void reverseBytes(final byte[] array) {
            for (int i = 0; i < array.length / 2; ++i) {
                final byte b = array[i];
                array[i] = array[array.length - 1 - i];
                array[array.length - 1 - i] = b;
            }
        }
    }
    
    private static class ECConverter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) {
            final X962Parameters instance = X962Parameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            Object o2;
            if (instance.isNamedCurve()) {
                final ASN1ObjectIdentifier asn1ObjectIdentifier = (ASN1ObjectIdentifier)instance.getParameters();
                X9ECParameters x9ECParameters = CustomNamedCurves.getByOID(asn1ObjectIdentifier);
                if (x9ECParameters == null) {
                    x9ECParameters = ECNamedCurveTable.getByOID(asn1ObjectIdentifier);
                }
                o2 = new ECNamedDomainParameters(asn1ObjectIdentifier, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
            }
            else if (instance.isImplicitlyCA()) {
                o2 = o;
            }
            else {
                final X9ECParameters instance2 = X9ECParameters.getInstance(instance.getParameters());
                o2 = new ECDomainParameters(instance2.getCurve(), instance2.getG(), instance2.getN(), instance2.getH(), instance2.getSeed());
            }
            final byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
            ASN1OctetString asn1OctetString = new DEROctetString(bytes);
            if (bytes[0] == 4 && bytes[1] == bytes.length - 2 && (bytes[2] == 2 || bytes[2] == 3) && new X9IntegerConverter().getByteLength(((ECDomainParameters)o2).getCurve()) >= bytes.length - 3) {
                try {
                    asn1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(bytes);
                }
                catch (final IOException ex) {
                    throw new IllegalArgumentException("error recovering public key");
                }
            }
            return new ECPublicKeyParameters(new X9ECPoint(((ECDomainParameters)o2).getCurve(), asn1OctetString).getPoint(), (ECDomainParameters)o2);
        }
    }
    
    private static class ElGamalConverter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) throws IOException {
            final ElGamalParameter instance = ElGamalParameter.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            return new ElGamalPublicKeyParameters(((ASN1Integer)subjectPublicKeyInfo.parsePublicKey()).getValue(), new ElGamalParameters(instance.getP(), instance.getG()));
        }
    }
    
    private static class GOST3410_2001Converter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) {
            final DERBitString publicKeyData = subjectPublicKeyInfo.getPublicKeyData();
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
            ASN1ObjectIdentifier asn1ObjectIdentifier;
            if (subjectPublicKeyInfo.getAlgorithm().getParameters() instanceof ASN1ObjectIdentifier) {
                asn1ObjectIdentifier = ASN1ObjectIdentifier.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters());
            }
            else {
                asn1ObjectIdentifier = GOST3410PublicKeyAlgParameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters()).getPublicKeyParamSet();
            }
            final ECDomainParameters byOID = ECGOST3410NamedCurves.getByOID(asn1ObjectIdentifier);
            return new ECPublicKeyParameters(byOID.getCurve().decodePoint(array), byOID);
        }
    }
    
    private static class GOST3410_2012Converter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) {
            final ASN1ObjectIdentifier algorithm = subjectPublicKeyInfo.getAlgorithm().getAlgorithm();
            final DERBitString publicKeyData = subjectPublicKeyInfo.getPublicKeyData();
            ASN1OctetString asn1OctetString;
            try {
                asn1OctetString = (ASN1OctetString)ASN1Primitive.fromByteArray(publicKeyData.getBytes());
            }
            catch (final IOException ex) {
                throw new IllegalArgumentException("error recovering public key");
            }
            final byte[] octets = asn1OctetString.getOctets();
            int n = 32;
            if (algorithm.equals(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512)) {
                n = 64;
            }
            final int n2 = 2 * n;
            final byte[] array = new byte[1 + n2];
            array[0] = 4;
            for (int i = 1; i <= n; ++i) {
                array[i] = octets[n - i];
                array[i + n] = octets[n2 - i];
            }
            final ECDomainParameters byOID = ECGOST3410NamedCurves.getByOID(GOST3410PublicKeyAlgParameters.getInstance(subjectPublicKeyInfo.getAlgorithm().getParameters()).getPublicKeyParamSet());
            return new ECPublicKeyParameters(byOID.getCurve().decodePoint(array), byOID);
        }
    }
    
    private static class RSAConverter extends SubjectPublicKeyInfoConverter
    {
        @Override
        AsymmetricKeyParameter getPublicKeyParameters(final SubjectPublicKeyInfo subjectPublicKeyInfo, final Object o) throws IOException {
            final RSAPublicKey instance = RSAPublicKey.getInstance(subjectPublicKeyInfo.parsePublicKey());
            return new RSAKeyParameters(false, instance.getModulus(), instance.getPublicExponent());
        }
    }
}
