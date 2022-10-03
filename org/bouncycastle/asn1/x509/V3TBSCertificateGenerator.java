package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERTaggedObject;

public class V3TBSCertificateGenerator
{
    DERTaggedObject version;
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time startDate;
    Time endDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    Extensions extensions;
    private boolean altNamePresentAndCritical;
    private DERBitString issuerUniqueID;
    private DERBitString subjectUniqueID;
    
    public V3TBSCertificateGenerator() {
        this.version = new DERTaggedObject(true, 0, new ASN1Integer(2L));
    }
    
    public void setSerialNumber(final ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.signature = signature;
    }
    
    @Deprecated
    public void setIssuer(final X509Name x509Name) {
        this.issuer = X500Name.getInstance(x509Name);
    }
    
    public void setIssuer(final X500Name issuer) {
        this.issuer = issuer;
    }
    
    public void setStartDate(final ASN1UTCTime asn1UTCTime) {
        this.startDate = new Time(asn1UTCTime);
    }
    
    public void setStartDate(final Time startDate) {
        this.startDate = startDate;
    }
    
    public void setEndDate(final ASN1UTCTime asn1UTCTime) {
        this.endDate = new Time(asn1UTCTime);
    }
    
    public void setEndDate(final Time endDate) {
        this.endDate = endDate;
    }
    
    @Deprecated
    public void setSubject(final X509Name x509Name) {
        this.subject = X500Name.getInstance(x509Name.toASN1Primitive());
    }
    
    public void setSubject(final X500Name subject) {
        this.subject = subject;
    }
    
    public void setIssuerUniqueID(final DERBitString issuerUniqueID) {
        this.issuerUniqueID = issuerUniqueID;
    }
    
    public void setSubjectUniqueID(final DERBitString subjectUniqueID) {
        this.subjectUniqueID = subjectUniqueID;
    }
    
    public void setSubjectPublicKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
    }
    
    @Deprecated
    public void setExtensions(final X509Extensions x509Extensions) {
        this.setExtensions(Extensions.getInstance(x509Extensions));
    }
    
    public void setExtensions(final Extensions extensions) {
        this.extensions = extensions;
        if (extensions != null) {
            final Extension extension = extensions.getExtension(Extension.subjectAlternativeName);
            if (extension != null && extension.isCritical()) {
                this.altNamePresentAndCritical = true;
            }
        }
    }
    
    public TBSCertificate generateTBSCertificate() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || (this.subject == null && !this.altNamePresentAndCritical) || this.subjectPublicKeyInfo == null) {
            throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.serialNumber);
        asn1EncodableVector.add(this.signature);
        asn1EncodableVector.add(this.issuer);
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        asn1EncodableVector2.add(this.startDate);
        asn1EncodableVector2.add(this.endDate);
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        if (this.subject != null) {
            asn1EncodableVector.add(this.subject);
        }
        else {
            asn1EncodableVector.add(new DERSequence());
        }
        asn1EncodableVector.add(this.subjectPublicKeyInfo);
        if (this.issuerUniqueID != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.issuerUniqueID));
        }
        if (this.subjectUniqueID != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 2, this.subjectUniqueID));
        }
        if (this.extensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(true, 3, this.extensions));
        }
        return TBSCertificate.getInstance(new DERSequence(asn1EncodableVector));
    }
}
