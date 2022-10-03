package com.me.mdm.webclient.filter;

import java.io.IOException;
import com.adventnet.iam.security.ActionRule;
import java.util.logging.Level;
import com.adventnet.iam.security.IAMSecurityException;
import com.adventnet.iam.security.SecurityRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.FilterConfig;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;
import java.util.Hashtable;
import java.util.logging.Logger;
import com.adventnet.iam.security.ZSecAuthenticationProviderImpl;

public class MDMUnifiedAuthenticationProviderImpl extends ZSecAuthenticationProviderImpl
{
    Logger logger;
    public Hashtable<String, UnifiedAuthenticationService> authenticatorClassesMap;
    
    public MDMUnifiedAuthenticationProviderImpl() {
        this.logger = Logger.getLogger("MDMLogger");
        this.authenticatorClassesMap = new Hashtable<String, UnifiedAuthenticationService>();
    }
    
    public void init(final FilterConfig filterConfig) throws ServletException {
        this.authenticatorClassesMap.putAll(AuthenticationHandlerUtil.getAuthenticationHandlers());
        final Enumeration<UnifiedAuthenticationService> enumeration = this.authenticatorClassesMap.elements();
        while (enumeration.hasMoreElements()) {
            enumeration.nextElement().init();
        }
    }
    
    public boolean isAuthenticated(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Boolean isAuthenticated = false;
        Boolean isAuthorized = false;
        try {
            final ActionRule rule = SecurityRequestWrapper.getInstance(request).getURLActionRule();
            String authType = rule.getCustomAttribute("auth-handler");
            if (authType == null || authType.isEmpty()) {
                authType = "default";
            }
            if (!rule.isAuthenticationRequired()) {
                return true;
            }
            final UnifiedAuthenticationService authObj = this.authenticatorClassesMap.get(authType);
            isAuthenticated = authObj.authentication(request, response);
            if (!isAuthenticated) {
                return false;
            }
            isAuthorized = authObj.authorization(request, response);
            if (isAuthorized) {
                return true;
            }
            throw new IAMSecurityException("UNAUTHORISED");
        }
        catch (final IAMSecurityException ex) {
            this.logger.log(Level.SEVERE, "Rejecting the URI due to IAM Exception in isAuthenticated ", (Throwable)ex);
            throw ex;
        }
        catch (final Exception ex2) {
            this.logger.log(Level.SEVERE, "Exception in isAuthenticatedFlag from Authentication Provider", ex2);
            return false;
        }
    }
}
