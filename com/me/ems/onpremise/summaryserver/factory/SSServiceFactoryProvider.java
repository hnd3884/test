package com.me.ems.onpremise.summaryserver.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.onpremise.summaryserver.summary.securitysettings.api.core.SSSecuritySettingsService;
import java.util.logging.Logger;

public class SSServiceFactoryProvider
{
    private static Logger logger;
    
    static {
        SSServiceFactoryProvider.logger = Logger.getLogger("SecurityLogger");
    }
    
    public static class SecuritySettings
    {
        private static SSSecuritySettingsService sSSecuritySettingsService;
        
        public static SSSecuritySettingsService getSecuritySettingsService() {
            try {
                if (SecuritySettings.sSSecuritySettingsService == null) {
                    if (SyMUtil.isProbeServer()) {
                        SecuritySettings.sSSecuritySettingsService = (SSSecuritySettingsService)Class.forName("com.me.ems.onpremise.summaryserver.probe.securitysettings.api.v1.service.PSSecuritySettingsServiceImpl").newInstance();
                    }
                    else {
                        SecuritySettings.sSSecuritySettingsService = (SSSecuritySettingsService)Class.forName("com.me.ems.onpremise.summaryserver.summary.securitysettings.api.v1.service.SSSecuritySettingsServiceImpl").newInstance();
                    }
                }
            }
            catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                SSServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting sSSecuritySettingsServiceObject", e);
            }
            return SecuritySettings.sSSecuritySettingsService;
        }
        
        static {
            SecuritySettings.sSSecuritySettingsService = null;
        }
    }
}
