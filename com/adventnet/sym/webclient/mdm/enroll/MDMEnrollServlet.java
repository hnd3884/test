package com.adventnet.sym.webclient.mdm.enroll;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.mdm.server.enrollment.MDMEnrollmentHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import com.adventnet.i18n.I18N;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.agent.servlets.DeviceRequestServlet;

public class MDMEnrollServlet extends DeviceRequestServlet
{
    public Logger logger;
    
    public MDMEnrollServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    protected void processRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final String isDemoMode = String.valueOf(ApiFactoryProvider.getDemoUtilAPI().isDemoMode());
            request.setAttribute("isDemoMode", (Object)isDemoMode);
            request.setAttribute("demoModeMessage", (Object)I18N.getMsg("dc.common.RUNNING_IN_RESTRICTED_MODE", new Object[0]));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while getting Demo message", ex);
        }
        final String action = request.getParameter("actionToCall");
        final String servletPath = request.getServletPath();
        final String userAgent = request.getHeader("user-agent");
        this.logger.log(Level.INFO, "MDMEnrollServlet: servletPath{0}", servletPath);
        this.logger.log(Level.INFO, "MDMEnrollServlet: action{0}", action);
        this.logger.log(Level.INFO, "MDMEnrollServlet: userAgent{0}", userAgent);
        response.addHeader("MDMEnrollment", "true");
        final JSONObject enrollData = new JSONObject();
        try {
            enrollData.put("action", (Object)action);
            enrollData.put("servletPath", (Object)servletPath);
            enrollData.put("userAgent", (Object)userAgent);
            enrollData.put("servletContext", (Object)this.getServletContext());
            enrollData.put("pathInfo", (Object)request.getPathInfo());
        }
        catch (final JSONException ex2) {
            this.logger.log(Level.WARNING, "Exception occurred while constructing enroll data", (Throwable)ex2);
        }
        final MDMEnrollmentHandler enrollHandler = MDMEnrollmentHandler.getEnrollmentHandler(userAgent);
        if (enrollHandler != null) {
            enrollHandler.initEnrollemnt(request, response, enrollData);
        }
        else {
            this.logger.log(Level.WARNING, "MDMEnrollmentHandler initialization failed {0}", enrollHandler);
            this.logger.log(Level.WARNING, "Enroll Data {0}", enrollData);
            this.getServletContext().getRequestDispatcher("/jsp/mdm/enroll/otherInvalidBrowser.jsp").forward((ServletRequest)request, (ServletResponse)response);
        }
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.processRequest(request, response);
    }
}
