package com.adventnet.db.adapter.mssql;

import java.util.Hashtable;
import java.nio.file.Files;
import org.json.simple.JSONArray;
import java.io.FileWriter;
import java.io.IOException;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import com.me.devicemanagement.onpremise.tools.backuprestore.handler.DMProcessBuilderHandler;
import com.adventnet.db.adapter.BackupStatus;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.util.zip.ZipOutputStream;
import java.io.BufferedOutputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.HashMap;
import java.sql.Connection;
import java.util.Date;
import com.adventnet.db.adapter.BackupRestoreException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.MssqlBackupRestoreUtil;
import java.util.Properties;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.mfw.BackupDB;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import com.adventnet.db.adapter.BackupResult;
import com.adventnet.db.adapter.BackupDBParams;
import java.util.logging.Logger;

public class DCMssqlBackupHandler extends MssqlBackupHandler
{
    Logger out;
    
    public DCMssqlBackupHandler() {
        this.out = Logger.getLogger("DCBackupRestoreUI");
    }
    
    public BackupResult doTableBackup(final BackupDBParams params) throws BackupRestoreException {
        final String backupDir = params.backupFolder.toString();
        final BackupResult backupResult = new BackupResult();
        this.out.log(Level.SEVERE, "backupDir :: [" + backupDir + "]");
        DCMssqlBackupHandler.backupDBInProgress = true;
        try {
            final Date today = Calendar.getInstance().getTime();
            final SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd-HHmm");
            final String datenewformat = formatter.format(today);
            final String path = backupDir + "/" + datenewformat;
            final File file = new File(path);
            final File zipFile = new File(path + ".zip");
            if (!zipFile.exists() && !file.exists() && !file.isDirectory()) {
                file.mkdirs();
                if (BackupDB.SHOW_STATUS) {
                    ConsoleOut.print("BackingUp the DB ...");
                }
                Connection connection = null;
                try {
                    final RelationalAPI relapi = RelationalAPI.getInstance();
                    connection = relapi.getConnection();
                    this.backupDynamicColumnSchema(path);
                    final HashMap pkdetails = this.getPkDetails(connection);
                    this.out.log(Level.FINE, "Test Print : " + pkdetails);
                    final List tableList = MetaDataUtil.getTableNamesInDefinedOrder();
                    final int size = tableList.size();
                    final String[] tableNames = tableList.toArray(new String[0]);
                    final Properties props = new Properties();
                    for (int i = 0; i < size; ++i) {
                        if (!DCMssqlBackupHandler.backupDBInProgress) {
                            this.out.log(Level.SEVERE, "backupDB is aborted" + path);
                            new MssqlBackupRestoreUtil().deleteFiles(path);
                            backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_TERMINATED);
                            return backupResult;
                        }
                        final String tableName = tableNames[i];
                        final String pK = pkdetails.get(tableName.toLowerCase());
                        if (MssqlBackupRestoreUtil.isToBackupTable(tableName) && this.isCreateTable(connection, tableName)) {
                            Connection con = null;
                            try {
                                con = relapi.getConnection();
                                this.out.log(Level.INFO, "Backing up table {0} ({1} of {2})", new Object[] { tableName, i + 1, size });
                                MssqlBackupRestoreUtil.getInstance().dumpTable(tableName, pK, con, path);
                                ((Hashtable<String, String>)props).put("table" + i, tableNames[i]);
                            }
                            catch (final Exception ex) {
                                this.out.log(Level.WARNING, "Exception in dumping table :", ex);
                                backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
                                return backupResult;
                            }
                            finally {
                                if (con != null) {
                                    con.close();
                                }
                            }
                        }
                        else {
                            this.out.log(Level.INFO, "createtable value false for this table : " + tableName);
                        }
                        if (BackupDB.SHOW_STATUS) {
                            ConsoleOut.print(".");
                        }
                    }
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(new File(path + "/backuprestore.conf"));
                        props.store(fos, "Backup Tables");
                    }
                    finally {
                        if (fos != null) {
                            fos.close();
                        }
                    }
                }
                catch (final Exception ex2) {
                    new MssqlBackupRestoreUtil().deleteFiles(path);
                    this.out.log(Level.WARNING, "Exception while dumping DB", ex2);
                    ex2.printStackTrace();
                    throw new BackupRestoreException(ex2.getMessage());
                }
                finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        }
                        catch (final Exception ex3) {}
                    }
                }
                this.zip(path);
                new MssqlBackupRestoreUtil().deleteFiles(path);
                this.out.log(Level.INFO, "Backup File " + path + ".zip");
                if (BackupDB.SHOW_STATUS) {
                    ConsoleOut.println("\nBackup File " + path + ".zip");
                }
                backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_SUCCESSFULLY_COMPLETED);
                return backupResult;
            }
            this.out.log(Level.INFO, "Already a file/directory with this name [" + path + ".zip] is found. Take the backup after a minute.");
            backupResult.setBackupStatus(BackupRestoreConfigurations.BACKUP_STATUS.BACKUP_PROCESS_FAILED);
            return backupResult;
        }
        finally {
            DCMssqlBackupHandler.backupDBInProgress = false;
        }
    }
    
    private HashMap getPkDetails(final Connection conn) throws Exception {
        final HashMap pkdetails = new LinkedHashMap();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            final String sql = "select max(td.TABLE_NAME) 'Table', max(cd.COLUMN_NAME) 'PKColumn' from PKDefinition pkd LEFT JOIN ColumnDetails cd ON pkd.PK_COLUMN_ID = cd.COLUMN_ID LEFT JOIN TableDetails td ON cd.TABLE_ID = td.TABLE_ID where DATA_TYPE IN ('BIGINT','INTEGER') group by cd.TABLE_ID HAVING COUNT(PK_COLUMN_ID) = 1";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                pkdetails.put(rs.getString(1).toLowerCase(), rs.getString(2));
            }
        }
        catch (final Exception ex) {}
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (rs != null) {
                rs.close();
            }
        }
        return pkdetails;
    }
    
    private boolean isCreateTable(final Connection con, final String tableName) {
        boolean result = true;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            final String sql = "select createtable from Tabledetails where TABLE_NAME = '" + tableName + "'";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString("createtable").equals("0")) {
                    result = false;
                }
            }
        }
        catch (final Exception ex) {
            this.out.log(Level.SEVERE, "error occurred while finding create table value for this table :: ", ex);
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e) {
                this.out.log(Level.SEVERE, "error occurred while closing result set in isCreateTable method  ", e);
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e2) {
                this.out.log(Level.SEVERE, "error occurred while closing result set in isCreateTable method  ", e2);
            }
        }
        return result;
    }
    
    protected void zip(final String path) {
        final int BUFFER = 2048;
        BufferedInputStream origin = null;
        FileOutputStream dest = null;
        ZipOutputStream output = null;
        FileInputStream fi = null;
        try {
            final String zipfile = path + ".zip";
            dest = new FileOutputStream(zipfile);
            output = new ZipOutputStream(new BufferedOutputStream(dest));
            final byte[] data = new byte[2048];
            final File f = new File(path);
            final String[] files = f.list();
            final File[] f2 = f.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (f2[i].isFile()) {
                    this.out.log(Level.FINE, "Adding to Zip: " + files[i]);
                    fi = new FileInputStream(f2[i]);
                    origin = new BufferedInputStream(fi, 2048);
                    final ZipEntry entry = new ZipEntry(files[i]);
                    output.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, 2048)) != -1) {
                        output.write(data, 0, count);
                    }
                    fi.close();
                    origin.close();
                }
            }
            output.close();
        }
        catch (final Exception e) {
            this.out.log(Level.FINE, "Error occurred while zipping:" + e);
            e.printStackTrace();
        }
        finally {
            try {
                output.close();
                origin.close();
                dest.close();
                fi.close();
            }
            catch (final Exception ex) {}
        }
    }
    
    public BackupResult doFileBackup(final String backupDir, final String backupFileName, final List<String> directoriesToBeArchived, final String versionHandlerName, final Properties prefProps) throws BackupRestoreException {
        return null;
    }
    
    public void doCleanup(final List<String> filesToBeDeleted) {
    }
    
    public boolean abortBackup() throws BackupRestoreException {
        return false;
    }
    
    public boolean isValid(final Properties backupProps) throws BackupRestoreException {
        return true;
    }
    
    protected void flushBuffers(final Statement s) throws BackupRestoreException {
    }
    
    protected BackupResult doIncrementalBackup(final BackupDBParams params) throws BackupRestoreException {
        return null;
    }
    
    protected BackupResult doFullBackup(final BackupDBParams params) throws BackupRestoreException {
        return super.doFullBackup(params);
    }
    
    protected File generateIndexFile(final BackupDBParams params, final BackupStatus backupStatus) throws BackupRestoreException {
        return super.generateIndexFile(params, backupStatus);
    }
    
    public void enableIncrementalBackup() throws BackupRestoreException {
    }
    
    public void disableIncrementalBackup() throws BackupRestoreException {
    }
    
    public void cleanBackupConfigFiles() throws BackupRestoreException {
    }
    
    public boolean isIncrementalBackupValid() throws BackupRestoreException {
        return false;
    }
    
    protected void executeCommand(final List<String> cmds, final Properties envProps, final String errorMsgToIgnore) throws BackupRestoreException {
        this.out.log(Level.INFO, "Calling executeCommand()");
        DMProcessBuilderHandler.getInstance().executeCommand((List)cmds, envProps, errorMsgToIgnore);
    }
    
    protected void zip(final File zipFolder, final String zipFileName, final File contentDirectory, final boolean includeContentDirectoryToo, final boolean includeFilesInContentDirectory, final List<String> includeFileList, final List<String> excludeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        this.out.log(Level.INFO, "Calling zip()");
        if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
            DMProcessBuilderHandler.getInstance().zip(zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFilesInContentDirectory, (List)includeFileList, (List)excludeFileList, archivePassword, encAlgo);
        }
        else {
            super.zip(zipFolder, zipFileName, contentDirectory, includeContentDirectoryToo, includeFilesInContentDirectory, (List)includeFileList, (List)excludeFileList, archivePassword, encAlgo);
        }
    }
    
    protected void appendInZip(final String zipFilePath, final List<String> includeFileList, final String archivePassword, final String encAlgo) throws BackupRestoreException {
        this.out.log(Level.INFO, "Calling appendInZip()");
        if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
            DMProcessBuilderHandler.getInstance().appendInZip(zipFilePath, (List)includeFileList, archivePassword, encAlgo);
        }
        else {
            super.appendInZip(zipFilePath, (List)includeFileList, archivePassword, encAlgo);
        }
    }
    
    protected void unZip(final File zipFile, final File destinationFolder, final List<String> includeFileList, final List<String> excludeFileList) throws IOException, InterruptedException {
        this.out.log(Level.INFO, "Calling unZip()");
        if (BackupRestoreUtil.getInstance().useNativeForExecution()) {
            DMProcessBuilderHandler.getInstance().unZip(zipFile, destinationFolder, (List)includeFileList, (List)excludeFileList, (String)null);
        }
        else {
            super.unZip(zipFile, destinationFolder, (List)includeFileList, (List)excludeFileList);
        }
    }
    
    public void backupDynamicColumnSchema(final String backupPath) {
        FileWriter file = null;
        try {
            final JSONArray dynamicColumnDetails = MssqlBackupRestoreUtil.getInstance().getDynamicColumnSchemaFromSetup();
            file = new FileWriter(backupPath + File.separator + "dynamiccolumndetails.json");
            file.write(dynamicColumnDetails.toString());
            file.flush();
        }
        catch (final Exception e) {
            this.out.log(Level.WARNING, "Exception while getting used locale from db : ", e);
            if (file != null) {
                try {
                    file.close();
                }
                catch (final IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        finally {
            if (file != null) {
                try {
                    file.close();
                }
                catch (final IOException e3) {
                    e3.printStackTrace();
                }
            }
        }
    }
    
    protected String getAllVersions(final File specsFile) throws IOException {
        final byte[] b = Files.readAllBytes(specsFile.toPath());
        final String strFromSpecsFile = new String(b);
        final String versionString = strFromSpecsFile.substring(strFromSpecsFile.indexOf(" AllVersions=") + 14, strFromSpecsFile.indexOf(" Versions=") - 1);
        return versionString;
    }
}
