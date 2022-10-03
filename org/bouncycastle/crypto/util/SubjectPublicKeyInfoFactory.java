package org.bouncycastle.crypto.util;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import java.io.IOException;
import org.bouncycastle.asn1.x9.X9ECPoint;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.asn1.ASN1Null;
import org.bouncycastle.asn1.x9.X962Parameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.asn1.pkcs.RSAPublicKey;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SubjectPublicKeyInfoFactory
{
    private SubjectPublicKeyInfoFactory() {
    }
    
    public static SubjectPublicKeyInfo createSubjectPublicKeyInfo(final AsymmetricKeyParameter asymmetricKeyParameter) throws IOException {
        if (asymmetricKeyParameter instanceof RSAKeyParameters) {
            final RSAKeyParameters rsaKeyParameters = (RSAKeyParameters)asymmetricKeyParameter;
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE), new RSAPublicKey(rsaKeyParameters.getModulus(), rsaKeyParameters.getExponent()));
        }
        if (asymmetricKeyParameter instanceof DSAPublicKeyParameters) {
            final DSAPublicKeyParameters dsaPublicKeyParameters = (DSAPublicKeyParameters)asymmetricKeyParameter;
            ASN1Encodable asn1Encodable = null;
            final DSAParameters parameters = dsaPublicKeyParameters.getParameters();
            if (parameters != null) {
                asn1Encodable = new DSAParameter(parameters.getP(), parameters.getQ(), parameters.getG());
            }
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, asn1Encodable), new ASN1Integer(dsaPublicKeyParameters.getY()));
        }
        if (asymmetricKeyParameter instanceof ECPublicKeyParameters) {
            final ECPublicKeyParameters ecPublicKeyParameters = (ECPublicKeyParameters)asymmetricKeyParameter;
            final ECDomainParameters parameters2 = ecPublicKeyParameters.getParameters();
            X962Parameters x962Parameters;
            if (parameters2 == null) {
                x962Parameters = new X962Parameters(DERNull.INSTANCE);
            }
            else if (parameters2 instanceof ECNamedDomainParameters) {
                x962Parameters = new X962Parameters(((ECNamedDomainParameters)parameters2).getName());
            }
            else {
                x962Parameters = new X962Parameters(new X9ECParameters(parameters2.getCurve(), parameters2.getG(), parameters2.getN(), parameters2.getH(), parameters2.getSeed()));
            }
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_ecPublicKey, x962Parameters), ((ASN1OctetString)new X9ECPoint(ecPublicKeyParameters.getQ()).toASN1Primitive()).getOctets());
        }
        throw new IOException("key parameters not recognised.");
    }
}
