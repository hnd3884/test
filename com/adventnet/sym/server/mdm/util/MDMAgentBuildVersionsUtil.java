package com.adventnet.sym.server.mdm.util;

import java.util.Properties;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.util.logging.Level;
import java.io.File;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.HashMap;

public class MDMAgentBuildVersionsUtil
{
    public static HashMap getMDMBuildVersionDetails() {
        final HashMap<String, String> mdmServerVersionDetails = new HashMap<String, String>();
        final String mdmAndroidAgentVer = getMDMAgentInfo("androidagentversion");
        mdmServerVersionDetails.put("mdmAndroidAgentVer", mdmAndroidAgentVer);
        final String mdmSafeAgentVer = getMDMAgentInfo("safeagentversion");
        mdmServerVersionDetails.put("mdmSafeAgentVer", mdmSafeAgentVer);
        final String mdmKnoxAgentVer = getMDMAgentInfo("knoxagentversion");
        mdmServerVersionDetails.put("mdmKnoxAgentVer", mdmKnoxAgentVer);
        final String mdmSamsungAgentVer = mdmSafeAgentVer + " / " + mdmKnoxAgentVer;
        mdmServerVersionDetails.put("mdmSamsungAgentVer", mdmSamsungAgentVer);
        final String mdmWindowsAgentVer = getMDMAgentInfo("windowsagentversion");
        mdmServerVersionDetails.put("mdmWindowsAgentVer", mdmWindowsAgentVer);
        return mdmServerVersionDetails;
    }
    
    public static String getMDMAgentInfo(final String key) {
        String value = null;
        try {
            final String fname = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "mdmagentdetails.conf";
            MDMUtil.logger.log(Level.INFO, "***********getmdmagentinfo***********fname: {0}", fname);
            final Properties props = FileAccessUtil.readProperties(fname);
            value = props.getProperty(key);
            if (value == null) {
                value = "";
            }
        }
        catch (final Exception ex) {
            MDMUtil.logger.log(Level.WARNING, ex, () -> "Caught exception while getting product property: " + s);
        }
        return value;
    }
}
