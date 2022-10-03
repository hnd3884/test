package com.zoho.mickey.tools.postgres;

import java.sql.DatabaseMetaData;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Map;
import java.sql.DriverManager;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceUtil;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.File;
import com.adventnet.mfw.Starter;
import java.util.Properties;
import java.util.logging.Logger;
import com.zoho.mickey.tools.CreateDBUser;
import com.adventnet.persistence.StandAlone;

public class CreatePostgresDBUser implements StandAlone, CreateDBUser
{
    private static final Logger LOGGER;
    
    public void runStandAlone(final String... args) {
        try {
            if (args == null || args.length == 0) {
                throw new IllegalArgumentException("Arguments not supplied");
            }
            if (args.length % 2 != 0) {
                throw new IllegalArgumentException("Arguments not supplied properly");
            }
            final Properties p = new Properties();
            for (int i = 0; i < args.length; i += 2) {
                p.setProperty(args[i], args[i + 1]);
            }
            final String userName = p.getProperty("-U");
            final String password = p.getProperty("-p");
            final String superUser = p.getProperty("-sU");
            final String superUserPassword = p.getProperty("-sp");
            final String role = p.getProperty("-r");
            this.validateUserInput(p);
            Starter.loadSystemProperties();
            PersistenceInitializer.initializeRelationalAPI(System.getProperty("server.home") + File.separator + "conf");
            if (!RelationalAPI.getInstance().getDBAdapter().getDBType().equalsIgnoreCase("postgres")) {
                throw new UnsupportedOperationException("CreatePostgresDBUser is applicable only for PostgreSQL databases");
            }
            final Properties properties = RelationalAPI.getInstance().getDBAdapter().getDBProps();
            final String url = properties.getProperty("url");
            try {
                this.startDB(url, userName);
                this.createUser(RelationalAPI.getInstance().getDBAdapter(), url, superUser, superUserPassword, userName, password, role);
                if (role.equalsIgnoreCase("default") && !RelationalAPI.getInstance().getDBAdapter().getDBProps().containsKey("superuser_pass") && PersistenceUtil.addKeyInDBConf("superuser_pass", PersistenceUtil.getDBPasswordProvider("postgres").getEncryptedPassword(superUserPassword))) {
                    CreatePostgresDBUser.LOGGER.log(Level.INFO, "Audit requisites has been logged successfully");
                }
            }
            finally {
                this.stopDB(url, userName);
            }
            System.exit(0);
        }
        catch (final Throwable e) {
            ConsoleOut.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private void validateUserInput(final Properties p) {
        if (p.getProperty("-sU") == null || p.getProperty("-sU").isEmpty()) {
            throw new IllegalArgumentException("Super User name not supplied");
        }
        if (p.getProperty("-sp") == null || p.getProperty("-sp").isEmpty()) {
            throw new IllegalArgumentException("Super User password not supplied");
        }
        if (p.getProperty("-U") == null || p.getProperty("-U").isEmpty()) {
            throw new IllegalArgumentException("User name not supplied");
        }
        if (p.getProperty("-p") == null || p.getProperty("-p").isEmpty()) {
            throw new IllegalArgumentException("Password not supplied");
        }
        if (p.getProperty("-r") == null || p.getProperty("-r").isEmpty()) {
            throw new IllegalArgumentException("Role not supplied");
        }
        if (p.getProperty("-U").equalsIgnoreCase(p.getProperty("-r"))) {
            throw new IllegalArgumentException("User name should not be same as role name");
        }
        if (p.getProperty("-U").contains("--") || p.getProperty("-U").contains(";") || p.getProperty("-U").contains("\\")) {
            throw new IllegalArgumentException("User name cannot have characters ::( -- , ; , \\)");
        }
        if (!p.getProperty("-r").equalsIgnoreCase("default") && !p.getProperty("-r").equalsIgnoreCase("readonly")) {
            throw new IllegalArgumentException("Invalid rolename specified");
        }
    }
    
    private void stopDB(final String url, final String userName) throws Exception {
        RelationalAPI.getInstance().getDBAdapter().stopDB(url, userName, (String)null);
    }
    
    private void startDB(final String url, final String userName) throws Exception {
        if (!RelationalAPI.getInstance().getDBAdapter().getDBInitializer().isServerStarted()) {
            RelationalAPI.getInstance().getDBAdapter().startDB(url, userName, (String)null);
            return;
        }
        throw new IllegalStateException("DB Server seems to be running. Please stop the DB Server and retry.");
    }
    
    public void createUser(final DBAdapter dbAdapter, String url, final String superUser, final String superUserPassword, final String userName, final String password, final String roleName) throws Exception {
        final Map urlProps = dbAdapter.splitConnectionURL(url);
        final String database = urlProps.get("DBName");
        if (roleName.equalsIgnoreCase("default")) {
            url = url.replaceAll(database, "template1");
        }
        Class.forName("org.postgresql.Driver");
        try (final Connection connection = DriverManager.getConnection(url, superUser, superUserPassword)) {
            CreatePostgresDBUser.LOGGER.log(Level.INFO, "Going to create PostgreSQL User :: {0}", userName);
            try (final Statement stmt = connection.createStatement()) {
                if (roleName.equalsIgnoreCase("default")) {
                    final DatabaseMetaData dbm = connection.getMetaData();
                    final Float version = new Float(dbm.getDatabaseMajorVersion() + "." + dbm.getDatabaseMinorVersion());
                    if (version >= new Float("10")) {
                        final String sql = "CREATE USER \"" + userName + "\" WITH CREATEDB NOCREATEROLE LOGIN REPLICATION ENCRYPTED PASSWORD '" + PersistenceUtil.getDBPasswordProvider().getPassword((Object)password) + "';";
                        stmt.execute(sql);
                        stmt.execute("GRANT pg_monitor TO \"" + userName + "\"");
                        stmt.execute("GRANT EXECUTE ON FUNCTION pg_start_backup TO \"" + userName + "\"");
                        stmt.execute("GRANT EXECUTE ON FUNCTION pg_stop_backup() TO \"" + userName + "\"");
                        stmt.execute("GRANT EXECUTE ON FUNCTION pg_switch_wal() TO \"" + userName + "\"");
                        stmt.execute("GRANT ALL ON SCHEMA public TO \"" + userName + "\"");
                    }
                    else {
                        final String sql = "CREATE USER \"" + userName + "\" WITH SUPERUSER CREATEDB NOCREATEROLE LOGIN REPLICATION ENCRYPTED PASSWORD '" + PersistenceUtil.getDBPasswordProvider().getPassword((Object)password) + "';";
                        stmt.execute(sql);
                        stmt.execute("GRANT ALL ON SCHEMA public TO \"" + userName + "\"");
                    }
                }
                else if (roleName.equalsIgnoreCase("replication")) {
                    final DatabaseMetaData dbm = connection.getMetaData();
                    final Float version = new Float(dbm.getDatabaseMajorVersion() + "." + dbm.getDatabaseMinorVersion());
                    if (version < new Float("10")) {
                        throw new UnsupportedOperationException("Replication Role not supported for versions below 10.0");
                    }
                    final String sql = "CREATE USER \"" + userName + "\" WITH NOCREATEDB NOCREATEROLE LOGIN REPLICATION ENCRYPTED PASSWORD '" + PersistenceUtil.getDBPasswordProvider().getPassword((Object)password) + "';";
                    stmt.execute(sql);
                    stmt.execute("GRANT pg_monitor TO \"" + userName + "\"");
                    stmt.execute("GRANT EXECUTE ON FUNCTION pg_start_backup TO \"" + userName + "\"");
                    stmt.execute("GRANT EXECUTE ON FUNCTION pg_stop_backup() TO \"" + userName + "\"");
                    stmt.execute("GRANT ALL ON SCHEMA public TO \"" + userName + "\"");
                }
                else {
                    if (!roleName.equalsIgnoreCase("readonly")) {
                        throw new IllegalArgumentException("Invalid rolename specified");
                    }
                    final String defaultUser = dbAdapter.getDBProps().getProperty("username");
                    if (defaultUser == null || defaultUser.isEmpty()) {
                        throw new IllegalArgumentException("Default User Name cannot be null or empty");
                    }
                    final String sql = "CREATE USER \"" + userName + "\" WITH ENCRYPTED PASSWORD '" + PersistenceUtil.getDBPasswordProvider().getPassword((Object)password) + "';";
                    stmt.execute(sql);
                    stmt.execute("REVOKE ALL ON SCHEMA public FROM \"" + userName + "\", public");
                    stmt.execute("GRANT CONNECT ON DATABASE \"" + database + "\" TO \"" + userName + "\", public");
                    stmt.execute("GRANT USAGE ON SCHEMA public TO \"" + userName + "\", public");
                    stmt.execute("GRANT SELECT ON ALL TABLES IN SCHEMA public TO \"" + userName + "\", public");
                    stmt.execute("ALTER DEFAULT PRIVILEGES FOR ROLE \"" + defaultUser + "\" IN SCHEMA public GRANT SELECT ON TABLES TO \"" + userName + "\", public");
                }
            }
            CreatePostgresDBUser.LOGGER.log(Level.INFO, "PostgreSQL User {0} created successfully", userName);
        }
        catch (final Exception e) {
            CreatePostgresDBUser.LOGGER.log(Level.INFO, "PostgreSQL User {0} creation failed", userName);
            throw e;
        }
    }
    
    public void postPopulation() {
    }
    
    public void prePopulation() {
    }
    
    public void startDB() {
    }
    
    public void loadModule(final String moduleName) {
    }
    
    public void populateServerStatus() {
    }
    
    public void stopDB() {
    }
    
    public void startServer() {
    }
    
    static {
        LOGGER = Logger.getLogger(CreatePostgresDBUser.class.getName());
    }
}
