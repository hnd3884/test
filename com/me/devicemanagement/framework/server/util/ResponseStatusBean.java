package com.me.devicemanagement.framework.server.util;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResponseStatusBean
{
    int code;
    String message;
    
    public int getCode() {
        return this.code;
    }
    
    public void setCode(final int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(final String message) {
        this.message = message;
    }
    
    public ResponseStatusBean() {
    }
    
    public ResponseStatusBean(final int code) {
        this.code = code;
        this.message = FrameworkStatusCodes.getErrorMessageByCode(code);
    }
    
    public ResponseStatusBean(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
