package java.security.cert;

import java.util.Collection;
import java.security.InvalidAlgorithmParameterException;

public abstract class CertStoreSpi
{
    public CertStoreSpi(final CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException {
    }
    
    public abstract Collection<? extends Certificate> engineGetCertificates(final CertSelector p0) throws CertStoreException;
    
    public abstract Collection<? extends CRL> engineGetCRLs(final CRLSelector p0) throws CertStoreException;
}
