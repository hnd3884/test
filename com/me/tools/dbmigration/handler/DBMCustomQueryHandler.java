package com.me.tools.dbmigration.handler;

import java.sql.ResultSet;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.sql.Statement;
import java.sql.Connection;
import com.adventnet.db.migration.util.DBMigrationUtil;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.db.migration.handler.DBMigrationPrePostHandler;

public class DBMCustomQueryHandler implements DBMigrationPrePostHandler
{
    private static Logger logger;
    
    public void preHandle() throws Exception {
        DBMCustomQueryHandler.logger.log(Level.INFO, "preHandle() method - Not yet supported");
    }
    
    public void postHandle() throws Exception {
        DBMCustomQueryHandler.logger.log(Level.INFO, "Entering into postHandle() method");
        Connection destConnection = null;
        try {
            if (!DBMigrationUtil.getSrcDBType().equals("MSSQL")) {
                destConnection = DBMigrationUtil.getDestConnection();
                final boolean hasCustomQueries = this.createQueryFile(destConnection);
                if (hasCustomQueries) {
                    this.changeQueryToDestDB(destConnection);
                }
            }
            else {
                DBMCustomQueryHandler.logger.log(Level.INFO, "MSSQL to MSSQL migration, No need to handle custom queries");
            }
        }
        catch (final Exception ex) {
            DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception in postHandle: ", ex);
            try {
                if (destConnection != null) {
                    destConnection.close();
                }
            }
            catch (final Exception ex) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception when closing the connection : ", ex);
            }
        }
        finally {
            try {
                if (destConnection != null) {
                    destConnection.close();
                }
            }
            catch (final Exception ex2) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception when closing the connection : ", ex2);
            }
        }
        DBMCustomQueryHandler.logger.log(Level.INFO, "Exiting from postHandle() method");
    }
    
    private void changeQueryToDestDB(final Connection destConnection) {
        DBMCustomQueryHandler.logger.log(Level.INFO, "Going to change the DB type for Custom queries");
        Statement stmt = null;
        try {
            String updateQuery = null;
            if (DBMigrationUtil.getDestDBType().equals("POSTGRES")) {
                final int db_type = 3;
                updateQuery = "UPDATE CRSaveViewDetails set DB_TYPE = " + db_type + " WHERE QR_QUERY IS NOT NULL";
            }
            else {
                final int db_type = 2;
                updateQuery = "UPDATE CRSaveViewDetails set DB_TYPE = " + db_type + " WHERE QR_QUERY IS NOT NULL";
            }
            stmt = destConnection.createStatement();
            DBMCustomQueryHandler.logger.log(Level.INFO, "Query to be executed : {0}", updateQuery);
            final int updatedRows = stmt.executeUpdate(updateQuery);
            DBMCustomQueryHandler.logger.log(Level.INFO, "No.of Custom queries changed : {0}", updatedRows);
        }
        catch (final Exception ex) {
            DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while handling custom queries : ", ex);
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception ex) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while closing connection in changeQueryToDestDB Method", ex);
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            }
            catch (final Exception ex2) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while closing connection in changeQueryToDestDB Method", ex2);
            }
        }
    }
    
    private boolean createQueryFile(final Connection destConnection) {
        DBMCustomQueryHandler.logger.log(Level.INFO, "Going to create file for Custom queries");
        Statement stmt = null;
        ResultSet queryRS = null;
        FileOutputStream fout = null;
        BufferedWriter out = null;
        boolean isCustomQueryPresent = false;
        try {
            final String query = "SELECT CRSaveViewDetails.DISPLAY_CRVIEWNAME,CRSaveViewDetails.QR_QUERY FROM CRSaveViewDetails WHERE CRSaveViewDetails.QR_QUERY IS NOT NULL";
            stmt = destConnection.createStatement();
            queryRS = stmt.executeQuery(query);
            if (queryRS.next()) {
                final File customQueryFile = new File(System.getProperty("server.home") + File.separator + "dbmigration" + File.separator + "CustomQuery.txt");
                customQueryFile.createNewFile();
                fout = new FileOutputStream(customQueryFile);
                out = new BufferedWriter(new OutputStreamWriter(fout, "UTF8"));
                out.write("Source database : " + DBMigrationUtil.getSrcDBType().toString() + "\n");
                out.write("Destination database : " + DBMigrationUtil.getDestDBType().toString() + "\n");
                out.write("Build number : " + this.getBuildNumber(destConnection) + "\n");
                do {
                    final String queryName = queryRS.getString("DISPLAY_CRVIEWNAME");
                    final String customQuery = queryRS.getString("QR_QUERY");
                    out.write(queryName);
                    out.write("\n");
                    out.write(customQuery);
                    out.write("\n\n");
                } while (queryRS.next());
                out.flush();
                isCustomQueryPresent = true;
                DBMCustomQueryHandler.logger.log(Level.INFO, "Created CustomQuery.txt file");
            }
            else {
                DBMCustomQueryHandler.logger.log(Level.INFO, "No custom queries");
            }
        }
        catch (final Exception ex) {
            DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while writing custom queries to file: ", ex);
            try {
                if (fout != null) {
                    fout.close();
                }
                if (out != null) {
                    out.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (queryRS != null) {
                    queryRS.close();
                }
            }
            catch (final Exception ex) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while closing connection in createQueryFile() Method. Can be ignored");
            }
        }
        finally {
            try {
                if (fout != null) {
                    fout.close();
                }
                if (out != null) {
                    out.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (queryRS != null) {
                    queryRS.close();
                }
            }
            catch (final Exception ex2) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while closing connection in createQueryFile() Method. Can be ignored");
            }
        }
        return isCustomQueryPresent;
    }
    
    private String getBuildNumber(final Connection destConnection) {
        DBMCustomQueryHandler.logger.log(Level.INFO, "Getting build number from DB");
        String buildNumber = "0";
        Statement stmt = null;
        ResultSet buildNumberRS = null;
        try {
            stmt = destConnection.createStatement();
            String buildNumberQuery;
            if (DBMigrationUtil.getDestDBType().equals("POSTGRES")) {
                buildNumberQuery = "SELECT BUILD_NUMBER FROM DCServerBuildHistory ORDER BY BUILD_NUMBER DESC LIMIT 1";
            }
            else {
                buildNumberQuery = "SELECT TOP 1 BUILD_NUMBER FROM DCServerBuildHistory ORDER BY BUILD_NUMBER DESC";
            }
            buildNumberRS = stmt.executeQuery(buildNumberQuery);
            if (buildNumberRS.next()) {
                buildNumber = buildNumberRS.getString("BUILD_NUMBER");
            }
            DBMCustomQueryHandler.logger.log(Level.INFO, "Got build number from DB : {0}", buildNumber);
        }
        catch (final Exception ex) {
            DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception in getBuildNumber", ex);
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (buildNumberRS != null) {
                    buildNumberRS.close();
                }
            }
            catch (final Exception ex) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while closing connection in getBuildNumber() Method", ex);
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (buildNumberRS != null) {
                    buildNumberRS.close();
                }
            }
            catch (final Exception ex2) {
                DBMCustomQueryHandler.logger.log(Level.WARNING, "Caught exception while closing connection in getBuildNumber() Method", ex2);
            }
        }
        return buildNumber;
    }
    
    static {
        DBMCustomQueryHandler.logger = Logger.getLogger(DBMCustomQueryHandler.class.getName());
    }
}
