package com.me.devicemanagement.onpremise.server.service;

import java.util.Hashtable;
import java.util.Map;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.devicemanagement.onpremise.start.StartupUtil;
import java.io.FileOutputStream;
import com.adventnet.persistence.fos.FOS;
import java.util.Properties;
import java.util.logging.Level;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.adventnet.persistence.DataAccess;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import java.io.File;
import java.util.Iterator;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.mfw.bean.BeanUtil;
import com.adventnet.persistence.Persistence;
import com.me.devicemanagement.onpremise.server.util.DBUtil;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.net.InetAddress;
import com.me.devicemanagement.onpremise.server.util.SyMUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.me.devicemanagement.framework.server.logger.SyMLogger;
import java.util.logging.Logger;

public class DCServerBuildHistoryProvider
{
    private static Logger logger;
    private static final int EXE = 1;
    private static String sourceClass;
    private static DCServerBuildHistoryProvider dcSBHProvider;
    private static Long currentStartTime;
    private static final int UPTIME_HISTORY_MAINTAIN_DAYS = 365;
    private static final String PRODUCT_INSTALLATION_IDENTITY_STRING = "PRODUCT_INSTALLATION_IDENTITY_STRING";
    private static final String SERVER_MIGRATE_CONF_FILE;
    private static final String APACHE_CONF;
    private static final String MOD_REWRITE_CONF_FILE;
    private static final String SSL_MOD_REWRITE_CONF_FILE;
    private static final String NGINX_MOD_REWRITE_CONF_FILE;
    
