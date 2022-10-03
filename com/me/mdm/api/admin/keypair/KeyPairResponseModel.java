package com.me.mdm.api.admin.keypair;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyPairResponseModel
{
    @JsonProperty("key")
    private String publicKey;
    
    public String getPublicKey() {
        return this.publicKey;
    }
    
    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }
}
