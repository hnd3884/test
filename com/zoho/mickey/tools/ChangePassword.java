package com.zoho.mickey.tools;

import java.util.Hashtable;
import com.adventnet.mfw.ConsoleOut;
import com.zoho.net.handshake.HandShakeUtil;
import com.adventnet.mfw.Starter;
import com.adventnet.db.adapter.mysql.MySqlDBInitializer;
import com.adventnet.db.adapter.postgres.DefaultPostgresDBInitializer;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Properties;
import com.adventnet.persistence.PersistenceException;
import java.sql.DriverManager;
import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.PersistenceUtil;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import com.adventnet.db.persistence.metadata.ColumnDefinition;
import com.adventnet.ds.query.Column;
import java.sql.Connection;
import com.adventnet.db.adapter.DBAdapter;
import java.util.logging.Logger;
import com.adventnet.persistence.StandAlone;

public class ChangePassword implements StandAlone
{
    private static final Logger LOGGER;
    
    public void startDB() throws Exception {
    }
    
    public void loadModule(final String moduleName) throws Exception {
    }
    
    public void populateServerStatus() throws Exception {
    }
    
    public void stopDB() throws Exception {
    }
    
    public void startServer() throws Exception {
    }
    
    private static void updateCredentialAuditTable(final DBAdapter dbAdapter, final Connection conn, final String oldPassword, final String newPassword) throws SQLException {
        final Column col = new Column("DBCredentialsAudit", "PASSWORD");
        final ColumnDefinition colDef = new ColumnDefinition();
        colDef.setColumnName("PASSWORD");
        colDef.setDataType("SCHAR");
        col.setDefinition(colDef);
        if (dbAdapter.isTablePresentInDB(conn, (String)null, "DBCredentialsAudit")) {
            try (final Statement stmt = conn.createStatement()) {
                final String encryptColSQl = dbAdapter.getSQLGenerator().getDBSpecificEncryptionString(col, "'" + newPassword + "'");
                final String updatesql = "UPDATE " + dbAdapter.getSQLGenerator().getDBSpecificTableName("DBCredentialsAudit") + " SET " + dbAdapter.getSQLGenerator().getDBSpecificColumnName("PASSWORD") + " = " + encryptColSQl + " , LAST_MODIFIED_TIME = '" + new Timestamp(System.currentTimeMillis()) + "' WHERE " + dbAdapter.getSQLGenerator().getDBSpecificColumnName("USERNAME") + " = 'postgres'";
                stmt.executeUpdate(updatesql);
                ChangePassword.LOGGER.info("Updated the credential entry.");
            }
        }
    }
    
    public static boolean changePassword(final DBAdapter dbAdapter, final String userName, final String oldPassword, final String newPassword) throws Exception {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null / empty. Please provide a valid password!!");
        }
        if (oldPassword.equals(newPassword)) {
            ChangePassword.LOGGER.severe("Old and new password are same. Hence, it is ignored.");
            return false;
        }
        ChangePassword.LOGGER.info("Going to change database password.");
        final Properties defaultDbProps = dbAdapter.getDBProps();
        boolean isUpdated = false;
        final String dbType = dbAdapter.getDBType();
        if (defaultDbProps.getProperty("username").equals(userName)) {
            final String encryptedPassword = PersistenceUtil.getDBPasswordProvider(dbType).getEncryptedPassword(newPassword);
            isUpdated = PersistenceUtil.updatePasswordInDBConf(encryptedPassword);
        }
        if (!defaultDbProps.getProperty("username").equals(userName) || (isUpdated && defaultDbProps.getProperty("username").equals(userName))) {
            Connection conn = null;
            try {
                if (dbType.equals("postgres")) {
                    Class.forName("org.postgresql.Driver");
                }
                else if (dbType.equals("mysql")) {
                    Class.forName("org.gjt.mm.mysql.Driver");
                }
                String url = dbAdapter.getDBProps().getProperty("url");
                final Map urlProps = dbAdapter.splitConnectionURL(url);
                final String database = urlProps.get("DBName");
                if (PersistenceInitializer.getConfigurationValue("DBName").equals("postgres")) {
                    url = url.replaceAll(database, "template1");
                }
                else if (PersistenceInitializer.getConfigurationValue("DBName").equals("mysql")) {
                    url = url.replaceAll(database, "");
                }
                conn = DriverManager.getConnection(url, userName, oldPassword);
                dbAdapter.changePassword(userName, oldPassword, newPassword, conn);
                if (dbType.equals("postgres") && userName.equals("postgres")) {
                    boolean isDBExists = false;
                    try (final Statement stmt = conn.createStatement();
                         final ResultSet rs = stmt.executeQuery("SELECT datname FROM pg_catalog.pg_database WHERE lower(datname) = lower('" + database + "')")) {
                        while (rs.next()) {
                            isDBExists = true;
                        }
                    }
                    ChangePassword.LOGGER.info("isDBExists: " + isDBExists);
                    if (isDBExists) {
                        try (final Connection conn2 = DriverManager.getConnection(dbAdapter.getDBProps().getProperty("url"), userName, newPassword)) {
                            updateCredentialAuditTable(dbAdapter, conn2, oldPassword, newPassword);
                        }
                    }
                    else {
                        PersistenceUtil.addKeyInDBConf("superuser_pass", PersistenceUtil.getDBPasswordProvider(dbType).getEncryptedPassword(newPassword));
                        ChangePassword.LOGGER.info("Updated key in database_params.conf");
                    }
                }
                return true;
            }
            catch (final Exception e) {
                if (defaultDbProps.getProperty("username").equals(userName)) {
                    PersistenceUtil.updatePasswordInDBConf(oldPassword);
                }
                throw new PersistenceException("Exception while updating the new password in database. " + e.getMessage(), (Throwable)e);
            }
            finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                }
                catch (final Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
        return false;
    }
    
