package com.me.devicemanagement.onpremise.webclient.configurations;

import com.me.devicemanagement.framework.webclient.cache.SessionAPI;
import java.util.Random;
import org.apache.commons.lang.RandomStringUtils;
import java.security.SecureRandom;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.util.TreeMap;
import com.me.devicemanagement.onpremise.webclient.admin.UserController;
import com.me.devicemanagement.framework.webclient.common.QuickLoadUtil;
import com.me.devicemanagement.onpremise.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.util.logging.Logger;

public class FrameworkLoginHandler implements LoginHandler
{
    Logger logger;
    String isSpicePlugin;
    String sslPort;
    
    public FrameworkLoginHandler() {
        this.logger = Logger.getLogger(FrameworkLoginHandler.class.getName());
        this.isSpicePlugin = "isSpicePlugin";
        this.sslPort = String.valueOf(SyMUtil.getSSLPort());
    }
    
    @Override
    public boolean productSpecificHandling(final HttpServletRequest request, final HttpServletResponse response, final ServletConfig servletConfig) {
        this.logger.log(Level.INFO, "Inside FrameworkLoginHandler ");
        final boolean validateHttps = this.validateHttps(request, response);
        if (!validateHttps) {
            return false;
        }
        this.setCSRFTokenInRequest(request);
        return true;
    }
    
    private boolean validateHttps(final HttpServletRequest request, final HttpServletResponse response) {
        final String httpsEnabled = SyMUtil.getSyMParameter("ENABLE_HTTPS");
        final String hostName = request.getServerName();
        final String isSpiceLogin = request.getParameter(this.isSpicePlugin);
        final boolean isDemoMode = ApiFactoryProvider.getDemoUtilAPI().isDemoMode();
        final boolean isNioWebServerPort = request.getServerPort() == SyMUtil.getNioWebServerPort();
        final boolean isSpicePluginLogin = isSpiceLogin != null && !isSpiceLogin.isEmpty() && isSpiceLogin.equalsIgnoreCase("true");
        try {
            if (!this.sslPort.equals("-1") && request.getScheme().equals("http") && !isDemoMode && httpsEnabled != null && httpsEnabled.equalsIgnoreCase("true") && !isNioWebServerPort) {
                this.logger.log(Level.INFO, "Https is enabled!");
                if (isSpicePluginLogin) {
                    QuickLoadUtil.redirectURL(this.constructURL(request, "https", hostName, this.sslPort, "/integrations", Boolean.TRUE), request, response);
                    return Boolean.FALSE;
                }
                QuickLoadUtil.redirectURL(this.constructURL(request, "https", hostName, this.sslPort, "/configurations", Boolean.FALSE), request, response);
                return Boolean.FALSE;
            }
            else if (isNioWebServerPort) {
                final TreeMap<String, String> domainList = UserController.getADDomainNamesForLoginPage();
                if (domainList != null) {
                    request.setAttribute("loginDomainList", (Object)domainList);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception in FrameworkLoginHandler :", ex);
        }
        this.logger.log(Level.INFO, "Execution of FrameworkLoginHandler complete ");
        return Boolean.TRUE;
    }
    
    private void setCSRFTokenInRequest(final HttpServletRequest request) {
        final SessionAPI sessionAPI = WebclientAPIFactoryProvider.getSessionAPI();
        final String salt = RandomStringUtils.random(20, 0, 0, true, true, (char[])null, (Random)new SecureRandom());
        sessionAPI.addToSession(request, "loginPageCsrfPreventionSalt", (Object)salt);
        request.setAttribute("loginPageCsrfPreventionSalt", (Object)salt);
    }
}
