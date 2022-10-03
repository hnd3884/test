package org.owasp.esapi.reference.accesscontrol.policyloader;

import org.owasp.esapi.reference.accesscontrol.DynaBeanACRParameter;
import org.owasp.esapi.reference.accesscontrol.BaseACR;

public class EchoDynaBeanPolicyParameterACR extends BaseACR<DynaBeanACRParameter, Object>
{
    @Override
    public boolean isAuthorized(final Object runtimeParameter) throws ClassCastException {
        return ((BaseACR<DynaBeanACRParameter, R>)this).getPolicyParameters().getBoolean("isTrue");
    }
}
