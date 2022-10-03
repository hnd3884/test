package com.me.devicemanagement.onpremise.start.util;

import java.util.Properties;
import com.me.devicemanagement.onpremise.start.DCConsoleOut;
import com.zoho.framework.utils.crypto.EnDecryptAES256Impl;
import com.adventnet.persistence.PersistenceInitializer;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DCExceptionalUpgradeUtil
{
    private static Logger logger;
    
    public static void main(final String[] args) {
        try {
            DCLogUtil.initLogger();
            DCExceptionalUpgradeUtil.logger.log(Level.INFO, "Allow to apply PPM condition based.  Customer input is :  " + args[0]);
            final String serverHome = System.getProperty("server.home");
            final String propertyFile = serverHome + File.separator + "bin" + File.separator + "upgradeOnce.props";
            final Properties props = StartupUtil.getProperties(propertyFile);
            final String buildNumber = args[0];
            DCExceptionalUpgradeUtil.logger.log(Level.INFO, "BuildNumber : " + buildNumber);
            PersistenceInitializer.loadPersistenceConfigurations();
            final String encryptedBuildNumber = new EnDecryptAES256Impl().encrypt(buildNumber, "MLITE_ENCRYPT_DECRYPT");
            DCExceptionalUpgradeUtil.logger.log(Level.INFO, "Encrypted BuildNumber : " + encryptedBuildNumber);
            props.setProperty("build-key", encryptedBuildNumber);
            StartupUtil.storeProperties(props, propertyFile);
            DCExceptionalUpgradeUtil.logger.log(Level.INFO, "you can now upgrade the Desktop Central server.");
            DCConsoleOut.println("\n Property file location :" + propertyFile);
        }
        catch (final Exception ex) {
            DCExceptionalUpgradeUtil.logger.log(Level.WARNING, "Exception occured while generating build key for allow to apply ppm ", ex);
            DCConsoleOut.println("\n" + ex.getMessage() + "\n" + args[0]);
            System.exit(1);
        }
    }
    
    static {
        DCExceptionalUpgradeUtil.logger = Logger.getLogger(DCExceptionalUpgradeUtil.class.getName());
    }
}
