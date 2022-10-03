package com.me.devicemanagement.framework.server.license;

import com.me.devicemanagement.framework.utils.JsonUtils;
import java.util.logging.Level;
import java.io.File;
import org.json.JSONException;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.utils.FrameworkConfigurations;
import java.util.logging.Logger;
import org.json.JSONObject;

public class LicenseConfigurations
{
    private static JSONObject licenseConfigurations;
    protected static Logger logger;
    private static String file_pattern;
    public static String licenseConfigurationFilePath;
    
    public static JSONObject getLicenseConfigurations() throws JSONException {
        final JSONObject frameworkConfigurations = FrameworkConfigurations.getFrameworkConfigurations();
        boolean isValidationNeeded = false;
        if (frameworkConfigurations.has("license_configurations") && ((JSONObject)frameworkConfigurations.get("license_configurations")).has("validation_enabled")) {
            isValidationNeeded = Boolean.parseBoolean(String.valueOf(((JSONObject)frameworkConfigurations.get("license_configurations")).get("validation_enabled")));
        }
        if (isValidationNeeded && ApiFactoryProvider.getLicenseKeyValidatorAPI() != null && !ApiFactoryProvider.getLicenseKeyValidatorAPI().isValidLicenseKey(LicenseConfigurations.licenseConfigurations)) {
            LicenseConfigurations.licenseConfigurations = null;
        }
        return LicenseConfigurations.licenseConfigurations;
    }
    
    static {
        LicenseConfigurations.licenseConfigurations = null;
        LicenseConfigurations.logger = Logger.getLogger(FrameworkConfigurations.class.getName());
        LicenseConfigurations.file_pattern = "license_settings((_[\\S]*)?_[\\d]+)?.json";
        LicenseConfigurations.licenseConfigurationFilePath = ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "configurations";
        try {
            LicenseConfigurations.logger.log(Level.INFO, "Going to Load framework configurations");
            LicenseConfigurations.licenseConfigurations = JsonUtils.loadJsonFile(LicenseConfigurations.licenseConfigurationFilePath, LicenseConfigurations.file_pattern);
        }
        catch (final JSONException jsonExcep) {
            LicenseConfigurations.logger.log(Level.INFO, "Exception while loading license settings json. Exception : ", (Throwable)jsonExcep);
        }
    }
}
