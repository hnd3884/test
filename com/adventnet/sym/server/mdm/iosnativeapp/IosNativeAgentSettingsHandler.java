package com.adventnet.sym.server.mdm.iosnativeapp;

import com.adventnet.persistence.Row;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.DBUtil;
import java.util.Properties;
import java.util.logging.Logger;

public class IosNativeAgentSettingsHandler
{
    public Logger logger;
    private static IosNativeAgentSettingsHandler settingsHandler;
    
    public IosNativeAgentSettingsHandler() {
        this.logger = Logger.getLogger("MDMEnrollment");
    }
    
    public static IosNativeAgentSettingsHandler getInstance() {
        if (IosNativeAgentSettingsHandler.settingsHandler == null) {
            IosNativeAgentSettingsHandler.settingsHandler = new IosNativeAgentSettingsHandler();
        }
        return IosNativeAgentSettingsHandler.settingsHandler;
    }
    
    public Properties getIosNativeAgentSettings(final Long customerID) throws Exception {
        final Properties prop = new Properties();
        return prop;
    }
    
    public void addDefaultIosNativeAgentSettings(final Long customerId) {
        final Properties prop = new Properties();
    }
    
    public boolean isIOSNativeAgentEnable(final Long customerId) {
        boolean isNativeAgentEnable = false;
        try {
            final Row iOSSettingsRow = DBUtil.getRowFromDB("IOSAgentSettings", "CUSTOMER_ID", (Object)customerId);
            if (iOSSettingsRow != null) {
                isNativeAgentEnable = (boolean)iOSSettingsRow.get("IS_NATIVE_APP_ENABLE");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, " Exception in isIOSNativeAgentEnable ", ex);
        }
        return isNativeAgentEnable;
    }
    
    static {
        IosNativeAgentSettingsHandler.settingsHandler = null;
    }
}
