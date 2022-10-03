package com.sun.xml.internal.ws.api.policy;

import com.sun.xml.internal.ws.policy.sourcemodel.PolicySourceModel;
import com.sun.xml.internal.ws.policy.Policy;
import com.sun.xml.internal.ws.policy.sourcemodel.PolicyModelGenerator;

public abstract class ModelGenerator extends PolicyModelGenerator
{
    private static final SourceModelCreator CREATOR;
    
    private ModelGenerator() {
    }
    
    public static PolicyModelGenerator getGenerator() {
        return PolicyModelGenerator.getCompactGenerator(ModelGenerator.CREATOR);
    }
    
    static {
        CREATOR = new SourceModelCreator();
    }
    
    protected static class SourceModelCreator extends PolicySourceModelCreator
    {
        @Override
        protected PolicySourceModel create(final Policy policy) {
            return PolicySourceModel.createPolicySourceModel(policy.getNamespaceVersion(), policy.getId(), policy.getName());
        }
    }
}
