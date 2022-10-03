package com.me.devicemanagement.onpremise.tools.backuprestore.handler;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.persistence.PersistenceInitializer;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.db.adapter.RestoreResult;
import com.adventnet.db.adapter.RestoreDBParams;
import java.util.logging.Logger;
import com.adventnet.db.adapter.postgres.PostgresRestoreHandler;

public class DMPostgresRestoreHandler extends PostgresRestoreHandler
{
    private static Logger logger;
    
    protected RestoreResult restoreTableBackup(final RestoreDBParams rdbp) {
        RestoreResult restoreResult = new RestoreResult(rdbp.getSourceFile());
        try {
            DMPostgresRestoreHandler.logger.log(Level.INFO, "Going to create MEDC role before restore setup. ");
            this.createReadUser();
            restoreResult = super.restoreTableBackup(rdbp);
            DMPostgresRestoreHandler.logger.log(Level.INFO, "Restore completed. Restore Result : " + restoreResult.toString());
        }
        catch (final Exception ex) {
            DMPostgresRestoreHandler.logger.log(Level.WARNING, "Exception occurred while invoking  restoreTableBackup method ", ex);
        }
        return restoreResult;
    }
    
    private void createReadUser() throws SQLException {
        DMPostgresRestoreHandler.logger.log(Level.INFO, "Going to create Read Only User ");
        final String query = "select * from pg_catalog.pg_user where USENAME='medc'";
        final boolean isUserExist = this.executePgsqlQuery(query);
        DMPostgresRestoreHandler.logger.log(Level.INFO, "IS User medc already exist " + isUserExist);
        if (!isUserExist) {
            final String createUserQuery = "CREATE USER medc WITH PASSWORD 'medc'";
            this.executePgsqlQuery(createUserQuery);
            final String connectQuery = "GRANT CONNECT ON DATABASE " + this.getDatabaseName() + " TO " + "medc";
            this.executePgsqlQuery(connectQuery);
            final String usageQuery = "GRANT USAGE ON SCHEMA public TO medc";
            this.executePgsqlQuery(usageQuery);
            final String grantPrevilageQuery = "GRANT SELECT ON ALL TABLES IN SCHEMA public TO medc";
            this.executePgsqlQuery(grantPrevilageQuery);
            final String alterQuery = "ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO medc";
            this.executePgsqlQuery(alterQuery);
        }
    }
    
    private boolean executePgsqlQuery(final String query) {
        Boolean status = Boolean.FALSE;
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            final String connURL = this.getValueFromDBProps("url");
            final String username = this.getValueFromDBProps("username");
            String password = this.getValueFromDBProps("password");
            Label_0084: {
                if (password == null) {
                    if ("".equals(password)) {
                        break Label_0084;
                    }
                }
                try {
                    PersistenceInitializer.loadPersistenceConfigurations();
                    password = PersistenceUtil.getDBPasswordProvider().getPassword((Object)password);
                }
                catch (final Exception ex) {
                    DMPostgresRestoreHandler.logger.log(Level.WARNING, "Exception While decrypting DB password ", ex);
                }
            }
            Class.forName("org.postgresql.Driver").newInstance();
            conn = DriverManager.getConnection(connURL, username, password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                status = Boolean.TRUE;
            }
        }
        catch (final Exception ex2) {
            DMPostgresRestoreHandler.logger.log(Level.WARNING, "Caught exception while Executing query: " + query + " Exception: " + ex2);
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e) {
                DMPostgresRestoreHandler.logger.log(Level.WARNING, "Caught exception while closing connection : " + e);
            }
        }
        finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
                if (rs != null) {
                    rs.close();
                }
            }
            catch (final Exception e2) {
                DMPostgresRestoreHandler.logger.log(Level.WARNING, "Caught exception while closing connection : " + e2);
            }
        }
        return status;
    }
    
    private String getDatabaseName() {
        String databaseName = null;
        final String connectionUrl = this.getValueFromDBProps("url");
        String[] tmp = connectionUrl.split(":");
        tmp = tmp[3].split("/");
        if (tmp[1].indexOf("?") == -1) {
            databaseName = tmp[1];
        }
        else {
            databaseName = tmp[1].substring(0, tmp[1].indexOf("?"));
        }
        return databaseName;
    }
    
    private String getValueFromDBProps(final String key) {
        final String dbConfFile = System.getProperty("server.home") + File.separator + "conf" + File.separator + "database_params.conf";
        DMPostgresRestoreHandler.logger.log(Level.INFO, "Conf File location: " + dbConfFile);
        final Properties props = new Properties();
        FileInputStream fis = null;
        String result = "";
        try {
            if (new File(dbConfFile).exists()) {
                fis = new FileInputStream(dbConfFile);
                props.load(fis);
                fis.close();
                if (props.containsKey(key)) {
                    result = props.getProperty(key);
                }
            }
        }
        catch (final Exception ex) {
            DMPostgresRestoreHandler.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex);
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex) {
                DMPostgresRestoreHandler.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex);
            }
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (final Exception ex2) {
                DMPostgresRestoreHandler.logger.log(Level.WARNING, "Caught exception while loading properties from: " + dbConfFile + " Exception: " + ex2);
            }
        }
        return result;
    }
    
    static {
        DMPostgresRestoreHandler.logger = Logger.getLogger(ScheduledCertificateBackUpHandler.class.getName());
    }
}
