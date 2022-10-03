package com.me.devicemanagement.framework.server.util;

import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Properties;

public class EMSServiceUtil
{
    public static final String ENABLED = "Enabled";
    
    private static boolean isEnabled(final Properties props) {
        return props != null && !props.isEmpty();
    }
    
    public static boolean isPatchEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("Patch");
        return isEnabled(props);
    }
    
    public static boolean isMDMEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("MobileDevices");
        return isEnabled(props);
    }
    
    public static boolean isVulnerabilityEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("Vulnerability");
        return isEnabled(props) || isVulnEnabledInOldLicenses() || isSecurityEnabled();
    }
    
    public static boolean isSOMEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("SOM");
        return isEnabled(props);
    }
    
    public static boolean isOSDeployerEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("OSDeployer");
        return isEnabled(props);
    }
    
    public static boolean isDCMEnabled() {
        return isDeviceControlEnabled();
    }
    
    public static boolean isVulnEnabledInOldLicenses() {
        boolean bool = false;
        if (LicenseProvider.getInstance().getEMSLicenseVersion() != "11" && LicenseProvider.getInstance().getProductName() != null && LicenseProvider.getInstance().getProductName().equals("ManageEngine Patch Manager Plus")) {
            final Properties props = LicenseProvider.getInstance().getModuleProperties("vulnerability");
            if (props != null && props.containsKey("VULN_ENABLED") && props.getProperty("VULN_ENABLED") != null) {
                bool = Boolean.parseBoolean(props.getProperty("VULN_ENABLED"));
            }
        }
        return bool;
    }
    
    public static boolean isSecurityEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("Security");
        return isEnabled(props);
    }
    
    public static boolean isApplicationControlEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("ApplicationControl");
        return isEnabled(props) || isSecurityEnabled();
    }
    
    public static boolean isDeviceControlEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("DeviceControl");
        return isEnabled(props) || isSecurityEnabled();
    }
    
    public static boolean isBrowserSecurityEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("BrowserSecurity");
        return isEnabled(props) || isSecurityEnabled();
    }
    
    public static boolean isBitLockerEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("BitLocker");
        return isEnabled(props) || isSecurityEnabled();
    }
    
    public static boolean isToolsEnabled() {
        final Properties props = LicenseProvider.getInstance().getModuleProperties("Tools");
        return isEnabled(props);
    }
}
