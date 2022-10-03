package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Object;

public class OriginatorIdentifierOrKey extends ASN1Object implements ASN1Choice
{
    private ASN1Encodable id;
    
    public OriginatorIdentifierOrKey(final IssuerAndSerialNumber id) {
        this.id = id;
    }
    
    @Deprecated
    public OriginatorIdentifierOrKey(final ASN1OctetString asn1OctetString) {
        this(new SubjectKeyIdentifier(asn1OctetString.getOctets()));
    }
    
    public OriginatorIdentifierOrKey(final SubjectKeyIdentifier subjectKeyIdentifier) {
        this.id = new DERTaggedObject(false, 0, subjectKeyIdentifier);
    }
    
    public OriginatorIdentifierOrKey(final OriginatorPublicKey originatorPublicKey) {
        this.id = new DERTaggedObject(false, 1, originatorPublicKey);
    }
    
    @Deprecated
    public OriginatorIdentifierOrKey(final ASN1Primitive id) {
        this.id = id;
    }
    
    public static OriginatorIdentifierOrKey getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        if (!b) {
            throw new IllegalArgumentException("Can't implicitly tag OriginatorIdentifierOrKey");
        }
        return getInstance(asn1TaggedObject.getObject());
    }
    
    public static OriginatorIdentifierOrKey getInstance(final Object o) {
        if (o == null || o instanceof OriginatorIdentifierOrKey) {
            return (OriginatorIdentifierOrKey)o;
        }
        if (o instanceof IssuerAndSerialNumber || o instanceof ASN1Sequence) {
            return new OriginatorIdentifierOrKey(IssuerAndSerialNumber.getInstance(o));
        }
        if (o instanceof ASN1TaggedObject) {
            final ASN1TaggedObject asn1TaggedObject = (ASN1TaggedObject)o;
            if (asn1TaggedObject.getTagNo() == 0) {
                return new OriginatorIdentifierOrKey(SubjectKeyIdentifier.getInstance(asn1TaggedObject, false));
            }
            if (asn1TaggedObject.getTagNo() == 1) {
                return new OriginatorIdentifierOrKey(OriginatorPublicKey.getInstance(asn1TaggedObject, false));
            }
        }
        throw new IllegalArgumentException("Invalid OriginatorIdentifierOrKey: " + o.getClass().getName());
    }
    
    public ASN1Encodable getId() {
        return this.id;
    }
    
    public IssuerAndSerialNumber getIssuerAndSerialNumber() {
        if (this.id instanceof IssuerAndSerialNumber) {
            return (IssuerAndSerialNumber)this.id;
        }
        return null;
    }
    
    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        if (this.id instanceof ASN1TaggedObject && ((ASN1TaggedObject)this.id).getTagNo() == 0) {
            return SubjectKeyIdentifier.getInstance((ASN1TaggedObject)this.id, false);
        }
        return null;
    }
    
    public OriginatorPublicKey getOriginatorKey() {
        if (this.id instanceof ASN1TaggedObject && ((ASN1TaggedObject)this.id).getTagNo() == 1) {
            return OriginatorPublicKey.getInstance((ASN1TaggedObject)this.id, false);
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.id.toASN1Primitive();
    }
}
