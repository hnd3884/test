package com.me.devicemanagement.framework.server.websockets;

import java.util.concurrent.Future;

public interface Socket
{
    void sendString(final String p0, final boolean p1) throws Exception;
    
    Future sendStringAsync(final String p0) throws Exception;
    
    void sendBytes(final byte[] p0, final boolean p1) throws Exception;
    
    Future sendBytesAsync(final byte[] p0) throws Exception;
    
    boolean isSecureConnection() throws Exception;
    
    String getClientIpAddress();
    
    void closeSocket() throws Exception;
    
    void setMaxIdleTime(final Long p0) throws Exception;
    
    long getMaxIdleTime() throws Exception;
}