    private DCServerBuildHistoryProvider() {
        final String sourceMethod = "DCServerBuildHistoryProvider";
        SyMLogger.info(DCServerBuildHistoryProvider.logger = Logger.getLogger("DCServiceLogger"), DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Creating instance...");
    }
    
    public static String getDate(final long dateVal) {
        final Date date = new Date(dateVal);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm:ss a");
        return dateFormat.format(date);
    }
    
    public static synchronized DCServerBuildHistoryProvider getInstance() {
        if (DCServerBuildHistoryProvider.dcSBHProvider == null) {
            DCServerBuildHistoryProvider.dcSBHProvider = new DCServerBuildHistoryProvider();
        }
        return DCServerBuildHistoryProvider.dcSBHProvider;
    }
    
    public void updateDCServerStartup() {
        final String sourceMethod = "updateDCServerStartup";
        try {
            DCServerBuildHistoryProvider.currentStartTime = new Long(System.currentTimeMillis());
            SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Going to update the DC Uptime history with startTime: " + DCServerBuildHistoryProvider.currentStartTime);
            final String buildNoStr = SyMUtil.getProductProperty("buildnumber");
            final Integer buildNumber = new Integer(buildNoStr);
            this.updateDCUptimeHistory(DCServerBuildHistoryProvider.currentStartTime, null, buildNumber);
            this.updateNewBuildDetection(DCServerBuildHistoryProvider.currentStartTime, buildNumber);
            this.cleanupDCServerUptimeHistory();
            this.updateProductInstallationIdentityString();
            this.logBuildNumbersHistory();
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while updating DC server startTime for history maintenance.", (Throwable)ex);
        }
    }
    
    public void updateDCServerShutdown() {
        final String sourceMethod = "updateDCServerShutdown";
        try {
            final Long shutdownTime = new Long(System.currentTimeMillis());
            SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Going to update the DC Uptime history with shutdownTime: " + shutdownTime);
            final String buildNoStr = SyMUtil.getProductProperty("buildnumber");
            this.updateDCUptimeHistory(DCServerBuildHistoryProvider.currentStartTime, shutdownTime, new Integer(buildNoStr));
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while updating DC server shutdownTime for history maintenance.", (Throwable)ex);
        }
    }
    
    private void updateDCUptimeHistory(final Long startTime, final Long shutdownTime, final Integer buildNumber) {
        final String sourceMethod = "updateDCUptimeHistory";
        SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "updateDCUptimeHistory() is invoked startTime: " + startTime + " shutdownTime: " + shutdownTime + " buildNumber: " + buildNumber);
        try {
            final String tblName = "DCServerUptimeHistory";
            final String serverName = InetAddress.getLocalHost().getHostName();
            if (shutdownTime != null) {
                final Criteria startCri = new Criteria(Column.getColumn(tblName, "START_TIME"), (Object)startTime, 0);
                final DataObject resultDO = SyMUtil.getPersistence().get(tblName, startCri);
                if (!resultDO.isEmpty()) {
                    SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "updateDCUptimeHistory row already exists in DB, going to update now.");
                    final Row hrow = resultDO.getRow(tblName);
                    hrow.set("SHUTDOWN_TIME", (Object)shutdownTime);
                    hrow.set("SHUTDOWN_TIME_STR", (Object)getDate(shutdownTime));
                    final Long diffMins = this.getDiffInMinutesFromMs(startTime, shutdownTime);
                    hrow.set("TOTAL_UPTIME_MIN", (Object)diffMins);
                    hrow.set("TOTAL_UPTIME_STR", (Object)this.convertMinutesToDays(diffMins));
                    resultDO.updateRow(hrow);
                    SyMUtil.getPersistence().update(resultDO);
                }
                else {
                    SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "updateDCUptimeHistory row doesn't exist in DB, going to add now. It must be a shutdown event.");
                    final Row hrow = new Row(tblName);
                    hrow.set("SERVER_HOST_NAME", (Object)serverName);
                    hrow.set("START_TIME", (Object)startTime);
                    hrow.set("START_TIME_STR", (Object)getDate(startTime));
                    hrow.set("SHUTDOWN_TIME", (Object)shutdownTime);
                    hrow.set("SHUTDOWN_TIME_STR", (Object)getDate(shutdownTime));
                    final Long diffMins = this.getDiffInMinutesFromMs(startTime, shutdownTime);
                    hrow.set("TOTAL_UPTIME_MIN", (Object)diffMins);
                    hrow.set("TOTAL_UPTIME_STR", (Object)this.convertMinutesToDays(diffMins));
                    hrow.set("BUILD_NUMBER", (Object)buildNumber);
                    resultDO.addRow(hrow);
                    SyMUtil.getPersistence().add(resultDO);
                }
            }
            else {
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "updateDCUptimeHistory row doesn't exist in DB, going to add now. It must be a startup event.");
                final Row hrow2 = new Row(tblName);
                hrow2.set("SERVER_HOST_NAME", (Object)serverName);
                hrow2.set("START_TIME", (Object)startTime);
                hrow2.set("START_TIME_STR", (Object)getDate(startTime));
                hrow2.set("BUILD_NUMBER", (Object)buildNumber);
                final DataObject histDO = SyMUtil.getPersistence().constructDataObject();
                histDO.addRow(hrow2);
                SyMUtil.getPersistence().add(histDO);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while updating DC uptime history with startTime: " + startTime + " shutdownTime: " + shutdownTime + " buildNumber: " + buildNumber, (Throwable)ex);
        }
    }
    
    private Long getDiffInMinutesFromMs(final Long startTime, final Long endTime) {
        final Long diffMin = (endTime - startTime) / 60000L;
        return diffMin;
    }
    
    private String convertMinutesToDays(final Long mins) {
        String resultStr = "";
        final Long oneDayInMins = new Long(1440L);
        final Long oneHourInMins = new Long(60L);
        final Long days = mins / oneDayInMins;
        final Long remainderHrs = mins % oneDayInMins;
        final Long hrs = remainderHrs / oneHourInMins;
        final Long remainderMins = remainderHrs % oneHourInMins;
        if (days > 0L) {
            final String unit = (days > 1L) ? "days" : "day";
            resultStr = resultStr + days + " " + unit + ", ";
        }
        if (hrs > 0L) {
            final String unit = (hrs > 1L) ? "hours" : "hour";
            resultStr = resultStr + hrs + " " + unit + ", ";
        }
        final String unit = (remainderMins > 1L) ? "minutes" : "minute";
        resultStr = resultStr + remainderMins + " " + unit;
        return resultStr;
    }
    
    public Integer getCurrentBuildNumberFromDB() {
        final String sourceMethod = "getCurrentBuildNumberFromDB";
        Integer currBuildNo = null;
        try {
            currBuildNo = (Integer)DBUtil.getMaxOfValue("DCServerBuildHistory", "BUILD_NUMBER", (Criteria)null);
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while retrieving current buildNumber from DB. ", (Throwable)ex);
        }
        return currBuildNo;
    }
    
    private void updateNewBuildDetection(final Long startTime, final Integer buildNumber) {
        final String sourceMethod = "updateNewBuildDetection";
        SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "updateNewBuildDetection() is invoked startTime: " + startTime + " buildNumber: " + buildNumber);
        try {
            final String tblName = "DCServerBuildHistory";
            Row bhRow = new Row(tblName);
            bhRow.set("BUILD_NUMBER", (Object)buildNumber);
            final DataObject resultDO = SyMUtil.getPersistence().get(tblName, bhRow);
            if (resultDO.isEmpty()) {
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Going to add new entry in DCServerBuildHistory. This must be the first startup with the build number: " + buildNumber);
                bhRow.set("BUILD_DETECTED_AT", (Object)startTime);
                bhRow.set("BUILD_DETECTED_AT_STR", (Object)getDate(startTime));
                bhRow.set("REMARKS", (Object)("addedBy=DCService, addedTime=" + getDate(startTime)));
                bhRow.set("BUILD_TYPE", (Object)1);
                resultDO.addRow(bhRow);
                SyMUtil.getPersistence().add(resultDO);
            }
            else {
                bhRow = resultDO.getRow(tblName);
                final Long buildDetectedTime = (Long)bhRow.get("BUILD_DETECTED_AT");
                if (buildDetectedTime == -1L) {
                    final Persistence persistence = (Persistence)BeanUtil.lookup("Persistence");
                    SelectQuery query = (SelectQuery)new SelectQueryImpl(new Table(tblName));
                    query.addSelectColumn(Column.getColumn((String)null, "*"));
                    query.addSortColumn(new SortColumn(Column.getColumn(tblName, "BUILD_DETECTED_AT"), false));
                    final DataObject sortedDO = persistence.get(query);
                    final Row dcServerBuildHistory = sortedDO.getFirstRow(tblName);
                    int lastUpdatedBuildNumber = 0;
                    final long latest_build_detected_time = (long)dcServerBuildHistory.get("BUILD_DETECTED_AT");
                    if (latest_build_detected_time > 0L) {
                        lastUpdatedBuildNumber = (int)dcServerBuildHistory.get("BUILD_NUMBER");
                    }
                    SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Going to update DCServerBuildHistory. This must be the first startup after the upgrade to the build number: " + buildNumber);
                    query = (SelectQuery)new SelectQueryImpl(new Table(tblName));
                    final Criteria c = new Criteria(new Column(tblName, "BUILD_NUMBER"), (Object)lastUpdatedBuildNumber, 5);
                    query.addSelectColumn(Column.getColumn((String)null, "*"));
                    query.addSortColumn(new SortColumn(Column.getColumn(tblName, "BUILD_NUMBER"), true));
                    query.setCriteria(c);
                    final DataObject updateDO = persistence.get(query);
                    if (!updateDO.isEmpty()) {
                        final Iterator it = updateDO.getRows(tblName, (Criteria)null);
                        while (it.hasNext()) {
                            final Row dcServerBuildHistoryRow = it.next();
                            dcServerBuildHistoryRow.set("BUILD_DETECTED_AT", (Object)startTime);
                            dcServerBuildHistoryRow.set("BUILD_DETECTED_AT_STR", (Object)getDate(startTime));
                            String remarks = (String)dcServerBuildHistoryRow.get("REMARKS");
                            if (remarks.contains("addedBy=StandAlonePersistenceExtn")) {
                                dcServerBuildHistoryRow.set("BUILD_TYPE", (Object)1);
                            }
                            remarks = remarks + ", updatedBy=DCService, updatedTime=" + getDate(startTime);
                            dcServerBuildHistoryRow.set("REMARKS", (Object)remarks);
                            updateDO.updateRow(dcServerBuildHistoryRow);
                            persistence.update(updateDO);
                        }
                    }
                }
                else {
                    SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Build number already exists in DB, no update is required.");
                }
            }
            this.updateNewJarPPMDetection(startTime, buildNumber);
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while updating DC new build detection with startTime: " + startTime + " buildNumber: " + buildNumber, (Throwable)ex);
        }
    }
    
    private void updateNewJarPPMDetection(final Long startTime, final Integer buildNumber) {
        final String sourceMethod = "updateNewJarPPMDetection";
        SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "updateNewJarPPMDetection() is invoked startTime: " + startTime + " buildNumber: " + buildNumber);
        try {
            final String qppmBuildHistoryProps = System.getProperty("server.home") + File.separator + "conf" + File.separator + "User-Conf" + File.separator + "qppm_build_history_details.props";
            if (new File(qppmBuildHistoryProps).exists()) {
                final Properties props = FileAccessUtil.readProperties(qppmBuildHistoryProps);
                final DataObject dataObject = (DataObject)new WritableDataObject();
                final Row dmJarBuildHistoryRow = new Row("DMJarBuildHistory");
                dmJarBuildHistoryRow.set("BUILD_NUMBER", (Object)((Hashtable<K, String>)props).get("BUILD_NUMBER"));
                dmJarBuildHistoryRow.set("BUILD_INSTALLED_AT", (Object)((Hashtable<K, String>)props).get("BUILD_INSTALLED_AT"));
                dmJarBuildHistoryRow.set("BUILD_DETECTED_AT", (Object)startTime);
                dmJarBuildHistoryRow.set("BUILD_DETECTED_AT_STR", (Object)getDate(startTime));
                final String remarks = ((Hashtable<K, String>)props).get("REMARKS") + ", type=JarPPM, updatedBy=DCService, updatedTime=" + getDate(startTime);
                dmJarBuildHistoryRow.set("REMARKS", (Object)remarks);
                dmJarBuildHistoryRow.set("BUILD_TYPE", (Object)((Hashtable<K, String>)props).get("BUILD_TYPE"));
                dataObject.addRow(dmJarBuildHistoryRow);
                DataAccess.add(dataObject);
                Files.deleteIfExists(Paths.get(qppmBuildHistoryProps, new String[0]));
                DCServerBuildHistoryProvider.logger.log(Level.INFO, "Jar build details added to 'DMJarBuildHistory' table" + remarks);
            }
            else {
                DCServerBuildHistoryProvider.logger.log(Level.INFO, "Jar build history file not founded, There are no QPPM's applied in last service restart.");
            }
        }
        catch (final Exception e) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Exception occured while updating the table DMJarBuildHistory ", (Throwable)e);
        }
    }
    
    private void cleanupDCServerUptimeHistory() {
        final String sourceMethod = "cleanupDCServerUptimeHistory";
        SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "cleanupDCServerUptimeHistory() is invoked.");
        long startTimeToCleanup = -1L;
        try {
            final long currentTime = System.currentTimeMillis();
            final long historyPeriodInMs = 31536000000L;
            startTimeToCleanup = currentTime - historyPeriodInMs;
            SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Going to clean up the DC server uptime history entries for the records whose startTime is less than : " + startTimeToCleanup + " = " + getDate(startTimeToCleanup));
            final Criteria delCri = new Criteria(Column.getColumn("DCServerUptimeHistory", "START_TIME"), (Object)startTimeToCleanup, 7);
            DataAccess.delete(delCri);
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while cleaning up the DC server uptime history entries for the records whose startTime is less than : " + startTimeToCleanup + " = " + getDate(startTimeToCleanup), (Throwable)ex);
        }
    }
    
    private void updateProductInstallationIdentityString() {
        final String sourceMethod = "updateProductInstallationIdentityString";
        try {
            final String serverHostName = InetAddress.getLocalHost().getHostName();
            final String prInstIdStr = serverHostName + "\\\\" + SyMUtil.getInstallationDir();
            final Boolean isFosEnabled = FOS.isEnabled();
            SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "ProductInstallationIdentityString is: " + prInstIdStr);
            final String prInstIdStrFromDB = SyMUtil.getSyMParameter("PRODUCT_INSTALLATION_IDENTITY_STRING");
            SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "ProductInstallationIdentityString retrieved from DB is: " + prInstIdStrFromDB);
            if (prInstIdStrFromDB == null || prInstIdStrFromDB.trim().length() == 0) {
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "ProductInstallationIdentityString retrieved from DB is empty. This must be the first server startup after the installation...");
                SyMUtil.updateSyMParameter("PRODUCT_INSTALLATION_IDENTITY_STRING", prInstIdStr);
            }
            else if (!isFosEnabled && !prInstIdStrFromDB.equalsIgnoreCase(prInstIdStr)) {
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "########################################################################");
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "###################     N E E D   A T T E N T I O N    #################");
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "ProductInstallationIdentityString retrieved from DB is different from what is expected. This must be a first startup after the server migration. Otherwise DB/Setup files might have been copied from some other setup. This will lead to critical problems.");
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "########################################################################");
                SyMUtil.updateSyMParameter("PRODUCT_INSTALLATION_IDENTITY_STRING", prInstIdStr);
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "ProductInstallationIdentityString retrieved from DB is different from what is expected. This must be a first startup after the server migration. So clear rewrite conf contents to avoid apache redirection.");
                this.updateNewServerDetailsAfterServerMigration();
                clearModRewriteConf();
                this.resetSystemHWType();
                this.resetAVtest();
                this.setServerMigrationFlag();
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while updating the product installation identity string.", (Throwable)ex);
        }
    }
    
    private void resetAVtest() {
    }
    
    private void setServerMigrationFlag() {
        SyMUtil.updateServerParameter("isMigratedSetup", Boolean.TRUE.toString());
    }
    
    private void resetSystemHWType() {
        try {
            DCServerBuildHistoryProvider.logger.log(Level.INFO, "Clearing Is Azure when machine level changes are done");
            SyMUtil.deleteServerParameter("SYSTEM_HW_TYPE");
            SyMUtil.findSystemHWType();
            final String serverDir = SyMUtil.getInstallationDir();
            final String serverInfoConf = serverDir + File.separator + "conf" + File.separator + "server_info.props";
            final String serverInfoLog = serverDir + File.separator + "logs" + File.separator + "server_info.props";
            final Properties prop = new Properties();
            final String serverHWType = SyMUtil.getServerParameter("SYSTEM_HW_TYPE");
            prop.setProperty("server.hardware.type", serverHWType);
            FileAccessUtil.storeProperties(prop, serverInfoConf, true);
            FileAccessUtil.storeProperties(prop, serverInfoLog, true);
        }
        catch (final Exception e) {
            DCServerBuildHistoryProvider.logger.log(Level.INFO, "Exception in updating System HW type");
        }
    }
    
    private void logBuildNumbersHistory() {
        final String sourceMethod = "logBuildNumbersHistory";
        SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Start of logBuildNumbersHistory()...");
        FileOutputStream fos = null;
        try {
            final String logFile = SyMUtil.getLogsDir() + File.separator + "build-history.txt";
            fos = new FileOutputStream(logFile);
            String comments = "Log Generated At " + new Date(System.currentTimeMillis());
            fos.write(comments.getBytes());
            final String tblName = "DCServerBuildHistory";
            final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(tblName));
            Column selCol = Column.getColumn((String)null, "*");
            selQuery.addSelectColumn(selCol);
            selQuery.addSortColumn(new SortColumn(Column.getColumn(tblName, "BUILD_NUMBER"), false));
            final DataObject resultDO = SyMUtil.getPersistence().get(selQuery);
            comments = "\n\n\nBuild Number History";
            fos.write(comments.getBytes());
            comments = "\n--------------------";
            fos.write(comments.getBytes());
            int rowCnt = resultDO.size(tblName);
            comments = "\n\nNumber of build numbers found in DB is " + rowCnt;
            fos.write(comments.getBytes());
            if (!resultDO.isEmpty()) {
                comments = "\n\n";
                fos.write(comments.getBytes());
                comments = "==================================================================================================================";
                fos.write(comments.getBytes());
                comments = "\nBUILD NUMBER \t BUILD DETECTED AT \t\t\t REMARKS\n";
                fos.write(comments.getBytes());
                comments = "==================================================================================================================";
                fos.write(comments.getBytes());
                final Iterator rows = resultDO.getRows(tblName);
                while (rows.hasNext()) {
                    final Row bhRow = rows.next();
                    comments = "\n" + bhRow.get("BUILD_NUMBER") + " \t\t\t " + bhRow.get("BUILD_DETECTED_AT_STR") + " \t " + bhRow.get("REMARKS");
                    fos.write(comments.getBytes());
                }
                comments = "\n==================================================================================================================";
                fos.write(comments.getBytes());
            }
            else {
                comments = "\n\nBuild number history is not available in DB.";
                fos.write(comments.getBytes());
            }
            comments = "\n\n\nServer Uptime History";
            fos.write(comments.getBytes());
            comments = "\n---------------------";
            fos.write(comments.getBytes());
            final String uhTblName = "DCServerUptimeHistory";
            final SelectQuery uhSelQuery = (SelectQuery)new SelectQueryImpl(Table.getTable(uhTblName));
            selCol = Column.getColumn((String)null, "*");
            uhSelQuery.addSelectColumn(selCol);
            uhSelQuery.addSortColumn(new SortColumn(Column.getColumn(uhTblName, "START_TIME"), false));
            final DataObject uhResultDO = SyMUtil.getPersistence().get(uhSelQuery);
            rowCnt = uhResultDO.size(uhTblName);
            comments = "\n\nNumber of uptime history records found in DB is " + rowCnt;
            fos.write(comments.getBytes());
            if (!resultDO.isEmpty()) {
                comments = "\n\n";
                fos.write(comments.getBytes());
                comments = "==================================================================================================================";
                fos.write(comments.getBytes());
                comments = "\nBUILD NUMBER \t\t\t\t STARTED AT \t\t\t\t\t SHUTDOWN AT \t\t\t\t\t UP TIME \n";
                fos.write(comments.getBytes());
                comments = "==================================================================================================================";
                fos.write(comments.getBytes());
                final Iterator rows2 = uhResultDO.getRows(uhTblName);
                while (rows2.hasNext()) {
                    final Row uhRow = rows2.next();
                    comments = "\n" + uhRow.get("BUILD_NUMBER") + " \t\t\t\t " + uhRow.get("START_TIME_STR") + " \t\t " + uhRow.get("SHUTDOWN_TIME_STR") + " \t\t " + uhRow.get("TOTAL_UPTIME_STR");
                    fos.write(comments.getBytes());
                }
                comments = "\n==================================================================================================================";
                fos.write(comments.getBytes());
            }
            else {
                comments = "\n\nBuild uptime history is not available in DB.";
                fos.write(comments.getBytes());
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Caught exception while creating the log entries with build history & uptime history.", (Throwable)ex);
        }
        finally {
            if (fos != null) {
                try {
                    fos.close();
                }
                catch (final Exception ex2) {}
            }
        }
        SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "End of logBuildNumbersHistory()...");
    }
    
    private static void clearModRewriteConf() {
        final String sourceMethod = "clearModRewriteConf";
        try {
            final String serverHome = SyMUtil.getInstallationDir();
            final String migrateConfFile = serverHome + File.separator + DCServerBuildHistoryProvider.SERVER_MIGRATE_CONF_FILE;
            if (new File(migrateConfFile).exists()) {
                new File(migrateConfFile).delete();
            }
            final String modRewriteConf = serverHome + File.separator + DCServerBuildHistoryProvider.MOD_REWRITE_CONF_FILE;
            clearFileContents(modRewriteConf);
            final String sslModRewriteConf = serverHome + File.separator + DCServerBuildHistoryProvider.SSL_MOD_REWRITE_CONF_FILE;
            clearFileContents(sslModRewriteConf);
            final String nginxModRewriteConf = serverHome + File.separator + DCServerBuildHistoryProvider.NGINX_MOD_REWRITE_CONF_FILE;
            if (new File(nginxModRewriteConf).exists()) {
                clearFileContents(nginxModRewriteConf);
            }
        }
        catch (final Exception e) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Exception while clearing apache mod rewrite files....", (Throwable)e);
        }
    }
    
    private static void clearFileContents(final String fileName) {
        FileOutputStream os = null;
        final String sourceMethod = "clearFileContents";
        try {
            os = new FileOutputStream(fileName);
            os.write(new String().getBytes());
            os.close();
        }
        catch (final Exception e) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Exception while clearing file contents ", (Throwable)e);
            try {
                final File file = new File(fileName);
                file.delete();
                final File newFile = new File(fileName);
                newFile.createNewFile();
            }
            catch (final Exception ex) {}
        }
        finally {
            try {
                os.close();
            }
            catch (final Exception ex2) {}
        }
    }
    
    public void updateServerStartupTimeIntoFileSystem() {
        final String sourceMethod = "updateServerStartupTimeIntoFileSystem";
        try {
            final String serverHome = SyMUtil.getInstallationDir();
            final String serverStartTimeFile = serverHome + File.separator + "conf" + File.separator + "server.starttime";
            if (!new File(serverStartTimeFile).exists()) {
                new File(serverStartTimeFile).createNewFile();
            }
            final Properties fileSystemProps = new Properties();
            SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Server Start time " + DCServerBuildHistoryProvider.currentStartTime);
            if (DCServerBuildHistoryProvider.currentStartTime != null) {
                final Boolean updateStatus = SyMUtil.updateStartupTimeInDB("last_server_startup_time_DB", String.valueOf(DCServerBuildHistoryProvider.currentStartTime));
                if (!updateStatus) {
                    throw new Exception("DB Update Failed while Updating the Last Server Startup Time");
                }
                fileSystemProps.setProperty("last_server_startup_time", String.valueOf(DCServerBuildHistoryProvider.currentStartTime));
                FileAccessUtil.storeProperties(fileSystemProps, serverStartTimeFile, false);
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Exception while writing server startup time into the file file_system.conf ", (Throwable)ex);
        }
    }
    
    public void updateNewServerDetailsAfterServerMigration() {
        final String sourceMethod = "updateNewServerDetailsAfterServerMigration";
        try {
            final String serverMigrationConf = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "server-migrate.conf";
            if (new File(serverMigrationConf).exists()) {
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Server-migration.conf file is exists.");
                final Properties serverMigrationProps = StartupUtil.getProperties(serverMigrationConf);
                SyMLogger.info(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Server-migration Properties : " + serverMigrationProps);
                final String newServerHTTPSPort = serverMigrationProps.getProperty("NewServerHttpsPort");
                final String newServerIp = serverMigrationProps.getProperty("NewServerIP");
                final boolean isMSP = CustomerInfoUtil.getInstance().isMSP();
                if (isMSP) {
                    final Table tableName = Table.getTable("DCServerNATInfo");
                    final SelectQuery query = (SelectQuery)new SelectQueryImpl(tableName);
                    query.addSelectColumn(Column.getColumn("DCServerNATInfo", "NAT_ADDRESS"));
                    query.addSelectColumn(Column.getColumn("DCServerNATInfo", "NAT_HTTPS_PORT"));
                    query.addSelectColumn(Column.getColumn("DCServerNATInfo", "NAT_ID"));
                    final DataObject doObject = SyMUtil.getPersistence().get(query);
                    if (!doObject.isEmpty()) {
                        final Row natRow = doObject.getFirstRow("DCServerNATInfo");
                        if (newServerIp != null) {
                            natRow.set("NAT_ADDRESS", (Object)newServerIp);
                        }
                        if (newServerHTTPSPort != null) {
                            natRow.set("NAT_HTTPS_PORT", (Object)newServerHTTPSPort);
                        }
                        doObject.updateRow(natRow);
                        SyMUtil.getPersistence().update(doObject);
                    }
                    else {
                        final String rdsSettingsConf = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "rdssettings.conf";
                        final String dcnsSettingsConf = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "dcnssettings.conf";
                        final String customDcnsSettingsConf = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "custom_dcnssettings.conf";
                        final String webSettingsConf = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "websettings.conf";
                        String natHTTPSPort = "8041";
                        String natRDSHttpsPort = "8047";
                        String natFTHttpsPort = "8053";
                        String natNSPort = "8057";
                        String natChatPort = "8052";
                        if (new File(rdsSettingsConf).exists()) {
                            final Properties rdsSettingsProps = StartupUtil.getProperties(rdsSettingsConf);
                            natRDSHttpsPort = rdsSettingsProps.getProperty("rds.default.https.port");
                            natFTHttpsPort = rdsSettingsProps.getProperty("ft.default.https.port");
                        }
                        if (new File(dcnsSettingsConf).exists()) {
                            final Properties nsSettingsProps = StartupUtil.getProperties(dcnsSettingsConf);
                            final Properties customnsSettingsProps = StartupUtil.getProperties(customDcnsSettingsConf);
                            nsSettingsProps.putAll(customnsSettingsProps);
                            natNSPort = nsSettingsProps.getProperty("ns.port");
                        }
                        if (new File(webSettingsConf).exists()) {
                            final Properties webSettingsProps = StartupUtil.getProperties(webSettingsConf);
                            natChatPort = webSettingsProps.getProperty("httpnio.port");
                            natHTTPSPort = webSettingsProps.getProperty("https.port");
                        }
                        final Row natRow2 = new Row("DCServerNATInfo");
                        natRow2.set("NAT_ADDRESS", (Object)newServerIp);
                        natRow2.set("NAT_HTTPS_PORT", (Object)natHTTPSPort);
                        natRow2.set("NAT_RDS_HTTPS_PORT", (Object)natRDSHttpsPort);
                        natRow2.set("NAT_FT_HTTPS_PORT", (Object)natFTHttpsPort);
                        natRow2.set("NAT_NS_PORT", (Object)natNSPort);
                        natRow2.set("NAT_CHAT_PORT", (Object)natChatPort);
                        doObject.addRow(natRow2);
                        SyMUtil.getPersistence().add(doObject);
                    }
                }
                this.deleteServerMigrationConfIfExists();
            }
        }
        catch (final Exception ex) {
            SyMLogger.error(DCServerBuildHistoryProvider.logger, DCServerBuildHistoryProvider.sourceClass, sourceMethod, "Exception occurred while update new IP in DCSERVERNATINFO table and portin websettings conf file after server migration", (Throwable)ex);
        }
    }
    
    public void deleteServerMigrationConfIfExists() throws Exception {
        final String serverMigrationConf = SyMUtil.getInstallationDir() + File.separator + "conf" + File.separator + "server-migrate.conf";
        if (new File(serverMigrationConf).exists()) {
            new File(serverMigrationConf).delete();
        }
    }
    
    static {
        DCServerBuildHistoryProvider.logger = null;
        DCServerBuildHistoryProvider.sourceClass = "DCServerBuildHistoryProvider";
        DCServerBuildHistoryProvider.dcSBHProvider = null;
        DCServerBuildHistoryProvider.currentStartTime = null;
        SERVER_MIGRATE_CONF_FILE = "conf" + File.separator + "server-migrate.conf";
        APACHE_CONF = "apache" + File.separator + "conf";
        MOD_REWRITE_CONF_FILE = DCServerBuildHistoryProvider.APACHE_CONF + File.separator + "httpd_mod_rewrite.conf";
        SSL_MOD_REWRITE_CONF_FILE = DCServerBuildHistoryProvider.APACHE_CONF + File.separator + "httpd_ssl_mod_rewrite.conf";
        NGINX_MOD_REWRITE_CONF_FILE = "nginx" + File.separator + "conf" + File.separator + "nginx_mod_rewrite.conf";
    }
}
