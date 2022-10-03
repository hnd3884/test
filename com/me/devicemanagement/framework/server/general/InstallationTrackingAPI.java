package com.me.devicemanagement.framework.server.general;

import java.util.Properties;

public interface InstallationTrackingAPI
{
    void writeServerInfoProps(final Properties p0);
    
    void writeInstallProps(final Properties p0);
}
