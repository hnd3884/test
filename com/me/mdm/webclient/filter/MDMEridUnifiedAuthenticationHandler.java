package com.me.mdm.webclient.filter;

import javax.servlet.ServletException;
import com.adventnet.iam.security.IAMSecurityException;
import java.io.IOException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class MDMEridUnifiedAuthenticationHandler implements UnifiedAuthenticationService
{
    private final Logger logger;
    
    public MDMEridUnifiedAuthenticationHandler() {
        this.logger = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    public void init() {
    }
    
    public boolean authentication(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
        Long enrollmentRequestId = null;
        try {
            enrollmentRequestId = Long.parseLong(httpServletRequest.getParameter("erid"));
            final String encapiKey = httpServletRequest.getParameter("encapiKey");
            final boolean isValidRequest = MDMDeviceAPIKeyGenerator.getInstance().isValidApiKeyForEnrollmentRequestId(enrollmentRequestId, encapiKey);
            return isValidRequest;
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, "Exception in Unified Erid authentication: ", ex);
            try {
                httpServletResponse.sendError(403, I18N.getMsg("dm.mdmod.common.NOT_AUTHORIED_CONTACT_ADMINISTRATOR", new Object[0]));
            }
            catch (final IOException e) {
                this.logger.log(Level.SEVERE, "IOException Occurred :", e);
            }
            this.logger.log(Level.INFO, "UnAuthenticated request in Unified Erid authentication: {0}, {1}", new Object[] { enrollmentRequestId, httpServletRequest.getRequestURI() });
            throw new IAMSecurityException("UNAUTHORISED");
        }
    }
    
    public boolean authorization(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
        return true;
    }
}
