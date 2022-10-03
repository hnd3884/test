package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.api.policy.AlternativeSelector;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import java.util.Iterator;
import javax.xml.ws.WebServiceException;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.sun.xml.internal.ws.policy.PolicyAssertion;
import com.sun.xml.internal.ws.policy.AssertionSet;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.api.policy.ValidationProcessor;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.api.policy.PolicyResolver;

public class DefaultPolicyResolver implements PolicyResolver
{
    @Override
    public PolicyMap resolve(final ServerContext context) {
        final PolicyMap map = context.getPolicyMap();
        if (map != null) {
            this.validateServerPolicyMap(map);
        }
        return map;
    }
    
    @Override
    public PolicyMap resolve(final ClientContext context) {
        PolicyMap map = context.getPolicyMap();
        if (map != null) {
            map = this.doAlternativeSelection(map);
        }
        return map;
    }
    
    private void validateServerPolicyMap(final PolicyMap policyMap) {
        try {
            final ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
            for (final Policy policy : policyMap) {
                for (final AssertionSet assertionSet : policy) {
                    for (final PolicyAssertion assertion : assertionSet) {
                        final PolicyAssertionValidator.Fitness validationResult = validationProcessor.validateServerSide(assertion);
                        if (validationResult != PolicyAssertionValidator.Fitness.SUPPORTED) {
                            throw new PolicyException(PolicyMessages.WSP_1015_SERVER_SIDE_ASSERTION_VALIDATION_FAILED(assertion.getName(), validationResult));
                        }
                    }
                }
            }
        }
        catch (final PolicyException e) {
            throw new WebServiceException(e);
        }
    }
    
    private PolicyMap doAlternativeSelection(final PolicyMap policyMap) {
        final EffectivePolicyModifier modifier = EffectivePolicyModifier.createEffectivePolicyModifier();
        modifier.connect(policyMap);
        try {
            AlternativeSelector.doSelection(modifier);
        }
        catch (final PolicyException e) {
            throw new WebServiceException(e);
        }
        return policyMap;
    }
}
