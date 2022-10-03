package com.adventnet.db.adapter;

import java.util.Hashtable;
import com.zoho.framework.utils.archive.SevenZipUtils;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import com.adventnet.db.migration.fkgraph.HierarchyProcessor;
import com.adventnet.db.persistence.metadata.util.TemplateMetaHandler;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.Collection;
import com.adventnet.db.archive.TableArchiverUtil;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.io.FileInputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.zoho.framework.utils.archive.ZipUtils;
import com.zoho.framework.utils.FileUtils;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.net.MalformedURLException;
import java.util.Iterator;
import com.adventnet.persistence.xml.Xml2DoConverter;
import java.util.ArrayList;
import com.zoho.conf.Configuration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.DerivedColumn;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.persistence.Row;
import java.sql.Connection;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.mfw.BackupDB;
import com.zoho.mickey.exception.PasswordException;
import java.sql.SQLException;
import com.adventnet.persistence.PersistenceInitializer;
import com.zoho.framework.utils.OSCheckUtil;
import com.zoho.framework.utils.crypto.CryptoUtil;
import java.io.File;
import java.sql.Statement;
import java.util.logging.Level;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.Messenger;
import com.zoho.mickey.crypto.BackupPasswordProvider;
import java.util.Properties;
import com.zoho.mickey.crypto.PasswordProvider;
import javax.sql.DataSource;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.mfw.VersionHandler;
import java.util.logging.Logger;

public abstract class AbstractBackupHandler implements BackupHandler
{
    private static final Logger LOGGER;
    public static final String BACKUP_STATUS_TOPIC = "BackupStatusTopic";
    protected static boolean backupDBInProgress;
    protected VersionHandler backupRestoreHandler;
    protected String backupSanityChecker;
    protected String backupRestoreListener;
    private static BackupRestoreSanityChecker backupChecker;
    private static MessageListener backupListener;
    protected DBAdapter dbAdapter;
    protected SQLGenerator sqlGenerator;
    protected DBInitializer dbInitializer;
    protected DataSource dataSource;
    boolean backupNonMickeyTables;
    protected PasswordProvider passwordProvider;
    
    public AbstractBackupHandler() {
        this.backupRestoreHandler = null;
        this.backupSanityChecker = null;
        this.backupRestoreListener = null;
        this.dbAdapter = null;
        this.sqlGenerator = null;
        this.dbInitializer = null;
        this.dataSource = null;
        this.backupNonMickeyTables = false;
        this.passwordProvider = null;
    }
    
    protected void initDBAdapter(final DBAdapter dbAdapter) {
        if (this.dbAdapter == null) {
            if (dbAdapter instanceof WrappedDBAdapter) {
                this.dbAdapter = ((WrappedDBAdapter)dbAdapter).getDBAdapter(0);
            }
            else {
                this.dbAdapter = dbAdapter;
            }
        }
    }
    
    protected void initDBInitializer(final DBInitializer dbInitializer) {
        if (this.dbInitializer == null) {
            this.dbInitializer = dbInitializer;
        }
    }
    
    protected void initSQLGenerator(final SQLGenerator sqlGenerator) {
        if (this.sqlGenerator == null) {
            this.sqlGenerator = sqlGenerator;
        }
    }
    
    protected void initDataSource(final DataSource dataSource) {
        if (this.dataSource == null) {
            this.dataSource = dataSource;
        }
    }
    
