package com.me.mdm.server.enrollment.deprovision;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

public class DeprovisionRequest
{
    private final Long customerId;
    private final Long userId;
    private int wipeType;
    private final int wipeReason;
    private final String wipeComment;
    private final List<Long> managedDeviceIDList;
    private boolean forceDeprovision;
    
    public DeprovisionRequest(final Long custID, final Long userID, final int wType, final int wReason, final String wComment, final List<Long> resourceList) throws Exception {
        if (custID == null || wComment == null || resourceList == null || resourceList.isEmpty()) {
            throw new Exception("CustomerID / Comment / Resource List cannot be empty or null");
        }
        this.customerId = custID;
        this.userId = userID;
        this.wipeType = wType;
        this.wipeReason = wReason;
        this.wipeComment = wComment;
        this.managedDeviceIDList = new ArrayList<Long>(resourceList);
        this.forceDeprovision = false;
    }
    
    public void setWipeType(final int wipeType) {
        this.wipeType = wipeType;
    }
    
    public void setForceDeprovision(final boolean flag) {
        this.forceDeprovision = flag;
    }
    
    public boolean isForceDeprovision() {
        return this.forceDeprovision;
    }
    
    public int getWipeType() {
        return this.wipeType;
    }
    
    public int getWipeReason() {
        return this.wipeReason;
    }
    
    public String getWipeComment() {
        return this.wipeComment;
    }
    
    public Long getCustomerId() {
        return this.customerId;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public List<Long> getManagedDeviceIDList() {
        return this.managedDeviceIDList;
    }
    
    @Override
    public String toString() {
        return "DeprovisionRequest{customerId=" + this.customerId + ", userId=" + this.userId + ", wipeType=" + this.wipeType + ", wipeReason=" + this.wipeReason + ", wipeComment=" + this.wipeComment + ", managedDeviceIDList=" + this.managedDeviceIDList + '}';
    }
}
