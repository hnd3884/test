package com.me.webclient.filter.security;

import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.iam.security.Authenticator;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.regex.Pattern;
import com.adventnet.iam.security.SecurityUtil;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.framework.uac.filters.AuthenticationProviderImpl;

public class MDMPAuthenticationProviderImpl extends AuthenticationProviderImpl
{
    private static Logger logger;
    private static final String MDMAPIREGEX = "^(\\/api\\/v\\d+\\/mdm\\/.*$)";
    private static final String MDMAPIJERSEYREGEX = "^(\\/api\\/mdm\\/.*$)";
    private static final String EMSAPIREGEX = "/emsapi";
    private static final String VIEWAPIREGEX = ".ec";
    
    public boolean isAuthenticated(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final String requestURI = request.getRequestURI();
            final String normalizedUri = SecurityUtil.getNormalizedURI(requestURI);
            if (requestURI.startsWith("/emsapi") || Pattern.matches("^(\\/api\\/v\\d+\\/mdm\\/.*$)", normalizedUri) || Pattern.matches("^(\\/api\\/mdm\\/.*$)", normalizedUri) || requestURI.endsWith(".ec")) {
                final Long loginIDFromMickey = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginID();
                if (loginIDFromMickey != null) {
                    MDMPAuthenticationProviderImpl.logger.log(Level.INFO, "api validation", requestURI);
                    request.setAttribute("ZSEC_AUTH_TYPE", (Object)Authenticator.AUTH_TYPE.PASSWORD.getValue());
                }
            }
            return true;
        }
        catch (final Exception ex) {
            MDMPAuthenticationProviderImpl.logger.log(Level.SEVERE, "Exception in isAuthenticated while fetching loginID from Factory Provider", ex);
            return false;
        }
    }
    
    static {
        MDMPAuthenticationProviderImpl.logger = Logger.getLogger(MDMPAuthenticationProviderImpl.class.getName());
    }
}
