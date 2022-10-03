package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CMCStatusInfoV2 extends ASN1Object
{
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private final DERUTF8String statusString;
    private final OtherStatusInfo otherStatusInfo;
    
    CMCStatusInfoV2(final CMCStatus cmcStatus, final ASN1Sequence bodyList, final DERUTF8String statusString, final OtherStatusInfo otherStatusInfo) {
        this.cMCStatus = cmcStatus;
        this.bodyList = bodyList;
        this.statusString = statusString;
        this.otherStatusInfo = otherStatusInfo;
    }
    
    private CMCStatusInfoV2(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 2 || asn1Sequence.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.cMCStatus = CMCStatus.getInstance(asn1Sequence.getObjectAt(0));
        this.bodyList = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() > 2) {
            if (asn1Sequence.size() == 4) {
                this.statusString = DERUTF8String.getInstance(asn1Sequence.getObjectAt(2));
                this.otherStatusInfo = OtherStatusInfo.getInstance(asn1Sequence.getObjectAt(3));
            }
            else if (asn1Sequence.getObjectAt(2) instanceof DERUTF8String) {
                this.statusString = DERUTF8String.getInstance(asn1Sequence.getObjectAt(2));
                this.otherStatusInfo = null;
            }
            else {
                this.statusString = null;
                this.otherStatusInfo = OtherStatusInfo.getInstance(asn1Sequence.getObjectAt(2));
            }
        }
        else {
            this.statusString = null;
            this.otherStatusInfo = null;
        }
    }
    
    public CMCStatus getcMCStatus() {
        return this.cMCStatus;
    }
    
    public BodyPartID[] getBodyList() {
        return Utils.toBodyPartIDArray(this.bodyList);
    }
    
    public DERUTF8String getStatusString() {
        return this.statusString;
    }
    
    public OtherStatusInfo getOtherStatusInfo() {
        return this.otherStatusInfo;
    }
    
    public boolean hasOtherInfo() {
        return this.otherStatusInfo != null;
    }
    
    public static CMCStatusInfoV2 getInstance(final Object o) {
        if (o instanceof CMCStatusInfoV2) {
            return (CMCStatusInfoV2)o;
        }
        if (o != null) {
            return new CMCStatusInfoV2(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        asn1EncodableVector.add(this.cMCStatus);
        asn1EncodableVector.add(this.bodyList);
        if (this.statusString != null) {
            asn1EncodableVector.add(this.statusString);
        }
        if (this.otherStatusInfo != null) {
            asn1EncodableVector.add(this.otherStatusInfo);
        }
        return new DERSequence(asn1EncodableVector);
    }
}
