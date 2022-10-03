package com.unboundid.util.ssl.cert;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.asn1.ASN1Set;
import com.unboundid.util.ObjectPair;
import java.util.List;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.asn1.ASN1BitString;
import com.unboundid.asn1.ASN1Element;
import com.unboundid.util.OID;
import java.math.BigInteger;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.InternalUseOnly;

@InternalUseOnly
@ThreadSafety(level = ThreadSafetyLevel.NOT_THREADSAFE)
public final class InternalCertHelper
{
    private InternalCertHelper() {
    }
    
    @InternalUseOnly
    public static X509Certificate createX509Certificate(final X509CertificateVersion version, final BigInteger serialNumber, final OID signatureAlgorithmOID, final ASN1Element signatureAlgorithmParameters, final ASN1BitString signatureValue, final DN issuerDN, final long notBefore, final long notAfter, final DN subjectDN, final OID publicKeyAlgorithmOID, final ASN1Element publicKeyAlgorithmParameters, final ASN1BitString encodedPublicKey, final DecodedPublicKey decodedPublicKey, final ASN1BitString issuerUniqueID, final ASN1BitString subjectUniqueID, final X509CertificateExtension... extensions) throws CertException {
        return new X509Certificate(version, serialNumber, signatureAlgorithmOID, signatureAlgorithmParameters, signatureValue, issuerDN, notBefore, notAfter, subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, decodedPublicKey, issuerUniqueID, subjectUniqueID, extensions);
    }
    
    @InternalUseOnly
    public static PKCS10CertificateSigningRequest createPKCS10CertificateSigningRequest(final PKCS10CertificateSigningRequestVersion version, final OID signatureAlgorithmOID, final ASN1Element signatureAlgorithmParameters, final ASN1BitString signatureValue, final DN subjectDN, final OID publicKeyAlgorithmOID, final ASN1Element publicKeyAlgorithmParameters, final ASN1BitString encodedPublicKey, final DecodedPublicKey decodedPublicKey, final List<ObjectPair<OID, ASN1Set>> nonExtensionAttributes, final X509CertificateExtension... extensions) throws CertException {
        return new PKCS10CertificateSigningRequest(version, signatureAlgorithmOID, signatureAlgorithmParameters, signatureValue, subjectDN, publicKeyAlgorithmOID, publicKeyAlgorithmParameters, encodedPublicKey, decodedPublicKey, nonExtensionAttributes, extensions);
    }
    
    @InternalUseOnly
    public static PKCS8PrivateKey createPKCS8PrivateKey(final PKCS8PrivateKeyVersion version, final OID privateKeyAlgorithmOID, final ASN1Element privateKeyAlgorithmParameters, final ASN1OctetString encodedPrivateKey, final DecodedPrivateKey decodedPrivateKey, final ASN1Element attributesElement, final ASN1BitString publicKey) throws CertException {
        return new PKCS8PrivateKey(version, privateKeyAlgorithmOID, privateKeyAlgorithmParameters, encodedPrivateKey, decodedPrivateKey, attributesElement, publicKey);
    }
}
