package com.adventnet.db.adapter.postgres;

import java.util.Hashtable;
import java.net.Inet6Address;
import java.net.InetAddress;
import com.zoho.mickey.ha.HAUtil;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.util.Collection;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import com.zoho.cp.LogicalConnection;
import com.adventnet.cp.WrappedConnection;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.ds.query.QueryConstructionException;
import com.zoho.framework.utils.OSCheckUtil;
import com.zoho.conf.AppResources;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.persistence.PersistenceUtil;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.adventnet.db.adapter.BackupRestoreUtil;
import com.zoho.framework.utils.FileUtils;
import com.adventnet.mfw.BackupDB;
import java.util.Properties;
import java.util.Iterator;
import com.adventnet.db.adapter.BackupStatus;
import java.util.Date;
import java.util.Collections;
import java.util.Arrays;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.adventnet.db.adapter.BackupResult;
import com.adventnet.db.adapter.BackupDBParams;
import java.sql.Connection;
import com.zoho.conf.Configuration;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.FilenameFilter;
import com.zoho.framework.utils.FileNameFilter;
import java.io.IOException;
import com.adventnet.db.adapter.BackupRestoreException;
import com.adventnet.db.adapter.BackupErrors;
import java.util.logging.Level;
import java.util.Locale;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.List;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.db.adapter.AbstractBackupHandler;

public class PostgresBackupHandler extends AbstractBackupHandler implements MessageListener
{
    protected List<String> sequenceNames;
    private static final Logger LOGGER;
    public static final int OS;
    private static boolean isReplicationEnabled;
    public static final String BACKUP_STATUS_TOPIC = "WALBackupStatusTopic";
    
    public PostgresBackupHandler() {
        this.sequenceNames = new ArrayList<String>();
    }
    
