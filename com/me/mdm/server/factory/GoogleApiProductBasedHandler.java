package com.me.mdm.server.factory;

public interface GoogleApiProductBasedHandler
{
    void handleServerTimeMismatch(final Exception p0, final Long p1);
    
    String getValueFromPropertiesFile(final String p0) throws Exception;
}
