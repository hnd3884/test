package com.me.mdm.server.deployment;

import java.util.Objects;
import java.util.HashMap;
import java.util.ArrayList;

public class DeploymentBean
{
    ArrayList<Long> includedResourceList;
    ArrayList<Long> excludedResourceList;
    boolean isSilentInstall;
    boolean notifyUserViaEmail;
    ArrayList<Long> appGroupList;
    Long customerId;
    HashMap<Long, Long> profileCollectionMap;
    
    public HashMap<Long, Long> getProfileCollectionMap() {
        return this.profileCollectionMap;
    }
    
    public void setProfileCollectionMap(final HashMap<Long, Long> profileCollectionMap) {
        this.profileCollectionMap = profileCollectionMap;
    }
    
    public ArrayList<Long> getIncludedResourceList() {
        return this.includedResourceList;
    }
    
    public void setIncludedResourceList(final ArrayList<Long> includedResourceList) {
        this.includedResourceList = includedResourceList;
    }
    
    public ArrayList<Long> getExcludedResourceList() {
        return this.excludedResourceList;
    }
    
    public void setExcludedResourceList(final ArrayList<Long> excludedResourceList) {
        this.excludedResourceList = excludedResourceList;
    }
    
    public boolean isSilentInstall() {
        return this.isSilentInstall;
    }
    
    public void setSilentInstall(final boolean silentInstall) {
        this.isSilentInstall = silentInstall;
    }
    
    public boolean isNotifyUserViaEmail() {
        return this.notifyUserViaEmail;
    }
    
    public void setNotifyUserViaEmail(final boolean notifyUserViaEmail) {
        this.notifyUserViaEmail = notifyUserViaEmail;
    }
    
    public ArrayList<Long> getAppGroupList() {
        return this.appGroupList;
    }
    
    public void setAppGroupList(final ArrayList<Long> appGroupList) {
        this.appGroupList = appGroupList;
    }
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final DeploymentBean that = (DeploymentBean)o;
        return this.isSilentInstall == that.isSilentInstall && this.notifyUserViaEmail == that.notifyUserViaEmail && Objects.equals(this.includedResourceList, that.includedResourceList) && Objects.equals(this.excludedResourceList, that.excludedResourceList) && Objects.equals(this.customerId, that.customerId);
    }
}
