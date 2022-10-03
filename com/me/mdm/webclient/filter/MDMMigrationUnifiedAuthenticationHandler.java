package com.me.mdm.webclient.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.logging.Level;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class MDMMigrationUnifiedAuthenticationHandler implements UnifiedAuthenticationService
{
    protected static Logger logger;
    
    public void init() {
    }
    
    public boolean authentication(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
        boolean isAuth = false;
        try {
            final boolean isEnabled = MDMFeatureParamsHandler.getInstance().isFeatureEnabled("MDMMigration");
            if (isEnabled) {
                String zapikey = httpServletRequest.getParameter("zapikey");
                if (zapikey == null) {
                    zapikey = httpServletRequest.getHeader("zapikey");
                }
                final String decodedZapikey = zapikey;
                final long id = MDMApiFactoryProvider.getMDMAuthTokenUtilAPI().authenticateUser(decodedZapikey);
                isAuth = true;
                MDMMigrationUnifiedAuthenticationHandler.logger.log(Level.INFO, "MDMMigration UnifiedAuth: Successfully authenticated userID " + id);
            }
            else {
                MDMMigrationUnifiedAuthenticationHandler.logger.log(Level.INFO, "MDMMigration UnifiedAuth: Migration feature params not enabled");
            }
        }
        catch (final Exception e) {
            MDMMigrationUnifiedAuthenticationHandler.logger.log(Level.SEVERE, "MDMMigration UnifiedAuth: Error while authenticating zapikey: ", e);
        }
        return isAuth;
    }
    
    public boolean authorization(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse) throws ServletException, IOException {
        return true;
    }
    
    static {
        MDMMigrationUnifiedAuthenticationHandler.logger = Logger.getLogger("MDMLogger");
    }
}
