package com.sun.xml.internal.ws.policy.spi;

import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;

public interface PolicyAssertionCreator
{
    String[] getSupportedDomainNamespaceURIs();
    
    PolicyAssertion createAssertion(final AssertionData p0, final Collection<PolicyAssertion> p1, final AssertionSet p2, final PolicyAssertionCreator p3) throws AssertionCreationException;
}
