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

final class BuilderHandlerServiceScope extends BuilderHandler
{
    private final QName service;
    
    BuilderHandlerServiceScope(final Collection<String> policyURIs, final Map<String, PolicySourceModel> policyStore, final Object policySubject, final QName service) {
        super(policyURIs, policyStore, policySubject);
        this.service = service;
    }
    
    @Override
    protected void doPopulate(final PolicyMapExtender policyMapExtender) throws PolicyException {
        final PolicyMapKey mapKey = PolicyMap.createWsdlServiceScopeKey(this.service);
        for (final PolicySubject subject : this.getPolicySubjects()) {
            policyMapExtender.putServiceSubject(mapKey, subject);
        }
    }
    
    @Override
    public String toString() {
        return this.service.toString();
    }
}
