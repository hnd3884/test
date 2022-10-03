package com.me.devicemanagement.framework.server.util;

import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.api.DemoUtilAPI;

public abstract class DemoUtil implements DemoUtilAPI
{
    protected static Logger logger;
    
    @Override
    public void loadDemoModeValFromConf() {
        Properties demoProperties = new Properties();
        String demo_mode = null;
        try {
            DemoUtil.logger.log(Level.INFO, "Reading demo conf File.");
            final String fileName = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "demo.conf";
            demoProperties = FileAccessUtil.readProperties(fileName);
            demo_mode = demoProperties.getProperty("demo_mode").trim();
            this.updateDemoMode(demo_mode);
        }
        catch (final Exception ex) {
            DemoUtil.logger.log(Level.WARNING, "Caught exception while Reading demo conf File.", ex);
        }
    }
    
    @Override
    public void updateDemoMode(String demo_mode) {
        DemoUtil.logger.log(Level.INFO, "Updating Demo Mode details in Cache and DB.");
        try {
            if (demo_mode == null) {
                demo_mode = "false";
            }
            final Boolean isDemoMode = Boolean.valueOf(demo_mode);
            SyMUtil.updateSyMParameter("isDemoMode", demo_mode);
            ApiFactoryProvider.getCacheAccessAPI().putCache("IS_DEMO_MODE", demo_mode, 2);
        }
        catch (final Exception ex) {
            DemoUtil.logger.log(Level.WARNING, "Caught exception while updating demo mode.", ex);
        }
    }
    
    static {
        DemoUtil.logger = Logger.getLogger(DemoUtil.class.getName());
    }
}
