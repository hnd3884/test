package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.cms.CMSSignedData;

public class DVCSResponse extends DVCSMessage
{
    private org.bouncycastle.asn1.dvcs.DVCSResponse asn1;
    
    public DVCSResponse(final CMSSignedData cmsSignedData) throws DVCSConstructionException {
        this(SignedData.getInstance((Object)cmsSignedData.toASN1Structure().getContent()).getEncapContentInfo());
    }
    
    public DVCSResponse(final ContentInfo contentInfo) throws DVCSConstructionException {
        super(contentInfo);
        if (!DVCSObjectIdentifiers.id_ct_DVCSResponseData.equals((Object)contentInfo.getContentType())) {
            throw new DVCSConstructionException("ContentInfo not a DVCS Response");
        }
        try {
            if (contentInfo.getContent().toASN1Primitive() instanceof ASN1Sequence) {
                this.asn1 = org.bouncycastle.asn1.dvcs.DVCSResponse.getInstance((Object)contentInfo.getContent());
            }
            else {
                this.asn1 = org.bouncycastle.asn1.dvcs.DVCSResponse.getInstance((Object)ASN1OctetString.getInstance((Object)contentInfo.getContent()).getOctets());
            }
        }
        catch (final Exception ex) {
            throw new DVCSConstructionException("Unable to parse content: " + ex.getMessage(), ex);
        }
    }
    
    @Override
    public ASN1Encodable getContent() {
        return (ASN1Encodable)this.asn1;
    }
}
