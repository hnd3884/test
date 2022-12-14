package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class Certificate extends ASN1Object
{
    ASN1Sequence seq;
    TBSCertificate tbsCert;
    AlgorithmIdentifier sigAlgId;
    DERBitString sig;
    
    public static Certificate getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static Certificate getInstance(final Object o) {
        if (o instanceof Certificate) {
            return (Certificate)o;
        }
        if (o != null) {
            return new Certificate(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private Certificate(final ASN1Sequence seq) {
        this.seq = seq;
        if (seq.size() == 3) {
            this.tbsCert = TBSCertificate.getInstance(seq.getObjectAt(0));
            this.sigAlgId = AlgorithmIdentifier.getInstance(seq.getObjectAt(1));
            this.sig = DERBitString.getInstance(seq.getObjectAt(2));
            return;
        }
        throw new IllegalArgumentException("sequence wrong size for a certificate");
    }
    
    public TBSCertificate getTBSCertificate() {
        return this.tbsCert;
    }
    
    public ASN1Integer getVersion() {
        return this.tbsCert.getVersion();
    }
    
    public int getVersionNumber() {
        return this.tbsCert.getVersionNumber();
    }
    
    public ASN1Integer getSerialNumber() {
        return this.tbsCert.getSerialNumber();
    }
    
    public X500Name getIssuer() {
        return this.tbsCert.getIssuer();
    }
    
    public Time getStartDate() {
        return this.tbsCert.getStartDate();
    }
    
    public Time getEndDate() {
        return this.tbsCert.getEndDate();
    }
    
    public X500Name getSubject() {
        return this.tbsCert.getSubject();
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.tbsCert.getSubjectPublicKeyInfo();
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.sigAlgId;
    }
    
    public DERBitString getSignature() {
        return this.sig;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.seq;
    }
}
