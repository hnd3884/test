package com.me.mdm.api;

import java.util.Properties;
import javax.servlet.ServletConfig;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.adventnet.iam.security.SecurityUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class APIServlet extends HttpServlet
{
    public Logger logger;
    protected List restrictedHostNames;
    
    public APIServlet() {
        this.logger = Logger.getLogger("MDMApiLogger");
        this.restrictedHostNames = new ArrayList();
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    protected void doDelete(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    protected void doHead(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final APIRequestProcessor requestProcessor = APIRequestProcessor.getNewInstance();
        try {
            this.logger.log(Level.INFO, "API Request Received at {0}", SyMUtil.getCurrentTimeWithDate());
            this.logger.log(Level.INFO, "URI is {0} : {1}", new Object[] { request.getMethod(), SecurityUtil.getRequestPath(request) });
            if (!this.restrictedHostNames.isEmpty()) {
                SYMClientUtil.returnRequestFromRestrictedHostName(this.restrictedHostNames, (ServletRequest)request, (ServletResponse)response);
            }
            requestProcessor.processRequest(request, response);
            this.logger.log(Level.INFO, "API Request Completed at {0}", SyMUtil.getCurrentTimeWithDate());
            this.logger.log(Level.INFO, "API Response Status - {0}", response.getStatus());
        }
        catch (final Exception e) {
            try {
                requestProcessor.writeOutputResponse(e.getMessage(), response);
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while sending failed request processing status {0}", e2);
            }
        }
        finally {
            ApiFactoryProvider.getAuthUtilAccessAPI().flushCredentials();
        }
    }
    
    public void init(final ServletConfig config) throws ServletException {
        Properties webServerProps = null;
        try {
            webServerProps = ApiFactoryProvider.getUtilAccessAPI().getWebServerSettings();
            if (webServerProps == null || !webServerProps.containsKey("restrict.ui.mobapp") || !webServerProps.getProperty("restrict.ui.mobapp").equalsIgnoreCase("false")) {
                this.restrictedHostNames = SYMClientUtil.getRestrictedHostNames();
            }
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while reading webserverprops : {0}", e);
        }
    }
}
