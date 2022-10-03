package org.bouncycastle.cert.path.validations;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.path.CertPathValidationException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidationContext;
import java.math.BigInteger;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.path.CertPathValidation;

public class BasicConstraintsValidation implements CertPathValidation
{
    private boolean isMandatory;
    private BasicConstraints bc;
    private int pathLengthRemaining;
    private BigInteger maxPathLength;
    
    public BasicConstraintsValidation() {
        this(true);
    }
    
    public BasicConstraintsValidation(final boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
    
    public void validate(final CertPathValidationContext certPathValidationContext, final X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        if (this.maxPathLength != null && this.pathLengthRemaining < 0) {
            throw new CertPathValidationException("BasicConstraints path length exceeded");
        }
        certPathValidationContext.addHandledExtension(Extension.basicConstraints);
        final BasicConstraints fromExtensions = BasicConstraints.fromExtensions(x509CertificateHolder.getExtensions());
        if (fromExtensions != null) {
            if (this.bc != null) {
                if (fromExtensions.isCA()) {
                    final BigInteger pathLenConstraint = fromExtensions.getPathLenConstraint();
                    if (pathLenConstraint != null) {
                        final int intValue = pathLenConstraint.intValue();
                        if (intValue < this.pathLengthRemaining) {
                            this.pathLengthRemaining = intValue;
                            this.bc = fromExtensions;
                        }
                    }
                }
            }
            else {
                this.bc = fromExtensions;
                if (fromExtensions.isCA()) {
                    this.maxPathLength = fromExtensions.getPathLenConstraint();
                    if (this.maxPathLength != null) {
                        this.pathLengthRemaining = this.maxPathLength.intValue();
                    }
                }
            }
        }
        else if (this.bc != null) {
            --this.pathLengthRemaining;
        }
        if (this.isMandatory && this.bc == null) {
            throw new CertPathValidationException("BasicConstraints not present in path");
        }
    }
    
    public Memoable copy() {
        final BasicConstraintsValidation basicConstraintsValidation = new BasicConstraintsValidation(this.isMandatory);
        basicConstraintsValidation.bc = this.bc;
        basicConstraintsValidation.pathLengthRemaining = this.pathLengthRemaining;
        return (Memoable)basicConstraintsValidation;
    }
    
    public void reset(final Memoable memoable) {
        final BasicConstraintsValidation basicConstraintsValidation = (BasicConstraintsValidation)memoable;
        this.isMandatory = basicConstraintsValidation.isMandatory;
        this.bc = basicConstraintsValidation.bc;
        this.pathLengthRemaining = basicConstraintsValidation.pathLengthRemaining;
    }
}
