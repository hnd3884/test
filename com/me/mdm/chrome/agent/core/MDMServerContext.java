package com.me.mdm.chrome.agent.core;

public class MDMServerContext
{
    public String deviceUDID;
    public Object commandVersion;
    public Long customerId;
    
    public MDMServerContext(final Long customerId, final String udid) {
        this.deviceUDID = null;
        this.commandVersion = null;
        this.customerId = customerId;
        this.deviceUDID = udid;
        this.commandVersion = "0.0.1 CMPA";
    }
}
