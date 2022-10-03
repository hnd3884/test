package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x509.DisplayText;
import org.bouncycastle.asn1.x509.NoticeReference;
import org.bouncycastle.asn1.ASN1Object;

public class SPUserNotice extends ASN1Object
{
    private NoticeReference noticeRef;
    private DisplayText explicitText;
    
    public static SPUserNotice getInstance(final Object o) {
        if (o instanceof SPUserNotice) {
            return (SPUserNotice)o;
        }
        if (o != null) {
            return new SPUserNotice(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private SPUserNotice(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            final ASN1Encodable asn1Encodable = objects.nextElement();
            if (asn1Encodable instanceof DisplayText || asn1Encodable instanceof ASN1String) {
                this.explicitText = DisplayText.getInstance(asn1Encodable);
            }
            else {
                if (!(asn1Encodable instanceof NoticeReference) && !(asn1Encodable instanceof ASN1Sequence)) {
                    throw new IllegalArgumentException("Invalid element in 'SPUserNotice': " + asn1Encodable.getClass().getName());
                }
                this.noticeRef = NoticeReference.getInstance(asn1Encodable);
            }
        }
    }
    
    public SPUserNotice(final NoticeReference noticeRef, final DisplayText explicitText) {
        this.noticeRef = noticeRef;
        this.explicitText = explicitText;
    }
    
    public NoticeReference getNoticeRef() {
        return this.noticeRef;
    }
    
    public DisplayText getExplicitText() {
        return this.explicitText;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.noticeRef != null) {
            asn1EncodableVector.add(this.noticeRef);
        }
        if (this.explicitText != null) {
            asn1EncodableVector.add(this.explicitText);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
