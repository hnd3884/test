package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class PollReqContent extends ASN1Object
{
    private ASN1Sequence content;
    
    private PollReqContent(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static PollReqContent getInstance(final Object o) {
        if (o instanceof PollReqContent) {
            return (PollReqContent)o;
        }
        if (o != null) {
            return new PollReqContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public PollReqContent(final ASN1Integer asn1Integer) {
        this(new DERSequence(new DERSequence(asn1Integer)));
    }
    
    public ASN1Integer[][] getCertReqIds() {
        final ASN1Integer[][] array = new ASN1Integer[this.content.size()][];
        for (int i = 0; i != array.length; ++i) {
            array[i] = sequenceToASN1IntegerArray((ASN1Sequence)this.content.getObjectAt(i));
        }
        return array;
    }
    
    private static ASN1Integer[] sequenceToASN1IntegerArray(final ASN1Sequence asn1Sequence) {
        final ASN1Integer[] array = new ASN1Integer[asn1Sequence.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = ASN1Integer.getInstance(asn1Sequence.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
