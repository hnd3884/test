package com.me.devicemanagement.onpremise.start.plugin;

import java.util.Hashtable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.CopyOption;
import java.nio.file.Paths;
import java.io.File;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DCPluginUpdator
{
    private static final String DC_PLUGIN_FILE;
    private static Logger logger;
    
    public static void main(final String[] args) {
        try {
            if (args.length == 0) {
                return;
            }
            final String productMode = args[0];
            DCPluginUpdator.logger.log(Level.WARNING, "productMode  " + productMode);
            final String[] productModeValues = productMode.split(";");
            final Properties pluginProps = new Properties();
            boolean isPlugin = false;
            for (int i = 0; i < productModeValues.length; ++i) {
                DCPluginUpdator.logger.log(Level.WARNING, " productModeValues[i]  " + productModeValues[i]);
                final String[] parms = productModeValues[i].split("=");
                final String key = parms[0];
                final String val = parms[1];
                if (key.equalsIgnoreCase("isPlugin") && val.equalsIgnoreCase("true")) {
                    isPlugin = true;
                }
                final String serverDir = SyMUtil.getInstallationDir();
                if (serverDir.contains("ServiceDesk") || serverDir.contains("AssetExplorer")) {
                    isPlugin = true;
                }
                ((Hashtable<String, String>)pluginProps).put(key, val);
            }
            StartupUtil.storeProperties(pluginProps, DCPluginUpdator.DC_PLUGIN_FILE);
            renameWrapperPluginConf(isPlugin);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void renameWrapperPluginConf(final boolean isPlugin) {
        final String backupFile = "../conf/wrapper-plugin-props.conf.bak";
        final String newFile = "../conf/wrapper-plugin-props.conf";
        if (isPlugin) {
            renameFile(backupFile, newFile);
        }
        else {
            renameFile(newFile, backupFile);
        }
    }
    
    private static boolean renameFile(final String source, final String destination) {
        try {
            DCPluginUpdator.logger.log(Level.WARNING, "Source  File : " + source);
            DCPluginUpdator.logger.log(Level.WARNING, "Destination  File : " + destination);
            final File newFile = new File(destination);
            newFile.getParentFile().mkdirs();
            Files.move(Paths.get(source, new String[0]), Paths.get(destination, new String[0]), StandardCopyOption.ATOMIC_MOVE);
            DCPluginUpdator.logger.log(Level.INFO, source + " Folder is successfully renamed to " + destination);
            return true;
        }
        catch (final Exception e) {
            DCPluginUpdator.logger.log(Level.SEVERE, "Exception while renaming the Folder" + e);
            return false;
        }
    }
    
    static {
        DC_PLUGIN_FILE = ".." + File.separator + "conf" + File.separator + "plugin_properties.conf";
        DCPluginUpdator.logger = Logger.getLogger(DCPluginUpdator.class.getName());
    }
}
