package org.bouncycastle.crypto.util;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.asn1.sec.ECPrivateKey;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.crypto.params.DSAPrivateKeyParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.ElGamalPrivateKeyParameters;
import org.bouncycastle.crypto.params.ElGamalParameters;
import org.bouncycastle.asn1.oiw.ElGamalParameter;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.crypto.params.DHPrivateKeyParameters;
import java.math.BigInteger;
import org.bouncycastle.crypto.params.DHParameters;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.ASN1InputStream;
import java.io.InputStream;
import java.io.IOException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class PrivateKeyFactory
{
    public static AsymmetricKeyParameter createKey(final byte[] array) throws IOException {
        return createKey(PrivateKeyInfo.getInstance(ASN1Primitive.fromByteArray(array)));
    }
    
    public static AsymmetricKeyParameter createKey(final InputStream inputStream) throws IOException {
        return createKey(PrivateKeyInfo.getInstance(new ASN1InputStream(inputStream).readObject()));
    }
    
    public static AsymmetricKeyParameter createKey(final PrivateKeyInfo privateKeyInfo) throws IOException {
        final AlgorithmIdentifier privateKeyAlgorithm = privateKeyInfo.getPrivateKeyAlgorithm();
        if (privateKeyAlgorithm.getAlgorithm().equals(PKCSObjectIdentifiers.rsaEncryption)) {
            final RSAPrivateKey instance = RSAPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
            return new RSAPrivateCrtKeyParameters(instance.getModulus(), instance.getPublicExponent(), instance.getPrivateExponent(), instance.getPrime1(), instance.getPrime2(), instance.getExponent1(), instance.getExponent2(), instance.getCoefficient());
        }
        if (privateKeyAlgorithm.getAlgorithm().equals(PKCSObjectIdentifiers.dhKeyAgreement)) {
            final DHParameter instance2 = DHParameter.getInstance(privateKeyAlgorithm.getParameters());
            final ASN1Integer asn1Integer = (ASN1Integer)privateKeyInfo.parsePrivateKey();
            final BigInteger l = instance2.getL();
            return new DHPrivateKeyParameters(asn1Integer.getValue(), new DHParameters(instance2.getP(), instance2.getG(), null, (l == null) ? 0 : l.intValue()));
        }
        if (privateKeyAlgorithm.getAlgorithm().equals(OIWObjectIdentifiers.elGamalAlgorithm)) {
            final ElGamalParameter instance3 = ElGamalParameter.getInstance(privateKeyAlgorithm.getParameters());
            return new ElGamalPrivateKeyParameters(((ASN1Integer)privateKeyInfo.parsePrivateKey()).getValue(), new ElGamalParameters(instance3.getP(), instance3.getG()));
        }
        if (privateKeyAlgorithm.getAlgorithm().equals(X9ObjectIdentifiers.id_dsa)) {
            final ASN1Integer asn1Integer2 = (ASN1Integer)privateKeyInfo.parsePrivateKey();
            final ASN1Encodable parameters = privateKeyAlgorithm.getParameters();
            DSAParameters dsaParameters = null;
            if (parameters != null) {
                final DSAParameter instance4 = DSAParameter.getInstance(parameters.toASN1Primitive());
                dsaParameters = new DSAParameters(instance4.getP(), instance4.getQ(), instance4.getG());
            }
            return new DSAPrivateKeyParameters(asn1Integer2.getValue(), dsaParameters);
        }
        if (privateKeyAlgorithm.getAlgorithm().equals(X9ObjectIdentifiers.id_ecPublicKey)) {
            final X962Parameters x962Parameters = new X962Parameters((ASN1Primitive)privateKeyAlgorithm.getParameters());
            ECDomainParameters ecDomainParameters;
            if (x962Parameters.isNamedCurve()) {
                final ASN1ObjectIdentifier asn1ObjectIdentifier = (ASN1ObjectIdentifier)x962Parameters.getParameters();
                X9ECParameters x9ECParameters = CustomNamedCurves.getByOID(asn1ObjectIdentifier);
                if (x9ECParameters == null) {
                    x9ECParameters = ECNamedCurveTable.getByOID(asn1ObjectIdentifier);
                }
                ecDomainParameters = new ECNamedDomainParameters(asn1ObjectIdentifier, x9ECParameters.getCurve(), x9ECParameters.getG(), x9ECParameters.getN(), x9ECParameters.getH(), x9ECParameters.getSeed());
            }
            else {
                final X9ECParameters instance5 = X9ECParameters.getInstance(x962Parameters.getParameters());
                ecDomainParameters = new ECDomainParameters(instance5.getCurve(), instance5.getG(), instance5.getN(), instance5.getH(), instance5.getSeed());
            }
            return new ECPrivateKeyParameters(ECPrivateKey.getInstance(privateKeyInfo.parsePrivateKey()).getKey(), ecDomainParameters);
        }
        throw new RuntimeException("algorithm identifier in key not recognised");
    }
}
