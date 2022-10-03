package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchUpdateUserPolicyResponse extends GenericJson
{
    @Key
    private List<UpdateFailureInfo> failedUsers;
    @Key
    private List<String> successUserIds;
    
    public List<UpdateFailureInfo> getFailedUsers() {
        return this.failedUsers;
    }
    
    public BatchUpdateUserPolicyResponse setFailedUsers(final List<UpdateFailureInfo> failedUsers) {
        this.failedUsers = failedUsers;
        return this;
    }
    
    public List<String> getSuccessUserIds() {
        return this.successUserIds;
    }
    
    public BatchUpdateUserPolicyResponse setSuccessUserIds(final List<String> successUserIds) {
        this.successUserIds = successUserIds;
        return this;
    }
    
    public BatchUpdateUserPolicyResponse set(final String s, final Object o) {
        return (BatchUpdateUserPolicyResponse)super.set(s, o);
    }
    
    public BatchUpdateUserPolicyResponse clone() {
        return (BatchUpdateUserPolicyResponse)super.clone();
    }
}
