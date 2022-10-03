package com.me.devicemanagement.framework.start;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.File;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;

public class StartupUtil
{
    private static final Logger LOGGER;
    private static final Logger WEBSERVER_LOGGER;
    
    public static void findAndReplaceStrings(final String sourceFileName, final String destFileName, final Properties findReplPair, final String keyPrefixSuffix) throws Exception {
        findAndReplaceStrings(sourceFileName, destFileName, findReplPair, keyPrefixSuffix, true);
    }
    
    public static void findAndReplaceStrings(final String sourceFileName, final String destFileName, final Properties findReplPair, final String keyPrefixSuffix, final boolean isQuoteReplacement) throws Exception {
        FileReader freader = null;
        FileWriter fwriter = null;
        try {
            StartupUtil.WEBSERVER_LOGGER.log(Level.INFO, "Invoked  findAndReplaceStringInFile with source fileName: " + sourceFileName + " dest fileName: " + destFileName + " input strings: " + findReplPair);
            final File sourceFile = new File(sourceFileName);
            if (!sourceFile.exists()) {
                StartupUtil.WEBSERVER_LOGGER.log(Level.WARNING, "Source File does not exist. " + sourceFileName);
                throw new FileNotFoundException("File does not exist: " + sourceFileName);
            }
            freader = new FileReader(sourceFileName);
            int read = 0;
            final char[] chBuf = new char[500];
            final StringBuilder strBuilder = new StringBuilder();
            while ((read = freader.read(chBuf)) > -1) {
                strBuilder.append(chBuf, 0, read);
            }
            freader.close();
            String finalStr = strBuilder.toString();
            final Enumeration prkeys = findReplPair.propertyNames();
            while (prkeys.hasMoreElements()) {
                String findStr = prkeys.nextElement();
                final String replaceStr = findReplPair.getProperty(findStr);
                findStr = keyPrefixSuffix + findStr + keyPrefixSuffix;
                if (isQuoteReplacement) {
                    finalStr = finalStr.replace(findStr, replaceStr);
                }
                else {
                    finalStr = finalStr.replaceAll(findStr, replaceStr);
                }
                StartupUtil.WEBSERVER_LOGGER.log(Level.INFO, "Invoked  findAndReplaceStrings findStr: " + findStr + " replaceStr: " + replaceStr);
            }
            fwriter = new FileWriter(destFileName, false);
            fwriter.write(finalStr, 0, finalStr.length());
        }
        catch (final Exception ex) {
            StartupUtil.WEBSERVER_LOGGER.log(Level.SEVERE, "Caught exception in findAndReplaceStrings() source fileName: " + sourceFileName + " dest fileName: " + destFileName + " input strings: " + findReplPair + " exception: " + ex);
            ex.printStackTrace();
            throw ex;
        }
        finally {
            if (freader != null) {
                freader.close();
            }
            if (fwriter != null) {
                fwriter.close();
            }
        }
    }
    
    public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
        if (!(input1 instanceof BufferedInputStream)) {
            input1 = new BufferedInputStream(input1);
        }
        if (!(input2 instanceof BufferedInputStream)) {
            input2 = new BufferedInputStream(input2);
        }
        for (int ch = input1.read(); -1 != ch; ch = input1.read()) {
            final int ch2 = input2.read();
            if (ch != ch2) {
                return false;
            }
        }
        final int ch2 = input2.read();
        return ch2 == -1;
    }
    
    static {
        LOGGER = Logger.getLogger(StartupUtil.class.getName());
        WEBSERVER_LOGGER = Logger.getLogger("WebServerControllerLogger");
    }
}
