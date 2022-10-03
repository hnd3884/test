package org.bouncycastle.jce;

import java.security.cert.CRLException;
import org.bouncycastle.asn1.x509.TBSCertList;
import java.security.cert.X509CRL;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.x509.TBSCertificateStructure;
import org.bouncycastle.asn1.ASN1Primitive;
import java.security.cert.X509Certificate;

public class PrincipalUtil
{
    public static X509Principal getIssuerX509Principal(final X509Certificate x509Certificate) throws CertificateEncodingException {
        try {
            return new X509Principal(X509Name.getInstance(TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(x509Certificate.getTBSCertificate())).getIssuer()));
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
    }
    
    public static X509Principal getSubjectX509Principal(final X509Certificate x509Certificate) throws CertificateEncodingException {
        try {
            return new X509Principal(X509Name.getInstance(TBSCertificateStructure.getInstance(ASN1Primitive.fromByteArray(x509Certificate.getTBSCertificate())).getSubject()));
        }
        catch (final IOException ex) {
            throw new CertificateEncodingException(ex.toString());
        }
    }
    
    public static X509Principal getIssuerX509Principal(final X509CRL x509CRL) throws CRLException {
        try {
            return new X509Principal(X509Name.getInstance(TBSCertList.getInstance(ASN1Primitive.fromByteArray(x509CRL.getTBSCertList())).getIssuer()));
        }
        catch (final IOException ex) {
            throw new CRLException(ex.toString());
        }
    }
}
