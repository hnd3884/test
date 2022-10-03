package com.me.mdm.server.easmanagement;

public class EASResponseObject
{
    private int responseType;
    private Object responseData;
    
    public int getResponseType() {
        return this.responseType;
    }
    
    public void setResponseType(final int responseType) {
        this.responseType = responseType;
    }
    
    public Object getResponseData() {
        return this.responseData;
    }
    
    public void setResponseData(final Object responseData) {
        this.responseData = responseData;
    }
}
