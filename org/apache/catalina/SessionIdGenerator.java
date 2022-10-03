package org.apache.catalina;

public interface SessionIdGenerator
{
    String getJvmRoute();
    
    void setJvmRoute(final String p0);
    
    int getSessionIdLength();
    
    void setSessionIdLength(final int p0);
    
    String generateSessionId();
    
    String generateSessionId(final String p0);
}
