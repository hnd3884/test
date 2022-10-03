package org.jscep.transport.response;

import java.security.cert.CertStoreParameters;
import org.jscep.util.SignedDataUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collections;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import net.jcip.annotations.ThreadSafe;
import java.security.cert.CertStore;

@ThreadSafe
public final class GetCaCertResponseHandler implements ScepResponseHandler<CertStore>
{
    private static final String RA_CERT = "application/x-x509-ca-ra-cert";
    private static final String CA_CERT = "application/x-x509-ca-cert";
    
    @Override
    public CertStore getResponse(final byte[] content, final String mimeType) throws ContentException {
        if (mimeType != null && mimeType.startsWith("application/x-x509-ca-cert")) {
            CertificateFactory factory;
            try {
                factory = CertificateFactory.getInstance("X509");
            }
            catch (final CertificateException e) {
                throw new RuntimeException(e);
            }
            try {
                final X509Certificate ca = (X509Certificate)factory.generateCertificate(new ByteArrayInputStream(content));
                final Collection<X509Certificate> caSet = Collections.singleton(ca);
                final CertStoreParameters storeParams = new CollectionCertStoreParameters(caSet);
                return CertStore.getInstance("Collection", storeParams);
            }
            catch (final GeneralSecurityException e2) {
                throw new InvalidContentTypeException(e2);
            }
        }
        if (mimeType == null || !mimeType.startsWith("application/x-x509-ca-ra-cert")) {
            throw new InvalidContentTypeException(mimeType, new String[] { "application/x-x509-ca-cert", "application/x-x509-ca-ra-cert" });
        }
        if (content.length == 0) {
            throw new InvalidContentException("Expected a SignedData object, but response was empty");
        }
        CMSSignedData sd;
        try {
            sd = new CMSSignedData(content);
        }
        catch (final CMSException e3) {
            throw new InvalidContentException((Throwable)e3);
        }
        return SignedDataUtils.fromSignedData(sd);
    }
}
