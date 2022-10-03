package com.me.emsalerts.sms.factory;

import java.util.List;
import java.util.Hashtable;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import com.me.ems.framework.uac.api.v1.model.User;

public interface SMSService
{
    Response updateSMSSettings(final User p0, final Map p1, final HttpServletRequest p2);
    
    Hashtable getSMSConfigurationSettings();
    
    List getDialingCodes();
    
    void enableSMSSettings(final User p0);
    
    void disableSMSSettings(final User p0);
    
    default boolean isSMSConfigured() {
        return false;
    }
    
    default Map getProbeSMSConfiguredStatusList() {
        return null;
    }
}
