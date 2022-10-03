package com.sun.xml.internal.ws.policy.subject;

import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import javax.xml.namespace.QName;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public class PolicyMapKeyConverter
{
    private static final PolicyLogger LOGGER;
    private final QName serviceName;
    private final QName portName;
    
    public PolicyMapKeyConverter(final QName serviceName, final QName portName) {
        this.serviceName = serviceName;
        this.portName = portName;
    }
    
    public PolicyMapKey getPolicyMapKey(final WsdlBindingSubject subject) {
        PolicyMapKeyConverter.LOGGER.entering(subject);
        PolicyMapKey key = null;
        if (subject.isBindingSubject()) {
            key = PolicyMap.createWsdlEndpointScopeKey(this.serviceName, this.portName);
        }
        else if (subject.isBindingOperationSubject()) {
            key = PolicyMap.createWsdlOperationScopeKey(this.serviceName, this.portName, subject.getName());
        }
        else if (subject.isBindingMessageSubject()) {
            if (subject.getMessageType() == WsdlBindingSubject.WsdlMessageType.FAULT) {
                key = PolicyMap.createWsdlFaultMessageScopeKey(this.serviceName, this.portName, subject.getParent().getName(), subject.getName());
            }
            else {
                key = PolicyMap.createWsdlMessageScopeKey(this.serviceName, this.portName, subject.getParent().getName());
            }
        }
        PolicyMapKeyConverter.LOGGER.exiting(key);
        return key;
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyMapKeyConverter.class);
    }
}
