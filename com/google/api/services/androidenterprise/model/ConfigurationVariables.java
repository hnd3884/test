package com.google.api.services.androidenterprise.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class ConfigurationVariables extends GenericJson
{
    @Key
    private String mcmId;
    @Key
    private List<VariableSet> variableSet;
    
    public String getMcmId() {
        return this.mcmId;
    }
    
    public ConfigurationVariables setMcmId(final String mcmId) {
        this.mcmId = mcmId;
        return this;
    }
    
    public List<VariableSet> getVariableSet() {
        return this.variableSet;
    }
    
    public ConfigurationVariables setVariableSet(final List<VariableSet> variableSet) {
        this.variableSet = variableSet;
        return this;
    }
    
    public ConfigurationVariables set(final String fieldName, final Object value) {
        return (ConfigurationVariables)super.set(fieldName, value);
    }
    
    public ConfigurationVariables clone() {
        return (ConfigurationVariables)super.clone();
    }
}
