package org.jscep.transport.response;

import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;

public final class PkiOperationResponseHandler implements ScepResponseHandler<CMSSignedData>
{
    private static final String PKI_MESSAGE = "application/x-pki-message";
    
    @Override
    public CMSSignedData getResponse(final byte[] content, final String mimeType) throws ContentException {
        if (mimeType.startsWith("application/x-pki-message")) {
            try {
                return new CMSSignedData(content);
            }
            catch (final CMSException e) {
                throw new InvalidContentException((Throwable)e);
            }
        }
        throw new InvalidContentTypeException(mimeType, new String[] { "application/x-pki-message" });
    }
}
