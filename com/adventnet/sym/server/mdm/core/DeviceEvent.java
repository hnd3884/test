package com.adventnet.sym.server.mdm.core;

import org.json.JSONObject;

public class DeviceEvent
{
    public Long resourceID;
    public Long customerID;
    public String udid;
    public int platformType;
    public Long enrollmentRequestId;
    public JSONObject resourceJSON;
    
    public DeviceEvent() {
        this.resourceID = null;
        this.customerID = null;
        this.udid = null;
        this.enrollmentRequestId = null;
        this.resourceJSON = null;
    }
    
    public DeviceEvent(final Long resourceID) {
        this.resourceID = null;
        this.customerID = null;
        this.udid = null;
        this.enrollmentRequestId = null;
        this.resourceJSON = null;
        this.resourceID = resourceID;
    }
    
    public DeviceEvent(final Long resourceID, final Long customerID) {
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
        return "resourceID:" + this.resourceID + ",customerID:" + this.customerID + ",udid:" + this.udid + ",platformType:" + this.platformType + ",enrollmentRequestId:" + this.enrollmentRequestId + ",resourceJSON:" + this.resourceJSON;
    }
}
