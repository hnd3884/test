package org.bouncycastle.x509;

import java.security.cert.CertPathValidatorException;
import java.util.Collection;
import java.security.cert.CertPath;
import java.util.Set;

public abstract class PKIXAttrCertChecker implements Cloneable
{
    public abstract Set getSupportedExtensions();
    
    public abstract void check(final X509AttributeCertificate p0, final CertPath p1, final CertPath p2, final Collection p3) throws CertPathValidatorException;
    
    public abstract Object clone();
}
