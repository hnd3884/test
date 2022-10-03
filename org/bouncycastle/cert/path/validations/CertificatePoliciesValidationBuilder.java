package org.bouncycastle.cert.path.validations;

import org.bouncycastle.cert.path.CertPath;

public class CertificatePoliciesValidationBuilder
{
    private boolean isExplicitPolicyRequired;
    private boolean isAnyPolicyInhibited;
    private boolean isPolicyMappingInhibited;
    
    public void setAnyPolicyInhibited(final boolean isAnyPolicyInhibited) {
        this.isAnyPolicyInhibited = isAnyPolicyInhibited;
    }
    
    public void setExplicitPolicyRequired(final boolean isExplicitPolicyRequired) {
        this.isExplicitPolicyRequired = isExplicitPolicyRequired;
    }
    
    public void setPolicyMappingInhibited(final boolean isPolicyMappingInhibited) {
        this.isPolicyMappingInhibited = isPolicyMappingInhibited;
    }
    
    public CertificatePoliciesValidation build(final int n) {
        return new CertificatePoliciesValidation(n, this.isExplicitPolicyRequired, this.isAnyPolicyInhibited, this.isPolicyMappingInhibited);
    }
    
    public CertificatePoliciesValidation build(final CertPath certPath) {
        return this.build(certPath.length());
    }
}
