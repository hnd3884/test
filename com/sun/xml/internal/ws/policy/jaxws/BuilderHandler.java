package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicySubject;
import java.util.Iterator;
import com.sun.xml.internal.ws.api.policy.ModelTranslator;
import java.util.ArrayList;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.resources.PolicyMessages;
import com.sun.xml.internal.ws.policy.PolicyException;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Map;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

abstract class BuilderHandler
{
    private static final PolicyLogger LOGGER;
    Map<String, PolicySourceModel> policyStore;
    Collection<String> policyURIs;
    Object policySubject;
    
    BuilderHandler(final Collection<String> policyURIs, final Map<String, PolicySourceModel> policyStore, final Object policySubject) {
        this.policyStore = policyStore;
        this.policyURIs = policyURIs;
        this.policySubject = policySubject;
    }
    
    final void populate(final PolicyMapExtender policyMapExtender) throws PolicyException {
        if (null == policyMapExtender) {
            throw BuilderHandler.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1006_POLICY_MAP_EXTENDER_CAN_NOT_BE_NULL()));
        }
        this.doPopulate(policyMapExtender);
    }
    
    protected abstract void doPopulate(final PolicyMapExtender p0) throws PolicyException;
    
    final Collection<Policy> getPolicies() throws PolicyException {
        if (null == this.policyURIs) {
            throw BuilderHandler.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1004_POLICY_URIS_CAN_NOT_BE_NULL()));
        }
        if (null == this.policyStore) {
            throw BuilderHandler.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1010_NO_POLICIES_DEFINED()));
        }
        final Collection<Policy> result = new ArrayList<Policy>(this.policyURIs.size());
        for (final String policyURI : this.policyURIs) {
            final PolicySourceModel sourceModel = this.policyStore.get(policyURI);
            if (sourceModel == null) {
                throw BuilderHandler.LOGGER.logSevereException(new PolicyException(PolicyMessages.WSP_1005_POLICY_REFERENCE_DOES_NOT_EXIST(policyURI)));
            }
            result.add(ModelTranslator.getTranslator().translate(sourceModel));
        }
        return result;
    }
    
    final Collection<PolicySubject> getPolicySubjects() throws PolicyException {
        final Collection<Policy> policies = this.getPolicies();
        final Collection<PolicySubject> result = new ArrayList<PolicySubject>(policies.size());
        for (final Policy policy : policies) {
            result.add(new PolicySubject(this.policySubject, policy));
        }
        return result;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(BuilderHandler.class);
    }
}
