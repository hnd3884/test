package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.ServiceType;

public class CPDRequestBuilder extends DVCSRequestBuilder
{
    public CPDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.CPD));
    }
    
    public DVCSRequest build(final byte[] array) throws DVCSException {
        return this.createDVCRequest(new Data(array));
    }
}
