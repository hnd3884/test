package org.jscep.transport.request;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.cms.CMSSignedData;

public final class PkiOperationRequest extends Request
{
    private final CMSSignedData msgData;
    
    public PkiOperationRequest(final CMSSignedData msgData) {
        super(Operation.PKI_OPERATION);
        this.msgData = msgData;
    }
    
    @Override
    public String getMessage() {
        try {
            return new String(Base64.encodeBase64(this.msgData.getEncoded(), false), "UTF-8");
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public String toString() {
        return this.msgData.toString();
    }
}
