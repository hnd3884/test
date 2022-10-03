package com.me.devicemanagement.framework.server.deletionfw;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.Properties;
import java.util.List;
import java.util.HashMap;
import java.io.File;
import java.util.logging.Logger;

class DeletionFWProps
{
    private static final Logger LOGGER;
    public static File propsFile;
    public static int chunkThreshold;
    public static int maxRetryCount;
    public static int groupThreshold;
    public static int metrackTopN;
    public static int metrackMDays;
    public static int metrackPCount;
    public static long orphanCountUpdateDuration;
    public static long orphanCleanupDuration;
    public static HashMap<String, List<String>> orphanCleanupSkipMap;
    public static int dbHistoryCleanupDuration_success;
    public static int dbHistoryCleanupDuration_failed;
    public static int dbHistoryCleanupDuration_aborted;
    public static int dbHistoryCleanupDuration_other;
    public static int deletionParamsColumnSize;
    public static final long VERSION = 1L;
    
    private static int getFromProps(final Properties props, final String key, final int defaultValue) {
        try {
            DeletionFWProps.LOGGER.log(Level.FINER, () -> "Reading prop " + s);
            return Integer.parseInt(props.getProperty(key));
        }
        catch (final Exception e) {
            DeletionFWProps.LOGGER.log(Level.SEVERE, e, () -> "Exception while getting value for " + s2 + ". So setting default values = " + n);
            return defaultValue;
        }
    }
    
    private static HashMap<String, List<String>> getOrphanCleanupSkipMap(final Properties props) {
        final String key = "deletion.orphanCleanup.skipTables";
        try {
            DeletionFWProps.LOGGER.log(Level.FINER, () -> "Reading prop " + s);
            final String propStr = props.getProperty(key);
            final String[] tableList = propStr.split(",");
            final HashMap<String, List<String>> result = new HashMap<String, List<String>>();
            for (final String table : tableList) {
                final String parent = table.split("-")[0].toLowerCase();
                final String child = table.split("-")[1].toLowerCase();
                if (!result.containsKey(parent)) {
                    result.put(parent, new LinkedList<String>());
                }
                result.get(parent).add(child);
            }
            return result;
        }
        catch (final Exception e) {
            DeletionFWProps.LOGGER.log(Level.SEVERE, e, () -> "Exception while getting value for " + s2 + ". So setting default empty map ");
            return new HashMap<String, List<String>>();
        }
    }
    
    public static String getFromProps(final String key) throws IOException {
        final Properties lnwProps = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(DeletionFWProps.propsFile);
            lnwProps.load(fis);
            if (lnwProps.containsKey(key)) {
                return lnwProps.getProperty(key);
            }
            return null;
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException e) {
                    DeletionFWProps.LOGGER.log(Level.SEVERE, e, () -> "Exception while closing " + DeletionFWProps.propsFile);
                }
            }
        }
    }
    
    static {
        LOGGER = DeletionTaskUtil.getDeletionFwLogger();
        DeletionFWProps.propsFile = new File(System.getProperty("server.home") + File.separator + "conf" + File.separator + "DeviceManagementFramework" + File.separator + "lnw-params.props");
        final Properties lnwProps = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(DeletionFWProps.propsFile);
            lnwProps.load(fis);
            DeletionFWProps.chunkThreshold = getFromProps(lnwProps, "deletion.chunkThreshold", 20000);
            DeletionFWProps.maxRetryCount = getFromProps(lnwProps, "deletion.maximum.retry", 3);
            DeletionFWProps.groupThreshold = getFromProps(lnwProps, "deletion.groupCount", 20);
            DeletionFWProps.metrackTopN = getFromProps(lnwProps, "deletion.metrack.topN", 3);
            DeletionFWProps.metrackMDays = getFromProps(lnwProps, "deletion.metrack.MDays", 7);
            DeletionFWProps.metrackPCount = getFromProps(lnwProps, "deletion.metrack.PCount", 3);
            DeletionFWProps.orphanCountUpdateDuration = getFromProps(lnwProps, "deletion.metrack.orphanCountUpdateDuration", 7);
            DeletionFWProps.orphanCleanupDuration = getFromProps(lnwProps, "deletion.orphanCleanup.duration", 14);
            DeletionFWProps.orphanCleanupSkipMap = getOrphanCleanupSkipMap(lnwProps);
            DeletionFWProps.dbHistoryCleanupDuration_success = getFromProps(lnwProps, "deletion.dbHistoryCleanup.duration.success", -1);
            DeletionFWProps.dbHistoryCleanupDuration_failed = getFromProps(lnwProps, "deletion.dbHistoryCleanup.duration.failed", -1);
            DeletionFWProps.dbHistoryCleanupDuration_aborted = getFromProps(lnwProps, "deletion.dbHistoryCleanup.duration.aborted", -1);
            DeletionFWProps.dbHistoryCleanupDuration_other = getFromProps(lnwProps, "deletion.dbHistoryCleanup.duration.other", -1);
            DeletionFWProps.deletionParamsColumnSize = getFromProps(lnwProps, "deletion.paramsTable.paramColSize", 2900);
        }
        catch (final Exception e) {
            DeletionFWProps.LOGGER.log(Level.SEVERE, "Exception while loading lnwProps. So setting default values", e);
            DeletionFWProps.chunkThreshold = 20000;
            DeletionFWProps.groupThreshold = 20;
            DeletionFWProps.maxRetryCount = 3;
            DeletionFWProps.metrackTopN = 3;
            DeletionFWProps.metrackMDays = 7;
            DeletionFWProps.metrackPCount = 3;
            DeletionFWProps.orphanCountUpdateDuration = 7L;
            DeletionFWProps.orphanCleanupDuration = 14L;
            DeletionFWProps.orphanCleanupSkipMap = new HashMap<String, List<String>>();
            DeletionFWProps.dbHistoryCleanupDuration_success = -1;
            DeletionFWProps.dbHistoryCleanupDuration_failed = -1;
            DeletionFWProps.dbHistoryCleanupDuration_aborted = -1;
            DeletionFWProps.dbHistoryCleanupDuration_other = -1;
            DeletionFWProps.deletionParamsColumnSize = 2900;
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException e2) {
                    DeletionFWProps.LOGGER.log(Level.SEVERE, e2, () -> "Exception while closing " + DeletionFWProps.propsFile);
                }
            }
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException e3) {
                    DeletionFWProps.LOGGER.log(Level.SEVERE, e3, () -> "Exception while closing " + DeletionFWProps.propsFile);
                }
            }
        }
    }
}
