package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.DigestInfo;

public class DVCSCertInfoBuilder
{
    private int version;
    private DVCSRequestInformation dvReqInfo;
    private DigestInfo messageImprint;
    private ASN1Integer serialNumber;
    private DVCSTime responseTime;
    private PKIStatusInfo dvStatus;
    private PolicyInformation policy;
    private ASN1Set reqSignature;
    private ASN1Sequence certs;
    private Extensions extensions;
    private static final int DEFAULT_VERSION = 1;
    private static final int TAG_DV_STATUS = 0;
    private static final int TAG_POLICY = 1;
    private static final int TAG_REQ_SIGNATURE = 2;
    private static final int TAG_CERTS = 3;
    
    public DVCSCertInfoBuilder(final DVCSRequestInformation dvReqInfo, final DigestInfo messageImprint, final ASN1Integer serialNumber, final DVCSTime responseTime) {
        this.version = 1;
        this.dvReqInfo = dvReqInfo;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.responseTime = responseTime;
    }
    
    public DVCSCertInfo build() {
        final ASN1EncodableVector asn1EncodableVector = new ASN1EncodableVector();
        if (this.version != 1) {
            asn1EncodableVector.add(new ASN1Integer(this.version));
        }
        asn1EncodableVector.add(this.dvReqInfo);
        asn1EncodableVector.add(this.messageImprint);
        asn1EncodableVector.add(this.serialNumber);
        asn1EncodableVector.add(this.responseTime);
        if (this.dvStatus != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 0, this.dvStatus));
        }
        if (this.policy != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 1, this.policy));
        }
        if (this.reqSignature != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 2, this.reqSignature));
        }
        if (this.certs != null) {
            asn1EncodableVector.add(new DERTaggedObject(false, 3, this.certs));
        }
        if (this.extensions != null) {
            asn1EncodableVector.add(this.extensions);
        }
        return DVCSCertInfo.getInstance(new DERSequence(asn1EncodableVector));
    }
    
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public void setDvReqInfo(final DVCSRequestInformation dvReqInfo) {
        this.dvReqInfo = dvReqInfo;
    }
    
    public void setMessageImprint(final DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }
    
    public void setSerialNumber(final ASN1Integer serialNumber) {
        this.serialNumber = serialNumber;
    }
    
    public void setResponseTime(final DVCSTime responseTime) {
        this.responseTime = responseTime;
    }
    
    public void setDvStatus(final PKIStatusInfo dvStatus) {
        this.dvStatus = dvStatus;
    }
    
    public void setPolicy(final PolicyInformation policy) {
        this.policy = policy;
    }
    
    public void setReqSignature(final ASN1Set reqSignature) {
        this.reqSignature = reqSignature;
    }
    
    public void setCerts(final TargetEtcChain[] array) {
        this.certs = new DERSequence(array);
    }
    
    public void setExtensions(final Extensions extensions) {
        this.extensions = extensions;
    }
}
