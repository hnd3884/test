package com.me.mdm.server.payload;

import com.me.devicemanagement.framework.server.exception.SyMException;

public class PayloadException extends SyMException
{
    private String payloadErrorCode;
    
    public PayloadException() {
        this.payloadErrorCode = null;
    }
    
    public PayloadException(final String payloadErrorCode) {
        this.payloadErrorCode = payloadErrorCode;
    }
    
    public void setPayloadErrorCode(final String payloadErrorCode) {
        this.payloadErrorCode = payloadErrorCode;
    }
    
    public String getPayloadErrorCode() {
        return this.payloadErrorCode;
    }
}
