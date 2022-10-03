package com.me.ems.framework.uac.filters;

import java.io.IOException;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.uac.api.v1.service.CoreUserService;
import com.adventnet.iam.security.Authenticator;
import javax.servlet.http.HttpServletResponse;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletRequest;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.me.devicemanagement.framework.server.admin.AuthenticationKeyUtil;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.general.UtilAccessAPI;
import com.me.devicemanagement.framework.server.util.ProductClassLoader;
import javax.servlet.FilterConfig;
import java.util.logging.Logger;
import com.adventnet.iam.security.ZSecAuthenticationProviderImpl;

public class AuthenticationProviderImpl extends ZSecAuthenticationProviderImpl
{
    private static Logger logger;
    private String cookieName;
    
    public AuthenticationProviderImpl() {
        this.cookieName = "";
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        try {
            final String serverURLClass = ProductClassLoader.getSingleImplProductClass("DM_UTIL_ACCESS_API_CLASS");
            final UtilAccessAPI utilAccessAPI = (UtilAccessAPI)ApiFactoryProvider.getImplClassInstance("DM_UTIL_ACCESS_API_CLASS");
            this.cookieName = utilAccessAPI.getWebServerSettings().getProperty("apache.catalina.SESSION_COOKIE_NAME");
        }
        catch (final Exception e) {
            AuthenticationProviderImpl.logger.log(Level.SEVERE, "Exception occurred in GET_IAM_COOKIE", e);
            this.cookieName = "";
        }
        super.init(filterConfig);
    }
    
    private Long getLoginIDIFValidAuthToken(final String authToken) {
        Long loginID = null;
        if (authToken == null || authToken.trim().equals("")) {
            return null;
        }
        final DataObject authDO = AuthenticationKeyUtil.getInstance().authenticateAPIKey(authToken, "301");
        try {
            if (authDO != null && !authDO.isEmpty()) {
                final Row authRow = authDO.getRow("APIKeyDetails");
                loginID = (Long)authRow.get("LOGIN_ID");
            }
        }
        catch (final Exception ex) {
            AuthenticationProviderImpl.logger.log(Level.SEVERE, "Exception occurred while fetching loginID from AuthToken", ex);
        }
        return loginID;
    }
    
    public String GET_IAM_COOKIE(final HttpServletRequest request) {
        try {
            if (this.cookieName.isEmpty()) {
                final String serverURLClass = ProductClassLoader.getSingleImplProductClass("DM_UTIL_ACCESS_API_CLASS");
                final UtilAccessAPI utilAccessAPI = (UtilAccessAPI)ApiFactoryProvider.getImplClassInstance("DM_UTIL_ACCESS_API_CLASS");
                this.cookieName = utilAccessAPI.getWebServerSettings().getProperty("apache.catalina.SESSION_COOKIE_NAME");
            }
            return SecurityUtil.getCookie(request, this.cookieName);
        }
        catch (final Exception e) {
            AuthenticationProviderImpl.logger.log(Level.SEVERE, "Exception occurred in GET_IAM_COOKIE", e);
            return "";
        }
    }
    
    public boolean isAuthenticated(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String authToken = request.getHeader("Authorization");
        try {
            if (!com.me.devicemanagement.framework.server.util.SecurityUtil.getNormalizedRequestURI(request).startsWith("/emsapi")) {
                return true;
            }
            final Long loginID = this.getLoginIDIFValidAuthToken(authToken);
            final Long loginIDFromMickey = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
            if (loginID == null && loginIDFromMickey != null) {
                request.setAttribute("ZSEC_AUTH_TYPE", (Object)Authenticator.AUTH_TYPE.PASSWORD.getValue());
                return true;
            }
            if (loginID != null) {
                final User user = new CoreUserService().getLoginDataForUser(loginID);
                final String loginName = user.getName();
                final String domainName = user.getDomainName();
                final Long userID = user.getUserID();
                ApiFactoryProvider.getAuthUtilAccessAPI().setUserCredential(loginName, "system", domainName, userID);
                return true;
            }
            return false;
        }
        catch (final Exception ex) {
            AuthenticationProviderImpl.logger.log(Level.SEVERE, "Exception in isAuthenticated while fetching loginID from Factory Provider", ex);
            return false;
        }
    }
    
    static {
        AuthenticationProviderImpl.logger = Logger.getLogger(AuthenticationProviderImpl.class.getName());
    }
}
