package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1EncodableVector;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class NoticeReference extends ASN1Object
{
    private DisplayText organization;
    private ASN1Sequence noticeNumbers;
    
    private static ASN1EncodableVector convertVector(final Vector vector) {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        final Enumeration elements = vector.elements();
        while (elements.hasMoreElements()) {
            final Object nextElement = elements.nextElement();
            ASN1Integer asn1Integer;
            if (nextElement instanceof BigInteger) {
                asn1Integer = new ASN1Integer((BigInteger)nextElement);
            }
            else {
                if (!(nextElement instanceof Integer)) {
                    throw new IllegalArgumentException();
                }
                asn1Integer = new ASN1Integer((int)nextElement);
            }
            asn1EncodableVector.add(asn1Integer);
        }
        return asn1EncodableVector;
    }
    
    public NoticeReference(final String s, final Vector vector) {
        this(s, convertVector(vector));
    }
    
    public NoticeReference(final String s, final ASN1EncodableVector asn1EncodableVector) {
        this(new DisplayText(s), asn1EncodableVector);
    }
    
    public NoticeReference(final DisplayText organization, final ASN1EncodableVector asn1EncodableVector) {
        this.organization = organization;
        this.noticeNumbers = new DERSequence(asn1EncodableVector);
    }
    
    private NoticeReference(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() != 2) {
            throw new IllegalArgumentException("Bad sequence size: " + asn1Sequence.size());
        }
        this.organization = DisplayText.getInstance(asn1Sequence.getObjectAt(0));
        this.noticeNumbers = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
    }
    
    public static NoticeReference getInstance(final Object o) {
        if (o instanceof NoticeReference) {
            return (NoticeReference)o;
        }
        if (o != null) {
            return new NoticeReference(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public DisplayText getOrganization() {
        return this.organization;
    }
    
    public ASN1Integer[] getNoticeNumbers() {
        final ASN1Integer[] array = new ASN1Integer[this.noticeNumbers.size()];
        for (int i = 0; i != this.noticeNumbers.size(); ++i) {
            array[i] = ASN1Integer.getInstance(this.noticeNumbers.getObjectAt(i));
        }
        return array;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.organization);
        asn1EncodableVector.add(this.noticeNumbers);
        return new DERSequence(asn1EncodableVector);
    }
}
