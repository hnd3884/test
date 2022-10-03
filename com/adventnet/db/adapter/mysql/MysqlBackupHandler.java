package com.adventnet.db.adapter.mysql;

import java.util.Hashtable;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.PersistenceInitializer;
import java.util.Collection;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.QueryConstructionException;
import java.util.Iterator;
import com.zoho.framework.utils.FileUtils;
import com.zoho.framework.utils.crypto.CryptoUtil;
import com.zoho.mickey.exception.PasswordException;
import com.adventnet.persistence.PersistenceException;
import com.adventnet.persistence.PersistenceUtil;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Properties;
import com.adventnet.db.adapter.BackupRestoreUtil;
import com.zoho.framework.utils.OSCheckUtil;
import com.adventnet.mfw.BackupDB;
import java.sql.Connection;
import com.adventnet.db.adapter.BackupStatus;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import com.zoho.conf.Configuration;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import java.io.IOException;
import com.adventnet.db.adapter.BackupResult;
import com.adventnet.db.adapter.BackupDBParams;
import java.sql.SQLException;
import com.adventnet.db.adapter.BackupRestoreException;
import com.adventnet.db.adapter.BackupErrors;
import java.util.logging.Level;
import java.sql.Statement;
import java.util.logging.Logger;
import com.adventnet.mfw.message.MessageListener;
import com.adventnet.db.adapter.AbstractBackupHandler;

public class MysqlBackupHandler extends AbstractBackupHandler implements MessageListener
{
    private static String server_home;
    private static final Logger LOGGER;
    public static final int OS;
    
