package com.me.mdm.webclient.filter;

import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.adventnet.iam.security.IAMSecurityException;
import java.io.IOException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.mdm.core.auth.MDMUserAPIKeyGenerator;
import org.json.JSONObject;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class MDMUserUnifiedAuthenticationHandler implements UnifiedAuthenticationService
{
    public Logger deviceDataLog;
    
    public MDMUserUnifiedAuthenticationHandler() {
        this.deviceDataLog = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    public boolean authentication(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        DeviceRequest devicerequest = null;
        try {
            devicerequest = AuthenticationHandlerUtil.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
            final String zAPIKey = request.getParameter("zapikey");
            final String templateToken = AuthenticationHandlerUtil.getTemplateToken(request, devicerequest, this.deviceDataLog);
            if (!MDMStringUtils.isEmpty(zAPIKey) && !MDMStringUtils.isEmpty(templateToken)) {
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("zapikey", (Object)zAPIKey);
                jsonObject.put("templateToken", (Object)templateToken);
                if (MDMUserAPIKeyGenerator.getInstance().validateAPIKey(jsonObject)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            this.deviceDataLog.log(Level.SEVERE, "Exception in Unified User authentication: ", e);
            try {
                response.sendError(403, I18N.getMsg("dm.mdmod.common.NOT_AUTHORIED_CONTACT_ADMINISTRATOR", new Object[0]));
            }
            catch (final IOException ex) {
                this.deviceDataLog.log(Level.SEVERE, "IOException Occurred :", e);
            }
        }
        this.deviceDataLog.log(Level.INFO, "UnAuthenticated request in Unified User authentication: {0}, {1}", new Object[] { devicerequest, request.getRequestURI() });
        throw new IAMSecurityException("UNAUTHORISED");
    }
    
    public void init() {
    }
    
    public boolean authorization(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        return true;
    }
}