    protected String getStartWALFileName(final String wal_backupstatus_file) throws BackupRestoreException {
        InputStreamReader isr = null;
        final FileInputStream fis = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(new FileInputStream(new File(wal_backupstatus_file)));
            br = new BufferedReader(isr);
            String startFile = null;
            while ((startFile = br.readLine()) != null) {
                if (startFile.toLowerCase(Locale.ENGLISH).startsWith("start wal location")) {
                    startFile = startFile.substring(startFile.indexOf("file") + 5, startFile.length() - 1);
                    PostgresBackupHandler.LOGGER.log(Level.INFO, "startWALFile :: [{0}]", startFile);
                    return startFile;
                }
            }
            return null;
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_DB_LOG_FILE, e);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    protected File getStopFile(final File backupStatusFile, final int waitDuration) throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.FINE, "Entered getStopFile method :: [{0}]", backupStatusFile);
        final String stopWALFileName = this.getStopWALFileName(backupStatusFile);
        final FileNameFilter fileFilter = new FileNameFilter(stopWALFileName, (String)null);
        int count = 0;
        int checkFrequency = 1;
        if (waitDuration > 10) {
            checkFrequency = (int)Math.round(waitDuration / 10.0);
        }
        while (count * checkFrequency < waitDuration) {
            final File[] archivedFiles = this.getDBArchiveFolder().listFiles((FilenameFilter)fileFilter);
            if (archivedFiles != null && archivedFiles.length > 0) {
                PostgresBackupHandler.LOGGER.log(Level.INFO, "backupStatusFile found in {0} seconds", count * checkFrequency);
                PostgresBackupHandler.LOGGER.log(Level.INFO, "Returning getBackupStatusFile :: File :: [{0}]", archivedFiles[0]);
                return archivedFiles[0];
            }
            try {
                Thread.sleep(checkFrequency * 1000);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            ++count;
        }
        PostgresBackupHandler.LOGGER.log(Level.SEVERE, "The stopWALFile is not found in the dbarchive folder after waiting for preconfigured time, hence WAL backup fails");
        throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_DB_LOG_FILE);
    }
    
    protected String getStopWALFileName(final File wal_backupstatus_file) throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.FINE, "getStopWALFileName :: [{0}]", wal_backupstatus_file);
        InputStreamReader isr = null;
        final FileInputStream fis = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(new FileInputStream(wal_backupstatus_file));
            br = new BufferedReader(isr);
            String stopFile = null;
            while ((stopFile = br.readLine()) != null && stopFile.length() > 0) {
                if (stopFile.toLowerCase(Locale.ENGLISH).startsWith("stop wal location")) {
                    stopFile = stopFile.substring(stopFile.indexOf("file") + 5, stopFile.length() - 1);
                    PostgresBackupHandler.LOGGER.log(Level.INFO, "stopWALFile :: [{0}]", stopFile);
                    return stopFile;
                }
            }
            return null;
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_DB_LOG_FILE, e);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
    
    protected long startBackup(final Statement s, final String backupLabel) throws BackupRestoreException {
        try {
            s.execute("SELECT PG_START_BACKUP('" + backupLabel + "', true)");
            return System.currentTimeMillis();
        }
        catch (final SQLException sqle) {
            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Problem while executing start backup command.");
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, sqle);
        }
    }
    
    protected long stopBackup(final Statement s) throws BackupRestoreException {
        try {
            s.execute("SELECT PG_STOP_BACKUP()");
            return System.currentTimeMillis();
        }
        catch (final SQLException sqle) {
            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Problem while executing stop backup command.");
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, sqle);
        }
    }
    
    @Override
    protected void flushBuffers(final Statement s) throws BackupRestoreException {
        try {
            s.execute("CHECKPOINT");
        }
        catch (final SQLException sqle) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FLUSHING_LOGS, sqle);
        }
    }
    
    public void onMessage(final Object msg) {
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Entered into the onMessage of serverStartupNotification");
        final String dbHome = Configuration.getString("db.home");
        final File backupLabelFile = new File(dbHome + File.separator + "data" + File.separator + "backup_label");
        Label_0383: {
            if (backupLabelFile.exists()) {
                if (backupLabelFile.isFile()) {
                    try {
                        final String startWALFileName = this.getStartWALFileName(backupLabelFile.toString());
                        PostgresBackupHandler.LOGGER.log(Level.INFO, "Start WAL File of previous backup :: [{0}]", startWALFileName);
                        final String stopWALFileName = this.getStopWALFileName(backupLabelFile);
                        PostgresBackupHandler.LOGGER.log(Level.INFO, "Stop WAL File of previous backup :: [{0}]", stopWALFileName);
                        if (stopWALFileName == null) {
                            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Previous backup seems to be incomplete, please wait ... ");
                            Connection c = null;
                            Statement s = null;
                            try {
                                c = this.dataSource.getConnection();
                                s = c.createStatement();
                                this.stopBackup(s);
                            }
                            finally {
                                if (s != null) {
                                    try {
                                        s.close();
                                    }
                                    catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (c != null) {
                                    try {
                                        c.close();
                                    }
                                    catch (final Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            PostgresBackupHandler.LOGGER.log(Level.SEVERE, " Now completed.");
                        }
                        else {
                            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Improper cleanup of previous backup ... ");
                            final File renamedBackupFile = new File(backupLabelFile.toString() + System.currentTimeMillis());
                            backupLabelFile.renameTo(renamedBackupFile);
                            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Renamed the backup_log file to [{0}]", renamedBackupFile);
                        }
                        break Label_0383;
                    }
                    catch (final Exception e2) {
                        e2.printStackTrace();
                        PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Exception occurred while trying to complte the incomplete backup :: {0}", e2.getMessage());
                        return;
                    }
                }
                PostgresBackupHandler.LOGGER.log(Level.SEVERE, "A folder with the name [backup_label] exists in [{0}], hence postgresDB backup cannot be initiated.", backupLabelFile.getParent());
                return;
            }
        }
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Returning from PostgresBackupHandler onMessage method");
    }
    
    @Override
    protected BackupResult doIncrementalBackup(final BackupDBParams params) throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Entered doIncrementalBackup :: [{0}]", params);
        BackupResult backupResult = null;
        try {
            backupResult = new BackupResult(params.zipFileName, params.backupFolder.getCanonicalPath());
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_BACKUP_DIRECTORY_PATH, e);
        }
        backupResult.setBackupMode(params.backupMode);
        backupResult.setBackupType(params.backupType);
        backupResult.setBackupContentType(params.backupContentType);
        final BackupStatus backupStatus = AbstractBackupHandler.getBackupStatus(params);
        try {
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED);
            Connection c = null;
            Statement s = null;
            String startWALFileName = null;
            try {
                c = this.dataSource.getConnection();
                s = c.createStatement();
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_PERFORM_PG_START_BACKUP);
                this.startBackup(s, params.zipFileName);
                try {
                    AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.PG_START_BACKUP_COMPLETED);
                    final String backupStatusFileName = Configuration.getString("db.home") + File.separator + "data" + File.separator + "backup_label";
                    PostgresBackupHandler.LOGGER.log(Level.INFO, "Reading the backup_label file :: [{0}] to know the startWALFileName", backupStatusFileName);
                    startWALFileName = this.getStartWALFileName(backupStatusFileName);
                }
                finally {
                    AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_PERFORM_PG_STOP_BACKUP);
                    this.stopBackup(s);
                    AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.PG_STOP_BACKUP_COMPLETED);
                }
            }
            catch (final SQLException e2) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_CONNECTION, e2);
            }
            finally {
                if (s != null) {
                    try {
                        s.close();
                    }
                    catch (final Exception e3) {
                        e3.printStackTrace();
                    }
                }
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (final Exception e3) {
                        e3.printStackTrace();
                    }
                }
            }
            File stopWALFile = null;
            File backupStatusFile = null;
            try {
                backupStatusFile = this.getBackupStatusFile(startWALFileName, params.backupLabelWaitDuration);
                stopWALFile = this.getStopFile(backupStatusFile, params.backupLabelWaitDuration);
            }
            catch (final BackupRestoreException bre) {
                throw bre;
            }
            final File[] filesInArchiveFolder = this.getDBArchiveFolder().listFiles();
            List<File> filesInArchiveDir = new ArrayList<File>();
            if (filesInArchiveFolder != null) {
                filesInArchiveDir = Arrays.asList(filesInArchiveFolder);
            }
            PostgresBackupHandler.LOGGER.log(Level.INFO, "filesInArchiveDir before sorting :: {0}", filesInArchiveDir);
            Collections.sort(filesInArchiveDir);
            PostgresBackupHandler.LOGGER.log(Level.INFO, "filesInArchiveDir after sorting :: {0}", filesInArchiveDir);
            final List<File> archivedFilesForBackUp = new ArrayList<File>();
            final List<String> walFileNames = new ArrayList<String>();
            int walFileCount = 0;
            int i = 0;
            while (i < filesInArchiveDir.size()) {
                final File file = filesInArchiveDir.get(i);
                archivedFilesForBackUp.add(file);
                walFileNames.add(file.getName());
                ++walFileCount;
                if (file.equals(stopWALFile)) {
                    if (i + 1 < filesInArchiveDir.size() && filesInArchiveDir.get(i + 1).getName().contains(stopWALFile.getName() + ".")) {
                        archivedFilesForBackUp.add(filesInArchiveDir.get(i + 1));
                        walFileNames.add(filesInArchiveDir.get(i + 1).getName());
                        ++walFileCount;
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
            PostgresBackupHandler.LOGGER.log(Level.INFO, "archivedFilesForBackup :: {0}", archivedFilesForBackUp);
            PostgresBackupHandler.LOGGER.log(Level.INFO, "walFileNames :: {0}", walFileNames);
            backupStatus.setLastDataFileName(stopWALFile.getName());
            backupStatus.setDataFileCount(walFileCount);
            backupStatus.setFileNames(walFileNames);
            final File indexFile = this.generateIndexFile(params, backupStatus);
            params.lastDataFileName = backupStatus.getLastDataFileName();
            final long sTime = (params.lastIncrementalBackupEndTime != -1L && params.lastIncrementalBackupEndTime < params.prevBackupLastDataFileModifiedTime) ? params.lastIncrementalBackupEndTime : params.prevBackupLastDataFileModifiedTime;
            final long eTime = new File(this.getDBArchiveFolder() + File.separator + "index.props").lastModified();
            PostgresBackupHandler.LOGGER.log(Level.INFO, "sTime :: {0}, eTime :: {1}, sTimeDate :: {2}, eTimeDate :: {3}", new Object[] { sTime, eTime, new Date(sTime), new Date(eTime) });
            final List<String> fileListForCleanup = new ArrayList<String>();
            for (final File file2 : archivedFilesForBackUp) {
                fileListForCleanup.add(file2.getAbsolutePath());
            }
            fileListForCleanup.add(indexFile.getAbsolutePath());
            PostgresBackupHandler.LOGGER.log(Level.INFO, "fileListForCleanup :: {0}", fileListForCleanup);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_CREATE_BACKUPZIP);
            try {
                final List<String> filesToExclude = new ArrayList<String>();
                for (final File archivedFile : filesInArchiveDir) {
                    if (!archivedFilesForBackUp.contains(archivedFile)) {
                        filesToExclude.add(archivedFile.getCanonicalPath());
                    }
                }
                this.zip(params.backupFolder, params.zipFileName, this.getDBArchiveFolder(), false, true, null, filesToExclude, params.archivePassword, params.archiveEncAlgo);
            }
            catch (final Exception e4) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING, e4);
            }
            params.backupSize = new File(params.backupFolder, params.zipFileName).length();
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUPZIP_CREATION_COMPLETED);
            params.backupEndTime = System.currentTimeMillis();
            backupStatus.setBackupStartTime(params.backupStartTime);
            backupStatus.setBackupEndTime(params.backupEndTime);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
            backupResult.setBackupSize(params.backupSize);
            backupResult.setStartTime(params.backupStartTime);
            backupResult.setEndTime(params.backupEndTime);
            backupResult.calculateDuration();
            backupResult.setFilesToBeCleaned(fileListForCleanup);
            backupResult.setBackupStatus(backupStatus.getStatus());
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Exiting doIncrementalBackup with backupResult :: {0}", backupResult);
            return backupResult;
        }
        catch (final BackupRestoreException e5) {
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            throw e5;
        }
    }
    
    @Override
    protected BackupResult doFullBackup(final BackupDBParams params) throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Entered doFullBackup :: [{0}]", params);
        final boolean isBundled = this.dbAdapter.isBundledDB();
        if (!isBundled) {
            PostgresBackupHandler.LOGGER.log(Level.WARNING, "Binary Backup not supported for Installed DB. Instead backing up by Dump (Tables) Method");
            params.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP;
            return this.doTableBackup(params);
        }
        if (!this.isWALBackupEnabledInDB(isBundled)) {
            throw new BackupRestoreException(BackupErrors.DATABASE_BACKUP_MISCONFIGURED);
        }
        BackupResult backupResult = null;
        try {
            backupResult = new BackupResult(params.zipFileName, params.backupFolder.getCanonicalPath());
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_BACKUP_DIRECTORY_PATH, e);
        }
        backupResult.setBackupMode(params.backupMode);
        backupResult.setBackupType(params.backupType);
        backupResult.setBackupContentType(params.backupContentType);
        final BackupStatus backupStatus = AbstractBackupHandler.getBackupStatus(params);
        try {
            final String backupStatusFileName = Configuration.getString("db.home") + File.separator + "data" + File.separator + "backup_label";
            if (new File(backupStatusFileName).exists()) {
                PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Already a backup has been triggered, hence without completing that, another backup is not possible");
                throw new BackupRestoreException(BackupErrors.ALREADY_BACKUP_RUNNING);
            }
            final Properties properties = this.dbAdapter.getDBProps();
            final String username = properties.getProperty("username");
            final String password = properties.getProperty("password", "");
            String url = properties.getProperty("url");
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            final String hostName = ((Hashtable<K, String>)properties).get("Server");
            final Integer port = ((Hashtable<K, Integer>)properties).get("Port");
            final Properties envProp = new Properties();
            envProp.setProperty("PGPASSWORD", password);
            final File tempBackupDir = new File(params.backupFolder + File.separator + params.zipFileName.replace(".ezip", ""));
            final File tempDataDir = new File(tempBackupDir, "data");
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Created directory [{0}] :: {1}", new Object[] { tempBackupDir, tempBackupDir.mkdirs() });
            final List<String> commandList = this.getBackupCommand(tempDataDir, username, port, hostName);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED);
            try {
                if (!BackupDB.BACKUP_DB_USING_SCRIPTS) {
                    FileUtils.deleteDir(Configuration.getString("db.home") + File.separator + "data" + File.separator + "wal_archive");
                }
                this.touchFile("full_backup");
                if (tempDataDir.exists()) {
                    final String[] filesInDataDir = tempDataDir.list();
                    if (filesInDataDir != null && filesInDataDir.length > 0) {
                        FileUtils.deleteDir(tempDataDir);
                    }
                }
                PostgresBackupHandler.LOGGER.log(Level.INFO, "Command to be executed :: [{0}]", commandList);
                this.executeCommand(commandList, envProp, null);
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_CREATE_BACKUPZIP);
                final List<String> excludeWalArchive = new ArrayList<String>();
                excludeWalArchive.add("wal_archive");
                excludeWalArchive.add("full_backup");
                this.zip(params.backupFolder, params.zipFileName, tempDataDir, false, true, null, excludeWalArchive, params.archivePassword, params.archiveEncAlgo);
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUPZIP_CREATION_COMPLETED);
            }
            finally {
                if (new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "full_backup").exists()) {
                    new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "full_backup").delete();
                }
            }
            final File indexFile = this.generateIndexFile(params, backupStatus);
            final File versionFile = new File(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH");
            final List<String> fileList = new ArrayList<String>();
            fileList.add(indexFile.getAbsolutePath());
            fileList.add(versionFile.getAbsolutePath());
            try {
                fileList.add(BackupRestoreUtil.getDynamicColumnsInfoFileLocation(tempBackupDir.getAbsolutePath()));
            }
            catch (final Exception e2) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE, e2);
            }
            fileList.add(this.getConfFilesLocation(tempBackupDir));
            this.appendInZip(params.backupFolder + File.separator + params.zipFileName, fileList, params.archivePassword, params.archiveEncAlgo);
            fileList.remove(versionFile.getAbsolutePath());
            fileList.add(tempBackupDir.getAbsolutePath());
            params.backupSize = new File(params.backupFolder, params.zipFileName).length();
            backupStatus.setBackupEndTime(params.backupEndTime = System.currentTimeMillis());
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
            backupResult.setBackupSize(params.backupSize);
            backupResult.setStartTime(params.backupStartTime);
            backupResult.setEndTime(params.backupEndTime);
            backupResult.calculateDuration();
            backupResult.setFilesToBeCleaned(fileList);
            backupResult.setBackupStatus(backupStatus.getStatus());
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Exiting doFullBackup with backupResult :: {0}", backupResult);
            return backupResult;
        }
        catch (final BackupRestoreException e3) {
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            throw e3;
        }
    }
    
    public void executeBackup(final File dataDirectory, final String username, final String password, final int port, final String hostName) throws BackupRestoreException {
        final Properties envProp = new Properties();
        envProp.setProperty("PGPASSWORD", password);
        if (dataDirectory.exists()) {
            final String[] filesInDataDir = dataDirectory.list();
            if (filesInDataDir != null && filesInDataDir.length > 0) {
                FileUtils.deleteDir(dataDirectory);
            }
        }
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Created directory [{0}] :: {1}", new Object[] { dataDirectory, dataDirectory.mkdirs() });
        final List<String> commandList = this.getBackupCommand(dataDirectory, username, port, hostName);
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Command to be executed :: [{0}]", commandList);
        this.executeCommand(commandList, envProp, null);
    }
    
    private List<String> getBackupCommand(final File dataDirectory, final String username, final int port, final String hostName) throws BackupRestoreException {
        final List<String> commandList = new ArrayList<String>();
        commandList.add(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "pg_basebackup");
        commandList.add("-D");
        commandList.add(dataDirectory.getAbsolutePath());
        commandList.add("-U");
        commandList.add(username);
        commandList.add("-p");
        commandList.add("" + port);
        if (!hostName.equals("localhost")) {
            commandList.add("-h");
            commandList.add(hostName);
        }
        else {
            try {
                final String hostAddress = this.dbInitializer.getHostAddressName(hostName);
                PostgresBackupHandler.LOGGER.log(Level.INFO, "hostAddress of localhost is {0}", hostAddress);
                commandList.add("-h");
                commandList.add(hostAddress);
            }
            catch (final IOException e) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_RESOLVING_HOSTNAME, e);
            }
        }
        commandList.add("-X");
        commandList.add("s");
        commandList.add("-w");
        return commandList;
    }
    
    @Override
    protected File generateIndexFile(final BackupDBParams params, final BackupStatus status) throws BackupRestoreException {
        final Properties properties = this.dbAdapter.getDBProps();
        String password = properties.getProperty("password", "");
        String url = properties.getProperty("url");
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
        final String host = properties.getProperty("Server");
        final Integer port = ((Hashtable<K, Integer>)properties).get("Port");
        final String dbName = properties.getProperty("DBName");
        final String userName = properties.getProperty("username");
        final Properties p = this.generateIndexProperties(params, status);
        p.setProperty("walfile_count", String.valueOf(status.getDataFileCount()));
        p.setProperty("last_walfile_name", (status.getLastDataFileName() == null) ? "" : status.getLastDataFileName());
        if (this.dbAdapter.isBundledDB()) {
            Label_0492: {
                if (new File(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH").exists()) {
                    try {
                        final String arch = new String(Files.readAllBytes(Paths.get(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH", new String[0]))).trim();
                        p.setProperty("arch", arch);
                        break Label_0492;
                    }
                    catch (final IOException e1) {
                        throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_BACKUP_INDEX_PROPS, e1);
                    }
                }
                try {
                    ((PostgresDBAdapter)this.dbAdapter).createVersionFile(port, host, userName, password, dbName);
                    final String arch = new String(Files.readAllBytes(Paths.get(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH", new String[0]))).trim();
                    p.setProperty("arch", arch);
                }
                catch (final IOException e1) {
                    PostgresBackupHandler.LOGGER.log(Level.WARNING, "Cannot create version file at :: {0}", new File(Configuration.getString("db.home") + File.separator + "bin" + File.separator + "PG_ARCH").getAbsolutePath());
                    throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_BACKUP_INDEX_PROPS, e1);
                }
                try {
                    p.setProperty("dbversion", this.dbInitializer.getVersion());
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        try {
            password = PersistenceUtil.getDBPasswordProvider().getEncryptedPassword(password);
        }
        catch (final PersistenceException | PasswordException e2) {
            e2.printStackTrace();
            throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_PASSWORD);
        }
        p.setProperty("uid", password);
        p.setProperty("uname", CryptoUtil.encrypt(database));
        int index = 0;
        final List<String> walFileNames = status.getFileNames();
        if (walFileNames != null) {
            for (final String walFileName : walFileNames) {
                p.setProperty("walfilename." + String.valueOf(index++), walFileName);
            }
        }
        File indexFile = null;
        if (params.backupType == BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP) {
            indexFile = new File(AppResources.getString("db.archive.folder", Configuration.getString("db.home") + File.separator + "data") + File.separator + "index.props");
        }
        else {
            indexFile = new File(AppResources.getString("db.archive.folder", Configuration.getString("db.home") + File.separator + "data" + File.separator + "wal_archive") + File.separator + "index.props");
        }
        try {
            FileUtils.writeToFile(indexFile, p, "backup_index_props");
            PostgresBackupHandler.LOGGER.log(Level.INFO, "index.props file generated for the backup");
        }
        catch (final IOException e3) {
            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Error while writing into property file");
            throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_BACKUP_INDEX_PROPS, e3);
        }
        AbstractBackupHandler.sendBackupStatusNotification(status, BackupRestoreConfigurations.BACKUP_STATUS.INDEXFILE_GENERATED_FOR_BACKUP);
        return indexFile;
    }
    
    public File getDBArchiveFolder() {
        return new File(AppResources.getString("db.archive.folder", Configuration.getString("db.home") + File.separator + "data" + File.separator + "wal_archive"));
    }
    
    protected File getBackupStatusFile(final String startFileName, final int waitDuration) throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.FINE, "Entered getBackupStatusFile :: startFileName :: [{0}]", startFileName);
        final FileNameFilter fileFilter = new FileNameFilter(startFileName, "backup");
        int count = 0;
        int checkFrequency = 1;
        if (waitDuration > 10) {
            checkFrequency = (int)Math.round(waitDuration / 10.0);
        }
        while (count * checkFrequency < waitDuration) {
            final File[] files = this.getDBArchiveFolder().listFiles((FilenameFilter)fileFilter);
            if (files != null && files.length > 0) {
                PostgresBackupHandler.LOGGER.log(Level.INFO, "backupStatusFile found in {0} seconds", count * checkFrequency);
                PostgresBackupHandler.LOGGER.log(Level.INFO, "Returning getBackupStatusFile :: File :: [{0}]", files[0]);
                return files[0];
            }
            try {
                Thread.sleep(checkFrequency * 1000);
            }
            catch (final Exception e) {
                e.printStackTrace();
            }
            ++count;
        }
        throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_BACKUP_STATUS_FILE);
    }
    
    public boolean isWALBackupEnabledInDB(final boolean isBundled) {
        boolean enabled = true;
        Connection con = null;
        try {
            con = this.dataSource.getConnection();
            if (isBundled) {
                final String wal_level = this.dbAdapter.getDBSystemProperty(con, "wal_level");
                final String archive_command = this.dbAdapter.getDBSystemProperty(con, "archive_command").toLowerCase(Locale.ENGLISH);
                final String archive_mode = this.dbAdapter.getDBSystemProperty(con, "archive_mode");
                PostgresBackupHandler.LOGGER.log(Level.INFO, "wal_level :: [{0}]  archive_mode :: [{1}]  archive_command :: [{2}]", new Object[] { wal_level, archive_mode, archive_command });
                if (wal_level.equals("minimal") || archive_mode.equals("off")) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "OnlineBackup schedule will be disabled since the Archiving not enabled during DB startup. To enable change the property [wal_level] to [archive / replica] and the [archive_mode] to [on] in postgresql.conf file");
                    enabled = false;
                }
                if (OSCheckUtil.getOSName().toLowerCase(Locale.ENGLISH).contains("windows")) {
                    if (!archive_command.equals("if exist archive.bat (archive.bat \"%p\" \"%f\")")) {
                        PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Zoho's scheduled online backup solution is not configured hence disabling the scheduled backup task !!! Configured archive_command is :: [" + archive_command + "]");
                        enabled = false;
                    }
                }
                else if (!archive_command.equals("if [ -f \"archive.sh\" ]; then sh archive.sh \"%p\" \"%f\"; else echo \"archive script not found\"; fi")) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Zoho's scheduled online backup solution is not configured hence disabling the scheduled backup task !!! Configured archive_command is :: [" + archive_command + "]");
                    enabled = false;
                }
            }
            else {
                final String wal_level = this.dbAdapter.getDBSystemProperty(con, "wal_level");
                if (wal_level.equals("minimal")) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "OnlineBackup schedule will be disabled since the Archiving not enabled during DB startup. To enable change the property [wal_level] to [archive]");
                    enabled = false;
                }
            }
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }
        catch (final QueryConstructionException e2) {
            e2.printStackTrace();
        }
        finally {
            if (con != null) {
                try {
                    con.close();
                }
                catch (final SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return enabled;
    }
    
    protected File touchFile(final String fileName) throws BackupRestoreException {
        try {
            final File file = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + fileName);
            file.createNewFile();
            return file;
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_CREATING_TOUCH_FILE, e);
        }
    }
    
    @Deprecated
    @Override
    protected BackupResult doTableBackup(final BackupDBParams params) throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Entered doTableBackup :: [{0}]", params);
        BackupResult backupResult = null;
        final BackupStatus backupStatus = AbstractBackupHandler.getBackupStatus(params);
        String backupDir = null;
        try {
            backupDir = params.backupFolder.getCanonicalPath();
            backupResult = new BackupResult(params.zipFileName, params.backupFolder.getCanonicalPath());
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_BACKUP_DIRECTORY_PATH, e);
        }
        backupResult.setBackupMode(params.backupMode);
        backupResult.setBackupContentType(params.backupContentType);
        String backupFile = params.zipFileName;
        if (backupFile == null || backupFile.equals("")) {
            backupFile = this.getBackUpFileName(backupFile);
            backupFile += ".ezip";
        }
        final String backupFileName = backupFile.substring(0, backupFile.lastIndexOf("."));
        final String path = backupDir + File.separator + backupFileName;
        try {
            final String pgDumpPath = Configuration.getString("db.home") + "/bin/pg_dump";
            OutputStreamWriter fWriter = null;
            final boolean isWindows = OSCheckUtil.isWindows(PostgresBackupHandler.OS);
            if (!this.checkBinaryForTableBackup()) {
                PostgresBackupHandler.LOGGER.log(Level.SEVERE, "{0} is not bundled with the database.", pgDumpPath);
                throw new BackupRestoreException(BackupErrors.DUMP_BINARY_NOT_FOUND);
            }
            final File file = new File(path);
            final File zipFile = new File(path + ".ezip");
            if (!zipFile.exists() && !file.exists() && !file.isDirectory()) {
                file.mkdirs();
                if (this.backupRestoreHandler != null) {
                    final String version = this.backupRestoreHandler.getCurrentVersion();
                    if (version == null) {
                        PostgresBackupHandler.LOGGER.severe("Current version cannot be null. Provide proper version.");
                        backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                        return backupResult;
                    }
                    final Properties versionProp = new Properties();
                    ((Hashtable<String, String>)versionProp).put("handler", this.backupRestoreHandler.getClass().getName());
                    ((Hashtable<String, String>)versionProp).put("version", version);
                    try {
                        FileUtils.writeToFile(new File(path + File.separator + "version.conf"), versionProp, "version properties");
                    }
                    catch (final IOException ioe) {
                        throw new BackupRestoreException(BackupErrors.PROBLEM_WRITING_VERSION_PROPERTIES, ioe);
                    }
                }
                Connection conn = null;
                Statement s = null;
                try {
                    conn = this.dataSource.getConnection();
                    s = conn.createStatement();
                    PostgresBackupHandler.LOGGER.log(Level.INFO, "Backup DB started...");
                    if (BackupDB.SHOW_STATUS) {
                        ConsoleOut.println("Backup DB started...");
                    }
                    if (!BackupDB.BACKUP_DB_USING_SCRIPTS) {
                        this.startBackup(s, params.zipFileName);
                    }
                    final List<String> tableList = this.getTableNamesForBackUp();
                    final String[] tableNames = tableList.toArray(new String[0]);
                    final Properties properties = this.dbAdapter.getDBProps();
                    final String username = properties.getProperty("username");
                    final String password = properties.getProperty("password", "");
                    String url = properties.getProperty("url");
                    if (url.contains("?")) {
                        url = url.substring(0, url.indexOf("?"));
                    }
                    final String hostName = ((Hashtable<K, String>)properties).get("Server");
                    final Integer port = ((Hashtable<K, Integer>)properties).get("Port");
                    final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
                    final Properties props = new Properties();
                    int index = 0;
                    int fileSerial = 0;
                    final Properties envProp = new Properties();
                    envProp.setProperty("PGPASSWORD", password);
                    BaseConnection baseConnection = null;
                    if (conn instanceof WrappedConnection) {
                        final LogicalConnection logicalConnection = (LogicalConnection)((WrappedConnection)conn).getConnection(0);
                        baseConnection = (BaseConnection)logicalConnection.getPhysicalConnection();
                    }
                    else {
                        final LogicalConnection logicalConnection = (LogicalConnection)conn;
                        baseConnection = (BaseConnection)logicalConnection.getPhysicalConnection();
                    }
                    final CopyManager copyManager = new CopyManager(baseConnection);
                    List<String> dumpcmd = new ArrayList<String>();
                    List<String> srccmd = null;
                    int flag = 0;
                    dumpcmd.add(pgDumpPath);
                    dumpcmd.add("-U" + username);
                    if (!hostName.equals("localhost")) {
                        dumpcmd.add("-h");
                        dumpcmd.add(hostName);
                    }
                    else {
                        final String hostAddress = this.dbInitializer.getHostAddressName(hostName);
                        PostgresBackupHandler.LOGGER.log(Level.INFO, "hostAddress of localhost is {0}", hostAddress);
                        dumpcmd.add("-h");
                        dumpcmd.add(hostAddress);
                    }
                    dumpcmd.add("--port=" + port);
                    dumpcmd.add("-w");
                    dumpcmd.add("-s");
                    srccmd = new ArrayList<String>(dumpcmd);
                    AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED);
                    backupResult.setStartTime(System.currentTimeMillis());
                    for (final String tableName : tableNames) {
                        if (!PostgresBackupHandler.backupDBInProgress) {
                            if (BackupDB.SHOW_STATUS) {
                                ConsoleOut.println("backupDB is aborted" + path);
                            }
                            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "backupDB is aborted" + path);
                            FileUtils.deleteDir(path);
                            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                            backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                            return backupResult;
                        }
                        if (flag == 1) {
                            dumpcmd = new ArrayList<String>(srccmd);
                            flag = 0;
                        }
                        dumpcmd.add("-t");
                        try {
                            fWriter = new OutputStreamWriter(new FileOutputStream(new File(path + File.separator + tableName + ".txt")), StandardCharsets.UTF_8);
                            final String dbSpecificTableName = this.sqlGenerator.getDBSpecificTableName(tableName);
                            final String quotedTableName = (!isWindows || (isWindows && !((PostgresDBAdapter)this.dbAdapter).getIsAutoQuoteEnabled() && dbSpecificTableName.equals(tableName))) ? dbSpecificTableName : ("\\\"" + dbSpecificTableName + "\\\"");
                            dumpcmd.add(quotedTableName);
                            copyManager.copyOut("COPY " + dbSpecificTableName + " TO STDOUT;", (Writer)fWriter);
                            ((Hashtable<String, String>)props).put("table" + index, dbSpecificTableName);
                            PostgresBackupHandler.LOGGER.log(Level.INFO, "Backup table name :: " + tableName);
                            if (BackupDB.SHOW_STATUS) {
                                ConsoleOut.println("Backup table name :: " + tableName);
                            }
                            ++index;
                        }
                        finally {
                            try {
                                if (fWriter != null) {
                                    fWriter.close();
                                }
                            }
                            catch (final Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (index % 100 == 0 || tableNames.length == index) {
                            this.createSchemaForBackup(dumpcmd, file, database, envProp, fileSerial);
                            ++fileSerial;
                            flag = 1;
                        }
                    }
                    for (int i = 0; i < fileSerial; ++i) {
                        this.sqlFileParser(path, "schema" + i + ".sql", tableNames);
                    }
                    if (this.sequenceNames.size() != 0) {
                        this.backupSequences(pgDumpPath, path, properties, this.sequenceNames);
                    }
                    this.addSpecXMLEntryToProps(props);
                    final String oldCryptTag = (PersistenceInitializer.getConfigurationValue("CryptTag") == null) ? "MLITE_ENCRYPT_DECRYPT" : PersistenceInitializer.getConfigurationValue("CryptTag");
                    ((Hashtable<String, String>)props).put("oldCryptTag", oldCryptTag);
                    FileUtils.writeToFile(new File(path + File.separator + "backuprestore.conf"), props, "Backup Tables");
                }
                catch (final IOException ioe) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Problem while writing spec.xml or backuprestore.conf");
                    throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_BACKUPRESTORE_PROPS, ioe);
                }
                catch (final SQLException e2) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Unable to retrieve tables from DB");
                    if (BackupDB.SHOW_STATUS) {
                        ConsoleOut.println("Unable to retrieve tables from DB");
                    }
                    throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e2);
                }
                finally {
                    if (!BackupDB.BACKUP_DB_USING_SCRIPTS) {
                        this.stopBackup(s);
                    }
                    if (s != null) {
                        try {
                            s.close();
                        }
                        catch (final Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                    if (conn != null) {
                        try {
                            conn.close();
                        }
                        catch (final Exception e3) {
                            e3.printStackTrace();
                        }
                    }
                }
                final List<String> dirs = new ArrayList<String>();
                dirs.add(path);
                try {
                    AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_CREATE_BACKUPZIP);
                    this.zip(params.backupFolder, backupFile, new File(path), false, true, null, null, params.archivePassword, params.archiveEncAlgo);
                    AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUPZIP_CREATION_COMPLETED);
                }
                catch (final Exception e4) {
                    throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_ZIPPING, e4);
                }
                finally {
                    FileUtils.deleteDir(path);
                }
                PostgresBackupHandler.LOGGER.log(Level.INFO, "Backup File {0}", path + ".ezip");
                params.backupSize = new File(params.backupFolder, params.zipFileName).length();
                params.backupEndTime = System.currentTimeMillis();
                backupResult.setBackupSize(params.backupSize);
                backupResult.setEndTime(params.backupEndTime);
                backupResult.calculateDuration();
                backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
                backupStatus.setBackupEndTime(params.backupEndTime);
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
                PostgresBackupHandler.LOGGER.log(Level.INFO, "Exiting doTableBackup with backupResult :: {0}", backupResult);
                return backupResult;
            }
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Already a file/directory with this name [{0}] is found. Take the backup after a minute.", path);
            throw new BackupRestoreException(BackupErrors.BACKUP_FILE_ALREADY_EXISTS);
        }
        catch (final BackupRestoreException e5) {
            FileUtils.deleteDir(path);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            throw e5;
        }
    }
    
    protected void sqlFileParser(final String path, final String fileName, final String[] tableNames) throws BackupRestoreException {
        BufferedReader sqlReader = null;
        BufferedWriter ddlWriter = null;
        BufferedWriter constraintWriter = null;
        int stmtType = 0;
        try {
            final File createTableFile = new File(path + File.separator + "table_create.sql");
            final File constraintsFile = new File(path + File.separator + "table_constrains.sql");
            String str = "";
            sqlReader = new BufferedReader(new FileReader(new File(path + File.separator + fileName)));
            if (createTableFile.exists()) {
                ddlWriter = new BufferedWriter(new FileWriter(createTableFile, true));
            }
            else {
                ddlWriter = new BufferedWriter(new FileWriter(createTableFile));
            }
            if (constraintsFile.exists()) {
                constraintWriter = new BufferedWriter(new FileWriter(constraintsFile, true));
            }
            else {
                constraintWriter = new BufferedWriter(new FileWriter(constraintsFile));
            }
            while ((str = sqlReader.readLine()) != null) {
                if (!str.startsWith("--")) {
                    if (str.startsWith("SET")) {
                        this.writeToStream(ddlWriter, str);
                        this.writeToStream(constraintWriter, str);
                    }
                    else {
                        if (str.startsWith("CREATE TABLE") || str.startsWith("CREATE SEQUENCE")) {
                            this.writeToStream(ddlWriter, str);
                            stmtType = 1;
                            continue;
                        }
                        if (str.startsWith("ALTER TABLE") || str.startsWith("CREATE INDEX")) {
                            this.writeToStream(constraintWriter, str);
                            stmtType = 2;
                            continue;
                        }
                        if (str.startsWith("ALTER SEQUENCE")) {
                            String tableName = str.substring(str.lastIndexOf("OWNED BY") + "OWNED BY".length(), str.lastIndexOf(".")).trim();
                            if (tableName.contains(".")) {
                                tableName = tableName.substring(tableName.indexOf(46) + 1);
                            }
                            final String seqenceName = str.substring(str.indexOf("SEQUENCE") + "SEQUENCE".length(), str.indexOf("OWNED BY")).trim();
                            if (this.contains(tableNames, tableName)) {
                                this.addSequenceName(seqenceName);
                                this.writeToStream(constraintWriter, str);
                                stmtType = 2;
                                continue;
                            }
                        }
                    }
                    if (stmtType == 1) {
                        this.writeToStream(ddlWriter, str);
                    }
                    else {
                        if (stmtType != 2) {
                            continue;
                        }
                        this.writeToStream(constraintWriter, str);
                    }
                }
            }
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_WRITING_SQL_FILE, e);
        }
        finally {
            if (ddlWriter != null) {
                try {
                    ddlWriter.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (constraintWriter != null) {
                try {
                    constraintWriter.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (sqlReader != null) {
                try {
                    sqlReader.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
            new File(path + File.separator + fileName).delete();
        }
    }
    
    protected void createSchemaForBackup(final List<String> dumpcmd, final File file, final String database, final Properties envProp, final int fileSerial) throws BackupRestoreException {
        try {
            dumpcmd.add("-f");
            dumpcmd.add(file.getCanonicalPath() + File.separator + "schema" + fileSerial + ".sql");
            dumpcmd.add(database);
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Command to be executed {0}", dumpcmd);
            this.executeCommand(dumpcmd, envProp, " ignored.");
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Creating SQL files for restore......Tables in range :" + fileSerial * 100 + "-" + (fileSerial + 1) * 100);
        }
        catch (final Exception e) {
            if (BackupDB.SHOW_STATUS) {
                ConsoleOut.println("Unable to create table-schema from DB");
            }
            PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Unable to create table-schema from DB");
            throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e);
        }
    }
    
    protected void writeToStream(final BufferedWriter bwriter, final String str) throws IOException {
        try {
            bwriter.write(str);
            bwriter.newLine();
            bwriter.flush();
        }
        catch (final IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public boolean checkBinaryForTableBackup() {
        String pgdumpPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "pg_dump";
        if (OSCheckUtil.isWindows(PostgresBackupHandler.OS)) {
            pgdumpPath += ".exe";
        }
        return new File(pgdumpPath).exists();
    }
    
    private boolean isArchiveScriptExists() {
        String archiveFileName = "archive";
        if (OSCheckUtil.isWindows(OSCheckUtil.getOS())) {
            archiveFileName += ".bat";
        }
        else {
            archiveFileName += ".sh";
        }
        final File archiveScript = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + archiveFileName);
        if (archiveScript.exists()) {
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Archive Script exists");
            return true;
        }
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Archive Script does not exists");
        return false;
    }
    
    public boolean isValid(final Properties backupProps) throws BackupRestoreException {
        boolean isValid = false;
        boolean isBundled = false;
        boolean isDumpBackup = false;
        isBundled = this.dbAdapter.isBundledDB();
        isDumpBackup = backupProps.getProperty("backup.content.type").equals("dump");
        if (isBundled) {
            isValid = (isDumpBackup || this.isWALBackupEnabledInDB(true));
        }
        else {
            if (!isDumpBackup) {
                PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Postgres Online Backup doesnot support binary backup for installed DB");
                throw new BackupRestoreException(BackupErrors.DATABASE_BACKUP_MISCONFIGURED);
            }
            isValid = (BackupDB.BACKUP_DB_USING_SCRIPTS || this.isWALBackupEnabledInDB(false));
        }
        if (isValid) {
            if (isDumpBackup) {
                if (!this.checkBinaryForTableBackup()) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "pg_dump not found.");
                    throw new BackupRestoreException(BackupErrors.DUMP_BINARY_NOT_FOUND);
                }
            }
            else {
                if (!this.isArchiveScriptExists()) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Archive Script not found at " + Configuration.getString("db.home") + File.separator + "data");
                    throw new BackupRestoreException(BackupErrors.ARCHIVE_SCRIPT_NOT_FOUND);
                }
                if (!this.checkBinaryForFullBackup()) {
                    PostgresBackupHandler.LOGGER.log(Level.SEVERE, "pg_basebackup not found.");
                    throw new BackupRestoreException(BackupErrors.FULL_BINARY_NOT_FOUND);
                }
                try {
                    this.enableReplicationInPostgres();
                }
                catch (final IOException e) {
                    throw new BackupRestoreException(BackupErrors.PROBLEM_ENABLING_REPLICATION, e);
                }
            }
        }
        return isValid;
    }
    
    @Override
    public void enableIncrementalBackup() throws BackupRestoreException {
        if (this.createTouchFile("incremental_backup_disabled", "incremental_backup")) {
            PostgresBackupHandler.LOGGER.log(Level.INFO, "IncrementalBackup enabled successfully");
        }
        else {
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Incremental Backup is already enabled.");
        }
    }
    
    @Override
    public void disableIncrementalBackup() throws BackupRestoreException {
        if (this.createTouchFile("incremental_backup", "incremental_backup_disabled")) {
            PostgresBackupHandler.LOGGER.log(Level.INFO, "IncrementalBackup disabled successfully");
        }
        else {
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Incremental Backup is already disabled.");
        }
    }
    
    @Override
    public void cleanBackupConfigFiles() throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Deleting the touchFiles");
        try {
            if (this.dbAdapter != null && this.dbAdapter.isBundledDB()) {
                File touchFile = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "full_backup");
                this.deleteTouchFile(touchFile);
                touchFile = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "incremental_backup");
                this.deleteTouchFile(touchFile);
                touchFile = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + "incremental_backup_disabled");
                this.deleteTouchFile(touchFile);
                if (!HAUtil.isDataBaseHAEnabled()) {
                    PostgresConfUtil.revokeAccessForHost("127.0.0.1/32", "replication", true);
                    if (OSCheckUtil.getOS() == 1) {
                        PostgresConfUtil.revokeAccessForHost("", "replication", true);
                    }
                    final InetAddress address = InetAddress.getByName("localhost");
                    if (address instanceof Inet6Address) {
                        PostgresConfUtil.revokeAccessForHost("::1/128", "replication", true);
                    }
                }
            }
        }
        catch (final Exception e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_DISABLING_REPLICATION, e);
        }
    }
    
    private boolean createTouchFile(final String fromFileName, final String toFileName) throws BackupRestoreException {
        boolean isCreated = false;
        final File fromFile = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + fromFileName);
        final File toFile = new File(Configuration.getString("db.home") + File.separator + "data" + File.separator + toFileName);
        if (toFile.exists()) {
            isCreated = false;
        }
        if (fromFile.exists()) {
            if (!fromFile.renameTo(toFile)) {
                PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Unable to rename the the file [" + fromFile + "] to [" + toFile + "]");
                throw new BackupRestoreException(BackupErrors.PROBLEM_RENAMING_TOUCH_FILE);
            }
        }
        else {
            try {
                toFile.createNewFile();
            }
            catch (final IOException e) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_CREATING_TOUCH_FILE, e);
            }
            isCreated = true;
        }
        return isCreated;
    }
    
    public boolean checkBinaryForFullBackup() {
        String pgdumpPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "pg_basebackup";
        if (OSCheckUtil.isWindows(PostgresBackupHandler.OS)) {
            pgdumpPath += ".exe";
        }
        return new File(pgdumpPath).exists();
    }
    
    protected void backupSequences(final String pgDumpPath, final String backupPath, final Properties dbProps, final List<String> sequenceNames) throws BackupRestoreException {
        PostgresBackupHandler.LOGGER.log(Level.INFO, "Going to backup the sequences :: {0}", sequenceNames);
        final String username = dbProps.getProperty("username");
        final String password = dbProps.getProperty("password", "");
        String url = dbProps.getProperty("url");
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        final String hostName = ((Hashtable<K, String>)dbProps).get("Server");
        final Integer port = ((Hashtable<K, Integer>)dbProps).get("Port");
        final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
        final Properties envProp = new Properties();
        envProp.setProperty("PGPASSWORD", password);
        final List<String> dumpcmd = new ArrayList<String>();
        dumpcmd.add(pgDumpPath);
        dumpcmd.add("-U" + username);
        if (!hostName.equals("localhost")) {
            dumpcmd.add("-h");
            dumpcmd.add(hostName);
        }
        else {
            String hostAddress = null;
            try {
                hostAddress = this.dbInitializer.getHostAddressName(hostName);
            }
            catch (final IOException e) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_RESOLVING_HOSTNAME, e);
            }
            PostgresBackupHandler.LOGGER.log(Level.INFO, "hostAddress of localhost is {0}", hostAddress);
            dumpcmd.add("-h");
            dumpcmd.add(hostAddress);
        }
        dumpcmd.add("--port=" + port);
        dumpcmd.add("-w");
        dumpcmd.add("-a");
        int fileIndex = 0;
        int seqCount = 1;
        while (seqCount <= sequenceNames.size()) {
            final List<String> seqCommand = new ArrayList<String>();
            seqCommand.addAll(dumpcmd);
            while (seqCount <= sequenceNames.size()) {
                seqCommand.add("-t");
                seqCommand.add(sequenceNames.get(seqCount - 1));
                if (seqCount % 100 == 0) {
                    ++seqCount;
                    break;
                }
                ++seqCount;
            }
            try {
                seqCommand.add("-f");
                seqCommand.add(new File(backupPath).getCanonicalPath() + File.separator + "sequences_" + fileIndex + ".sql");
                seqCommand.add(database);
                PostgresBackupHandler.LOGGER.log(Level.INFO, "Going to execute command {0}", seqCommand);
                this.executeCommand(seqCommand, envProp, " ignored.");
                if (BackupDB.SHOW_STATUS) {
                    ConsoleOut.println("Creating sequence file for restore.... sequences_" + fileIndex + ".sql");
                }
                PostgresBackupHandler.LOGGER.log(Level.INFO, "Creating sequence file for restore.... sequences_" + fileIndex + ".sql");
                ++fileIndex;
            }
            catch (final Exception ex) {
                if (BackupDB.SHOW_STATUS) {
                    ConsoleOut.println("Unable to fetch sequence data from DB");
                }
                PostgresBackupHandler.LOGGER.log(Level.SEVERE, "Unable to fetch sequence data from DB");
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, ex);
            }
        }
    }
    
    protected void enableReplicationInPostgres() throws IOException {
        if (PostgresBackupHandler.isReplicationEnabled) {
            return;
        }
        final Properties props = this.dbAdapter.getDBProps();
        final String username = props.getProperty("username");
        final String authMode = props.getProperty("db.default.authmode", "md5");
        PostgresConfUtil.grantAccessForHost("127.0.0.1/32", "replication", username, authMode, "host", true);
        if (OSCheckUtil.getOS() == 1) {
            PostgresConfUtil.grantAccessForHost("", "replication", username, authMode, "local", true);
        }
        final InetAddress address = InetAddress.getByName("localhost");
        if (address instanceof Inet6Address) {
            PostgresConfUtil.grantAccessForHost("::1/128", "replication", username, authMode, "host", true);
        }
        PostgresBackupHandler.isReplicationEnabled = true;
    }
    
    protected void addSequenceName(final String sequenceName) {
        if (!this.sequenceNames.contains(sequenceName)) {
            this.sequenceNames.add(sequenceName);
        }
    }
    
    protected boolean contains(final String[] tableNames, final String tableNameToSearch) {
        for (final String tableName : tableNames) {
            if (tableName.equalsIgnoreCase(tableNameToSearch)) {
                return true;
            }
        }
        return false;
    }
    
    protected void deleteTouchFile(final File touchFile) throws BackupRestoreException {
        if (touchFile.exists()) {
            final boolean isDeleted = FileUtils.deleteFile(touchFile);
            if (!isDeleted) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_DELETING_TOUCH_FILE);
            }
            PostgresBackupHandler.LOGGER.log(Level.INFO, "Deleted file :: [{0}]", touchFile.getAbsolutePath());
        }
    }
    
    static {
        LOGGER = Logger.getLogger(PostgresBackupHandler.class.getName());
        OS = OSCheckUtil.getOS();
        PostgresBackupHandler.isReplicationEnabled = false;
    }
}
