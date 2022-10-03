package com.me.devicemanagement.onpremise.webclient.filter;

import javax.servlet.ServletException;
import java.io.IOException;
import com.adventnet.persistence.Row;
import javax.servlet.http.HttpSession;
import com.me.devicemanagement.onpremise.webclient.common.SYMClientUtil;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.ServletRequest;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import javax.servlet.Filter;

public class AuthorizationFilter implements Filter
{
    private static Logger logger;
    
    public void init(final FilterConfig filterConfig) {
    }
    
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest servletRequest = (HttpServletRequest)request;
        final HttpSession session = servletRequest.getSession();
        String roleName = (String)session.getAttribute("roleName");
        try {
            final String loginUserName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
            if (roleName == null) {
                final Long userID = ApiFactoryProvider.getAuthUtilAccessAPI().getUserID();
                final Long accountID = ApiFactoryProvider.getAuthUtilAccessAPI().getAccountID();
                AuthorizationFilter.logger.log(Level.INFO, "Account ID       : " + accountID);
                AuthorizationFilter.logger.log(Level.INFO, "User ID          : " + userID);
                roleName = DMUserHandler.getRoleForUser(loginUserName);
                session.setAttribute("roleName", (Object)roleName);
                session.setAttribute("USER_ID", (Object)userID);
                session.setAttribute("loginID", (Object)accountID);
                session.setAttribute("loginUserName", (Object)loginUserName);
                final int maxInterval = 900;
                final Row userSettingsRow = DMUserHandler.setDefaultProperties(userID);
                final Integer timeout = (Integer)userSettingsRow.get("SESSION_EXPIRY_TIME");
                session.setMaxInactiveInterval((int)timeout);
                session.setAttribute("selectedskin", (Object)SyMUtil.getInstance().getTheme());
                session.setAttribute("title", (Object)ProductUrlLoader.getInstance().getValue("title"));
                AuthorizationFilter.logger.log(Level.INFO, loginUserName + " properties are loaded to session object");
                if (session.getAttribute("isPuginLogin") != null && session.getAttribute("isPuginLogin").equals("PLUGIN_LOGIN")) {
                    session.setAttribute("selectedskin", (Object)SYMClientUtil.getSDPTheme());
                }
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        chain.doFilter(request, response);
    }
    
    public void destroy() {
    }
    
    static {
        AuthorizationFilter.logger = Logger.getLogger(AuthorizationFilter.class.getName());
    }
}
