package com.me.tools.dbmigration.handler;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.db.migration.util.DBMigrationUtil;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.db.persistence.metadata.TableDefinition;
import com.adventnet.db.persistence.metadata.MetaDataException;
import java.util.logging.Level;
import com.adventnet.db.persistence.metadata.util.MetaDataUtil;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.DBMigrationPrePostHandler;

public class DBMUpdateStringLengthPreHandler implements DBMigrationPrePostHandler
{
    private static final Logger LOGGER;
    
    public void updateACSQLStringMaxColSize() {
        try {
            final TableDefinition acsqlStringTableDefn = MetaDataUtil.getTableDefinitionByName("ACSQLString");
            final ColumnDefinition sqlColumnDefn = acsqlStringTableDefn.getColumnDefinitionByName("SQL");
            sqlColumnDefn.setMaxLength(-1);
        }
        catch (final MetaDataException ex) {
            DBMUpdateStringLengthPreHandler.LOGGER.log(Level.SEVERE, null, (Throwable)ex);
        }
    }
    
    public void preHandle() throws Exception {
        Connection srcConnection = null;
        try {
            srcConnection = DBMigrationUtil.getSrcConnection();
            if (DBMigrationUtil.getSrcDBType().equals("MYSQL")) {
                DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "Trimming the entries for text column in MYSQL");
                Statement selectSTMT = null;
                ResultSet tablenameRS = null;
                Statement updateSTMT = null;
                try {
                    String sql = null;
                    int changedRowCount = 0;
                    selectSTMT = srcConnection.createStatement();
                    tablenameRS = selectSTMT.executeQuery("SELECT tabledetails.table_name, columndetails.column_name, columndetails.max_size FROM columndetails INNER JOIN tabledetails ON tabledetails.table_id = columndetails.table_id WHERE columndetails.max_size > 255");
                    while (tablenameRS.next()) {
                        final String tableName = tablenameRS.getString("table_name");
                        final String columnName = tablenameRS.getString("column_name");
                        final int columnSize = tablenameRS.getInt("max_size");
                        if (!tableName.equals("help_topic") && !tableName.equals("SelectSQLString") && !tableName.equals("ACSQLString") && !tableName.equals("ACCountSQLString") && !tableName.equals("DCNativeSQL") && !tableName.equals("DCNativeSQLString")) {
                            updateSTMT = srcConnection.createStatement();
                            sql = "UPDATE " + tableName + " SET " + columnName + "= LEFT(" + columnName + " , " + columnSize + ") WHERE LENGTH(" + columnName + ") > " + columnSize;
                            final int rowCount = updateSTMT.executeUpdate(sql);
                            changedRowCount += rowCount;
                            if (rowCount == 0) {
                                continue;
                            }
                            DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "Trimmed {0} rows of table {1}", new Object[] { changedRowCount, tableName });
                        }
                    }
                    DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "Length handling for text column in MYSQL has been done successfully");
                }
                catch (final Exception ex) {
                    DBMUpdateStringLengthPreHandler.LOGGER.log(Level.WARNING, "Got Exception while trimming Mysql text columns", ex);
                    try {
                        if (selectSTMT != null) {
                            selectSTMT.close();
                        }
                        if (tablenameRS != null) {
                            tablenameRS.close();
                        }
                        if (updateSTMT != null) {
                            updateSTMT.close();
                        }
                    }
                    catch (final Exception ex) {
                        DBMUpdateStringLengthPreHandler.LOGGER.log(Level.WARNING, "Got Exception while closing conection");
                    }
                }
                finally {
                    try {
                        if (selectSTMT != null) {
                            selectSTMT.close();
                        }
                        if (tablenameRS != null) {
                            tablenameRS.close();
                        }
                        if (updateSTMT != null) {
                            updateSTMT.close();
                        }
                    }
                    catch (final Exception ex2) {
                        DBMUpdateStringLengthPreHandler.LOGGER.log(Level.WARNING, "Got Exception while closing conection");
                    }
                }
            }
            if (DBMigrationUtil.getDestDBType().equals("MSSQL")) {
                DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "Starting SQL column handling for MSSQL");
                this.updateACSQLStringMaxColSize();
                DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "SQL column handling completed");
            }
            if (DBMigrationUtil.getDestDBType().equals("POSTGRES")) {
                DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "Starting SQL column handling for POSTGRES");
                this.updateACSQLStringMaxColSize();
                DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "SQL column handling completed");
            }
        }
        catch (final Throwable t) {
            t.printStackTrace();
            throw new Exception(t);
        }
        finally {
            if (srcConnection != null) {
                srcConnection.close();
            }
        }
    }
    
    public void postHandle() throws Exception {
        DBMUpdateStringLengthPreHandler.LOGGER.log(Level.INFO, "Not yet supported");
    }
    
    static {
        LOGGER = Logger.getLogger(DBMUpdateStringLengthPreHandler.class.getName());
    }
}
