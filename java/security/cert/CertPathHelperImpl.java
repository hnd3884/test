package java.security.cert;

import java.util.Date;
import sun.security.x509.GeneralNameInterface;
import java.util.Set;
import sun.security.provider.certpath.CertPathHelper;

class CertPathHelperImpl extends CertPathHelper
{
    private CertPathHelperImpl() {
    }
    
    static synchronized void initialize() {
        if (CertPathHelper.instance == null) {
            CertPathHelper.instance = new CertPathHelperImpl();
        }
    }
    
    @Override
    protected void implSetPathToNames(final X509CertSelector x509CertSelector, final Set<GeneralNameInterface> pathToNamesInternal) {
        x509CertSelector.setPathToNamesInternal(pathToNamesInternal);
    }
    
    @Override
    protected void implSetDateAndTime(final X509CRLSelector x509CRLSelector, final Date date, final long n) {
        x509CRLSelector.setDateAndTime(date, n);
    }
}
