package com.me.devicemanagement.onpremise.tools.servertroubleshooter.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.management.ManagementFactory;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class STSToolUtil
{
    private static final Logger LOGGER;
    private static Properties stsProps;
    
    public static Properties getSTSConfFileProps() throws IOException {
        if (STSToolUtil.stsProps == null) {
            STSToolUtil.LOGGER.log(Level.INFO, "Loading STS tool conf file");
            final String stsConfFilePath = System.getProperty("ststool.home") + File.separator + STSToolConstants.STS_CONF_FILE;
            STSToolUtil.stsProps = getPropsFromFile(stsConfFilePath);
            STSToolUtil.LOGGER.log(Level.INFO, "STS tool conf file is loaded");
        }
        return STSToolUtil.stsProps;
    }
    
    public static Properties getPropsFromFile(final String filePath) throws IOException {
        FileInputStream fileFIS = null;
        Properties props = null;
        try {
            props = new Properties();
            fileFIS = new FileInputStream(filePath);
            props.load(fileFIS);
        }
        finally {
            if (fileFIS != null) {
                try {
                    fileFIS.close();
                }
                catch (final IOException e) {
                    STSToolUtil.LOGGER.log(Level.WARNING, "Caught exception while closing InputStream: ", e);
                }
            }
        }
        return props;
    }
    
    public static long getJavaCurrentPid() {
        STSToolUtil.LOGGER.log(Level.FINE, "Getting java pid");
        final String pidWithUsername = ManagementFactory.getRuntimeMXBean().getName();
        final long currentPID = Long.parseLong(pidWithUsername.substring(0, pidWithUsername.indexOf("@")));
        STSToolUtil.LOGGER.log(Level.FINE, "java pid : {0}", currentPID);
        return currentPID;
    }
    
    public static String readFileAsString(final String fileLoc) {
        STSToolUtil.LOGGER.log(Level.INFO, "Reading METracking JSON file");
        FileReader reader = null;
        BufferedReader bufferedReader = null;
        final StringBuilder fileString = new StringBuilder();
        try {
            reader = new FileReader(fileLoc);
            bufferedReader = new BufferedReader(reader);
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                fileString.append(line);
            }
            STSToolUtil.LOGGER.log(Level.FINE, "METracking JSON file is converted to String successfully");
        }
        catch (final Exception ex) {
            STSToolUtil.LOGGER.log(Level.WARNING, "Caught exception in reading METracking JSON file :", ex);
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception ex) {
                STSToolUtil.LOGGER.log(Level.WARNING, "Caught exception in closing reader :", ex);
            }
        }
        finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (reader != null) {
                    reader.close();
                }
            }
            catch (final Exception ex2) {
                STSToolUtil.LOGGER.log(Level.WARNING, "Caught exception in closing reader :", ex2);
            }
        }
        return fileString.toString();
    }
    
    static {
        LOGGER = Logger.getLogger(STSToolUtil.class.getName());
        STSToolUtil.stsProps = null;
    }
}
