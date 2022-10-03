package com.sun.xml.internal.ws.policy.sourcemodel;

import java.util.HashMap;
import java.net.URI;
import java.util.Map;

public final class PolicySourceModelContext
{
    Map<URI, PolicySourceModel> policyModels;
    
    private PolicySourceModelContext() {
    }
    
    private Map<URI, PolicySourceModel> getModels() {
        if (null == this.policyModels) {
            this.policyModels = new HashMap<URI, PolicySourceModel>();
        }
        return this.policyModels;
    }
    
    public void addModel(final URI modelUri, final PolicySourceModel model) {
        this.getModels().put(modelUri, model);
    }
    
    public static PolicySourceModelContext createContext() {
        return new PolicySourceModelContext();
    }
    
    public boolean containsModel(final URI modelUri) {
        return this.getModels().containsKey(modelUri);
    }
    
    PolicySourceModel retrieveModel(final URI modelUri) {
        return this.getModels().get(modelUri);
    }
    
    PolicySourceModel retrieveModel(final URI modelUri, final URI digestAlgorithm, final String digest) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public String toString() {
        return "PolicySourceModelContext: policyModels = " + this.policyModels;
    }
}