    @Override
    protected void flushBuffers(final Statement stmt) throws BackupRestoreException {
        try {
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Flush Binary Logs");
            stmt.execute("FLUSH LOGS");
        }
        catch (final SQLException sqle) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FLUSHING_LOGS, sqle);
        }
    }
    
    public void onMessage(final Object msg) {
        MysqlBackupHandler.LOGGER.log(Level.INFO, "Entered into the onMessage of MysqlBackupHandler");
        MysqlBackupHandler.LOGGER.log(Level.INFO, "No handling required for improper cleanup in MySQL Backup");
        MysqlBackupHandler.LOGGER.log(Level.INFO, "Returning from the onMessage of MysqlBackupHandler");
    }
    
    @Override
    protected BackupResult doIncrementalBackup(final BackupDBParams params) throws BackupRestoreException {
        final boolean endsWithEZIP = params.zipFileName.endsWith(".ezip");
        MysqlBackupHandler.LOGGER.log(Level.INFO, "Entered doIncrementalBackup :: [{0}]", params);
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
            List<String> fileList = null;
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED);
            final String mysqlPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "mysqlbinlog";
            if (!this.checkBinaryForIncrementalBackup()) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "mysqlbinlog not found at :: {0}", mysqlPath);
                throw new BackupRestoreException(BackupErrors.INCREMENTAL_BINARY_NOT_FOUND);
            }
            final String sqlFileName = params.zipFileName.substring(0, params.zipFileName.length() - (endsWithEZIP ? 4 : 3)) + "sql";
            final String sqlFilePath = params.backupFolder + File.separator + sqlFileName;
            final String fileDataFolder = Configuration.getString("db.home") + File.separator + "data" + File.separator;
            final List<String> fileNames = new ArrayList<String>();
            fileNames.add(mysqlPath);
            final List<String> binLogsList = this.getBINFileNames();
            for (int i = 0; i < binLogsList.size(); ++i) {
                fileNames.add(fileDataFolder + binLogsList.get(i));
            }
            backupStatus.setDataFileCount(binLogsList.size());
            fileNames.add("--result-file=" + sqlFilePath);
            Connection c = null;
            Statement s = null;
            try {
                c = this.dataSource.getConnection();
                s = c.createStatement();
                final String lastBinFile = this.getLastBINFileName();
                final String nextBinFile = this.getNextBINFileName();
                this.flushBuffers(s);
                this.executeCommand(fileNames);
                backupStatus.setLastDataFileName(lastBinFile);
                params.lastDataFileName = lastBinFile;
                this.purgeLogs(s, nextBinFile);
            }
            catch (final SQLException e2) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_CONNECTION, e2);
            }
            catch (final Exception e3) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e3);
            }
            finally {
                if (s != null) {
                    try {
                        s.close();
                    }
                    catch (final Exception e4) {
                        e4.printStackTrace();
                    }
                }
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (final Exception e4) {
                        e4.printStackTrace();
                    }
                }
            }
            this.deleteBinlogQuery(sqlFilePath);
            final File tempBackupDir = new File(params.backupFolder + File.separator + params.zipFileName.replace(".ezip", ""));
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Created directory [{0}] :: {1}", new Object[] { tempBackupDir, tempBackupDir.mkdirs() });
            final File indexFile = this.generateIndexFile(params, backupStatus);
            fileList = new ArrayList<String>();
            fileList.add(sqlFilePath);
            fileList.add(indexFile.getAbsolutePath());
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_CREATE_BACKUPZIP);
            this.zip(params.backupFolder, params.zipFileName, params.backupFolder, false, false, fileList, null, params.archivePassword, params.archiveEncAlgo);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUPZIP_CREATION_COMPLETED);
            params.backupSize = new File(params.backupFolder, params.zipFileName).length();
            params.backupEndTime = System.currentTimeMillis();
            params.lastIncrementalBackupEndTime = params.backupEndTime;
            backupResult.setBackupSize(params.backupSize);
            backupResult.setStartTime(params.backupStartTime);
            backupResult.setEndTime(params.backupEndTime);
            fileList.add(tempBackupDir.getAbsolutePath());
            backupResult.setFilesToBeCleaned(fileList);
            backupResult.setBackupStatus(backupStatus.getStatus());
            backupResult.calculateDuration();
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
            backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Exiting doIncrementalBackup with backupResult :: {0}", backupResult);
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
        final boolean endsWithEZIP = params.zipFileName.endsWith(".ezip");
        MysqlBackupHandler.LOGGER.log(Level.INFO, "Entered doFullBackup :: [{0}]", params);
        final boolean isBundled = this.dbAdapter.isBundledDB();
        final boolean isLoopBackAddress = ((MysqlDBAdapter)this.dbAdapter).isLoopbackAddress();
        final String myCnf = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "my.cnf";
        if (!isBundled && BackupDB.BACKUP_DB_USING_SCRIPTS) {
            MysqlBackupHandler.LOGGER.log(Level.WARNING, "Binary Backup not supported for Installed DB. Instead backing up by Dump (Tables) Method");
            params.backupContentType = BackupRestoreConfigurations.BACKUP_CONTENT_TYPE.DUMP;
            return this.doTableBackup(params);
        }
        if (!isBundled && !OSCheckUtil.isWindows(MysqlBackupHandler.OS) && isLoopBackAddress) {
            final File f = new File(myCnf);
            if (!f.exists()) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Backup failed for Installed DB. Required file my.cnf not found at :: [{0}]", myCnf);
                throw new BackupRestoreException(BackupErrors.INSTALLED_DB_CONF_FILE_NOT_FOUND);
            }
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
            List<String> fileList = null;
            final Properties properties = this.dbAdapter.getDBProps();
            final String username = properties.getProperty("username");
            final String password = properties.getProperty("password", "");
            String url = properties.getProperty("url");
            if (url.contains("?")) {
                url = url.substring(0, url.indexOf("?"));
            }
            final String hostName = ((Hashtable<K, String>)properties).get("Server");
            final String sockPath = Configuration.getString("db.home") + File.separator + "tmp" + File.separator + "mysql.sock";
            final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
            final Integer port = ((Hashtable<K, Integer>)properties).get("Port");
            final File dir = params.backupFolder;
            if (!dir.exists()) {
                MysqlBackupHandler.LOGGER.log(Level.INFO, "Directory [{0}] doesnot exist. Creating directory.", dir);
                dir.mkdir();
            }
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED);
            final String mysqlPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "mysqldump";
            if (!this.checkBinaryForFullBackup()) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "mysqldump not found at :: {0}", mysqlPath);
                throw new BackupRestoreException(BackupErrors.FULL_BINARY_NOT_FOUND);
            }
            final String sqlFileName = params.zipFileName.substring(0, params.zipFileName.length() - (endsWithEZIP ? 4 : 3)) + "sql";
            final String sqlFilePath = params.backupFolder + File.separator + sqlFileName;
            String lastBinLog = null;
            String nextBinLog = null;
            Connection c = null;
            Statement s = null;
            try {
                c = this.dataSource.getConnection();
                s = c.createStatement();
                final List<String> commandList = new ArrayList<String>();
                if (params.backupMode == BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP) {
                    lastBinLog = this.getLastBINFileName();
                    nextBinLog = this.getNextBINFileName();
                    this.flushBuffers(s);
                }
                commandList.add(mysqlPath);
                if (isLoopBackAddress && !isBundled && !OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
                    commandList.add("--defaults-file=" + myCnf);
                }
                else {
                    commandList.add("--no-defaults");
                }
                commandList.add("--quick");
                commandList.add("--user=" + username);
                commandList.add("--password=" + password);
                commandList.add("--port=" + port);
                commandList.add("--host=" + hostName);
                if (isBundled && !OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
                    commandList.add("-S");
                    commandList.add(sockPath);
                }
                commandList.add("-B");
                commandList.add(database);
                commandList.add("--hex-blob");
                commandList.add("--add-drop-database");
                if (params.backupMode == BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP) {
                    commandList.add("--single-transaction");
                    commandList.add("--master-data");
                }
                commandList.add("--result-file=" + sqlFilePath);
                this.executeCommand(commandList);
                if (params.backupMode == BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP) {
                    this.purgeLogs(s, nextBinLog);
                }
            }
            catch (final SQLException e2) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_CONNECTION, e2);
            }
            catch (final Exception e3) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, e3);
            }
            finally {
                if (s != null) {
                    try {
                        s.close();
                    }
                    catch (final Exception e4) {
                        e4.printStackTrace();
                    }
                }
                if (c != null) {
                    try {
                        c.close();
                    }
                    catch (final Exception e4) {
                        e4.printStackTrace();
                    }
                }
            }
            final File tempBackupDir = new File(params.backupFolder + File.separator + params.zipFileName.replace(".ezip", ""));
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Created directory [{0}] :: {1}", new Object[] { tempBackupDir, tempBackupDir.mkdirs() });
            final File indexFile = this.generateIndexFile(params, backupStatus);
            backupStatus.setLastDataFileName(lastBinLog);
            params.lastDataFileName = lastBinLog;
            fileList = new ArrayList<String>();
            fileList.add(sqlFilePath);
            fileList.add(indexFile.getAbsolutePath());
            try {
                fileList.add(BackupRestoreUtil.getDynamicColumnsInfoFileLocation(tempBackupDir.getAbsolutePath()));
            }
            catch (final Exception e4) {
                throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_DYNAMIC_COLUMN_FILE, e4);
            }
            fileList.add(this.getConfFilesLocation(tempBackupDir));
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_CREATE_BACKUPZIP);
            this.zip(params.backupFolder, params.zipFileName, params.backupFolder, false, false, fileList, null, params.archivePassword, params.archiveEncAlgo);
            params.backupSize = new File(params.backupFolder, params.zipFileName).length();
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUPZIP_CREATION_COMPLETED);
            params.backupEndTime = System.currentTimeMillis();
            params.lastFullBackupStartTime = params.backupStartTime;
            backupResult.setBackupSize(params.backupSize);
            backupResult.setStartTime(params.backupStartTime);
            fileList.add(tempBackupDir.getAbsolutePath());
            backupResult.setFilesToBeCleaned(fileList);
            backupResult.setEndTime(params.backupEndTime);
            backupResult.calculateDuration();
            backupStatus.setBackupEndTime(params.backupEndTime);
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Exiting doFullBackup with backupResult :: {0}", backupResult);
            return backupResult;
        }
        catch (final BackupRestoreException e5) {
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            throw e5;
        }
    }
    
    private void deleteBinlogQuery(final String incBackupFileName) {
        try {
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Deleting BINLOG query");
            final File inputFile = new File(incBackupFileName);
            final File tempFile = new File("myTempFile.txt");
            final BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            final BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            final String lineToRemove = "BINLOG '";
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                final String trimmedLine = currentLine.trim();
                if (trimmedLine.equals(lineToRemove)) {
                    currentLine = reader.readLine();
                    currentLine = ((currentLine != null) ? currentLine.trim() : currentLine);
                    currentLine = reader.readLine();
                    currentLine = ((currentLine != null) ? currentLine.trim() : currentLine);
                    currentLine = reader.readLine();
                    currentLine = ((currentLine != null) ? currentLine.trim() : currentLine);
                }
                else {
                    writer.write(currentLine + "\n");
                }
            }
            reader.close();
            inputFile.delete();
            writer.close();
            tempFile.renameTo(inputFile);
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
    }
    
    private void purgeLogs(final Statement s, final String binFileName) {
        try {
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Purging Binary Logs");
            s.execute("PURGE BINARY LOGS TO '" + binFileName + "'");
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }
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
        final Properties p = this.generateIndexProperties(params, status);
        try {
            if (this.dbAdapter.isBundledDB()) {
                p.setProperty("arch", this.dbAdapter.getDBInitializer().getDBArchitecture() + "");
                p.setProperty("dbversion", this.dbAdapter.getDBInitializer().getVersion());
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        try {
            password = PersistenceUtil.getDBPasswordProvider().getEncryptedPassword(password);
        }
        catch (final PersistenceException | PasswordException e) {
            e.printStackTrace();
            throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_PASSWORD);
        }
        p.setProperty("uid", password);
        p.setProperty("uname", CryptoUtil.encrypt(database));
        final File tempBackupDir = new File(params.backupFolder + File.separator + params.zipFileName.replace(".ezip", ""));
        File indexFile = null;
        if (params.backupType == BackupRestoreConfigurations.BACKUP_TYPE.FULL_BACKUP) {
            indexFile = new File(tempBackupDir, "full_index.props");
        }
        else {
            indexFile = new File(tempBackupDir, "incremental_index.props");
        }
        try {
            FileUtils.writeToFile(indexFile, p, "backup_index_props");
            MysqlBackupHandler.LOGGER.log(Level.INFO, indexFile.getName() + " file generated for the backup");
        }
        catch (final IOException e2) {
            MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Error while writing into property file");
            throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_BACKUP_INDEX_PROPS, e2);
        }
        AbstractBackupHandler.sendBackupStatusNotification(status, BackupRestoreConfigurations.BACKUP_STATUS.INDEXFILE_GENERATED_FOR_BACKUP);
        return indexFile;
    }
    
    protected void executeCommand(final List<String> commandList) throws BackupRestoreException {
        try {
            this.executeCommand(commandList, null, null);
        }
        catch (final BackupRestoreException e) {
            int index = -1;
            for (final String str : commandList) {
                if (str.startsWith("--password")) {
                    index = commandList.indexOf(str);
                    break;
                }
            }
            if (index != -1) {
                commandList.set(index, "--password=********");
            }
            MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Unable to execute command ", commandList);
            throw e;
        }
    }
    
    public boolean isLogBinEnabledInMysql(final boolean isBundled) throws IOException {
        boolean enabled = false;
        MysqlBackupHandler.LOGGER.log(Level.FINE, "Entered isLogBinEnabledInMysql method");
        if (isBundled) {
            if (!BackupDB.BACKUP_DB_USING_SCRIPTS) {
                String scriptFileName = Configuration.getString("server.home") + File.separator + "bin" + File.separator;
                if (OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
                    scriptFileName += "startDB.bat";
                }
                else {
                    scriptFileName += "startDB.sh";
                }
                final File mysqlStartFile = new File(scriptFileName);
                if (!mysqlStartFile.exists()) {
                    MysqlBackupHandler.LOGGER.log(Level.WARNING, "startDB :: [{0}] file doesnot exists hence returning disable", mysqlStartFile);
                    enabled = false;
                    return enabled;
                }
                final FileReader fr = new FileReader(mysqlStartFile);
                final BufferedReader br = new BufferedReader(fr);
                final String word = "--log-bin";
                String s;
                while ((s = br.readLine()) != null) {
                    final int t1 = s.indexOf(word);
                    if (t1 != -1) {
                        enabled = true;
                    }
                }
                br.close();
                fr.close();
                if (!enabled) {
                    MysqlBackupHandler.LOGGER.log(Level.SEVERE, "OnlineBackup schedule will be disabled since the log-bin is not enabled during DB startup. To enable include --log-bin (after --no-defaults) in startDB script file");
                    enabled = false;
                }
            }
            else {
                MysqlBackupHandler.LOGGER.log(Level.INFO, "log-bin not necessary for script backup");
                enabled = true;
            }
        }
        else {
            Connection con = null;
            final DataSet ds = null;
            try {
                con = this.dataSource.getConnection();
                final String logBin = this.dbAdapter.getDBSystemProperty(con, "log_bin");
                if (logBin.equalsIgnoreCase("ON")) {
                    enabled = true;
                }
                else {
                    MysqlBackupHandler.LOGGER.log(Level.SEVERE, "log-bin has to be enabled");
                    enabled = false;
                }
            }
            catch (final SQLException e) {
                e.printStackTrace();
            }
            catch (final QueryConstructionException e2) {
                e2.printStackTrace();
            }
            finally {
                try {
                    if (ds != null) {
                        ds.close();
                    }
                }
                catch (final SQLException e3) {
                    e3.printStackTrace();
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                }
                catch (final SQLException e3) {
                    e3.printStackTrace();
                }
            }
        }
        return enabled;
    }
    
    @Deprecated
    @Override
    protected BackupResult doTableBackup(final BackupDBParams params) throws BackupRestoreException {
        final boolean endsWithEZIP = params.zipFileName.endsWith(".ezip");
        MysqlBackupHandler.LOGGER.log(Level.INFO, "Entered doFullBackup :: [{0}]", params);
        final String myCnf = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "my.cnf";
        final boolean isLoopBackAddress = ((MysqlDBAdapter)this.dbAdapter).isLoopbackAddress();
        final boolean isBundled = this.dbAdapter.isBundledDB();
        if (!isBundled && !OSCheckUtil.isWindows(MysqlBackupHandler.OS) && isLoopBackAddress) {
            final File f = new File(myCnf);
            if (!f.exists()) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Backup failed for Installed DB. Required file my.cnf not found at :: [{0}]", myCnf);
                throw new BackupRestoreException(BackupErrors.INSTALLED_DB_CONF_FILE_NOT_FOUND);
            }
        }
        String backupDir = null;
        try {
            backupDir = params.backupFolder.getCanonicalPath();
        }
        catch (final IOException ioe) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_BACKUP_DIRECTORY_PATH, ioe);
        }
        final String backupFile = params.zipFileName;
        MysqlBackupHandler.LOGGER.log(Level.INFO, "backupDir :: [" + backupDir + "]");
        final BackupStatus backupStatus = AbstractBackupHandler.getBackupStatus(params);
        final BackupResult backupResult = new BackupResult(backupFile, backupDir);
        backupResult.setBackupMode(params.backupMode);
        backupResult.setBackupContentType(params.backupContentType);
        try {
            final String mysqlPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "mysqldump";
            if (!this.checkBinaryForFullBackup()) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "{0} is not bundled with the database. Hence backup is not possible.", mysqlPath);
                throw new BackupRestoreException(BackupErrors.DUMP_BINARY_NOT_FOUND);
            }
            final String backupFileName = backupFile.substring(0, backupFile.lastIndexOf("."));
            final String path = backupDir + File.separator + backupFileName;
            final File file = new File(path);
            final File zipFile = new File(path + (endsWithEZIP ? ".ezip" : ".zip"));
            if (!zipFile.exists() && !file.exists() && !file.isDirectory()) {
                file.mkdirs();
                if (this.backupRestoreHandler != null) {
                    final String version = this.backupRestoreHandler.getCurrentVersion();
                    if (version == null) {
                        MysqlBackupHandler.LOGGER.severe("Current version cannot be null. Provide proper version.");
                        AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                        backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                        return backupResult;
                    }
                    final Properties versionProp = new Properties();
                    ((Hashtable<String, String>)versionProp).put("handler", this.backupRestoreHandler.getClass().getName());
                    ((Hashtable<String, String>)versionProp).put("version", version);
                    try {
                        FileUtils.writeToFile(new File(path + File.separator + "version.conf"), versionProp, "version properties");
                    }
                    catch (final IOException ioe2) {
                        throw new BackupRestoreException(BackupErrors.PROBLEM_WRITING_VERSION_PROPERTIES, ioe2);
                    }
                }
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_STARTED);
                backupResult.setStartTime(System.currentTimeMillis());
                MysqlBackupHandler.LOGGER.log(Level.INFO, "BackingUp the DB ...");
                if (BackupDB.SHOW_STATUS) {
                    ConsoleOut.print("BackingUp the DB ...");
                }
                try {
                    final List<String> tableList = this.getTableNamesForBackUp();
                    final int size = tableList.size();
                    final String[] tableNames = tableList.toArray(new String[0]);
                    final Properties properties = this.dbAdapter.getDBProps();
                    final String username = properties.getProperty("username");
                    final String password = properties.getProperty("password", "");
                    String url = properties.getProperty("url");
                    if (url.contains("?")) {
                        url = url.substring(0, url.indexOf("?"));
                    }
                    final String hostName = ((Hashtable<K, String>)properties).get("Server");
                    final String sockPath = Configuration.getString("db.home") + "/tmp/mysql.sock";
                    final Integer port = ((Hashtable<K, Integer>)properties).get("Port");
                    final String database = url.substring(url.lastIndexOf("/") + 1, url.length());
                    final Properties props = new Properties();
                    final List<String> commandList = new ArrayList<String>();
                    commandList.add(mysqlPath);
                    if (isLoopBackAddress && !isBundled && !OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
                        commandList.add("--defaults-file=" + myCnf);
                    }
                    else {
                        commandList.add("--no-defaults");
                    }
                    commandList.add("--user=" + username);
                    commandList.add("--password=" + password);
                    commandList.add("--port=" + port);
                    commandList.add("--host=" + hostName);
                    if (isBundled && !OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
                        commandList.add("-S");
                        commandList.add(sockPath);
                    }
                    commandList.add("--hex-blob");
                    commandList.add(database);
                    for (int i = 0; i < size; ++i) {
                        if (!MysqlBackupHandler.backupDBInProgress) {
                            MysqlBackupHandler.LOGGER.log(Level.INFO, "backupDB is aborted" + path);
                            FileUtils.deleteDir(path);
                            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                            backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                            return backupResult;
                        }
                        ((Hashtable<String, String>)props).put("table" + i, tableNames[i]);
                        backupResult.addTable(tableNames[i]);
                        final List<String> command = new ArrayList<String>();
                        command.addAll(commandList);
                        command.add(tableNames[i]);
                        if (params.backupMode == BackupRestoreConfigurations.BACKUP_MODE.ONLINE_BACKUP) {
                            command.add("--single-transaction");
                        }
                        command.add("-r");
                        if (OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
                            command.add("\"" + path + "\\" + tableNames[i] + ".sql\"");
                        }
                        else {
                            command.add(path + "/" + tableNames[i] + ".sql");
                        }
                        this.executeCommand(command);
                        MysqlBackupHandler.LOGGER.log(Level.INFO, "Table " + i + " : " + tableNames[i]);
                        if (BackupDB.SHOW_STATUS) {
                            ConsoleOut.print(".");
                        }
                    }
                    this.addSpecXMLEntryToProps(props);
                    final String oldCryptTag = (PersistenceInitializer.getConfigurationValue("CryptTag") == null) ? "MLITE_ENCRYPT_DECRYPT" : PersistenceInitializer.getConfigurationValue("CryptTag");
                    ((Hashtable<String, String>)props).put("oldCryptTag", oldCryptTag);
                    FileUtils.writeToFile(new File(path + File.separator + "backuprestore.conf"), props, "backuprestore.conf mysql database");
                }
                catch (final IOException ioe3) {
                    FileUtils.deleteDir(path);
                    MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Problem while writing spec.xml or backuprestore.conf");
                    throw new BackupRestoreException(BackupErrors.PROBLEM_GENERATING_BACKUPRESTORE_PROPS, ioe3);
                }
                catch (final Exception ex) {
                    FileUtils.deleteDir(path);
                    MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Unable to retrieve tables from DB");
                    if (BackupDB.SHOW_STATUS) {
                        ConsoleOut.println("Unable to retrieve tables from DB");
                    }
                    throw new BackupRestoreException(BackupErrors.PROBLEM_WHILE_EXECUTING_COMMAND, ex);
                }
                final List<String> dirs = new ArrayList<String>();
                dirs.add(path);
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.GOING_TO_CREATE_BACKUPZIP);
                this.zip(params.backupFolder, backupFile, new File(path), false, true, null, null, params.archivePassword, params.archiveEncAlgo);
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUPZIP_CREATION_COMPLETED);
                FileUtils.deleteDir(path);
                params.backupSize = new File(params.backupFolder, params.zipFileName).length();
                MysqlBackupHandler.LOGGER.log(Level.INFO, "Backup File {0}.zip", path);
                if (BackupDB.SHOW_STATUS) {
                    ConsoleOut.println("\nBackup File " + path + (endsWithEZIP ? ".ezip" : ".zip"));
                }
                backupResult.setBackupSize(params.backupSize);
                backupResult.setEndTime(System.currentTimeMillis());
                backupResult.calculateDuration();
                AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
                backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
                MysqlBackupHandler.LOGGER.log(Level.INFO, "Exiting doTableBackup with backupResult :: {0}", backupResult);
                return backupResult;
            }
            MysqlBackupHandler.LOGGER.log(Level.INFO, "Already a file/directory with this name [{0}.zip] is found. Take the backup after a minute.", path);
            throw new BackupRestoreException(BackupErrors.BACKUP_FILE_ALREADY_EXISTS);
        }
        catch (final BackupRestoreException e) {
            AbstractBackupHandler.sendBackupStatusNotification(backupStatus, BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            backupResult.setBackupStatus(backupStatus.getStatus());
            throw e;
        }
    }
    
    public boolean checkBinaryForFullBackup() {
        String mysqlPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "mysqldump";
        MysqlBackupHandler.LOGGER.log(Level.INFO, "mysqlPath :: {0}", mysqlPath);
        if (OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
            mysqlPath += ".exe";
        }
        return new File(mysqlPath).exists();
    }
    
    public boolean checkBinaryForIncrementalBackup() {
        String mysqlPath = Configuration.getString("db.home") + File.separator + "bin" + File.separator + "mysqlbinlog";
        if (OSCheckUtil.isWindows(MysqlBackupHandler.OS)) {
            mysqlPath += ".exe";
        }
        return new File(mysqlPath).exists();
    }
    
    private String getLastBINFileName() throws BackupRestoreException {
        String lastbinlog = null;
        final List<String> binLogList = this.getBINFileNames();
        lastbinlog = binLogList.get(binLogList.size() - 1);
        return lastbinlog;
    }
    
    private List<String> getBINFileNames() throws BackupRestoreException {
        final List<String> binLogList = new ArrayList<String>();
        Connection con = null;
        DataSet ds = null;
        try {
            con = this.dataSource.getConnection();
            ds = RelationalAPI.getInstance().executeQuery("SHOW BINARY LOGS", con);
            while (ds.next()) {
                binLogList.add(ds.getAsString(1));
            }
        }
        catch (final Exception e) {
            throw new BackupRestoreException(BackupErrors.PROBLEM_FETCHING_DB_LOG_FILE, e);
        }
        finally {
            try {
                if (ds != null) {
                    ds.close();
                }
            }
            catch (final SQLException e2) {
                e2.printStackTrace();
            }
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (final SQLException e2) {
                e2.printStackTrace();
            }
        }
        return binLogList;
    }
    
    private String getNextBINFileName() throws BackupRestoreException {
        String nextBinFileName = null;
        final String lastBinFileName = this.getLastBINFileName();
        final String binFileNameWithoutExtension = lastBinFileName.substring(0, lastBinFileName.length() - 6);
        int binFileIndex = Integer.parseInt(lastBinFileName.substring(lastBinFileName.length() - 6, lastBinFileName.length()));
        nextBinFileName = String.format("%s%06d", binFileNameWithoutExtension, ++binFileIndex);
        return nextBinFileName;
    }
    
    public boolean isBinLogFormatRow() throws IOException {
        boolean enabled = false;
        MysqlBackupHandler.LOGGER.log(Level.FINE, "Entered isBinLogFormatRow method");
        Connection con = null;
        try {
            con = this.dataSource.getConnection();
            final String binLogFormat = this.dbAdapter.getDBSystemProperty(con, "binlog_format");
            if (binLogFormat.equalsIgnoreCase("ROW")) {
                enabled = true;
            }
            else {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "binlog_format should be ROW");
                enabled = false;
            }
        }
        catch (final SQLException e) {
            e.printStackTrace();
        }
        catch (final QueryConstructionException e2) {
            e2.printStackTrace();
        }
        finally {
            try {
                if (con != null) {
                    con.close();
                }
            }
            catch (final SQLException e3) {
                e3.printStackTrace();
            }
        }
        return enabled;
    }
    
    public boolean isValid(final Properties backupProps) throws BackupRestoreException {
        boolean isValid = false;
        boolean isBundled = false;
        boolean isDumpBackup = false;
        try {
            isBundled = this.dbAdapter.isBundledDB();
            isValid = this.isLogBinEnabledInMysql(isBundled);
            if (isValid && !isBundled) {
                isValid = this.isBinLogFormatRow();
            }
        }
        catch (final IOException e) {
            throw new BackupRestoreException(BackupErrors.DATABASE_BACKUP_MISCONFIGURED, e);
        }
        isDumpBackup = backupProps.getProperty("backup.content.type").equals("dump");
        if (isDumpBackup) {
            if (!BackupDB.BACKUP_DB_USING_SCRIPTS) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Mysql Online Backup doesnot support table wise backup.");
                throw new BackupRestoreException(BackupErrors.UNSUPPORTED_BACKUP_TYPE);
            }
            if (!this.checkBinaryForFullBackup()) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "mysqldump not found.");
                throw new BackupRestoreException(BackupErrors.DUMP_BINARY_NOT_FOUND);
            }
        }
        if (isValid) {
            if (!this.checkBinaryForFullBackup()) {
                MysqlBackupHandler.LOGGER.log(Level.SEVERE, "mysqldump not found.");
                throw new BackupRestoreException(BackupErrors.FULL_BINARY_NOT_FOUND);
            }
            if (isBundled) {
                int fullBackupInterval = 0;
                if (backupProps.getProperty("fullbackup.interval") != null) {
                    fullBackupInterval = Integer.parseInt(backupProps.getProperty("fullbackup.interval"));
                    if (fullBackupInterval > 0 && !this.checkBinaryForIncrementalBackup()) {
                        MysqlBackupHandler.LOGGER.log(Level.SEVERE, "mysqlbinlog not found.");
                        throw new BackupRestoreException(BackupErrors.INCREMENTAL_BINARY_NOT_FOUND);
                    }
                }
            }
        }
        return isValid;
    }
    
    @Override
    public void cleanBackupConfigFiles() throws BackupRestoreException {
        MysqlBackupHandler.LOGGER.log(Level.SEVERE, "Backup Schedule has been disabled. Clean the binlogs manually or disable log binning");
    }
    
    static {
        MysqlBackupHandler.server_home = ((Configuration.getString("server.home") != null) ? Configuration.getString("server.home") : Configuration.getString("app.home"));
        LOGGER = Logger.getLogger(MysqlBackupHandler.class.getName());
        OS = OSCheckUtil.getOS();
    }
}
