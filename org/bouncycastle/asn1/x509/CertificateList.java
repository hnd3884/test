package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Object;

public class CertificateList extends ASN1Object
{
    TBSCertList tbsCertList;
    AlgorithmIdentifier sigAlgId;
    DERBitString sig;
    boolean isHashCodeSet;
    int hashCodeValue;
    
    public static CertificateList getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static CertificateList getInstance(final Object o) {
        if (o instanceof CertificateList) {
            return (CertificateList)o;
        }
        if (o != null) {
            return new CertificateList(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Deprecated
    public CertificateList(final ASN1Sequence asn1Sequence) {
        this.isHashCodeSet = false;
        if (asn1Sequence.size() == 3) {
            this.tbsCertList = TBSCertList.getInstance(asn1Sequence.getObjectAt(0));
            this.sigAlgId = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(1));
            this.sig = DERBitString.getInstance(asn1Sequence.getObjectAt(2));
            return;
        }
        throw new IllegalArgumentException("sequence wrong size for CertificateList");
    }
    
    public TBSCertList getTBSCertList() {
        return this.tbsCertList;
    }
    
    public TBSCertList.CRLEntry[] getRevokedCertificates() {
        return this.tbsCertList.getRevokedCertificates();
    }
    
    public Enumeration getRevokedCertificateEnumeration() {
        return this.tbsCertList.getRevokedCertificateEnumeration();
    }
    
    public AlgorithmIdentifier getSignatureAlgorithm() {
        return this.sigAlgId;
    }
    
    public DERBitString getSignature() {
        return this.sig;
    }
    
    public int getVersionNumber() {
        return this.tbsCertList.getVersionNumber();
    }
    
    public X500Name getIssuer() {
        return this.tbsCertList.getIssuer();
    }
    
    public Time getThisUpdate() {
        return this.tbsCertList.getThisUpdate();
    }
    
    public Time getNextUpdate() {
        return this.tbsCertList.getNextUpdate();
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.tbsCertList);
        asn1EncodableVector.add(this.sigAlgId);
        asn1EncodableVector.add(this.sig);
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public int hashCode() {
        if (!this.isHashCodeSet) {
            this.hashCodeValue = super.hashCode();
            this.isHashCodeSet = true;
        }
        return this.hashCodeValue;
    }
}
