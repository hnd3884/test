package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.AssertionValidationProcessor;
import com.sun.xml.internal.ws.policy.EffectivePolicyModifier;
import com.sun.xml.internal.ws.policy.EffectiveAlternativeSelector;

public class AlternativeSelector extends EffectiveAlternativeSelector
{
    public static void doSelection(final EffectivePolicyModifier modifier) throws PolicyException {
        final ValidationProcessor validationProcessor = ValidationProcessor.getInstance();
        EffectiveAlternativeSelector.selectAlternatives(modifier, validationProcessor);
    }
}
