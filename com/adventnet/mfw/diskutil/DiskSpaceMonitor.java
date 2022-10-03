package com.adventnet.mfw.diskutil;

import java.util.logging.Level;
import java.io.File;
import com.zoho.conf.Configuration;
import java.util.Properties;
import java.util.TimerTask;
import java.io.FileNotFoundException;
import java.util.logging.Logger;
import java.util.Timer;

public class DiskSpaceMonitor
{
    private Timer diskSpaceMonitorScheduler;
    static final Logger LOGGER;
    private static String server_home;
    
    public DiskSpaceMonitor() {
        this.diskSpaceMonitorScheduler = new Timer();
    }
    
    public void createDiskSpaceSchedule(final String path, final long lowerLimit, final long upperLimit, final DiskSpaceMonitorConstants.SPACEUNIT byteType, final long period, final DiskSpaceMonitorConstants.TIMEUNIT periodType, final DiskSpaceMonitorHandler handler) throws Exception {
        final String[] split;
        final String[] pathArr = split = path.split(",");
        for (final String pathToCheck : split) {
            if (!this.isValidFilePath(pathToCheck)) {
                throw new FileNotFoundException("The DiskSpaceMonitoring path {" + pathToCheck + "} specified in system_properties.conf file is not valid. ");
            }
        }
        this.diskSpaceMonitorScheduler.schedule(new DiskSpaceMonitorTask(this.toBytes(byteType, lowerLimit), this.toBytes(byteType, upperLimit), path, handler), 0L, this.toMillis(periodType, period));
    }
    
    public void createDiskSpaceSchedule(final String path, final long upperLimit, final DiskSpaceMonitorConstants.SPACEUNIT byteType, final long period, final DiskSpaceMonitorConstants.TIMEUNIT periodType, final DiskSpaceMonitorHandler handler) throws Exception {
        this.createDiskSpaceSchedule(path, 0L, upperLimit, byteType, period, periodType, handler);
    }
    
    public void createDiskSpaceSchedule(final String path, final long lowerLimit, final long upperLimit, final long period, final DiskSpaceMonitorHandler handler) throws Exception {
        this.createDiskSpaceSchedule(path, lowerLimit, upperLimit, DiskSpaceMonitorConstants.SPACEUNIT.MB, period, DiskSpaceMonitorConstants.TIMEUNIT.SECONDS, handler);
    }
    
    public void createDiskSpaceSchedule(final String path, final long upperLimit, final long period, final DiskSpaceMonitorHandler handler) throws Exception {
        this.createDiskSpaceSchedule(path, upperLimit, DiskSpaceMonitorConstants.SPACEUNIT.MB, period, DiskSpaceMonitorConstants.TIMEUNIT.SECONDS, handler);
    }
    
    public void cancelDiskSpaceScheduler() {
        if (this.diskSpaceMonitorScheduler != null) {
            this.diskSpaceMonitorScheduler.cancel();
            this.diskSpaceMonitorScheduler.purge();
        }
    }
    
    public void createDiskSpaceScheduleFromProps(final Properties props) throws Exception {
        try {
            final long lowerLimit = Long.parseLong(props.getProperty("diskcheck.min", "100"));
            final long upperLimit = Long.parseLong(props.getProperty("diskcheck.max", "200"));
            final long period = Long.parseLong(props.getProperty("diskcheck.period", "30"));
            final String periodTypeStr = props.getProperty("diskcheck.periodtype", "mins");
            final DiskSpaceMonitorConstants.TIMEUNIT periodType = periodTypeStr.equalsIgnoreCase("secs") ? DiskSpaceMonitorConstants.TIMEUNIT.SECONDS : (periodTypeStr.equalsIgnoreCase("mins") ? DiskSpaceMonitorConstants.TIMEUNIT.MINUTES : (periodTypeStr.equalsIgnoreCase("hrs") ? DiskSpaceMonitorConstants.TIMEUNIT.HOURS : DiskSpaceMonitorConstants.TIMEUNIT.SECONDS));
            final String byteTypeStr = props.getProperty("diskcheck.bytetype", "mb");
            final DiskSpaceMonitorConstants.SPACEUNIT byteType = byteTypeStr.equalsIgnoreCase("mb") ? DiskSpaceMonitorConstants.SPACEUNIT.MB : (byteTypeStr.equalsIgnoreCase("gb") ? DiskSpaceMonitorConstants.SPACEUNIT.GB : DiskSpaceMonitorConstants.SPACEUNIT.MB);
            boolean isDbHomePresent = false;
            if (Boolean.getBoolean("db.home") && this.isValidFilePath(Configuration.getString("db.home"))) {
                isDbHomePresent = true;
            }
            String path = props.getProperty("diskcheck.path");
            if (path == null) {
                path = DiskSpaceMonitor.server_home;
                if (isDbHomePresent) {
                    path = path + "," + Configuration.getString("db.home");
                }
            }
            final String handlerClass = props.getProperty("diskcheck.handler", null);
            DiskSpaceMonitorHandler handler = null;
            if (handlerClass != null) {
                handler = (DiskSpaceMonitorHandler)Thread.currentThread().getContextClassLoader().loadClass(handlerClass).newInstance();
            }
            this.createDiskSpaceSchedule(path, lowerLimit, upperLimit, byteType, period, periodType, handler);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    
    private long toBytes(final DiskSpaceMonitorConstants.SPACEUNIT byteType, final long upperLimit) {
        return byteType.equals(DiskSpaceMonitorConstants.SPACEUNIT.BYTES) ? upperLimit : (byteType.equals(DiskSpaceMonitorConstants.SPACEUNIT.MB) ? (upperLimit * 1024L * 1024L) : (byteType.equals(DiskSpaceMonitorConstants.SPACEUNIT.GB) ? (upperLimit * 1024L * 1024L * 1024L) : 0L));
    }
    
    private long toMillis(final DiskSpaceMonitorConstants.TIMEUNIT periodType, final long period) {
        return periodType.equals(DiskSpaceMonitorConstants.TIMEUNIT.MILLIS) ? period : (periodType.equals(DiskSpaceMonitorConstants.TIMEUNIT.SECONDS) ? (period * 1000L) : (periodType.equals(DiskSpaceMonitorConstants.TIMEUNIT.MINUTES) ? (period * 1000L * 60L) : (periodType.equals(DiskSpaceMonitorConstants.TIMEUNIT.HOURS) ? (period * 1000L * 60L * 60L) : 0L)));
    }
    
    private boolean isValidFilePath(final String path) {
        try {
            if (new File(path).exists()) {
                return true;
            }
        }
        catch (final SecurityException ex) {
            DiskSpaceMonitor.LOGGER.log(Level.INFO, "Does not have read permission to access the specified path");
            throw ex;
        }
        return false;
    }
    
    static {
        LOGGER = Logger.getLogger(DiskSpaceMonitor.class.getName());
        DiskSpaceMonitor.server_home = ((Configuration.getString("server.home", ".") != null) ? Configuration.getString("server.home", ".") : Configuration.getString("app.home", "."));
    }
}
