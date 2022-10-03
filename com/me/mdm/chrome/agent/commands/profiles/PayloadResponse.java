package com.me.mdm.chrome.agent.commands.profiles;

public class PayloadResponse
{
    public String payloadType;
    public String errorMsg;
    public String status;
    public int errorCode;
    public boolean isOncPayload;
    
    public PayloadResponse() {
        this.errorMsg = "The Operation Completed Sucessfully";
        this.status = "Acknowledged";
        this.errorCode = 0;
        this.isOncPayload = false;
    }
    
    public String getPayloadType() {
        return this.payloadType;
    }
    
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public String getErrorMsg() {
        return this.errorMsg;
    }
    
    public String getPayloadStatus() {
        return this.status;
    }
    
    public void setPayloadType(final String payloadType) {
        this.payloadType = payloadType;
    }
    
    public void setErrorMsg(final String errorMsg) {
        this.errorMsg = errorMsg;
    }
    
    public void setErrorCode(final int errorCode) {
        this.errorCode = errorCode;
        if (errorCode == 0) {
            this.status = "Acknowledged";
        }
        else {
            this.status = "Error";
        }
    }
    
    public void setIsONCPayload(final boolean isONCPayload) {
        this.isOncPayload = isONCPayload;
    }
    
    public boolean isONCPayload() {
        return this.isOncPayload;
    }
}
