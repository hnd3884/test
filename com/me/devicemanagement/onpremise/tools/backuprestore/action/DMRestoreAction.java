package com.me.devicemanagement.onpremise.tools.backuprestore.action;

import java.util.Hashtable;
import java.io.IOException;
import java.io.Reader;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import org.json.simple.JSONObject;
import com.me.devicemanagement.framework.server.fileaccess.FileAccessUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreContants;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.DataAccess;
import com.adventnet.mfw.Starter;
import java.util.ArrayList;
import java.io.FileInputStream;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.CompressUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DMBackupPasswordHandler;
import java.io.InputStream;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.MssqlBackupRestoreUtil;
import org.w3c.dom.Document;
import java.util.Map;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.FileUtil;
import com.adventnet.persistence.fos.FOS;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.DCBackupRestoreException;
import java.util.logging.Level;
import java.util.Properties;
import java.util.HashMap;
import java.io.File;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.Informable;
import java.util.logging.Logger;
import javax.swing.SwingWorker;

public class DMRestoreAction extends SwingWorker<Void, String>
{
    private static final Logger LOGGER;
    public static String sourceFile;
    public static int dbContenttypeFromBackup;
    private Informable informable;
    private String serverHome;
    private File currentBackupLocation;
    private boolean backupCurrentConfig;
    private String ppmbackupFileLocation;
    private HashMap<Integer, Properties> restoreList;
    private int dbBackupContentType;
    private boolean isRedisConfigured;
    private boolean isRedisAOFEnabled;
    private int fileBackupCurrentIndex;
    private boolean isToAskPermissionForBak;
    Boolean isRedisRevertNeeded;
    Boolean ignore_buildcheck;
    Boolean ignore_productcheck;
    Boolean clear_ppm_lock;
    Boolean include_AllVersions;
    Boolean ignore_architectureCheck;
    Boolean ignore_folderlockCheck;
    String allVersions;
    String tempFolderName;
    
    public DMRestoreAction(final String sourceFile) {
        this.isToAskPermissionForBak = true;
        this.isRedisRevertNeeded = false;
        this.ignore_buildcheck = false;
        this.ignore_productcheck = false;
        this.clear_ppm_lock = false;
        this.include_AllVersions = false;
        this.ignore_architectureCheck = false;
        this.ignore_folderlockCheck = false;
        this.allVersions = null;
        this.tempFolderName = null;
        DMRestoreAction.sourceFile = sourceFile;
        this.informable = null;
    }
    
    public DMRestoreAction(final String sourceFile, final Informable informable, final boolean backupCurrentConfig, final String ppm_backfile, final boolean isToAskPermissionForBak) {
        this.isToAskPermissionForBak = true;
        this.isRedisRevertNeeded = false;
        this.ignore_buildcheck = false;
        this.ignore_productcheck = false;
        this.clear_ppm_lock = false;
        this.include_AllVersions = false;
        this.ignore_architectureCheck = false;
        this.ignore_folderlockCheck = false;
        this.allVersions = null;
        this.tempFolderName = null;
        try {
            DMRestoreAction.sourceFile = sourceFile;
            this.informable = informable;
            this.backupCurrentConfig = backupCurrentConfig;
            this.ppmbackupFileLocation = ppm_backfile;
            this.serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
            this.isToAskPermissionForBak = isToAskPermissionForBak;
        }
        catch (final Exception e) {
            DMRestoreAction.LOGGER.log(Level.WARNING, "Exception ", e);
            this.serverHome = System.getProperty("server.home");
        }
        DMRestoreAction.LOGGER.log(Level.INFO, "SOURCE FILE :: {0}", sourceFile);
        DMRestoreAction.LOGGER.log(Level.INFO, "SERVER HOME :: {0}", this.serverHome);
        DMRestoreAction.LOGGER.log(Level.INFO, "BACKUP CURRENT CONFIG?? :: {0}", backupCurrentConfig);
    }
    
    public DMRestoreAction(final String sourceFile, final Informable informable, final boolean backupCurrentConfig) {
        this(sourceFile, informable, backupCurrentConfig, null, true);
    }
    
    public Void doInBackground() {
        boolean restoreStatus = true;
        DCBackupRestoreException errorInfo = null;
        try {
            this.restore();
        }
        catch (final DCBackupRestoreException e) {
            restoreStatus = false;
            errorInfo = e;
        }
        catch (final Exception e2) {
            restoreStatus = false;
            final String operationName = BackupRestoreUtil.getString("desktopcentral.tools.restore.title", null);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            errorInfo = BackupRestoreUtil.createException(-6, new Object[] { displayName, operationName }, e2);
            DMRestoreAction.LOGGER.log(Level.WARNING, "Exception occurred while performing restore :: ", e2);
        }
        this.firePropertyChange("result", restoreStatus, errorInfo);
        if (restoreStatus && this.dbBackupContentType == 2 && this.isCorruptionFound()) {
            DMRestoreAction.LOGGER.info("Going to add props to corruption.lock for successful restore..");
            this.addPropsForSuccessfulRestore();
        }
        return null;
    }
    
    @Override
    protected void process(final List<String> chunks) {
        if (this.informable != null) {
            for (final String message : chunks) {
                this.informable.messageRead(message);
            }
        }
    }
    
