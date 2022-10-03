package com.me.ems.framework.securitysettings.api.core;

import java.util.Map;
import com.me.ems.framework.securitysettings.api.v1.model.SecuritySettingsModel;

public interface SecuritySettingsAPI
{
    void getSecuritySettings(final SecuritySettingsModel p0) throws Exception;
    
    void saveSecuritySettings(final Map p0, final String p1, final Long p2) throws Exception;
    
    void getSecuritySettingsAlertDetails(final SecuritySettingsModel p0) throws Exception;
    
    default Map getSecurityEnforceDetails(final SecuritySettingsModel securitySettingsModel) throws Exception {
        return null;
    }
}
