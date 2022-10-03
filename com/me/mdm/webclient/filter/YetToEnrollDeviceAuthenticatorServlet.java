package com.me.mdm.webclient.filter;

import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class YetToEnrollDeviceAuthenticatorServlet extends HttpServlet
{
    private final Logger logger;
    
    public YetToEnrollDeviceAuthenticatorServlet() {
        this.logger = Logger.getLogger("MDMDeviceDataLogger");
    }
    
    protected final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handleGet(request, response);
    }
    
    protected final void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.handlePost(request, response);
    }
    
    protected void handleGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.logger.log(Level.SEVERE, "YetToEnrollDeviceAuthenticatorServlet: doGet called in YetToEnrollDeviceAuthenticatorServlet");
    }
    
    protected void handlePost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.logger.log(Level.SEVERE, "YetToEnrollDeviceAuthenticatorServlet: doPost called in YetToEnrollDeviceAuthenticatorServlet");
    }
    
    protected void handleBadRequest(final long enrollmentRequestId) {
        this.logger.log(Level.SEVERE, "YetToEnrollDeviceAuthenticatorServlet: Bad encapi key for {0}", new Object[] { enrollmentRequestId });
    }
}
