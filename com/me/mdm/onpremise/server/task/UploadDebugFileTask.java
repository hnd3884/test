package com.me.mdm.onpremise.server.task;

import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import java.util.Iterator;
import java.util.Collection;
import java.io.FilenameFilter;
import com.me.devicemanagement.onpremise.start.util.NginxServerUtils;
import com.me.devicemanagement.onpremise.start.util.WebServerUtil;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import com.adventnet.ds.DSUtil;
import java.util.ArrayList;
import com.me.devicemanagement.onpremise.server.util.UploadDebugFileUtil;
import java.io.File;
import com.me.devicemanagement.onpremise.server.service.DMOnPremisetHandler;
import com.me.devicemanagement.onpremise.server.metrack.METrackerHandler;
import java.util.logging.Level;
import java.util.Properties;
import java.util.logging.Logger;
import com.me.devicemanagement.framework.server.scheduler.SchedulerExecutionInterface;

public class UploadDebugFileTask implements SchedulerExecutionInterface
{
    private static Logger logger;
    private static Logger connectionDumpLog;
    private static final String UPLOADDB_LOG = "UploadDBLog";
    
    public void executeTask(final Properties props) {
        UploadDebugFileTask.logger.log(Level.INFO, "UploadDebugFileTask Task is invoked");
        ArrayList<Properties> fileListProperties = null;
        try {
            final boolean isMETrackEnabled = METrackerHandler.getMETrackSettings();
            final boolean isAutoUploadEnabled = this.isAutomaticUploadEnable();
            if (!isMETrackEnabled && !isAutoUploadEnabled) {
                UploadDebugFileTask.logger.log(Level.INFO, "Automatic upload task and METracking are disabled. Terminating upload process...");
                return;
            }
            if (isMETrackEnabled && !isAutoUploadEnabled) {
                UploadDebugFileTask.logger.log(Level.INFO, "Creating temp files...");
                DMOnPremisetHandler.createServerInfoConfWithoutPI();
            }
            final String confDir = System.getProperty("server.home") + File.separator + "conf";
            final String uploadPIFileListXML = confDir + File.separator + "uploadFileList.xml";
            final String uploadWithoutPIFileListXML = confDir + File.separator + "uploadFileListForMETrack.xml";
            final ArrayList<Properties> piFileListProperties = UploadDebugFileUtil.parseUploadFileListXML(uploadPIFileListXML);
            final ArrayList<Properties> withoutPIFileListProperties = UploadDebugFileUtil.parseUploadFileListXML(uploadWithoutPIFileListXML);
            if (isMETrackEnabled && isAutoUploadEnabled) {
                UploadDebugFileTask.logger.log(Level.INFO, "Going to upload log files with and without PI");
                fileListProperties = UploadDebugFileUtil.mergeFileListProps((ArrayList)piFileListProperties, (ArrayList)withoutPIFileListProperties);
            }
            else if (isAutoUploadEnabled) {
                UploadDebugFileTask.logger.log(Level.INFO, "Going to upload log files with PI");
                fileListProperties = piFileListProperties;
            }
            else if (isMETrackEnabled) {
                UploadDebugFileTask.logger.log(Level.INFO, "Going to upload log files without PI");
                fileListProperties = withoutPIFileListProperties;
            }
            if (fileListProperties != null) {
                final ArrayList<File> uploadFileList = processUploadFileListXML(fileListProperties);
                UploadDebugFileUtil.createAndUploadDebugFiles((ArrayList)uploadFileList, "MDMPDebugFiles.7z");
            }
        }
        catch (final Exception ex) {
            UploadDebugFileTask.logger.log(Level.WARNING, "Caught exception while executing UploadDebugFileTask", ex);
        }
        finally {
            DMOnPremisetHandler.deleteServerInfoConfWithoutPI();
        }
    }
    
