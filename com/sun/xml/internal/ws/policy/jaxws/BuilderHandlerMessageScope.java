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

final class BuilderHandlerMessageScope extends BuilderHandler
{
    private final QName service;
    private final QName port;
    private final QName operation;
    private final QName message;
    private final Scope scope;
    
    BuilderHandlerMessageScope(final Collection<String> policyURIs, final Map<String, PolicySourceModel> policyStore, final Object policySubject, final Scope scope, final QName service, final QName port, final QName operation, final QName message) {
        super(policyURIs, policyStore, policySubject);
        this.service = service;
        this.port = port;
        this.operation = operation;
        this.scope = scope;
        this.message = message;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuilderHandlerMessageScope)) {
            return false;
        }
        final BuilderHandlerMessageScope that = (BuilderHandlerMessageScope)obj;
        boolean result = true;
        result = (result && ((this.policySubject != null) ? this.policySubject.equals(that.policySubject) : (that.policySubject == null)));
        result = (result && ((this.scope != null) ? this.scope.equals(that.scope) : (that.scope == null)));
        result = (result && ((this.message != null) ? this.message.equals(that.message) : (that.message == null)));
        if (this.scope != Scope.FaultMessageScope) {
            result = (result && ((this.service != null) ? this.service.equals(that.service) : (that.service == null)));
            result = (result && ((this.port != null) ? this.port.equals(that.port) : (that.port == null)));
            result = (result && ((this.operation != null) ? this.operation.equals(that.operation) : (that.operation == null)));
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        int hashCode = 19;
        hashCode = 31 * hashCode + ((this.policySubject == null) ? 0 : this.policySubject.hashCode());
        hashCode = 31 * hashCode + ((this.message == null) ? 0 : this.message.hashCode());
        hashCode = 31 * hashCode + ((this.scope == null) ? 0 : this.scope.hashCode());
        if (this.scope != Scope.FaultMessageScope) {
            hashCode = 31 * hashCode + ((this.service == null) ? 0 : this.service.hashCode());
            hashCode = 31 * hashCode + ((this.port == null) ? 0 : this.port.hashCode());
            hashCode = 31 * hashCode + ((this.operation == null) ? 0 : this.operation.hashCode());
        }
        return hashCode;
    }
    
    @Override
    protected void doPopulate(final PolicyMapExtender policyMapExtender) throws PolicyException {
        PolicyMapKey mapKey;
        if (Scope.FaultMessageScope == this.scope) {
            mapKey = PolicyMap.createWsdlFaultMessageScopeKey(this.service, this.port, this.operation, this.message);
        }
        else {
            mapKey = PolicyMap.createWsdlMessageScopeKey(this.service, this.port, this.operation);
        }
        if (Scope.InputMessageScope == this.scope) {
            for (final PolicySubject subject : this.getPolicySubjects()) {
                policyMapExtender.putInputMessageSubject(mapKey, subject);
            }
        }
        else if (Scope.OutputMessageScope == this.scope) {
            for (final PolicySubject subject : this.getPolicySubjects()) {
                policyMapExtender.putOutputMessageSubject(mapKey, subject);
            }
        }
        else if (Scope.FaultMessageScope == this.scope) {
            for (final PolicySubject subject : this.getPolicySubjects()) {
                policyMapExtender.putFaultMessageSubject(mapKey, subject);
            }
        }
    }
    
    enum Scope
    {
        InputMessageScope, 
        OutputMessageScope, 
        FaultMessageScope;
    }
}
