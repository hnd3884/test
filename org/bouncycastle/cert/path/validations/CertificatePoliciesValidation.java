package org.bouncycastle.cert.path.validations;

import org.bouncycastle.util.Memoable;
import org.bouncycastle.cert.path.CertPathValidationException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.x509.PolicyConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.path.CertPathValidationContext;
import org.bouncycastle.cert.path.CertPathValidation;

public class CertificatePoliciesValidation implements CertPathValidation
{
    private int explicitPolicy;
    private int policyMapping;
    private int inhibitAnyPolicy;
    
    CertificatePoliciesValidation(final int n) {
        this(n, false, false, false);
    }
    
    CertificatePoliciesValidation(final int n, final boolean b, final boolean b2, final boolean b3) {
        if (b) {
            this.explicitPolicy = 0;
        }
        else {
            this.explicitPolicy = n + 1;
        }
        if (b2) {
            this.inhibitAnyPolicy = 0;
        }
        else {
            this.inhibitAnyPolicy = n + 1;
        }
        if (b3) {
            this.policyMapping = 0;
        }
        else {
            this.policyMapping = n + 1;
        }
    }
    
    public void validate(final CertPathValidationContext certPathValidationContext, final X509CertificateHolder x509CertificateHolder) throws CertPathValidationException {
        certPathValidationContext.addHandledExtension(Extension.policyConstraints);
        certPathValidationContext.addHandledExtension(Extension.inhibitAnyPolicy);
        if (!certPathValidationContext.isEndEntity() && !ValidationUtils.isSelfIssued(x509CertificateHolder)) {
            this.explicitPolicy = this.countDown(this.explicitPolicy);
            this.policyMapping = this.countDown(this.policyMapping);
            this.inhibitAnyPolicy = this.countDown(this.inhibitAnyPolicy);
            final PolicyConstraints fromExtensions = PolicyConstraints.fromExtensions(x509CertificateHolder.getExtensions());
            if (fromExtensions != null) {
                final BigInteger requireExplicitPolicyMapping = fromExtensions.getRequireExplicitPolicyMapping();
                if (requireExplicitPolicyMapping != null && requireExplicitPolicyMapping.intValue() < this.explicitPolicy) {
                    this.explicitPolicy = requireExplicitPolicyMapping.intValue();
                }
                final BigInteger inhibitPolicyMapping = fromExtensions.getInhibitPolicyMapping();
                if (inhibitPolicyMapping != null && inhibitPolicyMapping.intValue() < this.policyMapping) {
                    this.policyMapping = inhibitPolicyMapping.intValue();
                }
            }
            final Extension extension = x509CertificateHolder.getExtension(Extension.inhibitAnyPolicy);
            if (extension != null) {
                final int intValue = ASN1Integer.getInstance((Object)extension.getParsedValue()).getValue().intValue();
                if (intValue < this.inhibitAnyPolicy) {
                    this.inhibitAnyPolicy = intValue;
                }
            }
        }
    }
    
    private int countDown(final int n) {
        if (n != 0) {
            return n - 1;
        }
        return 0;
    }
    
    public Memoable copy() {
        return (Memoable)new CertificatePoliciesValidation(0);
    }
    
    public void reset(final Memoable memoable) {
        final CertificatePoliciesValidation certificatePoliciesValidation = (CertificatePoliciesValidation)memoable;
    }
}
