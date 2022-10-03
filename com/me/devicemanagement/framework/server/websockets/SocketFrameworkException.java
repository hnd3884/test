package com.me.devicemanagement.framework.server.websockets;

public class SocketFrameworkException extends Exception
{
    private String message;
    
    SocketFrameworkException(final String exceptionMessage) {
        super(exceptionMessage);
        this.message = exceptionMessage;
    }
    
    @Override
    public String getMessage() {
        return this.message;
    }
}
