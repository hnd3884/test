package com.adventnet.swissqlapi.util.database;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Hashtable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.sql.DriverManager;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Vector;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.Connection;
import com.adventnet.swissqlapi.config.metadata.MetaDataProperties;

public class MetaDataUtility
{
    private MetaDataProperties metaDataProperty;
    private String driverName;
    private String driverURL;
    private String userName;
    private String password;
    private String catalogname;
    private String schemaname;
    private String tablenamepattern;
    private String columnnamepattern;
    private String destinationFile;
    private Connection databaseConnection;
    private DatabaseMetaData dmd;
    
    public MetaDataUtility() throws IOException {
        this.metaDataProperty = null;
        this.driverName = null;
        this.driverURL = null;
        this.userName = null;
        this.password = null;
        this.catalogname = null;
        this.schemaname = null;
        this.tablenamepattern = null;
        this.columnnamepattern = null;
        this.destinationFile = null;
        this.databaseConnection = null;
        this.dmd = null;
        try {
            final MetaDataProperties property = new MetaDataProperties(null);
            this.metaDataProperty = property;
            this.getMetaDataUtilConfiguration();
        }
        catch (final IOException ioe) {
            System.out.println("conf/MetaDataConfiguration.conf file Not found.");
        }
        catch (final Exception e) {
            System.out.println("conf/MetaDataConfiguration.conf file Not found.");
        }
    }
    
    public MetaDataUtility(final MetaDataProperties property) throws IOException {
        this.metaDataProperty = null;
        this.driverName = null;
        this.driverURL = null;
        this.userName = null;
        this.password = null;
        this.catalogname = null;
        this.schemaname = null;
        this.tablenamepattern = null;
        this.columnnamepattern = null;
        this.destinationFile = null;
        this.databaseConnection = null;
        this.dmd = null;
        this.metaDataProperty = property;
        this.getMetaDataUtilConfiguration();
    }
    
    public MetaDataUtility(final Connection connection, final MetaDataProperties property) throws IOException {
        this.metaDataProperty = null;
        this.driverName = null;
        this.driverURL = null;
        this.userName = null;
        this.password = null;
        this.catalogname = null;
        this.schemaname = null;
        this.tablenamepattern = null;
        this.columnnamepattern = null;
        this.destinationFile = null;
        this.databaseConnection = null;
        this.dmd = null;
        this.metaDataProperty = property;
        this.databaseConnection = connection;
        this.getMetaDataUtilConfiguration();
    }
    
    public MetaDataUtility(final Connection connection, final String catalogName, final String schemaName, final String tableNamePattern, final String columnNamePattern) throws IOException {
        this.metaDataProperty = null;
        this.driverName = null;
        this.driverURL = null;
        this.userName = null;
        this.password = null;
        this.catalogname = null;
        this.schemaname = null;
        this.tablenamepattern = null;
        this.columnnamepattern = null;
        this.destinationFile = null;
        this.databaseConnection = null;
        this.dmd = null;
        this.databaseConnection = connection;
        this.catalogname = catalogName;
        this.schemaname = schemaName;
        this.tablenamepattern = tableNamePattern;
        this.columnnamepattern = columnNamePattern;
        if (this.catalogname.trim().equals("null") || this.catalogname.trim().equals("") || this.catalogname.trim().equals("*")) {
            this.catalogname = null;
        }
        else {
            this.catalogname = this.catalogname.toUpperCase();
        }
        if (this.tablenamepattern.trim().equals("null") || this.tablenamepattern.trim().equals("") || this.tablenamepattern.trim().equals("*")) {
            this.tablenamepattern = null;
        }
        else {
            this.tablenamepattern = this.tablenamepattern.toUpperCase();
        }
        if (this.columnnamepattern.trim().equals("null") || this.columnnamepattern.trim().equals("") || this.columnnamepattern.trim().equals("*")) {
            this.columnnamepattern = null;
        }
        else {
            this.columnnamepattern = this.columnnamepattern.toUpperCase();
        }
    }
    
