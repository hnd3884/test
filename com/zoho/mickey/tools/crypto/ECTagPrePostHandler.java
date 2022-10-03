package com.zoho.mickey.tools.crypto;

public interface ECTagPrePostHandler
{
    void preHandle() throws Exception;
    
    void postHandle(final boolean p0) throws Exception;
}
