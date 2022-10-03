package com.sun.xml.internal.ws.policy.jaxws;

import com.sun.xml.internal.ws.policy.PolicyException;
import java.util.Iterator;
import com.sun.xml.internal.ws.policy.PolicyMapKey;
import com.sun.xml.internal.ws.policy.PolicySubject;
import com.sun.xml.internal.ws.policy.PolicyMap;
import com.sun.xml.internal.ws.policy.PolicyMapExtender;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import java.util.Map;
import java.util.Collection;
import javax.xml.namespace.QName;

final class BuilderHandlerOperationScope extends BuilderHandler
{
    private final QName service;
    private final QName port;
    private final QName operation;
    
    BuilderHandlerOperationScope(final Collection<String> policyURIs, final Map<String, PolicySourceModel> policyStore, final Object policySubject, final QName service, final QName port, final QName operation) {
        super(policyURIs, policyStore, policySubject);
        this.service = service;
        this.port = port;
        this.operation = operation;
    }
    
    @Override
    protected void doPopulate(final PolicyMapExtender policyMapExtender) throws PolicyException {
        final PolicyMapKey mapKey = PolicyMap.createWsdlOperationScopeKey(this.service, this.port, this.operation);
        for (final PolicySubject subject : this.getPolicySubjects()) {
            policyMapExtender.putOperationSubject(mapKey, subject);
        }
    }
}
