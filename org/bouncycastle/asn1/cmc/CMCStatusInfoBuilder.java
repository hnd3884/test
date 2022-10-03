package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.ASN1Sequence;

public class CMCStatusInfoBuilder
{
    private final CMCStatus cMCStatus;
    private final ASN1Sequence bodyList;
    private DERUTF8String statusString;
    private CMCStatusInfo.OtherInfo otherInfo;
    
    public CMCStatusInfoBuilder(final CMCStatus cmcStatus, final BodyPartID bodyPartID) {
        this.cMCStatus = cmcStatus;
        this.bodyList = new DERSequence(bodyPartID);
    }
    
    public CMCStatusInfoBuilder(final CMCStatus cmcStatus, final BodyPartID[] array) {
        this.cMCStatus = cmcStatus;
        this.bodyList = new DERSequence(array);
    }
    
    public CMCStatusInfoBuilder setStatusString(final String s) {
        this.statusString = new DERUTF8String(s);
        return this;
    }
    
    public CMCStatusInfoBuilder setOtherInfo(final CMCFailInfo cmcFailInfo) {
        this.otherInfo = new CMCStatusInfo.OtherInfo(cmcFailInfo);
        return this;
    }
    
    public CMCStatusInfoBuilder setOtherInfo(final PendInfo pendInfo) {
        this.otherInfo = new CMCStatusInfo.OtherInfo(pendInfo);
        return this;
    }
    
    public CMCStatusInfo build() {
        return new CMCStatusInfo(this.cMCStatus, this.bodyList, this.statusString, this.otherInfo);
    }
}
