package com.sun.xml.internal.ws.policy;

public final class PolicyMapExtender extends PolicyMapMutator
{
    private PolicyMapExtender() {
    }
    
    public static PolicyMapExtender createPolicyMapExtender() {
        return new PolicyMapExtender();
    }
    
    public void putServiceSubject(final PolicyMapKey key, final PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.SERVICE, key, subject);
    }
    
    public void putEndpointSubject(final PolicyMapKey key, final PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.ENDPOINT, key, subject);
    }
    
    public void putOperationSubject(final PolicyMapKey key, final PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.OPERATION, key, subject);
    }
    
    public void putInputMessageSubject(final PolicyMapKey key, final PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.INPUT_MESSAGE, key, subject);
    }
    
    public void putOutputMessageSubject(final PolicyMapKey key, final PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.OUTPUT_MESSAGE, key, subject);
    }
    
    public void putFaultMessageSubject(final PolicyMapKey key, final PolicySubject subject) {
        this.getMap().putSubject(PolicyMap.ScopeType.FAULT_MESSAGE, key, subject);
    }
}