    private void validateInput(final Properties p) {
        if (p.getProperty("-P") == null || p.getProperty("-P").isEmpty()) {
            throw new IllegalArgumentException("New Password not supplied");
        }
    }
    
    private void startDBServer(final DBAdapter dbAdapter, final Properties dbProps) throws Exception {
        final Properties dbAdapterProps = dbAdapter.getDBProps();
        boolean isDBServerRunning = false;
        String userName = null;
        if (dbAdapter.getDBType().equals("postgres")) {
            isDBServerRunning = ((DefaultPostgresDBInitializer)dbAdapter.getDBInitializer()).checkServerStatus(dbAdapterProps.getProperty("Server"), (int)((Hashtable<K, Integer>)dbAdapterProps).get("Port"), dbAdapterProps.getProperty("username"));
            userName = "temp";
        }
        else if (dbAdapter.getDBType().equals("mysql")) {
            isDBServerRunning = ((MySqlDBInitializer)dbAdapter.getDBInitializer()).getMySQLAdminPingStatus(dbProps.getProperty("username"), (int)((Hashtable<K, Integer>)dbAdapterProps).get("Port"), dbProps.getProperty("password"));
            userName = dbProps.getProperty("username");
        }
        if (isDBServerRunning) {
            ChangePassword.LOGGER.severe("Database is running. Please stop the database server and try to change the password again!!");
            throw new Exception("Database is running. Please stop the database server and try to change the password again!!");
        }
        ChangePassword.LOGGER.info("DB server is not running. Hence going to start.");
        dbAdapter.startDB(dbProps.getProperty("url"), userName, dbProps.getProperty("password"));
    }
    
    private void stopDBServer(final DBAdapter dbAdapter, final Properties dbProps) throws Exception {
        String userName = dbProps.getProperty("username");
        if (dbAdapter.getDBType().equals("postgres")) {
            userName = "temp";
        }
        dbAdapter.stopDB(dbProps.getProperty("url"), userName, dbProps.getProperty("password"));
    }
    
    public void runStandAlone(final String... args) throws Exception {
        Properties dbProps = null;
        try {
            if (args == null || args.length == 0) {
                throw new IllegalArgumentException("Arguments has not been given to change the database password.");
            }
            if (args.length % 2 != 0) {
                throw new IllegalArgumentException("Arguments are not supplied properly");
            }
            final Properties props = new Properties();
            for (int i = 0; i < args.length; i += 2) {
                props.setProperty(args[i], args[i + 1]);
            }
            String oldPassword = props.getProperty("-p");
            String newPassword = props.getProperty("-P");
            String userName = props.getProperty("-U");
            this.validateInput(props);
            Starter.loadSystemProperties();
            PersistenceInitializer.loadPersistenceConfigurations();
            oldPassword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)oldPassword);
            newPassword = PersistenceUtil.getDBPasswordProvider().getPassword((Object)newPassword);
            oldPassword = ((oldPassword == null) ? "" : oldPassword);
            dbProps = PersistenceInitializer.getDBProps(PersistenceInitializer.getDBParamsFilePath());
            String oldPasswordFromProps = dbProps.getProperty("password");
            oldPasswordFromProps = ((oldPasswordFromProps == null) ? "" : oldPasswordFromProps);
            dbProps.setProperty("password", oldPasswordFromProps);
            userName = ((userName == null) ? dbProps.getProperty("username") : userName);
            if (userName.equals(dbProps.getProperty("username")) && !oldPassword.equals(oldPasswordFromProps)) {
                throw new IllegalArgumentException("Provided old password does not match with current database password.");
            }
            DBAdapter dbAdapter = null;
            try {
                if (HandShakeUtil.isServerListening()) {
                    ChangePassword.LOGGER.severe("Database password cannot be changed when server is running. Please shutdown the server and retry!!");
                    throw new Exception("Database password cannot be changed when server is running. Please shutdown the server and retry!!");
                }
                dbProps.putAll(PersistenceInitializer.getConfigurationProps(PersistenceInitializer.getConfigurationValue("DBName")));
                dbAdapter = PersistenceInitializer.createDBAdapter(dbProps);
                dbAdapter.initialize(dbProps);
                this.startDBServer(dbAdapter, dbProps);
                ConsoleOut.println("Going to change the database password.");
                if (changePassword(dbAdapter, userName, oldPassword, newPassword)) {
                    dbProps.setProperty("password", newPassword);
                    ConsoleOut.println("Password changed successfully.");
                }
                else {
                    ConsoleOut.println("Password has not been changed.");
                }
            }
            catch (final Exception e) {
                e.printStackTrace();
                throw e;
            }
            finally {
                ChangePassword.LOGGER.info("Going to stop database server!!");
                this.stopDBServer(dbAdapter, dbProps);
            }
            System.exit(0);
        }
        catch (final Exception e2) {
            ConsoleOut.println("Exception occurred while changing the password: " + e2.getMessage());
            e2.printStackTrace();
            System.exit(1);
        }
    }
    
    public void postPopulation() throws Exception {
    }
    
    public void prePopulation() throws Exception {
    }
    
    static {
        LOGGER = Logger.getLogger(ChangePassword.class.getName());
    }
}
