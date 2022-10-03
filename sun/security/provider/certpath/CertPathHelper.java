package sun.security.provider.certpath;

import java.util.Date;
import java.security.cert.X509CRLSelector;
import sun.security.x509.GeneralNameInterface;
import java.util.Set;
import java.security.cert.X509CertSelector;

public abstract class CertPathHelper
{
    protected static CertPathHelper instance;
    
    protected CertPathHelper() {
    }
    
    protected abstract void implSetPathToNames(final X509CertSelector p0, final Set<GeneralNameInterface> p1);
    
    protected abstract void implSetDateAndTime(final X509CRLSelector p0, final Date p1, final long p2);
    
    static void setPathToNames(final X509CertSelector x509CertSelector, final Set<GeneralNameInterface> set) {
        CertPathHelper.instance.implSetPathToNames(x509CertSelector, set);
    }
    
    public static void setDateAndTime(final X509CRLSelector x509CRLSelector, final Date date, final long n) {
        CertPathHelper.instance.implSetDateAndTime(x509CRLSelector, date, n);
    }
}
