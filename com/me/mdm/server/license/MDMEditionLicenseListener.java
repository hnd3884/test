package com.me.mdm.server.license;

import java.util.Hashtable;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.HashMap;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.Properties;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.me.devicemanagement.framework.server.license.LicenseEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseListener;

public class MDMEditionLicenseListener implements LicenseListener
{
    private static Logger logger;
    
    public void licenseChanged(final LicenseEvent licenseEvent) {
        try {
            final LicenseProvider licenseProvider = LicenseProvider.getInstance();
            final String edition = licenseProvider.getMDMLicenseAPI().getMDMLiceseEditionType();
            Properties oldLicenseProps = licenseEvent.oldLicenseDetails.get("MobileLicenseEdition");
            if (oldLicenseProps == null) {
                oldLicenseProps = licenseEvent.oldLicenseDetails.get("MobileDevices");
            }
            licenseProvider.getMDMLicenseAPI();
            String previousEdition = "Professional";
            if (oldLicenseProps != null && !oldLicenseProps.isEmpty()) {
                if (oldLicenseProps.containsKey("MDMEdition")) {
                    previousEdition = oldLicenseProps.getProperty("MDMEdition");
                }
                else if (oldLicenseProps.containsKey("Edition")) {
                    previousEdition = oldLicenseProps.getProperty("Edition");
                }
            }
            if (!previousEdition.equalsIgnoreCase(edition)) {
                final String s = edition;
                licenseProvider.getMDMLicenseAPI();
                if (s.equalsIgnoreCase("Standard")) {
                    final Properties taskProps = new Properties();
                    ((Hashtable<String, Long>)taskProps).put("user_id", MDMUtil.getInstance().getLoggedInUserID());
                    final HashMap taskInfoMap = new HashMap();
                    taskInfoMap.put("schedulertime", MDMUtil.getCurrentTimeInMillis());
                    ApiFactoryProvider.getSchedulerAPI().executeAsynchronously("com.me.mdm.server.license.MDMEditionLicenseChangeTask", taskInfoMap, taskProps, "mdmPool");
                }
            }
        }
        catch (final Exception e) {
            MDMEditionLicenseListener.logger.log(Level.SEVERE, "Excpetion in mdm license listener", e);
        }
    }
    
    static {
        MDMEditionLicenseListener.logger = Logger.getLogger(MDMEditionLicenseListener.class.getName());
    }
}
