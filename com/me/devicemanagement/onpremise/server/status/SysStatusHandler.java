package com.me.devicemanagement.onpremise.server.status;

import java.net.UnknownHostException;
import java.net.InetAddress;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SysStatusHandler
{
    public static long serverStartTime;
    private static long statusCalculatedTime;
    private static String serverName;
    private static Logger logger;
    
    public void checkSysStatus() throws Exception {
        SysStatusHandler.logger.log(Level.FINER, "Entering into method checkSysStatus()...");
        SysStatusHandler.serverName = fetchServerName();
        setLastStatusCalTime(System.currentTimeMillis());
        logSysStatus();
        SysStatusHandler.logger.log(Level.FINER, "Exiting from method checkSysStatus()...");
    }
    
    private static void logSysStatus() {
        if (ApiFactoryProvider.getDemoUtilAPI().isDemoMode()) {
            logMemoryStatus();
        }
    }
    
    public static void updateServerUptime(final String serverName, final Long startTime, Long endTime) {
        if (startTime != null) {
            SysStatusHandler.serverStartTime = startTime;
        }
        if (endTime == null) {
            endTime = new Long(-1L);
        }
        try {
            final Column serverNameCol = Column.getColumn("ServerUptime", "SERVER_NAME");
            final Criteria criteria = new Criteria(serverNameCol, (Object)serverName, 0);
            final DataObject serverUptimeDO = SyMUtil.getPersistence().get("ServerUptime", criteria);
            if (serverUptimeDO.isEmpty()) {
                final Row row = new Row("ServerUptime");
                row.set("SERVER_NAME", (Object)serverName);
                row.set("START_TIME", (Object)new Long(SysStatusHandler.serverStartTime));
                row.set("END_TIME", (Object)endTime);
                final DataObject serUptimeDO = SyMUtil.getPersistence().constructDataObject();
                serUptimeDO.addRow(row);
                SyMUtil.getPersistence().add(serUptimeDO);
            }
            else {
                final Row row = serverUptimeDO.getFirstRow("ServerUptime");
                row.set("START_TIME", (Object)new Long(SysStatusHandler.serverStartTime));
                row.set("END_TIME", (Object)endTime);
                serverUptimeDO.updateRow(row);
                SyMUtil.getPersistence().update(serverUptimeDO);
            }
        }
        catch (final Exception ex) {
            SysStatusHandler.logger.log(Level.WARNING, "Caught exception while updating server uptime in the DB.", ex);
        }
    }
    
    public static long getServerStartTime() {
        return SysStatusHandler.serverStartTime;
    }
    
    public static boolean getDBStatus() {
        boolean dbStatus = true;
        try {
            SyMUtil.getPersistence().get("SystemParams", (Criteria)null);
        }
        catch (final Exception ex) {
            dbStatus = false;
        }
        return dbStatus;
    }
    
    public static void setLastStatusCalTime(final long time) {
        SysStatusHandler.statusCalculatedTime = time;
    }
    
    public static long getLastStatusCalTime() {
        return SysStatusHandler.statusCalculatedTime;
    }
    
    public static String fetchServerName() {
        String server = null;
        try {
            server = InetAddress.getLocalHost().getHostName();
        }
        catch (final UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        return server;
    }
    
    public static String getServerName() {
        if (SysStatusHandler.serverName == null || SysStatusHandler.serverName.equals("")) {
            SysStatusHandler.serverName = fetchServerName();
        }
        return SysStatusHandler.serverName;
    }
    
    private static void logMemoryStatus() {
        final Logger sysLogger = Logger.getLogger("SysStatusLogger");
        try {
            System.gc();
            final Runtime rt = Runtime.getRuntime();
            final long totalMem = rt.totalMemory();
            final long freeMem = rt.freeMemory();
            final long usedMem = totalMem - freeMem;
            sysLogger.log(Level.INFO, "JVM Memory: Total = " + totalMem + "\t Free = " + freeMem + "\t Used = " + usedMem);
        }
        catch (final Exception ex) {
            sysLogger.log(Level.WARNING, "Caught exception while logging jvm memory status.", ex);
        }
    }
    
    static {
        SysStatusHandler.serverStartTime = -1L;
        SysStatusHandler.statusCalculatedTime = System.currentTimeMillis();
        SysStatusHandler.serverName = null;
        SysStatusHandler.logger = Logger.getLogger(SysStatusHandler.class.getName());
    }
}
