package com.me.devicemanagement.onpremise.tools.backuprestore.action;

import java.util.Hashtable;
import java.io.InputStream;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.CompressUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.MssqlBackupRestoreUtil;
import java.util.Iterator;
import java.util.List;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordHandler;
import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DCBackupRestoreException;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.util.logging.Level;
import java.io.File;
import java.util.Properties;
import java.util.HashMap;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

public class DMBackupAction extends SwingWorker<Void, String>
{
    private static final Logger LOGGER;
    private String destination;
    private Informable informable;
    private String serverHome;
    private String destFolderName;
    public static String temporaryZip1;
    public static String temporaryZip2;
    public static String finalZip;
    private String archivePassword;
    public static String backupFolderName;
    public static HashMap<Integer, Properties> backupList;
    private boolean isToAskPermissionForBak;
    
    public DMBackupAction(final String destination) {
        this(destination, null, false);
    }
    
    public DMBackupAction(final String destination, final Informable informable) {
        this(destination, informable, false);
    }
    
    public DMBackupAction(final String destination, final Informable informable, final boolean isToAskPermissionForBak) {
        this.destination = null;
        this.informable = null;
        this.serverHome = null;
        this.destFolderName = null;
        this.archivePassword = null;
        this.isToAskPermissionForBak = true;
        this.destination = destination;
        this.informable = informable;
        this.isToAskPermissionForBak = isToAskPermissionForBak;
        try {
            this.serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            DMBackupAction.LOGGER.log(Level.INFO, "SERVER HOME\t::\t{0}", this.serverHome);
        }
        catch (final Exception e) {
            DMBackupAction.LOGGER.log(Level.WARNING, "Exception while getting \"server.home\" canonical path", e);
            this.serverHome = System.getProperty("server.home");
            DMBackupAction.LOGGER.log(Level.WARNING, "Hence setting serverHome as {0}", this.serverHome);
        }
    }
    
    public Void doInBackground() {
        boolean backupStatus = true;
        DCBackupRestoreException errorInfo = null;
        try {
            BackupRestoreUtil.printOneLineLog(Level.INFO, "Calling Backup from ManualBackupWindow");
            this.backup();
        }
        catch (final DCBackupRestoreException e) {
            backupStatus = false;
            errorInfo = e;
        }
        catch (final Exception e2) {
            DMBackupAction.LOGGER.log(Level.INFO, "Exception while performing backup :: ", e2);
            backupStatus = false;
            final String operationName = BackupRestoreUtil.getString("desktopcentral.tools.backup.title", null);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            errorInfo = BackupRestoreUtil.createException(-6, new Object[] { displayName, operationName }, e2);
        }
        if (errorInfo != null) {
            DMBackupAction.LOGGER.log(Level.WARNING, errorInfo.getMessage());
        }
        this.firePropertyChange("result", backupStatus, errorInfo);
        return null;
    }
    
    public void backup() throws Exception {
        try {
            DMBackupAction.LOGGER.log(Level.INFO, "**************************** Starting backup **************************** ");
            BackupRestoreUtil.printOneLineLog(Level.INFO, "Starting Backup......");
            if (BackupRestoreUtil.getDBType() == 2 && !BackupRestoreUtil.isRemoteDB() && !BackupRestoreUtil.isDBRunning()) {
                new BackupRestoreUtil();
                BackupRestoreUtil.executeInitPgsql(System.getProperty("server.home"));
            }
            this.archivePassword = DMBackupPasswordHandler.getInstance().getPassword(Boolean.TRUE);
            this.firePropertyChange("status", false, 13);
            this.doPreCheck();
            this.destFolderName = this.createDestFolder();
            this.initZipNameForProcess();
            DMBackupAction.backupList = this.doFilesBackup();
            final HashMap<Integer, Properties> backupListForXML = (HashMap<Integer, Properties>)DMBackupAction.backupList.clone();
            final boolean isRedisBackupNeeded = BackupRestoreUtil.isRedisEnabled();
            final boolean isRedisAOFEnabled = BackupRestoreUtil.isAOFEnabled();
            final Properties rProps = new Properties();
            ((Hashtable<String, Boolean>)rProps).put("isRedisBackupRestoreNeeded", isRedisBackupNeeded);
            ((Hashtable<String, Boolean>)rProps).put("isRedisAOFEnabled", isRedisAOFEnabled);
            if (isRedisBackupNeeded) {
                this.doRedisBackup();
            }
            else {
                DMBackupAction.LOGGER.log(Level.INFO, "Redis backup is not necessary");
            }
            final int dbBackupContentType = this.doDBBackup();
            this.writeBackupXMLFile(backupListForXML, dbBackupContentType, rProps);
            this.compress(DMBackupAction.backupList);
            BackupRestoreUtil.printOneLineLog(Level.INFO, "Backup completed successfully");
            DMBackupAction.LOGGER.log(Level.INFO, "**************************** Backup completed successfully **************************** ");
        }
        catch (final Exception ex) {
            BackupRestoreUtil.printOneLineLog(Level.WARNING, "Exception occurred during Backup due to :" + ex.getMessage());
            if (this.destFolderName != null) {
                DMBackupAction.LOGGER.log(Level.INFO, " Delete zip {0} status :: {1}", new Object[] { DMBackupAction.finalZip, FileUtil.deleteFileOrFolder(new File(DMBackupAction.finalZip)) });
            }
            throw ex;
        }
        finally {
            this.firePropertyChange("status", true, 5);
            ((SwingWorker<T, String>)this).publish("");
            if (this.destFolderName != null) {
                DMBackupAction.LOGGER.log(Level.INFO, " Delete folder {0} status :: {1}", new Object[] { this.destFolderName, FileUtil.deleteFileOrFolder(new File(this.destFolderName)) });
                BackupRestoreUtil.getInstance().deleteTemporaryZipFile(DMBackupAction.temporaryZip1);
                BackupRestoreUtil.getInstance().deleteTemporaryZipFile(DMBackupAction.temporaryZip2);
            }
            BackupRestoreUtil.getInstance().deleteTempFiles();
        }
    }
    
