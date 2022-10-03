package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import java.util.List;
import com.google.api.client.json.GenericJson;

public final class BatchDeleteUserPolicyResponse extends GenericJson
{
    @Key
    private List<UpdateFailureInfo> failedUsers;
    @Key
    private List<String> successUserIds;
    
    public List<UpdateFailureInfo> getFailedUsers() {
        return this.failedUsers;
    }
    
    public BatchDeleteUserPolicyResponse setFailedUsers(final List<UpdateFailureInfo> failedUsers) {
        this.failedUsers = failedUsers;
        return this;
    }
    
    public List<String> getSuccessUserIds() {
        return this.successUserIds;
    }
    
    public BatchDeleteUserPolicyResponse setSuccessUserIds(final List<String> successUserIds) {
        this.successUserIds = successUserIds;
        return this;
    }
    
    public BatchDeleteUserPolicyResponse set(final String s, final Object o) {
        return (BatchDeleteUserPolicyResponse)super.set(s, o);
    }
    
    public BatchDeleteUserPolicyResponse clone() {
        return (BatchDeleteUserPolicyResponse)super.clone();
    }
}
