package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.ASN1Object;

public class DVCSCertInfo extends ASN1Object
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
    
    public DVCSCertInfo(final DVCSRequestInformation dvReqInfo, final DigestInfo messageImprint, final ASN1Integer serialNumber, final DVCSTime responseTime) {
        this.version = 1;
        this.dvReqInfo = dvReqInfo;
        this.messageImprint = messageImprint;
        this.serialNumber = serialNumber;
        this.responseTime = responseTime;
    }
    
    private DVCSCertInfo(final ASN1Sequence asn1Sequence) {
        this.version = 1;
        int i = 0;
        ASN1Encodable asn1Encodable = asn1Sequence.getObjectAt(i++);
        try {
            this.version = ASN1Integer.getInstance(asn1Encodable).getValue().intValue();
            asn1Encodable = asn1Sequence.getObjectAt(i++);
        }
        catch (final IllegalArgumentException ex) {}
        this.dvReqInfo = DVCSRequestInformation.getInstance(asn1Encodable);
        this.messageImprint = DigestInfo.getInstance(asn1Sequence.getObjectAt(i++));
        this.serialNumber = ASN1Integer.getInstance(asn1Sequence.getObjectAt(i++));
        this.responseTime = DVCSTime.getInstance(asn1Sequence.getObjectAt(i++));
        while (i < asn1Sequence.size()) {
            final ASN1Encodable object = asn1Sequence.getObjectAt(i++);
            if (object instanceof ASN1TaggedObject) {
                final ASN1TaggedObject instance = ASN1TaggedObject.getInstance(object);
                final int tagNo = instance.getTagNo();
                switch (tagNo) {
                    case 0: {
                        this.dvStatus = PKIStatusInfo.getInstance(instance, false);
                        continue;
                    }
                    case 1: {
                        this.policy = PolicyInformation.getInstance(ASN1Sequence.getInstance(instance, false));
                        continue;
                    }
                    case 2: {
                        this.reqSignature = ASN1Set.getInstance(instance, false);
                        continue;
                    }
                    case 3: {
                        this.certs = ASN1Sequence.getInstance(instance, false);
                        continue;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown tag encountered: " + tagNo);
                    }
                }
            }
            else {
                try {
                    this.extensions = Extensions.getInstance(object);
                }
                catch (final IllegalArgumentException ex2) {}
            }
        }
    }
    
    public static DVCSCertInfo getInstance(final Object o) {
        if (o instanceof DVCSCertInfo) {
            return (DVCSCertInfo)o;
        }
        if (o != null) {
            return new DVCSCertInfo(ASN1Sequence.getInstance(o));
        }
        return null;
    }
    
    public static DVCSCertInfo getInstance(final ASN1TaggedObject asn1TaggedObject, final boolean b) {
        return getInstance(ASN1Sequence.getInstance(asn1TaggedObject, b));
    }
    
    @Override
    public ASN1Primitive toASN1Primitive() {
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
        return new DERSequence(asn1EncodableVector);
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("DVCSCertInfo {\n");
        if (this.version != 1) {
            sb.append("version: " + this.version + "\n");
        }
        sb.append("dvReqInfo: " + this.dvReqInfo + "\n");
        sb.append("messageImprint: " + this.messageImprint + "\n");
        sb.append("serialNumber: " + this.serialNumber + "\n");
        sb.append("responseTime: " + this.responseTime + "\n");
        if (this.dvStatus != null) {
            sb.append("dvStatus: " + this.dvStatus + "\n");
        }
        if (this.policy != null) {
            sb.append("policy: " + this.policy + "\n");
        }
        if (this.reqSignature != null) {
            sb.append("reqSignature: " + this.reqSignature + "\n");
        }
        if (this.certs != null) {
            sb.append("certs: " + this.certs + "\n");
        }
        if (this.extensions != null) {
            sb.append("extensions: " + this.extensions + "\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
    
    public int getVersion() {
        return this.version;
    }
    
    private void setVersion(final int version) {
        this.version = version;
    }
    
    public DVCSRequestInformation getDvReqInfo() {
        return this.dvReqInfo;
    }
    
    private void setDvReqInfo(final DVCSRequestInformation dvReqInfo) {
        this.dvReqInfo = dvReqInfo;
    }
    
    public DigestInfo getMessageImprint() {
        return this.messageImprint;
    }
    
    private void setMessageImprint(final DigestInfo messageImprint) {
        this.messageImprint = messageImprint;
    }
    
    public ASN1Integer getSerialNumber() {
        return this.serialNumber;
    }
    
    public DVCSTime getResponseTime() {
        return this.responseTime;
    }
    
    public PKIStatusInfo getDvStatus() {
        return this.dvStatus;
    }
    
    public PolicyInformation getPolicy() {
        return this.policy;
    }
    
    public ASN1Set getReqSignature() {
        return this.reqSignature;
    }
    
    public TargetEtcChain[] getCerts() {
        if (this.certs != null) {
            return TargetEtcChain.arrayFromSequence(this.certs);
        }
        return null;
    }
    
    public Extensions getExtensions() {
        return this.extensions;
    }
}
