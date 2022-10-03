package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

class NormalizedModelGenerator extends PolicyModelGenerator
{
    private static final PolicyLogger LOGGER;
    private final PolicySourceModelCreator sourceModelCreator;
    
    NormalizedModelGenerator(final PolicySourceModelCreator sourceModelCreator) {
        this.sourceModelCreator = sourceModelCreator;
    }
    
    @Override
    public PolicySourceModel translate(final Policy policy) throws PolicyException {
        NormalizedModelGenerator.LOGGER.entering(policy);
        PolicySourceModel model = null;
        if (policy == null) {
            NormalizedModelGenerator.LOGGER.fine(LocalizationMessages.WSP_0047_POLICY_IS_NULL_RETURNING());
        }
        else {
            model = this.sourceModelCreator.create(policy);
            final ModelNode rootNode = model.getRootNode();
            final ModelNode exactlyOneNode = rootNode.createChildExactlyOneNode();
            for (final AssertionSet set : policy) {
                final ModelNode alternativeNode = exactlyOneNode.createChildAllNode();
                for (final PolicyAssertion assertion : set) {
                    final AssertionData data = AssertionData.createAssertionData(assertion.getName(), assertion.getValue(), assertion.getAttributes(), assertion.isOptional(), assertion.isIgnorable());
                    final ModelNode assertionNode = alternativeNode.createChildAssertionNode(data);
                    if (assertion.hasNestedPolicy()) {
                        this.translate(assertionNode, assertion.getNestedPolicy());
                    }
                    if (assertion.hasParameters()) {
                        this.translate(assertionNode, assertion.getParametersIterator());
                    }
                }
            }
        }
        NormalizedModelGenerator.LOGGER.exiting(model);
        return model;
    }
    
    @Override
    protected ModelNode translate(final ModelNode parentAssertion, final NestedPolicy policy) {
        final ModelNode nestedPolicyRoot = parentAssertion.createChildPolicyNode();
        final ModelNode exactlyOneNode = nestedPolicyRoot.createChildExactlyOneNode();
        final AssertionSet set = policy.getAssertionSet();
        final ModelNode alternativeNode = exactlyOneNode.createChildAllNode();
        this.translate(alternativeNode, set);
        return nestedPolicyRoot;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(NormalizedModelGenerator.class);
    }
}
