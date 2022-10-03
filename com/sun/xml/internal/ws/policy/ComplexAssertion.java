package com.sun.xml.internal.ws.policy;

import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.AssertionData;

public abstract class ComplexAssertion extends PolicyAssertion
{
    private final NestedPolicy nestedPolicy;
    
    protected ComplexAssertion() {
        this.nestedPolicy = NestedPolicy.createNestedPolicy(AssertionSet.emptyAssertionSet());
    }
    
    protected ComplexAssertion(final AssertionData data, final Collection<? extends PolicyAssertion> assertionParameters, final AssertionSet nestedAlternative) {
        super(data, assertionParameters);
        final AssertionSet nestedSet = (nestedAlternative != null) ? nestedAlternative : AssertionSet.emptyAssertionSet();
        this.nestedPolicy = NestedPolicy.createNestedPolicy(nestedSet);
    }
    
    @Override
    public final boolean hasNestedPolicy() {
        return true;
    }
    
    @Override
    public final NestedPolicy getNestedPolicy() {
        return this.nestedPolicy;
    }
}
