package org.bouncycastle.cert.path;

import org.bouncycastle.cert.X509CertificateHolder;

public class CertPath
{
    private final X509CertificateHolder[] certificates;
    
    public CertPath(final X509CertificateHolder[] array) {
        this.certificates = this.copyArray(array);
    }
    
    public X509CertificateHolder[] getCertificates() {
        return this.copyArray(this.certificates);
    }
    
    public CertPathValidationResult validate(final CertPathValidation[] array) {
        final CertPathValidationContext certPathValidationContext = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
        for (int i = 0; i != array.length; ++i) {
            for (int j = this.certificates.length - 1; j >= 0; --j) {
                try {
                    certPathValidationContext.setIsEndEntity(j == 0);
                    array[i].validate(certPathValidationContext, this.certificates[j]);
                }
                catch (final CertPathValidationException ex) {
                    return new CertPathValidationResult(certPathValidationContext, j, i, ex);
                }
            }
        }
        return new CertPathValidationResult(certPathValidationContext);
    }
    
    public CertPathValidationResult evaluate(final CertPathValidation[] array) {
        final CertPathValidationContext certPathValidationContext = new CertPathValidationContext(CertPathUtils.getCriticalExtensionsOIDs(this.certificates));
        final CertPathValidationResultBuilder certPathValidationResultBuilder = new CertPathValidationResultBuilder(certPathValidationContext);
        for (int i = 0; i != array.length; ++i) {
            for (int j = this.certificates.length - 1; j >= 0; --j) {
                try {
                    certPathValidationContext.setIsEndEntity(j == 0);
                    array[i].validate(certPathValidationContext, this.certificates[j]);
                }
                catch (final CertPathValidationException ex) {
                    certPathValidationResultBuilder.addException(j, i, ex);
                }
            }
        }
        return certPathValidationResultBuilder.build();
    }
    
    private X509CertificateHolder[] copyArray(final X509CertificateHolder[] array) {
        final X509CertificateHolder[] array2 = new X509CertificateHolder[array.length];
        System.arraycopy(array, 0, array2, 0, array2.length);
        return array2;
    }
    
    public int length() {
        return this.certificates.length;
    }
}