    public synchronized void restore() throws Exception {
        this.firePropertyChange("status", true, 12);
        DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Starting restore **************************** ");
        BackupRestoreUtil.printOneLineLog(Level.INFO, "Starting Restore....");
        if (BackupRestoreUtil.getDBType() == 2 && !BackupRestoreUtil.isRemoteDB() && !BackupRestoreUtil.isDBRunning()) {
            new BackupRestoreUtil();
            BackupRestoreUtil.executeInitPgsql(System.getProperty("server.home"));
        }
        try {
            this.doPreCheck();
            this.createTempFolder();
            this.doFileRestore();
            if (this.isRedisConfigured) {
                this.doRedisRestore(this.isRedisAOFEnabled);
                this.isRedisRevertNeeded = true;
            }
            else {
                DMRestoreAction.LOGGER.log(Level.INFO, "Redis Restore is not necessary");
            }
            this.doDBRestore();
            this.deleteLockFiles();
            if (FOS.isEnabled()) {
                BackupRestoreUtil.getInstance().copyToPeer();
            }
            this.executeInitPgsql();
            this.setProgress(100);
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(4, "success");
            BackupRestoreUtil.getInstance().addBackupTrackingDetails(10, String.valueOf(DMRestoreAction.dbContenttypeFromBackup));
            DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Restore completed successfully **************************** ");
            BackupRestoreUtil.printOneLineLog(Level.INFO, "Restore completed successfully");
        }
        catch (final Exception ex) {
            this.firePropertyChange("status", false, 10);
            DMRestoreAction.LOGGER.log(Level.WARNING, "Caught exception while restoring ", ex);
            BackupRestoreUtil.printOneLineLog(Level.WARNING, "Exception while Restoring due to :" + ex.getMessage());
            final boolean fileRevertStatus = this.backupCurrentConfig && this.revertFileRestore(this.restoreList, this.fileBackupCurrentIndex);
            boolean redisRevertStatus = false;
            DMRestoreAction.LOGGER.log(Level.INFO, "File Revert success?? :: {0}", fileRevertStatus);
            if (this.isRedisRevertNeeded) {
                redisRevertStatus = this.doRedisRevert();
                DMRestoreAction.LOGGER.log(Level.INFO, "Redis Revert success?? :: {0}", redisRevertStatus);
            }
            if (!fileRevertStatus || (this.isRedisRevertNeeded && !redisRevertStatus)) {
                BackupRestoreUtil.getInstance().createRevertLockFile();
                throw BackupRestoreUtil.createException(-12);
            }
            throw ex;
        }
        finally {
            this.firePropertyChange("status", true, 5);
            ((SwingWorker<T, String>)this).publish("");
            this.cleanUp(this.restoreList);
            this.cleanUpRedis();
        }
    }
    
    private void createTempFolder() {
        DMRestoreAction.LOGGER.log(Level.INFO, "Going to create temp folder");
        this.tempFolderName = BackupRestoreUtil.getInstance().getTempFolderName();
        DMDBBackupRestore.tempFolder = this.tempFolderName;
        if (this.backupCurrentConfig) {
            final File currentTempDir = new File(this.serverHome, this.tempFolderName);
            currentTempDir.mkdirs();
            this.currentBackupLocation = currentTempDir;
            DMRestoreAction.LOGGER.log(Level.INFO, "CURRENT BACKUP LOC :: {0}", currentTempDir);
        }
    }
    
    private void doDBRestore() throws Exception {
        this.firePropertyChange("status", false, 18);
        ((SwingWorker<T, String>)this).publish("");
        DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Starting DB restore **************************** ");
        final DMDBBackupRestore dmDBBackupRestoreObj = new DMDBBackupRestore(this.informable);
        dmDBBackupRestoreObj.restore(this.dbBackupContentType);
        DMRestoreAction.LOGGER.log(Level.INFO, "**************************** DB restore completed **************************** ");
    }
    
    private boolean doRedisRevert() throws Exception {
        boolean revertStatus = false;
        try {
            this.firePropertyChange("status", false, 20);
            ((SwingWorker<T, String>)this).publish("");
            DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Starting Redis revert **************************** ");
            final DMRedisBackupRestore dmRedisBackupRestoreObj = new DMRedisBackupRestore(this.informable);
            revertStatus = dmRedisBackupRestoreObj.doRedisRevert(this.tempFolderName, this.isRedisAOFEnabled);
            DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Redis revert completed **************************** ");
        }
        catch (final Exception e) {
            throw e;
        }
        return revertStatus;
    }
    
