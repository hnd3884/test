package com.me.tools.zcutil;

import java.util.Properties;

public interface ZCDataHandler
{
    void uploadData();
    
    Properties getInstallationDetails();
    
    void uploadODData(final long p0);
}
