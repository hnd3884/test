package com.adventnet.sym.server.admin.task;

import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.devicemanagement.framework.server.license.LicenseProvider;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.i18n.I18N;
import com.adventnet.persistence.fos.FOS;
import com.adventnet.ds.DSUtil;
import java.util.Iterator;
import com.zoho.framework.utils.FileUtils;
import java.util.Calendar;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import java.util.List;
import com.adventnet.persistence.WritableDataObject;
import com.me.devicemanagement.onpremise.server.metrack.METrackerUtil;
import java.util.Arrays;
import java.io.FilenameFilter;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import java.sql.Connection;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Query;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.SortColumn;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.fileaccess.FileOperationsUtil;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import java.io.File;
import com.adventnet.taskengine.TaskExecutionException;
import com.me.devicemanagement.onpremise.server.common.FlashMessage;
import com.me.devicemanagement.onpremise.server.license.LicenseUtil;
import com.me.mdm.onpremise.server.util.MDMPFwsUtil;
import com.adventnet.sym.server.admin.PPMCleanupHandler;
import com.me.devicemanagement.framework.server.util.DBUtil;
import com.adventnet.sym.webclient.common.SYMClientUtil;
import com.adventnet.sym.server.admin.DCCredentialManager;
import com.adventnet.sym.server.util.SyMUtil;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class DCGlobalTask implements SchedulerExecutionInterface
{
    public static final String TASK_NAME = "DCGlobalTask";
    private Logger log;
    private Logger logger;
    
    public DCGlobalTask() {
        this.log = Logger.getLogger("DMConnectionDump");
        (this.logger = Logger.getLogger(DCGlobalTask.class.getName())).log(Level.INFO, "DCGlobalTask() instance created.");
    }
    
    public void executeTask(final Properties taskProps) {
        final Long currTime = new Long(System.currentTimeMillis());
        this.logger.log(Level.INFO, "DCGlobalTask Task is invoked at {0}", SyMUtil.getDate((long)currTime));
        try {
            try {
                new DCCredentialManager().updateDomainCredentialStatus();
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Caught exception while updating AD Managed Domains Credential Status in DCGlobalTask", ex);
            }
            try {
                SYMClientUtil.getFirewallAndDCOMStatus();
            }
            catch (final Exception ex) {
                this.logger.log(Level.WARNING, "Caught exception getting firewall and dcom status in DCGlobalTask", ex);
            }
            this.checkFreeSpaceAvailable();
            final String activedb = DBUtil.getActiveDBName();
            if (activedb.equalsIgnoreCase("mssql")) {
                this.logger.log(Level.INFO, "Going to Check MSSQL DB Transaction Log Size ");
                SYMClientUtil.checkTransactionLogSizeforMSSQL();
            }
            this.cleanupApacheLogs();
            this.cleanupHprofFiles();
            this.cleanupPidFiles();
            this.cleanupDbLockFiles();
            this.sendDataFolderSize();
            this.cleanupTomcatLogs();
            this.cleanupMySqlLogs();
            PPMCleanupHandler.getInstance().cleanupOldPPMBackups();
            this.trackingLiveConnectionDetails();
            this.checkFailOverServerUp();
            MDMPFwsUtil.checkFwServerUp();
            this.logger.log(Level.INFO, "End of executeTask() from instance of DCGlobalTask");
            SyMUtil.writeInstallPropsInFile();
            LicenseUtil.addOrUpdateLicenseExpiryMsgDetails();
            FlashMessage.decrementShowAfterCounter();
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception while Executing DCGlobalTask", e);
        }
        finally {
            try {
                this.updateLicenseExpiryDetails();
                LicenseUtil.checkLicenseExpireDate();
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Caught exception while checking License Expiry during DCGlobalTask", ex2);
            }
            this.logger.log(Level.INFO, "End of executeTask() from instance of DCGlobalTask");
        }
    }
    
    public void stopTask() throws TaskExecutionException {
    }
    
    @Override
    public String toString() {
        return "DCGlobalTask";
    }
    
    private void cleanupApacheLogs() {
        try {
            this.logger.log(Level.INFO, "cleanupApacheLogs() is going to execute from DCGlobalTask...");
            final String logsFolder = SyMUtil.getInstallationDir() + File.separator + "logs";
            final int numOfFilesToMaintain = WebServerUtil.getApacheLogFilesCountToMaintain();
            final String accessLogPrefix = WebServerUtil.getApacheAccessLogFileNamePrefix();
            FileOperationsUtil.getInstance().cleanupFiles(logsFolder, accessLogPrefix, numOfFilesToMaintain);
            final String errorLogPrefix = WebServerUtil.getApacheErrorLogFileNamePrefix();
            FileOperationsUtil.getInstance().cleanupFiles(logsFolder, errorLogPrefix, numOfFilesToMaintain);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught error while cleaning up the apache log files", ex);
        }
    }
    
    private int getBuildNumber(final Long creationTime) {
        int buildNumber = 0;
        int defaultBuildNumber = 0;
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
        query.addSelectColumn(new Column("DCServerBuildHistory", "*"));
        query.addSortColumn(new SortColumn("DCServerBuildHistory", "BUILD_NUMBER", true));
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                defaultBuildNumber = ds.getInt("BUILD_NUMBER");
                final Long buildDetectionTime = ds.getAsLong("BUILD_DETECTED_AT");
                if (creationTime > buildDetectionTime) {
                    buildNumber = ds.getInt("BUILD_NUMBER");
                }
            }
            if (buildNumber == 0) {
                buildNumber = defaultBuildNumber;
            }
            ds.close();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception while getting Build detected time", ex);
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Exception while closing connection", ex2);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                this.logger.log(Level.WARNING, "Exception while closing connection", ex3);
            }
        }
        return buildNumber;
    }
    
    private void updateBuildwiseHprofCount() {
        final SelectQuery query = (SelectQuery)new SelectQueryImpl(Table.getTable("DCServerBuildHistory"));
        query.addSelectColumn(new Column("DCServerBuildHistory", "*"));
        query.addSortColumn(new SortColumn("DCServerBuildHistory", "BUILD_NUMBER", true));
        final RelationalAPI relapi = RelationalAPI.getInstance();
        Connection conn = null;
        this.logger.log(Level.FINE, "buildwise hprof");
        try {
            conn = relapi.getConnection();
            final DataSet ds = relapi.executeQuery((Query)query, conn);
            while (ds.next()) {
                final int buildNumberDetected = ds.getInt("BUILD_NUMBER");
                final Column buildNumber = Column.getColumn("ServerDebugInfo", "BUILD_NUMBER");
                final Column fileType = Column.getColumn("ServerDebugInfo", "FILE_TYPE");
                Criteria criteria = new Criteria(buildNumber, (Object)buildNumberDetected, 0);
                criteria = criteria.and(fileType, (Object)1, 0);
                final int count = DBUtil.getRecordCount("ServerDebugInfo", "SERVER_DEBUG_INFO_ID", criteria);
                if (count > 0) {
                    final Criteria hprofCriteria = new Criteria(Column.getColumn("ServerDebuggingFileSummary", "BUILD_NUMBER"), (Object)buildNumberDetected, 0);
                    hprofCriteria.and(Column.getColumn("ServerDebuggingFileSummary", "FILE_TYPE"), (Object)1, 0);
                    final DataObject hprofCountDO = SyMUtil.getPersistence().get("ServerDebuggingFileSummary", hprofCriteria);
                    if (hprofCountDO.isEmpty()) {
                        final Row row = new Row("ServerDebuggingFileSummary");
                        row.set("BUILD_NUMBER", (Object)buildNumberDetected);
                        row.set("FILE_TYPE", (Object)1);
                        row.set("COUNT", (Object)count);
                        hprofCountDO.addRow(row);
                        SyMUtil.getPersistence().add(hprofCountDO);
                        this.logger.log(Level.FINE, "buildwise hprof added{0}", row.toString());
                    }
                    else {
                        final Row row = hprofCountDO.getFirstRow("ServerDebuggingFileSummary");
                        row.set("COUNT", (Object)count);
                        hprofCountDO.updateRow(row);
                        SyMUtil.getPersistence().update(hprofCountDO);
                        this.logger.log(Level.FINE, "buildwise hprof updated{0}", row.toString());
                    }
                }
            }
            ds.close();
        }
        catch (final Exception e) {
            this.logger.log(Level.WARNING, "Exception while updating build-wise hprof occurences", e);
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex2) {
                this.logger.log(Level.WARNING, "Exception while closing connection", ex2);
            }
        }
        finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            }
            catch (final Exception ex3) {
                this.logger.log(Level.WARNING, "Exception while closing connection", ex3);
            }
        }
    }
    
    private void cleanupHprofFiles() {
        try {
            this.logger.log(Level.INFO, "cleanupHprofFiles() is going to execute from DCGlobalTask...");
            final String hprofRetainCountString = SyMUtil.getSyMParameter("Hprof_Retain_Count");
            int hprofRetainCount;
            if (hprofRetainCountString != null) {
                hprofRetainCount = Integer.parseInt(hprofRetainCountString);
            }
            else {
                hprofRetainCount = 3;
            }
            this.logger.log(Level.INFO, "Number of hprof files to be retained is : {0}", hprofRetainCount);
            final File dir = new File(SyMUtil.getInstallationDir() + File.separator + "bin");
            final FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.endsWith(".hprof");
                }
            };
            final List<File> hprofFilesList = Arrays.asList(dir.listFiles(filter));
            final int size = hprofFilesList.size();
            final Properties hprofProps = METrackerUtil.getMETrackParams("Hprof_Count");
            this.logger.log(Level.INFO, "Hprof count properties{0}", hprofProps);
            if (!hprofFilesList.isEmpty()) {
                File[] hprofFiles = new File[size];
                hprofFiles = hprofFilesList.toArray(hprofFiles);
                hprofFiles = FileOperationsUtil.getInstance().sortFilesWithLastModifiedTime(hprofFiles);
                final int length = hprofFiles.length;
                int deletedCount = 0;
                final Properties hprofLastModifiedTimeProps = METrackerUtil.getMETrackParams("Hprof_Last_Modified_Time");
                for (int i = 0; i < length; ++i) {
                    long hprofLastModifiedTimeFromDB = 0L;
                    if (!hprofLastModifiedTimeProps.isEmpty()) {
                        hprofLastModifiedTimeFromDB = Long.parseLong(hprofLastModifiedTimeProps.getProperty("Hprof_Last_Modified_Time"));
                    }
                    if (hprofFiles[i].lastModified() > hprofLastModifiedTimeFromDB) {
                        final Row serverDebugInfoRow = new Row("ServerDebugInfo");
                        serverDebugInfoRow.set("BUILD_NUMBER", (Object)this.getBuildNumber(hprofFiles[i].lastModified()));
                        serverDebugInfoRow.set("FILE_NAME", (Object)hprofFiles[i].getName());
                        serverDebugInfoRow.set("FILE_TYPE", (Object)1);
                        serverDebugInfoRow.set("CREATION_TIME", (Object)hprofFiles[i].lastModified());
                        final DataObject serverDebugInfoDO = (DataObject)new WritableDataObject();
                        serverDebugInfoDO.addRow(serverDebugInfoRow);
                        SyMUtil.getPersistence().add(serverDebugInfoDO);
                    }
                }
                if (hprofProps.isEmpty()) {
                    this.logger.log(Level.INFO, "Hprof cleanup : Obtaining number of hprof files from directory");
                    this.logger.log(Level.INFO, "Hprof Count {0}", length);
                    METrackerUtil.addOrUpdateMETrackParams("Hprof_Count", String.valueOf(length));
                }
                else {
                    final long hprofLastModifiedTimeFromDB2 = Long.parseLong(hprofLastModifiedTimeProps.getProperty("Hprof_Last_Modified_Time"));
                    this.logger.log(Level.INFO, "Last Modified Time from DB : {0}", hprofLastModifiedTimeFromDB2);
                    int newHprofFileCount = 0;
                    for (int flag = 0, j = length - 1; j >= 0 && flag == 0; --j) {
                        this.logger.log(Level.INFO, "Last Modified Time of the file : {0}", hprofFiles[j].lastModified());
                        if (hprofFiles[j].lastModified() > hprofLastModifiedTimeFromDB2) {
                            ++newHprofFileCount;
                        }
                        else {
                            flag = 1;
                        }
                    }
                    this.logger.log(Level.INFO, "Hprof cleanup : Updating the hprof count with respect to the directory");
                    final int prevCount = Integer.parseInt(hprofProps.getProperty("Hprof_Count"));
                    this.logger.log(Level.INFO, "New hprof count {0}", newHprofFileCount);
                    METrackerUtil.incrementMETrackParams("Hprof_Count", newHprofFileCount);
                }
                final long hprofLastModifiedDate = hprofFiles[length - 1].lastModified();
                METrackerUtil.addOrUpdateMETrackParams("Hprof_Last_Modified_Time", Long.toString(hprofLastModifiedDate));
                for (int k = 0; k < length - hprofRetainCount; ++k) {
                    final boolean isDeleted = FileOperationsUtil.getInstance().deleteFileOrFolder(hprofFiles[k]);
                    if (isDeleted == Boolean.TRUE) {
                        ++deletedCount;
                        final Column isDeletedCol = Column.getColumn("ServerDebugInfo", "IS_DELETED");
                        final Column fileName = Column.getColumn("ServerDebugInfo", "FILE_NAME");
                        final Criteria criteria = new Criteria(isDeletedCol, (Object)"false", 0);
                        criteria.and(fileName, (Object)hprofFiles[k].getName(), 0);
                        final DataObject serverDebugInfoDO2 = SyMUtil.getPersistence().get("ServerDebugInfo", criteria);
                        if (!serverDebugInfoDO2.isEmpty()) {
                            final Row row = serverDebugInfoDO2.getRow("ServerDebugInfo");
                            row.set("IS_DELETED", (Object)Boolean.TRUE);
                            serverDebugInfoDO2.updateRow(row);
                            SyMUtil.getPersistence().update(serverDebugInfoDO2);
                        }
                    }
                }
                this.logger.log(Level.INFO, "Deleted hprof count : {0}", deletedCount);
            }
            else if (hprofProps.isEmpty()) {
                this.logger.log(Level.INFO, "Hprof cleanup : No hprof files are available");
                METrackerUtil.addOrUpdateMETrackParams("Hprof_Count", String.valueOf(0));
                METrackerUtil.addOrUpdateMETrackParams("Hprof_Last_Modified_Time", String.valueOf(0));
            }
            else {
                this.logger.log(Level.INFO, "Hprof cleanup : Existing count is being maintained");
            }
            this.updateBuildwiseHprofCount();
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught error while cleaning up the hprof files", ex);
        }
    }
    
    private void cleanupPidFiles() {
        try {
            this.logger.log(Level.INFO, "cleanupPidFiles() is going to execute from DCGlobalTask...");
            final String pidRetainCountString = SyMUtil.getSyMParameter("Pid_Retain_Count");
            int pidRetainCount;
            if (pidRetainCountString != null) {
                pidRetainCount = Integer.parseInt(pidRetainCountString);
            }
            else {
                pidRetainCount = 5;
            }
            this.logger.log(Level.INFO, "Number of pid files to be retained is : {0}", pidRetainCount);
            final File dir = new File(SyMUtil.getInstallationDir() + File.separator + "bin");
            final FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(final File dir, final String name) {
                    return name.startsWith("hs_err_pid") && name.endsWith(".log");
                }
            };
            final List<File> pidFilesList = Arrays.asList(dir.listFiles(filter));
            final int size = pidFilesList.size();
            final Properties pidProps = METrackerUtil.getMETrackParams("PID_Count");
            this.logger.log(Level.INFO, "Pid count properties{0}", pidProps);
            if (!pidFilesList.isEmpty()) {
                File[] pidFiles = new File[size];
                pidFiles = pidFilesList.toArray(pidFiles);
                pidFiles = FileOperationsUtil.getInstance().sortFilesWithLastModifiedTime(pidFiles);
                final int length = pidFiles.length;
                int deletedCount = 0;
                final Properties pidLastModifiedTimeProps = METrackerUtil.getMETrackParams("PID_Last_Modified_Time");
                if (pidProps.isEmpty()) {
                    this.logger.log(Level.INFO, "Pid cleanup : Obtaining number of pid files from directory");
                    this.logger.log(Level.INFO, "Pid Count {0}", length);
                    METrackerUtil.addOrUpdateMETrackParams("PID_Count", String.valueOf(length));
                }
                else {
                    final long pidLastModifiedTimeFromDB = Long.parseLong(pidLastModifiedTimeProps.getProperty("PID_Last_Modified_Time"));
                    this.logger.log(Level.INFO, "Last Modified Time from DB : {0}", pidLastModifiedTimeFromDB);
                    int newPidFileCount = 0;
                    for (int flag = 0, i = length - 1; i >= 0 && flag == 0; --i) {
                        this.logger.log(Level.INFO, "Last Modified Time of the file : {0}", pidFiles[i].lastModified());
                        if (pidFiles[i].lastModified() > pidLastModifiedTimeFromDB) {
                            ++newPidFileCount;
                        }
                        else {
                            flag = 1;
                        }
                    }
                    this.logger.log(Level.INFO, "Pid cleanup : Updating the pid count with respect to the directory");
                    final int prevCount = Integer.parseInt(pidProps.getProperty("PID_Count"));
                    this.logger.log(Level.INFO, "New pid count {0}", newPidFileCount);
                    METrackerUtil.incrementMETrackParams("PID_Count", newPidFileCount);
                }
                final long pidLastModifiedDate = pidFiles[length - 1].lastModified();
                METrackerUtil.addOrUpdateMETrackParams("PID_Last_Modified_Time", Long.toString(pidLastModifiedDate));
                for (int j = 0; j < length - pidRetainCount; ++j) {
                    FileOperationsUtil.getInstance().deleteFileOrFolder(pidFiles[j]);
                    ++deletedCount;
                }
                this.logger.log(Level.INFO, "Deleted pid count : {0}", deletedCount);
            }
            else if (pidProps.isEmpty()) {
                this.logger.log(Level.INFO, "Pid cleanup : No pid files are present");
                METrackerUtil.addOrUpdateMETrackParams("PID_Count", String.valueOf(0));
                METrackerUtil.addOrUpdateMETrackParams("PID_Last_Modified_Time", String.valueOf(0));
            }
            else {
                this.logger.log(Level.INFO, "Pid cleanup : Existing count is being maintained");
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught error while cleaning up the pid files", ex);
        }
    }
    
    private void cleanupDbLockFiles() {
        try {
            this.logger.log(Level.INFO, "cleanupDbLockFiles() is going to execute from DCGlobalTask..");
            final FileUtil fileutilobj = new FileUtil();
            final DataObject dblocksettingsdo = SyMUtil.getPersistence().get("DbLockSettings", (Criteria)null);
            final Integer DbLockFilesRetainCount = (Integer)dblocksettingsdo.getFirstValue("DbLockSettings", "DBLOCKS_RETAIN_COUNT");
            long numberOfFilesindir = 0L;
            final String path = SyMUtil.getDbLocksFilePath();
            final File dir = new File(path);
            if (dir.isDirectory()) {
                numberOfFilesindir = FileUtil.getFileCount(dir);
            }
            this.logger.log(Level.INFO, "Number of files is{0}", numberOfFilesindir);
            if (numberOfFilesindir > DbLockFilesRetainCount) {
                final Integer cleanup_limit = (Integer)dblocksettingsdo.getFirstValue("DbLockSettings", "CLEANUP_LIMIT");
                if (cleanup_limit != null) {
                    final Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis());
                    cal.add(5, -1 * cleanup_limit);
                    final long deletionTime = cal.getTime().getTime();
                    this.logger.log(Level.INFO, "deletion time is {0}", deletionTime);
                    final Column lockinfocol = Column.getColumn("DbLockInfo", "IS_DELETED");
                    final Criteria criteria1 = new Criteria(lockinfocol, (Object)"false", 0);
                    final Criteria criteria2 = new Criteria(Column.getColumn("DbLockInfo", "CREATED_TIME"), (Object)new Long(deletionTime), 7);
                    final Criteria criteria3 = criteria1.and(criteria2);
                    final DataObject tobedeleteddo = SyMUtil.getPersistence().get("DbLockInfo", criteria3);
                    final Iterator rowtodelete = tobedeleteddo.getRows("DbLockInfo");
                    while (rowtodelete.hasNext()) {
                        final Row deleterow = rowtodelete.next();
                        final File fileToBeDeleted = new File(path + File.separator + deleterow.get("FILE_NAME").toString());
                        if (FileUtils.deleteDir(fileToBeDeleted)) {
                            deleterow.set("IS_DELETED", (Object)Boolean.TRUE);
                        }
                        tobedeleteddo.updateRow(deleterow);
                    }
                    SyMUtil.getPersistence().update(tobedeleteddo);
                }
            }
        }
        catch (final Exception ex) {
            Logger.getLogger(DCGlobalTask.class.getName()).log(Level.SEVERE, "Exception while executing dblockfiles cleanup..", ex);
        }
    }
    
    public long getFolderSize(final File dir) {
        long size = 0L;
        try {
            for (final File file : dir.listFiles()) {
                if (file.isFile()) {
                    this.logger.log(Level.FINE, "{0} {1}", new Object[] { file.getName(), file.length() });
                    size += file.length();
                }
                else {
                    size += this.getFolderSize(file);
                }
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught exception finding folder size ", ex);
        }
        return size;
    }
    
    private void sendDataFolderSize() {
        try {
            this.logger.log(Level.INFO, "sendDataFolderSize() is going to execute from DCGlobalTask...");
            final String activedb = DBUtil.getActiveDBName();
            if (activedb.equalsIgnoreCase("mysql") || activedb.equalsIgnoreCase("postgres")) {
                String dbHome = null;
                if (activedb.equalsIgnoreCase("mysql")) {
                    dbHome = System.getProperty("mysql.home");
                }
                else if (activedb.equalsIgnoreCase("postgres")) {
                    dbHome = System.getProperty("pgsql.home");
                }
                final File dataDir = new File(dbHome + File.separator + "data");
                long dataFolderSize = this.getFolderSize(dataDir);
                this.logger.log(Level.INFO, "Data Folder Size : {0}", dataFolderSize);
                dataFolderSize /= 1048576L;
                this.logger.log(Level.INFO, "Data Folder Size in MB: {0}", dataFolderSize);
                METrackerUtil.addOrUpdateMETrackParams("DBSizeInMB", String.valueOf(dataFolderSize));
            }
            else if (activedb.equalsIgnoreCase("mssql")) {
                METrackerUtil.addOrUpdateMETrackParams("DBSizeInMB", String.valueOf(-1));
            }
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while sending data folder size to ME tracking ", ex);
        }
    }
    
    private void cleanupTomcatLogs() {
        try {
            this.logger.log(Level.INFO, "cleanupTomcatLogs() is going to execute from DCGlobalTask...");
            final String logsFolder = SyMUtil.getInstallationDir() + File.separator + "logs";
            final int numOfFilesToMaintain = 10;
            final String tomcatLogPrefix = "localhost_log.";
            FileOperationsUtil.getInstance().cleanupFiles(logsFolder, tomcatLogPrefix, numOfFilesToMaintain);
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Caught error while cleaning up the tomcat log files", ex);
        }
    }
    
    private void cleanupMySqlLogs() {
        try {
            this.logger.log(Level.INFO, "cleanupMySqlLogs() is going to execute from DCGlobalTask...");
            final String mysqllogs = System.getProperty("server.home") + File.separator + "logs";
            FileOperationsUtil.getInstance().cleanupFiles(mysqllogs, "MySqlError", 4);
            FileOperationsUtil.getInstance().cleanupFiles(mysqllogs, "MySQL-slow", 4);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void checkFreeSpaceAvailable() {
        try {
            this.logger.log(Level.INFO, "checkFreeSpaceAvailable() is going to execute from DCGlobalTask...");
            final String activedb = DBUtil.getActiveDBName();
            SYMClientUtil.checkFreeDiskSpaceAvailableStatus(activedb, (boolean)Boolean.TRUE);
        }
        catch (final Exception ex) {
            this.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private void trackingLiveConnectionDetails() {
        try {
            int numberOfLiveConnections = 0;
            numberOfLiveConnections = DSUtil.getInUseConnectionCount(10800L);
            this.logger.log(Level.INFO, "Number of opened connections : {0}", DSUtil.getInUseConnectionCount(0L));
            this.logger.log(Level.INFO, "Number of connections which is opened before three hours : {0}", numberOfLiveConnections);
            this.log.log(Level.INFO, "Number of over all opened connections  {0}", DSUtil.getInUseConnectionCount(0L));
            this.log.log(Level.INFO, "Number of connections which is opened before three hours : {0}", numberOfLiveConnections);
            this.log.log(Level.INFO, "Number of opened connections : before three hours : {0}", DSUtil.getInUseConnectionInfo(10800L).toString());
            METrackerUtil.addOrUpdateMETrackParams("NumberOfLiveConnections", String.valueOf(numberOfLiveConnections));
        }
        catch (final Exception ex) {
            this.logger.log(Level.WARNING, "Exception occurred while gettin number of opened Connections ", ex);
        }
    }
    
    private void checkFailOverServerUp() {
        this.logger.log(Level.INFO, "checkFailOverServerUp() is going to execute from DCGlobalTask");
        Boolean isFosEnabled = Boolean.FALSE;
        try {
            isFosEnabled = FOS.isEnabled();
            if (isFosEnabled) {
                final Boolean isSlavePresent = FOS.standAloneMasterHealthCheck();
                this.logger.log(Level.INFO, "Slave Status{0}", isSlavePresent.toString());
                final FOS fos = new FOS();
                fos.initialize();
                final String currentIP = fos.getFOSConfig().ipaddr();
                final Column col = Column.getColumn("FosParams", "SERVER_IP");
                final Criteria crit = new Criteria(col, (Object)currentIP, 0, false);
                final DataObject fosDo = SyMUtil.getPersistence().get("FosParams", crit);
                int notificationCount = 0;
                Boolean sent = Boolean.FALSE;
                if (!fosDo.isEmpty()) {
                    notificationCount = (int)fosDo.getFirstValue("FosParams", "NOTIFICATION_COUNT");
                }
                if (!isSlavePresent && notificationCount < 5) {
                    String mailContent = "";
                    if (fos.getOtherNode() == null) {
                        mailContent = I18N.getMsg("dc.admin.fos.mail.activate_secondary_server", new Object[0]);
                    }
                    else {
                        mailContent = I18N.getMsg("dc.admin.fos.mail.server_down_content", new Object[] { fos.getOtherNode() });
                    }
                    final String mailSubject = I18N.getMsg("dc.admin.fos.mail.subject", new Object[] { null });
                    sent = SYMClientUtil.sendMailForFOS(fos.getOtherNode(), mailContent, mailSubject);
                    if (sent) {
                        try {
                            if (!fosDo.isEmpty()) {
                                final Row row = fosDo.getRow("FosParams");
                                row.set("NOTIFICATION_COUNT", (Object)(notificationCount + 1));
                                fosDo.updateRow(row);
                            }
                            else {
                                final Row row = new Row("FosParams");
                                row.set("SERVER_IP", (Object)currentIP);
                                row.set("NOTIFICATION_COUNT", (Object)1);
                                fosDo.addRow(row);
                            }
                            SyMUtil.getPersistence().update(fosDo);
                        }
                        catch (final Exception ex) {
                            Logger.getLogger(DCGlobalTask.class.getName()).log(Level.SEVERE, "Exception trace :", ex);
                        }
                    }
                }
                if (isSlavePresent) {
                    try {
                        if (!fosDo.isEmpty()) {
                            final Row row2 = fosDo.getRow("FosParams");
                            row2.set("NOTIFICATION_COUNT", (Object)0);
                            fosDo.updateRow(row2);
                        }
                        else {
                            final Row row2 = new Row("FosParams");
                            row2.set("SERVER_IP", (Object)currentIP);
                            row2.set("NOTIFICATION_COUNT", (Object)0);
                            fosDo.addRow(row2);
                        }
                        SyMUtil.getPersistence().update(fosDo);
                    }
                    catch (final DataAccessException ex2) {
                        Logger.getLogger(DCGlobalTask.class.getName()).log(Level.SEVERE, null, (Throwable)ex2);
                    }
                }
            }
        }
        catch (final Exception ex3) {
            Logger.getLogger(DCGlobalTask.class.getName()).log(Level.SEVERE, null, ex3);
        }
    }
    
    public void updateLicenseExpiryDetails() {
        try {
            MDMUtil.updateSyMParameter("licenseExpiryDays", Long.toString(LicenseProvider.getInstance().getEvaluationDays()));
        }
        catch (final Exception ex) {
            Logger.getLogger(DCGlobalTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
