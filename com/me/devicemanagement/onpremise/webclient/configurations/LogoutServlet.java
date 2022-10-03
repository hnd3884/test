package com.me.devicemanagement.onpremise.webclient.configurations;

import javax.servlet.http.HttpSession;
import com.me.devicemanagement.framework.webclient.common.QuickLoadUtil;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.util.I18NUtil;
import java.util.HashMap;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.customer.MSPWebClientUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.message.MessageProvider;
import com.me.devicemanagement.framework.server.eventlog.DCEventLogUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.framework.webclient.cache.SessionAPI;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;

public class LogoutServlet extends HttpServlet
{
    Logger logger;
    SessionAPI sessionAPI;
    
    public void init() throws ServletException {
        this.logger = Logger.getLogger(LogoutServlet.class.getName());
        this.sessionAPI = WebclientAPIFactoryProvider.getSessionAPI();
    }
    
    protected void service(final HttpServletRequest request, final HttpServletResponse response) {
        final DCEventLogUtil eventutil = new DCEventLogUtil();
        final String loginUserName = (String)this.sessionAPI.getSessionAttribute(request, "loginUserName");
        final String scheme = request.getScheme();
        final String hostName = request.getServerName();
        final int port = request.getServerPort();
        final String contextPath = request.getContextPath();
        try {
            final MessageProvider msgPro = MessageProvider.getInstance();
            final Long userId = DMUserHandler.getUserID(loginUserName);
            if (MessageProvider.collectMsgShownToPageList) {
                final Long customerId = MSPWebClientUtil.getCustomerID(request);
                this.logger.log(Level.INFO, "removing page visited for userId " + userId + " customerId " + customerId);
                msgPro.removeUserDataFromMsgShownList(userId, customerId);
            }
            final String remoteHost = ApiFactoryProvider.getDemoUtilAPI().isDemoMode() ? "xxx.xxx.xxx.xxx" : request.getRemoteHost();
            final String logMessage = "dc.config.USER_DISCONNECTED";
            final Object remarksArgs = loginUserName + "@@@" + remoteHost;
            eventutil.addEvent(719, loginUserName, (HashMap)null, logMessage, remarksArgs, false);
            this.logger.log(Level.INFO, I18NUtil.transformRemarks(logMessage, remarksArgs.toString()));
            SyMUtil.deleteUserParameter(userId, "lastLoginTime");
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "error while removing user from msgShownToPageList ", ex);
        }
        final HttpSession session = request.getSession();
        session.invalidate();
        QuickLoadUtil.redirectURL(scheme + "://" + hostName + ":" + port + contextPath + "/configurations", request, response);
    }
}
