package com.me.devicemanagement.onpremise.server.util;

import java.io.File;
import java.util.Properties;
import com.me.devicemanagement.onpremise.server.fileaccess.FileAccessImpl;
import com.me.devicemanagement.onpremise.start.StartupUtil;

public class SecureConnectionUtil
{
    private static final String ENABLE_SECURE_CONNECTION;
    private static final String DBPARAMSCONF_FILE;
    
    public static void changeDBUrlInDBParams() {
        String dbUrl = null;
        final Properties dbProps = StartupUtil.getProperties(SecureConnectionUtil.DBPARAMSCONF_FILE);
        if (dbProps != null && dbProps.containsKey("url")) {
            dbUrl = dbProps.getProperty("url").trim();
            if (dbUrl.toLowerCase().contains("sqlserver") && SecureConnectionUtil.ENABLE_SECURE_CONNECTION.equals("true") && !dbUrl.contains("ssl=request")) {
                final String newDbUrl = dbUrl + ";ssl=request";
                FileAccessImpl.findAndReplaceStringInFile(SecureConnectionUtil.DBPARAMSCONF_FILE, dbUrl, newDbUrl);
                System.setProperty("backup.dburl", dbUrl);
            }
        }
    }
    
    public static void revertChangedDBUrlInDBParams() {
        final String oldUrl = System.getProperty("backup.dburl");
        if (oldUrl != null) {
            final Properties dbProps = StartupUtil.getProperties(SecureConnectionUtil.DBPARAMSCONF_FILE);
            if (dbProps != null && dbProps.containsKey("url")) {
                final String currentUrl = dbProps.getProperty("url").trim();
                if (!oldUrl.equals(currentUrl)) {
                    FileAccessImpl.findAndReplaceStringInFile(SecureConnectionUtil.DBPARAMSCONF_FILE, currentUrl, oldUrl);
                }
            }
        }
    }
    
    static {
        ENABLE_SECURE_CONNECTION = System.getProperty("enable.secure.connection", "true");
        DBPARAMSCONF_FILE = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
    }
}
