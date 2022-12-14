package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.Data;

public class CCPDRequestData extends DVCSRequestData
{
    CCPDRequestData(final Data data) throws DVCSConstructionException {
        super(data);
        this.initDigest();
    }
    
    private void initDigest() throws DVCSConstructionException {
        if (this.data.getMessageImprint() == null) {
            throw new DVCSConstructionException("DVCSRequest.data.messageImprint should be specified for CCPD service");
        }
    }
    
    public MessageImprint getMessageImprint() {
        return new MessageImprint(this.data.getMessageImprint());
    }
}
