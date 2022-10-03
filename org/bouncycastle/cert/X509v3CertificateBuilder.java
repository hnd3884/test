package org.bouncycastle.cert;

import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Integer;
import java.util.Locale;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import java.util.Date;
import java.math.BigInteger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;

public class X509v3CertificateBuilder
{
    private V3TBSCertificateGenerator tbsGen;
    private ExtensionsGenerator extGenerator;
    
    public X509v3CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final X500Name x500Name2, final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date), new Time(date2), x500Name2, subjectPublicKeyInfo);
    }
    
    public X509v3CertificateBuilder(final X500Name x500Name, final BigInteger bigInteger, final Date date, final Date date2, final Locale locale, final X500Name x500Name2, final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this(x500Name, bigInteger, new Time(date, locale), new Time(date2, locale), x500Name2, subjectPublicKeyInfo);
    }
    
    public X509v3CertificateBuilder(final X500Name issuer, final BigInteger bigInteger, final Time startDate, final Time endDate, final X500Name subject, final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        (this.tbsGen = new V3TBSCertificateGenerator()).setSerialNumber(new ASN1Integer(bigInteger));
        this.tbsGen.setIssuer(issuer);
        this.tbsGen.setStartDate(startDate);
        this.tbsGen.setEndDate(endDate);
        this.tbsGen.setSubject(subject);
        this.tbsGen.setSubjectPublicKeyInfo(subjectPublicKeyInfo);
        this.extGenerator = new ExtensionsGenerator();
    }
    
    public X509v3CertificateBuilder setSubjectUniqueID(final boolean[] array) {
        this.tbsGen.setSubjectUniqueID(CertUtils.booleanToBitString(array));
        return this;
    }
    
    public X509v3CertificateBuilder setIssuerUniqueID(final boolean[] array) {
        this.tbsGen.setIssuerUniqueID(CertUtils.booleanToBitString(array));
        return this;
    }
    
    public X509v3CertificateBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final ASN1Encodable asn1Encodable) throws CertIOException {
        CertUtils.addExtension(this.extGenerator, asn1ObjectIdentifier, b, asn1Encodable);
        return this;
    }
    
    public X509v3CertificateBuilder addExtension(final Extension extension) throws CertIOException {
        this.extGenerator.addExtension(extension);
        return this;
    }
    
    public X509v3CertificateBuilder addExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final byte[] array) throws CertIOException {
        this.extGenerator.addExtension(asn1ObjectIdentifier, b, array);
        return this;
    }
    
    public X509v3CertificateBuilder copyAndAddExtension(final ASN1ObjectIdentifier asn1ObjectIdentifier, final boolean b, final X509CertificateHolder x509CertificateHolder) {
        final Extension extension = x509CertificateHolder.toASN1Structure().getTBSCertificate().getExtensions().getExtension(asn1ObjectIdentifier);
        if (extension == null) {
            throw new NullPointerException("extension " + asn1ObjectIdentifier + " not present");
        }
        this.extGenerator.addExtension(asn1ObjectIdentifier, b, extension.getExtnValue().getOctets());
        return this;
    }
    
    public X509CertificateHolder build(final ContentSigner contentSigner) {
        this.tbsGen.setSignature(contentSigner.getAlgorithmIdentifier());
        if (!this.extGenerator.isEmpty()) {
            this.tbsGen.setExtensions(this.extGenerator.generate());
        }
        return CertUtils.generateFullCert(contentSigner, this.tbsGen.generateTBSCertificate());
    }
}
