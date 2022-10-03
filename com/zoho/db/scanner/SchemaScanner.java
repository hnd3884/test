package com.zoho.db.scanner;

import java.util.Locale;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import com.zoho.db.scanner.util.ExecutorPool;
import javax.sql.DataSource;
import com.zoho.db.model.DataBase;
import java.util.logging.Logger;

public class SchemaScanner
{
    private static final Logger LOGGER;
    
    public static void scan(final DataBase dataBase, final DataSource dataSource) throws Exception {
        final ExecutorPool executerPool = new ExecutorPool(10);
        ScannerCrew.setThreadPool(executerPool.getWorkerPool());
        ScannerCrew.setDataSource(dataSource);
        try {
            submitTaskForCrew(executerPool, dataBase, false);
            executerPool.waitForTaskCompletion();
            submitTaskForCrew(executerPool, dataBase, true);
            executerPool.waitForTaskCompletion();
        }
        catch (final RejectedExecutionException ree) {
            executerPool.waitForWorkerPoolShutdown();
            SchemaScanner.LOGGER.severe("It seems scanner thread pool got interuppted.");
            throw new Exception("Scanning database schema failed for " + dataBase.getDbLable() + ".");
        }
        finally {
            executerPool.getWorkerPool().shutdown();
        }
    }
    
    protected static void submitTaskForCrew(final ExecutorPool executerPool, final DataBase dataBase, final boolean linkRelations) {
        for (final String tableName : dataBase.getAllTableNames()) {
            executerPool.submitTaskForWorker(new ScannerCrew(dataBase, tableName, linkRelations));
        }
    }
    
    public static List<String> getAllTableNames(final String catalog, final String schema, final Connection conn) throws SQLException {
        final DatabaseMetaData meta = conn.getMetaData();
        final List<String> list = new ArrayList<String>();
        ResultSet rs = null;
        try {
            rs = meta.getTables(catalog, schema, null, new String[] { "TABLE" });
            while (rs.next()) {
                final String tableName = rs.getString("TABLE_NAME");
                list.add(tableName);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }
        }
        return list;
    }
    
    public static boolean isTablePresentInDB(final Connection c, final String schemaName, final String tableName) throws SQLException {
        final boolean isExist = isTableExists(c, schemaName, tableName);
        if (!isExist) {
            return isTableExists(c, schemaName, tableName.toLowerCase(Locale.ENGLISH));
        }
        return isExist;
    }
    
    private static boolean isTableExists(final Connection c, final String schemaName, final String tableName) throws SQLException {
        ResultSet tableSet = null;
        boolean isExist = false;
        final DatabaseMetaData metaData = c.getMetaData();
        try {
            tableSet = metaData.getTables(null, null, tableName, new String[] { "TABLE" });
            if (tableSet.next()) {
                isExist = true;
            }
        }
        finally {
            if (tableSet != null) {
                tableSet.close();
            }
        }
        return isExist;
    }
    
    static {
        LOGGER = Logger.getLogger(SchemaScanner.class.getName());
    }
}
