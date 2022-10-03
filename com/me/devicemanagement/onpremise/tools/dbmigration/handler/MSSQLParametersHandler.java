package com.me.devicemanagement.onpremise.tools.dbmigration.handler;

import java.util.Map;
import com.adventnet.db.adapter.DBAdapter;
import java.sql.PreparedStatement;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.Column;
import java.util.LinkedHashMap;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.sql.Connection;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.DBMigrationPrePostHandler;

public class MSSQLParametersHandler implements DBMigrationPrePostHandler
{
    private static Logger logger;
    
    private static void modifyTxLogMaxSize(final Connection connection) throws Exception {
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            final String dbName = DBMigrationUtil.getHandlerFactory().getConfiguration("dest.create.db.name");
            stmt = connection.createStatement();
            String query = "select name, size from sys.master_files where name = '" + dbName.trim() + "'";
            int dbsize = 0;
            int txLogSizeInMB = 5120;
            resultSet = stmt.executeQuery(query);
            if (resultSet != null) {
                while (resultSet.next()) {
                    dbsize = resultSet.getInt("size");
                }
            }
            dbsize /= 128;
            if (dbsize < 5120) {
                txLogSizeInMB = 5120;
            }
            else if (dbsize > 15360) {
                txLogSizeInMB = 15360;
            }
            else {
                txLogSizeInMB = dbsize;
            }
            MSSQLParametersHandler.logger.info("Setting max size of tx log as = " + txLogSizeInMB);
            query = "ALTER DATABASE " + dbName + " MODIFY FILE (NAME=" + dbName + "_log,MAXSIZE=" + txLogSizeInMB + "MB);";
            stmt.execute(query);
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception e) {
                MSSQLParametersHandler.logger.log(Level.SEVERE, "Exception occurred while closing the connection in MSSQLParametersHandler..", e);
            }
        }
    }
    
    private static void enableShapshot(final Connection connection) throws Exception {
        Statement stmt = null;
        ResultSet resultSet = null;
        try {
            final String dbName = DBMigrationUtil.getHandlerFactory().getConfiguration("dest.create.db.name");
            MSSQLParametersHandler.logger.log(Level.INFO, "Destination DB Name = " + dbName);
            stmt = connection.createStatement();
            int connectionCount = 0;
            String query = "select b.name as DatabaseName, count(a.dbid) as TotalConnections from sys.sysprocesses a inner join sys.databases b on a.dbid = b.database_id where b.name='" + dbName.trim() + "' group by a.dbid, b.name";
            int count = 0;
            while (connectionCount != 1 && count < 5) {
                resultSet = stmt.executeQuery(query);
                if (resultSet.next()) {
                    connectionCount = Integer.parseInt(resultSet.getString("TotalConnections"));
                    MSSQLParametersHandler.logger.log(Level.INFO, "connection count " + connectionCount + " : " + resultSet.getString("DatabaseName"));
                    if (connectionCount > 1) {
                        Thread.sleep(3000L);
                        ++count;
                    }
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            if (count < 5 && connectionCount == 1) {
                MSSQLParametersHandler.logger.log(Level.INFO, "Going to set ALLOW_SNAPSHOT_ISOLATION and READ_COMMITTED_SNAPSHOT ON for MSSQL during DB migration");
                query = "ALTER DATABASE " + dbName + " SET READ_COMMITTED_SNAPSHOT ON WITH NO_WAIT";
                stmt.execute(query);
                query = "ALTER DATABASE " + dbName + " SET ALLOW_SNAPSHOT_ISOLATION ON";
                stmt.execute(query);
            }
            else {
                MSSQLParametersHandler.logger.log(Level.INFO, "Couldn't set the values of READ_COMMITTED_SNAPSHOT and ALLOW_SNAPSHOT_ISOLATION to ON,Since the connection count is still " + connectionCount + "Also it retries " + count + " times.");
            }
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception e) {
                MSSQLParametersHandler.logger.log(Level.SEVERE, "Exception occurred while closing the connection in MSSQLParametersHandler..", e);
            }
        }
    }
    
    private void updateServerParameter(final Connection connection) {
        PreparedStatement prepStmt = null;
        final ResultSet resultSet = null;
        try {
            final String dbName = DBMigrationUtil.getHandlerFactory().getConfiguration("dest.create.db.name");
            MSSQLParametersHandler.logger.log(Level.INFO, "Destination DB Name = " + dbName);
            final String currentTime = String.valueOf(System.currentTimeMillis());
            final DBAdapter destDBAdapter = DBMigrationUtil.getDestDBAdapter();
            final Map map = new LinkedHashMap();
            map.put(Column.getColumn("ServerParams", "PARAM_VALUE"), QueryConstants.PREPARED_STMT_CONST);
            final Criteria criteria = new Criteria(Column.getColumn("ServerParams", "PARAM_NAME"), (Object)"last_db_migration_time", 0);
            final String updateSQL = destDBAdapter.getSQLGenerator().getSQLForUpdate("ServerParams", map, criteria);
            prepStmt = connection.prepareStatement(updateSQL);
            prepStmt.setString(1, currentTime);
            final int rowsAfftected = prepStmt.executeUpdate();
            if (rowsAfftected > 0) {
                MSSQLParametersHandler.logger.log(Level.INFO, "last_db_migration_time updated in ServerParams table with " + currentTime);
            }
        }
        catch (final Exception e) {
            MSSQLParametersHandler.logger.log(Level.INFO, "Exception in updating server parameter table", e);
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (prepStmt != null) {
                    prepStmt.close();
                }
            }
            catch (final Exception e) {
                MSSQLParametersHandler.logger.log(Level.SEVERE, "Exception occurred while closing the connection in MSSQLParametersHandler..", e);
            }
        }
        finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (prepStmt != null) {
                    prepStmt.close();
                }
            }
            catch (final Exception e2) {
                MSSQLParametersHandler.logger.log(Level.SEVERE, "Exception occurred while closing the connection in MSSQLParametersHandler..", e2);
            }
        }
    }
    
    public void preHandle() throws Exception {
    }
    
    public void postHandle() {
        Connection connection = null;
        try {
            if (!DBMigrationUtil.getDestDBType().equals("MSSQL")) {
                MSSQLParametersHandler.logger.info("Not mssql migrations. exiting");
                return;
            }
            connection = DBMigrationUtil.getDestConnection();
            enableShapshot(connection);
            modifyTxLogMaxSize(connection);
            this.updateServerParameter(connection);
        }
        catch (final Exception e) {
            MSSQLParametersHandler.logger.log(Level.SEVERE, "Exception occurred while setting READ_COMMITTED_SNAPSHOT and ALLOW_SNAPSHOT_ISOLATION on", e);
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e) {
                MSSQLParametersHandler.logger.log(Level.SEVERE, "Exception occurred while closing the connection in MSSQLParametersHandler..", e);
            }
        }
        finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            }
            catch (final Exception e2) {
                MSSQLParametersHandler.logger.log(Level.SEVERE, "Exception occurred while closing the connection in MSSQLParametersHandler..", e2);
            }
        }
    }
    
    static {
        MSSQLParametersHandler.logger = Logger.getLogger(MSSQLParametersHandler.class.getName());
    }
}
