package org.jscep.transport.response;

import org.bouncycastle.cms.CMSException;
import java.io.IOException;
import org.jscep.util.SignedDataUtils;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cms.CMSSignedData;
import java.security.cert.X509Certificate;
import net.jcip.annotations.ThreadSafe;
import java.security.cert.CertStore;

@ThreadSafe
public final class GetNextCaCertResponseHandler implements ScepResponseHandler<CertStore>
{
    private static final String NEXT_CA_CERT = "application/x-x509-next-ca-cert";
    private final X509Certificate signer;
    
    public GetNextCaCertResponseHandler(final X509Certificate signer) {
        this.signer = signer;
    }
    
    @Override
    public CertStore getResponse(final byte[] content, final String mimeType) throws ContentException {
        if (mimeType.startsWith("application/x-x509-next-ca-cert")) {
            try {
                final CMSSignedData cmsMessageData = new CMSSignedData(content);
                final ContentInfo cmsContentInfo = ContentInfo.getInstance((Object)cmsMessageData.getEncoded());
                final CMSSignedData sd = new CMSSignedData(cmsContentInfo);
                if (!SignedDataUtils.isSignedBy(sd, this.signer)) {
                    throw new InvalidContentException("Invalid Signer");
                }
                return SignedDataUtils.fromSignedData(sd);
            }
            catch (final IOException e) {
                throw new InvalidContentTypeException(e);
            }
            catch (final CMSException e2) {
                throw new InvalidContentTypeException((Throwable)e2);
            }
        }
        throw new InvalidContentTypeException(mimeType, new String[] { "application/x-x509-next-ca-cert" });
    }
}
