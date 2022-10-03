package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class PKIMessages extends ASN1Object
{
    private ASN1Sequence content;
    
    private PKIMessages(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static PKIMessages getInstance(final Object o) {
        if (o instanceof PKIMessages) {
            return (PKIMessages)o;
        }
        if (o != null) {
            return new PKIMessages(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public PKIMessages(final PKIMessage pkiMessage) {
        this.content = new DERSequence(pkiMessage);
    }
    
    public PKIMessages(final PKIMessage[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.content = new DERSequence(asn1EncodableVector);
    }
    
    public PKIMessage[] toPKIMessageArray() {
        final PKIMessage[] array = new PKIMessage[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = PKIMessage.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
