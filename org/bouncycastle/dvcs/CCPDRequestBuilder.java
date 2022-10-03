package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.ServiceType;

public class CCPDRequestBuilder extends DVCSRequestBuilder
{
    public CCPDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.CCPD));
    }
    
    public DVCSRequest build(final MessageImprint messageImprint) throws DVCSException {
        return this.createDVCRequest(new Data(messageImprint.toASN1Structure()));
    }
}
