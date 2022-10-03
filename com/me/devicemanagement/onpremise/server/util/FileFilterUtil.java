package com.me.devicemanagement.onpremise.server.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class FileFilterUtil
{
    public static final int LOG_FILE = 100;
    private static Logger logger;
    private static String[] logFileExtns;
    private static String[] logFileExcludes;
    
    private FileFilterUtil() {
    }
    
    public static boolean isAllowed(final String fileName, final int filterType) {
        try {
            if (fileName == null) {
                return false;
            }
            final String[] fileExcludes = getExcludes(filterType);
            if (fileExcludes != null) {
                for (int excLen = fileExcludes.length, s = 0; s < excLen; ++s) {
                    if (fileName.toLowerCase().contains(fileExcludes[s].toLowerCase())) {
                        return false;
                    }
                }
            }
            final String[] allowedExtns = getExtensions(filterType);
            if (allowedExtns == null) {
                FileFilterUtil.logger.log(Level.WARNING, "Allowed Extension is null for the file: " + fileName + "\t with filter type: " + filterType + "\t returning true");
                return true;
            }
            final int lastidx = fileName.lastIndexOf(".");
            if (lastidx == -1) {
                return false;
            }
            final String extStr = fileName.substring(lastidx + 1, fileName.length());
            for (int len = allowedExtns.length, s2 = 0; s2 < len; ++s2) {
                if (allowedExtns[s2].equalsIgnoreCase(extStr)) {
                    return true;
                }
            }
        }
        catch (final Exception ex) {
            FileFilterUtil.logger.log(Level.WARNING, "Caught exception while checking the file: " + fileName + "\t with filter type: " + filterType, ex);
        }
        return false;
    }
    
    private static String[] getExtensions(final int filterType) {
        if (filterType == 100) {
            if (FileFilterUtil.logFileExtns == null) {
                setLogFileExtns();
            }
            return FileFilterUtil.logFileExtns;
        }
        return null;
    }
    
    private static String[] getExcludes(final int filterType) {
        if (filterType == 100) {
            if (FileFilterUtil.logFileExtns == null) {
                setLogFileExcludes();
            }
            return FileFilterUtil.logFileExcludes;
        }
        return null;
    }
    
    private static void setLogFileExtns() {
        final String extns = SyMUtil.getProductProperty("logfileextension");
        FileFilterUtil.logger.log(Level.INFO, "Log Extensions retrieved from product.conf is: " + extns);
        if (extns != null && extns.trim().length() > 0) {
            FileFilterUtil.logFileExtns = extns.trim().split(",");
        }
        else {
            FileFilterUtil.logger.log(Level.INFO, "Log Extensions retrieved from product.conf is empty.");
        }
    }
    
    private static void setLogFileExcludes() {
        final String excludes = SyMUtil.getProductProperty("logfileexclude");
        FileFilterUtil.logger.log(Level.INFO, "Log Excludes retrieved from product.conf is: " + excludes);
        if (excludes != null && excludes.trim().length() > 0) {
            FileFilterUtil.logFileExcludes = excludes.trim().split(",");
        }
        else {
            FileFilterUtil.logger.log(Level.INFO, "Log Excludes retrieved from product.conf is empty.");
        }
    }
    
    static {
        FileFilterUtil.logger = Logger.getLogger(FileFilterUtil.class.getName());
        FileFilterUtil.logFileExtns = null;
        FileFilterUtil.logFileExcludes = null;
    }
}
