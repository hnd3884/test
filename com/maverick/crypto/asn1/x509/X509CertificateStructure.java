package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DERObject;
import com.maverick.crypto.asn1.DERInteger;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.DERBitString;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.pkcs.PKCSObjectIdentifiers;
import com.maverick.crypto.asn1.DEREncodable;

public class X509CertificateStructure implements DEREncodable, X509ObjectIdentifiers, PKCSObjectIdentifiers
{
    ASN1Sequence p;
    TBSCertificateStructure s;
    AlgorithmIdentifier q;
    DERBitString r;
    
    public static X509CertificateStructure getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static X509CertificateStructure getInstance(final Object o) {
        if (o instanceof X509CertificateStructure) {
            return (X509CertificateStructure)o;
        }
        if (o instanceof ASN1Sequence) {
            return new X509CertificateStructure((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("unknown object in factory");
    }
    
    public X509CertificateStructure(final ASN1Sequence p) {
        this.p = p;
        if (p.size() == 3) {
            this.s = TBSCertificateStructure.getInstance(p.getObjectAt(0));
            this.q = AlgorithmIdentifier.getInstance(p.getObjectAt(1));
            this.r = (DERBitString)p.getObjectAt(2);
        }
    }
    
    public TBSCertificateStructure getTBSCertificate() {
        return this.s;
    }
    
    public int getVersion() {
        return this.s.getVersion();
    }
    
    public DERInteger getSerialNumber() {
        return this.s.getSerialNumber();
    }
    
    public X509Name getIssuer() {
        return this.s.getIssuer();
    }
    
    public Time getStartDate() {
        return this.s.getStartDate();
    }
    
    public Time getEndDate() {
        return this.s.getEndDate();
    }
    
    public X509Name getSubject() {
        return this.s.getSubject();
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.s.getSubjectPublicKeyInfo();
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.q;
    }
    
    public DERBitString getSignature() {
        return this.r;
    }
    
    public DERObject getDERObject() {
        return this.p;
    }
}
