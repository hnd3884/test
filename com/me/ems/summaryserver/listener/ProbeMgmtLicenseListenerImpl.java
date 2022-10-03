package com.me.ems.summaryserver.listener;

import java.util.logging.Level;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.license.LicenseEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseListener;

public class ProbeMgmtLicenseListenerImpl implements LicenseListener
{
    private static final Logger LOGGER;
    
    @Override
    public void licenseChanged(final LicenseEvent licenseEvent) {
        ProbeMgmtFactoryProvider.resetObjects();
        ProbeMgmtLicenseListenerImpl.LOGGER.log(Level.INFO, "Objects of ProbeMgmtFactoryProvider were reset");
    }
    
    static {
        LOGGER = Logger.getLogger(ProbeMgmtLicenseListenerImpl.class.getName());
    }
}
