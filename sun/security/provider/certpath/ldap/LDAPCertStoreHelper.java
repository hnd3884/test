package sun.security.provider.certpath.ldap;

import javax.naming.CommunicationException;
import javax.naming.ServiceUnavailableException;
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

public final class LDAPCertStoreHelper extends CertStoreHelper
{
    @Override
    public CertStore getCertStore(final URI uri) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        return LDAPCertStore.getInstance(LDAPCertStore.getParameters(uri));
    }
    
    @Override
    public X509CertSelector wrap(final X509CertSelector x509CertSelector, final X500Principal x500Principal, final String s) throws IOException {
        return new LDAPCertStore.LDAPCertSelector(x509CertSelector, x500Principal, s);
    }
    
    @Override
    public X509CRLSelector wrap(final X509CRLSelector x509CRLSelector, final Collection<X500Principal> collection, final String s) throws IOException {
        return new LDAPCertStore.LDAPCRLSelector(x509CRLSelector, collection, s);
    }
    
    @Override
    public boolean isCausedByNetworkIssue(final CertStoreException ex) {
        final Throwable cause = ex.getCause();
        return cause != null && (cause instanceof ServiceUnavailableException || cause instanceof CommunicationException);
    }
}
