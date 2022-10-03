package com.adventnet.nms.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.util.Hashtable;

public class DBParamsParser
{
    private boolean initialized;
    private String databaseName;
    private String url;
    private String driverName;
    private String userName;
    private String password;
    private Hashtable urlTable;
    private Hashtable driverNameTable;
    private Hashtable passwordTable;
    private Hashtable userNameTable;
    private int transactionalConnections;
    private int nonTransactionalConnections;
    private static DBParamsParser parser;
    
    public DBParamsParser() {
        this.initialized = false;
        this.databaseName = null;
        this.url = null;
        this.driverName = null;
        this.userName = null;
        this.password = null;
        this.urlTable = new Hashtable(5);
        this.driverNameTable = new Hashtable(5);
        this.passwordTable = new Hashtable(5);
        this.userNameTable = new Hashtable(5);
        this.transactionalConnections = 0;
        this.nonTransactionalConnections = 0;
    }
    
    public static DBParamsParser getInstance(final File file) throws IOException {
        if (DBParamsParser.parser == null) {
            (DBParamsParser.parser = new DBParamsParser()).getDatabaseParams(file);
        }
        return DBParamsParser.parser;
    }
    
    private synchronized void getDatabaseParams(final File file) throws IOException {
        String line;
        while ((line = new BufferedReader(new InputStreamReader(openFile(file))).readLine()) != null) {
            if (line.trim().equals("")) {
                continue;
            }
            if (line.startsWith("#")) {
                continue;
            }
            this.getData(line);
        }
    }
    
    private void getData(final String s) {
        final StringTokenizer stringTokenizer = new StringTokenizer(s);
        while (stringTokenizer.hasMoreTokens()) {
            String s2 = stringTokenizer.nextToken();
            if (s2.equals("databasename")) {
                this.databaseName = stringTokenizer.nextToken().toUpperCase();
            }
            if (s2.equals("url")) {
                this.url = stringTokenizer.nextToken();
                s2 = stringTokenizer.nextToken();
                if (s2.equals("AppModules")) {
                    this.urlTable.put(stringTokenizer.nextToken(), this.url);
                }
            }
            if (s2.equals("drivername")) {
                this.driverName = stringTokenizer.nextToken();
                s2 = stringTokenizer.nextToken();
                if (s2.equals("AppModules")) {
                    this.driverNameTable.put(stringTokenizer.nextToken(), this.driverName);
                }
            }
            if (s2.equals("username")) {
                this.userName = stringTokenizer.nextToken();
                s2 = stringTokenizer.nextToken();
                if (s2.equals("AppModules")) {
                    this.userNameTable.put(stringTokenizer.nextToken(), this.userName);
                }
            }
            if (s2.equals("password")) {
                this.password = stringTokenizer.nextToken();
                s2 = stringTokenizer.nextToken();
                if (s2.equals("AppModules")) {
                    this.passwordTable.put(stringTokenizer.nextToken(), this.password);
                }
            }
            if (s2.equals("TRANS_CONNECTIONS")) {
                s2 = stringTokenizer.nextToken();
                try {
                    this.transactionalConnections = Integer.parseInt(s2);
                }
                catch (final NumberFormatException ex) {
                    System.err.println("Exception in getting the number of transactional connections from database_params.conf file." + ex);
                }
            }
            if (s2.equals("NON_TRANS_CONNECTIONS")) {
                final String nextToken = stringTokenizer.nextToken();
                try {
                    this.nonTransactionalConnections = Integer.parseInt(nextToken);
                }
                catch (final NumberFormatException ex2) {
                    System.err.println("Exception in getting the number of non transactional connections from database_params.conf file." + ex2);
                }
            }
        }
    }
    
    public String getDatabaseName() {
        return this.databaseName;
    }
    
    void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }
    
    public String getURL() {
        return this.url;
    }
    
    public String getDriverName() {
        return this.driverName;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public Hashtable getURLTable() {
        return this.urlTable;
    }
    
    public Hashtable getDriverNameTable() {
        return this.driverNameTable;
    }
    
    public Hashtable getUserNameTable() {
        return this.userNameTable;
    }
    
    public Hashtable getPasswordTable() {
        return this.passwordTable;
    }
    
    public int getNumberOfTransactionalConnections() {
        return this.transactionalConnections;
    }
    
    public int getNumberOfNonTransactionalConnections() {
        return this.nonTransactionalConnections;
    }
    
    public void setHostName(final String s) {
        if (s == null) {
            System.err.println("Passed host name is null in DBParamsParser");
            return;
        }
        this.url = this.changeLocalHostToGivenHostName(this.url, s);
        final Hashtable urlTable = new Hashtable();
        final Enumeration keys = this.urlTable.keys();
        while (keys.hasMoreElements()) {
            final String s2 = (String)keys.nextElement();
            urlTable.put(s2, this.changeLocalHostToGivenHostName((String)this.urlTable.get(s2), s));
        }
        this.urlTable = urlTable;
    }
    
    private String changeLocalHostToGivenHostName(String string, final String s) {
        final int index = string.indexOf("localhost");
        if (index >= 0) {
            string = string.substring(0, index) + s + string.substring(index + 9);
        }
        return string;
    }
    
    private static InputStream openFile(final File file) throws IOException {
        InputStream resourceAsStream;
        if (System.getProperty("JavaWebStart") != null) {
            System.out.println("Java Web Start mode in DBParamsParser: " + file);
            resourceAsStream = DBParamsParser.class.getClassLoader().getResourceAsStream(file.getName());
        }
        else {
            resourceAsStream = new FileInputStream(file);
        }
        return resourceAsStream;
    }
    
    static {
        DBParamsParser.parser = null;
    }
}
