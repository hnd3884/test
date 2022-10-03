package com.me.devicemanagement.onpremise.server.util.summaryserver.summary;

import java.util.Map;
import com.me.devicemanagement.onpremise.server.license.factory.thresholdvalidator.LicenseThresholdValidator;

public class DCLicenseThresholdValidator implements LicenseThresholdValidator
{
    @Override
    public boolean isLicenseThresholdExceeded(final Map<String, Object> licenseDetails) {
        return false;
    }
}
