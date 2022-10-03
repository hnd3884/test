package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.util.Properties;

public class DriverManagerConnectionFactory implements ConnectionFactory
{
    private final String connectionUri;
    private final String userName;
    private final char[] userPassword;
    private final Properties properties;
    
    public DriverManagerConnectionFactory(final String connectionUri) {
        this.connectionUri = connectionUri;
        this.properties = new Properties();
        this.userName = null;
        this.userPassword = null;
    }
    
    public DriverManagerConnectionFactory(final String connectionUri, final Properties properties) {
        this.connectionUri = connectionUri;
        this.properties = properties;
        this.userName = null;
        this.userPassword = null;
    }
    
    public DriverManagerConnectionFactory(final String connectionUri, final String userName, final char[] userPassword) {
        this.connectionUri = connectionUri;
        this.userName = userName;
        this.userPassword = Utils.clone(userPassword);
        this.properties = null;
    }
    
    public DriverManagerConnectionFactory(final String connectionUri, final String userName, final String userPassword) {
        this.connectionUri = connectionUri;
        this.userName = userName;
        this.userPassword = Utils.toCharArray(userPassword);
        this.properties = null;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        if (null != this.properties) {
            return DriverManager.getConnection(this.connectionUri, this.properties);
        }
        if (this.userName == null && this.userPassword == null) {
            return DriverManager.getConnection(this.connectionUri);
        }
        return DriverManager.getConnection(this.connectionUri, this.userName, Utils.toString(this.userPassword));
    }
    
    public String getConnectionUri() {
        return this.connectionUri;
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    static {
        DriverManager.getDrivers();
    }
}
