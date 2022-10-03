package org.bouncycastle.asn1.x509;

import java.util.NoSuchElementException;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class TBSCertList extends ASN1Object
{
    ASN1Integer version;
    AlgorithmIdentifier signature;
    X500Name issuer;
    Time thisUpdate;
    Time nextUpdate;
    ASN1Sequence revokedCertificates;
    Extensions crlExtensions;
    
    public static TBSCertList getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static TBSCertList getInstance(final Object o) {
        if (o instanceof TBSCertList) {
            return (TBSCertList)o;
        }
        if (o != null) {
            return new TBSCertList(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public TBSCertList(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 3 || asn1Sequence.size() > 7) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        int n = 0;
        if (asn1Sequence.getObjectAt(n) instanceof ASN1Integer) {
            this.version = ASN1Integer.getInstance(asn1Sequence.getObjectAt(n++));
        }
        else {
            this.version = null;
        }
        this.signature = AlgorithmIdentifier.getInstance(asn1Sequence.getObjectAt(n++));
        this.issuer = X500Name.getInstance(asn1Sequence.getObjectAt(n++));
        this.thisUpdate = Time.getInstance(asn1Sequence.getObjectAt(n++));
        if (n < asn1Sequence.size() && (asn1Sequence.getObjectAt(n) instanceof ASN1UTCTime || asn1Sequence.getObjectAt(n) instanceof ASN1GeneralizedTime || asn1Sequence.getObjectAt(n) instanceof Time)) {
            this.nextUpdate = Time.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (n < asn1Sequence.size() && !(asn1Sequence.getObjectAt(n) instanceof ASN1TaggedObject)) {
            this.revokedCertificates = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(n++));
        }
        if (n < asn1Sequence.size() && asn1Sequence.getObjectAt(n) instanceof ASN1TaggedObject) {
            this.crlExtensions = Extensions.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(n), true));
        }
    }
    
    public int getVersionNumber() {
        if (this.version == null) {
            return 1;
        }
        return this.version.getValue().intValue() + 1;
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public AlgorithmIdentifier getSignature() {
        return this.signature;
    }
    
    public X500Name getIssuer() {
        return this.issuer;
    }
    
    public Time getThisUpdate() {
        return this.thisUpdate;
    }
    
    public Time getNextUpdate() {
        return this.nextUpdate;
    }
    
    public CRLEntry[] getRevokedCertificates() {
        if (this.revokedCertificates == null) {
            return new CRLEntry[0];
        }
        final CRLEntry[] array = new CRLEntry[this.revokedCertificates.size()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = CRLEntry.getInstance(this.revokedCertificates.getObjectAt(i));
        }
        return array;
    }
    
    public Enumeration getRevokedCertificateEnumeration() {
        if (this.revokedCertificates == null) {
            return new EmptyEnumeration();
        }
        return new RevokedCertificatesEnumeration(this.revokedCertificates.getObjects());
    }
    
    public Extensions getExtensions() {
        return this.crlExtensions;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.version != null) {
            asn1EncodableVector.add(this.version);
        }
        asn1EncodableVector.add(this.signature);
        asn1EncodableVector.add(this.issuer);
        asn1EncodableVector.add(this.thisUpdate);
        if (this.nextUpdate != null) {
            asn1EncodableVector.add(this.nextUpdate);
        }
        if (this.revokedCertificates != null) {
            asn1EncodableVector.add(this.revokedCertificates);
        }
        if (this.crlExtensions != null) {
            asn1EncodableVector.add(new DERTaggedObject(0, this.crlExtensions));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public static class CRLEntry extends ASN1Object
    {
        ASN1Sequence seq;
        Extensions crlEntryExtensions;
        
        private CRLEntry(final ASN1Sequence seq) {
            if (seq.size() < 2 || seq.size() > 3) {
                throw new IllegalArgumentException("Bad sequence size: " + seq.size());
            }
            this.seq = seq;
        }
        
        public static CRLEntry getInstance(final Object o) {
            if (o instanceof CRLEntry) {
                return (CRLEntry)o;
            }
            if (o != null) {
                return new CRLEntry(ASN1Sequence.getInstance(o));
            }
            return null;
        }
        
        public ASN1Integer getUserCertificate() {
            return ASN1Integer.getInstance(this.seq.getObjectAt(0));
        }
        
        public Time getRevocationDate() {
            return Time.getInstance(this.seq.getObjectAt(1));
        }
        
        public Extensions getExtensions() {
            if (this.crlEntryExtensions == null && this.seq.size() == 3) {
                this.crlEntryExtensions = Extensions.getInstance(this.seq.getObjectAt(2));
            }
            return this.crlEntryExtensions;
        }
        
        @Override
        public ASN1Primitive toASN1Primitive() {
            return this.seq;
        }
        
        public boolean hasExtensions() {
            return this.seq.size() == 3;
        }
    }
    
    private class EmptyEnumeration implements Enumeration
    {
        public boolean hasMoreElements() {
            return false;
        }
        
        public Object nextElement() {
            throw new NoSuchElementException("Empty Enumeration");
        }
    }
    
    private class RevokedCertificatesEnumeration implements Enumeration
    {
        private final Enumeration en;
        
        RevokedCertificatesEnumeration(final Enumeration en) {
            this.en = en;
        }
        
        public boolean hasMoreElements() {
            return this.en.hasMoreElements();
        }
        
        public Object nextElement() {
            return CRLEntry.getInstance(this.en.nextElement());
        }
    }
}
