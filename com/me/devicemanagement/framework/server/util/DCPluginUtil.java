package com.me.devicemanagement.framework.server.util;

import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.logging.Logger;

public class DCPluginUtil
{
    protected static DCPluginUtil dcpluginUtil;
    protected static Logger logger;
    private static Boolean isPlugin;
    
    protected DCPluginUtil() {
        this.isPlugin();
    }
    
    private String getPluginProperties(final String key) {
        String value = null;
        try {
            final String fname = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "plugin_properties.conf";
            final Properties props = FileAccessUtil.readProperties(fname);
            DCPluginUtil.logger.log(Level.WARNING, "plugin_properties === {0} ", props);
            value = props.getProperty(key);
        }
        catch (final Exception ex) {
            DCPluginUtil.logger.log(Level.WARNING, "Caught exception while getting property from plugin_properties file : ", ex);
        }
        return value;
    }
    
    public boolean isPlugin() {
        if (DCPluginUtil.isPlugin == null) {
            DCPluginUtil.logger.log(Level.WARNING, "Inside isPlugin method, isPlugin value null. Hence reading from plugin_properties file and set the value");
            final String val = this.getPluginProperties("isPlugin");
            setIsPluginProperty(val);
        }
        return DCPluginUtil.isPlugin;
    }
    
    private static void setIsPluginProperty(final String value) {
        if (value != null && value.equals("true")) {
            DCPluginUtil.isPlugin = Boolean.TRUE;
        }
        else {
            DCPluginUtil.isPlugin = Boolean.FALSE;
        }
    }
    
    public static DCPluginUtil getInstance() {
        if (DCPluginUtil.dcpluginUtil == null) {
            DCPluginUtil.dcpluginUtil = new DCPluginUtil();
        }
        return DCPluginUtil.dcpluginUtil;
    }
    
    static {
        DCPluginUtil.dcpluginUtil = null;
        DCPluginUtil.logger = Logger.getLogger(DCPluginUtil.class.getName());
        DCPluginUtil.isPlugin = null;
    }
}
