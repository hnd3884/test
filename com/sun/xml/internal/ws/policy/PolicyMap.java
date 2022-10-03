package com.sun.xml.internal.ws.policy;

import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import java.util.List;
import java.util.LinkedList;
import com.sun.xml.internal.ws.policy.privateutil.LocalizationMessages;
import java.util.Iterator;
import java.util.Collection;
import com.sun.xml.internal.ws.policy.privateutil.PolicyLogger;

public final class PolicyMap implements Iterable<Policy>
{
    private static final PolicyLogger LOGGER;
    private static final PolicyMapKeyHandler serviceKeyHandler;
    private static final PolicyMapKeyHandler endpointKeyHandler;
    private static final PolicyMapKeyHandler operationAndInputOutputMessageKeyHandler;
    private static final PolicyMapKeyHandler faultMessageHandler;
    private static final PolicyMerger merger;
    private final ScopeMap serviceMap;
    private final ScopeMap endpointMap;
    private final ScopeMap operationMap;
    private final ScopeMap inputMessageMap;
    private final ScopeMap outputMessageMap;
    private final ScopeMap faultMessageMap;
    
    private PolicyMap() {
        this.serviceMap = new ScopeMap(PolicyMap.merger, PolicyMap.serviceKeyHandler);
        this.endpointMap = new ScopeMap(PolicyMap.merger, PolicyMap.endpointKeyHandler);
        this.operationMap = new ScopeMap(PolicyMap.merger, PolicyMap.operationAndInputOutputMessageKeyHandler);
        this.inputMessageMap = new ScopeMap(PolicyMap.merger, PolicyMap.operationAndInputOutputMessageKeyHandler);
        this.outputMessageMap = new ScopeMap(PolicyMap.merger, PolicyMap.operationAndInputOutputMessageKeyHandler);
        this.faultMessageMap = new ScopeMap(PolicyMap.merger, PolicyMap.faultMessageHandler);
    }
    
    public static PolicyMap createPolicyMap(final Collection<? extends PolicyMapMutator> mutators) {
        final PolicyMap result = new PolicyMap();
        if (mutators != null && !mutators.isEmpty()) {
            for (final PolicyMapMutator mutator : mutators) {
                mutator.connect(result);
            }
        }
        return result;
    }
    
    public Policy getServiceEffectivePolicy(final PolicyMapKey key) throws PolicyException {
        return this.serviceMap.getEffectivePolicy(key);
    }
    
    public Policy getEndpointEffectivePolicy(final PolicyMapKey key) throws PolicyException {
        return this.endpointMap.getEffectivePolicy(key);
    }
    
    public Policy getOperationEffectivePolicy(final PolicyMapKey key) throws PolicyException {
        return this.operationMap.getEffectivePolicy(key);
    }
    
    public Policy getInputMessageEffectivePolicy(final PolicyMapKey key) throws PolicyException {
        return this.inputMessageMap.getEffectivePolicy(key);
    }
    
    public Policy getOutputMessageEffectivePolicy(final PolicyMapKey key) throws PolicyException {
        return this.outputMessageMap.getEffectivePolicy(key);
    }
    
    public Policy getFaultMessageEffectivePolicy(final PolicyMapKey key) throws PolicyException {
        return this.faultMessageMap.getEffectivePolicy(key);
    }
    
    public Collection<PolicyMapKey> getAllServiceScopeKeys() {
        return this.serviceMap.getAllKeys();
    }
    
    public Collection<PolicyMapKey> getAllEndpointScopeKeys() {
        return this.endpointMap.getAllKeys();
    }
    
    public Collection<PolicyMapKey> getAllOperationScopeKeys() {
        return this.operationMap.getAllKeys();
    }
    
    public Collection<PolicyMapKey> getAllInputMessageScopeKeys() {
        return this.inputMessageMap.getAllKeys();
    }
    
