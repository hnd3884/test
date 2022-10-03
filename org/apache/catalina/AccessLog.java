package org.apache.catalina;

import org.apache.catalina.connector.Response;
import org.apache.catalina.connector.Request;

public interface AccessLog
{
    public static final String REMOTE_ADDR_ATTRIBUTE = "org.apache.catalina.AccessLog.RemoteAddr";
    public static final String REMOTE_HOST_ATTRIBUTE = "org.apache.catalina.AccessLog.RemoteHost";
    public static final String PROTOCOL_ATTRIBUTE = "org.apache.catalina.AccessLog.Protocol";
    public static final String SERVER_NAME_ATTRIBUTE = "org.apache.catalina.AccessLog.ServerName";
    public static final String SERVER_PORT_ATTRIBUTE = "org.apache.catalina.AccessLog.ServerPort";
    
    void log(final Request p0, final Response p1, final long p2);
    
    void setRequestAttributesEnabled(final boolean p0);
    
    boolean getRequestAttributesEnabled();
}
