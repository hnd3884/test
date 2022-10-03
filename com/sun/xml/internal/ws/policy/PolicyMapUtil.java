package com.sun.xml.internal.ws.policy;

import com.sun.xml.internal.ws.policy.subject.PolicyMapKeyConverter;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.subject.WsdlBindingSubject;
import java.util.HashMap;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class PolicyMapUtil
{
    private static final PolicyLogger LOGGER;
    private static final PolicyMerger MERGER;
    
    private PolicyMapUtil() {
    }
    
    public static void rejectAlternatives(final PolicyMap map) throws PolicyException {
        for (final Policy policy : map) {
            if (policy.getNumberOfAssertionSets() > 1) {
                throw PolicyMapUtil.LOGGER.logSevereException(new PolicyException(LocalizationMessages.WSP_0035_RECONFIGURE_ALTERNATIVES(policy.getIdOrName())));
            }
        }
    }
    
    public static void insertPolicies(final PolicyMap policyMap, final Collection<PolicySubject> policySubjects, final QName serviceName, final QName portName) throws PolicyException {
        PolicyMapUtil.LOGGER.entering(policyMap, policySubjects, serviceName, portName);
        final HashMap<WsdlBindingSubject, Collection<Policy>> subjectToPolicies = new HashMap<WsdlBindingSubject, Collection<Policy>>();
        for (final PolicySubject subject : policySubjects) {
            final Object actualSubject = subject.getSubject();
            if (actualSubject instanceof WsdlBindingSubject) {
                final WsdlBindingSubject wsdlSubject = (WsdlBindingSubject)actualSubject;
                final Collection<Policy> subjectPolicies = new LinkedList<Policy>();
                subjectPolicies.add(subject.getEffectivePolicy(PolicyMapUtil.MERGER));
                final Collection<Policy> existingPolicies = subjectToPolicies.put(wsdlSubject, subjectPolicies);
                if (existingPolicies == null) {
                    continue;
                }
                subjectPolicies.addAll(existingPolicies);
            }
        }
        final PolicyMapKeyConverter converter = new PolicyMapKeyConverter(serviceName, portName);
        for (final WsdlBindingSubject wsdlSubject2 : subjectToPolicies.keySet()) {
            final PolicySubject newSubject = new PolicySubject(wsdlSubject2, subjectToPolicies.get(wsdlSubject2));
            final PolicyMapKey mapKey = converter.getPolicyMapKey(wsdlSubject2);
            if (wsdlSubject2.isBindingSubject()) {
                policyMap.putSubject(PolicyMap.ScopeType.ENDPOINT, mapKey, newSubject);
            }
            else if (wsdlSubject2.isBindingOperationSubject()) {
                policyMap.putSubject(PolicyMap.ScopeType.OPERATION, mapKey, newSubject);
            }
            else {
                if (!wsdlSubject2.isBindingMessageSubject()) {
                    continue;
                }
                switch (wsdlSubject2.getMessageType()) {
                    case INPUT: {
                        policyMap.putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, mapKey, newSubject);
                        continue;
                    }
                    case OUTPUT: {
                        policyMap.putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, mapKey, newSubject);
                        continue;
                    }
                    case FAULT: {
                        policyMap.putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, mapKey, newSubject);
                        continue;
                    }
                }
            }
        }
        PolicyMapUtil.LOGGER.exiting();
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyMapUtil.class);
        MERGER = PolicyMerger.getMerger();
    }
}
