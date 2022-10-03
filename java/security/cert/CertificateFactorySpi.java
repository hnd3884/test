package java.security.cert;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.io.InputStream;

public abstract class CertificateFactorySpi
{
    public abstract Certificate engineGenerateCertificate(final InputStream p0) throws CertificateException;
    
    public CertPath engineGenerateCertPath(final InputStream inputStream) throws CertificateException {
        throw new UnsupportedOperationException();
    }
    
    public CertPath engineGenerateCertPath(final InputStream inputStream, final String s) throws CertificateException {
        throw new UnsupportedOperationException();
    }
    
    public CertPath engineGenerateCertPath(final List<? extends Certificate> list) throws CertificateException {
        throw new UnsupportedOperationException();
    }
    
    public Iterator<String> engineGetCertPathEncodings() {
        throw new UnsupportedOperationException();
    }
    
    public abstract Collection<? extends Certificate> engineGenerateCertificates(final InputStream p0) throws CertificateException;
    
    public abstract CRL engineGenerateCRL(final InputStream p0) throws CRLException;
    
    public abstract Collection<? extends CRL> engineGenerateCRLs(final InputStream p0) throws CRLException;
}
