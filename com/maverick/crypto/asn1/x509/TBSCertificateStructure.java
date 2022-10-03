package com.maverick.crypto.asn1.x509;

import com.maverick.crypto.asn1.DERObject;
import com.maverick.crypto.asn1.DERTaggedObject;
import com.maverick.crypto.asn1.ASN1TaggedObject;
import com.maverick.crypto.asn1.DERBitString;
import com.maverick.crypto.asn1.DERInteger;
import com.maverick.crypto.asn1.ASN1Sequence;
import com.maverick.crypto.asn1.pkcs.PKCSObjectIdentifiers;
import com.maverick.crypto.asn1.DEREncodable;

public class TBSCertificateStructure implements DEREncodable, X509ObjectIdentifiers, PKCSObjectIdentifiers
{
    ASN1Sequence o;
    DERInteger l;
    DERInteger h;
    AlgorithmIdentifier g;
    X509Name k;
    Time f;
    Time j;
    X509Name n;
    SubjectPublicKeyInfo d;
    DERBitString e;
    DERBitString i;
    X509Extensions m;
    
    public static TBSCertificateStructure getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static TBSCertificateStructure getInstance(final Object o) {
        if (o instanceof TBSCertificateStructure) {
            return (TBSCertificateStructure)o;
        }
        if (o instanceof ASN1Sequence) {
            return new TBSCertificateStructure((ASN1Sequence)o);
        }
        throw new IllegalArgumentException("unknown object in factory");
    }
    
    public TBSCertificateStructure(final ASN1Sequence o) {
        int n = 0;
        this.o = o;
        if (o.getObjectAt(0) instanceof DERTaggedObject) {
            this.l = DERInteger.getInstance(o.getObjectAt(0));
        }
        else {
            n = -1;
            this.l = new DERInteger(0);
        }
        this.h = DERInteger.getInstance(o.getObjectAt(n + 1));
        this.g = AlgorithmIdentifier.getInstance(o.getObjectAt(n + 2));
        this.k = X509Name.getInstance(o.getObjectAt(n + 3));
        final ASN1Sequence asn1Sequence = (ASN1Sequence)o.getObjectAt(n + 4);
        this.f = Time.getInstance(asn1Sequence.getObjectAt(0));
        this.j = Time.getInstance(asn1Sequence.getObjectAt(1));
        this.n = X509Name.getInstance(o.getObjectAt(n + 5));
        this.d = SubjectPublicKeyInfo.getInstance(o.getObjectAt(n + 6));
        for (int i = o.size() - (n + 6) - 1; i > 0; --i) {
            final DERTaggedObject derTaggedObject = (DERTaggedObject)o.getObjectAt(n + 6 + i);
            switch (derTaggedObject.getTagNo()) {
                case 1: {
                    this.e = DERBitString.getInstance(derTaggedObject);
                    break;
                }
                case 2: {
                    this.i = DERBitString.getInstance(derTaggedObject);
                    break;
                }
                case 3: {
                    this.m = X509Extensions.getInstance(derTaggedObject);
                    break;
                }
            }
        }
    }
    
    public int getVersion() {
        return this.l.getValue().intValue() + 1;
    }
    
    public DERInteger getVersionNumber() {
        return this.l;
    }
    
    public DERInteger getSerialNumber() {
        return this.h;
    }
    
    public AlgorithmIdentifier getSignature() {
        return this.g;
    }
    
    public X509Name getIssuer() {
        return this.k;
    }
    
    public Time getStartDate() {
        return this.f;
    }
    
    public Time getEndDate() {
        return this.j;
    }
    
    public X509Name getSubject() {
        return this.n;
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.d;
    }
    
    public DERBitString getIssuerUniqueId() {
        return this.e;
    }
    
    public DERBitString getSubjectUniqueId() {
        return this.i;
    }
    
    public X509Extensions getExtensions() {
        return this.m;
    }
    
    public DERObject getDERObject() {
        return this.o;
    }
}
