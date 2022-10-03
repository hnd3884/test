package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class POPODecKeyRespContent extends ASN1Object
{
    private ASN1Sequence content;
    
    private POPODecKeyRespContent(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static POPODecKeyRespContent getInstance(final Object o) {
        if (o instanceof POPODecKeyRespContent) {
            return (POPODecKeyRespContent)o;
        }
        if (o != null) {
            return new POPODecKeyRespContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ASN1Integer[] toASN1IntegerArray() {
        final ASN1Integer[] array = new ASN1Integer[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = ASN1Integer.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
