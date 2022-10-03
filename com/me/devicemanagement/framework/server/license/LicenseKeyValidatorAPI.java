package com.me.devicemanagement.framework.server.license;

import org.json.JSONObject;

public interface LicenseKeyValidatorAPI
{
    boolean isValidLicenseKey(final JSONObject p0);
}
