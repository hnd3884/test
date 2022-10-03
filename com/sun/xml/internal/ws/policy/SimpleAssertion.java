package com.sun.xml.internal.ws.policy;

import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;

public abstract class SimpleAssertion extends PolicyAssertion
{
    protected SimpleAssertion() {
    }
    
    protected SimpleAssertion(final AssertionData data, final Collection<? extends PolicyAssertion> assertionParameters) {
        super(data, assertionParameters);
    }
    
    @Override
    public final boolean hasNestedPolicy() {
        return false;
    }
    
    @Override
    public final NestedPolicy getNestedPolicy() {
        return null;
    }
}
