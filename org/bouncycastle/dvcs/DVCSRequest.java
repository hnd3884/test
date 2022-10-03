package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.cms.CMSSignedData;

public class DVCSRequest extends DVCSMessage
{
    private org.bouncycastle.asn1.dvcs.DVCSRequest asn1;
    private DVCSRequestInfo reqInfo;
    private DVCSRequestData data;
    
    public DVCSRequest(final CMSSignedData cmsSignedData) throws DVCSConstructionException {
        this(SignedData.getInstance((Object)cmsSignedData.toASN1Structure().getContent()).getEncapContentInfo());
    }
    
    public DVCSRequest(final ContentInfo contentInfo) throws DVCSConstructionException {
        super(contentInfo);
        if (!DVCSObjectIdentifiers.id_ct_DVCSRequestData.equals((Object)contentInfo.getContentType())) {
            throw new DVCSConstructionException("ContentInfo not a DVCS Request");
        }
        try {
            if (contentInfo.getContent().toASN1Primitive() instanceof ASN1Sequence) {
                this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance((Object)contentInfo.getContent());
            }
            else {
                this.asn1 = org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance((Object)ASN1OctetString.getInstance((Object)contentInfo.getContent()).getOctets());
            }
        }
        catch (final Exception ex) {
            throw new DVCSConstructionException("Unable to parse content: " + ex.getMessage(), ex);
        }
        this.reqInfo = new DVCSRequestInfo(this.asn1.getRequestInformation());
        final int serviceType = this.reqInfo.getServiceType();
        if (serviceType == ServiceType.CPD.getValue().intValue()) {
            this.data = new CPDRequestData(this.asn1.getData());
        }
        else if (serviceType == ServiceType.VSD.getValue().intValue()) {
            this.data = new VSDRequestData(this.asn1.getData());
        }
        else if (serviceType == ServiceType.VPKC.getValue().intValue()) {
            this.data = new VPKCRequestData(this.asn1.getData());
        }
        else {
            if (serviceType != ServiceType.CCPD.getValue().intValue()) {
                throw new DVCSConstructionException("Unknown service type: " + serviceType);
            }
            this.data = new CCPDRequestData(this.asn1.getData());
        }
    }
    
    @Override
    public ASN1Encodable getContent() {
        return (ASN1Encodable)this.asn1;
    }
    
    public DVCSRequestInfo getRequestInfo() {
        return this.reqInfo;
    }
    
    public DVCSRequestData getData() {
        return this.data;
    }
    
    public GeneralName getTransactionIdentifier() {
        return this.asn1.getTransactionIdentifier();
    }
}
