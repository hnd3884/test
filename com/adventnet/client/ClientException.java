package com.adventnet.client;

import java.util.HashMap;
import java.util.Map;

public class ClientException extends Exception
{
    private static final long serialVersionUID = 1L;
    private String errorCode;
    private Map<String, Object> propMap;
    
    public ClientException(final ClientErrorCodes error) {
        super(error.toString());
        this.errorCode = null;
        this.errorCode = error.getCode();
    }
    
    public ClientException(final String message) {
        super(message);
        this.errorCode = null;
    }
    
    public ClientException(final ClientErrorCodes error, final Throwable t) {
        super(error.toString(), t);
        this.errorCode = null;
        this.errorCode = error.getCode();
    }
    
    public ClientException(final String message, final Throwable t) {
        super(message, t);
        this.errorCode = null;
    }
    
    public String getErrorCode() {
        return this.errorCode;
    }
    
    @Override
    public String getMessage() {
        return super.getMessage();
    }
    
    public Object getErrorProperty(final String propName) {
        return (this.propMap != null) ? this.propMap.get(propName) : null;
    }
    
    public void setErrorProperty(final String propName, final Object property) {
        if (this.propMap == null) {
            this.propMap = new HashMap<String, Object>();
        }
        this.propMap.put(propName, property);
    }
}
