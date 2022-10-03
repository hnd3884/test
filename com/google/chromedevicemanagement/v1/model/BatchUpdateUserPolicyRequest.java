package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class BatchUpdateUserPolicyRequest extends GenericJson
{
    @Key
    private String updateMask;
    @Key
    private List<String> userIds;
    @Key
    private UserPolicy userPolicy;
    
    public String getUpdateMask() {
        return this.updateMask;
    }
    
    public BatchUpdateUserPolicyRequest setUpdateMask(final String updateMask) {
        this.updateMask = updateMask;
        return this;
    }
    
    public List<String> getUserIds() {
        return this.userIds;
    }
    
    public BatchUpdateUserPolicyRequest setUserIds(final List<String> userIds) {
        this.userIds = userIds;
        return this;
    }
    
    public UserPolicy getUserPolicy() {
        return this.userPolicy;
    }
    
    public BatchUpdateUserPolicyRequest setUserPolicy(final UserPolicy userPolicy) {
        this.userPolicy = userPolicy;
        return this;
    }
    
    public BatchUpdateUserPolicyRequest set(final String s, final Object o) {
        return (BatchUpdateUserPolicyRequest)super.set(s, o);
    }
    
    public BatchUpdateUserPolicyRequest clone() {
        return (BatchUpdateUserPolicyRequest)super.clone();
    }
}
