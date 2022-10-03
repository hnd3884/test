package com.me.devicemanagement.onpremise.server.common;

import java.util.Properties;

public interface ProxyConfiguredListener
{
    void proxyConfigured(final Properties p0);
    
    void addUrlsForDomainValidation();
}
