package sun.security.provider.certpath;

import sun.security.util.UntrustedCertificates;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.security.cert.Certificate;
import java.util.Set;
import java.security.cert.CertPathValidatorException;
import sun.security.util.Debug;
import java.security.cert.PKIXCertPathChecker;

public final class UntrustedChecker extends PKIXCertPathChecker
{
    private static final Debug debug;
    
    @Override
    public void init(final boolean b) throws CertPathValidatorException {
    }
    
    @Override
    public boolean isForwardCheckingSupported() {
        return true;
    }
    
    @Override
    public Set<String> getSupportedExtensions() {
        return null;
    }
    
    @Override
    public void check(final Certificate certificate, final Collection<String> collection) throws CertPathValidatorException {
        final X509Certificate x509Certificate = (X509Certificate)certificate;
        if (UntrustedCertificates.isUntrusted(x509Certificate)) {
            if (UntrustedChecker.debug != null) {
                UntrustedChecker.debug.println("UntrustedChecker: untrusted certificate " + x509Certificate.getSubjectX500Principal());
            }
            throw new CertPathValidatorException("Untrusted certificate: " + x509Certificate.getSubjectX500Principal());
        }
    }
    
    static {
        debug = Debug.getInstance("certpath");
    }
}
