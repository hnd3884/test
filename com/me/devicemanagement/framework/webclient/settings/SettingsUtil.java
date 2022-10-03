package com.me.devicemanagement.framework.webclient.settings;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class SettingsUtil
{
    protected static Logger logger;
    public static final String SENDER_NAME_DEFAULT = "Administrator";
    public static final String SENDER_ADDRESS_DEFAULT = "admin@manageengine.com";
    
    public static void clearSessionAttributes(final HttpServletRequest request) {
        try {
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "SETUP_WIZARD");
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "setupList");
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "currentStepNo");
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "wizardForm");
            WebclientAPIFactoryProvider.getSessionAPI().removeSessionAttribute(request, "ManagedResources");
        }
        catch (final Exception e) {
            SettingsUtil.logger.log(Level.SEVERE, "Exception in WebclientAPIFactoryProvider : " + e);
        }
    }
    
    public static boolean isNATConfigured() {
        try {
            boolean isNATConfigured = false;
            final String natAddress = ApiFactoryProvider.getServerSettingsAPI().getNATConfigurationProperties().getProperty("NAT_ADDRESS", "");
            isNATConfigured = !natAddress.equals("");
            return isNATConfigured;
        }
        catch (final Exception ex) {
            SettingsUtil.logger.log(Level.INFO, "Exception while checking if NAT is configured.. {0}", ex);
            return false;
        }
    }
    
    static {
        SettingsUtil.logger = Logger.getLogger(SettingsUtil.class.getName());
    }
}
