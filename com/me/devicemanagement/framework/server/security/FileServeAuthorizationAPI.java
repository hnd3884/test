package com.me.devicemanagement.framework.server.security;

import javax.servlet.ServletRequest;

public interface FileServeAuthorizationAPI
{
    boolean isDSRequest(final ServletRequest p0);
    
    boolean isValidRequest(final String p0, final String p1, final String p2);
    
    boolean isFileAuthorizationOn();
    
    void initResourceCache();
    
    void updateCache(final String p0, final String p1, final String p2);
    
    void updateCacheAndRemoveOldResID(final String p0, final String p1, final String p2, final String p3);
}
