package com.me.devicemanagement.framework.webclient.api.servlet;

import java.util.Properties;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.ServletConfig;
import java.lang.reflect.Method;
import com.me.devicemanagement.framework.webclient.api.util.APIRequest;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import com.me.devicemanagement.framework.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.api.util.APIUtil;
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
    private Logger logger;
    private List restrictedHostNames;
    private Object moduleKeyObject;
    
    public APIServlet() {
        this.logger = Logger.getLogger("DCAPILogger");
        this.restrictedHostNames = new ArrayList();
        this.moduleKeyObject = null;
    }
    
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
    
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final APIUtil apiUtil = new APIUtil();
        try {
            this.logger.log(Level.FINEST, "API Request Received at {0}", SyMUtil.getCurrentTimeWithDate());
            this.logger.log(Level.FINEST, "URI is {0}", SecurityUtil.getNormalizedRequestURI(request));
            if (apiUtil.checkAnnouncementAPI(request)) {
                final Class apiClass = Class.forName("com.adventnet.sym.webclient.servlet.APIServlet");
                final Method apiMethod = apiClass.getMethod("doPost", HttpServletRequest.class, HttpServletResponse.class);
                apiMethod.invoke(apiClass.newInstance(), request, response);
            }
            else {
                if (!this.restrictedHostNames.isEmpty()) {
                    SYMClientUtil.returnRequestFromRestrictedHostName(this.restrictedHostNames, (ServletRequest)request, (ServletResponse)response);
                }
                apiUtil.processRequest(request, response);
            }
            this.logger.log(Level.FINEST, "API Request Completed at {0}", SyMUtil.getCurrentTimeWithDate());
        }
        catch (final Exception e) {
            try {
                apiUtil.errorCode = "1003";
                apiUtil.errorMessage = "Unable to retrieve details, Please try again later.";
                apiUtil.setOutput(apiUtil.constructMessageResponse(null, null, null, "", null));
                apiUtil.writeOutputResponse(null, apiUtil.getOutput(), response);
            }
            catch (final Exception e2) {
                this.logger.log(Level.SEVERE, "Exception while sending failed request processing status {0}", e2);
            }
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
