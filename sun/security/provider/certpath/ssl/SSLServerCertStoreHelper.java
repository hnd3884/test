package sun.security.provider.certpath.ssl;

import java.security.cert.CertStoreException;
import java.util.Collection;
import java.security.cert.X509CRLSelector;
import java.io.IOException;
import javax.security.auth.x500.X500Principal;
import java.security.cert.X509CertSelector;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.net.URI;
import sun.security.provider.certpath.CertStoreHelper;

public final class SSLServerCertStoreHelper extends CertStoreHelper
{
    @Override
    public CertStore getCertStore(final URI uri) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return SSLServerCertStore.getInstance(uri);
    }
    
    @Override
    public X509CertSelector wrap(final X509CertSelector x509CertSelector, final X500Principal x500Principal, final String s) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public X509CRLSelector wrap(final X509CRLSelector x509CRLSelector, final Collection<X500Principal> collection, final String s) throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isCausedByNetworkIssue(final CertStoreException ex) {
        final Throwable cause = ex.getCause();
        return cause != null && cause instanceof IOException;
    }
}
