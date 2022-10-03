package com.me.devicemanagement.framework.server.factory;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.me.ems.framework.securitysettings.api.core.SecuritySettingsService;
import java.util.logging.Logger;

public class ServiceFactoryProvider
{
    private static Logger logger;
    
    static {
        ServiceFactoryProvider.logger = Logger.getLogger("SecurityLogger");
    }
    
    public static class SecuritySettings
    {
        private static SecuritySettingsService securitySettingsService;
        
        public static SecuritySettingsService getSecuritySettingsService() {
            try {
                if (SecuritySettings.securitySettingsService == null) {
                    if (SyMUtil.isProbeServer()) {
                        SecuritySettings.securitySettingsService = (SecuritySettingsService)Class.forName("com.me.ems.onpremise.summaryserver.probe.securitysettings.api.v1.service.PSSecuritySettingsServiceImpl").newInstance();
                    }
                    else if (SyMUtil.isSummaryServer()) {
                        SecuritySettings.securitySettingsService = (SecuritySettingsService)Class.forName("com.me.ems.onpremise.summaryserver.summary.securitysettings.api.v1.service.SSSecuritySettingsServiceImpl").newInstance();
                    }
                    else {
                        SecuritySettings.securitySettingsService = (SecuritySettingsService)Class.forName("com.me.ems.framework.securitysettings.api.v1.service.SecuritySettingsServiceImpl").newInstance();
                    }
                }
            }
            catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                ServiceFactoryProvider.logger.log(Level.SEVERE, "Exception in getting SecuritySettingsServiceObject", e);
            }
            return SecuritySettings.securitySettingsService;
        }
        
        static {
            SecuritySettings.securitySettingsService = null;
        }
    }
}
