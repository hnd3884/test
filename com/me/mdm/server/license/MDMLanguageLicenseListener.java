package com.me.mdm.server.license;

import java.util.ArrayList;
import java.util.Map;
import com.adventnet.sym.server.mdm.iosnativeapp.IosNativeAppHandler;
import com.me.mdm.server.notification.NotificationHandler;
import java.util.List;
import com.adventnet.sym.server.mdm.command.DeviceCommandRepository;
import com.adventnet.sym.server.mdm.core.ManagedDeviceHandler;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import java.util.Properties;
import com.me.devicemanagement.framework.server.license.LicenseEvent;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.license.LicenseListener;

public class MDMLanguageLicenseListener implements LicenseListener
{
    Logger logger;
    
    public MDMLanguageLicenseListener() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    public void licenseChanged(final LicenseEvent licenseEvent) {
        final Map lastLicenseInfoMap = licenseEvent.oldLicenseDetails;
        if (lastLicenseInfoMap != null) {
            Properties lastLangLicProps = lastLicenseInfoMap.get("Multilangpack");
            if (lastLangLicProps == null) {
                lastLangLicProps = lastLicenseInfoMap.get("AddOnModules");
            }
            if (lastLangLicProps == null) {
                lastLangLicProps = new Properties();
            }
            final boolean isLastLanglicEnabled = Boolean.parseBoolean(lastLangLicProps.getProperty("Multi-lang-Pack", "false"));
            final boolean isLangLicEnabled = LicenseProvider.getInstance().isLanguagePackEnabled();
            this.logger.info("Last Language License Status and current language license status " + isLastLanglicEnabled + " & " + isLangLicEnabled);
            if (isLastLanglicEnabled != isLangLicEnabled) {
                this.logger.info("Changes in the Language License , so going to add commands ....");
                final ManagedDeviceHandler managedDeviceHandler = ManagedDeviceHandler.getInstance();
                final ArrayList androidResourceId = managedDeviceHandler.getManagedDeviceIDBasedOnVersionCode(2, 28);
                DeviceCommandRepository.getInstance().addLanguageLicenseCommand(androidResourceId, 1);
                try {
                    NotificationHandler.getInstance().SendNotification(androidResourceId, 2);
                }
                catch (final Exception ex) {
                    ex.printStackTrace();
                }
                final ArrayList iosResourceId = managedDeviceHandler.getIOSNativeAppInstalledDeviceResId(null);
                DeviceCommandRepository.getInstance().addLanguageLicenseCommand(iosResourceId, 2);
                IosNativeAppHandler.getInstance().regenerateMEMDMAppInstallCommand();
                final ArrayList windowsResourceId = managedDeviceHandler.getWindowsNativeAppInstalledDeviceResId(null);
                DeviceCommandRepository.getInstance().addLanguageLicenseCommand(windowsResourceId, 2);
            }
        }
    }
}
