package com.me.devicemanagement.onpremise.server.license.factory.thresholdvalidator;

import java.util.Map;

public interface LicenseThresholdValidator
{
    boolean isLicenseThresholdExceeded(final Map<String, Object> p0);
}
