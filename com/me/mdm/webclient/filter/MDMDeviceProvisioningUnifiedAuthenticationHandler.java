package com.me.mdm.webclient.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class MDMDeviceProvisioningUnifiedAuthenticationHandler implements UnifiedAuthenticationService
{
    public void init() {
    }
    
    public boolean authentication(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
        AuthenticationHandlerUtil.prepareDeviceRequest(httpServletRequest, Logger.getLogger("MDMEnrollment"));
        return true;
    }
    
    public boolean authorization(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
        return true;
    }
}
