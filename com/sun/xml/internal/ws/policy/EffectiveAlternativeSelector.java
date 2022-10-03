package com.sun.xml.internal.ws.policy;

import java.util.Collection;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class EffectiveAlternativeSelector
{
    private static final PolicyLogger LOGGER;
    
    public static void doSelection(final EffectivePolicyModifier modifier) throws PolicyException {
        final AssertionValidationProcessor validationProcessor = AssertionValidationProcessor.getInstance();
        selectAlternatives(modifier, validationProcessor);
    }
    
    protected static void selectAlternatives(final EffectivePolicyModifier modifier, final AssertionValidationProcessor validationProcessor) throws PolicyException {
        final PolicyMap map = modifier.getMap();
        for (final PolicyMapKey mapKey : map.getAllServiceScopeKeys()) {
            final Policy oldPolicy = map.getServiceEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForServiceScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (final PolicyMapKey mapKey : map.getAllEndpointScopeKeys()) {
            final Policy oldPolicy = map.getEndpointEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForEndpointScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (final PolicyMapKey mapKey : map.getAllOperationScopeKeys()) {
            final Policy oldPolicy = map.getOperationEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForOperationScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (final PolicyMapKey mapKey : map.getAllInputMessageScopeKeys()) {
            final Policy oldPolicy = map.getInputMessageEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForInputMessageScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (final PolicyMapKey mapKey : map.getAllOutputMessageScopeKeys()) {
            final Policy oldPolicy = map.getOutputMessageEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForOutputMessageScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
        }
        for (final PolicyMapKey mapKey : map.getAllFaultMessageScopeKeys()) {
            final Policy oldPolicy = map.getFaultMessageEffectivePolicy(mapKey);
            modifier.setNewEffectivePolicyForFaultMessageScope(mapKey, selectBestAlternative(oldPolicy, validationProcessor));
        }
    }
    
    private static Policy selectBestAlternative(final Policy policy, final AssertionValidationProcessor validationProcessor) throws PolicyException {
        AssertionSet bestAlternative = null;
        AlternativeFitness bestAlternativeFitness = AlternativeFitness.UNEVALUATED;
        for (final AssertionSet alternative : policy) {
            AlternativeFitness alternativeFitness = alternative.isEmpty() ? AlternativeFitness.SUPPORTED_EMPTY : AlternativeFitness.UNEVALUATED;
            for (final PolicyAssertion assertion : alternative) {
                final PolicyAssertionValidator.Fitness assertionFitness = validationProcessor.validateClientSide(assertion);
                switch (assertionFitness) {
                    case UNKNOWN:
                    case UNSUPPORTED:
                    case INVALID: {
                        EffectiveAlternativeSelector.LOGGER.warning(LocalizationMessages.WSP_0075_PROBLEMATIC_ASSERTION_STATE(assertion.getName(), assertionFitness));
                        break;
                    }
                }
                alternativeFitness = alternativeFitness.combine(assertionFitness);
            }
            if (bestAlternativeFitness.compareTo(alternativeFitness) < 0) {
                bestAlternative = alternative;
                bestAlternativeFitness = alternativeFitness;
            }
            if (bestAlternativeFitness == AlternativeFitness.SUPPORTED) {
                break;
            }
        }
        switch (bestAlternativeFitness) {
            case INVALID: {
                throw EffectiveAlternativeSelector.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0053_INVALID_CLIENT_SIDE_ALTERNATIVE()));
            }
            case UNKNOWN:
            case UNSUPPORTED:
            case PARTIALLY_SUPPORTED: {
                EffectiveAlternativeSelector.LOGGER.warning(LocalizationMessages.WSP_0019_SUBOPTIMAL_ALTERNATIVE_SELECTED(bestAlternativeFitness));
                break;
            }
        }
        Collection<AssertionSet> alternativeSet = null;
        if (bestAlternative != null) {
            alternativeSet = new LinkedList<AssertionSet>();
            alternativeSet.add(bestAlternative);
        }
        return Policy.createPolicy(policy.getNamespaceVersion(), policy.getName(), policy.getId(), alternativeSet);
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(EffectiveAlternativeSelector.class);
    }
    
    private enum AlternativeFitness
    {
        UNEVALUATED {
            @Override
            AlternativeFitness combine(final PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN: {
                        return EffectiveAlternativeSelector$AlternativeFitness$1.UNKNOWN;
                    }
                    case UNSUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$1.UNSUPPORTED;
                    }
                    case SUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$1.SUPPORTED;
                    }
                    case INVALID: {
                        return EffectiveAlternativeSelector$AlternativeFitness$1.INVALID;
                    }
                    default: {
                        return EffectiveAlternativeSelector$AlternativeFitness$1.UNEVALUATED;
                    }
                }
            }
        }, 
        INVALID {
            @Override
            AlternativeFitness combine(final PolicyAssertionValidator.Fitness assertionFitness) {
                return EffectiveAlternativeSelector$AlternativeFitness$2.INVALID;
            }
        }, 
        UNKNOWN {
            @Override
            AlternativeFitness combine(final PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN: {
                        return EffectiveAlternativeSelector$AlternativeFitness$3.UNKNOWN;
                    }
                    case UNSUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$3.UNSUPPORTED;
                    }
                    case SUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$3.PARTIALLY_SUPPORTED;
                    }
                    case INVALID: {
                        return EffectiveAlternativeSelector$AlternativeFitness$3.INVALID;
                    }
                    default: {
                        return EffectiveAlternativeSelector$AlternativeFitness$3.UNEVALUATED;
                    }
                }
            }
        }, 
        UNSUPPORTED {
            @Override
            AlternativeFitness combine(final PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN:
                    case UNSUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$4.UNSUPPORTED;
                    }
                    case SUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$4.PARTIALLY_SUPPORTED;
                    }
                    case INVALID: {
                        return EffectiveAlternativeSelector$AlternativeFitness$4.INVALID;
                    }
                    default: {
                        return EffectiveAlternativeSelector$AlternativeFitness$4.UNEVALUATED;
                    }
                }
            }
        }, 
        PARTIALLY_SUPPORTED {
            @Override
            AlternativeFitness combine(final PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN:
                    case UNSUPPORTED:
                    case SUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$5.PARTIALLY_SUPPORTED;
                    }
                    case INVALID: {
                        return EffectiveAlternativeSelector$AlternativeFitness$5.INVALID;
                    }
                    default: {
                        return EffectiveAlternativeSelector$AlternativeFitness$5.UNEVALUATED;
                    }
                }
            }
        }, 
        SUPPORTED_EMPTY {
            @Override
            AlternativeFitness combine(final PolicyAssertionValidator.Fitness assertionFitness) {
                throw new UnsupportedOperationException("Combine operation was called unexpectedly on 'SUPPORTED_EMPTY' alternative fitness enumeration state.");
            }
        }, 
        SUPPORTED {
            @Override
            AlternativeFitness combine(final PolicyAssertionValidator.Fitness assertionFitness) {
                switch (assertionFitness) {
                    case UNKNOWN:
                    case UNSUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$7.PARTIALLY_SUPPORTED;
                    }
                    case SUPPORTED: {
                        return EffectiveAlternativeSelector$AlternativeFitness$7.SUPPORTED;
                    }
                    case INVALID: {
                        return EffectiveAlternativeSelector$AlternativeFitness$7.INVALID;
                    }
                    default: {
                        return EffectiveAlternativeSelector$AlternativeFitness$7.UNEVALUATED;
                    }
                }
            }
        };
        
        abstract AlternativeFitness combine(final PolicyAssertionValidator.Fitness p0);
    }
}
