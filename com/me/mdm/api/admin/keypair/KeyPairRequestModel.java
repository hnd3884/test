package com.me.mdm.api.admin.keypair;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyPairRequestModel
{
    @JsonProperty("featureName")
    private String featureName;
    
    public String getFeatureName() {
        return this.featureName;
    }
    
    public void setFeatureName(final String featureName) {
        this.featureName = featureName;
    }
}