    private void doRedisRestore(final boolean isAOFEnabled) throws Exception {
        try {
            this.firePropertyChange("status", false, 20);
            ((SwingWorker<T, String>)this).publish("");
            DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Starting Redis restore **************************** ");
            final DMRedisBackupRestore dmRedisBackupRestoreObj = new DMRedisBackupRestore(this.informable);
            dmRedisBackupRestoreObj.doRedisRestore(this.tempFolderName, isAOFEnabled);
            DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Redis restore completed **************************** ");
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    private void doFileRestore() throws Exception {
        this.firePropertyChange("status", false, 17);
        DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Starting file restore **************************** ");
        final FileUtil fileUtil = new FileUtil();
        final long fileCount = FileUtil.getNumberOfFiles(this.restoreList);
        this.firePropertyChange("total_count", null, fileCount);
        DMRestoreAction.LOGGER.log(Level.INFO, "Files to be restored (parsed from backup-files.xml):: {0}", this.restoreList);
        this.fileBackupCurrentIndex = 0;
        try {
            final Document ppmdoc = this.getPPMDoc();
            HashMap<String, Properties> backupFileList_from_PPM = new HashMap<String, Properties>();
            if (ppmdoc != null) {
                backupFileList_from_PPM = BackupRestoreUtil.getInstance().getFileList(ppmdoc);
                DMRestoreAction.LOGGER.log(Level.INFO, "PPM  BACKUP LIST :: {0}", backupFileList_from_PPM);
            }
            for (final Map.Entry<Integer, Properties> entry : this.restoreList.entrySet()) {
                this.fileBackupCurrentIndex = entry.getKey();
                final Properties restoreProps = entry.getValue();
                DMRestoreAction.LOGGER.log(Level.INFO, " ********************* ( {0} ) {1}  ************************", new Object[] { this.fileBackupCurrentIndex, ((Hashtable<K, Object>)restoreProps).get("file_path") });
                final String file_path = ((Hashtable<K, String>)restoreProps).get("file_path");
                if (backupFileList_from_PPM.containsKey(file_path)) {
                    DMRestoreAction.LOGGER.log(Level.INFO, "File Path {0} :: is in exclude List", file_path);
                    final Properties excludeProps = backupFileList_from_PPM.get(file_path);
                    final Boolean exclude_restore = Boolean.valueOf(excludeProps.getProperty("exclude_restore"));
                    DMRestoreAction.LOGGER.log(Level.INFO, "Value of exclude_restore is in exclude List {0}", exclude_restore);
                    if (exclude_restore) {
                        DMRestoreAction.LOGGER.log(Level.INFO, "Going to skip restore for file ::{0}", file_path);
                        continue;
                    }
                }
                if (this.backupCurrentConfig) {
                    this.takeCurrentBackup(restoreProps, this.tempFolderName);
                }
                else {
                    this.renameIfFolder(restoreProps, this.tempFolderName);
                }
                this.restoreFileList(restoreProps);
            }
        }
        catch (final Exception ex) {
            DMRestoreAction.LOGGER.log(Level.INFO, "Exception while restoring backup files : ", ex);
            throw ex;
        }
        DMRestoreAction.LOGGER.log(Level.INFO, "**************************** File restore completed **************************** ");
    }
    
    private void doPreCheck() throws Exception {
        this.firePropertyChange("status", true, 16);
        DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Running Precheck **************************** ");
        BackupRestoreUtil.killAssociatedProcesses();
        final BackupRestoreUtil util = BackupRestoreUtil.getInstance();
        final InputStream xmlStream = this.getRestoreXMLInputStream();
        if (xmlStream == null) {
            throw BackupRestoreUtil.createException(-2);
        }
        DMRestoreAction.LOGGER.log(Level.INFO, "Parsing XML File :: {0}", "backup-files.xml");
        final Document doc = util.parseXML(xmlStream);
        Document ppmdoc = null;
        final Properties backupDetails = util.getBackupDetails(doc);
        Properties restorefromPPMList = new Properties();
        final Properties backupAttrDetails = util.getBackupAttributes(doc);
        String backup_fileName = this.ppmbackupFileLocation;
        DMRestoreAction.LOGGER.log(Level.INFO, "Backup Attributes Details from Restore Zip for PPM :: {0}", backupAttrDetails);
        if (backup_fileName == null && !backupAttrDetails.isEmpty()) {
            DMRestoreAction.LOGGER.log(Level.INFO, "backup-ppm-filename is Empty,Going to get value from XML");
            backup_fileName = backupAttrDetails.getProperty("backup-ppm-filename");
        }
        if (new File(backup_fileName).exists()) {
            ppmdoc = this.getPPMDoc();
            if (ppmdoc != null) {
                restorefromPPMList = util.getBackupAttributes(ppmdoc);
            }
        }
        else {
            DMRestoreAction.LOGGER.log(Level.INFO, "No File present for backup PPM with Name ::{0}", backup_fileName);
        }
        DMRestoreAction.LOGGER.log(Level.INFO, "Backup Details from Restore Zip :: {0}", backupDetails);
        final String dbBackupContentTypeValue = backupDetails.getProperty("backup_content_type");
        DMRestoreAction.LOGGER.log(Level.INFO, "Backup Content type : {0}", dbBackupContentTypeValue);
        if (dbBackupContentTypeValue != null) {
            this.dbBackupContentType = Integer.parseInt(dbBackupContentTypeValue.trim());
        }
        else {
            this.dbBackupContentType = 3;
        }
        DMRestoreAction.dbContenttypeFromBackup = this.dbBackupContentType;
        this.isRedisConfigured = Boolean.valueOf(backupDetails.getProperty("isRedisBackupRestoreNeeded"));
        this.isRedisAOFEnabled = Boolean.valueOf(backupDetails.getProperty("isRedisAOFEnabled"));
        this.isRedisRevertNeeded = Boolean.FALSE;
        DMRestoreAction.LOGGER.log(Level.INFO, "Backup Details from PPM Zip :: {0}", restorefromPPMList);
        if (restorefromPPMList.containsKey("ignore_build_number_check")) {
            this.ignore_buildcheck = Boolean.valueOf(restorefromPPMList.getProperty("ignore_build_number_check"));
        }
        if (restorefromPPMList.containsKey("clear_ppm_lock")) {
            this.clear_ppm_lock = Boolean.valueOf(restorefromPPMList.getProperty("clear_ppm_lock"));
        }
        if (restorefromPPMList.containsKey("include_AllVersions")) {
            this.include_AllVersions = Boolean.valueOf(restorefromPPMList.getProperty("include_AllVersions"));
        }
        if (restorefromPPMList.containsKey("all_versions_value")) {
            this.allVersions = restorefromPPMList.getProperty("all_versions_value");
        }
        if (restorefromPPMList.containsKey("ignore_product_name_check")) {
            this.ignore_productcheck = Boolean.valueOf(restorefromPPMList.getProperty("ignore_product_name_check"));
        }
        if (restorefromPPMList.containsKey("ignore_product_architecture_check")) {
            this.ignore_architectureCheck = Boolean.valueOf(restorefromPPMList.getProperty("ignore_product_architecture_check"));
        }
        if (restorefromPPMList.containsKey("ignore_folder_lock_check")) {
            this.ignore_folderlockCheck = Boolean.valueOf(restorefromPPMList.getProperty("ignore_folder_lock_check"));
        }
        this.checkForExceptions(backupDetails);
        util.checkFreeDiskSpace(this.restoreList = util.getFileListFromXML(doc), this.serverHome, 2.0f, "desktopcentral.tools.restore.title");
        this.checkMssqlVersionCompatibility();
        if (MssqlBackupRestoreUtil.getInstance().getActiveDBServer().equals("mssql") && ((this.isToAskPermissionForBak && DMDBBackupRestore.isBakFormatEnabled) || DMRestoreAction.dbContenttypeFromBackup == 1)) {
            MssqlBackupRestoreUtil.checkAndWaitForPermissionForBackupRestore();
        }
        DMRestoreAction.LOGGER.log(Level.INFO, "**************************** Precheck completed **************************** ");
    }
    
    private void checkMssqlVersionCompatibility() throws DCBackupRestoreException {
        try {
            if (MssqlBackupRestoreUtil.getInstance().getActiveDBServer().equals("mssql")) {
                if (!new File(this.serverHome + File.separator + "BackupTemp").exists()) {
                    new File(this.serverHome + File.separator + "BackupTemp").mkdir();
                }
                final CompressUtil compressUtil = new CompressUtil(this.informable, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
                compressUtil.extractFileFromArchive(DMRestoreAction.sourceFile, "conf" + File.separator + "server_info.props", this.serverHome + File.separator + "BackupTemp");
                final Properties backupServerInfoProps = BackupRestoreUtil.readProperties(this.serverHome + File.separator + "BackupTemp" + File.separator + "conf" + File.separator + "server_info.props");
                final String backupDBVersion = backupServerInfoProps.getProperty("db.version");
                if (backupDBVersion == null) {
                    DMRestoreAction.LOGGER.log(Level.INFO, "Problem in getting db.version from server_info.props in backup");
                    throw BackupRestoreUtil.createException(-20);
                }
                final int backupDBMajorVersion = Integer.parseInt(backupDBVersion.split("\\.")[0]);
                final Properties serverInfoProps = BackupRestoreUtil.readProperties(this.serverHome + File.separator + "conf" + File.separator + "server_info.props");
                final String dBVersion = serverInfoProps.getProperty("db.version");
                if (dBVersion == null) {
                    DMRestoreAction.LOGGER.log(Level.INFO, "Problem in getting db.version from server_info.props in setup");
                    throw BackupRestoreUtil.createException(-20);
                }
                final int dBMajorVersion = Integer.parseInt(dBVersion.split("\\.")[0]);
                if (backupDBMajorVersion != dBMajorVersion) {
                    DMRestoreAction.LOGGER.log(Level.INFO, "major versions of backup mssql and setup mssql are not same so unable to restore.");
                    throw BackupRestoreUtil.createException(-21, new String[] { backupDBMajorVersion + "", dBMajorVersion + "" }, null);
                }
            }
            FileUtil.deleteFileOrFolder(new File(this.serverHome + File.separator + "BackupTemp"));
        }
        catch (final DCBackupRestoreException dcBackupRestoreException) {
            throw dcBackupRestoreException;
        }
        catch (final Exception exception) {
            DMRestoreAction.LOGGER.log(Level.INFO, "Exception while checking mssql versions.", exception);
            throw BackupRestoreUtil.createException(-20);
        }
    }
    
    private Document getPPMDoc() throws Exception {
        Document ppmDoc = null;
        if (this.ppmbackupFileLocation != null) {
            final InputStream ppmxmlStream = new FileInputStream(this.ppmbackupFileLocation);
            ppmDoc = BackupRestoreUtil.getInstance().parseXML(ppmxmlStream);
        }
        return ppmDoc;
    }
    
    private void executeInitPgsql() {
        final String db_name = MssqlBackupRestoreUtil.getInstance().getActiveDBServer();
        DMRestoreAction.LOGGER.log(Level.INFO, "DB Name is :: {0}", db_name);
        if (db_name != null && db_name.equalsIgnoreCase("postgres")) {
            final String binFolder = this.serverHome + File.separator + "bin";
            final String initPgsqlBat = binFolder + File.separator + "initPgsql.bat";
            DMRestoreAction.LOGGER.log(Level.INFO, "going to execute command" + initPgsqlBat);
            final File filepath = new File(binFolder);
            final List<String> command = new ArrayList<String>();
            DMRestoreAction.LOGGER.log(Level.INFO, "Going to set AuthenticatedUsers Prvilege for Data Folder");
            try {
                command.add("cmd.exe");
                command.add("/c");
                command.add("initPgsql.bat");
                final ProcessBuilder processBuilder = new ProcessBuilder(command);
                processBuilder.directory(filepath);
                DMRestoreAction.LOGGER.log(Level.INFO, "COMMAND : {0}", processBuilder.command());
                final Process process = processBuilder.start();
                DMRestoreAction.LOGGER.log(Level.INFO, "Successfully set AuthenticatedUsers Prvilege to Data Folder");
            }
            catch (final Exception ex) {
                DMRestoreAction.LOGGER.log(Level.WARNING, "Exception occured while Setting Authenticated Users Prvilege for Data Folder ", ex);
            }
        }
    }
    
    private InputStream getRestoreXMLInputStream() throws Exception {
        final CompressUtil compressUtil = new CompressUtil(this.informable, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
        compressUtil.extractFileFromArchive(DMRestoreAction.sourceFile, "backup-files.xml", this.serverHome);
        final File xmlFile = new File(this.serverHome + File.separator + "backup-files.xml");
        final InputStream xmlStream = new FileInputStream(xmlFile);
        return xmlStream;
    }
    
    private void checkForExceptions(final Properties backupDetails) throws DCBackupRestoreException {
        final BackupRestoreUtil util = new BackupRestoreUtil();
        final String backupProductName = backupDetails.getProperty("product_name").trim();
        final String productName = util.getProductName().trim();
        if (!this.ignore_productcheck && !productName.equalsIgnoreCase(backupProductName)) {
            final Object[] arguments = { productName, backupProductName };
            throw BackupRestoreUtil.createException(-9, arguments, null);
        }
        final String backupBuildNumber = backupDetails.getProperty("build_no").trim();
        final String productBuildNumber = util.getBuildNumber();
        if (!this.ignore_buildcheck && !productBuildNumber.equalsIgnoreCase(backupBuildNumber)) {
            final Object[] arguments2 = { productBuildNumber, backupBuildNumber };
            throw BackupRestoreUtil.createException(-3, arguments2, null);
        }
        final String backupDatabase = backupDetails.getProperty("database").trim();
        final String productDatabase = MssqlBackupRestoreUtil.getInstance().getActiveDBServer();
        if (!productDatabase.equalsIgnoreCase(backupDatabase)) {
            final Object[] arguments3 = { productDatabase, backupDatabase };
            throw BackupRestoreUtil.createException(-10, arguments3, null);
        }
        Boolean isFosEnabled = Boolean.FALSE;
        try {
            isFosEnabled = FOS.isEnabled();
        }
        catch (final Exception ex) {
            DMRestoreAction.LOGGER.log(Level.SEVERE, "Exception while checking if failover is enabled.. {0}", ex);
            throw BackupRestoreUtil.createException(-6, new Object[] { productName }, ex);
        }
        if (!Starter.checkShutdownListenerPort()) {
            throw BackupRestoreUtil.createException(-4, new Object[] { productName }, null);
        }
        if (!BackupRestoreUtil.maintenanceCompletedSuccessfully()) {
            throw BackupRestoreUtil.createException(-18, new Object[] { productName }, null);
        }
        if (isFosEnabled) {
            DMRestoreAction.LOGGER.log(Level.INFO, "FOS is enabled");
            try {
                if (DataAccess.getTransactionManager() == null) {
                    PersistenceInitializer.initializeDB(System.getProperty("server.home") + File.separator + "conf");
                    PersistenceInitializer.initializeMickey(true);
                }
            }
            catch (final Exception ex) {
                DMRestoreAction.LOGGER.log(Level.SEVERE, "Exception while checking transaction manager in use{0}", ex);
            }
            if (FOS.standAloneMasterHealthCheck()) {
                DMRestoreAction.LOGGER.log(Level.INFO, "From Stand Alone Health Check.. Server Status : true");
                try {
                    PersistenceInitializer.stopDB();
                }
                catch (final Exception ex) {
                    DMRestoreAction.LOGGER.log(Level.SEVERE, "Exception while stopping database..", ex);
                }
                throw BackupRestoreUtil.createException(-16, new Object[] { productName }, null);
            }
            if (!util.isOtherServerInFosReachable()) {
                DMRestoreAction.LOGGER.log(Level.INFO, "failover server is not reachable to perform the restore operation..");
                try {
                    PersistenceInitializer.stopDB();
                }
                catch (final Exception ex) {
                    DMRestoreAction.LOGGER.log(Level.SEVERE, "Exception while stopping database..", ex);
                }
                throw BackupRestoreUtil.createException(-15, new Object[] { productName }, null);
            }
            try {
                PersistenceInitializer.stopDB();
            }
            catch (final Exception ex) {
                DMRestoreAction.LOGGER.log(Level.SEVERE, "Exception while stopping database..", ex);
            }
        }
        if (!this.ignore_architectureCheck) {
            final String backupArch = backupDetails.getProperty("product_arch").trim();
            final String operationName = BackupRestoreUtil.getString("desktopcentral.tools.restore.title", null);
            final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
            Boolean isDCProduct64bit = null;
            try {
                isDCProduct64bit = BackupRestoreUtil.isDCProduct64bit();
                if (isDCProduct64bit == null) {
                    final String exception = "Unable to determine the Architecture of the product as 32-bit or 64-bit in method isDCProduct64bit()...";
                    throw new Exception(exception);
                }
            }
            catch (final Exception e) {
                DMRestoreAction.LOGGER.log(Level.INFO, "Exception on checkForExceptions() method while checking whether the product is 32-bit/64-bit...", e);
                throw BackupRestoreUtil.createException(-6, new Object[] { displayName, operationName }, e);
            }
            if (isDCProduct64bit != null) {
                final String productArch = isDCProduct64bit ? "64-bit" : "32-bit";
                if (!productArch.equalsIgnoreCase(backupArch)) {
                    final Object[] arguments4 = { productArch, backupArch };
                    throw BackupRestoreUtil.createException(-14, arguments4, null);
                }
            }
        }
        try {
            if (!BackupRestoreUtil.isRemoteDB() && BackupRestoreUtil.isDBRunning()) {
                DMRestoreAction.LOGGER.log(Level.INFO, "DB is running. Going to stop the database..");
                BackupRestoreUtil.stopDB();
            }
        }
        catch (final Exception ex) {
            DMRestoreAction.LOGGER.log(Level.SEVERE, "Exception while stopping database..", ex);
        }
        if (!this.ignore_folderlockCheck) {
            final String confFolderPath = this.serverHome + File.separator + "conf";
            final String dataFolderPath = this.serverHome + File.separator + "pgsql" + File.separator + "data";
            boolean isFolderRenamable;
            try {
                isFolderRenamable = FileUtil.isFolderRenamable(confFolderPath);
                if (!MssqlBackupRestoreUtil.getInstance().getActiveDBServer().equalsIgnoreCase("mssql")) {
                    isFolderRenamable = (isFolderRenamable && FileUtil.isFolderRenamable(dataFolderPath));
                }
            }
            catch (final Exception ex2) {
                DMRestoreAction.LOGGER.log(Level.SEVERE, "Caught exception in checking folder rename: ", ex2);
                final String displayName2 = new BackupRestoreUtil().getValueFromGenProps("displayname");
                final String operation = BackupRestoreUtil.getString("desktopcentral.tools.restore.title", null);
                throw BackupRestoreUtil.createException(-6, new Object[] { displayName2, operation }, ex2);
            }
            if (!isFolderRenamable) {
                final String displayName3 = new BackupRestoreUtil().getValueFromGenProps("displayname");
                throw BackupRestoreUtil.createException(-11, new Object[] { displayName3 }, null);
            }
        }
    }
    
    private void restoreFileList(final Properties restoreProps) throws Exception {
        final String filePath = restoreProps.getProperty("file_path");
        final CompressUtil compressUtil = new CompressUtil(this.informable, DMBackupPasswordHandler.getInstance().getPassword(Boolean.FALSE));
        boolean moreThanOneFilePathExists = false;
        boolean extractSuccess = false;
        final String[] filePathsToRestore = filePath.split(",");
        if (filePathsToRestore.length > 1) {
            moreThanOneFilePathExists = true;
        }
        if (!moreThanOneFilePathExists) {
            extractSuccess = compressUtil.decompress(DMRestoreAction.sourceFile, filePath, this.serverHome);
            DMRestoreAction.LOGGER.log(Level.INFO, "Extraction status \t :: \t {0}", extractSuccess);
            if (!extractSuccess) {
                throw new Exception("Exception while unzipping filePath" + filePath);
            }
        }
        else {
            for (int i = 0; i < filePathsToRestore.length; ++i) {
                extractSuccess = compressUtil.decompress(DMRestoreAction.sourceFile, filePathsToRestore[i], this.serverHome);
                DMRestoreAction.LOGGER.log(Level.INFO, filePathsToRestore[i] + " Extraction status \t :: \t {1}", extractSuccess);
                if (!extractSuccess) {
                    throw new Exception("Exception while unzipping filePath" + filePathsToRestore[i]);
                }
            }
        }
        this.restoreFile(restoreProps);
    }
    
    private void takeCurrentBackup(final Properties props, final String tempFolderName) throws Exception {
        final String filePath = props.getProperty("file_path");
        final String backupOptions = props.getProperty("backup_options");
        final String handlerClass = props.getProperty("handler_class");
        final boolean ignoreError = props.getProperty("ignore_error").equalsIgnoreCase("true");
        Properties currentBackupProps = null;
        ((SwingWorker<T, String>)this).publish(filePath);
        if (handlerClass == null && backupOptions.equalsIgnoreCase("copy")) {
            currentBackupProps = this.fileSystemBackup(filePath, this.currentBackupLocation, tempFolderName);
        }
        if (currentBackupProps != null) {
            DMRestoreAction.LOGGER.log(Level.INFO, "Current backup type \t :: \t {0}", currentBackupProps.getProperty("current_backup_type"));
            DMRestoreAction.LOGGER.log(Level.INFO, "Current backup path \t :: \t {0}", currentBackupProps.getProperty("current_backup_path"));
            props.putAll(currentBackupProps);
            return;
        }
        if (ignoreError) {
            return;
        }
        final String displayName = new BackupRestoreUtil().getValueFromGenProps("displayname");
        throw BackupRestoreUtil.createException(-11, new Object[] { displayName }, null);
    }
    
    private void renameIfFolder(final Properties props, final String suffix) throws Exception {
        final String filePath = props.getProperty("file_path");
        final String backupOptions = props.getProperty("backup_options");
        final String handlerClass = props.getProperty("handler_class");
        Properties currentBackupProps = null;
        ((SwingWorker<T, String>)this).publish(filePath);
        final File file = new File(this.serverHome, filePath);
        if (file.isDirectory()) {
            if (handlerClass == null && backupOptions.equalsIgnoreCase("copy")) {
                currentBackupProps = this.fileSystemBackup(filePath, this.currentBackupLocation, suffix);
            }
            else {
                DMRestoreAction.LOGGER.log(Level.INFO, " !!!!! Handler Class found : {0}, Backupoptions : {1} !!!!!", new Object[] { handlerClass, backupOptions });
            }
        }
        if (currentBackupProps != null) {
            DMRestoreAction.LOGGER.log(Level.INFO, "Current backup type \t :: \t {0}", currentBackupProps.getProperty("current_backup_type"));
            DMRestoreAction.LOGGER.log(Level.INFO, "Current backup path \t :: \t {0}", currentBackupProps.getProperty("current_backup_path"));
            props.putAll(currentBackupProps);
        }
    }
    
    private Properties fileSystemBackup(final String filePath, final File destination, final String suffix) {
        File file = null;
        final FileUtil fileUtil = new FileUtil();
        Properties currentBackupProps = null;
        String currentBackupType = null;
        String currentBackupPath = null;
        boolean copySuccess = false;
        boolean moreThanOneFilePathExists = false;
        final String[] filePathsToRestore = filePath.split(",");
        if (filePathsToRestore.length > 1) {
            moreThanOneFilePathExists = true;
        }
        if (!moreThanOneFilePathExists) {
            file = new File(this.serverHome, filePath);
            DMRestoreAction.LOGGER.log(Level.INFO, "filePath present? \t :: \t {0}", file.exists());
            if (file.isFile()) {
                copySuccess = FileUtil.copy(this.serverHome, filePath, destination.getAbsolutePath(), this.informable);
                currentBackupType = "copy";
                currentBackupPath = destination + File.separator + filePath;
            }
            else {
                final String tempName = file.getAbsolutePath() + "-" + suffix;
                copySuccess = FileUtil.renameFolder(file.getAbsolutePath(), tempName);
                currentBackupType = "rename";
                currentBackupPath = tempName;
            }
            if (copySuccess) {
                currentBackupProps = new Properties();
                currentBackupProps.setProperty("current_backup_type", currentBackupType);
                currentBackupProps.setProperty("current_backup_path", currentBackupPath);
            }
            return currentBackupProps;
        }
        for (int i = 0; i < filePathsToRestore.length; ++i) {
            file = new File(this.serverHome, filePathsToRestore[i]);
            DMRestoreAction.LOGGER.log(Level.INFO, "filePath present? \t :: \t {0}", file.exists());
            if (file.isFile()) {
                copySuccess = FileUtil.copy(this.serverHome, filePathsToRestore[i], destination.getAbsolutePath(), this.informable);
                currentBackupType = "copy";
                currentBackupPath = destination + File.separator + filePathsToRestore[i];
            }
            if (copySuccess && i == 0) {
                currentBackupProps = new Properties();
                currentBackupProps.setProperty("current_backup_type", currentBackupType);
                currentBackupProps.setProperty("current_backup_path", currentBackupPath);
            }
        }
        return currentBackupProps;
    }
    
    private void restoreFile(final Properties restoreProps) throws Exception {
        final String backupType = restoreProps.getProperty("backup_type");
        final String filePath = restoreProps.getProperty("file_path");
        final FileUtil fileUtil = new FileUtil();
        final Long actualSize = Long.parseLong(restoreProps.getProperty("size"));
        DMRestoreAction.LOGGER.log(Level.INFO, "Actual backup type \t :: \t {0}", backupType);
        if (backupType.equalsIgnoreCase("copy")) {
            boolean moreThanOneFilePathExists = false;
            final String[] filePathsToRestore = filePath.split(",");
            long copiedSize = 0L;
            if (filePathsToRestore.length > 1) {
                moreThanOneFilePathExists = true;
            }
            if (!moreThanOneFilePathExists) {
                final File file = new File(this.serverHome, filePath);
                copiedSize = FileUtil.getFileOrFolderSize(file);
            }
            else {
                for (int i = 0; i < filePathsToRestore.length; ++i) {
                    final File file2 = new File(this.serverHome, filePathsToRestore[i]);
                    copiedSize += FileUtil.getFileOrFolderSize(file2);
                }
            }
            DMRestoreAction.LOGGER.log(Level.INFO, "Actual size \t\t :: \t {0}", actualSize);
            DMRestoreAction.LOGGER.log(Level.INFO, "Copied size \t\t :: \t {0}", copiedSize);
            DMRestoreAction.LOGGER.log(Level.INFO, "Copy success??\t\t :: \t {0}", actualSize == copiedSize);
        }
        else {
            DMRestoreAction.LOGGER.log(Level.INFO, "!!!!! Not copy backup !!!!!");
        }
    }
    
    private boolean revertFileRestore(final HashMap<Integer, Properties> restoreList, final Integer fileIndex) throws Exception {
        DMRestoreAction.LOGGER.log(Level.INFO, "Reverting to current setup.., till fileIndex :: {0}", fileIndex);
        boolean status = true;
        final FileUtil fileUtil = new FileUtil();
        for (int i = 1; i <= fileIndex; ++i) {
            final Properties restoreProps = restoreList.get(i);
            DMRestoreAction.LOGGER.log(Level.INFO, "Restore props :: {0}", restoreProps);
            if (restoreProps != null) {
                final String currentBackupType = restoreProps.getProperty("current_backup_type");
                final String currentBackupPath = restoreProps.getProperty("current_backup_path");
                if (currentBackupType != null && currentBackupPath != null) {
                    final String filePath = restoreProps.getProperty("file_path");
                    final File file = new File(this.serverHome, filePath);
                    if (currentBackupType != null && currentBackupType.equalsIgnoreCase("copy")) {
                        status = FileUtil.copy(currentBackupPath, null, file.getAbsolutePath(), this.informable);
                    }
                    else if (currentBackupType != null && currentBackupType.equalsIgnoreCase("rename")) {
                        final String fullPath = file.getAbsolutePath();
                        FileUtil.deleteFileOrFolder(file);
                        status = FileUtil.renameFolder(currentBackupPath, fullPath);
                    }
                    else if (currentBackupType != null && currentBackupType.equalsIgnoreCase("dump")) {
                        DMRestoreAction.LOGGER.log(Level.INFO, "!!!!! Dump backup found !!!!!");
                    }
                    if (!status) {
                        return false;
                    }
                }
            }
        }
        return status;
    }
    
    private void cleanUpRedis() {
        final FileUtil fileUtil = new FileUtil();
        DMRestoreAction.LOGGER.log(Level.INFO, "Cleaning up redis temp data folder");
        final File dataFolder = new File(this.serverHome, BackupRestoreContants.REDIS_DATA_FOLDER_LOC);
        final String tempDataFolderPath = dataFolder.getAbsolutePath() + "-" + this.tempFolderName;
        final File tempDataFolder = new File(tempDataFolderPath);
        if (FileUtil.isFileExists(tempDataFolderPath)) {
            if (!FileUtil.isFileExists(dataFolder.getAbsolutePath())) {
                DMRestoreAction.LOGGER.log(Level.INFO, "Data folder does not exists, but temp data folder exists, hence going to rename temp to data:");
                final boolean dataCopySuccess = FileUtil.renameFolder(tempDataFolderPath, dataFolder.getAbsolutePath());
                DMRestoreAction.LOGGER.log(Level.INFO, "Redis data Folder Clean Up status :" + dataCopySuccess);
            }
            else {
                final boolean status = FileUtil.deleteFileOrFolder(tempDataFolder);
                DMRestoreAction.LOGGER.log(Level.INFO, "Redis data Folder Clean Up status :" + status);
            }
        }
    }
    
    private void cleanUp(final HashMap<Integer, Properties> fileList) {
        final FileUtil fileUtil = new FileUtil();
        try {
            if (this.currentBackupLocation != null) {
                FileUtil.deleteFileOrFolder(this.currentBackupLocation);
            }
            final File tempFolderFullPath = new File(DMRestoreAction.sourceFile.substring(0, DMRestoreAction.sourceFile.lastIndexOf(".")));
            if (tempFolderFullPath.exists() && tempFolderFullPath.isDirectory()) {
                FileUtil.deleteFileOrFolder(tempFolderFullPath);
            }
            if (fileList != null) {
                for (final Map.Entry<Integer, Properties> entry : fileList.entrySet()) {
                    final Properties restoreProps = entry.getValue();
                    final String currentBackupType = restoreProps.getProperty("current_backup_type");
                    final String currentBackupPath = restoreProps.getProperty("current_backup_path");
                    if (currentBackupPath != null) {
                        final File fileToBeDeleted = new File(currentBackupPath);
                        if (!currentBackupType.equalsIgnoreCase("rename")) {
                            continue;
                        }
                        FileUtil.deleteFileOrFolder(fileToBeDeleted);
                    }
                }
            }
            BackupRestoreUtil.getInstance().deleteTempFiles();
        }
        catch (final Exception ex) {
            DMRestoreAction.LOGGER.log(Level.WARNING, "Exception in cleaning up temp folder : ", ex);
        }
    }
    
    private void deleteLockFiles() {
        DMRestoreAction.LOGGER.log(Level.INFO, "Restore successful - Going to delete ppm.lock and ppm.err and revert.lock file if exists.");
        if (this.clear_ppm_lock) {
            final String ppmLockFile = this.serverHome + File.separator + "bin" + File.separator + "ppm.lock";
            this.deleteFileIfExists(ppmLockFile);
        }
        final String ppmErrFile = this.serverHome + File.separator + "Patch" + File.separator + "ppm.err";
        this.deleteFileIfExists(ppmErrFile);
        final String revertLockFile = this.serverHome + File.separator + "bin" + File.separator + "revert.lock";
        this.deleteFileIfExists(revertLockFile);
        try {
            DMRestoreAction.LOGGER.log(Level.INFO, " Going to delete ws.modtime file ");
            final String wsModifiedTime = this.serverHome + File.separator + "conf" + File.separator + "ws.modtime";
            final File wsModifiedTimeFile = new File(wsModifiedTime);
            if (wsModifiedTimeFile.exists()) {
                DMRestoreAction.LOGGER.log(Level.INFO, "ws.modtime file deleted status : " + wsModifiedTimeFile.delete());
            }
        }
        catch (final Exception ex) {
            DMRestoreAction.LOGGER.log(Level.WARNING, "Exception while deleting ws.modtime file  :: ", ex);
        }
    }
    
    private void deleteFileIfExists(final String fileName) {
        final File file = new File(fileName);
        if (file.exists()) {
            DMRestoreAction.LOGGER.log(Level.INFO, "Lock file exists. Going to delete: {0} ", file);
            final boolean result = file.delete();
            DMRestoreAction.LOGGER.log(Level.INFO, "Lock file delete result :: {0} ", result);
        }
        else {
            DMRestoreAction.LOGGER.log(Level.INFO, "Lock file {0} does not exist!", fileName);
        }
    }
    
    private void addPropsForSuccessfulRestore() {
        final String corruptionLock = System.getProperty("server.home") + File.separator + "bin" + File.separator + "corruption.lock";
        try {
            final Properties backupProperties = FileAccessUtil.readProperties(corruptionLock);
            backupProperties.setProperty("restore.successful", "true");
            backupProperties.setProperty("clear.corruption.props", "true");
            FileAccessUtil.storeProperties(backupProperties, corruptionLock, false);
        }
        catch (final Exception e) {
            DMRestoreAction.LOGGER.log(Level.WARNING, "Exception caught while writing props into corruption.lock", e);
        }
    }
    
    private boolean isCorruptionFound() {
        final String corruptionJsonLogFile = System.getProperty("server.home") + File.separator + "logs" + File.separator + "corruptionInfo.json";
        final File file = new File(corruptionJsonLogFile);
        JSONObject jsonObject = new JSONObject();
        boolean isCorruptionFound = false;
        FileReader fileReader = null;
        try {
            if (file.exists()) {
                fileReader = new FileReader(file);
                final JSONParser parser = new JSONParser();
                jsonObject = (JSONObject)parser.parse((Reader)fileReader);
            }
            isCorruptionFound = (jsonObject.get((Object)"current") != null);
        }
        catch (final Exception ex) {
            DMRestoreAction.LOGGER.log(Level.WARNING, "Failed Load the JSON File : " + file);
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (final IOException e) {
                    DMRestoreAction.LOGGER.log(Level.WARNING, "Failed Close the JSON File Reader: " + file);
                }
            }
        }
        finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                }
                catch (final IOException e2) {
                    DMRestoreAction.LOGGER.log(Level.WARNING, "Failed Close the JSON File Reader: " + file);
                }
            }
        }
        return isCorruptionFound;
    }
    
    static {
        LOGGER = Logger.getLogger("DCBackupRestoreUI");
    }
}
