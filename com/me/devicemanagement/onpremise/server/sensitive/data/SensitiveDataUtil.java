package com.me.devicemanagement.onpremise.server.sensitive.data;

import java.io.IOException;
import java.util.logging.Level;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Logger;

public class SensitiveDataUtil
{
    private static Logger logger;
    private static String sensitiveLoggerPath;
    private static boolean enableSensitiveFilter;
    private static String[] dataHandler;
    static String serverHome;
    
    public static Properties getSensitiveProps() {
        final Properties sensitiveProps = new Properties();
        FileInputStream fis = null;
        try {
            final String sensitiveKeyString = SensitiveDataUtil.serverHome + File.separator + "conf" + File.separator + System.getProperty("sensitive.logger.params", "logger_params.conf");
            fis = new FileInputStream(sensitiveKeyString);
            sensitiveProps.load(fis);
            SensitiveDataUtil.sensitiveLoggerPath = SensitiveDataUtil.serverHome + File.separator + "logs" + File.separator + sensitiveProps.getProperty("logged.sensitive.path", "sensitive-log.txt");
            SensitiveDataUtil.enableSensitiveFilter = Boolean.valueOf(sensitiveProps.getProperty("enable.sensitive.filter", "false"));
            SensitiveDataUtil.dataHandler = sensitiveProps.getProperty("sensitive.data.handlers").split(";");
        }
        catch (final Exception e) {
            SensitiveDataUtil.logger.log(Level.WARNING, "Exception while loading the logger_params.conf ", e);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e2) {
                SensitiveDataUtil.logger.log(Level.WARNING, "Exception while closing the File object ", e2);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final IOException e3) {
                SensitiveDataUtil.logger.log(Level.WARNING, "Exception while closing the File object ", e3);
            }
        }
        return sensitiveProps;
    }
    
    public static String getSensitiveLoggedPath() {
        return SensitiveDataUtil.sensitiveLoggerPath;
    }
    
    public static boolean isSensitiveLogger() {
        return SensitiveDataUtil.enableSensitiveFilter;
    }
    
    public static String[] handlerList() {
        return SensitiveDataUtil.dataHandler;
    }
    
    static {
        SensitiveDataUtil.logger = Logger.getLogger(SensitiveDataUtil.class.getName());
        SensitiveDataUtil.serverHome = System.getProperty("server.home");
        getSensitiveProps();
    }
}
