package org.bouncycastle.dvcs;

import java.io.IOException;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import java.util.Date;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.ServiceType;

public class VSDRequestBuilder extends DVCSRequestBuilder
{
    public VSDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.VSD));
    }
    
    public void setRequestTime(final Date date) {
        this.requestInformationBuilder.setRequestTime(new DVCSTime(date));
    }
    
    public DVCSRequest build(final CMSSignedData cmsSignedData) throws DVCSException {
        try {
            return this.createDVCRequest(new Data(cmsSignedData.getEncoded()));
        }
        catch (final IOException ex) {
            throw new DVCSException("Failed to encode CMS signed data", ex);
        }
    }
}
