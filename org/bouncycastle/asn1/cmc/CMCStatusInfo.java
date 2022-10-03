package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Object;

public class CMCStatusInfo extends ASN1Object
{
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private final DERUTF8String statusString;
    private final OtherInfo otherInfo;
    
    CMCStatusInfo(final CMCStatus cmcStatus, final ASN1Sequence bodyList, final DERUTF8String statusString, final OtherInfo otherInfo) {
        this.cMCStatus = cmcStatus;
        this.bodyList = bodyList;
        this.statusString = statusString;
        this.otherInfo = otherInfo;
    }
    
    private CMCStatusInfo(final ASN1Sequence asn1Sequence) {
        if (asn1Sequence.size() < 2 || asn1Sequence.size() > 4) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.cMCStatus = CMCStatus.getInstance(asn1Sequence.getObjectAt(0));
        this.bodyList = ASN1Sequence.getInstance(asn1Sequence.getObjectAt(1));
        if (asn1Sequence.size() > 3) {
            this.statusString = DERUTF8String.getInstance(asn1Sequence.getObjectAt(2));
            this.otherInfo = getInstance(asn1Sequence.getObjectAt(3));
        }
        else if (asn1Sequence.size() > 2) {
            if (asn1Sequence.getObjectAt(2) instanceof DERUTF8String) {
                this.statusString = DERUTF8String.getInstance(asn1Sequence.getObjectAt(2));
                this.otherInfo = null;
            }
            else {
                this.statusString = null;
                this.otherInfo = getInstance(asn1Sequence.getObjectAt(2));
            }
        }
        else {
            this.statusString = null;
            this.otherInfo = null;
        }
    }
    
    public static CMCStatusInfo getInstance(final Object o) {
        if (o instanceof CMCStatusInfo) {
            return (CMCStatusInfo)o;
        }
        if (o != null) {
            return new CMCStatusInfo(ASN1Sequence.getInstance(o));
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
        if (this.otherInfo != null) {
            asn1EncodableVector.add(this.otherInfo);
        }
        return new DERSequence(asn1EncodableVector);
    }
    
    public CMCStatus getCMCStatus() {
        return this.cMCStatus;
    }
    
    public BodyPartID[] getBodyList() {
        return Utils.toBodyPartIDArray(this.bodyList);
    }
    
    public DERUTF8String getStatusString() {
        return this.statusString;
    }
    
    public boolean hasOtherInfo() {
        return this.otherInfo != null;
    }
    
    public OtherInfo getOtherInfo() {
        return this.otherInfo;
    }
    
    public static class OtherInfo extends ASN1Object implements ASN1Choice
    {
        private final CMCFailInfo failInfo;
        private final PendInfo pendInfo;
        
        private static OtherInfo getInstance(final Object o) {
            if (o instanceof OtherInfo) {
                return (OtherInfo)o;
            }
            if (o instanceof ASN1Encodable) {
                final ASN1Primitive asn1Primitive = ((ASN1Encodable)o).toASN1Primitive();
                if (asn1Primitive instanceof ASN1Integer) {
                    return new OtherInfo(CMCFailInfo.getInstance(asn1Primitive));
                }
                if (asn1Primitive instanceof ASN1Sequence) {
                    return new OtherInfo(PendInfo.getInstance(asn1Primitive));
                }
            }
            throw new IllegalArgumentException("unknown object in getInstance(): " + o.getClass().getName());
        }
        
        OtherInfo(final CMCFailInfo cmcFailInfo) {
            this(cmcFailInfo, null);
        }
        
        OtherInfo(final PendInfo pendInfo) {
            this(null, pendInfo);
        }
        
        private OtherInfo(final CMCFailInfo failInfo, final PendInfo pendInfo) {
            this.failInfo = failInfo;
            this.pendInfo = pendInfo;
        }
        
        public boolean isFailInfo() {
            return this.failInfo != null;
        }
        
        @Override
        public ASN1Primitive toASN1Primitive() {
            if (this.pendInfo != null) {
                return this.pendInfo.toASN1Primitive();
            }
            return this.failInfo.toASN1Primitive();
        }
    }
}
