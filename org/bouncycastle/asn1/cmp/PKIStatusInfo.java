package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;

public class PKIStatusInfo extends ASN1Object
{
    ASN1Integer status;
    PKIFreeText statusString;
    DERBitString failInfo;
    
    public static PKIStatusInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    public static PKIStatusInfo getInstance(final Object o) {
        if (o instanceof PKIStatusInfo) {
            return (PKIStatusInfo)o;
        }
        if (o != null) {
            return new PKIStatusInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    private PKIStatusInfo(final ASN1Sequence asn1Sequence) {
        this.status = ASN1Integer.getInstance(asn1Sequence.getObjectAt(0));
        this.statusString = null;
        this.failInfo = null;
        if (asn1Sequence.size() > 2) {
            this.statusString = PKIFreeText.getInstance(asn1Sequence.getObjectAt(1));
            this.failInfo = DERBitString.getInstance(asn1Sequence.getObjectAt(2));
        }
        else if (asn1Sequence.size() > 1) {
            final ASN1Encodable object = asn1Sequence.getObjectAt(1);
            if (object instanceof DERBitString) {
                this.failInfo = DERBitString.getInstance(object);
            }
            else {
                this.statusString = PKIFreeText.getInstance(object);
            }
        }
    }
    
    public PKIStatusInfo(final PKIStatus pkiStatus) {
        this.status = ASN1Integer.getInstance(pkiStatus.toASN1Primitive());
    }
    
    public PKIStatusInfo(final PKIStatus pkiStatus, final PKIFreeText statusString) {
        this.status = ASN1Integer.getInstance(pkiStatus.toASN1Primitive());
        this.statusString = statusString;
    }
    
    public PKIStatusInfo(final PKIStatus pkiStatus, final PKIFreeText statusString, final PKIFailureInfo failInfo) {
        this.status = ASN1Integer.getInstance(pkiStatus.toASN1Primitive());
        this.statusString = statusString;
        this.failInfo = failInfo;
    }
    
    public BigInteger getStatus() {
        return this.status.getValue();
    }
    
    public PKIFreeText getStatusString() {
        return this.statusString;
    }
    
    public DERBitString getFailInfo() {
        return this.failInfo;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.status);
        if (this.statusString != null) {
            asn1EncodableVector.add(this.statusString);
        }
        if (this.failInfo != null) {
            asn1EncodableVector.add(this.failInfo);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
