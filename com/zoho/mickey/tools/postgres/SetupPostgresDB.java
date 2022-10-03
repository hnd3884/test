package com.zoho.mickey.tools.postgres;

import java.sql.Connection;
import com.adventnet.db.adapter.postgres.PostgresDBAdapter;
import java.sql.DriverManager;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.mfw.ConsoleOut;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.persistence.PersistenceInitializer;
import java.io.File;
import com.adventnet.mfw.Starter;
import java.util.Properties;
import com.adventnet.persistence.StandAlone;

public class SetupPostgresDB implements StandAlone
{
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
            final String userName = "temp";
            final String superUser = p.getProperty("-sU");
            final String superUserPassword = p.getProperty("-sp");
            this.validateUserInput(p);
            Starter.loadSystemProperties();
            PersistenceInitializer.initializeRelationalAPI(System.getProperty("server.home") + File.separator + "conf");
            if (!RelationalAPI.getInstance().getDBAdapter().getDBType().equalsIgnoreCase("postgres")) {
                throw new UnsupportedOperationException("SetupPostgresDB is applicable only for PostgreSQL databases");
            }
            final Properties properties = RelationalAPI.getInstance().getDBAdapter().getDBProps();
            final String url = properties.getProperty("url");
            try {
                this.startDB(url, userName);
                final Properties dbProperties = RelationalAPI.getInstance().getDBAdapter().getDBProps();
                dbProperties.setProperty("username", superUser);
                dbProperties.setProperty("password", superUserPassword);
                RelationalAPI.getInstance().getDBAdapter().initialize(dbProperties);
                this.createExtensions(RelationalAPI.getInstance().getDBAdapter(), url, superUser, superUserPassword);
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
        if (p.getProperty("-sp") == null || p.getProperty("-sU").isEmpty()) {
            throw new IllegalArgumentException("Super User password not supplied");
        }
    }
    
    private void stopDB(final String url, final String userName) throws Exception {
        RelationalAPI.getInstance().getDBAdapter().stopDB(url, userName, (String)null);
    }
    
    private void startDB(final String url, final String userName) throws Exception {
        if (RelationalAPI.getInstance().getDBAdapter().getDBInitializer().isServerStarted()) {
            throw new IllegalStateException("Database server is already running. Try after stopping the database");
        }
        RelationalAPI.getInstance().getDBAdapter().startDB(url, userName, (String)null);
    }
    
    public void createExtensions(final DBAdapter dbAdapter, String url, final String superUser, final String superUserPassword) throws Exception {
        url = url.replaceAll(dbAdapter.getDBProps().getProperty("DBName"), "template1");
        Class.forName("org.postgresql.Driver");
        try (final Connection connection = DriverManager.getConnection(url, superUser, superUserPassword)) {
            ((PostgresDBAdapter)dbAdapter).checkAndCreatePostgresExtensions(connection);
        }
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
    
    public void postPopulation() {
    }
    
    public void prePopulation() {
    }
}
