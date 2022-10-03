package com.me.mdm.webclient.filter;

import javax.servlet.ServletException;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.HashMap;
import com.me.mdm.agent.handlers.DeviceRequest;
import com.adventnet.iam.security.IAMSecurityException;
import java.io.IOException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import java.util.Map;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import com.me.mdm.core.auth.APIKey;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import com.me.mdm.core.windows.SyncMLMessageParser;
import com.me.mdm.framework.syncml.xml.XML2SyncMLMessageConverter;
import org.json.JSONObject;
import com.google.json.JsonSanitizer;
import com.adventnet.sym.server.mdm.PlistWrapper;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class MDMDeviceUnifiedAuthenticationHandler implements UnifiedAuthenticationService
{
    public Logger deviceDataLog;
    public Logger enrollmentLog;
    
    public MDMDeviceUnifiedAuthenticationHandler() {
        this.deviceDataLog = Logger.getLogger("MDMDeviceDataLogger");
        this.enrollmentLog = Logger.getLogger("MDMEnrollment");
    }
    
    public boolean authentication(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String sUDID = null;
        DeviceRequest devicerequest = null;
        try {
            devicerequest = AuthenticationHandlerUtil.prepareDeviceRequest(request, this.enrollmentLog);
            sUDID = request.getParameter("udid");
            if (MDMUtil.getInstance().isEmpty(sUDID) && request.getContentType() != null && request.getContentType().contains("application/x-apple-aspen-mdm")) {
                final String strData = AuthenticationHandlerUtil.sanitizeXML((String)devicerequest.deviceRequestData);
                final HashMap hashPlist = PlistWrapper.getInstance().getHashFromPlist(strData);
                sUDID = hashPlist.get("UDID");
            }
            if (MDMUtil.getInstance().isEmpty(sUDID) && request.getContentType() != null && request.getContentType().contains("application/json")) {
                devicerequest.deviceRequestData = JsonSanitizer.sanitize((String)devicerequest.deviceRequestData);
                final String strData = (String)devicerequest.deviceRequestData;
                sUDID = String.valueOf(new JSONObject(strData).get("UDID"));
            }
            if (MDMUtil.getInstance().isEmpty(sUDID) && request.getContentType() != null && (request.getContentType().contains("application/soap+xml") || request.getContentType().contains("application/vnd.syncml.dm+xm"))) {
                final String strData = (String)devicerequest.deviceRequestData;
                final XML2SyncMLMessageConverter converter = new XML2SyncMLMessageConverter();
                final SyncMLMessage requestSyncML = converter.transform(strData);
                final SyncMLMessageParser parser = new SyncMLMessageParser();
                final JSONObject syncMLHeader = parser.parseSyncMLMessageHeader(requestSyncML);
                sUDID = String.valueOf(syncMLHeader.get("UDID"));
            }
            if (!MDMUtil.getInstance().isEmpty(request.getParameter("encapiKey"))) {
                final HashMap requestParams = AuthenticationHandlerUtil.getParameterValueMap(request);
                final JSONObject json = JSONUtil.mapToJSON(MDMDeviceAPIKeyGenerator.getInstance().fetchAPIKeyDetails(APIKey.VERSION_2_0, requestParams));
                json.put("UDID", (Object)sUDID);
                if (requestParams.containsKey("erid")) {
                    json.put("ENROLLMENT_REQUEST_ID", (Object)Long.valueOf(requestParams.get("erid")));
                }
                if (MDMDeviceAPIKeyGenerator.getInstance().validateAPIKey(json)) {
                    return true;
                }
            }
        }
        catch (final Exception e) {
            this.deviceDataLog.log(Level.SEVERE, "Exception in Unified Device authentication: ", e);
            try {
                response.sendError(403, I18N.getMsg("dm.mdmod.common.NOT_AUTHORIED_CONTACT_ADMINISTRATOR", new Object[0]));
            }
            catch (final IOException ex) {
                this.deviceDataLog.log(Level.SEVERE, "IOException Occurred :", e);
            }
        }
        this.deviceDataLog.log(Level.INFO, "UnAuthenticated request in Unified Device authentication: {0}, {1}, {2}", new Object[] { sUDID, devicerequest, request.getRequestURI() });
        throw new IAMSecurityException("UNAUTHORISED");
    }
    
    public void init() {
    }
    
    public boolean authorization(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        return true;
    }
}
