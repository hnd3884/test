package com.adventnet.db.adapter.mssql;

import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.db.persistence.metadata.MetaDataException;
import org.json.simple.parser.ParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.FileReader;
import org.json.simple.parser.JSONParser;
import org.json.JSONException;
import com.adventnet.ds.query.QueryConstructionException;
import org.json.simple.JSONArray;
import java.util.Optional;
import java.util.stream.Stream;
import java.sql.Statement;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.MssqlBackupRestoreUtil;
import java.util.concurrent.Callable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;
import com.me.devicemanagement.onpremise.server.util.EMSExecutorPool;
import com.me.devicemanagement.onpremise.tools.backuprestore.action.DMFileBackup;
import com.me.devicemanagement.onpremise.tools.backuprestore.util.BackupRestoreUtil;
import java.sql.SQLException;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.mfw.ConsoleOut;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import com.adventnet.db.adapter.BackupRestoreConfigurations;
import java.util.logging.Level;
import com.adventnet.mfw.RestoreDB;

public class DCMssqlDBAdapter extends MssqlDBAdapter
{
    public int restoreDB(final String zipFile) throws Exception {
        if (!RestoreDB.RESTORING_DB_USING_SCRIPTS) {
            DCMssqlDBAdapter.out.log(Level.WARNING, "Restore db can be called via RestoreDB.restoreDB only.");
            return BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_PROCESS_FAILED.getValue();
        }
        final String path = zipFile.substring(0, zipFile.length() - 4);
        final File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
        this.unZip(zipFile, path);
        FileInputStream fis = null;
        final Properties props = new Properties();
        try {
            fis = new FileInputStream(new File(path + "/backuprestore.conf"));
            props.load(fis);
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        ConsoleOut.println("Can proceed to restore the database from the Zip file");
        ConsoleOut.println("Starting to restore the DB");
        DCMssqlDBAdapter.out.log(Level.INFO, "RestoringUp DB Started");
        dropDynamicColumnsFromExistingSetup();
        Connection con = null;
        Statement stmt = null;
        try {
            ConsoleOut.print("Dropping Tables ...");
            final RelationalAPI relAPI = RelationalAPI.getInstance();
            con = relAPI.getConnection();
            stmt = con.createStatement();
            this.disableForeignKeyChecks(stmt, props);
            for (int i = 0; i < props.size(); ++i) {
                final String tableName = props.getProperty("table" + i);
                if (tableName != null) {
                    try {
                        final String deleteStr = "delete from " + tableName;
                        stmt.executeUpdate(deleteStr);
                        ConsoleOut.print(".");
                        DCMssqlDBAdapter.out.log(Level.SEVERE, "Dropped table " + tableName);
                    }
                    catch (final SQLException sqle) {
                        DCMssqlDBAdapter.out.log(Level.SEVERE, "tableName " + tableName);
                        sqle.printStackTrace();
                    }
                }
            }
        }
        catch (final Exception e) {
            DCMssqlDBAdapter.out.log(Level.INFO, "Problem while Reinitializing the DB.");
            throw e;
        }
        finally {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        }
        DCMssqlDBAdapter.out.log(Level.WARNING, " Dynamic Column handling  initiated ");
        restoreDynamicColumnSchemaChanges(path);
        ConsoleOut.println("");
        ConsoleOut.print("Restoring ...");
        DCMssqlDBAdapter.out.log(Level.INFO, "DB Restore process started");
        final int tableCount = props.size();
        final Properties backupAttributes = BackupRestoreUtil.getInstance().getBackupAttributes(DMFileBackup.getInstance().getDoc());
        final int corePoolSize = Integer.parseInt(backupAttributes.getProperty("mssql_dump_restore_thread_pool_core_size", "1"));
        final int maxPoolSize = Integer.parseInt(backupAttributes.getProperty("mssql_dump_restore_thread_pool_max_size", "1"));
        final int queueSize = Integer.parseInt(backupAttributes.getProperty("mssql_dump_restore_thread_pool_queue_size", "5000"));
        final int batchesPerTransaction = Integer.parseInt(backupAttributes.getProperty("mssql_restore_batches_per_transaction_commit", "5"));
        final int batchSize = Integer.parseInt(backupAttributes.getProperty("mssql_restore_batch_size", "500"));
        final long idleTimeout = Integer.parseInt(backupAttributes.getProperty("mssql_restore_thread_pool_idle_timeout", "1800000"));
        final EMSExecutorPool emsExecutorPool = new EMSExecutorPool("restoreTablePool", corePoolSize, maxPoolSize, queueSize, idleTimeout, DCMssqlDBAdapter.out);
        try (final Connection connection = RelationalAPI.getInstance().getConnection()) {
            final AtomicBoolean restoringTableStatus = new AtomicBoolean(true);
            for (int j = tableCount - 1; j >= 0; --j) {
                final String tableName2 = props.getProperty("table" + j);
                if (tableName2 != null) {
                    String backupFile = path + "/" + tableName2 + ".sql";
                    backupFile = backupFile.replaceAll("\\\\", "/");
                    long size = 0L;
                    String header = null;
                    Stream<String> stream = Files.lines(Paths.get(backupFile, new String[0]));
                    final Optional<String> optional = stream.limit(1L).findFirst();
                    if (!optional.isPresent()) {
                        DCMssqlDBAdapter.out.log(Level.INFO, "=========================\nRestoring Skipped For Empty Table {0} \nTable Count : {1} of {2} \n=========================", new Object[] { tableName2, tableCount - j, tableCount });
                    }
                    else {
                        stream.close();
                        stream = Files.lines(Paths.get(backupFile, new String[0]));
                        header = optional.get();
                        header = header.replaceAll("`", "\"");
                        size = stream.count();
                        stream.close();
                        for (long count = 1L; count < size; count += batchSize) {
                            emsExecutorPool.submitTaskForWorker(this.getRunnableForTableRestore(tableName2, tableCount - j, tableCount, path, restoringTableStatus, connection, count, batchSize, size, header, batchesPerTransaction));
                            if (MssqlBackupRestoreUtil.executeBatchStartedCount.get() >= batchesPerTransaction) {
                                while (MssqlBackupRestoreUtil.executeBatchStartedCount.get() != MssqlBackupRestoreUtil.executeBatchEndedCount.get()) {
                                    TimeUnit.MILLISECONDS.sleep(100L);
                                }
                                connection.commit();
                                MssqlBackupRestoreUtil.executeBatchEndedCount.set(0);
                                MssqlBackupRestoreUtil.executeBatchStartedCount.set(0);
                            }
                        }
                    }
                }
            }
            while (emsExecutorPool.hasActiveOrQueuedTasks()) {
                if (MssqlBackupRestoreUtil.executeBatchStartedCount.get() >= batchesPerTransaction) {
                    while (MssqlBackupRestoreUtil.executeBatchStartedCount.get() != MssqlBackupRestoreUtil.executeBatchEndedCount.get()) {
                        TimeUnit.MILLISECONDS.sleep(100L);
                    }
                    connection.commit();
                    MssqlBackupRestoreUtil.executeBatchEndedCount.set(0);
                    MssqlBackupRestoreUtil.executeBatchStartedCount.set(0);
                }
                TimeUnit.MILLISECONDS.sleep(100L);
                if (!restoringTableStatus.get()) {
                    throw new Exception("Exception while restoring DB");
                }
            }
            emsExecutorPool.waitForTaskCompletionAndShutDown();
        }
        catch (final Exception e2) {
            DCMssqlDBAdapter.out.log(Level.WARNING, "Exception in restoring DB ", e2);
            emsExecutorPool.interruptPool();
            throw e2;
        }
        enableFKConstraint();
        new MssqlBackupRestoreUtil().deleteFiles(path);
        return BackupRestoreConfigurations.RESTORE_STATUS.RESTORE_SUCCESSFULLY_COMPLETED.getValue();
    }
    
    private Callable<Boolean> getRunnableForTableRestore(final String tableName, final int currentTable, final int tableCount, final String backupFilePath, final AtomicBoolean restoringTableStatus, final Connection connection, final long start, final long size, final long totalRows, final String header, final int bathesPerTransaction) {
        return (Callable<Boolean>)(() -> {
            try {
                DCMssqlDBAdapter.out.log(Level.INFO, "=========================\nRestoring Started  For Table {0} \nTable Count : {1} of {2} \nRows Count : {3} - {4} of {5} \n=========================", new Object[] { s, n, n2, n3, n3 + n4, n5 });
                final String backupFile = s2 + "/" + s + ".sql";
                final String backupFile2 = backupFile.replaceAll("\\\\", "/");
                MssqlBackupRestoreUtil.getInstance().restoreTable(backupFile2, "mssql", connection2, n3, n4, s3, atomicBoolean, n6, s, true);
                ConsoleOut.print(".");
                DCMssqlDBAdapter.out.log(Level.INFO, "=========================\nRestoring Completed For Table {0} \nTable Count : {1} of {2} \nRows Count : {3} - {4} of {5} \n=========================", new Object[] { s, n, n2, n3, n3 + n4, n5 });
            }
            catch (final Exception ex) {
                DCMssqlDBAdapter.out.log(Level.INFO, "Exception while restoring Table " + s + " Exception :", ex);
                atomicBoolean.set(false);
                throw new RuntimeException(ex);
            }
            return null;
        });
    }
    
    private static void dropDynamicColumnsFromExistingSetup() throws QueryConstructionException, SQLException, JSONException {
        final JSONArray dynamicColumnList = MssqlBackupRestoreUtil.getInstance().getDynamicColumnSchemaFromSetup();
        MssqlBackupRestoreUtil.getInstance().dropDynamicColumns(dynamicColumnList);
    }
    
    private static void restoreDynamicColumnSchemaChanges(final String path) throws IOException, ParseException, JSONException, MetaDataException, QueryConstructionException, SQLException {
        final String dynamicColumnDetailsJSON = path + File.separator + "dynamiccolumndetails.json";
        final File file = new File(dynamicColumnDetailsJSON);
        if (file.exists()) {
            final JSONParser jsonParser = new JSONParser();
            final FileReader fileReader = new FileReader(dynamicColumnDetailsJSON);
            final Object object = jsonParser.parse((Reader)fileReader);
            final JSONArray dynamicColumnList = (JSONArray)object;
            MssqlBackupRestoreUtil.getInstance().createDynamicColumns(dynamicColumnList);
        }
        else {
            DCMssqlDBAdapter.out.log(Level.WARNING, "Dynamic Column details doesn't exists in backup. ");
        }
    }
    
    public void disableForeignKeyChecks(final Statement stmt, final Properties props) throws SQLException {
        for (int i = 0; i < props.size(); ++i) {
            final String tableName = props.getProperty("table" + i);
            if (tableName != null) {
                try {
                    stmt.executeUpdate("ALTER TABLE " + tableName + "  NOCHECK CONSTRAINT ALL");
                }
                catch (final Exception e) {
                    DCMssqlDBAdapter.out.log(Level.INFO, "Problem while disabling Foreign Key for the table = " + tableName + " ; Exception " + e);
                }
            }
        }
    }
    
    protected void unZip(final String src, final String dst) throws Exception {
        super.unZip(src, dst);
    }
    
    public static void enableFKConstraint() {
        String query = null;
        final String currentDB = PersistenceInitializer.getConfigurationValue("DBName");
        if ("mssql".equals(currentDB)) {
            query = "exec sp_MSforeachtable 'ALTER TABLE ? CHECK CONSTRAINT ALL'";
        }
        DCMssqlDBAdapter.out.log(Level.INFO, "enableFKConstraint : Query to set FK contraint : " + query);
        try {
            final RelationalAPI relapi = RelationalAPI.getInstance();
            relapi.execute(query);
            DCMssqlDBAdapter.out.log(Level.INFO, "enableFKConstraint : Successfully ENABLED FK Constraint");
        }
        catch (final Exception ex) {
            DCMssqlDBAdapter.out.log(Level.WARNING, "enableFKConstraint : Exception while ENABLING FK Constraint", ex);
        }
    }
}
