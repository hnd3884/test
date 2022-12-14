package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.ASN1Integer;

public class CertTemplateBuilder
{
    private ASN1Integer version;
    private ASN1Integer serialNumber;
    private AlgorithmIdentifier signingAlg;
    private X500Name issuer;
    private OptionalValidity validity;
    private X500Name subject;
    private SubjectPublicKeyInfo publicKey;
    private DERBitString issuerUID;
    private DERBitString subjectUID;
    private Extensions extensions;
    
    public CertTemplateBuilder setVersion(final int n) {
        this.version = new ASN1Integer(n);
        return this;
    }
    
    public CertTemplateBuilder setSerialNumber(final ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
        return this;
    }
    
    public CertTemplateBuilder setSigningAlg(final AlgorithmIdentifier signingAlg) {
        this.signingAlg = signingAlg;
        return this;
    }
    
    public CertTemplateBuilder setIssuer(final X500Name issuer) {
        this.issuer = issuer;
        return this;
    }
    
    public CertTemplateBuilder setValidity(final OptionalValidity validity) {
        this.validity = validity;
        return this;
    }
    
    public CertTemplateBuilder setSubject(final X500Name subject) {
        this.subject = subject;
        return this;
    }
    
    public CertTemplateBuilder setPublicKey(final SubjectPublicKeyInfo publicKey) {
        this.publicKey = publicKey;
        return this;
    }
    
    public CertTemplateBuilder setIssuerUID(final DERBitString issuerUID) {
        this.issuerUID = issuerUID;
        return this;
    }
    
    public CertTemplateBuilder setSubjectUID(final DERBitString subjectUID) {
        this.subjectUID = subjectUID;
        return this;
    }
    
    @Deprecated
    public CertTemplateBuilder setExtensions(final X509Extensions x509Extensions) {
        return this.setExtensions(Extensions.getInstance(x509Extensions));
    }
    
    public CertTemplateBuilder setExtensions(final Extensions extensions) {
        this.extensions = extensions;
        return this;
    }
    
    public CertTemplate build() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        this.addOptional(asn1EncodableVector, 0, false, this.version);
        this.addOptional(asn1EncodableVector, 1, false, this.serialNumber);
        this.addOptional(asn1EncodableVector, 2, false, this.signingAlg);
        this.addOptional(asn1EncodableVector, 3, true, this.issuer);
        this.addOptional(asn1EncodableVector, 4, false, this.validity);
        this.addOptional(asn1EncodableVector, 5, true, this.subject);
        this.addOptional(asn1EncodableVector, 6, false, this.publicKey);
        this.addOptional(asn1EncodableVector, 7, false, this.issuerUID);
        this.addOptional(asn1EncodableVector, 8, false, this.subjectUID);
        this.addOptional(asn1EncodableVector, 9, false, this.extensions);
        return CertTemplate.getInstance(new DERSequence(asn1EncodableVector));
    }
    
    private void addOptional(final ASN1EncodableVector asn1EncodableVector, final int n, final boolean b, final ASN1Encodable asn1Encodable) {
        if (asn1Encodable != null) {
            asn1EncodableVector.add(new DERTaggedObject(b, n, asn1Encodable));
        }
    }
}
