package com.adventnet.sym.server.patch.util;

import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;

public class MySQLDSParser
{
    private static String userName;
    private static String passWord;
    private static String dbName;
    private static String mysqlHome;
    private static String mysqlHost;
    private static String mysqlDSFile;
    private static String serverHome;
    private static int mysqlPort;
    
    public static void parseFile() throws Exception {
        MySQLDSParser.serverHome = System.getProperty("server.home");
        String url = "Not available";
        MySQLDSParser.mysqlDSFile = MySQLDSParser.serverHome + File.separator + "conf" + File.separator + "database_params.conf";
        final Properties dbProp = new Properties();
        try {
            dbProp.load(new FileInputStream(MySQLDSParser.mysqlDSFile));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        MySQLDSParser.userName = dbProp.getProperty("username");
        MySQLDSParser.passWord = dbProp.getProperty("password");
        if (MySQLDSParser.passWord == null) {
            MySQLDSParser.passWord = "";
        }
        url = dbProp.getProperty("url");
        final String driverName = dbProp.getProperty("drivername");
        MySQLDSParser.dbName = "desktopcentral";
        MySQLDSParser.mysqlHost = url.substring(url.indexOf("://") + 3, url.lastIndexOf(":"));
        MySQLDSParser.mysqlPort = Integer.parseInt(url.substring(url.lastIndexOf(":") + 1, url.lastIndexOf("/")));
        MySQLDSParser.mysqlHome = System.getProperty("db.home");
    }
    
    public static String getMySQLHome() {
        return MySQLDSParser.mysqlHome;
    }
    
    public static String getMySQLUser() {
        return MySQLDSParser.userName;
    }
    
    public static String getMySQLPassword() {
        return MySQLDSParser.passWord;
    }
    
    public static String getMySQLDBName() {
        return MySQLDSParser.dbName;
    }
    
    public static String getMySQLHost() {
        return MySQLDSParser.mysqlHost;
    }
    
    public static int getMySQLPort() {
        return MySQLDSParser.mysqlPort;
    }
    
    public static String getServerHome() {
        return MySQLDSParser.serverHome;
    }
    
    public static void main(final String[] args) throws Exception {
        parseFile();
    }
    
    static {
        MySQLDSParser.userName = "";
        MySQLDSParser.passWord = "";
        MySQLDSParser.dbName = "";
        MySQLDSParser.mysqlHome = "";
        MySQLDSParser.mysqlHost = "";
        MySQLDSParser.mysqlDSFile = "";
        MySQLDSParser.serverHome = "";
        MySQLDSParser.mysqlPort = 0;
    }
}
