package org.bouncycastle.cert.path.validations;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidation;

public class KeyUsageValidation implements CertPathValidation
{
    private boolean isMandatory;
    
    public KeyUsageValidation() {
        this(true);
    }
    
    public KeyUsageValidation(final boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
    
    public void validate(final CertPathValidationContext certPathValidationContext, final X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        certPathValidationContext.addHandledExtension(Extension.keyUsage);
        if (!certPathValidationContext.isEndEntity()) {
            final KeyUsage fromExtensions = KeyUsage.fromExtensions(x509CertificateHolder.getExtensions());
            if (fromExtensions != null) {
                if (!fromExtensions.hasUsages(4)) {
                    throw new CertPathValidationException("Issuer certificate KeyUsage extension does not permit key signing");
                }
            }
            else if (this.isMandatory) {
                throw new CertPathValidationException("KeyUsage extension not present in CA certificate");
            }
        }
    }
    
    public Memoable copy() {
        return (Memoable)new KeyUsageValidation(this.isMandatory);
    }
    
    public void reset(final Memoable memoable) {
        this.isMandatory = ((KeyUsageValidation)memoable).isMandatory;
    }
}
