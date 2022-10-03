package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class CertificationRequestInfo extends ASN1Object
{
    ASN1Integer version;
    X500Name subject;
    SubjectPublicKeyInfo subjectPKInfo;
    ASN1Set attributes;
    
    public static CertificationRequestInfo getInstance(final Object o) {
        if (o instanceof CertificationRequestInfo) {
            return (CertificationRequestInfo)o;
        }
        if (o != null) {
            return new CertificationRequestInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CertificationRequestInfo(final X500Name subject, final SubjectPublicKeyInfo subjectPKInfo, final ASN1Set attributes) {
        this.version = new ASN1Integer(0L);
        this.attributes = null;
        if (subject == null || subjectPKInfo == null) {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
        validateAttributes(attributes);
        this.subject = subject;
        this.subjectPKInfo = subjectPKInfo;
        this.attributes = attributes;
    }
    
    @Deprecated
    public CertificationRequestInfo(final X509Name x509Name, final SubjectPublicKeyInfo subjectPublicKeyInfo, final ASN1Set set) {
        this(X500Name.getInstance(x509Name.toASN1Primitive()), subjectPublicKeyInfo, set);
    }
    
    @Deprecated
    public CertificationRequestInfo(final ASN1Sequence asn1Sequence) {
        this.version = new ASN1Integer(0L);
        this.attributes = null;
        this.version = (ASN1Integer)asn1Sequence.getObjectAt(0);
        this.subject = X500Name.getInstance(asn1Sequence.getObjectAt(1));
        this.subjectPKInfo = SubjectPublicKeyInfo.getInstance(asn1Sequence.getObjectAt(2));
        if (asn1Sequence.size() > 3) {
            this.attributes = ASN1Set.getInstance((ASN1TaggedObject)asn1Sequence.getObjectAt(3), false);
        }
        validateAttributes(this.attributes);
        if (this.subject == null || this.version == null || this.subjectPKInfo == null) {
            throw new IllegalArgumentException("Not all mandatory fields set in CertificationRequestInfo generator.");
        }
    }
    
    public ASN1Integer getVersion() {
        return this.version;
    }
    
    public X500Name getSubject() {
        return this.subject;
    }
    
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return this.subjectPKInfo;
    }
    
    public ASN1Set getAttributes() {
        return this.attributes;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.version);
        asn1EncodableVector.add(this.subject);
        asn1EncodableVector.add(this.subjectPKInfo);
        if (this.attributes != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.attributes));
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    private static void validateAttributes(final ASN1Set set) {
        if (set == null) {
            return;
        }
        final Enumeration objects = set.getObjects();
        while (objects.hasMoreElements()) {
            final Attribute instance = Attribute.getInstance(objects.nextElement());
            if (instance.getAttrType().equals(PKCSObjectIdentifiers.pkcs_9_at_challengePassword) && instance.getAttrValues().size() != 1) {
                throw new IllegalArgumentException("challengePassword attribute must have one value");
            }
        }
    }
}
