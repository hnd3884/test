package com.me.mdm.webclient.filter;

import javax.servlet.ServletException;
import java.io.IOException;
import com.adventnet.i18n.I18N;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

public class UserAuthenticatedRequestServlet extends DeviceRequestServlet
{
    public Logger deviceDataLog;
    
    public UserAuthenticatedRequestServlet() {
        this.deviceDataLog = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    protected DeviceRequest validateRequest(final HttpServletRequest request, final HttpServletResponse response) {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.deviceDataLog.log(Level.INFO, "Entered {0} validateRequest", UserAuthenticatedRequestServlet.class.getName());
        }
        DeviceRequest devicerequest = null;
        try {
            devicerequest = this.prepareDeviceRequest(request, Logger.getLogger("MDMEnrollment"));
            return devicerequest;
        }
        catch (final Exception e) {
            this.deviceDataLog.log(Level.SEVERE, "Exception in User authenticated servlet when checking a request ", e);
            try {
                response.sendError(403, I18N.getMsg("dm.mdmod.common.NOT_AUTHORIED_CONTACT_ADMINISTRATOR", new Object[0]));
            }
            catch (final IOException ex) {
                this.deviceDataLog.log(Level.SEVERE, "IOException Occurred :", e);
            }
            catch (final Exception exception) {
                this.deviceDataLog.log(Level.SEVERE, "Exception Occurred :", exception);
            }
            throw new RuntimeException("Improper Authentication - Expected either encapiKey or zapiKey - invalid request without proper auth credentials");
        }
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.deviceDataLog.log(Level.INFO, "Entered {0} doGet", UserAuthenticatedRequestServlet.class.getName());
        }
        this.doGet(request, response, this.validateRequest(request, response));
    }
    
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.deviceDataLog.log(Level.INFO, "Entered {0} doPut", UserAuthenticatedRequestServlet.class.getName());
        }
        this.doPut(request, response, this.validateRequest(request, response));
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (MDMApiFactoryProvider.getMDMUtilAPI().isFeatureAllowedForUser("enrollment.debug.logs")) {
            this.deviceDataLog.log(Level.INFO, "Entered {0} doPost", UserAuthenticatedRequestServlet.class.getName());
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
