package com.me.ems.onpremise.summaryserver.summary.securitysettings.api.core;

import java.util.Map;

public interface SSSecuritySettingsService
{
    default long getProbeSecurityConfigPercentage() {
        return 0L;
    }
    
    default Map getProbeSecurityConfiguredPercentageList() {
        return null;
    }
}
