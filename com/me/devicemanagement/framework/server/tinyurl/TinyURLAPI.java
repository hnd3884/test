package com.me.devicemanagement.framework.server.tinyurl;

public interface TinyURLAPI
{
    String getTinyURL(final String p0, final String p1, final Long p2, final String p3) throws Exception;
    
    String getQRImageURL(final String p0) throws Exception;
    
    boolean modifyTargetURL(final String p0, final String p1) throws Exception;
    
    boolean expireURL(final String p0, final String p1) throws Exception;
    
    String getCompleteURLForTinyURL(final String p0) throws Exception;
    
    boolean isLinkAvailable(final String p0) throws Exception;
}
