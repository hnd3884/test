package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchDeleteUserPolicyRequest extends GenericJson
{
    @Key
    private List<String> userIds;
    
    public List<String> getUserIds() {
        return this.userIds;
    }
    
    public BatchDeleteUserPolicyRequest setUserIds(final List<String> userIds) {
        this.userIds = userIds;
        return this;
    }
    
    public BatchDeleteUserPolicyRequest set(final String s, final Object o) {
        return (BatchDeleteUserPolicyRequest)super.set(s, o);
    }
    
    public BatchDeleteUserPolicyRequest clone() {
        return (BatchDeleteUserPolicyRequest)super.clone();
    }
}
