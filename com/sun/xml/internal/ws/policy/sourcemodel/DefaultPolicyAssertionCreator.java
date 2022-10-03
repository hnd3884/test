package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.spi.AssertionCreationException;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionCreator;

class DefaultPolicyAssertionCreator implements PolicyAssertionCreator
{
    @Override
    public String[] getSupportedDomainNamespaceURIs() {
        return null;
    }
    
    @Override
    public PolicyAssertion createAssertion(final AssertionData data, final Collection<PolicyAssertion> assertionParameters, final AssertionSet nestedAlternative, final PolicyAssertionCreator defaultCreator) throws AssertionCreationException {
        return new DefaultPolicyAssertion(data, assertionParameters, nestedAlternative);
    }
    
    private static final class DefaultPolicyAssertion extends PolicyAssertion
    {
        DefaultPolicyAssertion(final AssertionData data, final Collection<PolicyAssertion> assertionParameters, final AssertionSet nestedAlternative) {
            super(data, assertionParameters, nestedAlternative);
        }
    }
}
