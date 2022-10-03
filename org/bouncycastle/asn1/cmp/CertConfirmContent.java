package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CertConfirmContent extends ASN1Object
{
    private ASN1Sequence content;
    
    private CertConfirmContent(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static CertConfirmContent getInstance(final Object o) {
        if (o instanceof CertConfirmContent) {
            return (CertConfirmContent)o;
        }
        if (o != null) {
            return new CertConfirmContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CertStatus[] toCertStatusArray() {
        final CertStatus[] array = new CertStatus[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = CertStatus.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
