package com.adventnet.sym.server.mdm.core;

import java.io.Serializable;

public class DeviceEventForQueue implements Serializable
{
    public Long resourceID;
    public Long customerID;
    public String udid;
    public int platformType;
    public Long enrollmentRequestId;
    public String resourceJSON;
    
    public DeviceEventForQueue() {
        this.resourceID = null;
        this.customerID = null;
        this.udid = null;
        this.enrollmentRequestId = null;
        this.resourceJSON = null;
    }
    
    public DeviceEventForQueue(final Long resourceID) {
        this.resourceID = null;
        this.customerID = null;
        this.udid = null;
        this.enrollmentRequestId = null;
        this.resourceJSON = null;
        this.resourceID = resourceID;
    }
    
    public DeviceEventForQueue(final Long resourceID, final Long customerID) {
        this.resourceID = null;
        this.customerID = null;
        this.udid = null;
        this.enrollmentRequestId = null;
        this.resourceJSON = null;
        this.resourceID = resourceID;
        this.customerID = customerID;
    }
    
    @Override
    public String toString() {
        return this.resourceID.toString();
    }
}
