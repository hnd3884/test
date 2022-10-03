package com.adventnet.swissqlapi.config.metadata;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.InputStream;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

public class MetaDataProperties
{
    private String connectionURL;
    private String driverName;
    private String userName;
    private String passwd;
    private String catalogName;
    private String schemaName;
    private String tableNamePattern;
    private String columnNamePattern;
    private String destinationFile;
    
    public MetaDataProperties() {
    }
    
    public MetaDataProperties(final String fileName) throws IOException {
        if (fileName == null) {
            this.loadMetaDataConfiguration("conf/MetaDataConfiguration.conf");
        }
        else {
            this.loadMetaDataConfiguration(fileName);
        }
    }
    
    public void setConnectionURL(final String connectionURL) {
        this.connectionURL = connectionURL;
    }
    
    public void setDriverName(final String driverName) {
        this.driverName = driverName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public void setPasswd(final String passwd) {
        this.passwd = passwd;
    }
    
    public void setCatalogName(final String catalogName) {
        this.catalogName = catalogName;
    }
    
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }
    
    public void setTableNamePattern(final String tableNamePattern) {
        this.tableNamePattern = tableNamePattern;
    }
    
    public void setColumnNamePattern(final String columnNamePattern) {
        this.columnNamePattern = columnNamePattern;
    }
    
    public void setMetadataStorageFile(final String fileName) {
        this.destinationFile = fileName;
    }
    
    public String getConnectionURL() {
        return this.connectionURL;
    }
    
    public String getDriverName() {
        return this.driverName;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public String getPasswd() {
        return this.passwd;
    }
    
    public String getCatalogName() {
        return this.catalogName;
    }
    
    public String getSchemaName() {
        return this.schemaName;
    }
    
    public String getTableNamePattern() {
        return this.tableNamePattern;
    }
    
    public String getColumnNamePattern() {
        return this.columnNamePattern;
    }
    
    public String getMetadataStorageFile() {
        return this.destinationFile;
    }
    
    public void loadMetaDataConfiguration(final String fileName) throws IOException {
        File file = null;
        if (fileName == null) {
            file = new File("conf/MetaDataConfiguration.conf");
        }
        else {
            file = new File(fileName);
        }
        if (file.exists()) {
            final FileInputStream str = new FileInputStream(file);
            final Properties props = new Properties();
            props.load(str);
            final Enumeration enum1 = props.keys();
            while (enum1.hasMoreElements()) {
                final String key = enum1.nextElement();
                final String val = ((Hashtable<K, String>)props).get(key);
                if (key.equalsIgnoreCase("connectionurl")) {
                    this.setConnectionURL(val);
                }
                else if (key.equalsIgnoreCase("drivername")) {
                    this.setDriverName(val);
                }
                else if (key.equalsIgnoreCase("user")) {
                    this.setUserName(val);
                }
                else if (key.equalsIgnoreCase("password")) {
                    this.setPasswd(val);
                }
                else if (key.equalsIgnoreCase("catalogname")) {
                    this.setCatalogName(val);
                }
                else if (key.equalsIgnoreCase("schemaname")) {
                    this.setSchemaName(val);
                }
                else if (key.equalsIgnoreCase("tablenamepattern")) {
                    this.setTableNamePattern(val);
                }
                else if (key.equalsIgnoreCase("columnnamepattern")) {
                    this.setColumnNamePattern(val);
                }
                else {
                    if (!key.equalsIgnoreCase("metadatastoragefile")) {
                        continue;
                    }
                    this.setMetadataStorageFile(val);
                }
            }
        }
        else {
            System.out.println(fileName + " file is not found ...");
        }
    }
}
