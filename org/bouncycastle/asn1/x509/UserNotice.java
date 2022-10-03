package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class UserNotice extends ASN1Object
{
    private final NoticeReference noticeRef;
    private final DisplayText explicitText;
    
    public UserNotice(final NoticeReference noticeRef, final DisplayText explicitText) {
        this.noticeRef = noticeRef;
        this.explicitText = explicitText;
    }
    
    public UserNotice(final NoticeReference noticeReference, final String s) {
        this(noticeReference, new DisplayText(s));
    }
    
    private UserNotice(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() == 2) {
            this.noticeRef = NoticeReference.getInstance(asn1Sequence.getObjectAt(0));
            this.explicitText = DisplayText.getInstance(asn1Sequence.getObjectAt(1));
        }
        else if (asn1Sequence.size() == 1) {
            if (asn1Sequence.getObjectAt(0).toASN1Primitive() instanceof ASN1Sequence) {
                this.noticeRef = NoticeReference.getInstance(asn1Sequence.getObjectAt(0));
                this.explicitText = null;
            }
            else {
                this.noticeRef = null;
                this.explicitText = DisplayText.getInstance(asn1Sequence.getObjectAt(0));
            }
        }
        else {
            if (asn1Sequence.size() != 0) {
                throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
            }
            this.noticeRef = null;
            this.explicitText = null;
        }
    }
    
    public static UserNotice getInstance(final Object o) {
        if (o instanceof UserNotice) {
            return (UserNotice)o;
        }
        if (o != null) {
            return new UserNotice(ASN1Sequence.getInstance(o));
        }
        return null;
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
