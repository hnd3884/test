package com.me.devicemanagement.onpremise.tools.dbmigration.action;

import com.adventnet.db.migration.util.DBMigrationUtil;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import java.util.logging.Level;
import com.me.devicemanagement.onpremise.tools.dbmigration.utils.DBMigrationUtils;
import com.me.devicemanagement.onpremise.tools.dbmigration.handler.DBMigrationHandler;
import java.util.logging.Logger;
import com.adventnet.db.migration.DBMigrationInterface;

public class DBMigrationAction implements DBMigrationInterface
{
    private Logger logger;
    
    public DBMigrationAction() {
        this.logger = Logger.getLogger("DBMigrationAction");
    }
    
    void updateConfigFiles(final String dsName, final String dbPropsPath) throws Exception {
        final DBMigrationHandler handler = new DBMigrationHandler();
        final Properties dbProps = DBMigrationUtils.getProperties(dbPropsPath);
        if (dbProps != null) {
            final String stype = dsName;
            final String connectionUrl = dbProps.getProperty("url");
            final String uname = dbProps.getProperty("username");
            final String pwd = dbProps.getProperty("password");
            String db = new String();
            String sname = new String();
            String sport = new String();
            String domain = new String();
            Boolean isWinAuthType = false;
            Boolean NTLMSetting = false;
            if ("mysql".equals(stype) || "postgres".equals(stype)) {
                String[] tmp = connectionUrl.split(":");
                sname = tmp[2].substring(2);
                tmp = tmp[3].split("/");
                sport = tmp[0];
                if (tmp[1].indexOf("?") == -1) {
                    db = tmp[1];
                }
                else {
                    db = tmp[1].substring(0, tmp[1].indexOf("?"));
                }
            }
            else if ("mssql".equals(stype)) {
                String[] tmp = connectionUrl.split(":");
                sname = tmp[2].substring(2);
                tmp = tmp[3].split("/");
                sport = tmp[0];
                if (tmp[1].indexOf(";") == -1) {
                    db = tmp[1];
                }
                else {
                    db = tmp[1].substring(0, tmp[1].indexOf(";"));
                }
                tmp = tmp[1].split(";");
                if (tmp.length > 1 && tmp[1].contains("Domain")) {
                    domain = tmp[1].substring(tmp[1].indexOf("=") + 1);
                    isWinAuthType = true;
                }
                if (tmp.length > 2 && tmp[2].contains("authenticationScheme=NTLM")) {
                    NTLMSetting = true;
                }
            }
            String driver = new String();
            String exceptionSorter = new String();
            if ("postgres".equals(stype)) {
                driver = "org.postgresql.Driver";
                exceptionSorter = "com.adventnet.db.adapter.postgres.PostgresExceptionSorter";
            }
            else if ("mysql".equals(stype)) {
                driver = "org.gjt.mm.mysql.Driver";
                exceptionSorter = "com.adventnet.db.adapter.mysql.MysqlExceptionSorter";
            }
            else if ("mssql".equals(stype)) {
                driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
                exceptionSorter = "com.me.devicemanagement.onpremise.server.sql.MssqlExceptionSorter";
            }
            this.logger.log(Level.INFO, "Updating DB Configuration Files");
            handler.updateDBConfigFile(stype, sname, sport, isWinAuthType, NTLMSetting, domain, uname, pwd, null, "", db, driver, exceptionSorter);
            this.logger.log(Level.INFO, "Updating DB Configuration Files Completed");
        }
    }
    
    void migration(final String dsName, final String dbPropsPath, final String serverHome) throws Exception, Throwable {
        final DBMigrationHandler handler = new DBMigrationHandler();
        this.logger.log(Level.INFO, "Setting Source DB Server");
        DBMigrationUtils.setDBHome();
        String dbSpecificFilePath = null;
        if (dsName == null || dbPropsPath == null) {
            this.logger.log(Level.WARNING, "Unknown options specified");
        }
        else {
            final File dbPropsFile = new File(dbPropsPath);
            if (!dbPropsFile.exists()) {
                this.logger.log(Level.WARNING, "DB property file [" + System.getProperty("server.home") + dbPropsPath + "] not found...");
            }
            else {
                dbSpecificFilePath = dbPropsFile.getAbsolutePath();
            }
        }
        this.logger.log(Level.INFO, "Reading DB Properties from file path ::: " + dbSpecificFilePath);
        final Properties props = new Properties();
        props.load(new FileInputStream(new File(dbSpecificFilePath)));
        handler.isAuthenticatedUsersAvailable(System.getProperty("server.home"), dsName);
        handler.updateMysqlINIFiles();
        handler.removeJunkPIDFiles();
        this.logger.log(Level.INFO, "Calling DBM Tool");
        if (dsName.equalsIgnoreCase("mssql")) {
            String url = props.getProperty("url");
            url = url.substring(0, url.lastIndexOf("/")) + ";databaseName=" + url.substring(url.lastIndexOf("/") + 1);
            props.setProperty("url", url);
        }
        DBMigrationUtil.migrateTables(dsName, props);
        this.logger.log(Level.INFO, "Data migration completed");
        this.updateConfigFiles(dsName, dbPropsPath);
    }
    
    public void migrateDB(final String dsName, final String dbPropsPath) throws Exception {
        final String serverHome = new File(System.getProperty("server.home")).getCanonicalPath();
        try {
            this.migration(dsName, dbPropsPath, serverHome);
        }
        catch (final Throwable e) {
            this.logger.log(Level.WARNING, "\nDatabase migration failed !!!!!!!!");
            throw new Exception(e);
        }
    }
}
