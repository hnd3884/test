package com.adventnet.db.adapter;

import com.adventnet.ds.DataSourcePlugIn;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.mfw.ConsoleOut;
import java.util.Map;
import java.util.Arrays;
import com.adventnet.mfw.RestoreDB;
import com.zoho.mickey.exception.PasswordException;
import com.zoho.framework.utils.archive.SevenZipUtils;
import java.sql.Connection;
import com.adventnet.persistence.Row;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.cp.WrappedConnection;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.DataAccess;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.db.persistence.metadata.DataTypeManager;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import com.adventnet.persistence.DataObject;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import java.util.Iterator;
import com.zoho.dddiff.DataDictionaryDiff;
import com.zoho.dddiff.DataDictionaryAggregator;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.persistence.WritableDataObject;
import com.adventnet.persistence.json.Json2DoConverter;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.db.persistence.metadata.ElementTransformer;
import com.zoho.dddiff.ModifiedElement;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.HashMap;
import java.net.URL;
import com.adventnet.persistence.migration.MigrationUtil;
import com.adventnet.db.persistence.metadata.MetaDataAccess;
import com.adventnet.db.persistence.metadata.DataDictionary;
import com.zoho.conf.Configuration;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.List;
import java.util.ArrayList;
import com.zoho.conf.AppResources;
import com.zoho.mickey.crypto.BackupPasswordProvider;
import java.util.logging.Level;
import com.adventnet.mfw.message.MessageFilter;
import com.adventnet.mfw.message.Messenger;
import com.adventnet.mfw.message.MessageListener;
import java.util.Properties;
import java.io.File;
import com.zoho.mickey.crypto.PasswordProvider;
import javax.sql.DataSource;
import com.adventnet.mfw.VersionHandler;
import java.util.logging.Logger;

public abstract class AbstractRestoreHandler implements RestoreHandler
{
    private static final Logger LOGGER;
    public static final String RESTORE_STATUS_TOPIC = "RestoreStatusTopic";
    protected VersionHandler backupRestoreHandler;
    protected String restoreSanityChecker;
    protected String restoreRestoreListener;
    BackupRestoreSanityChecker restoreChecker;
    private static String server_home;
    protected DBAdapter dbAdapter;
    protected SQLGenerator sqlGenerator;
    protected DBInitializer dbInitializer;
    protected DataSource dataSource;
    protected PasswordProvider passwordProvider;
    private WrappedDBAdapter wrappedDbAdapter;
    private File tempBackupDir;
    
    public AbstractRestoreHandler() {
        this.backupRestoreHandler = null;
        this.restoreSanityChecker = null;
        this.restoreRestoreListener = null;
        this.restoreChecker = null;
        this.dbAdapter = null;
        this.sqlGenerator = null;
        this.dbInitializer = null;
        this.dataSource = null;
        this.passwordProvider = null;
        this.wrappedDbAdapter = null;
    }
    
    protected void initDBAdapter(final DBAdapter dbAdapter) {
        if (this.dbAdapter == null) {
            if (dbAdapter instanceof WrappedDBAdapter) {
                this.wrappedDbAdapter = (WrappedDBAdapter)dbAdapter;
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
            this.restoreRestoreListener = dbProps.getProperty("BackupRestoreListener");
            this.restoreSanityChecker = dbProps.getProperty("BackupRestoreSanityChecker");
            if (this.restoreRestoreListener != null && !this.restoreRestoreListener.equals("")) {
                try {
                    Messenger.subscribe("RestoreStatusTopic", (MessageListener)Thread.currentThread().getContextClassLoader().loadClass(this.restoreRestoreListener).newInstance(), true, (MessageFilter)null);
                }
                catch (final Exception e) {
                    AbstractRestoreHandler.LOGGER.log(Level.SEVERE, "Exception occured while subscribing to {0}", "RestoreStatusTopic");
                    e.printStackTrace();
                }
            }
            if (this.restoreSanityChecker != null && !this.restoreSanityChecker.equals("")) {
                try {
                    this.restoreChecker = (BackupRestoreSanityChecker)Thread.currentThread().getContextClassLoader().loadClass(this.restoreSanityChecker).newInstance();
                }
                catch (final Exception e) {
                    AbstractRestoreHandler.LOGGER.log(Level.SEVERE, "Exception occured while creating SanityChecker :: {0}", this.restoreSanityChecker);
                    e.printStackTrace();
                }
            }
            try {
                final String handlerClass = dbProps.getProperty("PasswordProvider");
                if (handlerClass != null && !handlerClass.isEmpty()) {
                    this.passwordProvider = (PasswordProvider)Thread.currentThread().getContextClassLoader().loadClass(handlerClass).newInstance();
                }
                else {
                    this.passwordProvider = new BackupPasswordProvider();
                }
            }
            catch (final InstantiationException | IllegalAccessException | ClassNotFoundException e2) {
                e2.printStackTrace();
                throw e2;
            }
        }
        catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected void initVersionHandler(final String versionHandler) throws BackupRestoreException {
        AbstractRestoreHandler.LOGGER.log(Level.INFO, "BackUp/Restore Version handler :::: {0}", versionHandler);
        if (versionHandler != null) {
            try {
                this.backupRestoreHandler = (VersionHandler)Thread.currentThread().getContextClassLoader().loadClass(versionHandler).newInstance();
            }
            catch (final Exception e) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_INITIALIZING_VERSION_HANDLER, e);
            }
        }
    }
    
