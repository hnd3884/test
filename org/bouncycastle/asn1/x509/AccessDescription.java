package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Object;

public class AccessDescription extends ASN1Object
{
    public static final ASN1ObjectIdentifier id_ad_caIssuers;
    public static final ASN1ObjectIdentifier id_ad_ocsp;
    ASN1ObjectIdentifier accessMethod;
    GeneralName accessLocation;
    
    public static AccessDescription getInstance(final Object o) {
        if (o instanceof AccessDescription) {
            return (AccessDescription)o;
        }
        if (o != null) {
            return new AccessDescription(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private AccessDescription(final ASN1Sequence asn1Sequence) {
        this.accessMethod = null;
        this.accessLocation = null;
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("wrong number of elements in sequence");
        }
        this.accessMethod = ASN1ObjectIdentifier.getInstance(asn1Sequence.getObjectAt(0));
        this.accessLocation = GeneralName.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public AccessDescription(final ASN1ObjectIdentifier accessMethod, final GeneralName accessLocation) {
        this.accessMethod = null;
        this.accessLocation = null;
        this.accessMethod = accessMethod;
        this.accessLocation = accessLocation;
    }
    
    public ASN1ObjectIdentifier getAccessMethod() {
        return this.accessMethod;
    }
    
    public GeneralName getAccessLocation() {
        return this.accessLocation;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.accessMethod);
        asn1EncodableVector.add(this.accessLocation);
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        return "AccessDescription: Oid(" + this.accessMethod.getId() + ")";
    }
    
    static {
        id_ad_caIssuers = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.2");
        id_ad_ocsp = new ASN1ObjectIdentifier("1.3.6.1.5.5.7.48.1");
    }
}
