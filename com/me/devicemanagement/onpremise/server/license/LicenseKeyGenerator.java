package com.me.devicemanagement.onpremise.server.license;

import java.io.FileWriter;
import com.me.devicemanagement.framework.utils.JsonUtils;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;

public class LicenseKeyGenerator
{
    private static String file_pattern;
    public static String licenseConfigurationFileRelativePath;
    public static String licenseConfigurationFilePath;
    
    public static void main(final String[] args) throws IOException, JSONException {
        LicenseKeyGenerator.licenseConfigurationFilePath = ((args.length > 0) ? args[0] : ApiFactoryProvider.getUtilAccessAPI().getServerHome()).concat(File.separator + LicenseKeyGenerator.licenseConfigurationFileRelativePath);
        writeLicenseKeyToJSONFile();
    }
    
    public static JSONObject generateLicenseKey() throws JSONException {
        final JSONObject resultJSON = new JSONObject();
        final JSONObject jsonObj = JsonUtils.loadJsonFile(LicenseKeyGenerator.licenseConfigurationFilePath, LicenseKeyGenerator.file_pattern);
        final JSONObject countJSON = (JSONObject)jsonObj.get("free_edition_count");
        final JSONObject keyJSON = new JSONObject();
        String keyValue = "";
        if (ApiFactoryProvider.getCryptoAPI() != null) {
            keyValue = LicenseUtil.encryptLicensekey(countJSON.toString(), 8);
        }
        keyJSON.put("key", (Object)keyValue);
        resultJSON.put("free_edition_key", (Object)keyJSON);
        resultJSON.put("free_edition_count", (Object)countJSON);
        return resultJSON;
    }
    
    public static void writeLicenseKeyToJSONFile() throws IOException, JSONException {
        FileWriter fileWriter = null;
        try {
            final String licensekey = generateLicenseKey().toString();
            if (licensekey != null && licensekey.trim().length() > 0) {
                fileWriter = new FileWriter(LicenseKeyGenerator.licenseConfigurationFilePath + File.separator + "license_settings.json");
                fileWriter.write(licensekey);
            }
        }
        catch (final IOException ioe) {
            throw ioe;
        }
        finally {
            if (fileWriter != null) {
                fileWriter.flush();
                fileWriter.close();
            }
        }
    }
    
    static {
        LicenseKeyGenerator.file_pattern = "license_settings((_[\\S]*)?_[\\d]+)?.json";
        LicenseKeyGenerator.licenseConfigurationFileRelativePath = "conf" + File.separator + "DeviceManagementFramework" + File.separator + "configurations";
    }
}
