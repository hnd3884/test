package org.owasp.esapi.reference.accesscontrol;

import org.owasp.esapi.AccessControlRule;

public abstract class BaseACR<P, R> implements AccessControlRule<P, R>
{
    protected P policyParameters;
    
    @Override
    public void setPolicyParameters(final P policyParameter) {
        this.policyParameters = policyParameter;
    }
    
    @Override
    public P getPolicyParameters() {
        return this.policyParameters;
    }
}
