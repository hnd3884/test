package com.me.ems.framework.securitysettings.api.core;

import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;

public interface SecuritySettingsService
{
    Map getSecuritySettingsDetails(final Long p0) throws APIException;
    
    Long validateCustomer(final String p0) throws APIException;
    
    Map saveSecuritySettings(final Map p0, final User p1, final Long p2, final HttpServletRequest p3) throws APIException;
    
    Map getSecuritySettingAlertDetails(final User p0, final Long p1) throws APIException;
    
    void updateSecurityRedirectionTime(final Boolean p0);
    
    default Map getSecurityEnforceDetails() throws APIException {
        return null;
    }
}
