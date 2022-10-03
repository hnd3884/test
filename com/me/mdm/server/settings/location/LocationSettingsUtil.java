package com.me.mdm.server.settings.location;

import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;

public class LocationSettingsUtil
{
    public static String getLocationSettingsProperty(final String key) {
        String value = null;
        try {
            final String fname = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "locationsettings.conf";
            MDMUtil.logger.log(Level.INFO, "***********getLocationSettingsProperty***********fname: {0}", fname);
            final Properties props = FileAccessUtil.readProperties(fname);
            value = props.getProperty(key);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, ex, () -> "Caught exception while getting location settings property: " + s);
        }
        return value;
    }
}