    public Collection<PolicyMapKey> getAllOutputMessageScopeKeys() {
        return this.outputMessageMap.getAllKeys();
    }
    
    public Collection<PolicyMapKey> getAllFaultMessageScopeKeys() {
        return this.faultMessageMap.getAllKeys();
    }
    
    void putSubject(final ScopeType scopeType, final PolicyMapKey key, final PolicySubject subject) {
        switch (scopeType) {
            case SERVICE: {
                this.serviceMap.putSubject(key, subject);
                break;
            }
            case ENDPOINT: {
                this.endpointMap.putSubject(key, subject);
                break;
            }
            case OPERATION: {
                this.operationMap.putSubject(key, subject);
                break;
            }
            case INPUT_MESSAGE: {
                this.inputMessageMap.putSubject(key, subject);
                break;
            }
            case OUTPUT_MESSAGE: {
                this.outputMessageMap.putSubject(key, subject);
                break;
            }
            case FAULT_MESSAGE: {
                this.faultMessageMap.putSubject(key, subject);
                break;
            }
            default: {
                throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(scopeType)));
            }
        }
    }
    
    void setNewEffectivePolicyForScope(final ScopeType scopeType, final PolicyMapKey key, final Policy newEffectivePolicy) throws IllegalArgumentException {
        if (scopeType == null || key == null || newEffectivePolicy == null) {
            throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0062_INPUT_PARAMS_MUST_NOT_BE_NULL()));
        }
        switch (scopeType) {
            case SERVICE: {
                this.serviceMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case ENDPOINT: {
                this.endpointMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case OPERATION: {
                this.operationMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case INPUT_MESSAGE: {
                this.inputMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case OUTPUT_MESSAGE: {
                this.outputMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            case FAULT_MESSAGE: {
                this.faultMessageMap.setNewEffectivePolicy(key, newEffectivePolicy);
                break;
            }
            default: {
                throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0002_UNRECOGNIZED_SCOPE_TYPE(scopeType)));
            }
        }
    }
    
    public Collection<PolicySubject> getPolicySubjects() {
        final List<PolicySubject> subjects = new LinkedList<PolicySubject>();
        this.addSubjects(subjects, this.serviceMap);
        this.addSubjects(subjects, this.endpointMap);
        this.addSubjects(subjects, this.operationMap);
        this.addSubjects(subjects, this.inputMessageMap);
        this.addSubjects(subjects, this.outputMessageMap);
        this.addSubjects(subjects, this.faultMessageMap);
        return subjects;
    }
    
    public boolean isInputMessageSubject(final PolicySubject subject) {
        for (final PolicyScope scope : this.inputMessageMap.getStoredScopes()) {
            if (scope.getPolicySubjects().contains(subject)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isOutputMessageSubject(final PolicySubject subject) {
        for (final PolicyScope scope : this.outputMessageMap.getStoredScopes()) {
            if (scope.getPolicySubjects().contains(subject)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isFaultMessageSubject(final PolicySubject subject) {
        for (final PolicyScope scope : this.faultMessageMap.getStoredScopes()) {
            if (scope.getPolicySubjects().contains(subject)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isEmpty() {
        return this.serviceMap.isEmpty() && this.endpointMap.isEmpty() && this.operationMap.isEmpty() && this.inputMessageMap.isEmpty() && this.outputMessageMap.isEmpty() && this.faultMessageMap.isEmpty();
    }
    
    private void addSubjects(final Collection<PolicySubject> subjects, final ScopeMap scopeMap) {
        for (final PolicyScope scope : scopeMap.getStoredScopes()) {
            final Collection<PolicySubject> scopedSubjects = scope.getPolicySubjects();
            subjects.addAll(scopedSubjects);
        }
    }
    
    public static PolicyMapKey createWsdlServiceScopeKey(final QName service) throws IllegalArgumentException {
        if (service == null) {
            throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0031_SERVICE_PARAM_MUST_NOT_BE_NULL()));
        }
        return new PolicyMapKey(service, null, null, PolicyMap.serviceKeyHandler);
    }
    
    public static PolicyMapKey createWsdlEndpointScopeKey(final QName service, final QName port) throws IllegalArgumentException {
        if (service == null || port == null) {
            throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0033_SERVICE_AND_PORT_PARAM_MUST_NOT_BE_NULL(service, port)));
        }
        return new PolicyMapKey(service, port, null, PolicyMap.endpointKeyHandler);
    }
    
    public static PolicyMapKey createWsdlOperationScopeKey(final QName service, final QName port, final QName operation) throws IllegalArgumentException {
        return createOperationOrInputOutputMessageKey(service, port, operation);
    }
    
    public static PolicyMapKey createWsdlMessageScopeKey(final QName service, final QName port, final QName operation) throws IllegalArgumentException {
        return createOperationOrInputOutputMessageKey(service, port, operation);
    }
    
    public static PolicyMapKey createWsdlFaultMessageScopeKey(final QName service, final QName port, final QName operation, final QName fault) throws IllegalArgumentException {
        if (service == null || port == null || operation == null || fault == null) {
            throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0030_SERVICE_PORT_OPERATION_FAULT_MSG_PARAM_MUST_NOT_BE_NULL(service, port, operation, fault)));
        }
        return new PolicyMapKey(service, port, operation, fault, PolicyMap.faultMessageHandler);
    }
    
    private static PolicyMapKey createOperationOrInputOutputMessageKey(final QName service, final QName port, final QName operation) {
        if (service == null || port == null || operation == null) {
            throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0029_SERVICE_PORT_OPERATION_PARAM_MUST_NOT_BE_NULL(service, port, operation)));
        }
        return new PolicyMapKey(service, port, operation, PolicyMap.operationAndInputOutputMessageKeyHandler);
    }
    
    @Override
    public String toString() {
        final StringBuffer result = new StringBuffer();
        if (null != this.serviceMap) {
            result.append("\nServiceMap=").append(this.serviceMap);
        }
        if (null != this.endpointMap) {
            result.append("\nEndpointMap=").append(this.endpointMap);
        }
        if (null != this.operationMap) {
            result.append("\nOperationMap=").append(this.operationMap);
        }
        if (null != this.inputMessageMap) {
            result.append("\nInputMessageMap=").append(this.inputMessageMap);
        }
        if (null != this.outputMessageMap) {
            result.append("\nOutputMessageMap=").append(this.outputMessageMap);
        }
        if (null != this.faultMessageMap) {
            result.append("\nFaultMessageMap=").append(this.faultMessageMap);
        }
        return result.toString();
    }
    
    @Override
    public Iterator<Policy> iterator() {
        return new Iterator<Policy>() {
            private final Iterator<Iterator<Policy>> mainIterator;
            private Iterator<Policy> currentScopeIterator;
            
            {
                final Collection<Iterator<Policy>> scopeIterators = new ArrayList<Iterator<Policy>>(6);
                scopeIterators.add(PolicyMap.this.serviceMap.iterator());
                scopeIterators.add(PolicyMap.this.endpointMap.iterator());
                scopeIterators.add(PolicyMap.this.operationMap.iterator());
                scopeIterators.add(PolicyMap.this.inputMessageMap.iterator());
                scopeIterators.add(PolicyMap.this.outputMessageMap.iterator());
                scopeIterators.add(PolicyMap.this.faultMessageMap.iterator());
                this.mainIterator = scopeIterators.iterator();
                this.currentScopeIterator = this.mainIterator.next();
            }
            
            @Override
            public boolean hasNext() {
                while (!this.currentScopeIterator.hasNext()) {
                    if (!this.mainIterator.hasNext()) {
                        return false;
                    }
                    this.currentScopeIterator = this.mainIterator.next();
                }
                return true;
            }
            
            @Override
            public Policy next() {
                if (this.hasNext()) {
                    return this.currentScopeIterator.next();
                }
                throw PolicyMap.LOGGER.logSevereException(new NoSuchElementException(LocalizationMessages.WSP_0054_NO_MORE_ELEMS_IN_POLICY_MAP()));
            }
            
            @Override
            public void remove() {
                throw PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED()));
            }
        };
    }
    
    static {
        LOGGER = PolicyLogger.getLogger(PolicyMap.class);
        serviceKeyHandler = new PolicyMapKeyHandler() {
            @Override
            public boolean areEqual(final PolicyMapKey key1, final PolicyMapKey key2) {
                return key1.getService().equals(key2.getService());
            }
            
            @Override
            public int generateHashCode(final PolicyMapKey key) {
                int result = 17;
                result = 37 * result + key.getService().hashCode();
                return result;
            }
        };
        endpointKeyHandler = new PolicyMapKeyHandler() {
            @Override
            public boolean areEqual(final PolicyMapKey key1, final PolicyMapKey key2) {
                boolean retVal = true;
                retVal = (retVal && key1.getService().equals(key2.getService()));
                retVal = (retVal && ((key1.getPort() != null) ? key1.getPort().equals(key2.getPort()) : (key2.getPort() == null)));
                return retVal;
            }
            
            @Override
            public int generateHashCode(final PolicyMapKey key) {
                int result = 17;
                result = 37 * result + key.getService().hashCode();
                result = 37 * result + ((key.getPort() == null) ? 0 : key.getPort().hashCode());
                return result;
            }
        };
        operationAndInputOutputMessageKeyHandler = new PolicyMapKeyHandler() {
            @Override
            public boolean areEqual(final PolicyMapKey key1, final PolicyMapKey key2) {
                boolean retVal = true;
                retVal = (retVal && key1.getService().equals(key2.getService()));
                retVal = (retVal && ((key1.getPort() != null) ? key1.getPort().equals(key2.getPort()) : (key2.getPort() == null)));
                retVal = (retVal && ((key1.getOperation() != null) ? key1.getOperation().equals(key2.getOperation()) : (key2.getOperation() == null)));
                return retVal;
            }
            
            @Override
            public int generateHashCode(final PolicyMapKey key) {
                int result = 17;
                result = 37 * result + key.getService().hashCode();
                result = 37 * result + ((key.getPort() == null) ? 0 : key.getPort().hashCode());
                result = 37 * result + ((key.getOperation() == null) ? 0 : key.getOperation().hashCode());
                return result;
            }
        };
        faultMessageHandler = new PolicyMapKeyHandler() {
            @Override
            public boolean areEqual(final PolicyMapKey key1, final PolicyMapKey key2) {
                boolean retVal = true;
                retVal = (retVal && key1.getService().equals(key2.getService()));
                retVal = (retVal && ((key1.getPort() != null) ? key1.getPort().equals(key2.getPort()) : (key2.getPort() == null)));
                retVal = (retVal && ((key1.getOperation() != null) ? key1.getOperation().equals(key2.getOperation()) : (key2.getOperation() == null)));
                retVal = (retVal && ((key1.getFaultMessage() != null) ? key1.getFaultMessage().equals(key2.getFaultMessage()) : (key2.getFaultMessage() == null)));
                return retVal;
            }
            
            @Override
            public int generateHashCode(final PolicyMapKey key) {
                int result = 17;
                result = 37 * result + key.getService().hashCode();
                result = 37 * result + ((key.getPort() == null) ? 0 : key.getPort().hashCode());
                result = 37 * result + ((key.getOperation() == null) ? 0 : key.getOperation().hashCode());
                result = 37 * result + ((key.getFaultMessage() == null) ? 0 : key.getFaultMessage().hashCode());
                return result;
            }
        };
        merger = PolicyMerger.getMerger();
    }
    
    enum ScopeType
    {
        SERVICE, 
        ENDPOINT, 
        OPERATION, 
        INPUT_MESSAGE, 
        OUTPUT_MESSAGE, 
        FAULT_MESSAGE;
    }
    
    private static final class ScopeMap implements Iterable<Policy>
    {
        private final Map<PolicyMapKey, PolicyScope> internalMap;
        private final PolicyMapKeyHandler scopeKeyHandler;
        private final PolicyMerger merger;
        
        ScopeMap(final PolicyMerger merger, final PolicyMapKeyHandler scopeKeyHandler) {
            this.internalMap = new HashMap<PolicyMapKey, PolicyScope>();
            this.merger = merger;
            this.scopeKeyHandler = scopeKeyHandler;
        }
        
        Policy getEffectivePolicy(final PolicyMapKey key) throws PolicyException {
            final PolicyScope scope = this.internalMap.get(this.createLocalCopy(key));
            return (scope == null) ? null : scope.getEffectivePolicy(this.merger);
        }
        
        void putSubject(final PolicyMapKey key, final PolicySubject subject) {
            final PolicyMapKey localKey = this.createLocalCopy(key);
            final PolicyScope scope = this.internalMap.get(localKey);
            if (scope == null) {
                final List<PolicySubject> list = new LinkedList<PolicySubject>();
                list.add(subject);
                this.internalMap.put(localKey, new PolicyScope(list));
            }
            else {
                scope.attach(subject);
            }
        }
        
        void setNewEffectivePolicy(final PolicyMapKey key, final Policy newEffectivePolicy) {
            final PolicySubject subject = new PolicySubject(key, newEffectivePolicy);
            final PolicyMapKey localKey = this.createLocalCopy(key);
            final PolicyScope scope = this.internalMap.get(localKey);
            if (scope == null) {
                final List<PolicySubject> list = new LinkedList<PolicySubject>();
                list.add(subject);
                this.internalMap.put(localKey, new PolicyScope(list));
            }
            else {
                scope.dettachAllSubjects();
                scope.attach(subject);
            }
        }
        
        Collection<PolicyScope> getStoredScopes() {
            return this.internalMap.values();
        }
        
        Set<PolicyMapKey> getAllKeys() {
            return this.internalMap.keySet();
        }
        
        private PolicyMapKey createLocalCopy(final PolicyMapKey key) {
            if (key == null) {
                throw PolicyMap.LOGGER.logSevereException(new IllegalArgumentException(LocalizationMessages.WSP_0045_POLICY_MAP_KEY_MUST_NOT_BE_NULL()));
            }
            final PolicyMapKey localKeyCopy = new PolicyMapKey(key);
            localKeyCopy.setHandler(this.scopeKeyHandler);
            return localKeyCopy;
        }
        
        @Override
        public Iterator<Policy> iterator() {
            return new Iterator<Policy>() {
                private final Iterator<PolicyMapKey> keysIterator = ScopeMap.this.internalMap.keySet().iterator();
                
                @Override
                public boolean hasNext() {
                    return this.keysIterator.hasNext();
                }
                
                @Override
                public Policy next() {
                    final PolicyMapKey key = this.keysIterator.next();
                    try {
                        return ScopeMap.this.getEffectivePolicy(key);
                    }
                    catch (final PolicyException e) {
                        throw PolicyMap.LOGGER.logSevereException(new IllegalStateException(LocalizationMessages.WSP_0069_EXCEPTION_WHILE_RETRIEVING_EFFECTIVE_POLICY_FOR_KEY(key), e));
                    }
                }
                
                @Override
                public void remove() {
                    throw PolicyMap.LOGGER.logSevereException(new UnsupportedOperationException(LocalizationMessages.WSP_0034_REMOVE_OPERATION_NOT_SUPPORTED()));
                }
            };
        }
        
        public boolean isEmpty() {
            return this.internalMap.isEmpty();
        }
        
        @Override
        public String toString() {
            return this.internalMap.toString();
        }
    }
}
