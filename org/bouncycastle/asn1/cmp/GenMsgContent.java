package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class GenMsgContent extends ASN1Object
{
    private ASN1Sequence content;
    
    private GenMsgContent(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static GenMsgContent getInstance(final Object o) {
        if (o instanceof GenMsgContent) {
            return (GenMsgContent)o;
        }
        if (o != null) {
            return new GenMsgContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public GenMsgContent(final InfoTypeAndValue infoTypeAndValue) {
        this.content = new DERSequence(infoTypeAndValue);
    }
    
    public GenMsgContent(final InfoTypeAndValue[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.content = new DERSequence(asn1EncodableVector);
    }
    
    public InfoTypeAndValue[] toInfoTypeAndValueArray() {
        final InfoTypeAndValue[] array = new InfoTypeAndValue[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = InfoTypeAndValue.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
