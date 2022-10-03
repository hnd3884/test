package com.sun.xml.internal.ws.policy;

public final class EffectivePolicyModifier extends PolicyMapMutator
{
    public static EffectivePolicyModifier createEffectivePolicyModifier() {
        return new EffectivePolicyModifier();
    }
    
    private EffectivePolicyModifier() {
    }
    
    public void setNewEffectivePolicyForServiceScope(final PolicyMapKey key, final Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.SERVICE, key, newEffectivePolicy);
    }
    
    public void setNewEffectivePolicyForEndpointScope(final PolicyMapKey key, final Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.ENDPOINT, key, newEffectivePolicy);
    }
    
    public void setNewEffectivePolicyForOperationScope(final PolicyMapKey key, final Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OPERATION, key, newEffectivePolicy);
    }
    
    public void setNewEffectivePolicyForInputMessageScope(final PolicyMapKey key, final Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.INPUT_MESSAGE, key, newEffectivePolicy);
    }
    
    public void setNewEffectivePolicyForOutputMessageScope(final PolicyMapKey key, final Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.OUTPUT_MESSAGE, key, newEffectivePolicy);
    }
    
    public void setNewEffectivePolicyForFaultMessageScope(final PolicyMapKey key, final Policy newEffectivePolicy) {
        this.getMap().setNewEffectivePolicyForScope(PolicyMap.ScopeType.FAULT_MESSAGE, key, newEffectivePolicy);
    }
}