    protected void initialize(final Properties dbProps) {
        try {
            this.initVersionHandler(dbProps.getProperty("VersionHandler"));
            this.backupRestoreListener = dbProps.getProperty("BackupRestoreListener");
            this.backupSanityChecker = dbProps.getProperty("BackupRestoreSanityChecker");
            this.backupNonMickeyTables = Boolean.parseBoolean(dbProps.getProperty("backup.nonmickey.tables", "false"));
            final String handlerClass = dbProps.getProperty("PasswordProvider");
            if (handlerClass != null && !handlerClass.isEmpty()) {
                this.passwordProvider = (PasswordProvider)Thread.currentThread().getContextClassLoader().loadClass(handlerClass).newInstance();
            }
            else {
                this.passwordProvider = new BackupPasswordProvider();
            }
            if (this.backupRestoreListener != null && !this.backupRestoreListener.equals("") && AbstractBackupHandler.backupListener == null) {
                try {
                    Messenger.subscribe("BackupStatusTopic", AbstractBackupHandler.backupListener = (MessageListener)Thread.currentThread().getContextClassLoader().loadClass(this.backupRestoreListener).newInstance(), true, (MessageFilter)null);
                }
                catch (final Exception e) {
                    AbstractBackupHandler.LOGGER.log(Level.SEVERE, "Exception occured while subscribing to {0}", "BackupStatusTopic");
                    e.printStackTrace();
                }
            }
            if (this.backupSanityChecker != null && !this.backupSanityChecker.equals("") && AbstractBackupHandler.backupChecker == null) {
                try {
                    AbstractBackupHandler.backupChecker = (BackupRestoreSanityChecker)Thread.currentThread().getContextClassLoader().loadClass(this.backupSanityChecker).newInstance();
                }
                catch (final Exception e) {
                    AbstractBackupHandler.LOGGER.log(Level.SEVERE, "Exception occured while creating SanityChecker :: {0}", this.backupSanityChecker);
                    e.printStackTrace();
                }
            }
        }
        catch (final Exception e2) {
            throw new RuntimeException(e2);
        }
    }
    
