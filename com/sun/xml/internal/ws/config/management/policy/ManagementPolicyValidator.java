package com.sun.xml.internal.ws.config.management.policy;

import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;

public class ManagementPolicyValidator implements PolicyAssertionValidator
{
    @Override
    public Fitness validateClientSide(final PolicyAssertion assertion) {
        final QName assertionName = assertion.getName();
        if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(assertionName)) {
            return Fitness.SUPPORTED;
        }
        if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(assertionName)) {
            return Fitness.UNSUPPORTED;
        }
        return Fitness.UNKNOWN;
    }
    
    @Override
    public Fitness validateServerSide(final PolicyAssertion assertion) {
        final QName assertionName = assertion.getName();
        if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(assertionName)) {
            return Fitness.SUPPORTED;
        }
        if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(assertionName)) {
            return Fitness.UNSUPPORTED;
        }
        return Fitness.UNKNOWN;
    }
    
    @Override
    public String[] declareSupportedDomains() {
        return new String[] { "http://java.sun.com/xml/ns/metro/management" };
    }
}
