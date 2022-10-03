package com.me.devicemanagement.onpremise.webclient.configurations;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import java.util.Locale;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.license.FreeEditionHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.io.IOException;
import com.me.devicemanagement.framework.webclient.common.QuickLoadUtil;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.net.InetAddress;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.text.SimpleDateFormat;
import com.me.devicemanagement.framework.webclient.cache.SessionAPI;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class PostLoginServlet extends HttpServlet
{
    Logger logger;
    SessionAPI sessionAPI;
    String date_Format;
    SimpleDateFormat dateFormat;
    
    public void init() throws ServletException {
        this.logger = Logger.getLogger(PostLoginServlet.class.getName());
        this.sessionAPI = WebclientAPIFactoryProvider.getSessionAPI();
        this.date_Format = "MMM dd,yyyy hh:mm:ss a";
        this.dateFormat = new SimpleDateFormat(this.date_Format);
    }
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String remoteAddr = request.getRemoteAddr();
        String loginUserName = "";
        final long time = System.currentTimeMillis();
        final Date dt = new Date(time);
        final String hostName = InetAddress.getByName(remoteAddr).getHostName();
        final boolean isAWSLogin = Boolean.parseBoolean(SyMUtil.getServerParameter("IS_AWS_LOGIN"));
        final boolean isAwsDefaultPasswordChanged = Boolean.parseBoolean(SyMUtil.getServerParameter("IS_AMAZON_DEFAULT_PASSWORD_CHANGED"));
        final String productCode = ProductUrlLoader.getInstance().getValue("productcode");
        try {
            if (isAWSLogin && !isAwsDefaultPasswordChanged) {
                this.postLoginAwsHandling(request, response);
                return;
            }
            this.sessionAPI.removeSessionAttribute(request, "restrictedLoginPage");
            if (request.getUserPrincipal() != null) {
                loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            }
            MSPWebClientUtil.setCustomerIDSummaryInCookie(request, response, "All");
            final Long userID = SYMClientUtil.getCurrentlyLoggedInUserID(request);
            this.localeForCurrentlyLoggedInUser(request, userID);
            this.setActiveDB(request);
            SyMUtil.writeLogonCountInFile();
            this.updateLoginTime(time, userID);
            this.logger.log(Level.INFO, "********logon count is written in install.conf********");
            this.logger.log(Level.INFO, "The host " + hostName + " ( " + remoteAddr + " ) " + "is connected as " + loginUserName + " at " + this.dateFormat.format(dt));
            this.handlingPluginLogin(request);
            final boolean forwardToHomePage = this.forwardToHomePage(request, response);
            if (forwardToHomePage) {
                final int port = request.getServerPort();
                final String contextPath = request.getContextPath();
                final String requestScheme = request.getScheme();
                this.logger.log(Level.INFO, "From Login page forwarding to Home Page." + contextPath);
                QuickLoadUtil.redirectURL(requestScheme + "://" + request.getServerName() + ":" + port + contextPath + this.getInitParameter(productCode), request, response);
            }
            this.removedPagesListForUserID(request, loginUserName);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred in post login Operations of LoginServlet: ", ex);
        }
    }
    
    private void handlingPluginLogin(final HttpServletRequest request) {
        final String isPluginLogin = (String)this.sessionAPI.getSessionAttribute(request, "isPuginLogin");
        if (isPluginLogin != null && isPluginLogin.equals("PLUGIN_LOGIN")) {
            final String fromJumpTo = (String)this.sessionAPI.getSessionAttribute(request, "jumpto");
            if (fromJumpTo != null && fromJumpTo.equalsIgnoreCase("true")) {
                this.sessionAPI.addToSession(request, "jumpto", (Object)"true");
            }
        }
    }
    
    private void setActiveDB(final HttpServletRequest request) {
        String activeDB = "mysql";
        activeDB = DBUtil.getActiveDBName();
        this.sessionAPI.addToSession(request, "activeDB", (Object)activeDB);
    }
    
    private boolean forwardToHomePage(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (FreeEditionHandler.getInstance().isFreeEdition()) {
            this.logger.log(Level.INFO, "This is FREE EDITION LICENSE.");
            SyMUtil.writeLicenseTypeInFile();
        }
        else {
            this.logger.log(Level.INFO, "This is not FREE EDITION LICENSE.");
        }
        return true;
    }
    
    private void removedPagesListForUserID(final HttpServletRequest request, final String loginUserName) {
        try {
            final MessageProvider msgPro = MessageProvider.getInstance();
            if (MessageProvider.collectMsgShownToPageList) {
                final Long userId = DMUserHandler.getUserID(loginUserName);
                final Long customerId = MSPWebClientUtil.getCustomerID(request);
                this.logger.log(Level.INFO, "removing page visited list for userId : " + userId + " customerId : " + customerId);
                msgPro.removeUserDataFromMsgShownList(userId, customerId);
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "error while removing page visited data : ", ex);
        }
    }
    
    public void localeForCurrentlyLoggedInUser(final HttpServletRequest request, final Long userID) {
        final Locale locale = I18NUtil.getLocale(request.getLocale(), userID);
        this.sessionAPI.addToSession(request, "org.apache.struts.action.LOCALE", (Object)locale);
    }
    
    private void postLoginAwsHandling(final HttpServletRequest request, final HttpServletResponse response) {
        this.logger.log(Level.FINE, "AMAZON INSTANCE START-CHANGE PASSWORD");
        try {
            final Locale browserLocale = request.getLocale();
            request.setAttribute("browserLocale", (Object)browserLocale);
            request.setAttribute("AmazonUserLoginSuccess", (Object)"true");
            this.logger.log(Level.INFO, "FORWARD TO CHANGE PASSWORD " + request.getAttribute("AmazonUserLoginSuccess"));
            final RequestDispatcher rd = request.getRequestDispatcher(this.getInitParameter("loginAmazon"));
            rd.forward((ServletRequest)request, (ServletResponse)response);
        }
        catch (final Exception e) {
            this.logger.log(Level.INFO, "Exception while redirecting to change password for amazon user", e);
        }
    }
    
    private void updateLoginTime(final Long time, final Long userID) {
        try {
            SyMUtil.updateUserParameter(userID, "lastLoginTime", String.valueOf(time));
        }
        catch (final Exception ex) {
            this.logger.log(Level.INFO, "Exception while updating login time", ex);
        }
    }
}
