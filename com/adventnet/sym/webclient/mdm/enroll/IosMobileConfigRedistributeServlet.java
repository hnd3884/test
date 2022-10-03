package com.adventnet.sym.webclient.mdm.enroll;

import com.me.mdm.server.enrollment.MDMEnrollmentHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import com.me.mdm.agent.handlers.DeviceRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.DeviceAuthenticatedRequestServlet;

public class IosMobileConfigRedistributeServlet extends DeviceAuthenticatedRequestServlet
{
    private Logger logger;
    
    public IosMobileConfigRedistributeServlet() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response, final DeviceRequest deviceRequest) throws ServletException, IOException {
        this.processRequest(request, response);
    }
    
    private void processRequest(final HttpServletRequest request, final HttpServletResponse response) {
        final String servletPath = request.getServletPath();
        final String userAgent = request.getHeader("user-agent");
        this.logger.log(Level.INFO, "IosMobileConfigRedistributeServlet: servletPath{0}", servletPath);
        this.logger.log(Level.INFO, "IosMobileConfigRedistributeServlet: userAgent{0}", userAgent);
        final JSONObject enrollData = new JSONObject();
        try {
            enrollData.put("action", (Object)"refetchconfig");
            enrollData.put("servletPath", (Object)servletPath);
            enrollData.put("userAgent", (Object)userAgent);
            enrollData.put("servletContext", (Object)this.getServletContext());
            enrollData.put("pathInfo", (Object)request.getPathInfo());
        }
        catch (final JSONException ex) {
            this.logger.log(Level.WARNING, "Exception occurred while constructing enroll data", (Throwable)ex);
        }
        final MDMEnrollmentHandler enrollHandler = MDMEnrollmentHandler.getEnrollmentHandler(userAgent);
        enrollHandler.initEnrollemnt(request, response, enrollData);
    }
}
