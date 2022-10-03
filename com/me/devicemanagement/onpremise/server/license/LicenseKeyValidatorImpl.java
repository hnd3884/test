package com.me.devicemanagement.onpremise.server.license;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.json.JSONObject;
import com.me.devicemanagement.framework.server.license.LicenseKeyValidatorAPI;

public class LicenseKeyValidatorImpl implements LicenseKeyValidatorAPI
{
    public boolean isValidLicenseKey(final JSONObject licenseConfigurations) {
        try {
            final JSONObject countJSON = licenseConfigurations.getJSONObject("free_edition_count");
            final String licenseKeyFromJSON = String.valueOf(licenseConfigurations.getJSONObject("free_edition_key").get("key"));
            if (licenseKeyFromJSON != null && licenseKeyFromJSON.trim().length() > 0 && (ApiFactoryProvider.getCryptoAPI() == null || LicenseUtil.encryptLicensekey(countJSON.toString(), 8).equals(licenseKeyFromJSON))) {
                return true;
            }
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
