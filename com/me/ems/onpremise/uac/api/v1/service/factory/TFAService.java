package com.me.ems.onpremise.uac.api.v1.service.factory;

import javax.ws.rs.core.Response;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.Map;

public interface TFAService
{
    Map<String, Object> getTFAEnforcementDetails() throws APIException;
    
    Map<String, Object> getTwoFactorDetails() throws APIException;
    
    Response saveTwoFactorDetails(final Map p0, final User p1, final HttpServletRequest p2) throws APIException;
    
    Map<String, String> getQRCode(final long p0, final Map<String, String> p1) throws APIException;
    
    void extendOrDisableTFA(final Map p0, final String p1) throws APIException;
}
