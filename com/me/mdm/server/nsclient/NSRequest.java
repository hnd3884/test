package com.me.mdm.server.nsclient;

import java.util.ArrayList;

public class NSRequest
{
    long requestid;
    String requestcommand;
    String reqIDStr;
    String requestBuffer;
    byte[] returnData;
    Object clonedAgentRequest;
    
    public NSRequest() {
        this.requestid = 0L;
        this.requestcommand = "/p";
        this.reqIDStr = null;
        this.requestBuffer = null;
        this.returnData = null;
        this.clonedAgentRequest = null;
        this.generateRequestID();
        this.reqIDStr = Long.toString(this.requestid);
    }
    
    private void generateRequestID() {
        if (this.requestid == 0L) {
            try {
                Thread.sleep(1L);
                this.requestid = System.currentTimeMillis();
            }
            catch (final Exception ex) {}
        }
    }
    
    public long getRequestID() {
        if (this.requestid == 0L) {
            this.generateRequestID();
        }
        return this.requestid;
    }
    
    public void createNSRequestData(final Long resourceID, final String buffer) throws Exception {
        final ArrayList list = new ArrayList();
        list.add(resourceID);
        this.createNSRequestData(list, buffer);
    }
    
    public void createNSRequestData(final ArrayList resourceList, final String buffer) throws Exception {
        this.formatRequest(resourceList, buffer);
    }
    
    public byte[] getRequestBytes() {
        return this.returnData;
    }
    
    private byte[] formatRequest(final ArrayList resourceList, final String dataBuffer) throws Exception {
        String clientlistbuffer = null;
        clientlistbuffer = this.getClientsListBuffer(resourceList);
        if (this.requestcommand != null && this.reqIDStr != null && clientlistbuffer != null && dataBuffer != null) {
            this.requestBuffer = this.requestcommand + "=" + this.reqIDStr + ";clientList=" + clientlistbuffer + ";" + dataBuffer;
            if (this.requestBuffer != null) {
                this.returnData = this.requestBuffer.getBytes("UTF-8");
            }
            return this.returnData;
        }
        throw new Exception("Invalid Data to form the request");
    }
    
    private String getClientsListBuffer(final ArrayList resourceList) {
        String clientlistbuffer = null;
        final StringBuilder stbuffer = new StringBuilder();
        if (resourceList != null && resourceList.size() > 0) {
            for (int size = resourceList.size(), i = 0; i < size; ++i) {
                stbuffer.append(resourceList.get(i));
                if (i < size - 1) {
                    stbuffer.append(",");
                }
            }
            clientlistbuffer = stbuffer.toString();
        }
        return clientlistbuffer;
    }
    
    public void setAgentRequest(final Object cloneAgentRequest) {
        this.clonedAgentRequest = cloneAgentRequest;
    }
    
    public Object getAgentRequest() {
        return this.clonedAgentRequest;
    }
}
