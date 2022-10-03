package com.me.devicemanagement.onpremise.server.license.factory.thresholdvalidator;

import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.logging.Logger;

public class LicenseThresholdValidatorFactoryProvider
{
    static Logger logger;
    private static LicenseThresholdValidator licenseThresholdValidatorObj;
    
    public static LicenseThresholdValidator getLicenseThresholdValidatorImpl() {
        try {
            if (LicenseThresholdValidatorFactoryProvider.licenseThresholdValidatorObj == null) {
                if (SyMUtil.isSummaryServer() || SyMUtil.isProbeServer()) {
                    LicenseThresholdValidatorFactoryProvider.licenseThresholdValidatorObj = (LicenseThresholdValidator)Class.forName("com.me.devicemanagement.onpremise.server.util.summaryserver.summary.DCLicenseThresholdValidator").newInstance();
                }
                else {
                    LicenseThresholdValidatorFactoryProvider.licenseThresholdValidatorObj = (LicenseThresholdValidator)Class.forName("com.me.devicemanagement.onpremise.server.util.DCLicenseThresholdValidator").newInstance();
                }
            }
        }
        catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            LicenseThresholdValidatorFactoryProvider.logger.log(Level.SEVERE, "Exception in getLicenseThresholdValidatorImpl", e);
        }
        return LicenseThresholdValidatorFactoryProvider.licenseThresholdValidatorObj;
    }
    
    static {
        LicenseThresholdValidatorFactoryProvider.logger = Logger.getLogger(LicenseThresholdValidatorFactoryProvider.class.getName());
        LicenseThresholdValidatorFactoryProvider.licenseThresholdValidatorObj = null;
    }
}