    protected void initVersionHandler(final String versionHandler) throws BackupRestoreException {
        AbstractBackupHandler.LOGGER.log(Level.INFO, "BackUp/Restore Version handler :::: {0}", versionHandler);
        if (versionHandler != null) {
            try {
                this.backupRestoreHandler = (VersionHandler)Thread.currentThread().getContextClassLoader().loadClass(versionHandler).newInstance();
            }
            catch (final Exception e) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_INITIALIZING_VERSION_HANDLER, e);
            }
        }
    }
    
    protected abstract void flushBuffers(final Statement p0) throws BackupRestoreException;
    
    protected abstract BackupResult doIncrementalBackup(final BackupDBParams p0) throws BackupRestoreException;
    
    protected abstract BackupResult doFullBackup(final BackupDBParams p0) throws BackupRestoreException;
    
    protected abstract File generateIndexFile(final BackupDBParams p0, final BackupStatus p1) throws BackupRestoreException;
    
    protected Properties generateIndexProperties(final BackupDBParams params, final BackupStatus status) {
        final Properties p = new Properties();
        p.setProperty("zipfilename", status.getZipFileName());
        p.setProperty("backup_type", String.valueOf(status.getBackupType()));
        p.setProperty("backup_tag", CryptoUtil.encrypt(String.valueOf(System.currentTimeMillis())));
        p.setProperty("sizeOfDB", params.databaseSize + " Bytes");
        p.setProperty("osname", OSCheckUtil.getOSName());
        final String oldCryptTag = (PersistenceInitializer.getConfigurationValue("CryptTag") == null) ? "MLITE_ENCRYPT_DECRYPT" : PersistenceInitializer.getConfigurationValue("CryptTag");
        ((Hashtable<String, String>)p).put("oldCryptTag", oldCryptTag);
        if (params.backupMode == BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP) {
            p.setProperty("fullbackup_zipname", (params.fullbackup_zipname == null) ? "" : params.fullbackup_zipname);
            p.setProperty("previous_incr_backup_zipnames", (params.previous_incr_backup_zipnames == null) ? "" : params.previous_incr_backup_zipnames);
        }
        return p;
    }
    
    @Override
    public void enableIncrementalBackup() throws BackupRestoreException {
        AbstractBackupHandler.LOGGER.log(Level.FINE, "No specific handling required for enabling Incremental Backup");
    }
    
    @Override
    public void disableIncrementalBackup() throws BackupRestoreException {
        AbstractBackupHandler.LOGGER.log(Level.FINE, "No specific handling required for disabling Incremental Backup");
    }
    
    @Override
    public BackupResult doBackup(final BackupDBParams params) throws BackupRestoreException {
        AbstractBackupHandler.LOGGER.log(Level.INFO, "BackupDBParams for performing backup :: {0}", params.toString());
        if (AbstractBackupHandler.backupDBInProgress) {
            AbstractBackupHandler.LOGGER.log(Level.WARNING, "Already a backup DB process is running.");
            throw new BackupRestoreException(BackupErrors.ALREADY_BACKUP_RUNNING);
        }
        final File backupFile = new File(params.backupFolder + File.separator + params.zipFileName);
        if (backupFile.exists()) {
            AbstractBackupHandler.LOGGER.log(Level.INFO, "Already a file/directory with this name [{0}] is found.", backupFile.getAbsolutePath());
            throw new BackupRestoreException(BackupErrors.BACKUP_FILE_ALREADY_EXISTS);
        }
        this.validateBackupDBParams(params);
        try (final Connection conn = this.dataSource.getConnection()) {
            params.databaseSize = this.dbAdapter.getSizeOfDB(conn);
        }
        catch (final SQLException e) {
            e.printStackTrace();
            params.databaseSize = -1L;
        }
        if (!params.backupFolder.exists() && !params.backupFolder.mkdirs()) {
            throw new BackupRestoreException(BackupErrors.UNABLE_TO_CREATE_BACKUP_DIRECTORY);
        }
        BackupResult backupResult = null;
        long expectedTime = -1L;
        final Row prevBackup = this.getPreviousBackupDetails(params.backupType.getValue());
        if (this.passwordProvider != null) {
            try {
                params.archivePassword = this.passwordProvider.getPassword(params);
            }
            catch (final PasswordException e2) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_PASSWORD, e2);
            }
        }
        if (prevBackup == null || params.databaseSize <= 0L) {
            AbstractBackupHandler.LOGGER.log(Level.INFO, "This is the first backup of this type. So approximate time and space for backup can't be calculated.");
        }
        else {
            final long databaseOldSize = (long)prevBackup.get("DATABASE_SIZE");
            if (databaseOldSize > 0L) {
                final long prevBackupSize = (long)prevBackup.get("BACKUP_ZIPSIZE");
                if (prevBackupSize > 0L) {
                    params.expectedBackupSize = prevBackupSize * params.databaseSize / databaseOldSize;
                    AbstractBackupHandler.LOGGER.log(Level.INFO, "Expected Backup Size :: " + params.expectedBackupSize + " Bytes");
                }
                else {
                    AbstractBackupHandler.LOGGER.log(Level.INFO, "Expected Backup Size cannot be found.");
                }
                final long prevBackupStartTime = (long)prevBackup.get("BACKUP_STARTTIME");
                final long prevBackupEndTime = (long)prevBackup.get("BACKUP_ENDTIME");
                if (prevBackupEndTime > 0L && prevBackupStartTime > 0L) {
                    expectedTime = (prevBackupEndTime - prevBackupStartTime) * params.databaseSize / databaseOldSize;
                    AbstractBackupHandler.LOGGER.log(Level.INFO, "Expected Backup Time :: " + expectedTime + " ms");
                }
                else {
                    AbstractBackupHandler.LOGGER.log(Level.INFO, "Expected backup time cannot be found.");
                }
            }
            else {
                AbstractBackupHandler.LOGGER.log(Level.INFO, "Approximate time and space for backup can't be calculated.");
            }
        }
        boolean enableProgressBar = false;
        ProgressBar.PROGRESS_BAR_TYPE progressBarType = null;
        if (params.backupContentType == BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY) {
            if (BackupDB.BACKUP_DB_USING_SCRIPTS) {
                if (expectedTime > 0L) {
                    progressBarType = ProgressBar.PROGRESS_BAR_TYPE.FLOW_BAR;
                }
                else {
                    progressBarType = ProgressBar.PROGRESS_BAR_TYPE.LOADING_CIRCLE;
                }
                enableProgressBar = true;
            }
            else if (expectedTime > 0L) {
                progressBarType = ProgressBar.PROGRESS_BAR_TYPE.SIMPLE_PERCENTAGE;
                enableProgressBar = true;
            }
        }
        ProgressBar progressBar = null;
        if (enableProgressBar) {
            progressBar = new ProgressBar(expectedTime, progressBarType, "Backing-up ...", BackupDB.BACKUP_DB_USING_SCRIPTS);
            progressBar.startProgressBar();
        }
        try {
            AbstractBackupHandler.backupDBInProgress = true;
            if (!this.hasPermissionForBackup()) {
                final BackupStatus backupStatus = getBackupStatus(params);
                backupStatus.setStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
                sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
                AbstractBackupHandler.LOGGER.log(Level.SEVERE, "Not Enough Permission to execute Backup");
                throw new BackupRestoreException(BackupErrors.NOT_ENOUGH_PERMISSION_FOR_BACKUP);
            }
            this.checkForSpace(params);
            if (params.backupContentType == BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP) {
                backupResult = this.doTableBackup(params);
            }
            else {
                backupResult = this.doDBBackup(params);
            }
            if (AbstractBackupHandler.backupChecker != null) {
                try {
                    final boolean backupTestResult = AbstractBackupHandler.backupChecker.checkBackup(backupResult);
                    if (!backupTestResult) {
                        AbstractBackupHandler.LOGGER.log(Level.SEVERE, "Sanity Checker returned Backup Failed");
                        backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
                    }
                }
                catch (final Exception e3) {
                    throw new BackupRestoreException(BackupErrors.SANITY_TEST_FAILED, e3);
                }
            }
        }
        finally {
            if (enableProgressBar) {
                try {
                    progressBar.endProgressBar();
                }
                catch (final InterruptedException e4) {
                    throw new BackupRestoreException(BackupErrors.PROGRESS_BAR_INTERRUPTED, e4);
                }
            }
            AbstractBackupHandler.backupDBInProgress = false;
            if (BackupDB.SHOW_STATUS) {
                ConsoleOut.println("\nBackup OS         : " + OSCheckUtil.getOSName());
                try {
                    final byte arch = this.dbInitializer.getDBArchitecture();
                    if (arch > 0) {
                        ConsoleOut.println("Database Arch     : " + arch);
                    }
                    final String version = this.dbInitializer.getVersion();
                    if (version != null && !version.equals("")) {
                        ConsoleOut.println("Database Version  : " + version);
                    }
                }
                catch (final Exception e5) {
                    e5.printStackTrace();
                }
                ConsoleOut.println("");
            }
        }
        AbstractBackupHandler.LOGGER.log(Level.INFO, "backupResult :: {0}", backupResult.toString());
        return backupResult;
    }
    
    protected void validateBackupDBParams(final BackupDBParams params) throws BackupRestoreException {
        if (params.backupFolder == null) {
            throw new BackupRestoreException(BackupErrors.BACKUP_DIRECTORY_NOT_SPECIFIED);
        }
        if (params.zipFileName == null) {
            throw new BackupRestoreException(BackupErrors.ZIPFILE_NAME_NOT_SPECIFIED);
        }
        if (params.backupContentType == null) {
            throw new BackupRestoreException(BackupErrors.BACKUP_CONTENT_TYPE_NOT_SPECIFIED);
        }
        if (params.backupType == null) {
            params.backupType = BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP;
        }
    }
    
    private Row getPreviousBackupDetails(final int type) {
        Criteria c = new Criteria(Column.getColumn("BackupDetails", "BACKUP_TYPE"), type, 0);
        c = c.and(Column.getColumn("BackupDetails", "BACKUP_STATUS"), BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED.getValue(), 0);
        SelectQuery sq = new SelectQueryImpl(Table.getTable("BackupDetails"));
        sq.addSelectColumn(Column.getColumn("BackupDetails", "BACKUP_ID").maximum());
        sq.setCriteria(c);
        c = new Criteria(new Column("BackupDetails", "BACKUP_ID"), new DerivedColumn("BACKUP_ID", sq), 0);
        sq = new SelectQueryImpl(Table.getTable("BackupDetails"));
        sq.addSelectColumn(Column.getColumn("BackupDetails", "*"));
        sq.setCriteria(c);
        try {
            final DataObject dob = DataAccess.get(sq);
            if (!dob.isEmpty()) {
                return dob.getRow("BackupDetails");
            }
        }
        catch (final DataAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    protected BackupResult doDBBackup(final BackupDBParams params) throws BackupRestoreException {
        AbstractBackupHandler.LOGGER.log(Level.INFO, params.toString());
        if (params.backupType == BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP) {
            return this.doFullBackup(params);
        }
        return this.doIncrementalBackup(params);
    }
    
    @Deprecated
    protected abstract BackupResult doTableBackup(final BackupDBParams p0) throws BackupRestoreException;
    
    protected static void sendBackupStatusNotification(final BackupStatus backupStatus, final BackupRestoreConfigurations.BACKUP_STATUS status) throws BackupRestoreException {
        backupStatus.setStatus(status);
        AbstractBackupHandler.LOGGER.log(Level.INFO, "backupStatus :: {0}", backupStatus.toString());
        try {
            Messenger.publish("BackupStatusTopic", (Object)backupStatus);
        }
        catch (final Exception e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_PUBLISHING_BACKUP_STATUS, e);
        }
    }
    
    protected static BackupStatus getBackupStatus(final BackupDBParams params) {
        final BackupStatus status = new BackupStatus();
        status.setBackupFolder(params.backupFolder);
        status.setBackupID(params.backupID);
        status.setBackupStartTime(params.backupStartTime);
        status.setBackupType(params.backupType);
        status.setZipFileName(params.zipFileName);
        status.setBackupEndTime(params.backupEndTime);
        return status;
    }
    
    private Map<String, List<File>> getConfFiles() throws DataAccessException, MalformedURLException, SAXException, IOException, InterruptedException {
        final Map<String, List<File>> ddMap = new HashMap<String, List<File>>();
        final DataObject moduleDO = DataAccess.get("Module", (Criteria)null);
        AbstractBackupHandler.LOGGER.log(Level.INFO, "moduleDO :: {0}", moduleDO);
        final Iterator<?> iterator = moduleDO.getRows("Module");
        Row moduleRow = null;
        final String confHome = Configuration.getString("server.home") + File.separator + "conf" + File.separator;
        this.addFile(ddMap, "conf", new File(confHome, "module.xml"));
        final List<String> includeFileList = new ArrayList<String>();
        includeFileList.add("com/adventnet/db/persistence/metadata/conf/data-dictionary.xml");
        this.unZip(new File(System.getProperty("server.home") + "/lib/AdvPersistence.jar"), new File("tmp"), includeFileList, null);
        this.addFile(ddMap, "conf/Persistence", new File("tmp/com/adventnet/db/persistence/metadata/conf/data-dictionary.xml"));
        while (iterator.hasNext()) {
            moduleRow = (Row)iterator.next();
            AbstractBackupHandler.LOGGER.log(Level.INFO, "Processing the moduleRow :: {0}", moduleRow);
            final String moduleName = (String)moduleRow.get(3);
            String key = "conf/" + moduleName;
            File ddFile = new File(confHome, moduleName + File.separator + "dd-files.xml");
            AbstractBackupHandler.LOGGER.log(Level.INFO, "dd File :: [{0}]", ddFile);
            if (ddFile.exists()) {
                File sourceFile = ddFile;
                this.addFile(ddMap, key, sourceFile);
                final DataObject ddFilesDO = Xml2DoConverter.transform(ddFile.toURI().toURL());
                AbstractBackupHandler.LOGGER.log(Level.INFO, "DD File Entries for module [{0}] :: {1}", new Object[] { moduleName, ddFilesDO });
                final Iterator<?> ddFileIterator = ddFilesDO.getRows("ConfFile", (Criteria)null);
                while (ddFileIterator.hasNext()) {
                    final Row confFileRow = (Row)ddFileIterator.next();
                    final String url = (String)confFileRow.get(2);
                    sourceFile = new File(confHome + moduleName + File.separator + url);
                    key = "conf/" + moduleName + "/" + url;
                    key = key.substring(0, key.lastIndexOf("/"));
                    this.addFile(ddMap, key, sourceFile);
                }
            }
            else {
                ddFile = new File(confHome + moduleName + File.separator + "data-dictionary.xml");
                AbstractBackupHandler.LOGGER.log(Level.INFO, "data-dictionary File :: [{0}]", ddFile);
                if (ddFile.exists()) {
                    this.addFile(ddMap, key, ddFile);
                }
                else {
                    AbstractBackupHandler.LOGGER.log(Level.INFO, "Neither dd-files.xml nor data-dictionary.xml is present in the module :: [{0}], hence ignoring", moduleName);
                }
            }
            final File customAttributeFile = new File(confHome + moduleName + File.separator + "extended_dd.attr");
            if (customAttributeFile.exists()) {
                this.addFile(ddMap, key, customAttributeFile);
            }
        }
        final File dynamicFile = new File(confHome + "CustomAttr" + File.separator + "dynamic.atr");
        if (dynamicFile.exists()) {
            this.addFile(ddMap, "conf" + File.separator + "CustomAttr", dynamicFile);
        }
        AbstractBackupHandler.LOGGER.log(Level.INFO, "Finished copyDDFiles ... :: {0}", ddMap);
        return ddMap;
    }
    
    protected String getConfFilesLocation(final File backupFolder) throws BackupRestoreException {
        try {
            this.copyConfFiles(backupFolder);
            this.copyUDTFiles(backupFolder);
            return new File(backupFolder + File.separator + "conf").getCanonicalPath();
        }
        catch (final Exception e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_COPYING_CONF_FILES, e);
        }
    }
    
    protected void copyConfFiles(final File backupFolder) throws IOException, DataAccessException, SAXException, InterruptedException {
        final Map<String, List<File>> confFiles = this.getConfFiles();
        for (final Map.Entry<String, List<File>> me : confFiles.entrySet()) {
            final String key = me.getKey();
            final List<File> value = me.getValue();
            final File tempFile = new File(backupFolder, key);
            for (final File file : value) {
                FileUtils.copyFile(file, new File(tempFile, file.getName()));
            }
        }
        FileUtils.deleteDir("tmp");
    }
    
    protected void copyUDTFiles(final File backupFolder) throws IOException {
        final File udtDir = new File(Configuration.getString("server.home") + File.separator + "conf" + File.separator + "udt");
        final File backupUdtDir = new File(backupFolder + File.separator + "conf" + File.separator + "udt");
        if (!backupUdtDir.exists()) {
            backupUdtDir.mkdirs();
        }
        final File[] listOfFiles = udtDir.listFiles();
        if (listOfFiles != null && listOfFiles.length != 0) {
            for (final File udtFile : listOfFiles) {
                FileUtils.copyFile(udtFile, new File(backupUdtDir, udtFile.getName()));
            }
        }
    }
    
    private void addFile(final Map<String, List<File>> fileMap, final String key, final File file) {
        List<File> ddFiles = fileMap.get(key);
        if (ddFiles == null) {
            ddFiles = new ArrayList<File>();
            fileMap.put(key, ddFiles);
        }
        ddFiles.add(file);
    }
    
    @Override
    public void doCleanup(final List<String> filesToBeDeleted) {
        FileUtils.deleteFiles((List)filesToBeDeleted);
    }
    
    @Override
    public BackupResult doFileBackup(String backupDir, String backupFileName, final List<String> directoriesToBeArchived, final String versionHandlerName, final Properties prefProps) throws BackupRestoreException {
        final BackupResult backupResult = new BackupResult(backupFileName, backupDir);
        backupResult.setBackupMode(BackupRestoreConfigurations.BACKUP_MODE.FILE_BACKUP);
        try {
            if (AbstractBackupHandler.backupDBInProgress) {
                AbstractBackupHandler.LOGGER.log(Level.WARNING, "Already a backup DB process is running.");
                throw new BackupRestoreException(BackupErrors.ALREADY_BACKUP_RUNNING);
            }
            AbstractBackupHandler.backupDBInProgress = true;
            this.initVersionHandler(versionHandlerName);
            backupFileName = this.getBackUpFileName(backupFileName);
            backupResult.setBackupFile(backupFileName);
            final File backUpDirPath = new File((backupDir == null) ? "../Backup/" : backupDir);
            if (!backUpDirPath.exists()) {
                backUpDirPath.mkdir();
            }
            backupDir = backUpDirPath.getCanonicalPath();
            backupResult.setBackupFolder(backupDir);
            AbstractBackupHandler.LOGGER.log(Level.INFO, "BackUp file directory ::: {0}", backupDir);
            final String zipFileName = backupDir + File.separator + backupFileName;
            AbstractBackupHandler.LOGGER.log(Level.INFO, "Zip file path ::: {0}", zipFileName);
            backupResult.setStartTime(System.currentTimeMillis());
            ZipUtils.zip(zipFileName, (List)directoriesToBeArchived, true, prefProps);
            backupResult.setEndTime(System.currentTimeMillis());
            backupResult.calculateDuration();
            backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
            return backupResult;
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            return backupResult;
        }
        finally {
            AbstractBackupHandler.backupDBInProgress = false;
        }
    }
    
    @Override
    public boolean abortBackup() throws BackupRestoreException {
        if (!AbstractBackupHandler.backupDBInProgress) {
            return false;
        }
        AbstractBackupHandler.backupDBInProgress = false;
        return true;
    }
    
    protected String getBackUpFileName(String backupFile) {
        if (backupFile == null) {
            final Date today = Calendar.getInstance().getTime();
            final SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmm");
            backupFile = formatter.format(today);
        }
        return backupFile;
    }
    
    protected void addSpecXMLEntryToProps(final Properties props) throws IOException {
        final FileInputStream fis = null;
        try {
            final File specsFile = new File(Configuration.getString("server.home") + "/Patch/specs.xml");
            if (specsFile.exists()) {
                props.setProperty("AllVersions", this.getAllVersions(specsFile));
            }
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    protected String getAllVersions(final File specsFile) throws IOException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(specsFile);
            final byte[] b = new byte[4096];
            fis.read(b);
            final String strFromSpecsFile = new String(b);
            final String versionString = strFromSpecsFile.substring(strFromSpecsFile.indexOf(" AllVersions=") + 14, strFromSpecsFile.indexOf(" Versions=") - 1);
            return versionString;
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    protected List<String> getTableNamesForBackUp() throws BackupRestoreException {
        if (this.backupNonMickeyTables) {
            AbstractBackupHandler.LOGGER.log(Level.INFO, "Backup Non Mickey Tables");
            return this.getTablesFromDatabaseMetaData();
        }
        AbstractBackupHandler.LOGGER.log(Level.INFO, "Backup Mickey Tables");
        return this.getTablesFromMickeyMetaData();
    }
    
    private List<String> getTablesFromMickeyMetaData() throws BackupRestoreException {
        final List<String> tableList = new ArrayList<String>();
        try {
            for (final String tableName : MetaDataUtil.getTableNamesInDefinedOrder()) {
                final TableDefinition tDef = MetaDataUtil.getTableDefinitionByName(tableName);
                if (!tDef.isTemplate()) {
                    if (tDef.creatable()) {
                        if (!tableList.contains(tDef.getTableName())) {
                            tableList.add(tDef.getTableName());
                            continue;
                        }
                        continue;
                    }
                    else {
                        try {
                            try (final Connection c = this.dataSource.getConnection()) {
                                if (this.dbAdapter.isTablePresentInDB(c, null, tableName) && !tableList.contains(tDef.getTableName())) {
                                    tableList.add(tDef.getTableName());
                                }
                            }
                            continue;
                        }
                        catch (final SQLException e) {
                            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_TABLE_NAMES, e);
                        }
                    }
                }
                final TemplateMetaHandler templateHandler = MetaDataUtil.getTemplateHandler(tDef.getModuleName());
                final List<String> templateInstances = templateHandler.getTemplateInstancesForBackUp(tDef.getTableName());
                if (templateInstances != null) {
                    for (int k = 0; k < templateInstances.size(); ++k) {
                        if (!tableList.contains(templateInstances.get(k))) {
                            tableList.add(templateInstances.get(k));
                        }
                    }
                }
            }
            tableList.addAll(TableArchiverUtil.getTableNamesForBackup());
        }
        catch (final MetaDataException e2) {
            e2.printStackTrace();
        }
        return tableList;
    }
    
    private List<String> getTablesFromDatabaseMetaData() throws BackupRestoreException {
        final List<String> tableList = new ArrayList<String>();
        Connection c = null;
        ResultSet rs = null;
        try {
            c = this.dataSource.getConnection();
            final DatabaseMetaData md = c.getMetaData();
            final String[] types = { "TABLE" };
            rs = md.getTables(null, null, "%", types);
            while (rs.next()) {
                tableList.add(rs.getString("TABLE_NAME"));
            }
        }
        catch (final SQLException e) {
            e.printStackTrace();
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
            if (c != null) {
                try {
                    c.close();
                }
                catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
            if (c != null) {
                try {
                    c.close();
                }
                catch (final SQLException e2) {
                    e2.printStackTrace();
                }
            }
        }
        try {
            final HierarchyProcessor hp = new HierarchyProcessor(tableList, this.dataSource, this.dbAdapter);
            final int levels = hp.getTotalLevels();
            final List<String> newTableList = new ArrayList<String>();
            for (int i = 1; i <= levels; ++i) {
                AbstractBackupHandler.LOGGER.log(Level.INFO, "Adding Tables to List :: Level :: {0}", i);
                newTableList.addAll(hp.getLevel(i));
            }
            return newTableList;
        }
        catch (final Exception e3) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_TABLE_NAMES, e3);
        }
    }
    
    @Override
    public void cleanBackupConfigFiles() throws BackupRestoreException {
        AbstractBackupHandler.LOGGER.log(Level.FINE, "No clean up required");
    }
    
    protected boolean hasPermissionForBackup() {
        boolean backupPermission = true;
        try {
            if (!this.dbAdapter.isBundledDB()) {
                try (final Connection conn = this.dataSource.getConnection()) {
                    backupPermission = this.dbAdapter.hasPermissionForBackup(conn);
                }
            }
        }
        catch (final Exception e) {
            AbstractBackupHandler.LOGGER.log(Level.SEVERE, "Could not check for backup permission. Assuming there is permission for backup.");
            e.printStackTrace();
        }
        AbstractBackupHandler.LOGGER.log(Level.INFO, "Has permission for backup : " + backupPermission);
        return backupPermission;
    }
    
    protected void checkForSpace(final BackupDBParams params) throws BackupRestoreException {
        if (!params.backupFolder.exists()) {
            AbstractBackupHandler.LOGGER.log(Level.SEVERE, "Backup Folder :: [{0}] does not exists.", params.backupFolder);
            throw new BackupRestoreException(BackupErrors.BACKUP_DIRECTORY_DOES_NOT_EXIST);
        }
        final long freeSpace = params.backupFolder.getFreeSpace();
        if (params.expectedBackupSize > 0L && freeSpace > 0L) {
            if (params.expectedBackupSize * 2L >= freeSpace) {
                AbstractBackupHandler.LOGGER.log(Level.SEVERE, "Free space in the location where the backup needs to be saved is " + freeSpace + ". The free space required is around " + 2L * params.expectedBackupSize + " since the expected backup size is " + params.expectedBackupSize);
                throw new BackupRestoreException(BackupErrors.INSUFFICIENT_STORAGE_SPACE);
            }
            AbstractBackupHandler.LOGGER.log(Level.INFO, "Assuming that there is enough space to store the backup file");
        }
        else {
            AbstractBackupHandler.LOGGER.log(Level.WARNING, "The space check cannot be performed.");
        }
    }
    
    @Override
    public boolean isIncrementalBackupValid() throws BackupRestoreException {
        return this.dbAdapter.isBundledDB();
    }
    
    protected void executeCommand(final List<String> cmds, final Properties envProps, final String errorMsgToIgnore) throws BackupRestoreException {
        BackupRestoreUtil.executeCommand(cmds, envProps, errorMsgToIgnore);
    }
    
    protected void zip(final File zipFolder, final String zipFileName, final File contentDirectory, final boolean includeContentDirectoryToo, final boolean includeFilesInContentDirectory, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        try {
            final int exitValue = SevenZipUtils.zip(zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFilesInContentDirectory, (List)includeFileList, (List)excludeFileList, archivePassword, encAlgo);
            if (exitValue != 0) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING);
            }
        }
        catch (final IOException | InterruptedException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING, e);
        }
    }
    
    protected void appendInZip(final String zipFilePath, final List<String> includeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        try {
            final int exitValue = SevenZipUtils.appendInZip(zipFilePath, (List)includeFileList, archivePassword, encAlgo);
            if (exitValue != 0) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING);
            }
        }
        catch (final IOException | InterruptedException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING, e);
        }
    }
    
    protected void unZip(final File zipFile, final File destinationFolder, final List<String> includeFileList, final List<String> excludeFileList) throws IOException, InterruptedException {
        SevenZipUtils.unZip(zipFile, destinationFolder, (List)includeFileList, (List)excludeFileList);
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractBackupHandler.class.getName());
        AbstractBackupHandler.backupDBInProgress = false;
        AbstractBackupHandler.backupChecker = null;
        AbstractBackupHandler.backupListener = null;
    }
}
