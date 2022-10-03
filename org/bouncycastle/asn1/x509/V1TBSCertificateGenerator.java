package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERTaggedObject;

public class V1TBSCertificateGenerator
{
    DERTaggedObject version;
    ASN1Integer serialNumber;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time startDate;
    Time endDate;
    X500Name subject;
    SubjectPublicKeyInfo subjectPublicKeyInfo;
    
    public V1TBSCertificateGenerator() {
        this.version = new DERTaggedObject(true, 0, new ASN1Integer(0L));
    }
    
    public void setSerialNumber(final ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void setSignature(final AlgorithmIdentifier signature) {
        this.signature = signature;
    }
    
    @Deprecated
    public void setIssuer(final X509Name x509Name) {
        this.issuer = X500Name.getInstance(x509Name.toASN1Primitive());
    }
    
    public void setIssuer(final X500Name issuer) {
        this.issuer = issuer;
    }
    
    public void setStartDate(final Time startDate) {
        this.startDate = startDate;
    }
    
    public void setStartDate(final ASN1UTCTime asn1UTCTime) {
        this.startDate = new Time(asn1UTCTime);
    }
    
    public void setEndDate(final Time endDate) {
        this.endDate = endDate;
    }
    
    public void setEndDate(final ASN1UTCTime asn1UTCTime) {
        this.endDate = new Time(asn1UTCTime);
    }
    
    @Deprecated
    public void setSubject(final X509Name x509Name) {
        this.subject = X500Name.getInstance(x509Name.toASN1Primitive());
    }
    
    public void setSubject(final X500Name subject) {
        this.subject = subject;
    }
    
    public void setSubjectPublicKeyInfo(final SubjectPublicKeyInfo subjectPublicKeyInfo) {
        this.subjectPublicKeyInfo = subjectPublicKeyInfo;
    }
    
    public TBSCertificate generateTBSCertificate() {
        if (this.serialNumber == null || this.signature == null || this.issuer == null || this.startDate == null || this.endDate == null || this.subject == null || this.subjectPublicKeyInfo == null) {
            throw new IllegalStateException("not all mandatory fields set in V1 TBScertificate generator");
        }
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.serialNumber);
        asn1EncodableVector.add(this.signature);
        asn1EncodableVector.add(this.issuer);
        final ASN1EncodableVector asn1EncodableVector2 = new ASN1EncodableVector();
        asn1EncodableVector2.add(this.startDate);
        asn1EncodableVector2.add(this.endDate);
        asn1EncodableVector.add(new DERSequence(asn1EncodableVector2));
        asn1EncodableVector.add(this.subject);
        asn1EncodableVector.add(this.subjectPublicKeyInfo);
        return TBSCertificate.getInstance(new DERSequence(asn1EncodableVector));
    }
}