    protected boolean isBackupCompatible(final String fullBackupZipFileName, final BackupRestoreConfigurations.RESET_MICKEY resetMickeyType, final String password) throws BackupRestoreException {
        final File tmpDir = new File("tmp");
        if (AppResources.getBoolean("force.restore", Boolean.valueOf(false))) {
            return true;
        }
        try {
            try {
                final List<String> includeFileList = new ArrayList<String>();
                includeFileList.add("conf");
                includeFileList.add("dcinfo.json");
                includeFileList.add("edtinfo.json");
                this.unZip(new File(fullBackupZipFileName), tmpDir, includeFileList, null, password);
            }
            catch (final Exception e) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_UNZIPPING, e);
            }
            if (resetMickeyType == BackupRestoreConfigurations.RESET_MICKEY.RESET) {
                try {
                    PersistenceInitializer.resetMickey(true);
                }
                catch (final Exception e) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_RESETTING_MICKEY, e);
                }
            }
            boolean ddCompatibility = false;
            boolean customAttributeCompatibility = false;
            DataDictionary metaDD = null;
            DataDictionary persDD = null;
            try {
                final File tmpConf = new File(tmpDir, "conf");
                final String backUpConfDir = tmpConf.toString();
                final String existingConfDir = Configuration.getString("server.home") + "/conf";
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "backupConfDir :: [{0}]", backUpConfDir);
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "existingConfDir :: [{0}]", existingConfDir);
                final URL metaDDURL = DataDictionary.class.getResource("conf/meta-dd.xml");
                metaDD = MetaDataAccess.loadDataDictionary(metaDDURL, false);
                final URL persDDURL = DataDictionary.class.getResource("conf/data-dictionary.xml");
                persDD = MetaDataAccess.loadDataDictionary(persDDURL, false);
                final DataDictionaryAggregator oldAggregator = MigrationUtil.getAllDataDictionaries(backUpConfDir);
                final DataDictionaryAggregator newAggregator = MigrationUtil.getAllDataDictionaries(existingConfDir);
                final File persdd = new File(backUpConfDir + "/Persistence/data-dictionary.xml");
                if (persdd.exists()) {
                    oldAggregator.addFile(persdd.toURI().toURL());
                    final String path = "jar:file:" + existingConfDir + "/../lib/AdvPersistence.jar!/com/adventnet/db/persistence/metadata/conf/data-dictionary.xml";
                    final URL existingPersDDurl = new URL(path);
                    newAggregator.addFile(existingPersDDurl);
                }
                final DataDictionaryDiff diff = oldAggregator.diff(newAggregator);
                final List<?> changes = diff.getAllChanges();
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "Changed files :: {0}", changes);
                final List<URL> backUpCustomAttrFiles = this.getCustomAttributeFiles(backUpConfDir);
                final List<URL> existingCustomAttrFiles = this.getCustomAttributeFiles(existingConfDir);
                HashMap<String, String> backUpCustomAttributes = new HashMap<String, String>();
                HashMap<String, String> existingCustomAttributes = new HashMap<String, String>();
                backUpCustomAttributes = MetaDataUtil.getCustomAttributes(backUpCustomAttrFiles, true);
                existingCustomAttributes = MetaDataUtil.getCustomAttributes(existingCustomAttrFiles, true);
                this.loadDynamicProperties(backUpConfDir, backUpCustomAttributes);
                this.loadDynamicProperties(existingConfDir, existingCustomAttributes);
                ddCompatibility = this.areBackUpDDsCompatible(diff);
                if (ddCompatibility) {
                    final List<ModifiedElement> modifiedColumns = diff.getModifiedColumns();
                    for (final ModifiedElement modify : modifiedColumns) {
                        final ColumnDefinition oldCD = ElementTransformer.getColumnDefinition(modify.getOldElement());
                        final ColumnDefinition newCD = ElementTransformer.getColumnDefinition(modify.getNewElement());
                        final String tableName = modify.getTableName();
                        String key = tableName + "." + newCD.getColumnName() + ".maxsize";
                        final String oldExtendedMaxValue = backUpCustomAttributes.get(key);
                        final String newExtendedMaxValue = existingCustomAttributes.get(key);
                        if (oldExtendedMaxValue != null) {
                            oldCD.setMaxLength(Integer.parseInt(oldExtendedMaxValue));
                        }
                        if (newExtendedMaxValue != null) {
                            newCD.setMaxLength(Integer.parseInt(newExtendedMaxValue));
                        }
                        key = tableName + "." + newCD.getColumnName() + ".defaultvalue";
                        final String newExtendedDefValue = existingCustomAttributes.get(key);
                        final String oldExtendedDefValue = backUpCustomAttributes.get(key);
                        if (newExtendedDefValue != null) {
                            newCD.setDefaultValue(newExtendedDefValue);
                        }
                        if (oldExtendedDefValue != null) {
                            oldCD.setDefaultValue(oldExtendedDefValue);
                        }
                        if (!oldCD.equals(newCD)) {
                            ddCompatibility = false;
                            break;
                        }
                    }
                }
                if (!FileUtils.deleteDir(new File(backUpConfDir))) {
                    AbstractRestoreHandler.LOGGER.log(Level.INFO, "tmp files not cleared properly !!!");
                }
                else {
                    AbstractRestoreHandler.LOGGER.log(Level.INFO, "tmp conf files cleared");
                }
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "isBackupCompatible :: data-dictionary :: " + ddCompatibility);
                final List<String> ignorableCustomAttributes = PersistenceInitializer.getConfigurationList("BackupRestore");
                this.refineCustomAttributes(backUpCustomAttributes, ignorableCustomAttributes);
                this.refineCustomAttributes(existingCustomAttributes, ignorableCustomAttributes);
                if (backUpCustomAttributes.equals(existingCustomAttributes)) {
                    customAttributeCompatibility = true;
                }
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "isBackupCompatible :: customAttributes  :: " + customAttributeCompatibility);
            }
            catch (final Exception e2) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_GENERATING_DD_DIFF, e2);
            }
            boolean dcCompatibility = false;
            try {
                if (!AppResources.getBoolean("ignore.dynamic.columns", Boolean.valueOf(false))) {
                    final File oldDCInfo = new File(tmpDir, "dcinfo.json");
                    final DataObject oldDCInfoDO = Json2DoConverter.transform(oldDCInfo.getCanonicalPath(), false);
                    ((WritableDataObject)oldDCInfoDO).clearOperations();
                    final File newDCInfo = new File(Configuration.getString("server.home") + File.separator + "bin" + File.separator + "dcinfo.json");
                    final DataObject newDCInfoDO = Json2DoConverter.transform(newDCInfo.getCanonicalPath(), false);
                    ((WritableDataObject)newDCInfoDO).clearOperations();
                    dcCompatibility = oldDCInfoDO.equals(newDCInfoDO);
                    AbstractRestoreHandler.LOGGER.log(Level.INFO, "isBackupCompatible :: DC :: " + dcCompatibility);
                }
                else {
                    dcCompatibility = true;
                }
            }
            catch (final Exception e3) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_TRANSFORMING_DYNAMIC_COLUMN_INFO, e3);
            }
            try {
                MetaDataUtil.removeDataDictionaryConfiguration(metaDD.getName());
                MetaDataUtil.removeDataDictionaryConfiguration(persDD.getName());
            }
            catch (final MetaDataException e4) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_RESETTING_MICKEY, e4);
            }
            return ddCompatibility && dcCompatibility && customAttributeCompatibility;
        }
        catch (final BackupRestoreException bre) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_CHECKING_COMPATIBILITY, bre);
        }
        finally {
            FileUtils.deleteDir(tmpDir);
            FileUtils.deleteFile(Configuration.getString("server.home") + File.separator + "bin" + File.separator + "edtinfo.json");
            FileUtils.deleteFile(Configuration.getString("server.home") + File.separator + "bin" + File.separator + "dcinfo.json");
        }
    }
    
    private void refineCustomAttributes(final HashMap<String, String> customAttributes, final List<String> ignorableCustomAttributes) {
        final Iterator<String> iter = customAttributes.keySet().iterator();
        String key = null;
        while (iter.hasNext()) {
            key = iter.next();
            if (ignorableCustomAttributes.contains(key.substring(key.lastIndexOf(".") + 1))) {
                iter.remove();
            }
        }
    }
    
    private boolean areBackUpDDsCompatible(final DataDictionaryDiff diff) {
        return diff.getDroppedForeignKeys().isEmpty() && diff.getNewForeignKeys().isEmpty() && diff.getModifiedForeignKeys().isEmpty() && diff.getDroppedUniquesKeys().isEmpty() && diff.getNewUniqueKeys().isEmpty() && diff.getModifiedUniqueKeys().isEmpty() && diff.getDroppedIndexes().isEmpty() && diff.getNewIndexes().isEmpty() && diff.getModifiedIndexes().isEmpty() && diff.getModifiedPrimaryKeys().isEmpty() && diff.getNewColumns().isEmpty() && diff.getDroppedColumns().isEmpty() && diff.getNewTables().isEmpty() && diff.getDroppedTables().isEmpty() && diff.getModifiedDDs().isEmpty() && diff.getModifiedTables().isEmpty();
    }
    
    private List<URL> getCustomAttributeFiles(final String confDir) throws SAXException, IOException, ParserConfigurationException {
        final List<URL> listOfExtendedDDFiles = new ArrayList<URL>();
        final List<String> modules = PersistenceInitializer.getModuleNames();
        for (final String moduleName : modules) {
            final File confFilePath = new File(confDir + File.separator + moduleName + File.separator + "extended_dd.attr");
            if (confFilePath.exists() && confFilePath.isFile()) {
                listOfExtendedDDFiles.add(confFilePath.toURI().toURL());
            }
        }
        return listOfExtendedDDFiles;
    }
    
    private void loadDynamicProperties(final String confDir, final HashMap<String, String> attributes) throws IOException {
        final File dynamicFile = new File(confDir + File.separator + "CustomAttr" + File.separator + "dynamic.atr");
        if (dynamicFile.exists()) {
            final Properties properties = new Properties();
            try (final FileInputStream fis = new FileInputStream(dynamicFile)) {
                properties.load(fis);
            }
            for (final String key : properties.stringPropertyNames()) {
                attributes.put(key, properties.getProperty(key));
            }
        }
    }
    
    protected File touchFile(final String fileName) throws BackupRestoreException {
        try {
            final File file = new File(AbstractRestoreHandler.server_home + File.separator + "bin" + File.separator + fileName);
            file.createNewFile();
            return file;
        }
        catch (final IOException e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_CREATING_TOUCH_FILE, e);
        }
    }
    
    @Override
    public RestoreResult restoreBackup(final String backupZipFileName, final String password) throws BackupRestoreException {
        try {
            RestoreResult restoreResult = null;
            final RestoreDBParams rdbp = this.preRestoreDB(backupZipFileName, password);
            AbstractRestoreHandler.LOGGER.log(Level.INFO, "RestoreDBParams generated by preRestoreDB :: {0}", rdbp.toString());
            RestoreStatus restoreStatus = getRestoreStatus(rdbp);
            this.sendRestoreNotification(restoreStatus);
            File statusFile = this.touchFile("restore_params_generated");
            if (!rdbp.isValid()) {
                restoreResult = new RestoreResult(backupZipFileName);
                restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
                restoreResult.setRestoreStatus(restoreStatus.getStatus());
                this.sendRestoreNotification(restoreStatus);
                return restoreResult;
            }
            try {
                if (this.dbInitializer.isServerStarted() && !this.hasPermissionForRestore()) {
                    final String message = "Not enough permission for restore";
                    AbstractRestoreHandler.LOGGER.log(Level.SEVERE, message);
                    restoreStatus = getRestoreStatus(rdbp);
                    restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
                    this.sendRestoreNotification(restoreStatus);
                    throw new BackupRestoreException(RestoreErrors.NOT_ENOUGH_PERMISSION_FOR_RESTORE);
                }
            }
            catch (final IOException e) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_CHECKING_DB_SERVER_STATUS, e);
            }
            statusFile.delete();
            statusFile = this.touchFile("restore_valid");
            try {
                if (rdbp.getInitializePersistenceType() == BackupRestoreConfigurations.INIT_PERSISTENCE.BEFORE_RESTORE) {
                    try {
                        PersistenceInitializer.checkAndPrepareDatabase(this.dbAdapter, this.dataSource);
                    }
                    catch (final Exception e2) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_PREPARING_DB_SERVER, e2);
                    }
                }
                statusFile.delete();
                statusFile = this.touchFile("restore_started");
                if (rdbp.getRestoreBackupContentType() == BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY) {
                    final ProgressBar progressBar = new ProgressBar(0L, ProgressBar.PROGRESS_BAR_TYPE.LOADING_CIRCLE, "Restoring ...", false);
                    progressBar.startProgressBar();
                    restoreResult = this.restoreDBBackup(rdbp);
                    try {
                        progressBar.endProgressBar();
                    }
                    catch (final InterruptedException e3) {
                        throw new BackupRestoreException(RestoreErrors.PROGRESS_BAR_INTERRUPTED, e3);
                    }
                }
                else if (rdbp.getRestoreBackupContentType() == BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP) {
                    restoreResult = this.restoreTableBackup(rdbp);
                }
                else {
                    if (rdbp.getRestoreBackupMode() != BackupRestoreConfigurations.BACKUP_MODE.FILE_BACKUP) {
                        throw new BackupRestoreException(RestoreErrors.UNSUPPORTED_RESTORE_TYPE);
                    }
                    restoreResult = this.restoreFileBackup(rdbp);
                }
                restoreResult.setOldCryptTag(rdbp.getOldCryptTag());
                statusFile.delete();
                statusFile = this.touchFile("restore_db_completed");
                this.postRestoreDB(rdbp);
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "Resetting password");
                this.resetPassword();
                if (rdbp.getInitializePersistenceType() == BackupRestoreConfigurations.INIT_PERSISTENCE.AFTER_RESTORE) {
                    try {
                        PersistenceInitializer.checkAndPrepareDatabase(this.dbAdapter, this.dataSource);
                    }
                    catch (final Exception e2) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_PREPARING_DB_SERVER, e2);
                    }
                }
                try {
                    Label_0778: {
                        if (rdbp.getRestoreBackupContentType() != BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.BINARY) {
                            if (rdbp.getRestoreBackupContentType() != BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP) {
                                break Label_0778;
                            }
                        }
                        try {
                            DataTypeManager.initialize(null);
                            PersistenceInitializer.initializeMickey(true);
                        }
                        catch (final Exception e2) {
                            throw new BackupRestoreException(RestoreErrors.PROBLEM_INITIALIZING_METADATA, e2);
                        }
                        this.dbAdapter.logDatabaseDetails();
                        AbstractRestoreHandler.LOGGER.log(Level.INFO, "Trying to update restore status in database");
                        final String zipFileName = backupZipFileName.substring(backupZipFileName.lastIndexOf(File.separator) + 1);
                        final Criteria c = new Criteria(Column.getColumn("BackupDetails", "BACKUP_ZIPNAME"), zipFileName, 12);
                        final SelectQuery sq = new SelectQueryImpl(Table.getTable("BackupDetails"));
                        sq.addSelectColumn(Column.getColumn(null, "*"));
                        sq.setCriteria(c);
                        final DataObject backupDO = DataAccess.get(sq);
                        if (!backupDO.isEmpty()) {
                            AbstractRestoreHandler.LOGGER.log(Level.INFO, "Full BackupDetails table entries :: {0}", DataAccess.get("BackupDetails", (Criteria)null));
                            AbstractRestoreHandler.LOGGER.log(Level.INFO, "DO before updating :: {0}", backupDO);
                            final Row row = backupDO.getRow("BackupDetails");
                            row.set("BACKUP_STATUS", BackupRestoreConfigurations.BACKUP_STATUS.RESTORED_BACKUP.getValue());
                            backupDO.updateRow(row);
                            DataAccess.update(backupDO);
                            AbstractRestoreHandler.LOGGER.log(Level.INFO, "DO after updating :: {0}", backupDO);
                            statusFile.delete();
                            statusFile = this.touchFile("restore_status_updated");
                        }
                        else {
                            AbstractRestoreHandler.LOGGER.log(Level.WARNING, "Restoration status not updated in DB. backupDO :: {0}", backupDO);
                        }
                    }
                }
                catch (final DataAccessException dae) {
                    throw new BackupRestoreException(RestoreErrors.PROBLEM_UPDATING_RESTORE_STATUS_IN_DB, dae);
                }
                statusFile.delete();
                boolean restoreTestResult = true;
                restoreTestResult = doRestoreSanityTesting();
                if (!restoreTestResult) {
                    restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
                    this.sendRestoreNotification(restoreStatus);
                    restoreResult.setRestoreStatus(restoreStatus.getStatus());
                }
                if (this.restoreChecker != null) {
                    try {
                        restoreTestResult = this.restoreChecker.checkRestore(restoreResult);
                        if (!restoreTestResult) {
                            AbstractRestoreHandler.LOGGER.log(Level.SEVERE, "Sanity Checker returned Restore Failed");
                            restoreResult.setRestoreStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
                        }
                    }
                    catch (final Exception e4) {
                        throw new BackupRestoreException(RestoreErrors.SANITY_TEST_FAILED, e4);
                    }
                }
                if (restoreResult.getRestoreStatus() == BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED && PersistenceInitializer.getConfigurationValue("EnableMWSR") != null && PersistenceInitializer.getConfigurationValue("EnableMWSR").equals("true")) {
                    try {
                        Connection conn = null;
                        try {
                            conn = this.dataSource.getConnection();
                            final WrappedConnection wrappedConnection = (WrappedConnection)conn;
                            for (int i = 1; i < PersistenceInitializer.getDatabases().size(); ++i) {
                                final DBAdapter dbAdapter = this.wrappedDbAdapter.getDBAdapter(i);
                                dbAdapter.dropAllTables(wrappedConnection.getConnection(i), false);
                            }
                        }
                        finally {
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        DBMigrationUtil.dbMigrationForMWSR();
                    }
                    catch (final Exception e4) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_MIGRATING_DB_MWSR, e4);
                    }
                }
            }
            catch (final BackupRestoreException e5) {
                throw e5;
            }
            finally {
                try {
                    if (rdbp.isDBStopRequired()) {
                        PersistenceInitializer.stopDB();
                        rdbp.requiresDBStop(false);
                    }
                }
                catch (final Exception e6) {
                    AbstractRestoreHandler.LOGGER.log(Level.INFO, "Problem while Stopping DB.", e6);
                }
            }
            AbstractRestoreHandler.LOGGER.log(Level.INFO, "restoreResult :: {0}", restoreResult.toString());
            return restoreResult;
        }
        catch (final Exception e7) {
            e7.printStackTrace();
            throw e7;
        }
        finally {
            if (this.tempBackupDir != null) {
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "Delete [{0}] :: [{1}]", new Object[] { this.tempBackupDir, FileUtils.deleteDir(this.tempBackupDir) });
                this.tempBackupDir = null;
            }
        }
    }
    
    protected boolean isBackupEncrypted(final RestoreDBParams rdbp, final String password) throws BackupRestoreException {
        final List<String> files = this.getNecessaryFiles();
        try {
            if (SevenZipUtils.isZipFileEncrypted(rdbp.getSourceFile(), (List)files)) {
                if (SevenZipUtils.canOpen(rdbp.getSourceFile(), (List)files, password)) {
                    return true;
                }
                throw new BackupRestoreException(RestoreErrors.INCORRECT_CONTENTS_OR_PASSWORD_IN_ZIP);
            }
        }
        catch (final IOException | InterruptedException e) {
            AbstractRestoreHandler.LOGGER.log(Level.WARNING, e.getMessage(), e);
        }
        return false;
    }
    
    protected abstract RestoreResult restoreDBBackup(final RestoreDBParams p0) throws BackupRestoreException;
    
    @Deprecated
    protected abstract RestoreResult restoreTableBackup(final RestoreDBParams p0) throws BackupRestoreException;
    
    protected RestoreDBParams preRestoreDB(final String backupZipName, String password) throws BackupRestoreException {
        final RestoreDBParams rdbp = new RestoreDBParams(backupZipName);
        if (this.isBackupEncrypted(rdbp, password)) {
            if (password == null) {
                throw new BackupRestoreException(RestoreErrors.PASSWORD_REQUIRED_FOR_ENCRYPTED_ZIP);
            }
            rdbp.setArchivePassword(password);
        }
        if (this.passwordProvider != null) {
            try {
                password = this.passwordProvider.getPassword(rdbp);
                rdbp.setArchivePassword(password);
            }
            catch (final PasswordException e) {
                e.printStackTrace();
                throw new BackupRestoreException(RestoreErrors.PASSWORD_REQUIRED_FOR_ENCRYPTED_ZIP);
            }
        }
        this.extractNecessaryFiles(backupZipName, rdbp.getArchivePassword());
        if (!RestoreDB.RESTORING_DB_USING_SCRIPTS) {
            AbstractRestoreHandler.LOGGER.log(Level.WARNING, "Restore db can be called via RestoreDB.restoreDB only.");
            rdbp.setValid(false);
        }
        if (!new File(this.getTempBackupDirectory(), "backuprestore.conf").exists()) {
            rdbp.setRestoreBackupMode(BackupRestoreConfigurations.BACKUP_MODE.FILE_BACKUP);
        }
        return rdbp;
    }
    
    protected File getTempBackupDirectory() {
        return this.tempBackupDir;
    }
    
    protected void extractNecessaryFiles(final String backupZipName, final String archivePassword) throws BackupRestoreException {
        this.tempBackupDir = new File(backupZipName.substring(0, backupZipName.lastIndexOf(46)));
        AbstractRestoreHandler.LOGGER.log(Level.INFO, "Deleted directory [{0}] :: {1}", new Object[] { this.tempBackupDir, FileUtils.deleteDir(this.tempBackupDir) });
        AbstractRestoreHandler.LOGGER.log(Level.INFO, "Created temp directory [{0}] :: {1}", new Object[] { this.tempBackupDir, this.tempBackupDir.mkdirs() });
        this.unZip(new File(backupZipName), this.tempBackupDir, this.getNecessaryFiles(), null, archivePassword);
    }
    
    protected List<String> getNecessaryFiles() {
        return Arrays.asList("backuprestore.conf", "version.conf", "full_index.props", "incremental_index.props", "index.props", "postgresql.conf");
    }
    
    protected void postRestoreDB(final RestoreDBParams rdbp) {
    }
    
    protected void setOldCryptTag(final File file, final RestoreDBParams dbParams) throws IOException {
        final Properties props = FileUtils.readPropertyFile(file);
        final String oldCryptTag = props.getProperty("oldCryptTag", "MLITE_ENCRYPT_DECRYPT");
        dbParams.setOldCryptTag(oldCryptTag);
    }
    
    protected RestoreResult restoreFileBackup(final RestoreDBParams rdbp) throws BackupRestoreException {
        final RestoreResult restoreResult = new RestoreResult(rdbp.getSourceFile());
        final RestoreStatus restoreStatus = getRestoreStatus(rdbp);
        restoreResult.setBackupMode(BackupRestoreConfigurations.BACKUP_MODE.FILE_BACKUP);
        try {
            try {
                if (this.dbInitializer.isServerStarted()) {
                    AbstractRestoreHandler.LOGGER.log(Level.SEVERE, "DB Server is already running, hence please stop the Database and then try restoring ...");
                    throw new BackupRestoreException(RestoreErrors.DATABASE_ALREADY_RUNNING);
                }
            }
            catch (final IOException e1) {
                throw new BackupRestoreException(RestoreErrors.PROBLEM_CHECKING_DB_SERVER_STATUS, e1);
            }
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.GOING_TO_UNZIP_BACKUPZIP);
            this.sendRestoreNotification(restoreStatus);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            restoreStatus.setRestoreStartTime(System.currentTimeMillis());
            this.unZip(new File(rdbp.getSourceFile()), new File(System.getProperty("server.home")), null, null, rdbp.getArchivePassword());
            restoreStatus.setRestoreEndTime(System.currentTimeMillis());
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.UNZIP_BACKUPZIP_COMPLETED);
            this.sendRestoreNotification(restoreStatus);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED);
            this.sendRestoreNotification(restoreStatus);
            restoreResult.setStartTime(restoreStatus.getRestoreStartTime());
            restoreResult.setEndTime(restoreStatus.getRestoreEndTime());
            restoreResult.calculateDuration();
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
        }
        catch (final BackupRestoreException e2) {
            restoreStatus.setStatus(BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED);
            this.sendRestoreNotification(restoreStatus);
            restoreResult.setRestoreStatus(restoreStatus.getStatus());
            throw e2;
        }
        return restoreResult;
    }
    
    protected boolean isCompatibleVersion(final String zipFilePath, final Properties backupRestoreProps, final String archivePassword) {
        boolean isCompatible = true;
        try {
            final File backupRestoreConfFile = new File(this.tempBackupDir, "backuprestore.conf");
            if (backupRestoreConfFile.exists()) {
                backupRestoreProps.putAll(FileUtils.readPropertyFile(backupRestoreConfFile));
            }
            final File versionConfFile = new File(this.tempBackupDir, "version.conf");
            if (versionConfFile.exists()) {
                final Properties props = FileUtils.readPropertyFile(versionConfFile);
                final String handlerClass = props.getProperty("handler");
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "RestoreDB handler class ::: {0}", handlerClass);
                final VersionHandler handler = (VersionHandler)Thread.currentThread().getContextClassLoader().loadClass(handlerClass).newInstance();
                final String version = props.getProperty("version");
                AbstractRestoreHandler.LOGGER.log(Level.INFO, "RestoreDB zip version ::: {0}", version);
                isCompatible = handler.isCompatible(version);
            }
            else {
                AbstractRestoreHandler.LOGGER.info("version.conf file does not exist. Hence compatible check done using spec.xml property.");
                final File specsFile = new File(AbstractRestoreHandler.server_home + "/Patch/specs.xml");
                String allVersionsFromPatchDir = null;
                if (specsFile.exists()) {
                    allVersionsFromPatchDir = this.getAllVersions(specsFile);
                }
                AbstractRestoreHandler.LOGGER.info("Checking compatibility with spec.xml");
                final String allVersionsFromZip = backupRestoreProps.getProperty("AllVersions");
                if ((allVersionsFromZip == null && allVersionsFromPatchDir == null) || ("".equals(allVersionsFromZip) && allVersionsFromPatchDir == null) || (allVersionsFromZip == null && "".equals(allVersionsFromPatchDir)) || (allVersionsFromZip != null && allVersionsFromPatchDir != null && allVersionsFromZip.equals(allVersionsFromPatchDir))) {
                    isCompatible = true;
                }
                else {
                    ConsoleOut.println("This backup file [" + zipFilePath + "] and the current version of the product are not compatible.");
                    ConsoleOut.println("Versions from zip file  ::: " + allVersionsFromZip);
                    ConsoleOut.println("Versions from patch dir ::: " + allVersionsFromPatchDir);
                    isCompatible = false;
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        AbstractRestoreHandler.LOGGER.log(Level.INFO, "isCompatible returns :::: {0}", isCompatible);
        return isCompatible;
    }
    
    protected String getAllVersions(final File specsFile) throws Exception {
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
    
    public static boolean doRestoreSanityTesting() {
        final Map<Integer, String> restoreStatus = new HashMap<Integer, String>();
        restoreStatus.put(1, "restore_params_generated");
        restoreStatus.put(2, "restore_valid");
        restoreStatus.put(3, "restore_started");
        restoreStatus.put(4, "restore_db_completed");
        restoreStatus.put(5, "restore_status_updated");
        File checkStatusFile = null;
        int restoreLevel;
        for (restoreLevel = 1; restoreLevel <= 5; ++restoreLevel) {
            checkStatusFile = new File(AbstractRestoreHandler.server_home + File.separator + "bin" + File.separator + restoreStatus.get(restoreLevel));
            if (checkStatusFile != null & checkStatusFile.exists()) {
                break;
            }
        }
        if (restoreLevel == 6) {
            AbstractRestoreHandler.LOGGER.log(Level.INFO, "Restore process fully completed.");
            return true;
        }
        if (restoreLevel >= 4) {
            AbstractRestoreHandler.LOGGER.log(Level.WARNING, "DB Restored Successfully. Restore process not fully completed.");
            return true;
        }
        if (restoreLevel <= 2) {
            AbstractRestoreHandler.LOGGER.log(Level.WARNING, "DB not restored. Old DB is consistent.");
            return false;
        }
        AbstractRestoreHandler.LOGGER.log(Level.SEVERE, "DB corrupted.");
        return false;
    }
    
    protected void sendRestoreNotification(final RestoreStatus restoreStatus) {
        AbstractRestoreHandler.LOGGER.log(Level.INFO, "restoreStatus :: {0}", restoreStatus.toString());
        try {
            Messenger.publish("RestoreStatusTopic", (Object)restoreStatus);
        }
        catch (final Exception e1) {
            e1.printStackTrace();
        }
    }
    
    protected static RestoreStatus getRestoreStatus(final RestoreDBParams params) {
        final RestoreStatus status = new RestoreStatus();
        status.setBackupMode(params.getRestoreBackupMode());
        status.setZipFileName(params.getSourceFile());
        return status;
    }
    
    protected boolean hasPermissionForRestore() {
        boolean restorePermission = true;
        try {
            if (!this.dbAdapter.isBundledDB()) {
                try (final Connection conn = this.dataSource.getConnection()) {
                    restorePermission = this.dbAdapter.hasPermissionForRestore(conn);
                }
            }
        }
        catch (final Exception e) {
            AbstractRestoreHandler.LOGGER.log(Level.SEVERE, "Could not check for the restore permission. Assuming there is permission for restore.");
            e.printStackTrace();
        }
        AbstractRestoreHandler.LOGGER.log(Level.INFO, "Has permission for restoration : " + restorePermission);
        return restorePermission;
    }
    
    protected void resetPassword() throws BackupRestoreException {
    }
    
    protected void resetDataSource() throws BackupRestoreException {
        try {
            final DataSourcePlugIn dsp = PersistenceInitializer.createDataSourcePlugIn(this.dbAdapter.getDBProps());
            final Properties confProps = new Properties();
            confProps.setProperty("StreamingResultSet", "false");
            final RelationalAPI relApi = new RelationalAPI(this.dbAdapter, dsp.getDataSource(), RelationalAPI.getInstance().getArchiveAdapter(), confProps);
            this.dataSource = relApi.getDataSource();
        }
        catch (final Exception e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_RESETTING_DATA_SOURCE, e);
        }
    }
    
    protected void replaceUDTFiles(final String fullBackupZipFileName, final String password) throws BackupRestoreException {
        final File tmpDir = new File("tmp");
        try {
            final List<String> includeFileList = new ArrayList<String>();
            includeFileList.add("conf/udt");
            this.unZip(new File(fullBackupZipFileName), tmpDir, includeFileList, null, password);
            final File udtDir = new File(Configuration.getString("server.home") + File.separator + "conf" + File.separator + "udt");
            if (udtDir.exists()) {
                final File oldUdtDir = new File(Configuration.getString("server.home") + File.separator + "conf" + File.separator + "udt_old");
                if (oldUdtDir.exists()) {
                    AbstractRestoreHandler.LOGGER.log(Level.WARNING, "Deleting old UDT directory :: {0}");
                    FileUtils.deleteDir(oldUdtDir);
                }
                udtDir.renameTo(oldUdtDir);
            }
            else {
                udtDir.mkdirs();
            }
            final File tmpUDTConf = new File(tmpDir + File.separator + "conf" + File.separator + "udt");
            final File[] listOfFiles = tmpUDTConf.listFiles();
            if (listOfFiles != null && listOfFiles.length != 0) {
                for (final File udtFile : listOfFiles) {
                    try {
                        FileUtils.copyFile(udtFile, new File(udtDir, udtFile.getName()));
                    }
                    catch (final IOException e) {
                        throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_COPYING_CONF, e);
                    }
                }
            }
        }
        finally {
            FileUtils.deleteDir(tmpDir);
        }
    }
    
    protected void executeCommand(final List<String> cmds, final Properties envProps, final String errorMsgToIgnore) throws BackupRestoreException {
        BackupRestoreUtil.executeCommand(cmds, envProps, errorMsgToIgnore);
    }
    
    protected void unZip(final File zipFile, final File destinationFolder, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword) throws BackupRestoreException {
        try {
            final int exitValue = SevenZipUtils.unZip(zipFile, destinationFolder, (List)includeFileList, (List)excludeFileList, archivePassword);
            if (exitValue != 0) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING);
            }
        }
        catch (final IOException | InterruptedException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING, e);
        }
    }
    
    protected Properties readProperty(final String zipNameWithFullPath, final String entryNameWithPackage, final String archivePassword) throws BackupRestoreException {
        try {
            return SevenZipUtils.readProperty(zipNameWithFullPath, entryNameWithPackage, archivePassword);
        }
        catch (final IOException | InterruptedException e) {
            throw new BackupRestoreException(RestoreErrors.PROBLEM_WHILE_CHECKING_FILE_IN_ZIP, e);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(AbstractRestoreHandler.class.getName());
        AbstractRestoreHandler.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
    }
}
