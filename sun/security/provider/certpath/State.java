package sun.security.provider.certpath;

import java.security.cert.CertPathValidatorException;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

interface State extends Cloneable
{
    void updateState(final X509Certificate p0) throws CertificateException, IOException, CertPathValidatorException;
    
    Object clone();
    
    boolean isInitial();
    
    boolean keyParamsNeeded();
}
