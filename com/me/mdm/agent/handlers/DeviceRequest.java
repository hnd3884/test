package com.me.mdm.agent.handlers;

import com.me.devicemanagement.framework.server.util.DBUtil;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import java.util.HashMap;

public class DeviceRequest
{
    public Object deviceRequestData;
    public byte[] deviceRequestDatabytes;
    public int devicePlatform;
    public String deviceRequestType;
    public String deviceUDID;
    public Long customerID;
    public Long resourceID;
    public int repositoryType;
    public HashMap requestMap;
    public HashMap headerMap;
    
    public DeviceRequest() {
        this.deviceRequestData = null;
        this.deviceRequestDatabytes = null;
        this.devicePlatform = -1;
        this.deviceRequestType = null;
        this.deviceUDID = null;
        this.customerID = null;
        this.resourceID = null;
        this.repositoryType = -1;
        this.requestMap = null;
        this.headerMap = null;
    }
    
    public void initDeviceRequest(final String deviceUDID) throws Exception {
        this.deviceUDID = deviceUDID;
        this.resourceID = ManagedDeviceHandler.getInstance().getResourceIDFromUDID(deviceUDID);
        if (this.resourceID == null) {
            return;
        }
        this.customerID = CustomerInfoUtil.getInstance().getCustomerIDForResID(this.resourceID);
        this.devicePlatform = (int)DBUtil.getValueFromDB("ManagedDevice", "UDID", (Object)deviceUDID, "PLATFORM_TYPE");
    }
    
    @Override
    public String toString() {
        return "[Platform :" + this.devicePlatform + ", Device UDID: " + this.deviceUDID + ", Request Data:" + this.deviceRequestData + ", Customer Id :" + this.customerID;
    }
}
