package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedClientAssertion;
import com.sun.xml.internal.ws.api.config.management.policy.ManagedServiceAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;

public class ManagementAssertionCreator implements PolicyAssertionCreator
{
    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return new String[] { "http://java.sun.com/xml/ns/metro/management" };
    }
    
    @Override
    public PolicyAssertion createAssertion(final AssertionData data, final Collection<PolicyAssertion> assertionParameters, final AssertionSet nestedAlternative, final PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
        final QName name = data.getName();
        if (ManagedServiceAssertion.MANAGED_SERVICE_QNAME.equals(name)) {
            return new ManagedServiceAssertion(data, assertionParameters);
        }
        if (ManagedClientAssertion.MANAGED_CLIENT_QNAME.equals(name)) {
            return new ManagedClientAssertion(data, assertionParameters);
        }
        return defaultCreator.createAssertion(data, assertionParameters, nestedAlternative, null);
    }
}