    public void getMetaData(final Vector outputStrings) throws SQLException, Exception {
        try {
            File dest = null;
            if (this.destinationFile != null) {
                dest = new File(this.destinationFile);
            }
            else {
                dest = new File("conf/DatabaseMetaDataFile.conf");
                this.destinationFile = "conf/DatabaseMetaDataFile.conf";
                System.out.println("Default MetadataStorage file is taken as conf/DatabaseMetaDataFile.conf and proceeding...");
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(dest);
            }
            catch (final FileNotFoundException fe) {
                System.out.println(" File Not Found : " + dest + ". Default MetadataStorage file is taken as conf/DatabaseMetaDataFile.conf");
                fos = new FileOutputStream("conf/DatabaseMetaDataFile.conf");
                this.destinationFile = "conf/DatabaseMetaDataFile.conf";
            }
            final OutputStreamWriter osw = new OutputStreamWriter(fos);
            final PrintWriter pw = new PrintWriter(osw);
            if (this.databaseConnection == null && this.driverName != null && this.driverURL != null) {
                Class.forName(this.driverName);
                this.databaseConnection = DriverManager.getConnection(this.driverURL, this.userName, this.password);
            }
            if (this.databaseConnection != null) {
                this.dmd = this.databaseConnection.getMetaData();
            }
            if (this.schemaname != null) {
                final StringTokenizer st1 = new StringTokenizer(this.schemaname, ",");
                while (st1.hasMoreTokens()) {
                    String schema = st1.nextToken().trim();
                    if (schema.trim().equals("null") || schema.trim().equals("*")) {
                        schema = null;
                    }
                    else {
                        schema = schema.toUpperCase();
                    }
                    if (this.catalogname != null && this.catalogname.trim().equals("*")) {
                        this.catalogname = null;
                    }
                    if (this.tablenamepattern != null && this.tablenamepattern.trim().equals("*")) {
                        this.tablenamepattern = null;
                    }
                    if (this.columnnamepattern != null && this.columnnamepattern.trim().equals("*")) {
                        this.columnnamepattern = null;
                    }
                    final ResultSet rs = this.dmd.getColumns(this.catalogname, schema, this.tablenamepattern, this.columnnamepattern);
                    final boolean isSuccess = this.printResultSet(rs, pw);
                    if (isSuccess) {
                        if (schema == null) {
                            final String out = "\nMetadata successfully fetched from the database\n";
                            System.out.println(out);
                            if (outputStrings != null) {
                                outputStrings.addElement(out);
                            }
                        }
                        else {
                            final String out = "\nMetadata for schema '" + schema + "' successfully fetched from the database\n";
                            System.out.println(out);
                            if (outputStrings != null) {
                                outputStrings.addElement(out);
                            }
                        }
                    }
                    else if (schema == null) {
                        final String out = "\nFetching of Metadata failed\n";
                        System.out.println(out);
                        if (outputStrings != null) {
                            outputStrings.addElement(out);
                        }
                    }
                    else {
                        final String out = "\nFetching of Metadata for schema '" + schema + "' failed\n";
                        System.out.println(out);
                        if (outputStrings != null) {
                            outputStrings.addElement(out);
                        }
                    }
                    rs.close();
                }
            }
            else if (this.dmd != null) {
                final ResultSet rs2 = this.dmd.getColumns(this.catalogname, this.schemaname, this.tablenamepattern, this.columnnamepattern);
                final boolean isSucess = this.printResultSet(rs2, pw);
                if (isSucess) {
                    if (this.schemaname == null) {
                        final String out2 = "\nMetadata successfully fetched from the database\n";
                        System.out.println(out2);
                        if (outputStrings != null) {
                            outputStrings.addElement(out2);
                        }
                    }
                    else {
                        final String out2 = "\nMetadata for schema '" + this.schemaname + "' successfully fetched from the database\n";
                        System.out.println(out2);
                        if (outputStrings != null) {
                            outputStrings.addElement(out2);
                        }
                    }
                }
                else if (this.schemaname == null) {
                    final String out2 = "\nFetching of Metadata failed\n";
                    System.out.println(out2);
                    if (outputStrings != null) {
                        outputStrings.addElement(out2);
                    }
                }
                else {
                    final String out2 = "\nFetching of Metadata for schema '" + this.schemaname + "' failed\n";
                    System.out.println(out2);
                    if (outputStrings != null) {
                        outputStrings.addElement(out2);
                    }
                }
                rs2.close();
            }
            pw.close();
        }
        catch (final SQLException se) {
            throw se;
        }
        catch (final Exception e) {
            throw e;
        }
    }
    
    public String getDestinationFile() {
        return this.destinationFile;
    }
    
