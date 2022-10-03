package com.me.devicemanagement.onpremise.start.util;

import java.util.logging.Formatter;
import com.adventnet.sym.logging.EnhancedLogFormatter;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.Iterator;
import java.util.Set;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.io.IOException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DCLogUtil
{
    private static Logger oneLineLogger;
    
    public static void initLogger(final String logFileName) {
        final Properties fileHandlerProps = getDefaultLogFileHandlerProps();
        fileHandlerProps.setProperty("java.util.logging.FileHandler.pattern", "../logs/" + logFileName + "%g.txt");
        try {
            LogManager.getLogManager().readConfiguration(getPropsAsStream(fileHandlerProps));
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    public static void initLogger() {
        final String logFileName = System.getProperty("log.filename");
        if (logFileName != null) {
            initLogger(logFileName);
        }
    }
    
    public static void initLogger(final Properties loggingProperties) {
        final Properties defaultProperties = getDefaultLogFileHandlerProps();
        defaultProperties.putAll(loggingProperties);
        try {
            LogManager.getLogManager().readConfiguration(getPropsAsStream(defaultProperties));
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private static Properties getDefaultLogFileHandlerProps() {
        final Properties props = new Properties();
        props.setProperty("handlers", "java.util.logging.FileHandler");
        props.setProperty("java.util.logging.FileHandler.level", "INFO");
        props.setProperty("java.util.logging.FileHandler.limit", "5000000");
        props.setProperty("java.util.logging.FileHandler.count", "5");
        props.setProperty("java.util.logging.FileHandler.formatter", "java.util.logging.SimpleFormatter");
        props.setProperty("java.util.logging.FileHandler.formatter", "com.me.devicemanagement.onpremise.start.util.DCLogFormatter");
        props.setProperty("java.util.logging.ConsoleHandler.level", "INFO");
        props.setProperty("java.util.logging.ConsoleHandler.formatter", "java.util.logging.SimpleFormatter");
        props.setProperty("java.util.logging.FileHandler.append", "true");
        return props;
    }
    
    private static InputStream getPropsAsStream(final Properties props) throws IOException {
        final Set set = props.entrySet();
        final Iterator iter = set.iterator();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (iter.hasNext()) {
            final Map.Entry entry = iter.next();
            bos.write(entry.toString().getBytes());
            bos.write("\n".getBytes());
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bos.toByteArray());
        return byteArrayInputStream;
    }
    
    public static Logger getOneLineLoggerInstance() {
        if (DCLogUtil.oneLineLogger == null) {
            DCLogUtil.oneLineLogger = Logger.getLogger("oneLineLogger");
            initOneLineLogger();
        }
        return DCLogUtil.oneLineLogger;
    }
    
    private static void initOneLineLogger() {
        DCLogUtil.oneLineLogger.setUseParentHandlers(false);
        try {
            final FileHandler fileHandler = new FileHandler("../logs/onelinelogger_%g.log", true);
            DCLogUtil.oneLineLogger.addHandler(fileHandler);
            final EnhancedLogFormatter formatter = new EnhancedLogFormatter();
            fileHandler.setFormatter((Formatter)formatter);
        }
        catch (final SecurityException e) {
            e.printStackTrace();
        }
        catch (final IOException e2) {
            e2.printStackTrace();
        }
    }
    
    static {
        DCLogUtil.oneLineLogger = null;
    }
}
