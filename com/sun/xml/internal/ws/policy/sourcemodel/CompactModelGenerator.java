package com.sun.xml.internal.ws.policy.sourcemodel;

import com.sun.xml.internal.ws.policy.NestedPolicy;
import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

class CompactModelGenerator extends PolicyModelGenerator
{
    private static final PolicyLogger LOGGER;
    private final PolicySourceModelCreator sourceModelCreator;
    
    CompactModelGenerator(final PolicySourceModelCreator sourceModelCreator) {
        this.sourceModelCreator = sourceModelCreator;
    }
    
    @Override
    public PolicySourceModel translate(final Policy policy) throws PolicyException {
        CompactModelGenerator.LOGGER.entering(policy);
        PolicySourceModel model = null;
        if (policy == null) {
            CompactModelGenerator.LOGGER.fine(LocalizationMessages.WSP_0047_POLICY_IS_NULL_RETURNING());
        }
        else {
            model = this.sourceModelCreator.create(policy);
            ModelNode rootNode = model.getRootNode();
            final int numberOfAssertionSets = policy.getNumberOfAssertionSets();
            if (numberOfAssertionSets > 1) {
                rootNode = rootNode.createChildExactlyOneNode();
            }
            ModelNode alternativeNode = rootNode;
            for (final AssertionSet set : policy) {
                if (numberOfAssertionSets > 1) {
                    alternativeNode = rootNode.createChildAllNode();
                }
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
        CompactModelGenerator.LOGGER.exiting(model);
        return model;
    }
    
    @Override
    protected ModelNode translate(final ModelNode parentAssertion, final NestedPolicy policy) {
        final ModelNode nestedPolicyRoot = parentAssertion.createChildPolicyNode();
        final AssertionSet set = policy.getAssertionSet();
        this.translate(nestedPolicyRoot, set);
        return nestedPolicyRoot;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(CompactModelGenerator.class);
    }
}