    private boolean printResultSet(final ResultSet rs) throws SQLException, IOException {
        File dest = null;
        if (this.destinationFile != null) {
            dest = new File(this.destinationFile);
        }
        else {
            dest = new File("conf/DatabaseMetaDataFile.conf");
            this.destinationFile = "conf/DatabaseMetaDataFile.conf";
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(dest);
        }
        catch (final FileNotFoundException fe) {
            System.out.println(" File Not Found : " + dest + ". Default MetadataStorage file is taken as conf/DatabaseMetaDataFile.conf");
            fos = new FileOutputStream("conf/DatabaseMetaDataFile.conf");
            this.destinationFile = "conf/DatabaseMetaDataFile.conf";
        }
        final OutputStreamWriter osw = new OutputStreamWriter(fos);
        final PrintWriter pw = new PrintWriter(osw);
        return this.printResultSet(rs, pw);
    }
    
    private boolean printResultSet(final ResultSet rs, final PrintWriter pw) throws SQLException, IOException {
        final ResultSetMetaData rsmd = rs.getMetaData();
        boolean isSucess = false;
        try {
            final Hashtable tablePKColList = new Hashtable(10);
            while (rs.next()) {
                final String tableName = rs.getString(3);
                final String columnName = rs.getString(4);
                pw.println("" + rsmd.getColumnLabel(3) + "=: " + tableName);
                pw.println("" + rsmd.getColumnLabel(4) + "=: " + columnName);
                String schema = null;
                if (this.schemaname.trim().equals("null") || this.schemaname.trim().equals("*")) {
                    schema = null;
                }
                else {
                    schema = this.schemaname.toUpperCase();
                }
                if (!tablePKColList.containsKey(tableName)) {
                    final ArrayList pkColList = new ArrayList();
                    final ResultSet rsPKCols = this.dmd.getPrimaryKeys(this.catalogname, schema, tableName);
                    while (rsPKCols.next()) {
                        pkColList.add(rsPKCols.getString(4));
                    }
                    rsPKCols.close();
                    tablePKColList.put(tableName, pkColList);
                }
                String typeLength = "";
                if (rs.getString(7) != null) {
                    typeLength = "(" + rs.getString(7);
                }
                if (rs.getString(9) != null && !rs.getString(9).trim().equalsIgnoreCase("0")) {
                    typeLength = typeLength + "," + rs.getString(9) + ")";
                }
                else {
                    typeLength += ")";
                }
                if (rs.getString(6).equalsIgnoreCase("DATE") || rs.getString(6).equalsIgnoreCase("LONG") || rs.getString(6).equalsIgnoreCase("RAW") || rs.getString(6).equalsIgnoreCase("LONG RAW") || rs.getString(6).equalsIgnoreCase("BFILE") || rs.getString(6).equalsIgnoreCase("BLOB") || rs.getString(6).equalsIgnoreCase("CLOB") || rs.getString(6).equalsIgnoreCase("TIMESTAMP")) {
                    pw.println("" + rsmd.getColumnLabel(6) + "=: " + rs.getString(6));
                }
                else {
                    pw.println("" + rsmd.getColumnLabel(6) + "=: " + rs.getString(6) + typeLength);
                }
                if (tablePKColList.get(tableName) != null && tablePKColList.get(tableName).contains(columnName)) {
                    pw.println("PRIMARY_KEY=: 1");
                }
                else {
                    pw.println("PRIMARY_KEY=: 0");
                }
            }
            isSucess = true;
        }
        catch (final Exception e) {
            System.out.println(" Exception in Print Resultset. Proceeding with default handling...");
        }
        return isSucess;
    }
    
    private void getMetaDataUtilConfiguration() throws IOException {
        this.destinationFile = this.metaDataProperty.getMetadataStorageFile();
        this.driverName = this.metaDataProperty.getDriverName();
        this.driverURL = this.metaDataProperty.getConnectionURL();
        this.userName = this.metaDataProperty.getUserName();
        this.password = this.metaDataProperty.getPasswd();
        this.catalogname = this.metaDataProperty.getCatalogName();
        this.schemaname = this.metaDataProperty.getSchemaName();
        this.tablenamepattern = this.metaDataProperty.getTableNamePattern();
        this.columnnamepattern = this.metaDataProperty.getColumnNamePattern();
        if (this.tablenamepattern != null) {
            this.tablenamepattern = this.tablenamepattern.toUpperCase();
        }
        if (this.columnnamepattern != null) {
            this.columnnamepattern = this.columnnamepattern.toUpperCase();
        }
    }
}
