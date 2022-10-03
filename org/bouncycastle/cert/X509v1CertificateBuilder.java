package org.bouncycastle.cert;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.ASN1Integer;
import java.util.Locale;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.V1TBSCertificateGenerator;

public class X509v1CertificateBuilder
{
    private V1TBSCertificateGenerator tbsGen;
    
    public X509v1CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name2, final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date), new Time(date2), x500Name2, subjectPublicKeyInfo);
    }
    
    public X509v1CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final Locale locale, final X500Name x500Name2, final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date, locale), new Time(date2, locale), x500Name2, subjectPublicKeyInfo);
    }
    
    public X509v1CertificateBuilder(final X500Name issuer, final BigInteger bigInteger, final Time startDate, final Time endDate, final X500Name subject, final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        if (issuer == null) {
            throw new IllegalArgumentException("issuer must not be null");
        }
        if (subjectPublicKeyInfo == null) {
            throw new IllegalArgumentException("publicKeyInfo must not be null");
        }
        (this.tbsGen = new V1TBSCertificateGenerator()).setSerialNumber(new ASN1Integer(bigInteger));
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setStartDate(startDate);
        this.tbsGen.setEndDate(endDate);
        this.tbsGen.setSubject(subject);
        this.tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);
    }
    
    public X509CertificateHolder build(final ContentSigner contentSigner) {
        this.tbsGen.setSignature(contentSigner.getAlgorithmIdentifier());
        return CertUtils.generateFullCert(contentSigner, this.tbsGen.generateTBSCertificate());
    }
}
