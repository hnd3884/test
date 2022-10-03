package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class ErrorMsgContent extends ASN1Object
{
    private PKIStatusInfo pkiStatusInfo;
    private ASN1Integer errorCode;
    private PKIFreeText errorDetails;
    
    private ErrorMsgContent(final ASN1Sequence asn1Sequence) {
        final Enumeration objects = asn1Sequence.getObjects();
        this.pkiStatusInfo = PKIStatusInfo.getInstance(objects.nextElement());
        while (objects.hasMoreElements()) {
            final Object nextElement = objects.nextElement();
            if (nextElement instanceof ASN1Integer) {
                this.errorCode = ASN1Integer.getInstance(nextElement);
            }
            else {
                this.errorDetails = PKIFreeText.getInstance(nextElement);
            }
        }
    }
    
    public static ErrorMsgContent getInstance(final Object o) {
        if (o instanceof ErrorMsgContent) {
            return (ErrorMsgContent)o;
        }
        if (o != null) {
            return new ErrorMsgContent(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public ErrorMsgContent(final PKIStatusInfo pkiStatusInfo) {
        this(pkiStatusInfo, null, null);
    }
    
    public ErrorMsgContent(final PKIStatusInfo pkiStatusInfo, final ASN1Integer errorCode, final PKIFreeText errorDetails) {
        if (pkiStatusInfo == null) {
            throw new IllegalArgumentException("'pkiStatusInfo' cannot be null");
        }
        this.pkiStatusInfo = pkiStatusInfo;
        this.errorCode = errorCode;
        this.errorDetails = errorDetails;
    }
    
    public PKIStatusInfo getPKIStatusInfo() {
        return this.pkiStatusInfo;
    }
    
    public ASN1Integer getErrorCode() {
        return this.errorCode;
    }
    
    public PKIFreeText getErrorDetails() {
        return this.errorDetails;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.pkiStatusInfo);
        this.addOptional(asn1EncodableVector, this.errorCode);
        this.addOptional(asn1EncodableVector, this.errorDetails);
        return new DERSequence(asn1EncodableVector);
    }
    
    private void addOptional(final ASN1EncodableVector asn1EncodableVector, final ASN1Encodable asn1Encodable) {
        if (asn1Encodable != null) {
            asn1EncodableVector.add(asn1Encodable);
        }
    }
}
