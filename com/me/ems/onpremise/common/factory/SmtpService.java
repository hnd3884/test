package com.me.ems.onpremise.common.factory;

import java.util.HashMap;
import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;

public interface SmtpService
{
    Map<String, Boolean> isMailServerEnabled();
    
    Map<String, Object> getSmtpSettings() throws APIException;
    
    void verifyAndSendTestMail(final Map<String, Object> p0) throws APIException;
    
    Response updateSmtpSettings(final Map<String, Object> p0, final String p1, final HttpServletRequest p2) throws APIException;
    
    void deleteSmtpSettings() throws APIException;
    
    boolean isSmtpConfigured();
    
    default Map getProbeSmtpConfiguredStatusList() {
        return null;
    }
    
    HashMap getAuthorizationServerDetails(final String p0);
}
