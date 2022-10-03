package com.me.idps.core.api;

public class IdpsAPIException extends RuntimeException
{
    private String msg;
    private String errorCode;
    
    public IdpsAPIException(final String errorCode) {
        this(errorCode, (String)null);
    }
    
    public IdpsAPIException(final String errorCode, final String msg) {
        super(errorCode);
        this.msg = msg;
        this.errorCode = errorCode;
    }
}
