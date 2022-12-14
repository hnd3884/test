package org.bouncycastle.asn1.x9;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Object;

public class OtherInfo extends ASN1Object
{
    private KeySpecificInfo keyInfo;
    private ASN1OctetString partyAInfo;
    private ASN1OctetString suppPubInfo;
    
    public OtherInfo(final KeySpecificInfo keyInfo, final ASN1OctetString partyAInfo, final ASN1OctetString suppPubInfo) {
        this.keyInfo = keyInfo;
        this.partyAInfo = partyAInfo;
        this.suppPubInfo = suppPubInfo;
    }
    
    public static OtherInfo getInstance(final Object o) {
        if (o instanceof OtherInfo) {
            return (OtherInfo)o;
        }
        if (o != null) {
            return new OtherInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private OtherInfo(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.keyInfo = KeySpecificInfo.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            final ASN1TaggedObject asn1TaggedObject = objects.nextElement();
            if (asn1TaggedObject.getTagNo() == 0) {
                this.partyAInfo = (ASN1OctetString)asn1TaggedObject.getObject();
            }
            else {
                if (asn1TaggedObject.getTagNo() != 2) {
                    continue;
                }
                this.suppPubInfo = (ASN1OctetString)asn1TaggedObject.getObject();
            }
        }
    }
    
    public KeySpecificInfo getKeyInfo() {
        return this.keyInfo;
    }
    
    public ASN1OctetString getPartyAInfo() {
        return this.partyAInfo;
    }
    
    public ASN1OctetString getSuppPubInfo() {
        return this.suppPubInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.keyInfo);
        if (this.partyAInfo != null) {
            asn1EncodableVector.add(new DERTaggedObject(0, this.partyAInfo));
        }
        asn1EncodableVector.add(new DERTaggedObject(2, this.suppPubInfo));
        return new DERSequence(asn1EncodableVector);
    }
}
