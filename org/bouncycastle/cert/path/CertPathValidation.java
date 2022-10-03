package org.bouncycastle.cert.path;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.Memoable;

public interface CertPathValidation extends Memoable
{
    void validate(final CertPathValidationContext p0, final X509CertificateHolder p1) throws CertPathValidationException;
}
