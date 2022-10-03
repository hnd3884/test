package com.adventnet.mfw.logging;

import java.text.DateFormat;
import java.io.PrintStream;
import com.adventnet.mfw.Starter;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.zoho.conf.Configuration;
import com.zoho.conf.AppResources;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.util.Collections;
import java.util.logging.LogManager;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggerUtil
{
    private static Logger LOGGER;
    private static String server_home;
    
    public static void setLoggerLevel(final String loggerName, final Level level, final boolean persistLevel) {
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        try {
            Logger.getLogger(loggerName).setLevel(level);
            ClassLoader tempClassLoader = currentLoader;
            while (tempClassLoader != ClassLoader.getSystemClassLoader()) {
                tempClassLoader = tempClassLoader.getParent();
                Thread.currentThread().setContextClassLoader(tempClassLoader);
                Logger.getLogger(loggerName).setLevel(level);
            }
            if (persistLevel) {
                persistNewLoggerLevel(loggerName, level);
                LoggerUtil.LOGGER.info("New logger level persisted...");
            }
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentLoader);
        }
    }
    
    public static List<Logger> getLogger(final String loggerName) {
        final List<Logger> loggerList = new ArrayList<Logger>();
        final ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        try {
            ClassLoader tempClassLoader = currentLoader;
            while (tempClassLoader != ClassLoader.getSystemClassLoader()) {
                final List<String> loggerNames = LogManager.getLoggingMXBean().getLoggerNames();
                if (loggerNames.contains(loggerName)) {
                    loggerList.add(Logger.getLogger(loggerName));
                }
                tempClassLoader = tempClassLoader.getParent();
                Thread.currentThread().setContextClassLoader(tempClassLoader);
            }
        }
        finally {
            Thread.currentThread().setContextClassLoader(currentLoader);
        }
        return Collections.unmodifiableList((List<? extends Logger>)loggerList);
    }
    
    private static void persistNewLoggerLevel(final String loggerName, final Level level) {
        final String serverHome = LoggerUtil.server_home;
        final String dynamicPropertyFilePath = buildString(serverHome, File.separator, "conf", File.separator, "dynamic-logging.properties");
        final File dynamicPropFile = new File(dynamicPropertyFilePath);
        InputStream dynamicFileIPStream = null;
        OutputStream dynamicPropOPStream = null;
        final Properties prop = new Properties();
        try {
            if (dynamicPropFile.exists()) {
                dynamicFileIPStream = new FileInputStream(dynamicPropertyFilePath);
                prop.load(dynamicFileIPStream);
            }
            prop.setProperty(loggerName + ".level", level.toString());
            dynamicPropOPStream = new FileOutputStream(dynamicPropertyFilePath);
            prop.store(dynamicPropOPStream, "Dynamic logging properties");
        }
        catch (final IOException ex) {
            Logger.getLogger(LoggerUtil.class.getName()).log(Level.SEVERE, null, ex);
            try {
                if (dynamicFileIPStream != null) {
                    dynamicFileIPStream.close();
                }
            }
            catch (final IOException ex) {
                LoggerUtil.LOGGER.log(Level.SEVERE, null, ex);
            }
            try {
                if (dynamicPropOPStream != null) {
                    dynamicPropOPStream.close();
                }
            }
            catch (final IOException ex) {
                LoggerUtil.LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        finally {
            try {
                if (dynamicFileIPStream != null) {
                    dynamicFileIPStream.close();
                }
            }
            catch (final IOException ex2) {
                LoggerUtil.LOGGER.log(Level.SEVERE, null, ex2);
            }
            try {
                if (dynamicPropOPStream != null) {
                    dynamicPropOPStream.close();
                }
            }
            catch (final IOException ex2) {
                LoggerUtil.LOGGER.log(Level.SEVERE, null, ex2);
            }
        }
    }
    
    private static String buildString(final String... strArray) {
        final StringBuilder strBuff = new StringBuilder();
        for (final String string : strArray) {
            strBuff.append(string);
        }
        return strBuff.toString();
    }
    
    public static void initLog(final String name) throws Exception {
        initLog(name, AppResources.getLong("log.file.rotate.size", Long.valueOf(10485760L)), false);
    }
    
    public static void initLog(final String name, final boolean ignoreDateInFileName) throws Exception {
        initLog(name, Long.valueOf(System.getProperty("log.file.rotate.size", "10485760")), ignoreDateInFileName);
    }
    
    public static void initLog(final String name, final long fileSplitSize, final boolean ignoreDateInFileName) throws Exception {
        final String homeDir = LoggerUtil.server_home;
        final String logPropName = homeDir + "/conf/" + name + "log.props";
        Configuration.setString("java.util.logging.config.file", logPropName);
        final Properties props = new Properties();
        props.setProperty(".handlers", "com.adventnet.mfw.logging.ExtendedJDKFileHandler");
        props.setProperty("handlers", "com.adventnet.mfw.logging.ExtendedJDKFileHandler");
        props.setProperty("com.adventnet.mfw.logging.ExtendedJDKFileHandler.level", "INFO");
        final File logFolder = new File(homeDir + "/logs");
        String logFileName = null;
        if (ignoreDateInFileName) {
            logFileName = logFolder.getCanonicalPath() + File.separator + name + "log_%g.txt";
        }
        else {
            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final Date date = new Date();
            logFileName = logFolder.getCanonicalPath() + File.separator + name + "log_" + dateFormat.format(date) + "_%g.txt";
        }
        props.setProperty("com.adventnet.mfw.logging.ExtendedJDKFileHandler.pattern", logFileName);
        props.setProperty("com.adventnet.mfw.logging.ExtendedJDKFileHandler.formatter", "com.adventnet.mfw.logging.DefaultFormatter");
        props.setProperty("com.adventnet.mfw.logging.ExtendedJDKFileHandler.limit", String.valueOf(fileSplitSize));
        props.setProperty("com.adventnet.mfw.logging.ExtendedJDKFileHandler.count", "10");
        props.setProperty("com.adventnet.mfw.logging.ExtendedJDKFileHandler.append", "true");
        FileOutputStream fos = null;
        InputStream in = null;
        try {
            fos = new FileOutputStream(logPropName);
            props.store(fos, "Logger for " + name);
            in = new FileInputStream(logPropName);
            LogsArchiver.flushHandlersInfo();
            LogManager.getLogManager().readConfiguration();
            LoggerUtil.LOGGER = Logger.getLogger(LoggerUtil.class.getName());
            System.setOut(new Starter.SysLogStream(true));
            System.setErr(new Starter.SysLogStream(false));
        }
        finally {
            if (in != null) {
                in.close();
            }
            if (fos != null) {
                fos.close();
            }
            final File logProp = new File(logPropName);
            logProp.delete();
            Configuration.setString("log.enabled", "true");
        }
    }
    
    public static boolean reloadLoggingProperties() throws Exception {
        LoggerUtil.LOGGER.info("Reinitializing logging.properties");
        final String serverHome = LoggerUtil.server_home;
        final String staticLogginPropLoc = AppResources.getString("java.util.logging.config.file");
        final String dynamicPropertyFilePath = buildString(serverHome, File.separator, "conf", File.separator, "dynamic-logging.properties");
        final File dynamicPropFile = new File(dynamicPropertyFilePath);
        boolean isReloaded = false;
        if (dynamicPropFile.exists()) {
            InputStream staticFileIPStream = null;
            InputStream dynamicFileIPSream = null;
            InputStream mergedFileIPStream = null;
            OutputStream mergedFileOPStream = null;
            final String mergedPropertyFilePath = buildString(serverHome, File.separator, "conf", File.separator, "merged-logging.properties");
            try {
                final String staticPropertyFilePath = buildString(staticLogginPropLoc);
                final File mergedPropFile = new File(mergedPropertyFilePath);
                if (mergedPropFile.exists()) {
                    mergedPropFile.delete();
                }
                staticFileIPStream = new FileInputStream(staticPropertyFilePath);
                dynamicFileIPSream = new FileInputStream(dynamicPropertyFilePath);
                final Properties prop = new Properties();
                prop.load(staticFileIPStream);
                prop.load(dynamicFileIPSream);
                mergedFileOPStream = new FileOutputStream(mergedPropertyFilePath);
                prop.store(mergedFileOPStream, "Merged logging properties");
                LoggerUtil.LOGGER.log(Level.INFO, "Properties files merged. Merged file created at ::: {0}", mergedPropertyFilePath);
                mergedFileIPStream = new FileInputStream(mergedPropertyFilePath);
                LogsArchiver.flushHandlersInfo();
                LogManager.getLogManager().reset();
                LogManager.getLogManager().readConfiguration(mergedFileIPStream);
                (LoggerUtil.LOGGER = Logger.getLogger(LoggerUtil.class.getName())).info("logging.properties reinitialized by merged-logging.properties file...");
                isReloaded = true;
            }
            catch (final Exception ex) {
                throw ex;
            }
            finally {
                try {
                    if (staticFileIPStream != null) {
                        staticFileIPStream.close();
                    }
                }
                catch (final IOException ex2) {
                    LoggerUtil.LOGGER.log(Level.SEVERE, null, ex2);
                }
                try {
                    if (dynamicFileIPSream != null) {
                        dynamicFileIPSream.close();
                    }
                }
                catch (final IOException ex2) {
                    LoggerUtil.LOGGER.log(Level.SEVERE, null, ex2);
                }
                try {
                    if (mergedFileOPStream != null) {
                        mergedFileOPStream.close();
                    }
                }
                catch (final IOException ex2) {
                    LoggerUtil.LOGGER.log(Level.SEVERE, null, ex2);
                }
                final File mergedFile = new File(mergedPropertyFilePath);
                if (mergedFile.exists()) {
                    mergedFile.delete();
                }
            }
        }
        else {
            LoggerUtil.LOGGER.info(dynamicPropertyFilePath);
            LoggerUtil.LOGGER.info("dynamic-logging.properties not exist hence reinitialize logger properties ignored");
        }
        return isReloaded;
    }
    
    static {
        LoggerUtil.LOGGER = Logger.getLogger(LoggerUtil.class.getName());
        LoggerUtil.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