    private void writeBackupXMLFile(final HashMap<Integer, Properties> backupList, final int dbBackupContentType, final Properties rProps) throws Exception {
        final String destXML = this.destFolderName + File.separator + "backup-files.xml";
        DMBackupAction.LOGGER.log(Level.INFO, "DESTINATION XML\t::\t{0}", destXML);
        BackupRestoreUtil.getInstance().createXML(backupList, dbBackupContentType, rProps, destXML);
    }
    
    @Override
    protected void process(final List<String> chunks) {
        if (this.informable != null) {
            for (final String message : chunks) {
                this.informable.messageRead(message);
            }
        }
    }
    
    public void doPreCheck() throws Exception {
        this.firePropertyChange("status", false, 16);
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** Pre check begins **************************** ");
        final long fileCount = BackupRestoreUtil.getInstance().preCheck(this.destination);
        this.firePropertyChange("total_count", null, fileCount);
        if (this.isToAskPermissionForBak && DMDBBackupRestore.isBakFormatEnabled) {
            MssqlBackupRestoreUtil.checkAndWaitForPermissionForBackupRestore();
        }
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** Pre check ends **************************** ");
    }
    
    private HashMap<Integer, Properties> doFilesBackup() throws Exception {
        this.firePropertyChange("status", false, 14);
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** File backup begins **************************** ");
        HashMap<Integer, Properties> backupFileList = null;
        backupFileList = DMFileBackup.getInstance().getBackupList();
        DMFileBackup.getInstance().takeFileBackupList(backupFileList, this.destFolderName);
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** File backup ends **************************** ");
        return backupFileList;
    }
    
    private void doRedisBackup() throws Exception {
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** Redis backup begins **************************** ");
        this.firePropertyChange("status", false, 19);
        DMRedisBackupRestore.backupRedis();
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** Redis backup ends **************************** ");
    }
    
    private int doDBBackup() throws Exception {
        this.firePropertyChange("status", false, 15);
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** DB backup begins **************************** ");
        final DMDBBackupRestore dcdbBackupObj = new DMDBBackupRestore(this.destFolderName, this.informable);
        final int backupContentType = dcdbBackupObj.backupDB();
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** DB backup ends **************************** ");
        return backupContentType;
    }
    
    private void compress(final HashMap<Integer, Properties> backupList) throws Exception {
        this.firePropertyChange("status", false, 4);
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** Compressing files begins **************************** ");
        final CompressUtil compressUtilWithPwd = new CompressUtil(this.informable, this.archivePassword);
        if (!compressUtilWithPwd.compress(backupList, this.destFolderName)) {
            throw new Exception("Exception while compressing backup-contents.");
        }
        BackupRestoreUtil.checkZipTempFile(DMBackupAction.temporaryZip2);
        final CompressUtil compressUtil = new CompressUtil();
        compressUtil.updateFileToArchive(this.destFolderName + ".zip", "DB_Password_Hint.txt", this.serverHome);
        DMBackupAction.LOGGER.log(Level.INFO, "**************************** Compressing files ends **************************** ");
    }
    
    private InputStream getBackupXMLInputStream() {
        return this.getClass().getResourceAsStream("/conf/backup-files.xml");
    }
    
    private String createDestFolder() {
        DMBackupAction.LOGGER.log(Level.INFO, "Creating destination folder");
        final File destFolder = new File(this.destination, this.getDestFolderName());
        final boolean folderCreated = destFolder.mkdirs();
        final String destFolderPath = destFolder.getAbsolutePath();
        DMBackupAction.LOGGER.log(Level.INFO, "DESTINATION FOLDER\t::\t{0}, Folder created? {1}", new Object[] { destFolderPath, folderCreated });
        return destFolderPath;
    }
    
    private void initZipNameForProcess() {
        DMBackupAction.temporaryZip1 = this.destFolderName + "_temp1.zip";
        DMBackupAction.temporaryZip2 = this.destFolderName + "_temp2.zip";
        DMBackupAction.finalZip = this.destFolderName + ".zip";
    }
    
    private String getDestFolderName() {
        String destinationFolderName = null;
        try {
            final Properties backupAttributes = BackupRestoreUtil.getInstance().getBackupAttributes(DMFileBackup.getInstance().getDoc());
            String fileNameFormat = backupAttributes.getProperty("outfile_name_format");
            if (fileNameFormat == null) {
                fileNameFormat = "$BUILDNUMBER-$TIMESTAMP";
            }
            destinationFolderName = BackupRestoreUtil.getInstance().getFormattedName(fileNameFormat);
            DMBackupAction.LOGGER.log(Level.INFO, "DESTINATION FOLDER NAME\t::\t{0}", destinationFolderName);
            DMBackupAction.backupFolderName = destinationFolderName;
        }
        catch (final Exception ex) {
            DMBackupAction.LOGGER.log(Level.WARNING, "Caught Exception while getting destination backup folder name ", ex);
        }
        return destinationFolderName;
    }
    
    static {
        LOGGER = Logger.getLogger("ScheduleDBBackup");
        DMBackupAction.temporaryZip1 = null;
        DMBackupAction.temporaryZip2 = null;
        DMBackupAction.finalZip = null;
        DMBackupAction.backupList = null;
    }
}
