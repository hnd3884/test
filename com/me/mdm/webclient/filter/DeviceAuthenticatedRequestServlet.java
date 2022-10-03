package com.me.mdm.webclient.filter;

import javax.servlet.ServletException;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.io.IOException;
import com.adventnet.i18n.I18N;
import com.adventnet.sym.server.mdm.util.AuthTokenUtil;
import com.me.mdm.core.windows.SyncMLMessageParser;
import com.me.mdm.framework.syncml.xml.XML2SyncMLMessageConverter;
import com.adventnet.sym.server.mdm.util.JSONUtil;
import org.json.JSONObject;
import com.google.json.JsonSanitizer;
import com.me.mdm.core.auth.MDMDeviceAPIKeyGenerator;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

public class DeviceAuthenticatedRequestServlet extends DeviceRequestServlet
{
    public Logger deviceDataLog;
    
    public DeviceAuthenticatedRequestServlet() {
        this.deviceDataLog = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    protected DeviceRequest validateRequest(final HttpServletRequest request, final HttpServletResponse response) {
        DeviceRequest devicerequest = null;
        try {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
                this.deviceDataLog.log(Level.INFO, "Entered {0} validateRequest", DeviceAuthenticatedRequestServlet.class.getName());
            }
            devicerequest = this.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
            if (!MDMDeviceAPIKeyGenerator.getInstance().isClientVersion2_0(request.getServletPath())) {
                String commandUUID = null;
                String stringUdid = null;
                final Long erid = (request.getParameter("erid") == null) ? null : Long.valueOf(request.getParameter("erid"));
                final Long custIDFromParam = (request.getParameter("customerId") == null) ? ((request.getParameter("cid") == null) ? null : Long.valueOf(request.getParameter("cid"))) : Long.valueOf(request.getParameter("customerId"));
                if (request.getContentType() != null) {
                    if (request.getContentType().contains("application/json")) {
                        devicerequest = this.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
                        devicerequest.deviceRequestData = JsonSanitizer.sanitize((String)devicerequest.deviceRequestData);
                        final String strData = (String)devicerequest.deviceRequestData;
                        final JSONObject jsonObject = new JSONObject(strData);
                        commandUUID = JSONUtil.getString(jsonObject, "CommandUUID", null);
                        stringUdid = JSONUtil.getString(jsonObject, "UDID", null);
                    }
                    if (request.getContentType().contains("application/soap+xml") || request.getContentType().contains("application/vnd.syncml.dm+xm")) {
                        devicerequest = this.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
                        final String strData = (String)devicerequest.deviceRequestData;
                        final XML2SyncMLMessageConverter converter = new XML2SyncMLMessageConverter();
                        final SyncMLMessage requestSyncML = converter.transform(strData);
                        final SyncMLMessageParser parser = new SyncMLMessageParser();
                        final JSONObject commandStatusObject = parser.parseCommandStatusMessage(requestSyncML);
                        commandUUID = JSONUtil.getString(commandStatusObject, "CommandUUID", null);
                        final JSONObject syncMLHeader = parser.parseSyncMLMessageHeader(requestSyncML);
                        stringUdid = JSONUtil.getString(syncMLHeader, "UDID", null);
                    }
                }
                this.deviceDataLog.log(Level.INFO, "Incoming request with authtoken: {0} {1} {2} {3}", new Object[] { erid, commandUUID, request.getRequestURI(), stringUdid });
                if (commandUUID != null && (commandUUID.equals("DeviceInformation") || commandUUID.equals("DeviceInformation;USER_INVOKED") || commandUUID.equals("AssetScan") || commandUUID.equals("AssetScan;USER_INVOKED"))) {
                    this.deviceDataLog.log(Level.INFO, "Adding to AUTH_TOKEN_CHECK_IN_QUEUE_TYPE: {0}", new Object[] { erid });
                    AuthTokenUtil.addToQueue(erid, custIDFromParam, stringUdid);
                }
            }
            return devicerequest;
        }
        catch (final RuntimeException e) {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
                this.deviceDataLog.log(Level.SEVERE, "Exception Occurred :", e);
            }
            throw e;
        }
        catch (final Exception e2) {
            if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
                this.deviceDataLog.log(Level.SEVERE, "Exception Occurred :", e2);
            }
            try {
                response.sendError(403, I18N.getMsg("dm.mdmod.common.NOT_AUTHORIED_CONTACT_ADMINISTRATOR", new Object[0]));
            }
            catch (final IOException ex) {
                this.deviceDataLog.log(Level.SEVERE, "IOException Occurred :", e2);
            }
            catch (final Exception exception) {
                this.deviceDataLog.log(Level.SEVERE, "Exception Occurred :", exception);
            }
            throw new RuntimeException("Improper Authentication - invalid request without proper auth credentials");
        }
    }
    
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.deviceDataLog.log(Level.INFO, "Entered {0} doGet", DeviceAuthenticatedRequestServlet.class.getName());
        }
        this.doGet(request, response, this.validateRequest(request, response));
    }
    
    protected final void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.deviceDataLog.log(Level.INFO, "Entered {0} doPut", DeviceAuthenticatedRequestServlet.class.getName());
        }
        this.doPut(request, response, this.validateRequest(request, response));
    }
    
    protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.deviceDataLog.log(Level.INFO, "Entered {0} doPost", DeviceAuthenticatedRequestServlet.class.getName());
        }
        this.doPost(request, response, this.validateRequest(request, response));
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.deviceDataLog.log(Level.WARNING, "doGet called in DeviceAuthenticatedRequestServlet");
    }
    
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.deviceDataLog.log(Level.WARNING, "doGet called in DeviceAuthenticatedRequestServlet");
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.deviceDataLog.log(Level.WARNING, "doGet called in DeviceAuthenticatedRequestServlet");
    }
}
