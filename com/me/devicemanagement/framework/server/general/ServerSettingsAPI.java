package com.me.devicemanagement.framework.server.general;

import java.util.HashMap;
import java.util.Properties;

public interface ServerSettingsAPI
{
    Properties getNATConfigurationProperties() throws Exception;
    
    Properties getProxyConfiguration() throws Exception;
    
    Properties getProxyConfiguration(final String p0, final Properties p1) throws Exception;
    
    HashMap getAllNATProperties();
    
    int getCertificateType() throws Exception;
    
    String getResourceBundleRootDirectory();
}
