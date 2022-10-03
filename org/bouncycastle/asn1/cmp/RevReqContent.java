package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class RevReqContent extends ASN1Object
{
    private ASN1Sequence content;
    
    private RevReqContent(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static RevReqContent getInstance(final Object o) {
        if (o instanceof RevReqContent) {
            return (RevReqContent)o;
        }
        if (o != null) {
            return new RevReqContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public RevReqContent(final RevDetails revDetails) {
        this.content = new DERSequence(revDetails);
    }
    
    public RevReqContent(final RevDetails[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.content = new DERSequence(asn1EncodableVector);
    }
    
    public RevDetails[] toRevDetailsArray() {
        final RevDetails[] array = new RevDetails[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = RevDetails.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
