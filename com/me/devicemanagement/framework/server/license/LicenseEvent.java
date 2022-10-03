package com.me.devicemanagement.framework.server.license;

import java.util.Properties;
import java.util.Map;

public class LicenseEvent
{
    public Map<String, Properties> oldLicenseDetails;
    
    public LicenseEvent() {
        this.oldLicenseDetails = null;
    }
}
