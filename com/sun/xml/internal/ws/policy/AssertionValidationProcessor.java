package com.sun.xml.internal.ws.policy;

import java.util.Iterator;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class AssertionValidationProcessor
{
    private static final PolicyLogger LOGGER;
    private final Collection<PolicyAssertionValidator> validators;
    
    private AssertionValidationProcessor() throws PolicyException {
        this(null);
    }
    
    protected AssertionValidationProcessor(final Collection<PolicyAssertionValidator> policyValidators) throws PolicyException {
        this.validators = new LinkedList<PolicyAssertionValidator>();
        for (final PolicyAssertionValidator validator : PolicyUtils.ServiceProvider.load(PolicyAssertionValidator.class)) {
            this.validators.add(validator);
        }
        if (policyValidators != null) {
            for (final PolicyAssertionValidator validator2 : policyValidators) {
                this.validators.add(validator2);
            }
        }
        if (this.validators.size() == 0) {
            throw AssertionValidationProcessor.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0076_NO_SERVICE_PROVIDERS_FOUND(PolicyAssertionValidator.class.getName())));
        }
    }
    
    public static AssertionValidationProcessor getInstance() throws PolicyException {
        return new AssertionValidationProcessor();
    }
    
    public PolicyAssertionValidator.Fitness validateClientSide(final PolicyAssertion assertion) throws PolicyException {
        PolicyAssertionValidator.Fitness assertionFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
        for (final PolicyAssertionValidator validator : this.validators) {
            assertionFitness = assertionFitness.combine(validator.validateClientSide(assertion));
            if (assertionFitness == PolicyAssertionValidator.Fitness.SUPPORTED) {
                break;
            }
        }
        return assertionFitness;
    }
    
    public PolicyAssertionValidator.Fitness validateServerSide(final PolicyAssertion assertion) throws PolicyException {
        PolicyAssertionValidator.Fitness assertionFitness = PolicyAssertionValidator.Fitness.UNKNOWN;
        for (final PolicyAssertionValidator validator : this.validators) {
            assertionFitness = assertionFitness.combine(validator.validateServerSide(assertion));
            if (assertionFitness == PolicyAssertionValidator.Fitness.SUPPORTED) {
                break;
            }
        }
        return assertionFitness;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(AssertionValidationProcessor.class);
    }
}
