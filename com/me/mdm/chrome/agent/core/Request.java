package com.me.mdm.chrome.agent.core;

public class Request
{
    public String commandUUID;
    public String requestType;
    public Object requestData;
    public String deviceUDID;
    long requestStartTime;
    long requestEndTime;
    String commandScope;
    private MDMContainer container;
    
    public Request() {
        this.commandUUID = null;
        this.requestType = null;
        this.requestData = null;
        this.deviceUDID = null;
        this.requestStartTime = 0L;
        this.requestEndTime = 0L;
        this.container = null;
    }
    
    public MDMContainer getContainer() {
        return this.container;
    }
    
    public void setContainer(final MDMContainer container) {
        this.container = container;
    }
}