    public static ArrayList<File> processUploadFileListXML(final ArrayList<Properties> fileListProperties) {
        final ArrayList<File> uploadFileList = new ArrayList<File>();
        final ArrayList<File> defaultFileList = new ArrayList<File>();
        try {
            UploadDebugFileTask.logger.log(Level.INFO, "Searching File list from xml...");
            final String serverHome = System.getProperty("server.home");
            for (final Properties prop : fileListProperties) {
                if (prop.getProperty("fileType").equals("DefaultFile")) {
                    final String fullFilePath = serverHome + File.separator + prop.getProperty("fileName");
                    defaultFileList.add(new File(fullFilePath));
                    UploadDebugFileTask.logger.log(Level.INFO, "Added {0} to Default file list.", fullFilePath);
                }
                else {
                    final Long lastUploadTime = UploadDebugFileUtil.checkLastUploadStatus(prop);
                    if (lastUploadTime == UploadDebugFileUtil.REACHED_UPLOAD_LIMIT) {
                        continue;
                    }
                    final String searchName = prop.getProperty("fileSearchString");
                    final String dependentFileSearchName = prop.getProperty("dependentFileSearchString");
                    final String path = serverHome + File.separator + prop.getProperty("filePath");
                    UploadDebugFileTask.logger.log(Level.INFO, "FILE PATH :{0}\t SEARCH STRING :{1}\t TYPE :{2}", new Object[] { path, searchName, prop.getProperty("fileType") });
                    Boolean fileBundle = Boolean.TRUE;
                    if (prop.getProperty("fileType").equals("no-managed-connection")) {
                        final int noManagedConnection = DSUtil.getInUseConnectionCount(10800L);
                        fileBundle = ((noManagedConnection > 0) ? Boolean.TRUE : Boolean.FALSE);
                        if (noManagedConnection > 0) {
                            UploadDebugFileTask.logger.log(Level.INFO, "No-managed-connection found.Hence file bundled for upload. No of current  Live connections : {0}", noManagedConnection);
                            UploadDebugFileTask.connectionDumpLog.log(Level.INFO, "--------------------*****************************--------------------");
                            UploadDebugFileTask.connectionDumpLog.log(Level.INFO, "Automatic Diagnosis Log Upload Task");
                            UploadDebugFileTask.connectionDumpLog.log(Level.INFO, "Number of connections which is opened before three hours : {0}", noManagedConnection);
                            UploadDebugFileTask.connectionDumpLog.log(Level.INFO, "Number of opened connections : before three hours : {0}", DSUtil.getInUseConnectionInfo(10800L).toString());
                            UploadDebugFileTask.connectionDumpLog.log(Level.INFO, "--------------------*****************************--------------------");
                        }
                        else {
                            UploadDebugFileTask.logger.log(Level.INFO, "No-managed-connection not found");
                        }
                    }
                    if (prop.getProperty("fileType").equals("jvm_thread_blocks")) {
                        final String temp = SyMUtil.getServerParameter("LastOccuredBlock");
                        if (temp != null) {
                            final long lastDeadLockOccurredTime = Long.parseLong(temp);
                            if (lastDeadLockOccurredTime > UploadDebugFileUtil.checkLastUploadStatus(prop)) {
                                fileBundle = Boolean.TRUE;
                                UploadDebugFileTask.logger.log(Level.INFO, "Changes in jvm threads blocks found.Hence file bundled for upload.");
                            }
                            else {
                                fileBundle = Boolean.FALSE;
                                UploadDebugFileTask.logger.log(Level.INFO, "No change in jvm threads occurrence log file");
                            }
                        }
                    }
                    if (prop.getProperty("fileType").equals("nginx-error") && WebServerUtil.getWebServerSettings().getProperty("webserver.name").equalsIgnoreCase("nginx")) {
                        if (!NginxServerUtils.isNginxServerRunning()) {
                            fileBundle = Boolean.TRUE;
                        }
                        else {
                            fileBundle = Boolean.FALSE;
                        }
                    }
                    if (!fileBundle) {
                        continue;
                    }
                    final File dir = new File(path);
                    if (!dir.exists()) {
                        continue;
                    }
                    final FilenameFilter filter = new FilenameFilter() {
                        @Override
                        public boolean accept(final File dir, final String name) {
                            return name.contains(searchName) && !name.contains(".lck");
                        }
                    };
                    final File[] files = dir.listFiles(filter);
                    if (files.length > 0) {
                        uploadFileList.addAll(UploadDebugFileUtil.getUploadFileList(files, prop));
                    }
                    if (dependentFileSearchName == null || dependentFileSearchName == "") {
                        continue;
                    }
                    final FilenameFilter dependentFileFilter = new FilenameFilter() {
                        @Override
                        public boolean accept(final File dir, final String name) {
                            return name.contains(dependentFileSearchName) && !name.contains(".lck");
                        }
                    };
                    final File[] dependentFiles = dir.listFiles(dependentFileFilter);
                    if (dependentFiles.length <= 0) {
                        continue;
                    }
                    uploadFileList.addAll(UploadDebugFileUtil.getUploadFileList(dependentFiles, prop));
                }
            }
            if (!uploadFileList.isEmpty()) {
                uploadFileList.addAll(defaultFileList);
            }
        }
        catch (final Exception e) {
            UploadDebugFileTask.logger.log(Level.INFO, "Exception in proccessing input file list :", e);
        }
        return uploadFileList;
    }
    
    public boolean isAutomaticUploadEnable() throws Exception {
        try {
            Criteria cri = new Criteria(Column.getColumn("Consent", "CONSENT_NAME"), (Object)"UploadDBLog", 0);
            SelectQuery sq = (SelectQuery)new SelectQueryImpl(new Table("Consent"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            DataObject dataObject = DataAccess.get(sq);
            final String consent_id = String.valueOf(dataObject.getFirstValue("Consent", "CONSENT_ID"));
            cri = new Criteria(Column.getColumn("ConsentStatus", "CONSENT_ID"), (Object)consent_id, 0);
            sq = (SelectQuery)new SelectQueryImpl(new Table("ConsentStatus"));
            sq.setCriteria(cri);
            sq.addSelectColumn(Column.getColumn((String)null, "*"));
            dataObject = DataAccess.get(sq);
            final String consent_status = String.valueOf(dataObject.getFirstValue("ConsentStatus", "STATUS"));
            if (consent_status.equalsIgnoreCase("1")) {
                return true;
            }
        }
        catch (final Exception e) {
            UploadDebugFileTask.logger.log(Level.SEVERE, "Exception in isAutomaticUploadEnable ", e);
            throw e;
        }
        return false;
    }
    
    static {
        UploadDebugFileTask.logger = Logger.getLogger("UploadDebugFilesLogger");
        UploadDebugFileTask.connectionDumpLog = Logger.getLogger("DMConnectionDump");
    }
}
