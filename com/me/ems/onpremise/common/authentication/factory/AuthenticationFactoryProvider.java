package com.me.ems.onpremise.common.authentication.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.webclient.authentication.WebclientAuthentication;
import com.me.ems.framework.common.factory.UnifiedAuthenticationService;

public class AuthenticationFactoryProvider
{
    private static UnifiedAuthenticationService authProvider;
    private static WebclientAuthentication webclientAuthentication;
    private static Logger logger;
    
    public static WebclientAuthentication getWebclientAuthenticationImpl() {
        try {
            if (AuthenticationFactoryProvider.webclientAuthentication == null) {
                if (SyMUtil.isProbeServer()) {
                    AuthenticationFactoryProvider.webclientAuthentication = (WebclientAuthentication)Class.forName("com.me.devicemanagement.onpremise.summaryserver.probe.webclient.authentication.ProbeWebclientAuthenticationImpl").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    AuthenticationFactoryProvider.webclientAuthentication = (WebclientAuthentication)Class.forName("com.me.devicemanagement.onpremise.webclient.authentication.WebclientAuthenticationImpl").newInstance();
                }
                else {
                    AuthenticationFactoryProvider.webclientAuthentication = (WebclientAuthentication)Class.forName("com.me.devicemanagement.onpremise.webclient.authentication.WebclientAuthenticationImpl").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            AuthenticationFactoryProvider.logger.log(Level.SEVERE, "Exception in getting WebclientAuthentication", e);
        }
        return AuthenticationFactoryProvider.webclientAuthentication;
    }
    
    public static UnifiedAuthenticationService getAPIAuthenticationProvider() {
        try {
            if (AuthenticationFactoryProvider.authProvider == null) {
                if (SyMUtil.isProbeServer()) {
                    AuthenticationFactoryProvider.authProvider = (UnifiedAuthenticationService)Class.forName("com.me.dcop.uac.summaryserver.probe.filters.ProbeAPIUnifiedAuthenticationHandler").newInstance();
                }
                else if (SyMUtil.isSummaryServer()) {
                    AuthenticationFactoryProvider.authProvider = (UnifiedAuthenticationService)Class.forName("com.me.dcop.uac.summaryserver.summary.filters.SSAPIAuthenticationHandler").newInstance();
                }
                else {
                    AuthenticationFactoryProvider.authProvider = (UnifiedAuthenticationService)Class.forName("com.me.dcop.uac.filters.DCAPIUnifiedAuthenticationHandler").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            AuthenticationFactoryProvider.logger.log(Level.SEVERE, "Exception while getting Authentication Provider", e);
        }
        return AuthenticationFactoryProvider.authProvider;
    }
    
    public static void resetObjects() {
        AuthenticationFactoryProvider.authProvider = null;
        AuthenticationFactoryProvider.webclientAuthentication = null;
    }
    
    static {
        AuthenticationFactoryProvider.authProvider = null;
        AuthenticationFactoryProvider.webclientAuthentication = null;
        AuthenticationFactoryProvider.logger = Logger.getLogger(AuthenticationFactoryProvider.class.getName());
    }
}
