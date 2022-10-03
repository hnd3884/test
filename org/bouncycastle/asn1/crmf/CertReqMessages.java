package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CertReqMessages extends ASN1Object
{
    private ASN1Sequence content;
    
    private CertReqMessages(final ASN1Sequence content) {
        this.content = content;
    }
    
    public static CertReqMessages getInstance(final Object o) {
        if (o instanceof CertReqMessages) {
            return (CertReqMessages)o;
        }
        if (o != null) {
            return new CertReqMessages(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public CertReqMessages(final CertReqMsg certReqMsg) {
        this.content = new DERSequence(certReqMsg);
    }
    
    public CertReqMessages(final CertReqMsg[] array) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < array.length; ++i) {
            asn1EncodableVector.add(array[i]);
        }
        this.content = new DERSequence(asn1EncodableVector);
    }
    
    public CertReqMsg[] toCertReqMsgArray() {
        final CertReqMsg[] array = new CertReqMsg[this.content.size()];
        for (int i = 0; i != array.length; ++i) {
            array[i] = CertReqMsg.getInstance(this.content.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        return this.content;
    }
}
