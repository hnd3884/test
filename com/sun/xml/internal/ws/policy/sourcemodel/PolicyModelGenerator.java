package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public abstract class PolicyModelGenerator
{
    private static final PolicyLogger LOGGER;
    
    protected PolicyModelGenerator() {
    }
    
    public static PolicyModelGenerator getGenerator() {
        return getNormalizedGenerator(new PolicySourceModelCreator());
    }
    
    protected static PolicyModelGenerator getCompactGenerator(final PolicySourceModelCreator creator) {
        return new CompactModelGenerator(creator);
    }
    
    protected static PolicyModelGenerator getNormalizedGenerator(final PolicySourceModelCreator creator) {
        return new NormalizedModelGenerator(creator);
    }
    
    public abstract PolicySourceModel translate(final Policy p0) throws PolicyException;
    
    protected abstract ModelNode translate(final ModelNode p0, final NestedPolicy p1);
    
    protected void translate(final ModelNode node, final AssertionSet assertions) {
        for (final PolicyAssertion assertion : assertions) {
            final AssertionData data = AssertionData.createAssertionData(assertion.getName(), assertion.getValue(), assertion.getAttributes(), assertion.isOptional(), assertion.isIgnorable());
            final ModelNode assertionNode = node.createChildAssertionNode(data);
            if (assertion.hasNestedPolicy()) {
                this.translate(assertionNode, assertion.getNestedPolicy());
            }
            if (assertion.hasParameters()) {
                this.translate(assertionNode, assertion.getParametersIterator());
            }
        }
    }
    
    protected void translate(final ModelNode assertionNode, final Iterator<PolicyAssertion> assertionParametersIterator) {
        while (assertionParametersIterator.hasNext()) {
            final PolicyAssertion assertionParameter = assertionParametersIterator.next();
            final AssertionData data = AssertionData.createAssertionParameterData(assertionParameter.getName(), assertionParameter.getValue(), assertionParameter.getAttributes());
            final ModelNode assertionParameterNode = assertionNode.createChildAssertionParameterNode(data);
            if (assertionParameter.hasNestedPolicy()) {
                throw PolicyModelGenerator.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0005_UNEXPECTED_POLICY_ELEMENT_FOUND_IN_ASSERTION_PARAM(assertionParameter)));
            }
            if (!assertionParameter.hasNestedAssertions()) {
                continue;
            }
            this.translate(assertionParameterNode, assertionParameter.getNestedAssertionsIterator());
        }
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyModelGenerator.class);
    }
    
    protected static class PolicySourceModelCreator
    {
        protected PolicySourceModel create(final Policy policy) {
            return PolicySourceModel.createPolicySourceModel(policy.getNamespaceVersion(), policy.getId(), policy.getName());
        }
    }
}
